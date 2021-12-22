package com.yzm.redis08.message;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

public class MySubscribe implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] bytes) {
        System.out.println("订阅频道:" + new String(message.getChannel()));
        System.out.println("接收数据:" + new String(message.getBody()));
    }
}