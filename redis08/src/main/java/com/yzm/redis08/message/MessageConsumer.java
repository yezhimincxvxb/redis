package com.yzm.redis08.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@EnableScheduling
public class MessageConsumer {

    public static final String MESSAGE_KEY = "message:queue";
    private final StringRedisTemplate redisTemplate;

    public MessageConsumer(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

//    @Scheduled(initialDelay = 5 * 1000, fixedRate = 2 * 1000)
    public void rPop() {
        String message = redisTemplate.opsForList().rightPop(MESSAGE_KEY);
        log.info(message);
    }

//    @PostConstruct
    public void brPop() {
        new Thread(() -> {
            while (true) {
                String message = redisTemplate.opsForList().rightPop(MESSAGE_KEY, 10, TimeUnit.SECONDS);
                log.info(message);
            }
        }).start();
    }

}
