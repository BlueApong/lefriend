package pers.apong.lefriend.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import pers.apong.lefriend.model.domain.Team;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import pers.apong.lefriend.model.vo.TeamUserVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TeamMapper extends BaseMapper<Team> {

    /**
     * 查询所有队伍信息（附带成员信息、队长名字）
     *
     * @param ew
     * @return
     */
    List<TeamUserVo> queryAllTeamWithUser(@Param("ew") QueryWrapper<Team> ew,
                                          @Param("current") int current, @Param("pageSize") int pageSize);

    /**
     * 查询队伍信息（附带成员信息）
     *
     * @param teamId
     * @return
     */
    TeamUserVo queryTeamWithUser(@Param("teamId") long teamId);

    /**
     * 查询用户创建的队伍
     *
     * @param userId
     * @return
     */
    List<TeamUserVo> queryMyTeam(@Param("userId") long userId);
}




