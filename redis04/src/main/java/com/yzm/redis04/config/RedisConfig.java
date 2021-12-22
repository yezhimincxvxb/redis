package com.yzm.redis04.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Data
@Configuration
@ConditionalOnProperty(prefix = "redis", name = "enable", havingValue = "true", matchIfMissing = true)
@ConfigurationProperties(prefix = "redis")
public class RedisConfig {

    private String host;
    private int port;
    private int timeout;
    private String password;
    private int maxTotal;
    private int maxIdle;
    private int maxWaitMillis;

    @Bean(name = "jedPoolConf")
    @Lazy
    public JedisPoolConfig jedPoolConf() {
        JedisPoolConfig config = new JedisPoolConfig();
        // 最大连接数, 默认8个
        config.setMaxTotal(maxTotal);
        // 最大空闲连接数, 默认8个
        config.setMaxIdle(maxIdle);
        // 获取连接时的最大等待毫秒数，超时就会报JedisConnectionException
        config.setMaxWaitMillis(maxWaitMillis);
        // 获取连接时，检查有效性, 默认false
        config.setTestOnBorrow(true);
        // 返还连接时，检查有效性, 默认false
        config.setTestOnReturn(true);
        return config;
    }

    @Bean(name = "jedPool")
    @Lazy
    public JedisPool jedPool(@Qualifier("jedPoolConf") JedisPoolConfig config) {

        return new JedisPool(config, host, port, timeout, StringUtils.hasLength(password) ? password : null);
    }

}
