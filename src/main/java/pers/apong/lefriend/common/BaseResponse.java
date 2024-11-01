package pers.apong.lefriend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 基本返回类型
 *
 * @author apong
 */
@Data
public class BaseResponse<T> implements Serializable {

    private Integer code;
    private T data;
    private String msg;
    private String description;

    public BaseResponse(Integer code, T data, String msg, String description) {
        this.code = code;
        this.data = data;
        this.msg = msg;
        this.description = description;
    }

    public BaseResponse(Integer code, T data, String msg) {
        this(code, data, msg, "");
    }

    public BaseResponse(Integer code, T data) {
        this(code, data, "");
    }

    /**
     * 用于返回错误结果
     *
     * @param errorCode
     * @param msg
     */
    public BaseResponse(ErrorCode errorCode, String msg) {
        this(errorCode.getCode(), null, msg);
    }
}
