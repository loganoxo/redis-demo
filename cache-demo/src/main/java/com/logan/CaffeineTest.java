package com.logan;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.logan.entity.User;

import java.time.Duration;

/**
 * @author logan
 * @version 1.0
 * @date 2022/5/30
 * @description TODO
 */
public class CaffeineTest {
    public static void main(String[] args) {
        Cache<Long, User> cache = Caffeine.newBuilder()
                //初始大小
                .initialCapacity(100)
                //设置缓存大小上限为 10
                .maximumSize(10)
                // 设置缓存有效期为 10 秒，从最后一次写入开始计时
                .expireAfterWrite(Duration.ofSeconds(10))
                .build();

        //缓存中查不到，则通过第二个函数型参数获取数据，并存储缓存
        User userCache = cache.get(10L, id -> {
            User user = new User();
            user.setId(id);
            user.setName("asd");
            user.setAge(324);
            return user;
        });
        System.out.println("userCache = " + userCache);


        //直接从缓存中获取，没有则返回null
        User userCache2 = cache.getIfPresent(10L);
        System.out.println("userCache2 = " + userCache2);

    }
}
