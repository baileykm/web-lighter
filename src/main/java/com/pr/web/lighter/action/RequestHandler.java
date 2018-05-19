package com.pr.web.lighter.action;

import com.pr.web.lighter.annotation.Param;
import com.pr.web.lighter.annotation.Request;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HttpServletRequest 处理器信息
 *
 * @author Bailey
 */
public class RequestHandler {
    // 可处理的HttpServletRequest的Url模式,
    private String                         urlPattern;
    // 用于执行HttpServletRequest处理的Action类
    private Class<? extends ActionSupport> actionClass;
    // Action类中用于HttpServletRequest处理的具体方法
    private Method                         method;


    // 用于提取URL中变量名的Regular Expression Pattern
    final private static Pattern      paramRegex = Pattern.compile("\\{(\\w+)\\}");
    // 编译完成的urlPattern
    private              Pattern      urlRegex;
    // url中的占位参数
    private              List<String> urlPlaceholderParams;

    RequestHandler(Class<? extends ActionSupport> actionClass, Method method) {
        this.actionClass = actionClass;
        this.method = method;
        analyzeMethod();
    }


    String getUrlPattern() {
        return urlPattern;
    }

    Class<? extends ActionSupport> getActionClass() {
        return actionClass;
    }

    Method getMethod() {
        return method;
    }

    // 替换原始 url-pattern 中的占位符, 形成正则表达式
    static String getUlrRegex(String urlPattern) {
        return urlPattern.replaceAll("\\*", "\\\\w+").replaceAll("\\.", "\\\\.").replaceAll("\\{\\w+\\}", "(\\\\w+)");
    }

    boolean isMatched(String url) {
        return urlRegex.matcher(url).find();
    }

    /**
     * 获得URL中占位参数的键值对
     * <p>将值处理为String[] 是为了和表单提交的数据形式一致, 以便后续处理</p>
     *
     * @param url 请求的url
     * @return 参数键值对
     * @throws ActionException 需要的参数与实际取得的参数个数不一致
     */
    public Map<String, String[]> getUrlPlaceholderValues(String url) throws ActionException {
        if (urlPlaceholderParams == null) return null;

        // 从URL中提取占位参数值
        Matcher      matcherUrl = urlRegex.matcher(url);
        List<String> values     = new ArrayList<>();
        while (matcherUrl.find()) {
            for (int i = 1, count = matcherUrl.groupCount(); i <= count; i++) {
                values.add(matcherUrl.group(i));
            }
        }

        int paramCount = urlPlaceholderParams.size();
        if (paramCount != values.size()) {
            throw new ActionException("Mismatch Url parameter-placeholder, expect " + paramCount + ", but got " + values.size());
        }

        Map<String, String[]> placeholderValues = new LinkedHashMap<>();
        for (int i = 0; i < paramCount; i++) {
            placeholderValues.put(urlPlaceholderParams.get(i), new String[]{values.get(i)});
        }
        return placeholderValues;
    }

    private void analyzeMethod() {
        Request requestAnnotation = method.getAnnotation(Request.class);
        urlPattern = requestAnnotation.url();

        // 预编译匹配URL的正则表达式
        urlRegex = Pattern.compile(getUlrRegex(urlPattern));

        // 分析 url-pattern 中的占位参数信息
        Matcher paramMatcher = paramRegex.matcher(urlPattern);

        if (paramMatcher.find()) {
            urlPlaceholderParams = new ArrayList<>();
            do {
                for (int i = 1, count = paramMatcher.groupCount(); i <= count; i = i + 2) {
                    urlPlaceholderParams.add(paramMatcher.group(i));
                }
            } while (paramMatcher.find());
        }
    }
}
