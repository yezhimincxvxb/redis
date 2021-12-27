package com.yzm.redis12.controller;

import com.yzm.redis12.entity.Product;
import com.yzm.redis12.service.ProductService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class RedisController {

    private final StringRedisTemplate stringRedisTemplate;
    private final ProductService productService;

    public RedisController(StringRedisTemplate stringRedisTemplate, ProductService productService) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.productService = productService;
    }

    @GetMapping("/penetrate")
    public String cachePenetrate(Integer id) {
        String cacheKey = "penetrate:" + id;
        long cacheTime = 30L;

        //缓存查询
        String cacheValue = stringRedisTemplate.opsForValue().get(cacheKey);
        if (cacheValue == null) {
            //缓存没有，查询数据库
            Product product = productService.getById(id);
            if (product == null) {
                //数据库没有，设置空值或默认值
                cacheValue = "";
            } else {
                cacheValue = product.getName();
            }
            stringRedisTemplate.opsForValue().set(cacheKey, cacheValue, cacheTime, TimeUnit.SECONDS);
        }

        return cacheValue;
    }

    @GetMapping("/puncture")
    public String cachePuncture(Integer id) {
        String cacheKey = "puncture:" + id;
        long cacheTime = 30L;

        //缓存查询
        String cacheValue = stringRedisTemplate.opsForValue().get(cacheKey);
        if (cacheValue == null) {
            //缓存没有，使用互斥锁查询数据库更新缓存，其余阻塞排队
            synchronized (cacheKey) {
                //此时可能有缓存数据了
                cacheValue = stringRedisTemplate.opsForValue().get(cacheKey);
                if (cacheValue == null) {
                    //缓存还是没有，查询数据库
                    Product product = productService.getById(id);
                    cacheValue = product.getName();
                    //回设缓存
                    stringRedisTemplate.opsForValue().set(cacheKey, cacheValue, cacheTime * 10, TimeUnit.SECONDS);
                }
            }
        }

        return cacheValue;
    }

    public String cachePuncture2(Integer id) throws InterruptedException {
        String cacheKey = "puncture:" + id;
        String blockKey = "block:" + id;
        long cacheTime = 30L;

        //缓存查询
        String cacheValue = stringRedisTemplate.opsForValue().get(cacheKey);
        if (cacheValue == null) {
            //setIfAbsent == SETNX ：只有key不存在的时候才能设置成功，利用它可以实现锁的效果
            if (stringRedisTemplate.opsForValue().setIfAbsent(blockKey, "1", cacheTime, TimeUnit.SECONDS)) {
                //查询数据库
                Product product = productService.getById(id);
                cacheValue = product.getName();
                //回设缓存
                stringRedisTemplate.opsForValue().set(cacheKey, cacheValue, cacheTime * 10, TimeUnit.SECONDS);
                stringRedisTemplate.delete(blockKey);
            } else {
                //阻塞一会，再重试获取数据
                Thread.sleep(50);
                return cachePuncture2(id);
            }
        }

        return cacheValue;
    }

    @GetMapping("/avalanche")
    public String cacheAvalanche(Integer id) {
        String cacheKey = "avalanche:" + id;
        long cacheTime = 30L;

        //缓存查询
        String cacheValue = stringRedisTemplate.opsForValue().get(cacheKey);
        if (cacheValue == null) {
            //缓存没有，使用互斥锁查询数据库更新缓存，其余阻塞排队
            synchronized (cacheKey) {
                //此时可能有缓存数据了
                cacheValue = stringRedisTemplate.opsForValue().get(cacheKey);
                if (cacheValue == null) {
                    //缓存还是没有，查询数据库
                    Product product = productService.getById(id);
                    cacheValue = product.getName();
                    //回设缓存
                    stringRedisTemplate.opsForValue().set(cacheKey, cacheValue, cacheTime * 10, TimeUnit.SECONDS);
                }
            }
        }

        return cacheValue;
    }

    @GetMapping("/avalanche2")
    public String cacheAvalanche2(Integer id) {
        String cacheKey = "avalanche:" + id;
        String signKey = "avalanche:sign" + id;
        long cacheTime = 60L;

        //缓存查询
        String cacheValue = stringRedisTemplate.opsForValue().get(cacheKey);
        //缓存标记
        String signValue = stringRedisTemplate.opsForValue().get(signKey);
        if (signValue == null) {
            //缓存标记过期
            //设置成功的去查询数据库并更新缓存，其余的返回旧的缓存值(缓存值的时间是缓存标记的2倍)
            if (stringRedisTemplate.opsForValue().setIfAbsent(signKey, "1", cacheTime, TimeUnit.SECONDS)) {
                //查询数据库
                Product product = productService.getById(id);
                cacheValue = product.getName();
                stringRedisTemplate.opsForValue().set(cacheKey, cacheValue, cacheTime * 2, TimeUnit.SECONDS);
            }
        }

        return cacheValue;
    }

}
