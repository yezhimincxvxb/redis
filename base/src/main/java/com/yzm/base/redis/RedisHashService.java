package com.yzm.base.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class RedisHashService {

    @Autowired
    private HashOperations<String, String, Object> hashOperations;

    //========================添加==========================
    //存储元素
    public void put(String k, String hk, Object hv) {
        hashOperations.put(k, hk, hv);
    }

    public void putAll(String k, Map<String, Object> map) {
        hashOperations.putAll(k, map);
    }

    //hk不存在才会插入元素
    public Boolean putIfAbsent(String k, String hk, Object hv) {
        return hashOperations.putIfAbsent(k, hk, hv);
    }

    //========================获取==========================
    //获取元素
    public Object get(String k, String hk) {
        return hashOperations.get(k, hk);
    }

    public List<Object> multiGet(String k, Collection<String> hk) {
        return hashOperations.multiGet(k, hk);
    }

    public Set<String> keys(String k) {
        return hashOperations.keys(k);
    }

    public List<Object> values(String k) {
        return hashOperations.values(k);
    }

    public Map<String, Object> entries(String k) {
        return hashOperations.entries(k);
    }

    public Long size(String k) {
        return hashOperations.size(k);
    }

    //========================修改==========================
    public Long increment(String k, String hk, long inc) {
        return hashOperations.increment(k, hk, inc);
    }

    public Double increment(String k, String hk, double inc) {
        return hashOperations.increment(k, hk, inc);
    }

    //========================删除==========================
    public Long delete(String k, Object... hvs) {
        return hashOperations.delete(k, hvs);
    }

    //========================判断==========================
    public Boolean hasKey(String k, Object hv) {
        return hashOperations.hasKey(k, hv);
    }

}
