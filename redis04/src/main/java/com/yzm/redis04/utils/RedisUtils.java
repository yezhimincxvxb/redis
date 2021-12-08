package com.yzm.redis04.utils;

import lombok.extern.slf4j.Slf4j;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.params.SetParams;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 */
@Slf4j
@Component
public class RedisUtils {

    @Value("${redis.index}")
    private Integer index;

    @Resource(name = "jedPool")
    @Lazy
    private JedisPool jedisPool;
    private Jedis jedis;

    private final ExpiringMap<Integer, Jedis> map;

    {
        map = ExpiringMap.builder().variableExpiration()
                .variableExpiration()
                // 过期时间：10分钟内该jedis实例没有被使用则释放
                .expiration(10, TimeUnit.MINUTES)
                // 过期策略：每次使用jedis实例则刷新过期时间，即从零重新计时
                .expirationPolicy(ExpirationPolicy.ACCESSED)
                .expirationListener((key, value) -> {
                    log.info("关闭jedis连接");
                    close(value);
                })
                .build();
    }

    // 默认0号数据库
    @PostConstruct
    public void init() {
        init(index);
    }

    // 自定义选择数据库
    public void init(int index) {
        Jedis jedis = map.get(index);
        // jedis实例不可用，重新获取
        if (null == jedis || !jedis.isConnected()) {
            jedis = jedisPool.getResource();
            jedis.select(index);
            map.put(index, jedis);
        }
        this.jedis = jedis;
    }

    private void close(Object object) {
        if (object instanceof Jedis) {
            Jedis jedis = (Jedis) object;
            jedis.close();
        }
    }


    /* ---------------------- 公共的命令 ---------------------- */

    /**
     * 判断多个key是否存在，返回key存在的记录(记录即条数)
     */
    public Long exists(String... keys) {
        return jedis.exists(keys);
    }

    /**
     * 判断某个key是否存在
     */
    public Boolean exists(String key) {
        return jedis.exists(key);
    }

    /**
     * 删除多个key，不存在的key直接忽略，返回删除成功的记录
     */
    public Long del(String... keys) {
        return jedis.del(keys);
    }

    /**
     * 删除一个key
     */
    public Long del(String key) {
        return jedis.del(key);
    }

    /**
     * 模糊匹配
     * <p>
     * pattern = * ，查询所有
     * pattern = user* ，查询所有以user开头的key
     * pattern = *user ，查询所有以user结尾的key
     */
    public Set<String> keys(String pattern) {
        return jedis.keys(pattern);
    }

    /**
     * 设置过期时间(单位：秒)
     */
    public Long expire(String key, int seconds) {
        return jedis.expire(key, seconds);
    }

    /**
     * 获取剩余生存时间(单位：秒)
     */
    public Long ttl(String key) {
        return jedis.ttl(key);
    }

    /**
     * 集合排序，正序，从小到大
     */
    public List<String> sort(String key) {
        return jedis.sort(key);
    }

    /**
     * 把排序后的结果存到dstkey集合中
     */
    public Long sort(String key, String dstkey) {
        return jedis.sort(key, dstkey);
    }

    /**
     * 根据条件排序
     */
    public List<String> sort(String key, SortingParams sortingParameters) {
        return jedis.sort(key, sortingParameters);
    }

    /**
     * 把根据条件排序后的结果存到dstkey集合中
     */
    public Long sort(String dstkey, SortingParams sortingParameters, String key) {
        return jedis.sort(key, sortingParameters, dstkey);
    }

    /* ---------------------- String常用命令 ---------------------- */

    /**
     * 存储键值对：key<->value
     * <p>
     * nxxx：nx，当key不存在才执行，xx当key存在才执行
     * expx：ex，过期时间单位：秒，px，过期时间单位：毫秒
     */
    public String set(String key, String value, String nxxx, String expx, long time) {
        /*return jedis.set(key, value, nxxx, expx, time);*/
        SetParams setParams = SetParams.setParams();
        if (!StringUtils.isEmpty(nxxx)) {
            if ("nx".equals(nxxx)) setParams.nx();
            if ("xx".equals(nxxx)) setParams.xx();
        }
        if (!StringUtils.isEmpty(expx)) {
            if ("ex".equals(expx)) setParams.ex((int) time);
            if ("px".equals(expx)) setParams.px(time);
        }
        return jedis.set(key, value, setParams);
    }

    public String set(String key, String value) {
        return jedis.set(key, value);
    }

    public Long setnx(String key, String value) {
        return jedis.setnx(key, value);
    }

    public String setex(String key, String value, int seconds) {
        return jedis.setex(key, seconds, value);
    }

