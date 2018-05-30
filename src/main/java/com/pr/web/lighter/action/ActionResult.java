package com.pr.web.lighter.action;

/**
 * 用于封装 Action 处理结果
 * <p>封装的数据包括: </p>
 * <pre>
 * code     - 状态码, 正数或 0 表示成功, 默认为0; 负数表示出错
 * result     - 回传的业务数据
 * message  - 附加消息, 通常用于保存提示信息, 如: 出错原因
 * total    - 全部记录数. 通常用于分页查询时返回符合条件的总记录数
 * </pre>
 *
 * @author Bailey
 */
public class ActionResult {
    final public static int CODE_SUCCESS         = 0;
    final public static int CODE_FAILURE_DEFAULT = -1;

    private int    code;
    private Object result;
    private String message;
    private Long   total;

    public ActionResult(int code, Object result, String message, Long total) {
        this.code = code;
        this.result = result;
        this.message = message;
        this.total = total;
    }

    public static ActionResult success(int code, Object data, String message, Long total) {
        if (code < 0) {
            System.err.println("[WARNING] The success-code is " + code + ". Usually, it should be >= 0.");
        }
        return new ActionResult(code, data, message, total);
    }

    public static ActionResult success(Object data, String message, Long total) {
        return success(CODE_SUCCESS, data, message, total);
    }

    public static ActionResult success(Object data, Long total) {
        return success(CODE_SUCCESS, data, null, total);
    }

    public static ActionResult success(Object data, String message) {
        return success(data, message, null);
    }

    public static ActionResult success(Object data) {
        return success(data, null, null);
    }

    public static ActionResult success() {
        return success(null);
    }


    public static ActionResult failure(int code, Object data, String message) {
        if (!(code < 0)) {
            System.err.println("[WARNING] The failure-code is " + code + ". Usually, it should be < 0.");
        }
        return new ActionResult(code, data, message, null);
    }

    public static ActionResult failure(Object data, String message) {
        return failure(CODE_FAILURE_DEFAULT, data, message);
    }

    public static ActionResult failure(String message) {
        return failure(null, message);
    }

    public static ActionResult failure() {
        return failure(null, null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object data) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
