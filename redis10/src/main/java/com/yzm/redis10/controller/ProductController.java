package com.yzm.redis10.controller;


import com.yzm.redis10.entity.Product;
import com.yzm.redis10.redis.RedisLock;
import com.yzm.redis10.redis.RedisLock2;
import com.yzm.redis10.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductController {

    private final ProductService productService;
    private final RedisLock redisLock;
    private final RedisLock2 redisLock2;

    public ProductController(ProductService productService, RedisLock redisLock, RedisLock2 redisLock2) {
        this.productService = productService;
        this.redisLock = redisLock;
        this.redisLock2 = redisLock2;
    }

    @GetMapping("/list")
    public List<Product> list() {
        return productService.list();
    }

    @GetMapping("/inr")
    public void inrLeft(Integer id) {
        productService.updateById(Product.builder().id(id).leftNum(10).build());
    }

    @GetMapping("/buy")
    public String buy() {
        for (int i = 0; i < 100; i++) {
            try {
                new Thread(() -> redisLock.secKill(1)).start();
                Thread.sleep(50);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "秒杀结束";
    }

    @GetMapping("/buy2")
    public String buy2() {
        for (int i = 0; i < 100; i++) {
            try {
                new Thread(() -> redisLock2.secKill(1)).start();
                Thread.sleep(60);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "秒杀结束";
    }
}
