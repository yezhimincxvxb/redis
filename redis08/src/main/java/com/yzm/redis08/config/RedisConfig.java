package com.yzm.redis08.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.Resource;

@Configuration
public class RedisConfig {

    @Resource(name = "myObjectMapper")
    private ObjectMapper objectMapper;

    /**
     * redisTemplate配置
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 配置连接工厂
        template.setConnectionFactory(factory);

        Jackson2JsonRedisSerializer<Object> jacksonSerializer = jackson2JsonRedisSerializer();
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // 使用StringRedisSerializer来序列化和反序列化redis的key,value采用json序列化
        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(jacksonSerializer);

        // 设置hash key 和value序列化模式
        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(jacksonSerializer);
        template.afterPropertiesSet();

        return template;
    }

    private Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer() {
        //使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值（默认使用JDK的序列化方式）
        Jackson2JsonRedisSerializer<Object> jacksonSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        jacksonSerializer.setObjectMapper(objectMapper);
        return jacksonSerializer;
    }

}

