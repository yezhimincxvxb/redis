package com.yzm.redis03.controller;


import com.yzm.redis03.utils.RedisTransactionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedisController {

    private final RedisTransactionUtils transactionUtils;
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisController(RedisTransactionUtils transactionUtils, RedisTemplate<String, Object> redisTemplate) {
        this.transactionUtils = transactionUtils;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/test01")
    public void test01() {
        transactionUtils.test01();
    }

    @GetMapping("/test02")
    public void test02() {
        transactionUtils.test02();
    }

    @GetMapping("/test03")
    public void test03() {
        transactionUtils.test03();
    }

    @GetMapping("/test04")
    public void test04() {
        transactionUtils.test04();
    }

    @GetMapping("/test05")
    public void test05() {
        transactionUtils.test05();
    }

    @GetMapping("/test06")
    public void test06() {
        transactionUtils.test06();
    }

    @GetMapping("/test07")
    public void test07() {
        transactionUtils.test07();
    }

    @GetMapping("/test071")
    public void test071() {
        redisTemplate.opsForValue().set("watch3", "no");
    }

}
