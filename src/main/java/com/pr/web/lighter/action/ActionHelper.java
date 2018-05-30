package com.pr.web.lighter.action;


import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.pr.web.lighter.WebLighterConfig;
import com.pr.web.lighter.annotation.*;
import com.pr.web.lighter.utils.ClassHelper;
import com.pr.web.lighter.utils.GsonUTCDateAdapter;
import com.pr.web.lighter.utils.file.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.*;
import java.sql.Timestamp;
import java.util.*;

/**
 * Action 处理工具类, 包含如下功能:
 * <pre>
 * - 根据 Request 的 URI 找到处理该 Request 的函数
 * - 实例化处理 Request 的 Action 对象, 并调用相应的处理函数. 调用处理函数前将自动解析上行参数 ( JSON / Text), 并将在调用处理函数时作为参数注入.
 * - 支持多文件上传 ( 可同时携带数据 )
 * - 调用 Request 处理函数时自动实例化并注入 Service 之类的对象 ( 使用 @Inject )
 * </pre>
 *
 * @author Bailey
 * @see Request
 * @see Inject
 * @see Param
 * @see Upload
 * @see Download
 */
public class ActionHelper {
    final private static List<RequestHandler> requestHandlers = new ArrayList<>();

    private static Gson gson;

    public ActionHelper() {
        // 配置并创建Gson对象
        GsonBuilder        gsonBuilder        = new GsonBuilder();
        GsonUTCDateAdapter gsonUTCDateAdapter = new GsonUTCDateAdapter();
        gsonBuilder.registerTypeAdapter(Date.class, gsonUTCDateAdapter);
        gsonBuilder.registerTypeAdapter(java.sql.Date.class, gsonUTCDateAdapter);
        gsonBuilder.registerTypeAdapter(Timestamp.class, gsonUTCDateAdapter);
        gson = gsonBuilder.create();
    }

    /**
     * 初始化requestHandlers
     * 遍历所有ActionSupport的子类, 将其信息缓存到requestHandlers, 以便后续使用
     *
     * @see RequestHandler
     */
    public static void initRequestHandlers() {
        long start = System.currentTimeMillis();
        // 获得所有ActionSupport的子类
        List<Class<ActionSupport>> actionClasses = new ClassHelper().getSubClasses(ActionSupport.class);
        // 临时存储已缓存过的RequestHandler, 用于判定是否多个方法用于处理同一个请求
        List<String> urlRegExpress = new ArrayList<>();
        for (Class<ActionSupport> actionCls : actionClasses) {
            Method[] methods = actionCls.getDeclaredMethods();
            for (Method method : methods) {
                if (!method.isAnnotationPresent(Request.class)) continue;   // 忽略无@Request的方法
                String urlPattern = method.getAnnotation(Request.class).url();
                String urlRegex   = RequestHandler.getUlrRegex(urlPattern);
                if (urlRegExpress.contains(urlRegex)) {
                    throw new RuntimeException("More than one method is declared as request handler: " + urlPattern);
                } else {
                    urlRegExpress.add(urlRegex);
                    requestHandlers.add(new RequestHandler(actionCls, method));
                }
            }
        }

        System.out.println("RequestHandler Initialized in " + (System.currentTimeMillis() - start) + "ms. " + requestHandlers.size() + " request(s) mapped.");
        if (WebLighterConfig.isPrintUrlMapReport()) System.out.println(getRequestHandlersReport());
    }

    /**
     * 获得用于处理给定uri请求的RequestHandler
     *
     * @param url 前端发来的请求的URL
     * @return 符合条件的 {@link RequestHandler}
     */
    public static RequestHandler getRequestHandler(String url) {
        for (RequestHandler handler : requestHandlers) {
            if (handler.isMatched(url)) return handler;
        }
        return null;
    }

    /**
     * 获得请求(Request)与处理方法(Action Method)之间的映射表. 用于调试
     *
     * @return 映射表
     */
    public static String getRequestHandlersReport() {
        Map<String, String> map                 = new LinkedHashMap<>();
        int                 maxUrlLength        = 0;
        int                 maxMethodNameLength = 0;
        int                 len;
        for (RequestHandler handler : requestHandlers) {
            String url    = handler.getUrlPattern();
            String method = handler.getActionClass().getName() + "." + handler.getMethod().getName() + "()";
            map.put(url, method);
            if ((len = url.length()) > maxUrlLength) maxUrlLength = len;
            if ((len = method.length()) > maxMethodNameLength) maxMethodNameLength = len;
        }
        String       line = StringUtils.leftPad("", maxUrlLength + maxMethodNameLength + WebLighterConfig.getUrlPrefix().length() + 5, "-") + "\n";
        StringBuffer sb   = new StringBuffer();
        sb.append(line);
        for (String k : map.keySet()) {
            sb.append(WebLighterConfig.getUrlPrefix() + StringUtils.rightPad(k, maxUrlLength, " ") + " ==> " + map.get(k) + "\n");
        }
        sb.append(line);
        return sb.toString();
    }

