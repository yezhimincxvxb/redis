package com.yzm.redis11.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    /**
     * 单机模式
     */
    @Bean(name = "singleServer", destroyMethod = "shutdown")
    public RedissonClient useSingleServer() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://192.168.8.128:6379")
                .setPassword("1234")
                .setDatabase(0);
        return Redisson.create(config);
    }


}
