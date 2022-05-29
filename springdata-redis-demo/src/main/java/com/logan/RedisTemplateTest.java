package com.logan;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;

/**
 * @author logan
 * @version 1.0
 * @date 2022/5/24
 * @description TODO
 */
@SpringBootTest
public class RedisTemplateTest {

    @Resource(name = "redisTemplate")
    private RedisTemplate redisTemplate;

    @Resource(name = "customRedisTemplate")
    private RedisTemplate<String, Object> customRedisTemplate;

    @Test
    void testString() {
        //默认是jdk序列化
        redisTemplate.opsForValue().set("h1", "hah");
        Object h1 = redisTemplate.opsForValue().get("h1");
        System.out.println(h1);
    }


    @Test
    void testCustom() {
        //自定义序列化序列化
        customRedisTemplate.opsForValue().set("s1", "uouio");
        Object s1 = customRedisTemplate.opsForValue().get("s1");
        System.out.println(s1);

        Long num = 290L;
        customRedisTemplate.opsForValue().set("s2", num);
        Object s2 = customRedisTemplate.opsForValue().get("s2");
        System.out.println(s2);

        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("aa", "aaa");
        stringStringHashMap.put("aa1", "aaa");
        stringStringHashMap.put("aa2", "aaa");
        customRedisTemplate.opsForValue().set("s3", stringStringHashMap);
    }

    @Test
    void as() throws JsonProcessingException {
        Long as = 9L;
        String sad = "sad";
        System.out.println(JSONObject.toJSONString(as));
        System.out.println(JSONObject.toJSONString(sad).length());
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(objectMapper.writeValueAsString(sad));
    }

    @Test
    void name() {
        Long a = 2147483648L;
        System.out.println(a<<32);
        System.out.println(new Date(4398046511103L));
        System.out.println(System.currentTimeMillis());

    }

    @Test
    void testHyperLogLog() {
        String[] values = new String[5];
        int j = 0;
        for (int i = 0; i < 10; i++) {
            j = i % 5;
            values[j] = "user_" + i;
            if (j == 4) {
                // 发送到Redis
                customRedisTemplate.opsForHyperLogLog().add("hl5", values);
            }
        }
        // 统计数量
        Long count = customRedisTemplate.opsForHyperLogLog().size("hl5");
        System.out.println("count = " + count);
    }

    @Test
    void nam1e() {
        System.out.println("asd");
    }
}
