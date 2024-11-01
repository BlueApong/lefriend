package pers.apong.lefriend.service;

import pers.apong.lefriend.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import pers.apong.lefriend.model.domain.User;
import pers.apong.lefriend.model.dto.TeamQuery;
import pers.apong.lefriend.model.request.team.TeamJoinRequest;
import pers.apong.lefriend.model.request.team.TeamQuitRequest;
import pers.apong.lefriend.model.request.team.TeamUpdateRequest;
import pers.apong.lefriend.model.vo.TeamUserVo;
import pers.apong.lefriend.model.vo.TeamVo;

import java.util.List;

/**
* @author a
*/
public interface TeamService extends IService<Team> {

    /**
     * 创建队伍
     *
     * @param team
     * @param loginUser
     * @return
     */
    long addTeam(Team team, User loginUser);

    /**
     * 查询队伍
     *
     * @param teamQuery
     * @return
     */
    List<TeamUserVo> listTeams(TeamQuery teamQuery);

    /**
     * 更新队伍信息（队长或管理员）
     *
     * @param teamUpdateRequest
     * @param loginUser
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

    /**
     * 加入队伍
     *
     * @param teamJoinRequest
     * @param loginUser
     * @return
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    /**
     * 退出队伍
     *
     * @param teamQuitRequest
     * @return
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest);

    /**
     * 删除（解散）队伍
     *
     * @param id
     * @return
     */
    boolean deleteTeam(long id);

    /**
     * 根据 id 获取队伍信息
     *
     * @param id
     * @return
     */
    TeamVo getTeamVoById(long id);

    /**
     * 获取我创建的队伍信息
     *
     * @return
     */
    List<TeamUserVo> getMyTeams(TeamQuery teamQuery);

    /**
     * 获取我加入的队伍信息
     *
     * @return
     */
    List<TeamUserVo> getJoinTeams(TeamQuery teamQuery);
}
