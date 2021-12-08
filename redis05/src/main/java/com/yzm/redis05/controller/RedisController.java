package com.yzm.redis05.controller;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class RedisController {

    @Resource(name = "masterRedisTemplate")
    private RedisTemplate<String, Object> writeTemplate;
    @Resource(name = "slaveRedisTemplate")
    private RedisTemplate<String, Object> readTemplate;
    @Resource(name = "slave2RedisTemplate")
    private RedisTemplate<String, Object> read2Template;

    @GetMapping("/set")
    public String set(String key, String value) {
        writeTemplate.opsForValue().set(key, value);
        return "success";
    }

    @GetMapping("/get")
    public String get(String key) {
        System.out.println("readTemplate = " + readTemplate.opsForValue().get(key));
        System.out.println("read2Template = " + read2Template.opsForValue().get(key));
        return "success";
    }

}
