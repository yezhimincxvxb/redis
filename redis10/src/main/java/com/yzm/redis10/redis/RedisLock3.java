package com.yzm.redis10.redis;

import com.yzm.redis10.entity.Product;
import com.yzm.redis10.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class RedisLock3 {

    private final StringRedisTemplate redisTemplate;
    private final ProductService productService;

    public RedisLock3(StringRedisTemplate redisTemplate, ProductService productService) {
        this.redisTemplate = redisTemplate;
        this.productService = productService;
    }

    private static final int TIMEOUT = 4000;
    private static final String LOCK_PREFIX = "secKill:";
    private final AtomicInteger ato = new AtomicInteger(1);

    public void secKill(int productId) {
        Product product = productService.getById(1);
        if (product.getLeftNum() < 1) {
            log.error("库存不足");
            return;
        }

        //加锁
        //value包含拥有者标识，具有唯一性，防止任何人都可以解锁
        String userId = UUID.randomUUID() + ":" + System.currentTimeMillis() + TIMEOUT;
        if (!lock(LOCK_PREFIX + productId, userId)) {
            log.error("活动太火爆了，请稍后再操作");
            return;
        }

        //秒杀逻辑
        try {
            product.setLeftNum(product.getLeftNum() - 1);
            productService.updateById(product);
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //解锁
        unlock(LOCK_PREFIX + productId, userId);
        log.info("秒杀成功" + ato.getAndIncrement());
    }

    /**
     * 加锁
     */
    public boolean lock(String key, String value) {
        //没有锁，持有并加锁
        Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent(key, value);
        if (ifAbsent != null && ifAbsent) {
            log.info("加锁成功");
            return true;
        }

        //锁已被持有，判断锁过期
        synchronized (RedisLock3.class) {
            String redisValue = redisTemplate.opsForValue().get(key);
            if (StringUtils.hasLength(redisValue) && Long.parseLong(redisValue.split(":")[1]) < System.currentTimeMillis()) {
                String oldValue = redisTemplate.opsForValue().getAndSet(key, value);
                if (StringUtils.hasLength(oldValue) && oldValue.equals(redisValue)) {
                    log.info("锁过期，重新持有锁");
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 解锁
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


