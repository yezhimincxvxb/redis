package com.yzm.redis02.utils;

import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class RedisStringUtils {

    private final ValueOperations<String, Object> valueOperations;

    public RedisStringUtils(ValueOperations<String, Object> valueOperations) {
        this.valueOperations = valueOperations;
    }

    //========================添加==========================
    //存储key=value键值对
    public void set(String key, Object value) {
        valueOperations.set(key, value);
    }
    //存储key=value键值对并设置过期时间
    public void set(String key, Object value, long time, TimeUnit timeUnit) {
        valueOperations.set(key, value, time, timeUnit);
    }

    public void set(String key, Object value, Duration duration) {
        valueOperations.set(key, value, duration);
    }

    //key不存在才存储，存在不操作
    public Boolean setIfAbsent(String key, Object value) {
        return valueOperations.setIfAbsent(key, value);
    }

    public Boolean setIfAbsent(String key, Object value, long time, TimeUnit timeUnit) {
        return valueOperations.setIfAbsent(key, value, time, timeUnit);
    }

    public Boolean setIfAbsent(String key, Object value, Duration duration) {
        return valueOperations.setIfAbsent(key, value, duration);
    }

    //key存在才存储，不存在不操作
    public Boolean setIfPresent(String key, Object value) {
        return valueOperations.setIfPresent(key, value);
    }

    public Boolean setIfPresent(String key, Object value, long time, TimeUnit timeUnit) {
        return valueOperations.setIfPresent(key, value, time, timeUnit);
    }

    public Boolean setIfPresent(String key, Object value, Duration duration) {
        return valueOperations.setIfPresent(key, value, duration);
    }

    //设置新值并返回旧值
    public Object getAndSet(String key, Object newValue) {
        return valueOperations.getAndSet(key, newValue);
    }

    //========================获取==========================
    //批量存储
    public void multiSet(Map<String, Object> map) {
        valueOperations.multiSet(map);
    }

    public Boolean multiSetIfAbsent(Map<String, Object> map) {
        return valueOperations.multiSetIfAbsent(map);
    }

    //根据key获取value
    public Object get(String key) {
        return valueOperations.get(key);
    }

    public List<Object> multiGet(Collection<String> keys) {
        return valueOperations.multiGet(keys);
    }

    //========================修改==========================
    //value值自增(+1)
    public Long increment(String key) {
        return valueOperations.increment(key);
    }

    //value值自增(incValue)
    public Long increment(String key, long incValue) {
        return valueOperations.increment(key, incValue);
    }

    public Double increment(String key, double incValue) {
        return valueOperations.increment(key, incValue);
    }
}
