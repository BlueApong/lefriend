package pers.apong.lefriend.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 队伍信息数据
 *
 */
@Data
public class TeamUserVo implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 创建人id
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

    /**
     * 队长信息
     */
    private String leaderName;

    /**
     * 成员
     */
    private List<UserVo> userList;

    /**
     * 当前用户是否已加入
     */
    private boolean hasJoin;

    /**
     * 队伍目前人数
     */
    private int teamUserNum;

    /**
     * 队伍封面图（队长头像）
     */
    private String teamCoverImage;
}
