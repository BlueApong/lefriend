package pers.apong.lefriend.job;

import pers.apong.lefriend.constant.RedisConstant;
import pers.apong.lefriend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static pers.apong.lefriend.constant.RedisConstant.USER_MATCH_CACHE_KEY;

@Component
@Slf4j
public class PreCacheJob {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserService userService;

    @Resource
    private RedissonClient redissonClient;

    /**
     * 相似用户推荐缓存关照——重点用户
     * todo: 如何在海量数据下，缓存所有用户的数据，如何优化？
     */
    private List<Long> mainUserList = Arrays.asList(1L);

    /**
     * 缓存计算相似用户，每小时更新一次缓存
     * 速度从 8s 减少到 15ms。
     */
    @Scheduled(cron = "30 26 * * * *")
    public void doCacheMatchUser() {
        // 避免分布式集群下重复执行
        RLock lock = redissonClient.getLock(RedisConstant.USER_MATCH_CACHE_LOCK_KEY);
        // 默认不等待，释放时间30s
        boolean isLock = lock.tryLock();
        if (!isLock) {
            log.info("{} 未获取到锁", Thread.currentThread().getId());
            return;
        }
        try {
            for (Long userId : mainUserList) {
                String userMatchCacheKey = USER_MATCH_CACHE_KEY + userId.toString();
                // 升序存储，distance越小排名越高
                Set<ZSetOperations.TypedTuple<String>> matchUserSet = userService.getMatchUsers(userId).stream()
                        .map(userMatchDto -> ZSetOperations.TypedTuple.of(String.valueOf(userMatchDto.getUserId()),
                                        (double)userMatchDto.getDistance()))
                        .collect(Collectors.toSet());
                // 写入缓存
                try {
                    stringRedisTemplate.opsForZSet().add(userMatchCacheKey, matchUserSet);
                } catch (Exception e) {
                    log.error("user: {}, match-users cache build error", userId, e);
                }
            }
        } finally {
            lock.unlock();
        }

    }
}
