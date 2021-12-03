package com.yzm.redis01.controller;


import com.yzm.redis01.entity.User;
import com.yzm.redis01.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/saveUser")
    public void saveUser() {
        User user = new User();
        user.setUsername("yzm");
        user.setPassword("yzm");
        System.out.println(userService.saveUser(user));
    }

    @GetMapping("/updateUser")
    public void updateUser(Integer id) {
        User user = new User();
        user.setId(id);
        user.setUsername("yzm");
        user.setPassword("123");
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

    @GetMapping("/findAll")
    public void findAll(@RequestParam(required = false) Integer id, @RequestParam(required = false) String username) {
        List<User> users = userService.findAll(id, username);
        users.forEach(System.out::println);
    }

}
