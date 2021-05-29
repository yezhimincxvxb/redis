package com.yzm.redisson.config;

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
                .setAddress("redis://192.168.145.11:6380")
                .setPassword("3Q3Q")
                .setDatabase(0);
        return Redisson.create(config);
    }

    /**
     * 哨兵模式
     */
//    @Bean(destroyMethod = "shutdown")
    public RedissonClient useSentinelServers() {
        Config config = new Config();
        config.useSentinelServers()
                .setMasterName("mymaster")
                .addSentinelAddress("redis://192.168.145.11:26381," +
                        "redis://192.168.145.11:26382," +
                        "redis://192.168.145.11:26383")
                .setPassword("3Q3Q")
                .setDatabase(0);
        return Redisson.create(config);
    }

    /**
     * 集群模式
     */
//    @Bean(destroyMethod = "shutdown")
    public RedissonClient useClusterServers() {
        Config config = new Config();
        config.useClusterServers()
                .addNodeAddress("redis://192.168.145.11:6391," +
                        "redis://192.168.145.11:6392," +
                        "redis://192.168.145.11:6393," +
                        "redis://192.168.145.11:6394," +
                        "redis://192.168.145.11:6395," +
                        "redis://192.168.145.11:6396")
                .setPassword("3Q3Q");
        return Redisson.create(config);
    }


}
