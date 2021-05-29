package com.yzm.master_slave.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class SlaveConf extends RedisConf {

    @Bean(name = "slaveProperties")
    @ConfigurationProperties(prefix = "spring.redis.slave")
    public RedisProperties getBaseDBProperties() {
        return new RedisProperties();
    }

    @Bean(name = "slaveFactory")
    @Override
    public RedisConnectionFactory createRedisConnFactory(@Qualifier("slaveProperties") RedisProperties redisProperties) {
        return super.createRedisConnFactory(redisProperties);
    }

    @Bean(name = "slaveRedisTemplate")
    @Override
    public RedisTemplate<String, Object> buildRedisTemplate(@Qualifier("slaveFactory") RedisConnectionFactory redisConnectionFactory) {
        return super.buildRedisTemplate(redisConnectionFactory);
    }

}
