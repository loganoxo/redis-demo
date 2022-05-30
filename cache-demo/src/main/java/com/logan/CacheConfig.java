package com.logan;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.assertj.core.util.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author logan
 * @version 1.0
 * @date 2022/5/30
 */
@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {

    @Autowired
    private LettuceConnectionFactory lettuceConnectionFactory;

    @Override
    public CacheResolver cacheResolver() {
        List<CacheManager> list = new ArrayList<>();
        // 优先读取堆内存缓存
        list.add(caffeineCacheManager());
        // 堆内存缓存读取不到该key时再读取redis缓存
        list.add(redisCacheManager());
        return new CustomCacheResolver(list);
    }

    public CacheManager caffeineCacheManager() {
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder().initialCapacity(10) //初始大小
                .maximumSize(11)  //最大大小
                .expireAfterWrite(1, TimeUnit.HOURS); //写入/更新之后1小时过期

        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setAllowNullValues(true);
        caffeineCacheManager.setCaffeine(caffeine);

        caffeine = Caffeine.newBuilder().initialCapacity(11) //初始大小
                .maximumSize(11)  //最大大小
                .expireAfterWrite(1, TimeUnit.HOURS);
        caffeineCacheManager.registerCustomCache("User", caffeine.build());
        caffeine = Caffeine.newBuilder().initialCapacity(12) //初始大小
                .maximumSize(11)  //最大大小
                .expireAfterWrite(1, TimeUnit.HOURS);
        caffeineCacheManager.registerCustomCache("Account", caffeine.build());
        return caffeineCacheManager;
    }

    @Bean
    public CacheManager redisCacheManager() {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        RedisSerializationContext.SerializationPair<String> keySerializer = RedisSerializationContext.SerializationPair
                .fromSerializer(RedisSerializer.string());
        RedisSerializationContext.SerializationPair<Object> valueSerializer = RedisSerializationContext.SerializationPair
                .fromSerializer(new GenericFastJsonRedisSerializer());
        config.serializeKeysWith(keySerializer);
        config.serializeValuesWith(valueSerializer);
        config.computePrefixWith(cacheName -> cacheName + ":");//默认是双冒号
        config.entryTtl(Duration.ofDays(20));// 设置缓存的默认过期时间，也是使用Duration设置

        // 设置一个初始化的缓存空间set集合
        Set<String> cacheNames = Sets.newHashSet();
        cacheNames.add("User");
        cacheNames.add("Account");
        cacheNames.add("Order");

        // 对每个缓存空间应用不同的配置
        Map<String, RedisCacheConfiguration> configMap = new HashMap<>(16);
        configMap.put("User", RedisCacheConfiguration.defaultCacheConfig()
                .computePrefixWith(cacheName -> cacheName + ":")
                .serializeKeysWith(keySerializer)
                .serializeValuesWith(valueSerializer)
                .entryTtl(Duration.ofDays(20)));
        configMap.put("Account", RedisCacheConfiguration.defaultCacheConfig()
                .computePrefixWith(cacheName -> cacheName + ":")
                .serializeKeysWith(keySerializer)
                .serializeValuesWith(valueSerializer)
                .entryTtl(Duration.ofDays(20)));
        configMap.put("Order", RedisCacheConfiguration.defaultCacheConfig()
                .computePrefixWith(cacheName -> cacheName + ":")
                .serializeKeysWith(keySerializer)
                .serializeValuesWith(valueSerializer)
                .entryTtl(Duration.ofDays(20)));

        // 使用自定义的缓存配置初始化一个cacheManager
        return RedisCacheManager.builder(createConnectionFactory(lettuceConnectionFactory, 2))
                .cacheDefaults(config)
                .initialCacheNames(cacheNames)  // 注意这两句的调用顺序，一定要先调用该方法设置初始化的缓存名，再初始化相关的配置
                .withInitialCacheConfigurations(configMap)
                .build();
    }

    private LettuceConnectionFactory createConnectionFactory(LettuceConnectionFactory lettuceConnectionFactory, int dbIndex) {
        LettuceClientConfiguration clientConfiguration = lettuceConnectionFactory.getClientConfiguration();
        RedisStandaloneConfiguration standaloneConfiguration = lettuceConnectionFactory.getStandaloneConfiguration();
        RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration(standaloneConfiguration.getHostName(), standaloneConfiguration.getPort());
        serverConfig.setDatabase(dbIndex);
        serverConfig.setUsername(standaloneConfiguration.getUsername());
        serverConfig.setPassword(standaloneConfiguration.getPassword());
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(serverConfig, clientConfiguration);
        connectionFactory.afterPropertiesSet();
        return connectionFactory;
    }
}
