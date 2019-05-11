package com.bailey.web.lighter;

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

    // 本工具包的名称
    public static final String LIB_NAME = "com.bailey.web.lighter";

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
     * 加载配置信息
     */
    public static void loadConfiguration() {
        File configFile = null;
        try {
            JAXBContext context = JAXBContext.newInstance(Configuration.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            String rootPath = Thread.currentThread().getContextClassLoader().getResources("/").nextElement().getPath();

            configFile = new File(rootPath, CONFIG_FILE_NAME);

            // 找不到配置文件, 直接返回(取默认值)
            if (!configFile.exists()) return;

            config = (Configuration) unmarshaller.unmarshal(configFile);
        } catch (Exception e) {
            System.err.println("[ INFO ] 读取配置文件失败 " + ((configFile == null) ? "" : "[" + configFile.getAbsolutePath()) + "]" + ", 使用默认值");
        }
    }
}
