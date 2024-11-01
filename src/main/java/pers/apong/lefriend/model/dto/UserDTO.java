package pers.apong.lefriend.model.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * User请求体
 *
 */
@Data
public class UserDTO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 登录账号
     */
    private String userAccount;

    /**
     * 用户头像链接
     */
    private String avatarUrl;

    /**
     * 性别：0 - 男，1 - 女
     */
    private String gender;

    /**
     * 电话号码
     */
    private String phone;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 个人简介
     */
    private String profile;

    /**
     * 星球编号
     */
    private String planetCode;

    /**
     * 用户标签列表json
     */
    private List<String> tagNameList;

    private static final long serialVersionUID = 1L;
}