    /**
     * 先获取旧value，再用新的value覆盖掉
     */
    public String getSet(String key, String value) {
        return jedis.getSet(key, value);
    }

    /**
     * 根据key获取value
     */
    public String get(String key) {
        return jedis.get(key);
    }

    /**
     * 获取多个key对应的values
     */
    public List<String> mget(String... keys) {
        return jedis.mget(keys);
    }

    /**
     * 向key对应的value值进行加减操作
     * <p>
     * increment：正数，加操作，反之，减操作
     * 返回操作后的结果
     */
    public Long incrBy(String key, long increment) {
        return jedis.incrBy(key, increment);
    }

    /**
     * 向key对应的value值进行加减操作
     * <p>
     * increment：正数，加操作，反之，减操作
     * 返回操作后的结果
     */
    public Double incrByFloat(String key, double increment) {
        return jedis.incrByFloat(key, increment);
    }

    /**
     * 向key对应的value(旧的)的末尾拼接value(新的)
     * key：oldValue
     * append
     * key：oldValue + newValue
     */
    public Long append(String key, String value) {
        return jedis.append(key, value);
    }

    /**
     * 截取下标[start,end]范围的字符串(下标从0开始)
     * 包头包尾
     */
    public String substr(String key, int start, int end) {
        return jedis.substr(key, start, end);
    }

    /* ---------------------- Hash常用命令 ---------------------- */

    /**
     * 向名称为key的hash中添加元素field<—>value
     * <p>
     * key不存在，创建新的
     * field不存在，创建新的
     * field存在，value覆盖
     */
    public Long hset(String key, String field, String value) {
        return jedis.hset(key, field, value);
    }

    public Long hsetnx(String key, String field, String value) {
        return jedis.hsetnx(key, field, value);
    }

    public Long hsetex(String key, String field, String value, int expireTime) {
        jedis.hset(key, field, value);
        return jedis.expire(key, expireTime);
    }

    public String hmset(String key, Map<String, String> hash) {
        return jedis.hmset(key, hash);
    }

    /**
     * 获取名称为key的hash中field对应的value
     */
    public String hget(String key, String field) {
        return jedis.hget(key, field);
    }

    /**
     * 获取名称为key的hash中多个field对应的values
     */
    public List<String> hmget(String key, String... fields) {
        return jedis.hmget(key, fields);
    }

    /**
     * 获取名称为key的hash中所有fields
     */
    public Set<String> hkeys(String key) {
        return jedis.hkeys(key);
    }

    /**
     * 获取名称为key的hash中所有values
     */
    public List<String> hvals(String key) {
        return jedis.hvals(key);
    }

    /**
     * 获取名称为key的hash表
     */
    public Map<String, String> hgetAll(String key) {
        return jedis.hgetAll(key);
    }

    /**
     * 获取名称为key的hash中field对应的value进行加减操作
     * <p>
     * increment：正数，加操作，反之，减操作
     * 返回操作后的结果
     */
    public Long hincrBy(String key, String field, long increment) {
        return jedis.hincrBy(key, field, increment);
    }

    /**
     * 获取名称为key的hash中field对应的value进行加减操作
     * <p>
     * increment：正数，加操作，反之，减操作
     * 返回操作后的结果
     */
    public Double hincrByFloat(String key, String field, double increment) {
        return jedis.hincrByFloat(key, field, increment);
    }

    /**
     * 判断key名称对应的hash中field是否存在
     */
    public Boolean hexists(String key, String field) {
        return jedis.hexists(key, field);
    }

    /**
     * 批量删除key名称对应的hash中的多个field
     */
    public Long hdel(String key, String... fields) {
        return jedis.hdel(key, fields);
    }

    /**
     * 获取key名称的hash的元素个数
     */
    public Long hlen(String key) {
        return jedis.hlen(key);
    }

    /* ---------------------- List常用命令 ---------------------- */

    /**
     * 向key名称的list尾部添加元素(第一个元素在最左边，最后一个元素在最右边)
     */
    public Long rpush(String key, String... strings) {
        return jedis.rpush(key, strings);
    }

    /**
     * 向key名称的list尾部添加元素(第一个元素在最左边，最后一个元素在最右边)
     * 若key名称的list不存在，则终止操作
     */
    public Long rpushnx(String key, String... strings) {
        return jedis.rpushx(key, strings);
    }

    /**
     * 向key名称的list尾部添加元素(第一个元素在最左边，最后一个元素在最右边)
     * 并设置过期时间(单位：秒)
     */
    public Long rpushex(String key, int seconds, String... strings) {
        jedis.rpush(key, strings);
        return jedis.expire(key, seconds);
    }

