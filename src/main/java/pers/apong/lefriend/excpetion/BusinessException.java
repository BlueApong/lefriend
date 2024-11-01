package pers.apong.lefriend.excpetion;

import pers.apong.lefriend.common.ErrorCode;

/**
 * 业务异常类
 *
 * @author apong
 */
public class BusinessException extends RuntimeException{
    private final Integer code;

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.code = errorCode.getCode();
    }

    public Integer getCode() {
        return code;
    }
}
