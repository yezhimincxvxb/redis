package com.yzm.master_slave.controller;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/redis")
public class RedisController {

    @Resource(name = "masterRedisTemplate")
    private RedisTemplate<String, Object> writeTemplate;
    @Resource(name = "slaveRedisTemplate")
    private RedisTemplate<String, Object> readTemplate;

    @GetMapping("/setValue")
    public void setValue() {
        writeTemplate.opsForValue().set("key:20210312", "value:20210312");
    }

    @GetMapping("/getValue")
    public void getValue() {
        Object value = writeTemplate.opsForValue().get("key:20210312");
        Object value2 = readTemplate.opsForValue().get("key:20210312");
        System.out.println("value = " + value);
        System.out.println("value2 = " + value2);
    }

}
