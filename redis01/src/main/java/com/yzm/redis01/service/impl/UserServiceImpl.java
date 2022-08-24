package com.yzm.redis01.service.impl;

import com.yzm.redis01.entity.User;
import com.yzm.redis01.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@CacheConfig(cacheNames = "users")
public class UserServiceImpl implements UserService {


    private static final Map<Integer, User> userMap;

    static {
        userMap = new HashMap<>();
        userMap.put(userMap.size() + 1, User.builder()
                .id(userMap.size() + 1).username("root").password("root").createDate(new Date()).updateDate(LocalDateTime.now()).build());
        userMap.put(userMap.size() + 1, User.builder()
                .id(userMap.size() + 1).username("admin").password("admin").createDate(new Date()).updateDate(LocalDateTime.now()).build());
    }

    @Override
    @CachePut(key = "#result.id", condition = "#result.id gt 0")
    public User saveUser(User user) {
        log.info("保存数据");
        int id = userMap.size() + 1;
        User build = User.builder()
                .id(id)
                .username(user.getUsername())
                .password(user.getPassword())
                .createDate(new Date())
                .updateDate(LocalDateTime.now())
                .build();
        userMap.put(id, build);
        return build;
    }

    @Override
    @CachePut(key = "#user.id", unless = "#result ne 1")
    public int updateUser(User user) {
        log.info("更新数据");
        if (userMap.containsKey(user.getId())) {
            User update = userMap.get(user.getId());
            update.setUsername(user.getUsername())
                    .setPassword(user.getPassword())
                    .setUpdateDate(LocalDateTime.now());
            userMap.replace(user.getId(), update);
            return 1;
        }
        return 0;
    }

    @Override
    @CacheEvict(key = "#id", condition = "#result eq 1")
    public int deleteUser(Integer id) {
        log.info("删除数据");
        if (userMap.containsKey(id)) {
            userMap.remove(id);
            return 1;
        }
        return 0;
    }

    @Override
    @CacheEvict(allEntries = true)
    public void deleteAllCache() {
        log.info("清空缓存");
    }

    @Override
    @Cacheable(key = "#id", condition = "#id ge 1") //
    public User getUserById(Integer id) {
        log.info("查询用户");
        return userMap.get(id);
    }

    @Override
    @Cacheable(key = "#root.methodName")
    public List<User> selectAll() {
        log.info("查询所有");
        return new ArrayList<>(userMap.values());
    }

    @Override
    @Cacheable(keyGenerator = "myKeyGenerator")
    public List<User> findAll(Object... params) {
        log.info("查询所有");
        return new ArrayList<>(userMap.values());
    }

}
