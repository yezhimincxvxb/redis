package com.yzm.base.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;

@Component
@EnableScheduling
public class RedisZSetService {

    @Autowired
    private ZSetOperations<String, Object> zSetOperations;

    //========================添加==========================
    public Boolean add(String key, Object var, double score) {
        return zSetOperations.add(key, var, score);
    }

    public Long add(String key, Set<ZSetOperations.TypedTuple<Object>> vars) {
        return zSetOperations.add(key, vars);
    }

    //========================获取==========================
    //获取var元素对应的下标
    public Long rank(String key, Object var) {
        return zSetOperations.rank(key, var);
    }

    public Long reverseRank(String key, Object var) {
        return zSetOperations.reverseRank(key, var);
    }

    //正序
    //根据下标区间[start,end]获取元素var
    public Set<Object> range(String key, long start, long end) {
        return zSetOperations.range(key, start, end);
    }

    //根据下标区间[start,end]获取元素var和score
    public Set<ZSetOperations.TypedTuple<Object>> rangeWithScores(String key, long start, long end) {
        return zSetOperations.rangeWithScores(key, start, end);
    }

    //根据score区间[min,max]获取元素var
    public Set<Object> rangeByScore(String key, double min, double max) {
        return zSetOperations.rangeByScore(key, min, max);
    }

    //根据score区间[min,max]获取元素var和score
    public Set<ZSetOperations.TypedTuple<Object>> rangeByScoreWithScores(String key, double min, double max) {
        return zSetOperations.rangeByScoreWithScores(key, min, max);
    }

    //根据score区间[min,max]获取元素var，跳过offset个，最后只拿count个
    public Set<Object> rangeByScore(String key, double min, double max, long offset, long count) {
        return zSetOperations.rangeByScore(key, min, max, offset, count);
    }

    //根据score区间[min,max]获取元素var和score，跳过offset个，最后只拿count个
    public Set<ZSetOperations.TypedTuple<Object>> rangeByScoreWithScores(String key, double min, double max, long offset, long count) {
        return zSetOperations.rangeByScoreWithScores(key, min, max, offset, count);
    }

    public Set<Object> rangeByLex(String key, RedisZSetCommands.Range range) {
        return zSetOperations.rangeByLex(key, range);
    }

    public Set<Object> rangeByLex(String key, RedisZSetCommands.Range range, RedisZSetCommands.Limit limit) {
        return zSetOperations.rangeByLex(key, range, limit);
    }

    //反序
    //根据下标区间[start,end]获取元素var
    public Set<Object> reverseRange(String key, long start, long end) {
        return zSetOperations.reverseRange(key, start, end);
    }

    //根据下标区间[start,end]获取元素var和score
    public Set<ZSetOperations.TypedTuple<Object>> reverseRangeWithScores(String key, long start, long end) {
        return zSetOperations.reverseRangeWithScores(key, start, end);
    }

    //根据score区间[min,max]获取元素var
    public Set<Object> reverseRangeByScore(String key, double min, double max) {
        return zSetOperations.reverseRangeByScore(key, min, max);
    }

    //根据score区间[min,max]获取元素var和score
    public Set<ZSetOperations.TypedTuple<Object>> reverseRangeByScoreWithScores(String key, double min, double max) {
        return zSetOperations.reverseRangeByScoreWithScores(key, min, max);
    }

    //根据score区间[min,max]获取元素var，跳过offset个，最后只拿count个
    public Set<Object> reverseRangeByScore(String key, double min, double max, long offset, long count) {
        return zSetOperations.reverseRangeByScore(key, min, max, offset, count);
    }

    //根据score区间[min,max]获取元素var和score，跳过offset个，最后只拿count个
    public Set<ZSetOperations.TypedTuple<Object>> reverseRangeByScoreWithScores(String key, double min, double max, long offset, long count) {
        return zSetOperations.reverseRangeByScoreWithScores(key, min, max, offset, count);
    }

    public Set<Object> reverseRangeByLex(String key, RedisZSetCommands.Range range) {
        return zSetOperations.reverseRangeByLex(key, range);
    }

    public Set<Object> reverseRangeByLex(String key, RedisZSetCommands.Range range, RedisZSetCommands.Limit limit) {
        return zSetOperations.reverseRangeByLex(key, range, limit);
    }

    //获取score区间[mix,max]的元素个数
    public Long count(String key, double min, double max) {
        return zSetOperations.count(key, min, max);
    }

    public Long size(String key) {
        return zSetOperations.size(key);
    }

    //获取score
    public Double score(String key, Object var) {
        return zSetOperations.score(key, var);
    }

    //========================修改==========================
    public Double incrementScore(String key, Object var, double score) {
        return zSetOperations.incrementScore(key, var, score);
    }

    //========================删除==========================
    public Long remove(String key, Object... vars) {
        return zSetOperations.remove(key, vars);
    }

    public Long removeRange(String key, long start, long end) {
        return zSetOperations.removeRange(key, start, end);
    }

    public Long removeRangeByScore(String key, double min, double max) {
        return zSetOperations.removeRangeByScore(key, min, max);
    }

    //========================合集==========================
    public Long unionAndStore(String key1, String key2, String dest) {
        return zSetOperations.unionAndStore(key1, key2, dest);
    }

    public Long unionAndStore(String key1, Collection<String> keys, String dest) {
        return zSetOperations.unionAndStore(key1, keys, dest);
    }

    public Long unionAndStore(String key1, Collection<String> keys, String dest, RedisZSetCommands.Aggregate aggregate) {
        return zSetOperations.unionAndStore(key1, keys, dest, aggregate);
    }

    public Long unionAndStore(String key1, Collection<String> keys, String dest, RedisZSetCommands.Aggregate aggregate, RedisZSetCommands.Weights weights) {
        return zSetOperations.unionAndStore(key1, keys, dest, aggregate, weights);
    }

    //========================交集==========================
    public Long intersectAndStore(String key1, String key2, String dest) {
        return zSetOperations.intersectAndStore(key1, key2, dest);
    }

    public Long intersectAndStore(String key1, Collection<String> keys, String dest) {
        return zSetOperations.intersectAndStore(key1, keys, dest);
    }

    public Long intersectAndStore(String key1, Collection<String> keys, String dest, RedisZSetCommands.Aggregate aggregate) {
        return zSetOperations.intersectAndStore(key1, keys, dest, aggregate);
    }

    public Long intersectAndStore(String key1, Collection<String> keys, String dest, RedisZSetCommands.Aggregate aggregate, RedisZSetCommands.Weights weights) {
        return zSetOperations.intersectAndStore(key1, keys, dest, aggregate, weights);
    }

//    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void test() {
//        Set<ZSetOperations.TypedTuple<Object>> tuples = rangeByScoreWithScores("zs_key", 60.0D, 95.0D, 2, 2);
//        for (ZSetOperations.TypedTuple<Object> next : tuples) {
//            System.out.println(next.getValue());
//            System.out.println(next.getScore());
//        }
//        System.out.println(size("zs_key"));
    }
}
