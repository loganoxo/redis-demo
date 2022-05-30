package com.logan.mapper;

import com.logan.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Random;
import java.util.UUID;

/**
 * @author logan
 * @version 1.0
 * @date 2022/5/30
 * @description 模拟数据库操作
 */
@Repository
public class UserMapper {
    public User get(Long id) {
        System.out.println("数据库查询");
        User user = new User();
        user.setId(id);
        user.setName(UUID.randomUUID().toString());
        user.setAge(new Random().nextInt());
        return user;
    }

    public void save(User user) {
        System.out.println("数据库保存");
    }
}
