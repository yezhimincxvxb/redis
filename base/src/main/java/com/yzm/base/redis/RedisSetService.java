package com.yzm.base.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Component
public class RedisSetService {

    @Autowired
    private SetOperations<String, Object> setOperations;

    //========================添加==========================
    //添加元素，返回成功添加个数
    public Long add(String key, Object... vars) {
        return setOperations.add(key, vars);
    }

    //========================获取==========================
    //获取元素
    public Set<Object> members(String key) {
        return setOperations.members(key);
    }

    //随机获取一个元素
    public Object randomMember(String key) {
        return setOperations.randomMember(key);
    }

    //随机获取count个数量的元素(元素会重复)
    public List<Object> randomMembers(String key, long count) {
        return setOperations.randomMembers(key, count);
    }

    //随机获取count个数量的元素(元素不重复)
    public Set<Object> distinctRandomMembers(String key, long count) {
        return setOperations.distinctRandomMembers(key, count);
    }

    //set集合弹出一个元素，集合元素个数减一
    public Object pop(String key) {
        return setOperations.pop(key);
    }

    public List<Object> pop(String key, long count) {
        return setOperations.pop(key, count);
    }

    //集合大小
    public Long size(String key) {
        return setOperations.size(key);
    }

    //========================移除==========================
    //移除元素，返回成功移除个数
    public Long remove(String key, Object... vars) {
        return setOperations.remove(key, vars);
    }

    //转移
    public Boolean move(String key1, Object var, String key2) {
        return setOperations.move(key1, var, key2);
    }

    //========================判断==========================
    //判断set集合是否有var元素
    public Boolean isMember(String key, Object var) {
        return setOperations.isMember(key, var);
    }

    //========================合集==========================
    //把所有集合的元素加在一起，然后去重
    public Set<Object> union(String key1, String key2) {
        return setOperations.union(key1, key2);
    }

    public Set<Object> union(String key1, Collection<String> keys) {
        return setOperations.union(key1, keys);
    }

    public Set<Object> union(Collection<String> keys) {
        return setOperations.union(keys);
    }

    public Long unionAndStore(String key1, String key2, String dest) {
        return setOperations.unionAndStore(key1, key2, dest);
    }

    public Long unionAndStore(String key1, Collection<String> keys, String dest) {
        return setOperations.unionAndStore(key1, keys, dest);
    }

    public Long unionAndStore(Collection<String> keys, String dest) {
        return setOperations.unionAndStore(keys, dest);
    }

    //========================交集==========================
    //所有集合都共有的元素
    public Set<Object> intersect(String key1, String key2) {
        return setOperations.intersect(key1, key2);
    }

    public Set<Object> intersect(String key1, Collection<String> keys) {
        return setOperations.intersect(key1, keys);
    }

    public Set<Object> intersect(Collection<String> keys) {
        return setOperations.intersect(keys);
    }

    public Long intersectAndStore(String key1, String key2, String dest) {
        return setOperations.intersectAndStore(key1, key2, dest);
    }

    public Long intersectAndStore(String key1, Collection<String> keys, String dest) {
        return setOperations.intersectAndStore(key1, keys, dest);
    }

    public Long intersectAndStore(Collection<String> keys, String dest) {
        return setOperations.intersectAndStore(keys, dest);
    }


    //========================差集==========================
    //以第一个集合为准，去除与其他集合共同的元素，最后只留下自身独有的元素
    public Set<Object> difference(String key1, String key2) {
        return setOperations.difference(key1, key2);
    }

    public Set<Object> difference(String key1, Collection<String> keys) {
        return setOperations.difference(key1, keys);
    }

    public Set<Object> difference(Collection<String> keys) {
        return setOperations.difference(keys);
    }

    public Long differenceAndStore(String key1, String key2, String dest) {
        return setOperations.differenceAndStore(key1, key2, dest);
    }

    public Long differenceAndStore(String key1, Collection<String> keys, String dest) {
        return setOperations.differenceAndStore(key1, keys, dest);
    }

    public Long differenceAndStore(Collection<String> keys, String dest) {
        return setOperations.differenceAndStore(keys, dest);
    }

}
