package pers.apong.lefriend.service;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.concurrent.*;

import pers.apong.lefriend.model.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;

@SpringBootTest // fixme: 别开开了，运行会自动测试的。。
public class InsertUsersTest {
    @Resource
    private UserService userService;

    /**
     * MP 批插入，22秒左右
     */
    @Test
    void insertUsersByBatch() {
        final int INSERT_NUM = 100_000;
        final int BATCH_SIZE = 2500;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 分为 n 批
        for (int i = 0; i < INSERT_NUM / BATCH_SIZE; i++) {
            List<User> userList = new ArrayList<>();
            // 一批 BATCH_SIZE 个新用户
            for (int j = 0; j < BATCH_SIZE; j++) {
                User user = new User();
                user.setUsername("fakeUser");
                user.setUserAccount("fakeUser");
                user.setUserPassword("12345678");
                user.setAvatarUrl("https://pic.code-nav.cn/user_avatar/1628290566401896450/4tJFqUG0-WechatIMG14.jpeg");
                user.setGender(0);
                user.setPhone("12312312321");
                user.setEmail("123@qq.com");
                user.setProfile("一个假人");
                user.setUserRole(0);
                user.setPlanetCode("12345");
                user.setTags("[\"java\"]");
                userList.add(user);
            }
            userService.saveBatch(userList, BATCH_SIZE);
        }
        stopWatch.stop();
        System.out.println("总用时：" + stopWatch.getTotalTimeMillis());
    }

    /**
     * 测试异步 batch 插入数据，6866 ms
     */
    @Test
    void insertUsersByAsyncBatch() {
        final int INSERT_NUM = 100_000;
        final int BATCH_SIZE = 5_000;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        // 分为 n 批
        for (int i = 0; i < INSERT_NUM / BATCH_SIZE; i++) {
            List<User> userList = new ArrayList<>();
            // 一批 BATCH_SIZE 个新用户
            for (int j = 0; j < BATCH_SIZE; j++) {
                User user = new User();
                user.setUsername("fakeUser");
                user.setUserAccount("fakeUser");
                user.setUserPassword("12345678");
                user.setAvatarUrl("https://pic.code-nav.cn/user_avatar/1628290566401896450/4tJFqUG0-WechatIMG14.jpeg");
                user.setGender(0);
                user.setPhone("12312312321");
                user.setEmail("123@qq.com");
                user.setProfile("一个假人");
                user.setUserRole(0);
                user.setPlanetCode("12345");
                user.setTags("[\"java\"]");
                userList.add(user);
            }
            // 异步执行
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println(Thread.currentThread().getName());
                userService.saveBatch(userList, BATCH_SIZE);
            });
            futureList.add(future);
        }
        // 执行后合并异步
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        stopWatch.stop();
        System.out.println("总用时：" + stopWatch.getTotalTimeMillis());
    }

    private ExecutorService executorService = new ThreadPoolExecutor(40, 500,
            10_000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(10_000));

    /**
     * 测试 超量线程池 异步 batch 插入数据
     * 插入数量 - 批数： 10w - 2500 耗时：6297 ms 40线程
     * 插入数量 - 批数： 20w - 2500 耗时：10719 ms 80线程
     */
    @Test
    void insertUsersBySuperAsyncBatch() {
        final int INSERT_NUM = 10_000;
        final int BATCH_SIZE = 2_500;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        int count = 1;
        // 分为 n 批
        for (int i = 0; i < INSERT_NUM / BATCH_SIZE; i++) {
            List<User> userList = new ArrayList<>();
            // 一批 BATCH_SIZE 个新用户
            for (int j = 0; j < BATCH_SIZE; j++) {
                User user = new User();
                user.setUsername("fakeUser");
                user.setUserAccount("fakeUser"+count++);
                user.setUserPassword("94944c48331237ad2885a97f0d0d72a0");
                user.setAvatarUrl("https://pic.code-nav.cn/user_avatar/1628290566401896450/4tJFqUG0-WechatIMG14.jpeg");
                user.setGender(0);
                user.setPhone("12312312321");
                user.setEmail("123@qq.com");
                user.setProfile("一个假人");
                user.setUserRole(0);
                user.setPlanetCode("12345");
                user.setTags("[\"java\"]");
                userList.add(user);
            }
            // 异步执行
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println(Thread.currentThread().getName());
                userService.saveBatch(userList, BATCH_SIZE);
            }, executorService);
            futureList.add(future);
        }
        // 执行后合并异步
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        stopWatch.stop();
        System.out.println("总用时：" + stopWatch.getTotalTimeMillis());
    }
}
