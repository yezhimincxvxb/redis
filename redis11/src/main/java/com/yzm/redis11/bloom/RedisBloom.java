package com.yzm.redis11.bloom;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * redis布隆过滤器
 */
@Component
public class RedisBloom {

    private final StringRedisTemplate redisTemplate;

    public RedisBloom(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private static RedisScript<Boolean> reserve = new DefaultRedisScript<>("return redis.call('bf.reserve', KEYS[1], ARGV[1], ARGV[2])", Boolean.class);

    private static RedisScript<Boolean> add = new DefaultRedisScript<>("return redis.call('bf.add', KEYS[1], ARGV[1])", Boolean.class);

    private static RedisScript<Boolean> exists = new DefaultRedisScript<>("return redis.call('bf.exists', KEYS[1], ARGV[1])", Boolean.class);

    private static String mAdd = "return redis.call('bf.madd', KEYS[1], %s)";

    private static String mExists = "return redis.call('bf.mexists', KEYS[1], %s)";


    /**
     * 设置错误率和大小（需要在添加元素前调用，若已存在元素，则会报错）
     * 错误率越低，需要的空间越大
     * bf.reserve key errorRate initialSize
     *
     * @param key
     * @param errorRate   错误率，默认0.01
     * @param initialSize 默认100，预计放入的元素数量，当实际数量超出这个数值时，误判率会上升，尽量估计一个准确数值再加上一定的冗余空间
     */
    public Boolean reserve(String key, String errorRate, String initialSize) {
        return redisTemplate.execute(reserve, Arrays.asList(key), errorRate, initialSize);
    }

    /**
     * 添加元素
     * bf.add key value
     *
     * @return true表示添加成功，false表示添加失败（存在时会返回false）
     */
    public Boolean add(String key, String value) {
        return redisTemplate.execute(add, Arrays.asList(key), value);
    }

    /**
     * 查看元素是否存在（判断为存在时有可能是误判，不存在是一定不存在）
     * bf.exists key value
     *
     * @return true表示存在，false表示不存在
     */
    public Boolean exists(String key, String value) {
        return redisTemplate.execute(exists, Arrays.asList(key), value);
    }

    /**
     * 批量添加元素
     * bf.madd key v1,v2,v3...
     *
     * @return 按序 1表示添加成功，0表示添加失败
     */
    public List<Integer> mAdd(String key, String... values) {
        return (List<Integer>) redisTemplate.execute(this.generateScript(mAdd, values), Arrays.asList(key), values);
    }

    /**
     * 批量检查元素是否存在（判断为存在时有可能是误判，不存在是一定不存在）
     * bf.mexists key v1,v2,v3...
     *
     * @return 按序 1表示存在，0表示不存在
     */
    public List<Integer> mExists(String key, String... values) {
        return (List<Integer>) redisTemplate.execute(this.generateScript(mExists, values), Arrays.asList(key), values);
    }

    private RedisScript<List> generateScript(String script, String[] values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= values.length; i++) {
            if (i != 1) {
                sb.append(",");
            }
            sb.append("ARGV[").append(i).append("]");
        }
        return new DefaultRedisScript<>(String.format(script, sb), List.class);
    }

}
