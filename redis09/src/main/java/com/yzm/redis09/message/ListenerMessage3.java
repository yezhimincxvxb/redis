package com.yzm.redis09.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;

/**
 * redis stream监听消息
 * 在消费完成后确认已消费
 */
@Slf4j
public class ListenerMessage3 implements StreamListener<String, MapRecord<String, String, String>> {

    private final String group;
    private final RedisTemplate<String,Object> redisTemplate;

    public ListenerMessage3(String group,RedisTemplate<String,Object> redisTemplate) {
        this.group = group;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        log.info("手动ack");
        // 接收到消息
        System.out.println("3 == > message id " + message.getId());
        System.out.println("3 == > stream " + message.getStream());
        System.out.println("3 == > body " + message.getValue());

        // 消费完成后确认消费（ACK）
        redisTemplate.opsForStream().acknowledge(message.getStream(),group, message.getId());
    }
}
