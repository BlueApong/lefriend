package pers.apong.lefriend.model.request.team;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TeamAddRequest {
    /**
     * 队伍名称
     */
    private String name;

    /**
     * 加入密码
     */
    private String password;

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
}
