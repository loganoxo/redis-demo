package com.logan.jedis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author logan
 * @version 1.0
 * @date 2022/5/24
 * @description TODO
 */
public class JedisTest {
    private Jedis jedis;

    @BeforeEach
    public void setUp() {
        // 建立连接
        jedis = new Jedis("localhost", 6379);
        // 设置密码
        jedis.auth("123456");
        // 选择库
        jedis.select(0);
    }

    /**
     * 测试线程安全1
     * redis.clients.jedis.Connection.connect() socket对象是共享变量，导致线程安全问题
     */
    @Test
    public void testThreadSecurity1() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(20);
        for (int i = 0; i < 20; i++) {
            pool.execute(() -> jedis.set("test", "hello"));
//            Thread.sleep(200);
        }
    }

    /**
     * 测试线程安全2
     * 多线程通过RedisInputStream和RedisOutputStream读写缓冲区的时候引起的问题造成的数据问题不满足RESP协议引起的
     */
    @Test
    public void testThreadSecurity2() throws InterruptedException {
        jedis.connect();
        ExecutorService pool = Executors.newFixedThreadPool(20);
        for (int i = 0; i < 20; i++) {
            pool.execute(() -> jedis.set("test", "hello"));
//            Thread.sleep(200);
        }
    }

    /**
     * 测试线程安全3
     * 使用JedisPool，每个线程用不同的jedis实例去规避线程安全问题
     */
    @Test
    public void testThreadSecurity3() {
        JedisPoolConfig config = new JedisPoolConfig();
        //最大连接
        config.setMaxTotal(2);
        //最大空闲连接
        config.setMaxIdle(2);
        //最小空闲连接
        config.setMinIdle(0);
        //当池中没有可用连接时，最长等待时间 ms  超时报错
        config.setMaxWait(Duration.ofMillis(10));
        JedisPool jPool = new JedisPool(config, "localhost", 6379, 1000, "123456");
        System.out.println(jPool.getNumActive());
        System.out.println(jPool.getNumIdle());
        System.out.println(jPool.getNumWaiters());
        ExecutorService pool = Executors.newFixedThreadPool(1000);
        for (int i = 0; i < 1000; i++) {
            int finalI = i;
            pool.execute(() -> {
                System.out.println("Active:" + jPool.getNumActive());
                System.out.println("Idle:" + jPool.getNumIdle());
                System.out.println("Waiters:" + jPool.getNumWaiters());
                Jedis resource = jPool.getResource();
                resource.lpush("list", finalI + "");
                resource.close();
            });
        }
    }

}
