package com.yzm.lock.redis;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yzm.lock.entity.Product;
import com.yzm.lock.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 分布式锁
 * 模拟秒杀
 */
@Slf4j
@Component
@EnableScheduling
public class RedisLock2 {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ProductService productService;

    private AtomicInteger i = new AtomicInteger(1);
    private static final ThreadLocal<String> local = new ThreadLocal<>();

    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void test() {
        for (int i = 0; i < 100; i++) {
            try {
                new Thread(() -> secKill(1)).start();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void secKill(int productId) {
        Product product = productService.getOne(Wrappers.<Product>lambdaQuery()
                .eq(Product::getId, productId)
                .gt(Product::getLeftNum, 0)
        );
        if (product == null) {
            log.error("库存不足");
            return;
        }

        //加锁
        if (!lock(String.valueOf(productId), UUID.randomUUID().toString(), 4)) {
            log.info("人太多了，请稍后再试");
            return;
        }

        //秒杀逻辑
        product.setLeftNum(product.getLeftNum() - 1);
        productService.updateById(product);

        //解锁
        unlock(String.valueOf(productId));
        log.info("秒杀成功" + i.getAndIncrement());
    }

    /**
     * 加锁
     */
    public boolean lock(String key, String value, long seconds) {
        Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent(key, value, Duration.ofSeconds(seconds));
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
    public void unlock(String key) {
        String localValue = local.get();
        if (StringUtils.hasLength(localValue)) {
            String redisValue = redisTemplate.opsForValue().get(key);
            if (localValue.equals(redisValue)) {
                log.info("解锁成功");
                local.remove();
                redisTemplate.delete(key);
                return;
            }
        }
        log.info("锁过期");
    }

}


