package com.bailey.web.lighter.annotation;


import com.bailey.web.lighter.utils.file.UploadFileInfo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注当前形参为可接收文件信息.
 * <p>参数类型必须为 FileInfo 或 {@code List<FileInfo> }. 若参数类型为 FileInfo 则注入成功上传的第1个文件信息</p>
 *
 * @see UploadFileInfo
 */

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)

public @interface ParamFileInfo {

}
