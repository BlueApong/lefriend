package pers.apong.lefriend.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import pers.apong.lefriend.common.BaseResponse;
import pers.apong.lefriend.common.ErrorCode;
import pers.apong.lefriend.common.ResultUtils;
import pers.apong.lefriend.excpetion.BusinessException;
import pers.apong.lefriend.model.domain.Tag;
import pers.apong.lefriend.model.domain.User;
import pers.apong.lefriend.model.dto.UserDTO;
import pers.apong.lefriend.model.request.user.UserLoginRequest;
import pers.apong.lefriend.model.request.user.UserRegisterRequest;
import pers.apong.lefriend.model.request.user.UserTagAddRequest;
import pers.apong.lefriend.service.TagService;
import pers.apong.lefriend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用户接口
 * @author apong
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Resource
    private UserService userService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private TagService tagService;

    @PostMapping("/register")
    public BaseResponse<Long> register(@RequestBody UserRegisterRequest userRegisterRequest) {
        if(userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数错误！");
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();
        long userId = userService.register(userAccount, userPassword, checkPassword, planetCode);
        return ResultUtils.success(userId);
    }

    @PostMapping("/login")
    public BaseResponse<User> login(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        // 基本的参数检验
        if(userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数错误！");
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        User user = userService.login(userAccount, userPassword, request);
        // TODO: 将参数错误修改为具体的错误
        return ResultUtils.success(user);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> logout(HttpServletRequest request) {
        int logout = userService.logout(request);
        return ResultUtils.success(logout);
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        User oldUser = userService.getLoginUser(request);
        if(oldUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "请先登录！");
        }
        // 为确保用户状态更新，选择从数据库中查询
        Long id = oldUser.getId();
        User user = userService.getCurrentUser(id);
        return ResultUtils.success(user);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
        if(userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "你没有权限！");
        }
        // 内容为空也能查呀！！！
//        if(StringUtils.isBlank(username)) {
//            return null;
//        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 模糊查询
        if(StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> safeTyUserList = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(safeTyUserList);
    }

    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchByTagNameList(@RequestParam(required = false) List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<User> users = userService.searchUsersByTagsWithDB(tagNameList);
        return ResultUtils.success(users);
    }

    @PostMapping("/tags/add")
    public BaseResponse<Boolean> addUserTags(@RequestBody UserTagAddRequest userTagAddRequest) {
        if (userTagAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<String> tagNameList = userTagAddRequest.getTagNameList();
        List<String> newTagList = userTagAddRequest.getNewTagList();
        boolean a = tagService.addUserTags(tagNameList, newTagList);
        if (!a) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "标签添加失败");
        }
        return ResultUtils.success(true);
    }

    @GetMapping("/tags/remove")
    public BaseResponse<Boolean> removeUserTags(String tag) {
        if (StringUtils.isBlank(tag)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User currentUser = userService.getCurrentUser();
        List<String> tagNameList = JSONUtil.toList(currentUser.getTags(), String.class);
        boolean b = tagNameList.removeIf(tagName -> tagName.equals(tag));
        if (!b) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "标签移除失败");
        }
        // 保存新标签
        boolean update = userService.update()
                .set("tags", JSONUtil.toJsonStr(tagNameList))
                .eq("id", currentUser.getId())
                .update();
        if (!update) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "标签移除失败");
        }
        return ResultUtils.success(true);
    }

    @GetMapping("/tags/other")
    public BaseResponse<List<String>> getOtherTags() {
        User currentUser = userService.getCurrentUser();
        List<String> tagNameList = JSONUtil.toList(currentUser.getTags(), String.class);
        if (CollUtil.isEmpty(tagNameList)) {
            tagNameList = Collections.emptyList();
        }
        List<String> finalTagNameList = tagNameList.stream().map(String::toUpperCase).collect(Collectors.toList());
        List<String> otherTagList = tagService.list().stream()
                .map(Tag::getTagName)
                .filter(tagName -> !finalTagNameList.contains(tagName.toUpperCase()))
                .collect(Collectors.toList());
        return ResultUtils.success(otherTagList);
    }

    @GetMapping("/recommend")
    public BaseResponse<Page<User>> getRecommendUsers(long pageNum, long pageSize, HttpServletRequest request) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        User loginUser = userService.getLoginUserPermitNull();
        if (loginUser != null) {
            wrapper.not(qw -> qw.eq("id", loginUser.getId()));
        }
        Page<User> userPage = userService.page(new Page<>(pageNum, pageSize), wrapper);
        List<User> userList = userPage.getRecords().stream()
                .map(userService::getSafetyUser).collect(Collectors.toList());
        userPage.setRecords(userList);
        return ResultUtils.success(userPage);
    }

    /**
     * 匹配相关用户，
     * 和推荐不同，推荐可以是系统意愿
     *
     * @param num 匹配 n 个用户
     * @return
     */
    @GetMapping("/match")
    public BaseResponse<List<User>> getMatchUsers(int num) {
        List<User> userList = userService.getMatchUsers(num);
        return ResultUtils.success(userList);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> delete(@RequestBody long id, HttpServletRequest request) {
        if(userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "你没有权限！");
        }
        if(id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id值必须大于0！");
        }
        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }

    @PostMapping("/update")
    public BaseResponse<User> updateUser(@RequestBody UserDTO userDTO, HttpServletRequest request) {
        if (Objects.isNull(userDTO)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if (Objects.isNull(loginUser)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        User currentUser = userService.updateUser(userDTO, loginUser);
        return ResultUtils.success(currentUser);
    }
}
