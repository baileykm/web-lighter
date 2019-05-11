package com.bailey.web.lighter.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 读取数据的工具类
 *
 * @author Bailey
 */
public class ContentReader {

    final private static int BUFFER_SIZE = 1024 * 8;

    /**
     * 从 InputStream 从读取字节流数据
     *
     * @param inputStream 输入流
     * @return 读取到的字节流数据
     * @throws IOException IOException
     */
    public static ByteArrayOutputStream readFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[]                buffer = new byte[BUFFER_SIZE];
        int                   length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result;
    }

    /**
     * 从 InputStream 从读取字节流数据
     *
     * @param inputStream 输入流
     * @return 读取到的字节流数据
     * @throws IOException IOException
     */
    public static byte[] readBytes(InputStream inputStream) throws IOException {
        return readFromInputStream(inputStream).toByteArray();
    }

    /**
     * 从 InputStream 从读取数据形成字符串
     *
     * @param inputStream 输入流
     * @param charsetName 字符集名称
     * @return 读取到的字符串数据
     * @throws IOException IOException
     */
    public static String readString(InputStream inputStream, String charsetName) throws IOException {
        return readFromInputStream(inputStream).toString(charsetName);
    }
}
