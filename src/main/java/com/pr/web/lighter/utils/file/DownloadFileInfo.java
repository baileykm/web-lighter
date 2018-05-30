package com.pr.web.lighter.utils.file;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 下载文件的信息
 *
 * @author Bailey
 */
public class DownloadFileInfo {
    // 文件数据输入流
    private InputStream inputStream;
    // 客户端默认保存的文件名
    private String      clientFileName;
    // 文件类别
    private String      contentType;

    /**
     * 从<code>inputStream</code>中读取数据, 供前端下载
     * @param inputStream    前端保存时使用的默认文件名
     * @param clientFileName 文件名
     * @param contentType 文件的contentType, 供前端浏览器识别
     */
    public DownloadFileInfo(InputStream inputStream, String clientFileName, String contentType) {
        this.inputStream = inputStream;
        this.clientFileName = clientFileName;
        this.contentType = contentType;
    }

    /**
     * 从<code>file</code>中读取数据, 供前端下载
     * @param file           文件
     * @param clientFileName 前端保存时使用的默认文件名
     * @throws FileNotFoundException 文件未找到
     */
    public DownloadFileInfo(File file, String clientFileName) throws FileNotFoundException {
        this(new FileInputStream(file), clientFileName, getContentType(file));
    }

    /**
     * 获得文件的 ContentType
     *
     * @param file 待下载的文件
     * @return 文件的ContentType
     */
    public static String getContentType(File file) {
        String type = null;
        try {
            Path path = Paths.get(file.getAbsolutePath());
            type = Files.probeContentType(path);
        } catch (IOException e) {
        }
        return type;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public String getClientFileName() {
        return clientFileName;
    }

    public String getContentType() {
        return contentType;
    }
}
