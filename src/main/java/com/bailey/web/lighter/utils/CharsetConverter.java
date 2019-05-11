package com.bailey.web.lighter.utils;

import java.io.UnsupportedEncodingException;

/**
 * 字符集转换工具
 *
 * @author Bailey
 */
public class CharsetConverter {
    /**
     * 字符集ISO8859-1
     */
    public final static String CHARSET_ISO8859_1 = "ISO8859-1";

    /**
     * 字符集UTF-8
     */
    public final static String CHARSET_UTF_8 = "UTF-8";

    /**
     * 字符集GBK
     */
    public final static String CHARSET_GBK    = "GBK";
    /**
     * 字符集GB2312
     */
    public final static String CHARSET_GB2312 = "GB2312";


    private String from;
    private String to;

    /**
     * 构建一个字符集转换器实例
     *
     * @param from 源字符集
     * @param to   目标字符集
     */
    public CharsetConverter(String from, String to) {
        this.from = from;
        this.to = to;
    }

    /**
     * 将字符串从<code>from</code>字符集转成<code>to</code>字符集
     *
     * @param str 待转换的字符串
     * @return 转成目标字符集编码的字符串
     * @throws UnsupportedEncodingException 不支持的字符集编码
     */
    public String convert(String str) throws UnsupportedEncodingException {
        if (str == null) return null;
        if (from == null || to == null) return str;
        return new String(str.getBytes(from), to);
    }

    /**
     * 将字符串 <code>str</code> 由 Latin1 (ISO8859-1) 编码转为 UTF-8
     * <p>多数浏览器默认编码为 Latin1 (ISO8859-1), 若浏览器端提交的数据显示为乱码, 常可使用此方法处理</p>
     *
     * @param str 源字符串
     * @return 使用 UTF-8 编码的字符串
     * @throws UnsupportedEncodingException 不支持的字符集
     */
    public static String latin1ToUTF8(String str) throws UnsupportedEncodingException {
        return new String(str.getBytes(CHARSET_ISO8859_1), CHARSET_UTF_8);
    }
}
