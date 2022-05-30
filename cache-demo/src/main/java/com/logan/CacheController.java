package com.logan;

import com.logan.entity.User;
import com.logan.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author logan
 * @version 1.0
 * @date 2022/5/24
 */
@RestController
public class CacheController {

    private final UserService userService;

    public CacheController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/get")
    public User getById(Long id) {
        User byId = userService.getById(id);
        System.out.println("byId = " + byId);
        return byId;
    }

    @GetMapping("/oo")
    public User oo(Long id) {
        User byId = userService.oo(id);
        System.out.println("byId = " + byId);
        return byId;
    }

    @GetMapping("/save")
    public void save(Long id) {
        User user = new User();
        user.setId(id);
        userService.save(user);
    }


}
