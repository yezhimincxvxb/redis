package com.yzm.redis09.service.impl;

import com.yzm.redis09.service.RedisStreamService;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class RedisStreamServiceImpl implements RedisStreamService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StreamOperations<String, Object, Object> streamOperations;

    public RedisStreamServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.streamOperations = redisTemplate.opsForStream();
    }

    @Override
    public RecordId xAdd(String key, Map<String, Object> map) {
        return streamOperations.add(key, map);
    }

    @Override
    public StreamInfo.XInfoStream xInfo(String key) {
        return streamOperations.info(key);
    }

    @Override
    public Long xLen(String key) {
        return streamOperations.size(key);
    }

    @Override
    public List<MapRecord<String, Object, Object>> xRange(String key, Range<String> range, RedisZSetCommands.Limit limit) {
        return streamOperations.range(key, range, limit);
    }

    @Override
    public List<MapRecord<String, Object, Object>> xReverseRange(String key, Range<String> range, RedisZSetCommands.Limit limit) {
        return streamOperations.reverseRange(key, range, limit);
    }

    @Override
    public Long xDel(String key, String... recordIds) {
        return streamOperations.delete(key, recordIds);
    }

    @Override
    public Long xTrim(String key, long count, boolean approximateTrimming) {
        return streamOperations.trim(key, count, approximateTrimming);
    }

    @Override
    public String xGroupCreate(String key, ReadOffset offset, String group) {
        return streamOperations.createGroup(key, offset, group);
    }

    @Override
    public Boolean xGroupDestroy(String key, String group) {
        return streamOperations.destroyGroup(key, group);
    }

    @Override
    public StreamInfo.XInfoGroups xInfoGroups(String key) {
        return streamOperations.groups(key);
    }

    @SafeVarargs
    @Override
    public final List<MapRecord<String, Object, Object>> xRead(StreamReadOptions options, StreamOffset<String>... offsets) {
        return streamOperations.read(options, offsets);
    }

    @SafeVarargs
    @Override
    public final List<MapRecord<String, Object, Object>> xReadGroup(Consumer consumer, StreamReadOptions options, StreamOffset<String>... offsets) {
        return streamOperations.read(consumer, options, offsets);
    }

    @Override
    public StreamInfo.XInfoConsumers xInfoConsumers(String key, String group) {
        return streamOperations.consumers(key, group);
    }

    @Override
    public Boolean xGroupDelConsumer(String key, Consumer consumer) {
        return streamOperations.deleteConsumer(key, consumer);
    }

    @Override
    public PendingMessagesSummary xPending(String key, String group) {
        return streamOperations.pending(key, group);
    }

    @Override
    public PendingMessages xPending(String key, Consumer consumer, Range<?> range, long count) {
        return streamOperations.pending(key, consumer, range, count);
    }

    @Override
    public Long xAck(String key, String group, String... recordIds) {
        return streamOperations.acknowledge(key, group, recordIds);
    }

    @Override
    public List<ByteRecord> xClaim(String key, String group, String consumer, long idleTime, RecordId... recordIds) {
        return redisTemplate.execute(new RedisCallback<List<ByteRecord>>() {
            @Override
            public List<ByteRecord> doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return redisConnection.streamCommands().xClaim(key.getBytes(), group, consumer, Duration.ofSeconds(idleTime), recordIds);
            }
        });
    }
}
