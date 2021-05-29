package com.yzm.sentinel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis")
public class RedisController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/set")
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @GetMapping("/get")
    public void get(String key) {
        System.out.println(redisTemplate.opsForValue().get(key));
    }

}
