package com.yzm.redis08.utils;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
public class RedisUtils {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisUtils(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 流存在，添加消息；不存在，创建并添加消息
    public RecordId add(String key, Map<String, Object> map) {
        return redisTemplate.opsForStream().add(key, map);
    }

    // 查看流信息
    public StreamInfo.XInfoStream info(String key) {
        return redisTemplate.opsForStream().info(key);
    }

    // 消息个数
    public Long size(String key) {
        return redisTemplate.opsForStream().size(key);
    }

    // 根据条件获取消息
    // end ：结束值， + 表示最大值
    // start ：开始值， - 表示最小值
    public List<MapRecord<String, Object, Object>> range(String key, Range<String> range, RedisZSetCommands.Limit limit) {
        return redisTemplate.opsForStream().range(key, range, limit);
    }

    public List<MapRecord<String, Object, Object>> range(String key, Range<String> range) {
        return this.range(key, range, RedisZSetCommands.Limit.unlimited());
    }

    // 根据条件获取消息，然后反序输出
    public List<MapRecord<String, Object, Object>> reverseRange(String key, Range<String> range, RedisZSetCommands.Limit limit) {
        return redisTemplate.opsForStream().range(key, range, limit);
    }

    public List<MapRecord<String, Object, Object>> reverseRange(String key, Range<String> range) {
        return this.reverseRange(key, range, RedisZSetCommands.Limit.unlimited());
    }

    // 移除消息
    public Long delete(String key, String... recordIds) {
        return redisTemplate.opsForStream().delete(key, recordIds);
    }

    // 修剪，限制长度
    // count保留最新的消息个数，旧的(ID较小的)移除
    public Long trim(String key, long count) {
        return redisTemplate.opsForStream().trim(key, count);
    }

    // 参数approximateTrimming：表示近似的，比如保留10，approximateTrimming=true，意味着至少保留10，可以是10来个
    public Long trim(String key, long count, boolean approximateTrimming) {
        return redisTemplate.opsForStream().trim(key, count, approximateTrimming);
    }

    // 创建消费组
    // $ ： 表示从尾部开始消费，只接受新消息，当前 Stream 消息会全部忽略。
    public String createGroup(String key, String group) {
        return redisTemplate.opsForStream().createGroup(key, group);
    }

    // 移除消费组
    public Boolean destroyGroup(String key, String group) {
        return redisTemplate.opsForStream().destroyGroup(key, group);
    }

    // 查看消费组信息
    public StreamInfo.XInfoGroups groups(String key) {
        return redisTemplate.opsForStream().groups(key);
    }

    // 查看消费者信息
    public StreamInfo.XInfoConsumers consumers(String key, String group) {
        return redisTemplate.opsForStream().consumers(key, group);
    }

    // 移除消费者
    public Boolean deleteConsumer(String key, Consumer consumer) {
        return redisTemplate.opsForStream().deleteConsumer(key, consumer);
    }

    // xReadGroup 读取，读取后进入pending列表
    // >：表示消费者希望只接收从未被投递给其他消费者的消息
    @SafeVarargs
    public final List<MapRecord<String, Object, Object>> read(Consumer consumer, StreamReadOptions options, StreamOffset<String>... offsets) {
        return redisTemplate.opsForStream().read(consumer, options, offsets);
    }

    // xRead，从所有流中读取数据
    @SafeVarargs
    public final List<MapRecord<String, Object, Object>> read(StreamReadOptions options, StreamOffset<String>... offsets) {
        return redisTemplate.opsForStream().read(options, offsets);
    }

    // 查看待确认消息
    public PendingMessages pending(String key, Consumer consumer) {
        return redisTemplate.opsForStream().pending(key, consumer);
    }

    public PendingMessagesSummary pending(String key, String group) {
        return redisTemplate.opsForStream().pending(key, group);
    }

    public PendingMessages pending(String key, Consumer consumer, Range<?> range, long count) {
        return redisTemplate.opsForStream().pending(key, consumer, range, count);
    }

    // 确认消息
    public Long acknowledge(String key, String group, String... recordIds) {
        return redisTemplate.opsForStream().acknowledge(key, group, recordIds);
    }

    // pending消息转移，idleTime：表示超过多少时间没有ack
    public List<ByteRecord> xClaim(String key, String group, String consumer, long idleTime, String recordId) {
        return xClaim(key, group, consumer, idleTime, RecordId.of(recordId));
    }

    public List<ByteRecord> xClaim(String key, String group, String consumer, long idleTime, RecordId... recordIds) {
        return redisTemplate.execute(new RedisCallback<List<ByteRecord>>() {
            @Override
            public List<ByteRecord> doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return redisConnection.streamCommands().xClaim(key.getBytes(), group, consumer, Duration.ofSeconds(idleTime), recordIds);
            }
        });
    }

}
