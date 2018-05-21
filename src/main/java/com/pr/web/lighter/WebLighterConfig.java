package com.pr.web.lighter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;

/**
 * web-lighter 的总体配置
 *
 * @author Bailey
 */
public class WebLighterConfig {

    // 配置文件名
    private static final String CONFIG_FILE_NAME = "web-lighter.xml";

    // 配置信息
    private static Configuration config = new Configuration();

    /**
     * 配置信息
     */
    @XmlRootElement(name = "configuration")
    private static class Configuration {

        // HTTP请求URL前缀
        @XmlElement(name = "urlPrefix")
        public String urlPrefix = "/wl";

        // 日期型数据的序列化/反序列化格式
        @XmlElement(name = "dateFormat")
        public String dateFormat = "yyyy-MM-dd hh:mm:ss";

        // 是否输出URL映射报表
        @XmlElement(name = "printUrlMapReport")
        public boolean printUrlMapReport = false;
    }

    /**
     * HTTP 请求 URL 前缀
     *
     * @return URL 前缀
     */
    public static String getUrlPrefix() {
        return config.urlPrefix;
    }

    /**
     * 获得日期型数据的序列化/反序列化格式
     *
     * @return 日期型数据的序列化/反序列化格式
     */
    public static String getDateFormat() {
        return config.dateFormat;
    }

    /**
     * 是否输出URL映射报表
     * @return 是否输出URL映射报表
     */
    public static boolean isPrintUrlMapReport() {
        return config.printUrlMapReport;
    }

    ;

    /**
     * 加载配置信息
     */
    public static void loadConfiguration() {
        File configFile = null;
        try {
            JAXBContext  context      = JAXBContext.newInstance(Configuration.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            String       rootPath     = Thread.currentThread().getContextClassLoader().getResources("/").nextElement().getPath();

            configFile = new File(rootPath, CONFIG_FILE_NAME);

            // 找不到配置文件, 直接返回(取默认值)
            if (!configFile.exists()) return;

            config = (Configuration) unmarshaller.unmarshal(configFile);
        } catch (Exception e) {
            System.err.println("[ INFO ] 读取配置文件失败 " + ((configFile == null) ? "" : "[" + configFile.getAbsolutePath()) + "]" + ", 使用默认值");
        }
    }
}
