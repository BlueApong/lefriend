package pers.apong.lefriend.model.request.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author apong
 */
@Data
public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID = 1234L;

    private String userAccount;
    private String userPassword;
    private String checkPassword;
    private String planetCode;
}
