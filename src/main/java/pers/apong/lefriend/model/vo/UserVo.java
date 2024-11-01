package pers.apong.lefriend.model.vo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户信息
 */
@Data
public class UserVo implements Serializable {

    private static final long serialVersionUID = -5162250159340157688L;
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
    private Integer gender;

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
     * 账号状态：0 - 正常
     */
    private Integer userStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 用户身份：0 - 成员，1 - 管理员
     */
    private Integer userRole;

    /**
     * 星球编号
     */
    private String planetCode;

    /**
     * 用户标签列表json
     */
    private String tags;
}
