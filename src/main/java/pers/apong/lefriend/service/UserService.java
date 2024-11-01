package pers.apong.lefriend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.apong.lefriend.model.domain.User;
import pers.apong.lefriend.model.dto.UserDTO;
import pers.apong.lefriend.model.dto.UserMatchDto;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

/**
 * 用户
 *
 * @author a
 */
public interface UserService extends IService<User> {
    /**
     * 盐值：用于混淆加密
     */
    String SALT = "otto";
    /**
     * 注册
     *
     * @param userAccount   帐号
     * @param userPassword  密码
     * @param checkPassword 检验密码
     * @param planetCode
     * @return 用户id
     */
    long register(String userAccount, String userPassword, String checkPassword, String planetCode);

    /**
     * 登录
     *
     * @param userAccount  账号
     * @param userPassword 密码
     * @param request      登录请求
     * @return 登录用户对象
     */
    User login(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 登出
     *
     * @param request  登录请求
     * @return 1
     */
    int logout(HttpServletRequest request);

    /**
     * 用户脱敏
     *
     * @param originUser 原始数据
     * @return 安全数据
     */
    User getSafetyUser(User originUser);

    /**
     * 根据标签名搜索用户（缓存匹配）
     *
     * @param tagNameList
     * @return
     */
    List<User> searchUsersByTagsWithCache(List<String> tagNameList);

    /**
     * 根据标签名搜索用户（数据库搜索）
     *
     * @param tagNameList
     * @return
     */
    List<User> searchUsersByTagsWithDB(List<String> tagNameList);

    /**
     * 更新用户信息
     *
     * @param userDTO
     * @param loginUser
     * @return
     */
    User updateUser(UserDTO userDTO, User loginUser);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @return
     */
    User getLoginUser();

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @return
     */
    User getLoginUserPermitNull();

    /**
     * 判断是否用户管理员权限
     *
     * @param request 请求对象，用于获取登录态
     * @return 是否为管理员
     */
    boolean isAdmin(@NotNull HttpServletRequest request);

    /**
     * 判断是否用户管理员权限
     *
     * @param user
     * @return 是否为管理员
     */
    boolean isAdmin(@NotNull User user);

    /**
     * 获取当前登录用户最新信息
     *
     * @param id
     * @return
     */
    User getCurrentUser(Long id);

    /**
     * 获取当前登录用户最新信息
     *
     * @return
     */
    User getCurrentUser();

    /**
     * 获取 n 个匹配的用户
     *
     * @param num
     * @return
     */
    List<User> getMatchUsers(int num);

    /**
     * 计算出目标用户的所有相似用户，按相似度降序排列
     *
     * @param userId
     * @return
     */
    Set<UserMatchDto> getMatchUsers(long userId);
}
