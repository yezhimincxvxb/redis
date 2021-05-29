package com.yzm.master_slave.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

public class RedisConf {

    public RedisConnectionFactory createRedisConnFactory(RedisProperties redisProperties) {
        RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration();
        standaloneConfiguration.setHostName(redisProperties.getHost());
        standaloneConfiguration.setPort(redisProperties.getPort());
        standaloneConfiguration.setDatabase(redisProperties.getDatabase());
        standaloneConfiguration.setPassword(redisProperties.getPassword());

        return lettuceConnectionFactory(redisProperties, standaloneConfiguration);
    }

    //Lettuce
    private LettuceConnectionFactory lettuceConnectionFactory(RedisProperties redisProperties, RedisStandaloneConfiguration standaloneConfiguration) {
        LettuceClientConfiguration build = LettucePoolingClientConfiguration.builder().
                commandTimeout(Duration.ofMillis(redisProperties.getTimeout()))
                .poolConfig(createPoolConfig(redisProperties, new GenericObjectPoolConfig()))
                .build();

        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(standaloneConfiguration, build);
        lettuceConnectionFactory.afterPropertiesSet();
        return lettuceConnectionFactory;
    }

    //Jedis
    private JedisConnectionFactory jedisConnectionFactory(RedisProperties redisProperties, RedisStandaloneConfiguration standaloneConfiguration) {
        JedisClientConfiguration build = JedisClientConfiguration.builder()
                .connectTimeout(Duration.ofMillis(redisProperties.getTimeout()))
                .usePooling()
                .poolConfig(createPoolConfig(redisProperties, new JedisPoolConfig()))
                .build();

        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(standaloneConfiguration, build);
        jedisConnectionFactory.afterPropertiesSet();
        return jedisConnectionFactory;
    }

    private GenericObjectPoolConfig createPoolConfig(RedisProperties redisProperties, GenericObjectPoolConfig poolConfig) {
        //最大连接数, 默认8个
        poolConfig.setMaxTotal(redisProperties.getPool().getMaxActive());
        //最大空闲连接数, 默认8个
        poolConfig.setMaxIdle(redisProperties.getPool().getMaxIdle());
        //最小空闲连接数, 默认0个
        poolConfig.setMinIdle(redisProperties.getPool().getMinIdle());
        //获取连接时的最大等待毫秒数，默认-1
        poolConfig.setMaxWaitMillis(redisProperties.getPool().getMaxWait());

        //最小可驱逐空闲时间 毫秒，达到此值后空闲资源将被移除  默认1800000毫秒(30分钟)
        poolConfig.setMinEvictableIdleTimeMillis(1800000);
        //空闲资源的检测周期(单位为毫秒) 如果为负数,则不运行逐出线程, 默认-1
        poolConfig.setTimeBetweenEvictionRunsMillis(-1);

        poolConfig.setTestWhileIdle(false);
        poolConfig.setTestOnCreate(false);
        poolConfig.setTestOnBorrow(false);
        poolConfig.setTestOnReturn(false);
        return poolConfig;
    }

    public RedisTemplate<String, Object> buildRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 配置连接工厂
        template.setConnectionFactory(redisConnectionFactory);

        //使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值（默认使用JDK的序列化方式）
        Jackson2JsonRedisSerializer jacksonSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        //ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会跑出异常
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jacksonSerializer.setObjectMapper(objectMapper);

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
}
