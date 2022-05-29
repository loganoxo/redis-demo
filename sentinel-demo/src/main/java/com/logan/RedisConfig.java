package com.logan;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import io.lettuce.core.ReadFrom;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.Objects;

/**
 * @author logan
 * @version 1.0
 * @date 2022/5/24
 */
@Configuration
public class RedisConfig {

    /**
     * 配置redis集群的读写分离
     */
    @Bean
    public LettuceClientConfigurationBuilderCustomizer clientConfigurationBuilderCustomizer() {
        return clientConfigurationBuilder -> clientConfigurationBuilder.readFrom(ReadFrom.REPLICA_PREFERRED);
    }

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
        LettuceConnectionFactory connectionFactory = null;
        RedisStandaloneConfiguration standaloneConfiguration = lettuceConnectionFactory.getStandaloneConfiguration();
        RedisSentinelConfiguration sentinelConfiguration = lettuceConnectionFactory.getSentinelConfiguration();
        LettuceClientConfiguration clientConfiguration = lettuceConnectionFactory.getClientConfiguration();
        if (sentinelConfiguration == null) {
            RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration(standaloneConfiguration.getHostName(), standaloneConfiguration.getPort());
            serverConfig.setDatabase(dbIndex);
            serverConfig.setUsername(standaloneConfiguration.getUsername());
            serverConfig.setPassword(standaloneConfiguration.getPassword());
            connectionFactory = new LettuceConnectionFactory(serverConfig, clientConfiguration);
            connectionFactory.afterPropertiesSet();
        } else {
            RedisSentinelConfiguration serverConfig = new RedisSentinelConfiguration();
            serverConfig.setSentinels(sentinelConfiguration.getSentinels());
            serverConfig.setMaster(Objects.requireNonNull(sentinelConfiguration.getMaster()));
            serverConfig.setUsername(sentinelConfiguration.getUsername());
            serverConfig.setPassword(sentinelConfiguration.getPassword());
            serverConfig.setSentinelPassword(sentinelConfiguration.getSentinelPassword());
            serverConfig.setDatabase(dbIndex);
            connectionFactory = new LettuceConnectionFactory(serverConfig, clientConfiguration);
            connectionFactory.afterPropertiesSet();
        }
        return connectionFactory;
    }

}
