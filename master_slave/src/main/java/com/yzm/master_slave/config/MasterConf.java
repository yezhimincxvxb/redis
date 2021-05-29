package com.yzm.master_slave.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class MasterConf extends RedisConf {

    @Bean(name = "masterProperties")
    @ConfigurationProperties(prefix = "spring.redis.master")
    public RedisProperties getBaseDBProperties() {
        return new RedisProperties();
    }

    @Primary
    @Bean(name = "masterFactory")
    @Override
    public RedisConnectionFactory createRedisConnFactory(@Qualifier("masterProperties") RedisProperties redisProperties) {
        return super.createRedisConnFactory(redisProperties);
    }

    @Bean(name = "masterRedisTemplate")
    @Override
    public RedisTemplate<String, Object> buildRedisTemplate(@Qualifier("masterFactory") RedisConnectionFactory redisConnectionFactory) {
        return super.buildRedisTemplate(redisConnectionFactory);
    }

}
