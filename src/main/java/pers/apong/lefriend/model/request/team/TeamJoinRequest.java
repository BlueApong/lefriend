package pers.apong.lefriend.model.request.team;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 加入队伍请求
 */
@Data
public class TeamJoinRequest implements Serializable {

    private static final long serialVersionUID = 2542066413652521609L;
    /**
     * id
     */
    private Long teamId;

    /**
     * 加入密码
     */
    private String password;
}
