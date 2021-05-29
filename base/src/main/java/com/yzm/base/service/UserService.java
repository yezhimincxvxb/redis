package com.yzm.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yzm.base.entity.User;

import java.util.List;

/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author Yzm
 * @since 2021-02-25
 */
public interface UserService extends IService<User> {

    User saveUser(User user);

    User updateUser(User user);

    int deleteUser(Integer id);

    void deleteAllCache();

    User getUserById(Integer id);

    List<User> selectAll();

}
