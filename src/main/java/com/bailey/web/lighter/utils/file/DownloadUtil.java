package com.bailey.web.lighter.utils.file;

import nl.bitwalker.useragentutils.Browser;
import nl.bitwalker.useragentutils.UserAgent;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 文件下载工具类
 *
 * @author Bailey
 */
public class DownloadUtil {

    private final static int BUFFER_SIZE = 1024 * 1024;

    /**
     * 对文件名进行编码. IE 和 其他浏览器使用的默认字符集编码不一致, 为避免前端文件名显示乱码, 须区别处理
     * @param req Request对象
     * @param fileName 文件名
     * @return 经过编码的文件名
     * @throws UnsupportedEncodingException 不支持的字符集
     */
    private String encodeFileName(HttpServletRequest req, String fileName) throws UnsupportedEncodingException {
        UserAgent userAgent = UserAgent.parseUserAgentString(req.getHeader("User-Agent"));
        Browser   browser   = userAgent.getBrowser();

        if (browser.getName().contains("IE")) {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } else {
            fileName = new String(fileName.getBytes(), "ISO-8859-1");
        }
        return fileName;
    }

    /**
     * 开始下载, 将数据写入输出流
     *
     * @param req      Request对象
     * @param resp     Response对象
     * @param fileInfo 文件信息
     */
    public void download(HttpServletRequest req, HttpServletResponse resp, DownloadFileInfo fileInfo) {
        try {
            String fileName = encodeFileName(req, fileInfo.getClientFileName());

            InputStream         fis    = fileInfo.getInputStream();
            int                 length = fis.available();
            ServletOutputStream sos    = resp.getOutputStream();

            resp.setContentLength(length);
            resp.setContentType(fileInfo.getContentType());
            resp.setCharacterEncoding("UTF-8");
            resp.addHeader("Content-Disposition", "attachment; filename=" + fileName);

            byte[] buffer    = new byte[BUFFER_SIZE];
            int    readBytes = -1;
            while ((readBytes = fis.read(buffer, 0, BUFFER_SIZE)) != -1) {
                sos.write(buffer, 0, readBytes);
            }
            sos.close();
            fis.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
