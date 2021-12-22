package com.yzm.redis08.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageProducer {

    public static final String MESSAGE_KEY = "message:queue";
    private final StringRedisTemplate redisTemplate;

    public MessageProducer(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void lPush() {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                Long size = redisTemplate.opsForList().leftPush(MESSAGE_KEY, Thread.currentThread().getName() + "：hello world");
                log.info(Thread.currentThread().getName() + "：put message size = " + size);
            }).start();
        }
    }

}