    /**
     * 执行请求处理函数
     *
     * @param handler 处理请求的{@link RequestHandler}
     * @param url     去除请求前缀(WebLighterConfig.URL_PREFIX)后的url
     * @param req     HttpServletRequest
     * @param resp    HttpServletResponse
     * @throws ActionException ActionException
     * @throws IOException     IOException
     * @see RequestHandler
     */
    public void execute(RequestHandler handler, String url, HttpServletRequest req, HttpServletResponse resp) throws ActionException, IOException {
        if (WebLighterConfig.isPrintUrlMapReport()) {
            System.out.println(WebLighterConfig.getUrlPrefix() + url + " ==> " + handler.getActionClass().getName() + "." + handler.getMethod().getName() + "()");
        }

        Method  method            = handler.getMethod(); // 处理请求的Action方法
        Request RequestAnnotation = method.getAnnotation(Request.class);

        // 参数数据格式
        ParamFormat paramFormat = RequestAnnotation.format();

        List<UploadFileInfo>     fileInfos         = null;          // 上传的文件信息
        Map<String, JsonElement> requestParameters = new LinkedHashMap<>();         // 参数集合

        if (method.isAnnotationPresent(Upload.class)) {     // 带文件上传的请求
            // 接收并保存文件
            Upload       annotation = method.getAnnotation(Upload.class);
            UploadResult resultInfo = new UploadUtil().upload(req, annotation.uploadDir(), annotation.nameRule(), annotation.maxFileSize(), annotation.maxRequestSize());
            requestParameters.putAll(parseParams(resultInfo.getParameters(), paramFormat));
            fileInfos = resultInfo.getFiles();
        } else {    // 普通的请求
            requestParameters.putAll(parseParams(req, paramFormat));
        }

        // url占位参数
        Map<String, String[]> urlPlaceholderParams = handler.getUrlPlaceholderValues(url);

        // 检查占位参数是否与其他上行参数冲突
        if (urlPlaceholderParams != null) {
            for (String urlParamName : urlPlaceholderParams.keySet()) {
                for (String paramName : requestParameters.keySet()) {
                    if (urlParamName.equals(paramName)) {
                        throw new ActionException("The parameter name \"" + paramName + "\" is conflict with url placeholder parameter. url-pattern: " + handler.getUrlPattern());
                    }
                }
            }

            // 将url占位参数添加到参数集合中
            requestParameters.putAll(parseParams(urlPlaceholderParams, ParamFormat.text));  // url 占位参数只可能按字符串处理
        }

        // 请求处理结果
        ActionResult actionResult = invoke(handler, req, resp, requestParameters, fileInfos);

        if (actionResult == null) {
            throw new ActionException("The Action Result Unassigned!!!");
        }

        if (!method.isAnnotationPresent(Download.class) || actionResult.getCode() < 0) {
            // 非文件下载请求, 回传结果
            PrintWriter writer = resp.getWriter();
            gson.toJson(actionResult, writer);
            writer.flush();
        } else {
            // 文件下载请求
            if (!(actionResult.getResult() instanceof DownloadFileInfo)) {
                throw new ActionException("The result property of ActionResult REQUIRE an instance of com.pr.web.lighter.utils.file.DownloadFileInfo");
            }

            DownloadFileInfo fileInfo= (DownloadFileInfo) actionResult.getResult();
            new DownloadUtil().download(req, resp, fileInfo);
        }
    }

    /**
     * 按指定的参数格式声明(paramFormat)将参数值封装为JsonElement
     *
     * @param raw         原始参数
     * @param paramFormat 参数格式声明
     * @return 封装完成的JsonElement
     * @see ParamFormat
     */
    private JsonElement parse2Json(String raw, ParamFormat paramFormat) {
        try {
            switch (paramFormat) {
                case json:
                    return gson.fromJson(raw, JsonElement.class);
                case text:
                default:
                    return new JsonPrimitive(raw);
            }
        } catch (JsonSyntaxException e) {
            return new JsonPrimitive(raw);
        }
    }

    /**
     * 将数据转成JSON格式, 以便统一处理
     *
     * @param rawParams   原始数据
     * @param paramFormat 数据格式
     * @return JSON格式数据
     */
    private Map<String, JsonElement> parseParams(Map<String, String[]> rawParams, ParamFormat paramFormat) {
        Map<String, JsonElement> params = new LinkedHashMap<>();
        for (String key : rawParams.keySet()) {
            String[]    values = rawParams.get(key);
            JsonElement val    = null;
            if (!ArrayUtils.isEmpty(values)) {
                if (values.length == 1) {
                    val = parse2Json(values[0], paramFormat);
                } else {
                    JsonArray arr = new JsonArray();
                    for (String value : values) {
                        arr.add(parse2Json(value, paramFormat));
                    }
                    val = arr;
                }
            }
            params.put(key, val);
        }
        return params;
    }

