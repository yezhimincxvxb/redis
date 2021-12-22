package com.yzm.redis10.redis;

import com.yzm.redis10.entity.Product;
import com.yzm.redis10.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class RedisLock2 {

    private final StringRedisTemplate redisTemplate;
    private final ProductService productService;

    public RedisLock2(StringRedisTemplate redisTemplate, ProductService productService) {
        this.redisTemplate = redisTemplate;
        this.productService = productService;
    }

    private static final int TIMEOUT = 4000;
    private static final String LOCK_PREFIX = "secKill:";
    private final AtomicInteger i = new AtomicInteger(1);
    private static final ThreadLocal<String> local = new ThreadLocal<>();

    public void secKill(int productId) {
        Product product = productService.getById(1);
        if (product.getLeftNum() < 1) {
            log.error("库存不足");
            return;
        }

        //加锁
        String uuid = UUID.randomUUID().toString();
        if (!lock(LOCK_PREFIX + productId, uuid)) {
            log.info("活动太火爆了，请稍后再操作");
            return;
        }

        //秒杀逻辑
        try {
            product.setLeftNum(product.getLeftNum() - 1);
            productService.updateById(product);
            Thread.sleep(80);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //解锁
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
            local.set(value);
            return true;
        }
        return false;
    }

    /**
     * 解锁
     */
    public void unlock(String key, String value) {
        String localValue = local.get();
        if (localValue.equals(value) && localValue.equals(redisTemplate.opsForValue().get(key))) {
            log.info("解锁成功");
            redisTemplate.delete(key);
            local.remove();
        }
    }

}


