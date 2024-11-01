package pers.apong.lefriend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import pers.apong.lefriend.common.ErrorCode;
import pers.apong.lefriend.excpetion.BusinessException;
import pers.apong.lefriend.mapper.UserMapper;
import pers.apong.lefriend.model.domain.User;
import pers.apong.lefriend.model.dto.UserDTO;
import pers.apong.lefriend.model.dto.UserMatchDto;
import pers.apong.lefriend.model.enums.Gender;
import pers.apong.lefriend.service.UserService;
import pers.apong.lefriend.utils.AlgorithmUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pers.apong.lefriend.constant.RedisConstant;
import pers.apong.lefriend.constant.UserConstant;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author apong
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2023-04-16 09:41:34
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public long register(String userAccount, String userPassword, String checkPassword, String planetCode) {
        // 非空
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空！");
        }
        // 账号不小于4位
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号过短！");
        }
        // 星球编号不大于5位
        if (planetCode.length() > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编号过长！");
        }
        // 密码不小于8位
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度过短！");
        }
        // 不包含特殊字符
        String validPattern = "[ `~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号不能包含特殊字符！");
        }
        // 两次密码输入相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码必须输入相同！");
        }
        // 账号不能重复（放到最后，先判断其他条件，避免浪费资源）
        // 为查询设置条件
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 判断账号字段是否相等
        queryWrapper.eq("user_account", userAccount);
        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复！");
        }
        // 星球编号不能重复，不能和上面划一起就变成同时相等才算了，或的话也不合适分不出是哪个重复了
        // 再查一次表好吧
        queryWrapper = new QueryWrapper<>();
        // 判断账号字段是否相等
        queryWrapper.eq("planet_code", planetCode);
        count = this.count(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编号重复！");
        }
        // 密码加密，不要明文直接存储到数据库中
        String encryptPassword = DigestUtils.md5DigestAsHex((UserService.SALT + userPassword).getBytes(StandardCharsets.UTF_8));
        // 创建插入数据库的对象
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setPlanetCode(planetCode);
        boolean result = this.save(user);
        // 因为实体类是Long包装类型，如果到时候返回的id是null，就不能对接方法的long签名了
        // 所以失败的时候让它返回-1
        if (!result) {
            return -1;
        }

        return user.getId();
    }

    @Override
    public User login(String userAccount, String userPassword, HttpServletRequest request) {
        // 非空
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空！");
        }
        // 账号不小于4位
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号密码不匹配");
        }
        // 密码不小于8位
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号密码不匹配");
        }
        // 不包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号密码不匹配");
        }
        // 密码加密，检验
        String encryptPassword = DigestUtils.md5DigestAsHex((UserService.SALT + userPassword).getBytes(StandardCharsets.UTF_8));
        // 查询数据库账号密码是否匹配，在此之前判断格式可以省去部分资源
        // 记得开启软删除模式，防止扫描到已经删除的数据
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        queryWrapper.eq("user_password", encryptPassword);
        User user = this.getOne(queryWrapper);
        if(user == null) {
            log.error("login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.NULL_ERROR, "账号密码不匹配");
        }
        // 脱敏处理
        User safetyUser = getSafetyUser(user);
        // 记录登录态
        HttpSession session = request.getSession();
        session.setAttribute(UserConstant.USER_LOGIN_STATE, safetyUser);

        return safetyUser;
    }

    @Override
    public int logout(HttpServletRequest request) {
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return 1;
    }

    @Override
    public User getSafetyUser(User user) {
        if (user == null) {
            return null;
        }
        user.setUserPassword(null);
        return user;
    }

    /**
     * 根据标签名搜索用户（缓存）
     * 在测试两种方法的时间时，可以使用System.currentTimeMillis()方法获取当前时间戳，
     * 如果两种方法在一起测试，一定要排除第一次启动数据库的时间，初次启动会很慢
     *
     * @param tagNameList
     * @return
     */
    @Override
    public List<User> searchUsersByTagsWithCache(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签为空");
        }
        // 1. 先查询所有用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = this.list(queryWrapper);
        // 2. 在内存中查询符合标签的用户
        // Cake: GSON
        Gson gson = new Gson();
        return userList.stream().filter(user -> {
            String tagsStr = user.getTags();
            // 在获取对象后使用前，一定要检查是否为空指针
            if (StringUtils.isBlank(tagsStr)) {
                return false;
            }
            // 不支持直接写类型，getType
            // 用集合方便查询
            Set<String> userTagNameList = gson.fromJson(tagsStr, new TypeToken<Set<String>>() {
            }.getType());
            // Cake: Optional
//            userTagNameList = Optional.ofNullable(userTagNameList).orElse(new HashSet<>());
            // 用户是否含有目标标签
            for (String tagName : tagNameList) {
                // 只要有一个不符合，就不返回
                if (!userTagNameList.contains(tagName)) {
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
    }

    /**
     * 根据标签名搜索用户（数据库搜索）
     *
     * @param tagNameList
     * @return
     */
    @Override
    public List<User> searchUsersByTagsWithDB(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签为空");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 模糊拼接查询
        for (String tagName : tagNameList) {
            queryWrapper = queryWrapper.like("tags", tagName);
        }
        List<User> userList = this.list(queryWrapper);
        if (CollUtil.isEmpty(userList)) {
            return CollUtil.empty(List.class);
        }
        List<User> users = userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
        return users;
    }

    @Override
    public User updateUser(@NotNull UserDTO userDTO, User loginUser) {
        Long id = userDTO.getId();
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (!checkUserFields1ExExceptId(userDTO)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 只有管理员和本人可以更改信息
        if (isAdmin(loginUser) && !Objects.equals(loginUser.getId(), id)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        User user = BeanUtil.copyProperties(userDTO, User.class, "gender");
        user.setGender(Gender.getValueByText(userDTO.getGender()));
        user.setTags(JSONUtil.toJsonStr(userDTO.getTagNameList()));
        boolean b = this.updateById(user);
        return b ? this.getCurrentUser(id) : null;
    }

    /**
     * 判断是否除 id 属性外存在一个属性不为 null
     *
     * @param userDTO
     * @return
     */
    private boolean checkUserFields1ExExceptId(UserDTO userDTO) {
        /*// 保证 set 操作是原子性的，避免数据不一致
        AtomicBoolean flag = new AtomicBoolean(false);
        // declare 表示不获取父类的 fields
        // 没必要使用 forEach 为每个都执行操作
        Arrays.stream(userDTO.getClass().getDeclaredFields()).forEach(field -> {
            if (field.getName().equals("id")) {
                return;
            }
            field.setAccessible(true);
            try {
                if (field.get(userDTO) != null) {
                    flag.set(true);
                }
            } catch (IllegalAccessException e) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR);
            }
        });
        return flag.get();*/
        try {
            for (Field field : userDTO.getClass().getDeclaredFields()) {
                if (field.getName().equals("serialVersionUID")) {
                    continue;
                }
                if (field.getName().equals("id")) {
                    continue;
                }
                field.setAccessible(true);
                if (field.get(userDTO) != null) {
                    return true;
                }
            }
        } catch (IllegalAccessException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return false;
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        User loginUser = (User) session.getAttribute(UserConstant.USER_LOGIN_STATE);
        if (Objects.isNull(loginUser)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        return loginUser;
    }

    @Override
    public User getLoginUser() {
        HttpServletRequest request =
                ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        User loginUser = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (Objects.isNull(loginUser)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        return loginUser;
    }

    @Override
    @Nullable
    public User getLoginUserPermitNull() {
        HttpServletRequest request =
                ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        return (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
    }

    @Override
    public boolean isAdmin(@NotNull HttpServletRequest request) {
        User user = this.getLoginUser(request);
        // 权限控制
        return user == null || user.getUserRole() != UserConstant.ADMIN_ROLE;
    }

    @Override
    public boolean isAdmin(@NotNull User user) {
        // 权限控制
        return user == null || Optional.of(user.getUserRole()).orElse(-1) != UserConstant.ADMIN_ROLE;
    }

    @Override
    public User getCurrentUser(Long id) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        User user = this.getOne(queryWrapper);
        User safetyUser = this.getSafetyUser(user);
        return safetyUser;
    }

    @Override
    public User getCurrentUser() {
        User loginUser = this.getLoginUser();
        User currentUser = query().eq("id", loginUser.getId()).one();
        return this.getSafetyUser(currentUser);
    }

    /**
     * 检索 =》 召回 =》 粗排 =》 精排
     *
     * @return
     */
    @Override
    public List<User> getMatchUsers(int num) {
        // 获取当前用户
        User loginUser = this.getLoginUser();
        long userId = loginUser.getId();
        // 查询计算缓存
        String userMatchCacheKey = RedisConstant.USER_MATCH_CACHE_KEY + userId;
        Set<String> topUserIdSet = stringRedisTemplate.opsForZSet().range(userMatchCacheKey, 0, num - 1);
        if (CollUtil.isEmpty(topUserIdSet)) {
            topUserIdSet = this.getMatchUsers(userId).stream()
                    .map(userMatchDto -> String.valueOf(userMatchDto.getUserId()))
                    .collect(Collectors.toSet());
        }
        // 获取前 n 用户信息
        String topUserIdSeq = CollUtil.join(topUserIdSet, ",");
        List<User> userList = this.query()
                .in("id", topUserIdSet)
                .last("ORDER BY FIELD(id, " + topUserIdSeq + ")")
                .list();
        // 信息脱敏
        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }

    @Override
    public Set<UserMatchDto> getMatchUsers(long userId) {
        // 获取当前用户
        User currentUser = query().eq("id", userId).one();
        String currentUserTags = currentUser.getTags();
        // 查询所有用户
        List<User> users = this.query()
                .select("id", "tags")
                .isNotNull("tags")
                .list();
        // 计算相似距离，缓存前 500 个用户。
        final int MAX_MATCH_USER_NUM = 500;
        PriorityQueue<UserMatchDto> priorityQueue = new PriorityQueue<>(MAX_MATCH_USER_NUM);
        for (User targetUser : users) {
            // 排除自己
            if (Objects.equals(targetUser.getId(), currentUser.getId())) {
                continue;
            }
            int distance = AlgorithmUtils.minDistance(currentUserTags, targetUser.getTags());
            int size = priorityQueue.size();
            // 队列已满
            if (size >= MAX_MATCH_USER_NUM) {
                int maxDistance = priorityQueue.peek().getDistance();
                // 保留更相似的（相似距离小的）
                if (distance >= maxDistance) {
                    continue;
                }
                priorityQueue.poll();
            }
            UserMatchDto userMatchDto = new UserMatchDto();
            userMatchDto.setDistance(distance);
            userMatchDto.setUserId(targetUser.getId());
            priorityQueue.offer(userMatchDto);
        }
        return new HashSet<>(priorityQueue);
    }
}




