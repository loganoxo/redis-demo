package com.logan.lua;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import javax.annotation.Resource;
import java.util.Collections;

/**
 * @author logan
 * @version 1.0
 * @date 2022/5/24
 * @description TODO
 */
@SpringBootTest
public class LuaTest {

    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT;

    static {
        UNLOCK_SCRIPT = new DefaultRedisScript<>();
        ClassPathResource resource = new ClassPathResource("unlock.lua");
        System.out.println(resource.exists());
        UNLOCK_SCRIPT.setLocation(resource);
        UNLOCK_SCRIPT.setResultType(Long.class);
    }

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Test
    public void test() {
        Object o = stringRedisTemplate.execute(UNLOCK_SCRIPT,
                Collections.singletonList("a"),
                "b");
        System.out.println(o);
    }
}
