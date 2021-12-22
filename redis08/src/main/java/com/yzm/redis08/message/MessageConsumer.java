package com.yzm.redis08.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@EnableScheduling
public class MessageConsumer {

    public static final String MESSAGE_KEY = "message:queue";
    public static final String MESSAGE_ZKEY = "message:zqueue";
    private final StringRedisTemplate redisTemplate;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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

    //    @Scheduled(initialDelay = 5 * 1000, fixedRate = 1000)
    public void rangeByScore() {
        log.info("延时队列消费。。。");
        // 拉取score小于当前时间戳的消息
        Set<String> messages = redisTemplate.opsForZSet().rangeByScore(MESSAGE_ZKEY, 0, System.currentTimeMillis());
        if (messages != null) {
            for (String message : messages) {
                Double score = redisTemplate.opsForZSet().score(MESSAGE_ZKEY, message);
                log.info("消费了：" + message + "消费时间为：" + simpleDateFormat.format(score));
                redisTemplate.opsForZSet().remove(MESSAGE_ZKEY, message);
            }
        }
    }

}
