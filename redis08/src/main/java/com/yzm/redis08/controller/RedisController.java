package com.yzm.redis08.controller;


import com.yzm.redis08.entity.User;
import com.yzm.redis08.message.MessageProducer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedisController {

    public final MessageProducer messageProducer;
    public final RedisTemplate<String, Object> redisTemplate;

    public RedisController(MessageProducer messageProducer, RedisTemplate<String, Object> redisTemplate) {
        this.messageProducer = messageProducer;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/lPush")
    public void lPush() {
        messageProducer.lPush();
    }


    @GetMapping("/publish")
    public void publish() {
        redisTemplate.convertAndSend("channel_first", "hello world");
    }

    @GetMapping("/publish2")
    public void publish2() {
        redisTemplate.convertAndSend("channel2", "hello world");
    }

    @GetMapping("/publish3")
    public void publish3() {
        User user = User.builder().id(1).username("yzm").build();
        redisTemplate.convertAndSend("user", user);
    }

    @GetMapping("/zadd")
    public void zadd() {
        messageProducer.zadd();
    }

}
