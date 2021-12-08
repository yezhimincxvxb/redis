package com.yzm.redis04.controller;

import com.yzm.redis04.utils.RedisUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class RedisController {

    private final RedisUtils redisUtils;

    public RedisController(RedisUtils redisUtils) {
        this.redisUtils = redisUtils;
    }

    @GetMapping(path = "/set")
    public String set(String key, String value) {
        return redisUtils.set(key, value, "nx", "ex", 5 * 60);
    }

    @GetMapping(path = "/get")
    public String get(String key) {
        return redisUtils.get(key);
    }

    @GetMapping(path = "/incrBy")
    public String incrBy(String key, long increment) {
        return redisUtils.incrBy(key, increment).toString();
    }

    @GetMapping(path = "/hmset")
    public String hmset(String key) {
        Map<String, String> map = new HashMap<>();
        map.put("mfield1", "mvalue1");
        map.put("mfield2", "mvalue2");
        map.put("mfield3", "mvalue3");
        return redisUtils.hmset(key, map);
    }

    @GetMapping(path = "/hgetAll")
    public String hget(String key) {
        Map<String, String> map = redisUtils.hgetAll(key);
        map.forEach((field, value) -> System.out.println(field + "=" + value));
        return "ok";
    }

    /*@GetMapping(path = "/sort")
    public String set(String v) {
        RedisUtil.CommonRedis.del(redisUtil.getRedis_0(), "list");
        String[] lists = {"1", "5", "3", "4"};
        RedisUtil.ListRedis.rpush(redisUtil.getRedis_0(), "list", lists);
        List<String> list = RedisUtil.ListRedis.lrange(redisUtil.getRedis_0(), "list", 0, -1);
        System.out.println("list = " + list);

        RedisUtil.StringRedis.set(redisUtil.getRedis_0(), "name_1", "aaa");
        RedisUtil.StringRedis.set(redisUtil.getRedis_0(), "level_1", "67");
        RedisUtil.StringRedis.set(redisUtil.getRedis_0(), "name_2", "bbb");
        RedisUtil.StringRedis.set(redisUtil.getRedis_0(), "level_2", "76");
        RedisUtil.StringRedis.set(redisUtil.getRedis_0(), "name_3", "ccc");
        RedisUtil.StringRedis.set(redisUtil.getRedis_0(), "level_3", "59");
        RedisUtil.StringRedis.set(redisUtil.getRedis_0(), "name_4", "ddd");
        RedisUtil.StringRedis.set(redisUtil.getRedis_0(), "level_4", "23");
        RedisUtil.StringRedis.set(redisUtil.getRedis_0(), "name_5", "eee");
        RedisUtil.StringRedis.set(redisUtil.getRedis_0(), "level_5", "99");

        SortingParams params = new SortingParams()
                // 根据list的元素匹配level，并获取对应的等级值
                .by("level_*")
                // 根据等级值降序，从大到小
                .desc()
                // 获取对应的name跟level
                .get("name_*")
                .get("level_*");
        List<String> sort = RedisUtil.CommonRedis.sort(redisUtil.getRedis_0(), "list", params);
        System.out.println("sort = " + sort);
        return "ok";
    }

    @GetMapping(path = "b")
    public String get(String v, Integer i, Double d) {
        System.out.println(RedisUtil.CommonRedis.ttl(redisUtil.getRedis_0(), "keyName"));
        System.out.println(RedisUtil.StringRedis.get(redisUtil.getRedis_0(), "keyName"));
        Double aaa = RedisUtil.StringRedis.incrByFloat(redisUtil.getRedis_0(), "keyName", d);
        System.out.println("keyName = " + aaa);
        return "ok";
    }*/
}
