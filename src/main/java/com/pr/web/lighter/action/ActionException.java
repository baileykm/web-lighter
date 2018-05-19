package com.pr.web.lighter.action;

/**
 * Action异常
 *
 * @author Bailey
 */
public class ActionException extends Exception {
    private Throwable rootCause;

    public ActionException() {
        super();
    }

    public ActionException(String message) {
        super(message);
    }

    public ActionException(String message, Throwable rootCause) {
        super(message, rootCause);
        this.rootCause = rootCause;
    }

    public ActionException(Throwable rootCause) {
        super(rootCause);
        this.rootCause = rootCause;
    }

    public Throwable getRootCause() {
        return rootCause;
    }
}
