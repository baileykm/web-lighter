package com.bailey.web.lighter.utils.file;

import java.io.File;

/**
 * 上传的文件信息
 *
 * @author Bailey
 */
public class UploadFileInfo {
    // 表单中该文件上传字段的名称
    private String fieldName;
    // 原文件名
    private String origFileName;
    // 文件大小
    private long   fileSize;
    // 文件类型
    private String fileType;
    // 服务器端保存的文件名
    private String serverFileName;
    // 服务器端保存的文件相对路径(含文件名)
    private String serverFileRelativePath;
    // 服务器端保存的文件物理路径(含文件名)
    private String serverFileAbsolutePath;

    /**
     * @param fieldName      字段名
     * @param origFileName   原文件名
     * @param fileSize       文件字节数
     * @param fileType       文件MIME类型
     * @param serverFileName 服务器端文件名
     * @param serverFileRelativePath 服务器端保存的文件相对路径(含文件名)
     * @param serverFileAbsolutePath 服务器端保存的文件物理路径(含文件名)
     */
    public UploadFileInfo(String fieldName, String origFileName, long fileSize, String fileType, String serverFileName, String serverFileRelativePath, String serverFileAbsolutePath) {
        this.fieldName = fieldName;
        this.origFileName = origFileName;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.serverFileName = serverFileName;
        this.serverFileRelativePath = serverFileRelativePath;
        this.serverFileAbsolutePath =serverFileAbsolutePath;
    }

    /**
     * 获得表单中该文件上传字段的名称
     *
     * @return 字段名
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * 获得原文件名
     *
     * @return 原文件名
     */
    public String getOrigFileName() {
        return origFileName;
    }

    /**
     * 获得文件字节数
     *
     * @return 字节数
     */
    public long getFileSize() {
        return fileSize;
    }

    /**
     * 获得文件MIME 类型
     *
     * @return 文件类型
     */
    public String getFileType() {
        return fileType;
    }

    /**
     * 获得服务器端存储时使用的文件名
     *
     * @return 服务器端文件名
     */
    public String getServerFileName() {
        return serverFileName;
    }


    /**
     * 获得服务器端保存的文件相对路径(含文件名)
     *
     * @return 服务器端保存的文件相对路径(含文件名)
     */
    public String getServerFileRelativePath() {
        return serverFileRelativePath;
    }
    /**
     * 获得服务器端保存的文件物理路径(含文件名)
     *
     * @return 服务器端保存的文件物理路径(含文件名)
     */
    public String getServerFileAbsolutePath() {
        return serverFileAbsolutePath;
    }

    @Override
    public String toString() {
        return "FileInfo{" + "origFileName='" + origFileName + '\'' + ", fileSize=" + fileSize + ", fileType='" + fileType + '\'' + ", serverFileRelativePath='" + serverFileRelativePath + '\'' + '}';
    }
}