    /**
     * 向key名称的list头部添加元素(第一个元素在最右边，最后一个元素在最左边)
     */
    public Long lpush(String key, String... strings) {
        return jedis.lpush(key, strings);
    }

    /**
     * 向key名称的list头部添加元素(第一个元素在最右边，最后一个元素在最左边)
     * 若key名称的list不存在，则终止操作
     */
    public Long lpushnx(String key, String... strings) {
        return jedis.lpushx(key, strings);
    }

    /**
     * 向key名称的list头部添加元素(第一个元素在最右边，最后一个元素在最左边)
     * 并设置过期时间(单位：秒)
     */
    public Long lpushex(String key, int seconds, String... strings) {
        jedis.lpushx(key, strings);
        return jedis.expire(key, seconds);
    }

    /**
     * 获取key名称list的长度
     */
    public Long llen(String key) {
        return jedis.llen(key);
    }

    /**
     * 获取指定[start，stop]范围的元素(下标从0开始)：
     * [0,-1]获取所有
     * [0,-2] 获取除最后一个的所有元素
     */
    public List<String> lrange(String key, long start, long stop) {
        return jedis.lrange(key, start, stop);
    }

    /**
     * 保留指定[start，stop]下标之内的元素
     */
    public String ltrim(String key, long start, long stop) {
        return jedis.ltrim(key, start, stop);
    }

    /**
     * 移除多个(count)value元素(列表中有多个)
     * <p>
     * count > 0 : 从表头开始向表尾搜索，移除与 VALUE 相等的元素，数量为 COUNT
     * count < 0 : 从表尾开始向表头搜索，移除与 VALUE 相等的元素，数量为 COUNT 的绝对值
     * count = 0 : 移除表中所有与 VALUE 相等的值
     */
    public Long lrem(String key, long count, String value) {
        return jedis.lrem(key, count, value);
    }

    /**
     * 根据下标获取对应的元素
     */
    public String lindex(String key, long index) {
        return jedis.lindex(key, index);
    }

    /**
     * 给指定下标元素重新赋值
     */
    public String lset(String key, long index, String value) {
        return jedis.lset(key, index, value);
    }

    /**
     * 左出栈一个元素(即获取并移除最左边的一个元素)
     */
    public String lpop(String key) {
        return jedis.lpop(key);
    }

    /**
     * 右出栈一个元素(即获取并移除最右边的一个元素)
     */
    public String rpop(String key) {
        return jedis.rpop(key);
    }

    /**
     * 从源列表(srckey)的尾部移出一个元素，添加到目标列表(dstkey)的头部
     */
    public String rpoplpush(String srckey, String dstkey) {
        return jedis.rpoplpush(srckey, dstkey);
    }

    /* ---------------------- Set常用命令 ---------------------- */

    /**
     * 向key名称的Set集合中添加元素
     */
    public Long sadd(String key, String... members) {
        return jedis.sadd(key, members);
    }

    /**
     * 获取Set集合的所有元素
     */
    public Set<String> smembers(String key) {
        return jedis.smembers(key);
    }

    /**
     * 移除key名称的Set集合中指定的一个或多个元素
     */
    public Long srem(String key, String... members) {
        return jedis.srem(key, members);
    }

    /**
     * 随机出栈一个元素(获取并删除元素)
     */
    public String spop(String key) {
        return jedis.spop(key);
    }

    /**
     * 随机出栈count个元素(获取并删除元素)
     */
    public Set<String> spop(String key, long count) {
        return jedis.spop(key, count);
    }

    /**
     * 随机获取一个元素(只获取不删元素)
     */
    public String srandmember(String key) {
        return jedis.srandmember(key);
    }

    /**
     * 随机获取count个元素(只获取不删元素)
     */
    public List<String> srandmember(String key, int count) {
        return jedis.srandmember(key, count);
    }

    /**
     * 从源Set集合(srckey)将member元素移到目标Set集合(dstkey)中
     */
    public Long smove(String srckey, String dstkey, String member) {
        return jedis.smove(srckey, dstkey, member);
    }

    /**
     * 获取Set集合元素个数
     */
    public Long scard(String key) {
        return jedis.scard(key);
    }

    /**
     * 判断member是否是key名称Set集合中的元素
     */
    public Boolean sismember(String key, String member) {
        return jedis.sismember(key, member);
    }

    /**
     * 返回2个Set集合的交集(2个集合都有的元素)
     */
    public Set<String> sinter(String key1, String key2) {
        return jedis.sinter(key1, key2);
    }

