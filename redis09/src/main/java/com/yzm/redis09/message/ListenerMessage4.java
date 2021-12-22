package com.yzm.redis09.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamListener;

/**
 * redis stream监听消息
 * 在消费完成后确认已消费
 */
@Slf4j
public class ListenerMessage4 implements StreamListener<String, MapRecord<String, String, String>> {


    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        // 接收到消息
        System.out.println("4 == > message id " + message.getId());
        System.out.println("4 == > stream " + message.getStream());
        System.out.println("4 == > body " + message.getValue());
    }
}
