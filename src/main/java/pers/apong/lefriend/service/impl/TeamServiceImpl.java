package pers.apong.lefriend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import pers.apong.lefriend.common.ErrorCode;
import pers.apong.lefriend.common.ThrowUtils;
import pers.apong.lefriend.excpetion.BusinessException;
import pers.apong.lefriend.mapper.TeamMapper;
import pers.apong.lefriend.model.domain.Team;
import pers.apong.lefriend.model.domain.User;
import pers.apong.lefriend.model.domain.UserTeam;
import pers.apong.lefriend.model.dto.TeamQuery;
import pers.apong.lefriend.model.enums.TeamStatusEnum;
import pers.apong.lefriend.model.request.team.TeamJoinRequest;
import pers.apong.lefriend.model.request.team.TeamQuitRequest;
import pers.apong.lefriend.model.request.team.TeamUpdateRequest;
import pers.apong.lefriend.model.vo.TeamUserVo;
import pers.apong.lefriend.model.vo.TeamVo;
import pers.apong.lefriend.model.vo.UserVo;
import pers.apong.lefriend.service.TeamService;
import pers.apong.lefriend.service.UserService;
import pers.apong.lefriend.service.UserTeamService;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.apong.lefriend.constant.RedisConstant;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * todo: add update delete 并发优化
 *
 * @author apong
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {

    @Resource
    private UserTeamService userTeamService;

    @Resource
    private UserService userService;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final DefaultRedisScript<Long> JOIN_TEAM_SCRIPT;

    static {
        JOIN_TEAM_SCRIPT = new DefaultRedisScript<>();
        JOIN_TEAM_SCRIPT.setLocation(new ClassPathResource("join_team.lua"));
        JOIN_TEAM_SCRIPT.setResultType(Long.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {
        final Long userId = loginUser.getId();
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(5);
        // 队伍人数 2-20
        if (maxNum <= 1 || maxNum > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不符合规定");
        }
        String name = team.getName();
        // 标题长度 <= 20
        if (StringUtils.isBlank(name) || name.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        // 描述长度 <= 200
        String description = team.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() > 200) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "描述过长");
        }
        // 状态符合
        int statusValue = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum status = TeamStatusEnum.getEnumByValue(statusValue);
        if (status == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "状态错误");
        }
        // 加密的队伍密码长度要求>=6
        if (TeamStatusEnum.SAFE.equals(status)) {
            String password = team.getPassword();
            if (password.length() < 6 || password.length() > 16) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不符合要求");
            }
        }
        // 超时时间大于当前时间，不设置表示永久
        LocalDateTime expireTime = team.getExpireTime();
        if (expireTime != null && expireTime.isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "超时时间设置无效");
        }
        // 最多创建5个队伍
        long hasTeamNum = Optional.ofNullable(
                lambdaQuery().eq(Team::getUserId, userId).count()
        ).orElse(0L);
        if (hasTeamNum >= 5) {
            throw new BusinessException(ErrorCode.INVALID, "最多创建5个队伍");
        }
        // 新增队伍，插入队伍表
        team.setId(null);
        team.setUserId(userId);
        boolean b = save(team);
        Long teamId = team.getId();
        if (!b || teamId == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        // 将当前用户加入该队伍，插入关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(LocalDateTime.now());
        boolean save = userTeamService.save(userTeam);
        ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR);
        // 保存队伍用户信息到 redis
        stringRedisTemplate.opsForSet().add(RedisConstant.TEAM_UP_TEAM_PREFIX + teamId, userId.toString());
        return teamId;
    }

    @Override
    public List<TeamUserVo> listTeams(TeamQuery teamQuery) {
        QueryWrapper<Team> wrapper = new QueryWrapper<>();
        // 添加条件
        if (teamQuery != null) {
            // 指定id
            Long id = teamQuery.getId();
            wrapper.eq(id != null && id > 0, "t.id", id);
            List<Long> ids = teamQuery.getIds();
            wrapper.in(CollUtil.isNotEmpty(ids), "t.id", ids);
            // 按关键词搜索
            String searchText = teamQuery.getSearchText();
            if (StringUtils.isNotBlank(searchText)) {
                wrapper.and(
                        qw -> qw.like("t.name", searchText)
                                .or()
                                .like("t.description", searchText)
                );
            }
            // 搜索名称
            String name = teamQuery.getName();
            wrapper.like(StringUtils.isNotBlank(name), "t.name", name);
            // 搜索描述
            String description = teamQuery.getDescription();
            wrapper.like(StringUtils.isNotBlank(description), "t.description", description);
            // 指定最大人数
            Integer maxNum = teamQuery.getMaxNum();
            wrapper.eq(maxNum != null && maxNum > 0, "t.max_num", maxNum);
            // 搜索创建人
            Long userId = teamQuery.getUserId();
            wrapper.eq(userId != null && userId > 0, "t.user_id", userId);
            // 搜索不同类型的队伍，允许任何值，涉及私有的后续排除
            Integer status = teamQuery.getStatus();
            wrapper.eq(status != null, "t.status", status);
            // 指定创建时间以内的
            LocalDateTime createTime = teamQuery.getCreateTime();
            if (createTime != null) {
                wrapper.between("t.create_time", createTime, LocalDateTime.now());
            }
        }
        // 只展示未超时队伍 expireTime > now || expireTime == null
        wrapper.and(
                qw -> qw.gt("t.expire_time", LocalDateTime.now())
                        .or().isNull("t.expire_time")
        );
        int pageNum = teamQuery.getPageNum();
        int pageSize = teamQuery.getPageSize();
        int current = (pageNum - 1) * pageSize;
        User loginUser = userService.getLoginUserPermitNull();
        return baseMapper.queryAllTeamWithUser(wrapper, current, pageSize)
                .stream()
                .filter(teamUserVo -> {
                    TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(teamUserVo.getStatus());
                    // 非私密队伍
                    if (!TeamStatusEnum.PRIVATE.equals(teamStatusEnum)) {
                        return true;
                    }
                    // 只有管理员，队伍创建人或已加入才能查看私密队伍
                    if (loginUser == null) {
                        return false;
                    } else if (userService.isAdmin(loginUser)) {
                        return true;
                    } else if (Objects.equals(loginUser.getId(), teamUserVo.getUserId())) {
                        return true;
                    } else if (this.hasJoinTeam(teamUserVo)) {
                        return true;
                    }
                    return false;
                })
                .peek(teamUserVo -> {
                    // 设置队伍人数
                    teamUserVo.setTeamUserNum(teamUserVo.getUserList().size());
                    // 当前用户是否已加入
                    if (loginUser == null) {
                        teamUserVo.setHasJoin(false);
                    } else {
                        teamUserVo.setHasJoin(this.hasJoinTeam(teamUserVo));
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * 当前用户是否已加入队伍
     *
     * @param teamUserVo
     * @return
     */
    private boolean hasJoinTeam(TeamUserVo teamUserVo) {
        User loginUser = userService.getLoginUser();
        for (UserVo userVo : teamUserVo.getUserList()) {
            if (loginUser.getId().equals(userVo.getId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        // 队伍存在
        Long teamId = teamUpdateRequest.getId();
        Team oldTeam = this.getTeamById(teamId);
        // 只有队长或管理员才能修改
        if (!Objects.equals(loginUser.getId(), oldTeam.getUserId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        // 和旧值一致不做更新，前提：每次传所有的值
        TeamUpdateRequest oldData = BeanUtil.copyProperties(oldTeam, TeamUpdateRequest.class);
        if (Objects.equals(teamUpdateRequest, oldData)) {
            return true;
        }
        if (TeamStatusEnum.SAFE.equalsValue(teamUpdateRequest.getStatus())) {
            // 加密队伍，必须携带密码
            if (StringUtils.isBlank(teamUpdateRequest.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "加密队伍需要设置密码");
            }
        } else {
            // 非加密队伍不允许携带密码
            if (StringUtils.isNotBlank(teamUpdateRequest.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "公开队伍无法设置密码");
            }
        }
        Team newTeam = BeanUtil.copyProperties(teamUpdateRequest, Team.class);
        return updateById(newTeam);
    }

    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        // 队伍是否存在
        Long teamId = teamJoinRequest.getTeamId();
        Team team = getTeamById(teamId);
        // 未过期
        if (team.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已过期");
        }
        // 无法加入私有队伍
        Integer status = team.getStatus();
        if (TeamStatusEnum.PRIVATE.equalsValue(status)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "无法加入私有队伍");
        }
        // 加入加密队伍，需要密码匹配
        if (TeamStatusEnum.SAFE.equalsValue(status)) {
            String password = teamJoinRequest.getPassword();
            if (StringUtils.isBlank(password) || !password.equals(team.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
            }
        }
        /**
         * 锁的本质作用：锁住目标的状态，让其在一段区域内是同步更新的，不允许多个步骤同个状态
         */
        //region 分布式锁 todo 并发对比测试 全局锁，用户加锁，队伍加锁
        /**
         * 1. 不同用户同时加入同一个队伍 优先级最高
         * 2. 同一个用户同时加入不同队伍
         * 由于需要尽量放开队伍的申请限制，应该给用户加锁，
         * 借助 redis 存储队伍用户关系，保证队伍状态正确性
         */
        Long userId = loginUser.getId();
        RLock userLock = redissonClient.getLock(RedisConstant.TEAM_UP_USER_LOCK_KEY + userId);
        RLock teamLock = redissonClient.getLock(RedisConstant.TEAM_UP_USER_LOCK_KEY + teamId);
        // 联锁（用户 + 队伍）
        //RLock multiLock = redissonClient.getMultiLock(userLock, teamLock);
        // 阻塞锁
        userLock.lock();
        //multiLock.lock();
        try {
            String teamKey = RedisConstant.TEAM_UP_TEAM_PREFIX + teamId;
            Long result = stringRedisTemplate.execute(
                    JOIN_TEAM_SCRIPT,
                    Collections.singletonList(teamKey),
                    team.getMaxNum().toString(),
                    userId.toString()
            );
            // 判断是否满足条件
            int r = result.intValue();
            if (r != 0) {
                String errorMessage = r == 1 ? "队伍人数已满" : "不能重复加入";
                throw new BusinessException(ErrorCode.PARAMS_ERROR, errorMessage);
            }

            // 最多加入 5 个队伍
            QueryWrapper<UserTeam> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", userId);
            long hasJoinNum = userTeamService.count(wrapper);
            if (hasJoinNum >= 5) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "最多加入5个队伍");
            }
            /*wrapper.eq("team_id", teamId);
            long count = userTeamService.count(wrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能重复加入");
            }
            wrapper = new QueryWrapper<>();
            wrapper.eq("team_id", teamId);
            long teamUserNum = userTeamService.count(wrapper);
            if (teamUserNum >= team.getMaxNum()) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数已满");
            }*/
            //endregion
            // 新增用户-队伍关联信息
            UserTeam userTeam = new UserTeam();
            userTeam.setUserId(userId);
            userTeam.setTeamId(teamId);
            userTeam.setJoinTime(LocalDateTime.now());

            return userTeamService.save(userTeam);
        } catch (BusinessException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, e.getMessage());
        } finally {
            userLock.unlock();
            //multiLock.unlock();
        }
    }

    /**
     * 根据 id 获取队伍信息
     *
     * @param teamId
     * @return
     */
    @NotNull
    private Team getTeamById(Long teamId) {
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        return team;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean quitTeam(TeamQuitRequest teamQuitRequest) {
        Long teamId = teamQuitRequest.getTeamId();
        if (teamId == null || teamId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        /*
            直接查出该队伍附带队员
            拿空间换时间，减少数据库查询次数
         */
        // todo 以队伍为id加锁，防止队伍状态不统一
        // 队伍是否存在
        TeamUserVo teamUserVo = baseMapper.queryTeamWithUser(teamId);
        if (teamUserVo == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        // 当前用户是否已加入队伍
        boolean hasJoin = this.hasJoinTeam(teamUserVo);
        if (!hasJoin) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "尚未加入队伍");
        }
        // 队伍人数
        int teamUserNum = teamUserVo.getUserList().size();
        Long userId = userService.getLoginUser().getId();
        // 只剩一个人队伍解散
        if (teamUserNum == 1) {
            // 移除队伍及队伍用户信息
            this.removeById(teamId);
        } else {
            // 不止一个人且是队长退出队伍，随机转让给队伍中下一个用户
            Long curLeaderId = teamUserVo.getUserId();
            if (Objects.equals(curLeaderId, userId)) {
                long newLeaderId;
                do {
                    int i = RandomUtil.randomInt(0, teamUserNum);
                    newLeaderId = teamUserVo.getUserList().get(i).getId();
                } while (Objects.equals(newLeaderId, curLeaderId));
                boolean update = update().set("user_id", newLeaderId).eq("id", teamId).update();
                if (!update) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR);
                }
            }
        }
        // 移除当前用户队伍关系
        boolean b = userTeamService.update().eq("team_id", teamId).eq("user_id", userId).remove();
        ThrowUtils.throwIf(!b, ErrorCode.OPERATION_ERROR, "退出队伍异常");
        // 移除 redis 中队伍成员信息
        stringRedisTemplate.opsForSet().remove(RedisConstant.TEAM_UP_TEAM_PREFIX + teamId, userId.toString());
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteTeam(long id) {
        // 队伍是否存在
        Team team = getTeamById(id);
        // 是否为队长
        User loginUser = userService.getLoginUser();
        if (!Objects.equals(loginUser.getId(), team.getUserId())) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权限");
        }
        // 移除队伍中所有用户关系
        boolean b = userTeamService.update().eq("team_id", id).remove();
        ThrowUtils.throwIf(!b, ErrorCode.OPERATION_ERROR, "队伍用户信息清除异常");
        return removeById(id);
    }

    @Override
    public TeamVo getTeamVoById(long id) {
        // 队伍存在
        Team team = this.getTeamById(id);
        // 只有队长或管理员才能获取私有队伍
        User loginUser = userService.getLoginUser();
        if (TeamStatusEnum.PRIVATE.equalsValue(team.getStatus())) {
            if (!Objects.equals(loginUser.getId(), team.getUserId()) && !userService.isAdmin(loginUser)) {
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
        }
        return BeanUtil.copyProperties(team, TeamVo.class);
    }

    @Override
    public List<TeamUserVo> getMyTeams(TeamQuery teamQuery) {
        User loginUser = userService.getLoginUser();
        teamQuery.setUserId(loginUser.getId());
        return listTeams(teamQuery);
    }

    @Override
    public List<TeamUserVo> getJoinTeams(TeamQuery teamQuery) {
        // 查询用户加入的队伍关系
        User loginUser = userService.getLoginUser();
        List<UserTeam> userTeams = userTeamService.query().eq("user_id", loginUser.getId()).list();
        // 如果没有加入任何队伍，直接返回空，不然会搜出所有数据
        if (CollUtil.isEmpty(userTeams)) {
            return Collections.emptyList();
        }
        // 由于一个队伍有多个队员，关系表中的teamId会重复
        Set<Long> ids = userTeams.stream().collect(Collectors.groupingBy(UserTeam::getTeamId)).keySet();
        teamQuery.setIds(new ArrayList<>(ids));
        return listTeams(teamQuery);
    }
}




