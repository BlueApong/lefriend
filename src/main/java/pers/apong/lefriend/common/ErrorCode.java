package pers.apong.lefriend.common;

/**
 * 错误码
 *
 * @author apong
 */
public enum ErrorCode {
    /**
     * 请求参数错误
     */
    PARAMS_ERROR(40000, "请求参数错误"),

    /**
     * 请求数据为空
     */
    NULL_ERROR(40001, "请求数据为空"),

    /**
     * 用户未登录
     */
    NOT_LOGIN(40100, "用户未登录"),

    /**
     * 用户没有权限
     */
    NO_AUTH(40101, "用户没有权限"),

    /**
     * 操作无效
     */
    INVALID(40102, "无效操作"),

    /**
     * 系统内部异常
     */
    SYSTEM_ERROR(50000, "系统内部异常"),

    /**
     * 操作失败
     */
    OPERATION_ERROR(50001, "操作失败");
    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return this.message;
    }

}
