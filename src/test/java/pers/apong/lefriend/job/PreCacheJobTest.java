package pers.apong.lefriend.job;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class PreCacheJobTest {
    @Resource
    private PreCacheJob preCacheJob;

    @Test
    void doCacheRecommendUser() {
        preCacheJob.doCacheMatchUser();
    }
}
