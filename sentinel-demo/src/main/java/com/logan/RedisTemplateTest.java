package com.logan;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

/**
 * @author logan
 * @version 1.0
 * @date 2022/5/24
 */
@SpringBootTest
public class RedisTemplateTest {

    @Resource(name = "customRedisTemplate")
    private RedisTemplate<String, Object> customRedisTemplate;

    @Test
    void testSet() {
        customRedisTemplate.opsForValue().set("sen", "123");
    }

    @Test
    void testGet() {
        Object h1 = customRedisTemplate.opsForValue().get("sen");
        System.out.println(h1);
    }

    @Test
    void name() {
        System.out.println("asdf");
    }
}
