package com.pr.web.lighter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注于Action中的方法上, 说明该方法为处理前端请求的方法.
 * <p>参数说明:</p>
 * <pre>
 *     url          - 可处理的请求的url, 支持通配符和参数, 如: /{param1}/*.action/{param2}
 *                    <strong>-- 注意 --</strong>
 *                    - web-lighter 使用路径匹配方式拦截前端请求, 默认情况下, 若请求的 url 匹配模式 "/wl/*" 时将被 web-lighter 拦截并处理. 若需要更改拦截匹配模式, 请在配置文件中进行设置.
 *                    - 前端访问路径记得添加路径前缀 ( 默认为"/wl" ), 如: http://localhost:8080<strong style="color:red">/wl</strong>/doSomething.action
 *                    - 本注解的 url 参数无需添加路径前缀, 如: /doSomething, 运行时 web-lighter 将会匹配 /wl/doSomething
 *                    - 虽然 url 中支持类似 RESTful Web 风格的参数, 但 web-lighter 暂未完全支持 RESTful Web 的标准方法
 *
 *     format       - 上行数据的格式, 默认 ParamFormat.json, Content-Type = "application/json" 时此参数无效 ( 始终被理解为JSON 格式数据)
 * </pre>
 *
 * @see ParamFormat
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Request {
    /**
     * 可处理的请求的url. 详见 {@link Request}
     * @return url
     */
    String url();

    /**
     * 上行参数类型. 详见 {@link Request}
     * @return 参数类型
     */
    ParamFormat format() default ParamFormat.json;
}
