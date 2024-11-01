package pers.apong.lefriend.service;

import cn.hutool.core.collection.CollUtil;
import pers.apong.lefriend.utils.AlgorithmUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
@Slf4j
public class AlgorithmUtilsTest {
    @Test
    void testMatchTags() {
        String tags1 = CollUtil.join(Arrays.asList("Java", "大一", "男"), "");
        String tags2 = CollUtil.join(Arrays.asList("Java编程", "大二", "男"), "");
        String tags3 = CollUtil.join(Arrays.asList("Python", "大二", "女"), "");
        int i = AlgorithmUtils.minDistance(tags1, tags2);
        int j = AlgorithmUtils.minDistance(tags1, tags3);
        System.out.printf("%d : %d%n", i, j);
        Assertions.assertTrue(i < j);
    }
}
