package com.yzm.redis05.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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

import javax.annotation.Resource;
import java.time.Duration;

@Configuration
public class RedisConfig {

    @Resource(name = "myObjectMapper")
    private ObjectMapper objectMapper;

    //=============================================== master =======================================================
    @Bean(name = "masterProperties")
    @ConfigurationProperties(prefix = "spring.redis.master")
    public RedisProperties masterProperties() {
        return new RedisProperties();
    }

    @Primary
    @Bean(name = "masterFactory")
    public RedisConnectionFactory masterFactory(@Qualifier("masterProperties") RedisProperties redisProperties) {
        return this.createRedisConnFactory(redisProperties);
    }

    @Bean(name = "masterRedisTemplate")
    public RedisTemplate<String, Object> masterRedisTemplate(@Qualifier("masterFactory") RedisConnectionFactory redisConnectionFactory) {
        return this.buildRedisTemplate(redisConnectionFactory);
    }

    //=============================================== slave =======================================================
    @Bean(name = "slaveProperties")
    @ConfigurationProperties(prefix = "spring.redis.slave")
    public RedisProperties slaveProperties() {
        return new RedisProperties();
    }

    @Bean(name = "slaveFactory")
    public RedisConnectionFactory slaveFactory(@Qualifier("slaveProperties") RedisProperties redisProperties) {
        return this.createRedisConnFactory(redisProperties);
    }

    @Bean(name = "slaveRedisTemplate")
    public RedisTemplate<String, Object> slaveRedisTemplate(@Qualifier("slaveFactory") RedisConnectionFactory redisConnectionFactory) {
        return this.buildRedisTemplate(redisConnectionFactory);
    }

    //=============================================== slave2 =======================================================
    @Bean(name = "slave2Properties")
    @ConfigurationProperties(prefix = "spring.redis.slave2")
    public RedisProperties slave2Properties() {
        return new RedisProperties();
    }

    @Bean(name = "slave2Factory")
    public RedisConnectionFactory slave2Factory(@Qualifier("slave2Properties") RedisProperties redisProperties) {
        return this.createRedisConnFactory(redisProperties);
    }

    @Bean(name = "slave2RedisTemplate")
    public RedisTemplate<String, Object> slave2RedisTemplate(@Qualifier("slave2Factory") RedisConnectionFactory redisConnectionFactory) {
        return this.buildRedisTemplate(redisConnectionFactory);
    }

    public RedisConnectionFactory createRedisConnFactory(RedisProperties redisProperties) {
        RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration();
        standaloneConfiguration.setHostName(redisProperties.getHost());
        standaloneConfiguration.setPort(redisProperties.getPort());
        standaloneConfiguration.setDatabase(redisProperties.getDatabase());
        standaloneConfiguration.setPassword(redisProperties.getPassword());

        return lettuceConnectionFactory(redisProperties, standaloneConfiguration);
//        return jedisConnectionFactory(redisProperties, standaloneConfiguration);
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
        Jackson2JsonRedisSerializer<Object> jacksonSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        jacksonSerializer.setObjectMapper(objectMapper);

        //使用StringRedisSerializer来序列化和反序列化redis的key,value采用json序列化
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(jacksonSerializer);

        // 设置hash key 和value序列化模式
        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(jacksonSerializer);
        template.afterPropertiesSet();
        return template;
    }
}
