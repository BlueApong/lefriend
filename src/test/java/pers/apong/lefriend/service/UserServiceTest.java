package pers.apong.lefriend.service;

import pers.apong.lefriend.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserService userService;

    @Test
    void testAddUser() {
        User user = new User();
        user.setUsername("dogYupi");
        user.setUserAccount("123");
        user.setUserPassword("123");
        user.setAvatarUrl("https://bcdh.yuque.com/dashboard");
        user.setGender(0);
        user.setPhone("123");
        user.setEmail("456");

        boolean save = userService.save(user);
        Assertions.assertTrue(save);

    }

    @Test
    void testRegister() {
        // 非空
        String userAccount = "";
        String userPassword = "12345678";
        String checkPassword = "12345678";
        long result = userService.register(userAccount, userPassword, checkPassword, "999");
        Assertions.assertEquals(-1, result);
        // 账号不小于4位
        userAccount = "123";
        result = userService.register(userAccount, userPassword, checkPassword, "999");
        Assertions.assertEquals(-1, result);
        // 不包含特殊字符
        userAccount = "ot to";
        userPassword = "12345678";
        result = userService.register(userAccount, userPassword, checkPassword, "999");
        Assertions.assertEquals(-1, result);
        // 密码不小于8位
        userAccount = "apong";
        userPassword = "1234567";
        result = userService.register(userAccount, userPassword, checkPassword, "999");
        Assertions.assertEquals(-1, result);
        // 两次密码相同
        userAccount = "apong";
        userPassword = "12345678";
        checkPassword = "123456789";
        result = userService.register(userAccount, userPassword, checkPassword, "999");
        Assertions.assertEquals(-1, result);
        // 账号不能重复
        userAccount = "apong";
        checkPassword = "12345678";
        result = userService.register(userAccount, userPassword, checkPassword, "999");
        Assertions.assertEquals(-1, result);
        // 星球编号不能重复，为不影响原本测试，其他的测试都设为999
        userAccount = "yupidog";
        result = userService.register(userAccount, userPassword, checkPassword, "1");
        Assertions.assertEquals(-1, result);
        // 成功插入
        String planetCode = "2";
        result = userService.register(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertTrue(result > 0);
    }

    @Test
    void testLogin() {
        // 登录服务做不了直接测试，需要用接口去测试
    }

    @Test
    void deleteUser() {
        boolean b = userService.removeById(6L);
        Assert.isTrue(b, "删除成功");
    }

    @Test
    void searchUsersByTagsWithDB() {
        // TODO 后续是否需要考虑大小写问题
        List<String> tagNameList = Arrays.asList("java", "python");
        List<User> userList = userService.searchUsersByTagsWithDB(tagNameList);
        Assert.notNull(userList);

    }

    @Test
    void searchUsersByTagsWithCache() {
        List<String> tagNameList = Arrays.asList("java", "python");
        List<User> userList = userService.searchUsersByTagsWithCache(tagNameList);
        Assert.notNull(userList);
    }

    @Test
    void getMatchUsers() {
        List<User> matchUsers = userService.getMatchUsers(5);
        Assertions.assertNotNull(matchUsers);
    }
}
