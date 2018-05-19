package com.pr.web.lighter.servlet;

import com.pr.web.lighter.WebLighterConfig;
import com.pr.web.lighter.action.ActionException;
import com.pr.web.lighter.action.ActionHelper;
import com.pr.web.lighter.action.RequestHandler;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 负责分发请求的主Servlet
 * <p>所有以".wlq" 为后缀的请求均由此 Servlet 进行处理</p>
 * <p>与前端的数据交换使用UTF-8字符集.</p>
 * <p>返回前端的数据为 com.pr.web.lighter.action.ActionResult 对象按 JSON 格式序列化后的结果,
 * 如: {"code": 0, "data": ..., "message": "extras", "total": 100} </p>
 *
 * @author Bailey
 * @see com.pr.web.lighter.action.ActionResult
 */
//@WebServlet(urlPatterns = WebLighterConfig.URL_PREFIX + "/*", loadOnStartup = 0)
public class DispatcherServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");

        // 获得处理当前请求的 RequestHandler
        String         url     = req.getRequestURI().substring(WebLighterConfig.getUrlPrefix().length());
        RequestHandler handler = ActionHelper.getRequestHandler(url);
        if (handler == null) {
            throw new RuntimeException("No Action mapped for " + url + " !!!");
        }

        // 执行 HttpServletRequest 处理
        try {
            new ActionHelper().execute(handler, url, req, resp);
        } catch (ActionException e) {
            throw new RuntimeException("An exception occurred while processing the request !!!", e);
        }
    }
}
