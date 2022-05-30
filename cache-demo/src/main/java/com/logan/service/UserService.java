package com.logan.service;

import com.logan.entity.User;
import com.logan.mapper.UserMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author logan
 * @version 1.0
 * @date 2022/5/30
 * @description 模拟数据库查询
 */
@Service
public class UserService {

    private final UserMapper userMapper;


    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }


    /*
     * @Cacheable 执行前都会检查Cache中是否存在相同key的缓存元素，如果存在就不再执行该方法，而是直接从缓存中获取结果进行返回，否则才会执行并将返回结果存入指定的缓存中
     * @CachePut 每次都会执行userMapper.get()，并将执行结果以键值对的形式存入指定的缓存中。
     * @CacheEvict 清除缓存元素
     */
    @Cacheable(value = {"User"}, key = "#id", condition = "#id%2==0")
    public User getById(Long id) {
        return userMapper.get(id);
    }

    @CacheEvict(value = "User", beforeInvocation = false, key = "#user.id", condition = "#user.id%2==0")
    public void save(User user) {
        userMapper.save(user);
    }

    @Cacheable(value = {"b"}, key = "#id", condition = "#id%2==0")
    public User oo(Long id) {
        return userMapper.get(id);
    }
}
