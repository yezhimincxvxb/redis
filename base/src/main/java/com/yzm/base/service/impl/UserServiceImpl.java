package com.yzm.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yzm.base.entity.User;
import com.yzm.base.mapper.UserMapper;
import com.yzm.base.service.UserService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author Yzm
 * @since 2021-02-25
 */
@Service
@CacheConfig(cacheNames = "users")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    @CachePut(key = "#result.id", condition = "#result.id gt 0")
    public User saveUser(User user) {
        if (baseMapper.insert(user) > 0) {
            return user;
        }
        return null;
    }

    @Override
    @CachePut(key = "#user.id", condition = "#result ne null")
    public User updateUser(User user) {
        if (baseMapper.updateById(user) > 0) {
            return user;
        }
        return null;
    }

    @Override
    @CacheEvict(key = "#id", condition = "#result gt 0")
    public int deleteUser(Integer id) {
        return baseMapper.deleteById(id);
    }

    @Override
    @CacheEvict(allEntries = true)
    public void deleteAllCache() {

    }

    @Override
    @Cacheable(key = "#id",condition = "#result ne null")
    public User getUserById(Integer id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Cacheable(key = "#root.methodName")
    public List<User> selectAll() {
        List<User> users = baseMapper.selectList(null);
        users.forEach(System.out::println);
        return users;
    }
}
