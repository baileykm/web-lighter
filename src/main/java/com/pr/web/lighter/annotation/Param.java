package com.pr.web.lighter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注当前形参可接收前端上行数据.
 * <p>上行数据中参数名应与本注解的 name 一致, 默认值为 data </p>
 * <p>上行数据可是 JSON 格式或 com.google.gson.JsonPrimitive ( Integer, Long, Short, Float, Double, Byte, Boolean, Character )</p>
 * @see com.google.gson.JsonPrimitive
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {
    /**
     * 参数名
     * @return 参数名
     */
    String name() default "data";
}
