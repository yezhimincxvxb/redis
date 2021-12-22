package com.yzm.redis08.message;

public class MySubscribe2 {

    public void getMessage(Object message, String channel) {
        System.out.println("订阅频道2:" + channel);
        System.out.println("接收数据2:" + message);
    }
}