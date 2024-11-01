package pers.apong.lefriend.common;

/**
 * 返回响应结果工具类
 *
 * @author apong
 */
public class ResultUtils {
    /**
     * 请求成功
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "请求成功");
    }

    /**
     * 请求出错
     *
     * @param code
     * @param msg
     * @param description
     * @return
     */
    public static BaseResponse error(Integer code, String msg, String description) {
        return new BaseResponse<>(code, null, msg, description);
    }

}
