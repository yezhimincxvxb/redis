package com.yzm.redis10.redis;

import com.yzm.redis10.entity.Product;
import com.yzm.redis10.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class RedisLock {

    private final StringRedisTemplate redisTemplate;
    private final ProductService productService;

    public RedisLock(StringRedisTemplate redisTemplate, ProductService productService) {
        this.redisTemplate = redisTemplate;
        this.productService = productService;
    }

    private static final int TIMEOUT = 4000;
    private static final String LOCK_PREFIX = "secKill:";
    private final AtomicInteger i = new AtomicInteger(1);

    public void secKill(int productId) {
        // 查询库存
        Product product = productService.getById(1);
        if (product.getLeftNum() < 1) {
            log.error("库存不足");
            return;
        }

        // 加锁
        String uuid = UUID.randomUUID().toString();
        if (!lock(LOCK_PREFIX + productId, uuid)) {
            log.info("活动太火爆了，请稍后再操作");
            return;
        }

        // 秒杀逻辑
        try {
            product.setLeftNum(product.getLeftNum() - 1);
            productService.updateById(product);
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 解锁
        unlock(LOCK_PREFIX + productId, uuid);
        log.info("秒杀成功" + i.getAndIncrement());
    }

    /**
     * 加锁
     */
    public boolean lock(String key, String value) {
        Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent(key, value, Duration.ofMillis(TIMEOUT));
        if (ifAbsent != null && ifAbsent) {
            log.info("加锁成功");
            return true;
        }
        return false;
    }

    /**
     * 解锁(lua脚本原子性)
     */
    public void unlock(String key, String value) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] " +
                "then return redis.call('del', KEYS[1]) " +
                "else return 0 " +
                "end";

        List<String> keys = new ArrayList<>();
        keys.add(key);
        Long execute = redisTemplate.execute(RedisScript.of(script, Long.class), keys, value);
        log.info("解锁成功 = " + execute);
    }
}


