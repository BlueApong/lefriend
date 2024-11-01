package pers.apong.lefriend.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 *
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
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
     * 登录密码
     */
    private String userPassword;

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
     * 是否删除：0，1
     */
    @TableLogic
    private Integer isDelete;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
