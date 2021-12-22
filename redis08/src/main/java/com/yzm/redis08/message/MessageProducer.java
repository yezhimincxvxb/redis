package com.yzm.redis08.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class MessageProducer {

    public static final String MESSAGE_KEY = "message:queue";
    public static final String MESSAGE_ZKEY = "message:zqueue";
    private final StringRedisTemplate redisTemplate;
    private final AtomicInteger ati = new AtomicInteger(1);

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

    public void zadd() {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                int increment = ati.getAndIncrement();
                log.info(Thread.currentThread().getName() + "：put message to zset = " + increment);
                double score = System.currentTimeMillis() + new Random().nextInt(60 * 1000);
                redisTemplate.opsForZSet().add(MESSAGE_ZKEY, Thread.currentThread().getName() + " hello zset：" + increment, score);
            }).start();
        }
    }

}
