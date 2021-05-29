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

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 分布式锁
 * 模拟秒杀
 */
@Slf4j
@Component
@EnableScheduling
public class RedisLock {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ProductService productService;

    //超时时间
    private static final int TIMEOUT = 4000;
    private AtomicInteger ato = new AtomicInteger(1);

//    @Scheduled(fixedRate = 60 * 60 * 1000)
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
        long time = System.currentTimeMillis() + TIMEOUT;
        Product product = productService.getOne(Wrappers.<Product>lambdaQuery()
                .eq(Product::getId, productId)
                .gt(Product::getLeftNum, 0)
        );
        if (product == null) {
            log.error("库存不足");
            return;
        }

        //加锁
        if (!lock(String.valueOf(productId), String.valueOf(time))) {
            log.error("活动太火爆了，请稍后再操作");
            return;
        }

        //秒杀逻辑
        product.setLeftNum(product.getLeftNum() - 1);
        productService.updateById(product);

        //解锁
        unlock(String.valueOf(productId), String.valueOf(time));
        log.info("秒杀成功" + ato.getAndIncrement());
    }

    /**
     * 加锁
     *
     * @param value 当前时间 + 超时时间
     */
    public boolean lock(String key, String value) {
        Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent(key, value);
        if (ifAbsent != null && ifAbsent) {
            log.info("加锁成功");
            return true;
        }

        String redisValue = redisTemplate.opsForValue().get(key);
        if (StringUtils.hasLength(redisValue) && Long.parseLong(redisValue) < System.currentTimeMillis()) {
            String oldValue = redisTemplate.opsForValue().getAndSet(key, value);
            if (StringUtils.hasLength(oldValue) && oldValue.equals(redisValue)) {
                log.info("锁过期，重新持有锁");
                return true;
            }
        }

        return false;
    }

    /**
     * 解锁
     */
    public void unlock(String key, String value) {
        try {
            String redisValue = redisTemplate.opsForValue().get(key);
            if (StringUtils.hasLength(redisValue) && redisValue.equals(value)) {
                log.info("解锁成功");
                redisTemplate.delete(key);
            }
        } catch (Exception e) {
            log.error("解锁失败");
        }
    }

}


