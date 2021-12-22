package com.yzm.redis09.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamListener;

/**
 * redis stream监听消息
 */
@Slf4j
public class ListenerMessage2 implements StreamListener<String, MapRecord<String, String, String>> {

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        log.info("监听");
        System.out.println("2 ==> message id " + message.getId());
        System.out.println("2 ==> stream " + message.getStream());
        System.out.println("2 ==> body " + message.getValue());
    }

}
