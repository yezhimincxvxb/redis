package com.yzm.redis08.message;

import com.yzm.redis08.config.ObjectMapperConfig;
import com.yzm.redis08.entity.User;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

public class MySubscribe3 implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] bytes) {
        Jackson2JsonRedisSerializer<User> jacksonSerializer = new Jackson2JsonRedisSerializer<>(User.class);
        jacksonSerializer.setObjectMapper(ObjectMapperConfig.objectMapper);
        User user = jacksonSerializer.deserialize(message.getBody());

        System.out.println("订阅频道3:" + new String(message.getChannel()));
        System.out.println("接收数据3:" + user);
    }
}