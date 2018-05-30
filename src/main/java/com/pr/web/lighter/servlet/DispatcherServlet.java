package com.pr.web.lighter.servlet;

import com.pr.web.lighter.WebLighterConfig;
import com.pr.web.lighter.action.ActionException;
import com.pr.web.lighter.action.ActionHelper;
import com.pr.web.lighter.action.RequestHandler;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 负责分发请求的主Servlet
 *
 * @author Bailey
 * @see com.pr.web.lighter.action.ActionResult
 */
public class DispatcherServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");

        // 获得处理当前请求的 RequestHandler
        String         url     = req.getRequestURI().substring((req.getContextPath() + WebLighterConfig.getUrlPrefix()).length());
        RequestHandler handler = ActionHelper.getRequestHandler(url);
        if (handler == null) {
            resp.sendError(404, "No Action mapped for " + url);
            return;
        }

        // 执行 HttpServletRequest 处理
        try {
            new ActionHelper().execute(handler, url, req, resp);
        } catch (ActionException e) {
            throw new RuntimeException("An exception occurred while processing the request !!!", e);
        }
    }
}
