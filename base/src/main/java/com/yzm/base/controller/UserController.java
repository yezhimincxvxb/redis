package com.yzm.base.controller;


import com.yzm.base.entity.User;
import com.yzm.base.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 用户信息表 前端控制器
 * </p>
 *
 * @author Yzm
 * @since 2021-02-25
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/saveUser")
    public void saveUser() {
        User user = new User();
        user.setUsername("aaa");
        user.setRealName("aaa");
        user.setPassword("aaa");
        System.out.println(userService.saveUser(user));
    }

    @GetMapping("/updateUser")
    public void updateUser() {
        User user = new User();
        user.setId(12);
        user.setUsername("bbb");
        user.setRealName("bbb");
        user.setPassword("bbb");
        System.out.println(userService.updateUser(user));
    }

    @GetMapping("/deleteUser")
    public void deleteUser(@RequestParam Integer id) {
        System.out.println(userService.deleteUser(id));
    }

    @GetMapping("/deleteAllCache")
    public void deleteAllCache() {
        userService.deleteAllCache();
    }

    @GetMapping("/getUserById")
    public void getUserById(@RequestParam Integer id) {
        System.out.println(userService.getUserById(id));
    }

    @GetMapping("/selectAll")
    public void selectAll() {
        List<User> users = userService.selectAll();
        users.forEach(System.out::println);
    }

}
