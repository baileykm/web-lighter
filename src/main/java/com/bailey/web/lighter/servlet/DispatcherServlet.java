package com.bailey.web.lighter.servlet;

import com.bailey.web.lighter.WebLighterConfig;
import com.bailey.web.lighter.action.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 负责分发请求的主Servlet
 *
 * @author Bailey
 * @see ActionResult
 */
public class DispatcherServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        RequestHandler handler = null;
        String requestUrl = null;
        String uri = null;
        try {
            req.setCharacterEncoding("UTF-8");
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("text/html;charset=UTF-8");

            // 获得处理当前请求的 RequestHandler
            uri = req.getRequestURI();
            requestUrl = uri.substring((req.getContextPath() + WebLighterConfig.getUrlPrefix()).length());
            handler = ActionHelper.getRequestHandler(requestUrl);
            if (handler == null) {
                resp.sendError(404, "No Action mapped for " + uri);
                return;
            }
        } catch (IOException e) {
            ActionLogger.logger.error("Dispatch request error.", e);
            throw new RuntimeException(e);
        }

        try {
            // 执行 HttpServletRequest 处理
            new ActionHelper().execute(handler, requestUrl, req, resp);
        } catch (ActionException | IOException e) {
            ActionLogger.logger.error("Invoke action method error:  " + handler.toString(), e);
            throw new RuntimeException(e);
        }
    }
}
