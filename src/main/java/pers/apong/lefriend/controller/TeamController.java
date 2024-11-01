package pers.apong.lefriend.controller;

import cn.hutool.core.bean.BeanUtil;
import pers.apong.lefriend.common.BaseResponse;
import pers.apong.lefriend.common.DeleteRequest;
import pers.apong.lefriend.common.ErrorCode;
import pers.apong.lefriend.common.ResultUtils;
import pers.apong.lefriend.excpetion.BusinessException;
import pers.apong.lefriend.model.domain.Team;
import pers.apong.lefriend.model.domain.User;
import pers.apong.lefriend.model.dto.TeamQuery;
import pers.apong.lefriend.model.request.team.TeamAddRequest;
import pers.apong.lefriend.model.request.team.TeamJoinRequest;
import pers.apong.lefriend.model.request.team.TeamQuitRequest;
import pers.apong.lefriend.model.request.team.TeamUpdateRequest;
import pers.apong.lefriend.model.vo.TeamUserVo;
import pers.apong.lefriend.model.vo.TeamVo;
import pers.apong.lefriend.service.TeamService;
import pers.apong.lefriend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 队伍接口
 * @author apong
 */
@RestController
@RequestMapping("/team")
@Slf4j
public class TeamController {
    @Resource
    private TeamService teamService;
    @Resource
    private UserService userService;

    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) {
        if (teamAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = BeanUtil.copyProperties(teamAddRequest, Team.class);
        long id = teamService.addTeam(team, userService.getLoginUser(request));
        return ResultUtils.success(id);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean b = teamService.updateTeam(teamUpdateRequest, loginUser);
        if (!b) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return ResultUtils.success(true);
    }

    /**
     * 获取队伍详细信息（附带队员信息），允许未登录
     *
     * @param teamQuery
     * @return
     */
    @GetMapping("/page/list")
    public BaseResponse<List<TeamUserVo>> listTeams(TeamQuery teamQuery) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<TeamUserVo> teamList = teamService.listTeams(teamQuery);
        return ResultUtils.success(teamList);
    }

    /**
     * 获取队伍自身相关信息（不附带队员信息）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<TeamVo> getTeamInfo(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        TeamVo teamVo = teamService.getTeamVoById(id);
        return ResultUtils.success(teamVo);
    }

    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request) {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.joinTeam(teamJoinRequest, loginUser);
        return ResultUtils.success(result);
    }

    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest) {
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = teamService.quitTeam(teamQuitRequest);
        return ResultUtils.success(result);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = teamService.deleteTeam(deleteRequest.getId());
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "解散失败");
        }
        return ResultUtils.success(result);
    }

    /**
     * 获取当前用户创建的队伍详细信息（携带队员信息）
     *
     * @return
     */
    @GetMapping("/list/my/create")
    public BaseResponse<List<TeamUserVo>> getMyTeams(TeamQuery teamQuery) {
        List<TeamUserVo> teamList = teamService.getMyTeams(teamQuery);
        return ResultUtils.success(teamList);
    }

    /**
     * 获取加入的队伍详细信息（携带队员信息）
     *
     * @return
     */
    @GetMapping("/list/my/join")
    public BaseResponse<List<TeamUserVo>> getJoinTeams(TeamQuery teamQuery) {
        List<TeamUserVo> teamList = teamService.getJoinTeams(teamQuery);
        return ResultUtils.success(teamList);
    }
}
