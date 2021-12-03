package com.yzm.redis01.service;

import com.yzm.redis01.entity.User;

import java.util.List;

public interface UserService {

    User saveUser(User user);

    User updateUser(User user);

    int deleteUser(Integer id);

    void deleteAllCache();

    User getUserById(Integer id);

    List<User> selectAll();

    List<User> findAll(Object... params);

}
