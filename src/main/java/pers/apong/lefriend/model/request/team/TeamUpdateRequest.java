package pers.apong.lefriend.model.request.team;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 队伍更新请求
 */
@Data
public class TeamUpdateRequest implements Serializable {

    private static final long serialVersionUID = 2542066413652521609L;
    /**
     * id
     */
    private Long id;

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
     * 过期时间
     */
    private LocalDateTime expireTime;
}
