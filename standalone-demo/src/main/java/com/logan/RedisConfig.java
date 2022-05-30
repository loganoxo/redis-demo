package com.logan;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @author logan
 * @version 1.0
 * @date 2022/5/24
 * @description TODO
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate customRedisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        //创建新的连接工厂
        LettuceConnectionFactory connectionFactory = createConnectionFactory(lettuceConnectionFactory, 2);
        //创建新的redisTemplate
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        //设置key序列化
        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        //设置value序列化
        redisTemplate.setValueSerializer(new GenericFastJsonRedisSerializer());
//        redisTemplate.setValueSerializer(RedisSerializer.json());
        redisTemplate.setHashValueSerializer(RedisSerializer.json());
        //设置连接工厂
        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;
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