    /**
     * 返回2个Set集合的交集(2个集合都有的元素)，并把结果存到key3集合中
     */
    public Long sinterstore(String key1, String key2, String key3) {
        return jedis.sinterstore(key1, key2, key3);
    }

    /**
     * 返回2个Set集合的并集(把2个集合的元素合在一起去除重复后的元素)
     */
    public Set<String> sunion(String key1, String key2) {
        return jedis.sunion(key1, key2);
    }

    /**
     * 返回2个Set集合的并集(把2个集合的元素合在一起去除重复后的元素)，并把结果存到key3集合中
     */
    public Long sunionstore(String key1, String key2, String key3) {
        return jedis.sunionstore(key1, key2, key3);
    }

    /**
     * 返回2个Set集合的差集(key1有但key2没有的元素)
     */
    public Set<String> sdiff(String key1, String key2) {
        return jedis.sdiff(key1, key2);
    }

    /**
     * 返回2个Set集合的差集(key1有但key2没有的元素)，并把结果存到key3集合中
     */
    public Long sdiffstore(String key1, String key2, String key3) {
        return jedis.sdiffstore(key1, key2, key3);
    }

    /* ---------------------- ZSet常用命令 ---------------------- */

    /**
     * 向key名称的ZSet集合中添加元素并设置分值
     */
    public Long zadd(String key, double score, String member) {
        return jedis.zadd(key, score, member);
    }

    /**
     * 向key名称的ZSet集合中添加一个Map(key->元素，value->分值)
     */
    public Long zadd(String key, Map<String, Double> scoreMembers) {
        return jedis.zadd(key, scoreMembers);
    }

    /**
     * 获取key名称的ZSet集合指定[start,stop]范围下标的元素：[0,-1]获取所有
     */
    public Set<String> zrange(String key, long start, long stop) {
        return jedis.zrange(key, start, stop);
    }

    /**
     * 获取key名称的ZSet集合指定[min,max]范围分值的元素，包括端点
     */
    public Set<String> zrangeByScore(String key, String min, String max) {
        return jedis.zrangeByScore(key, min, max);
    }

    /**
     * 获取key名称的ZSet集合指定(min,max)范围分值的元素，不包括端点
     */
    public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max) {
        return jedis.zrangeByScoreWithScores(key, min, max);
    }

    /**
     * 获取key名称ZSet集合中member元素的索引下标index
     */
    public Long zrank(String key, String member) {
        return jedis.zrank(key, member);
    }

    /**
     * 获取key名称ZSet集合中member元素的分值score
     */
    public Double zscore(String key, String member) {
        return jedis.zscore(key, member);
    }

    /**
     * 获取key名称的ZSet集合的元素个数
     */
    public Long zcard(String key) {
        return jedis.zcard(key);
    }

    /**
     * 获取key名称的ZSet集合指定[min，max]范围分值的元素个数
     * <p>
     * increment：正数，增操作，反之，减操作
     */
    public Long zcount(String key, String min, String max) {
        return jedis.zcount(key, min, max);
    }

    /**
     * 向key名称的ZSet集合中member元素的分值score进行增减
     * <p>
     * increment：正数，增操作，反之，减操作
     */
    public Double zincrby(String key, double increment, String member) {
        return jedis.zincrby(key, increment, member);
    }

    /**
     * 将key2跟key3的交集结果存放到key1中
     */
    public Long zinterstore(String key1, String key2, String key3) {
        return zinterstore(key1, key2, key3);
    }

    /**
     * 将多个keys的交集结果存放到key中
     */
    public Long zinterstore(String key, String... keys) {
        return jedis.zinterstore(key, keys);
    }

    /**
     * 将key2跟key3的并集结果存放到key1中
     */
    public Long zunionstore(String key1, String key2, String key3) {
        return zunionstore(key1, key2, key3);
    }

    /**
     * 将多个keys的并集结果存放到key中
     */
    public Long zunionstore(String key, String... keys) {
        return jedis.zunionstore(key, keys);
    }

    /**
     * 删除key名称的ZSet集合中的多个members元素
     */
    public Long zrem(String key, String... members) {
        return jedis.zrem(key, members);
    }

    /**
     * 删除key名称ZSet集合指定[start,stop]下标索引index区间的元素
     */
    public Long zremrangeByRank(String key, long start, long stop) {
        return jedis.zremrangeByRank(key, start, stop);
    }

    /**
     * 删除key名称ZSet集合指定[min,max]分值score区间的元素
     */
    public Long zremrangeByScore(String key, String min, String max) {
        return jedis.zremrangeByScore(key, min, max);

    }

}
