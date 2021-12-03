package com.yzm.redis01.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKey;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * key生成器
 */
@Slf4j
public class MyKeyGenerator implements KeyGenerator {

    private static final String NO_PARAM = "[]";
    private static final String NULL_PARAM = "_";

    @Override
    public Object generate(Object target, Method method, Object... params) {
        StringBuilder key = new StringBuilder();
        key.append(target.getClass().getSimpleName()).append(".").append(method.getName()).append(":");

        if (params.length == 0) {
            return new SimpleKey(key.append(NO_PARAM).toString());
        }

        return new SimpleKey(key.append(Arrays.toString(params).replace("null", NULL_PARAM)).toString());
    }

}
