package pers.apong.lefriend.constant;

public interface RedisConstant {
    String BASE_KEY_PREFIX = "lefriend:";
    /**
     * 相似用户推荐缓存锁
     */
    String USER_MATCH_CACHE_LOCK_KEY = BASE_KEY_PREFIX + "preCacheJob:match:lock";

    /**
     * 相似用户推荐缓存
     */
    String USER_MATCH_CACHE_KEY = BASE_KEY_PREFIX + "preCacheJob:match:";

    /**
     * 用户加入队伍锁
     */
    String TEAM_UP_USER_LOCK_KEY = BASE_KEY_PREFIX + "teamUp:user:lock";
    /**
     * 队伍成员id集合 key 前缀
     */
    String TEAM_UP_TEAM_PREFIX = BASE_KEY_PREFIX + "teamUp:team:";


}
