package pers.apong.lefriend.model.request.team;

import lombok.Data;

import java.io.Serializable;

/**
 * 退出队伍请求
 */
@Data
public class TeamQuitRequest implements Serializable {

    private static final long serialVersionUID = 2542066413652521609L;
    /**
     * id
     */
    private Long teamId;
}
