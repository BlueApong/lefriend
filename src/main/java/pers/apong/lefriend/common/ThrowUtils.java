package pers.apong.lefriend.common;

import pers.apong.lefriend.excpetion.BusinessException;

/**
 * 异常抛出工具类
 */
public class ThrowUtils {
    public static void throwIf(boolean condition, ErrorCode code, String msg) {
        if (condition) {
            throw new BusinessException(code, msg);
        }
    }
    public static void throwIf(boolean condition, ErrorCode code) {
        if (condition) {
            throw new BusinessException(code);
        }
    }
}