    /**
     * 将数据转成JSON格式, 以便统一处理
     *
     * @param req HttpServletRequest
     * @return JSON格式数据
     */
    private Map<String, JsonElement> parseParams(HttpServletRequest req, ParamFormat paramFormat) throws IOException, ActionException {
        String contentType = req.getContentType();
        if (contentType == null || contentType.indexOf("application/x-www-form-urlencoded") >= 0) {
            return parseParams(req.getParameterMap(), paramFormat);
        } else if (contentType.indexOf("application/json") >= 0) {
            JsonObject               jsonObject = gson.fromJson(gson.newJsonReader(req.getReader()), JsonObject.class);
            Map<String, JsonElement> params     = new LinkedHashMap<>();
            for (String name : jsonObject.keySet()) {
                params.put(name, jsonObject.get(name));
            }
            return params;
        } else {
            throw new ActionException("The Content-Type MUST be 'application/json' or 'application/x-www-form-urlencoded'");
        }
    }

    /**
     * 调用请求处理函数, 同时注入相关参数
     *
     * @param handler           处理请求的 RequestHandler 对象
     * @param req               Request 对象
     * @param resp              Response 对象
     * @param requestParameters 参数集合, key 为参数名, value 为参数值
     * @param fileInfos         上传的文件信息
     * @return 封装为 ActionResult 的处理结果
     * @throws ActionException Action处理异常
     * @see JsonElement
     * @see ActionResult
     */
    private ActionResult invoke(RequestHandler handler, HttpServletRequest req, HttpServletResponse resp, Map<String, JsonElement> requestParameters, List<UploadFileInfo> fileInfos) throws ActionException {

        // 创建Action实例
        ActionSupport action;
        // 调用Action中相应的处理方法
        try {
            action = handler.getActionClass().newInstance();
            action.setRequest(req);
            action.setResponse(resp);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ActionException("Creating Action instance error: " + handler.getActionClass().getName(), e);
        }


        // 处理请求的Action方法
        Method method = handler.getMethod();

        // 调用方法所需参数
        Parameter[] parameters = method.getParameters();

        // 调用方法需要传入的参数值
        Object[] paramValues = new Object[parameters.length];

        // 依次取得调用请求处理方法所需参数
        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];

            // 参数带@Inject注解, 需要实例化并注入, 如 Service
            if (param.isAnnotationPresent(Inject.class)) {
                try {
                    paramValues[i] = param.getType().newInstance();
                } catch (Exception e) {
                    throw new ActionException("Creating parameter instance error: " + param.getType().getName(), e);
                }
                continue;
            }

            // 参数带@ParamFileInfo注解, 本参数用于接收上传文件信息
            if (param.isAnnotationPresent(ParamFileInfo.class)) {
                if (param.getType().isAssignableFrom(UploadFileInfo.class)) {  // 函数只接收单个文件信息, 取fileInfo中的第0个元素
                    paramValues[i] = (fileInfos != null && !fileInfos.isEmpty()) ? fileInfos.get(0) : null;
                } else { // List, 接收多个文件信息
                    paramValues[i] = fileInfos;
                }
                continue;
            }

            // 参数带@Param, 本参数用于接收上行参数
            if (param.isAnnotationPresent(Param.class)) {   // 上行参数
                Param       paramAnnotation = param.getAnnotation(Param.class);  // Param 注解
                String      paramName       = paramAnnotation.name();    // 上行数据中的参数名
                JsonElement paramValue      = requestParameters.get(paramName);   // 上行数据值
                Type        paramType       = param.getParameterizedType();
                Class       paramClass      = param.getType();

                if (paramValue == null) {
                    paramValues[i] = null;
                    continue;
                }

                try {
                    // 若要求的参数类型为数组或Collection, 而实际解析得到的参数不是JsonArray则将参数封装为JsonArray
                    if ((paramClass.isArray() || Collection.class.isAssignableFrom(paramClass)) && !paramValue.isJsonArray()) {
                        JsonArray arr = new JsonArray();
                        arr.add(paramValue);
                        paramValue = arr;
                    } else if (String.class.isAssignableFrom(paramClass) && !paramValue.isJsonPrimitive()) {
                        // 要求的参数类型为String, 而解析时被处理成了VO, 则转为String
                        paramValue = new JsonPrimitive(gson.toJson(paramValue));
                    }

                    if (paramType instanceof ParameterizedType) {   // 泛型参数
                        ParameterizedType parameterizedType   = (ParameterizedType) paramType;
                        Type              rowType             = parameterizedType.getRawType();
                        Type[]            actualTypeArguments = parameterizedType.getActualTypeArguments();
                        paramValues[i] = gson.fromJson(paramValue, TypeToken.getParameterized(rowType, actualTypeArguments).getType());
                    } else {    // 非泛型参数
                        paramValues[i] = gson.fromJson(paramValue, paramType);
                    }
                } catch (Exception e) {
                    throw new ActionException("Injecting the parameter error: " + paramName, e);
                }
            }
        }

        // 调用Action中相应的处理方法
        try {
            return (ActionResult) method.invoke(action, paramValues);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ActionException("Error calling method: " + method.getName(), e);
        }
    }
}
