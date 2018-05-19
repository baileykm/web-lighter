package com.pr.web.lighter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注于Action中的方法上, 说明该方法将处理文件上传请求
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Upload {
    /**
     * 文件保存目录, 默认根目录下的upload子目录
     * @return 文件保存目录
     */
    String uploadDir() default "upload";

    /**
     * 单个文件的最大字节数
     * @return 单个文件的最大字节数
     */
    int maxFileSize() default 1024 * 1024 * 40;

    /**
     * 请求的最大字节数
     * @return 请求的最大字节数
     */
    int maxRequestSize() default 1024 * 1024 * 50;
}
