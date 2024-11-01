package pers.apong.lefriend.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 队伍自身信息
 *
 */
@Data
public class TeamVo implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 创建人
     */
    private Long userId;

    /**
     * 队伍描述
     */
    private String description;

    /**
     * 队伍可见：0-公开，1-私有，2-加密
     */
    private Integer status;

    /**
     * 队伍最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    private static final long serialVersionUID = 1L;
}
