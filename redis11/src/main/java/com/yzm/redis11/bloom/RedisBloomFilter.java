package com.yzm.redis11.bloom;

import org.springframework.data.redis.core.StringRedisTemplate;


public class RedisBloomFilter extends MyBloomFilter {

    private final String bloom = "y_bloom";
    private final StringRedisTemplate stringRedisTemplate;

    public RedisBloomFilter(StringRedisTemplate stringRedisTemplate) {
        //向布隆过滤器中填充数据
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public Integer count() {
        return count;
    }

    public void clear() {
        stringRedisTemplate.delete(bloom);
        count = 0;
    }

    public void push(Object key) {
        for (int i : ints) {
            stringRedisTemplate.opsForValue().setBit(bloom, hash(key, i), true);
        }
        count++;
    }

    public boolean contains(Object key) {
        for (int i : ints) {
            Boolean exist = stringRedisTemplate.opsForValue().getBit(bloom, hash(key, i));
            if (exist != null && !exist) return false;
        }
        return true;
    }

    public int hash(Object key, int i) {
        int h;
        int index = key == null ? 0 : (Integer.MAX_VALUE - 1 - i) & ((h = key.hashCode()) ^ (h >>> 16));
        // offset偏移量是正整数
        return index > 0 ? index : -index;
    }
}
