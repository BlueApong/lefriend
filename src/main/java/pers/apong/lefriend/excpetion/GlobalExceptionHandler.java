package pers.apong.lefriend.excpetion;

import pers.apong.lefriend.common.BaseResponse;
import pers.apong.lefriend.common.ErrorCode;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理类
 *
 * @author apong
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 处理业务异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public BaseResponse doBusinessException(BusinessException e) {
        return new BaseResponse(e.getCode(), null, e.getMessage());
    }

    /**
     * 处理业务异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse doRuntimeException(RuntimeException e) {
        return new BaseResponse(ErrorCode.SYSTEM_ERROR, "系统繁忙！");
    }
}
