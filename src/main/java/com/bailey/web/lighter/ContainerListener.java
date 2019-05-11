package com.bailey.web.lighter;

import com.bailey.web.lighter.action.ActionHelper;
import com.bailey.web.lighter.servlet.DispatcherServlet;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * 监听Web容器初始化事件, 进行初始化
 *
 * @author Bailey
 */
@WebListener
public class ContainerListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // 加载配置
        WebLighterConfig.loadConfiguration();

        // 初始化 ActionHelper
        try {
            ActionHelper.initRequestHandlers();
        } catch (Exception e) {
            LoggerFactory.getLogger(WebLighterConfig.LIB_NAME).error("Web-lighter initialization exception!", e);
        }

        ServletContext context = sce.getServletContext();
        context.addServlet("wlqDispatcher", DispatcherServlet.class).addMapping(WebLighterConfig.getUrlPrefix() + "/*");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}