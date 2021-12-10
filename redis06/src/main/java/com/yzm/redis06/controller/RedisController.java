package com.yzm.redis06.controller;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedisController {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisController(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/set")
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @GetMapping("/get")
    public void get(String key) {
        System.out.println(redisTemplate.opsForValue().get(key));
    }

}
