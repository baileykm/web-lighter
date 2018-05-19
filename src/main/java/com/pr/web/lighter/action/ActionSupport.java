package com.pr.web.lighter.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Action 的公共父类, 所示Action应继承此类
 *
 * @author Bailey
 */
public abstract class ActionSupport {

    private HttpServletRequest request;

    void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * 获得Request对象
     *
     * @return Request
     */
    protected HttpServletRequest getRequest() {
        return request;
    }

    /**
     * 获得Session对象
     *
     * @return Session
     */
    protected HttpSession getSession() {
        return request.getSession();
    }
}
