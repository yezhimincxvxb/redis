package com.yzm.redis11.controller;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.yzm.redis11.bloom.DefaultBloomFilter;
import com.yzm.redis11.bloom.MyBloomFilter;
import com.yzm.redis11.bloom.RedisBloom;
import com.yzm.redis11.bloom.RedisBloomFilter;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.NumberFormat;
import java.util.*;

@RestController
public class RedisController {

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisBloom redisBloom;
    private final RedissonClient redissonClient;

    public RedisController(StringRedisTemplate stringRedisTemplate, RedisBloom redisBloom, RedissonClient redissonClient) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.redisBloom = redisBloom;
        this.redissonClient = redissonClient;
    }

    @GetMapping("/test")
    public void test(@RequestParam("total") int total,
                     @RequestParam(value = "fpp", required = false) String fpp) {

        BloomFilter<String> bloomFilter;
        if (fpp == null) {
            // 默认误差是：0.03
            bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), total);
        } else {
            bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), total, Float.parseFloat(fpp));
        }

        Set<String> set = new HashSet<>(1000);
        List<String> list = new ArrayList<>(1000);

        // 布隆过滤器添加total个数据，比如100w
        for (int i = 0; i < total; i++) {
            String uuid = UUID.randomUUID().toString();
            if (i < 1000) {
                set.add(uuid);
                list.add(uuid);
            }
            bloomFilter.put(uuid);
        }

        // 模拟10w个数据，其中1000个是布隆过滤器已记录的
        int number = total / 10;
        int right = 0; // 布隆过滤器正确次数
        int wrong = 0; // 布隆过滤器误判次数
        for (int i = 0; i < number; i++) {
            int index = number / 1000;
            String str = i % (index) == 0 ? list.get(i / index) : UUID.randomUUID().toString();
            if (bloomFilter.mightContain(str)) {
                if (set.contains(str)) {
                    right++;
                } else {
                    wrong++;
                }
            }
        }

        System.out.println("布隆过滤器容器大小:" + total);
        System.out.println("模拟数据量:" + number);
        System.out.println("正确次数:" + right);
        System.out.println("误判次数:" + wrong);
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(4);
        System.out.println("误判率:" + numberFormat.format((float) wrong / (float) number));
    }

    @GetMapping("/test2")
    public void test2(@RequestParam("total") int total) {
        MyBloomFilter bf = new DefaultBloomFilter();
        bf.clear();

        Set<String> set = new HashSet<>(1000);
        List<String> list = new ArrayList<>(1000);
        //向布隆过滤器中填充数据
        for (int i = 0; i < total; i++) {
            String uuid = UUID.randomUUID().toString();
            if (i < 1000) {
                set.add(uuid);
                list.add(uuid);
            }
            bf.push(uuid);
        }

        int number = total / 10;
        int right = 0; // 布隆过滤器正确次数
        int wrong = 0; // 布隆过滤器误判次数
        for (int i = 0; i < number; i++) {
            int index = number / 1000;
            String str = i % (index) == 0 ? list.get(i / index) : UUID.randomUUID().toString();
            if (bf.contains(str)) {
                if (set.contains(str)) {
                    right++;
                } else {
                    wrong++;
                }
            }
        }

        System.out.println("布隆过滤器容器大小:" + total);
        System.out.println("模拟数据量:" + number);
        System.out.println("正确次数:" + right);
        System.out.println("误判次数:" + wrong);
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(4);
        System.out.println("误判率:" + numberFormat.format((float) wrong / (float) number));
    }


    MyBloomFilter bf;
    Set<String> set;
    List<String> list;

    @GetMapping("/test3")
    public void test3(@RequestParam("total") int total, boolean first) {
        // 向redis添加数据比较久，就第一次添加，一般都是提前添加好并日常积累
        if (first) {
            bf = new RedisBloomFilter(stringRedisTemplate);
            set = new HashSet<>(1000);
            list = new ArrayList<>(1000);

            bf.clear();
            //向布隆过滤器中填充数据
            for (int i = 0; i < total; i++) {
                String uuid = UUID.randomUUID().toString();
                if (i < 1000) {
                    set.add(uuid);
                    list.add(uuid);
                }
                bf.push(uuid);
            }
        }

        int number = total / 10;
        int right = 0; // 布隆过滤器正确次数
        int wrong = 0; // 布隆过滤器误判次数
        for (int i = 0; i < number; i++) {
            int index = number / 1000;
            String str = i % (index) == 0 ? list.get(i / index) : UUID.randomUUID().toString();
            if (bf.contains(str)) {
                if (set.contains(str)) {
                    right++;
                } else {
                    wrong++;
                }
            }
        }

        System.out.println("布隆过滤器容器大小:" + total);
        System.out.println("模拟数据量:" + number);
        System.out.println("正确次数:" + right);
        System.out.println("误判次数:" + wrong);
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(4);
        System.out.println("误判率:" + numberFormat.format((float) wrong / (float) number));
    }

    @GetMapping("/test4")
    public void test4() {
        String key = "y_test4";
        // 配置错误率和存储空间
        System.err.println(redisBloom.reserve(key, "0.01", "10000"));
        // 添加元素
        System.err.println(redisBloom.add(key, "周星驰"));
        // 判断元素是否存在
        System.err.println(redisBloom.exists(key, "周星星"));
        // 批量添加元素
        System.err.println(redisBloom.mAdd(key, "张学友", "刘德华", "郭富城", "黎明"));
        // 批量判断元素是否存在
        System.err.println(redisBloom.mExists(key, "张学友", "刘德华", "郭德纲", "黎明"));
    }

    Set<String> set5;
    List<String> list5;

    @GetMapping("/test5")
    public void test5(@RequestParam("total") int total, String rate, boolean first) {
        String key = "y_test5";
        if (first) {
            redisBloom.reserve(key, rate, String.valueOf(total));
            set5 = new HashSet<>(1000);
            list5 = new ArrayList<>(1000);

            //向布隆过滤器中填充数据
            for (int i = 0; i < total; i++) {
                String uuid = UUID.randomUUID().toString();
                if (i < 1000) {
                    set5.add(uuid);
                    list5.add(uuid);
                }
                redisBloom.add(key, uuid);
            }
        }

        int number = total / 10;
        int right = 0; // 布隆过滤器正确次数
        int wrong = 0; // 布隆过滤器误判次数
        for (int i = 0; i < number; i++) {
            int index = number / 1000;
            String str = i % (index) == 0 ? list5.get(i / index) : UUID.randomUUID().toString();
            if (redisBloom.exists(key, str)) {
                if (set5.contains(str)) {
                    right++;
                } else {
                    wrong++;
                }
            }
        }

        System.out.println("布隆过滤器容器大小:" + total);
        System.out.println("模拟数据量:" + number);
        System.out.println("正确次数:" + right);
        System.out.println("误判次数:" + wrong);
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(4);
        System.out.println("误判率:" + numberFormat.format((float) wrong / (float) number));
    }

    Set<String> set6;
    List<String> list6;
    @GetMapping("/test6")
    public void test6(@RequestParam("total") int total, String rate, boolean first) {
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter("y_test6");

        if (first) {
            bloomFilter.tryInit(total, Double.parseDouble(rate));
            set6 = new HashSet<>(1000);
            list6 = new ArrayList<>(1000);

            //向布隆过滤器中填充数据
            for (int i = 0; i < total; i++) {
                String uuid = UUID.randomUUID().toString();
                if (i < 1000) {
                    set6.add(uuid);
                    list6.add(uuid);
                }
                bloomFilter.add(uuid);
            }
        }

        int number = total / 10;
        int right = 0; // 布隆过滤器正确次数
        int wrong = 0; // 布隆过滤器误判次数
        for (int i = 0; i < number; i++) {
            int index = number / 1000;
            String str = i % (index) == 0 ? list6.get(i / index) : UUID.randomUUID().toString();
            if (bloomFilter.contains(str)) {
                if (set6.contains(str)) {
                    right++;
                } else {
                    wrong++;
                }
            }
        }

        System.out.println("布隆过滤器容器大小:" + total);
        System.out.println("模拟数据量:" + number);
        System.out.println("正确次数:" + right);
        System.out.println("误判次数:" + wrong);
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(4);
        System.out.println("误判率:" + numberFormat.format((float) wrong / (float) number));
    }

}
