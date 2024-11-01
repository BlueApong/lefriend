package pers.apong.lefriend.model.dto;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * 用户匹配数据类
 */
@Data
public class UserMatchDto implements Comparable {
    /**
     * 相似距离
     */
    private int distance;
    /**
     * 用户id
     */
    private long userId;

    /**
     * 相似距离最大的排前面
     *
     * @param o the object to be compared.
     * @return
     */
    @Override
    public int compareTo(@NotNull Object o) {
        UserMatchDto oi = (UserMatchDto) o;
        return -1 * Integer.compare(distance, oi.distance);
    }
}
