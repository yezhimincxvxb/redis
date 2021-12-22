package com.yzm.redis09.service;

import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.connection.stream.*;

import java.util.List;
import java.util.Map;

public interface RedisStreamService {

    /**
     * 生产消息
     * XADD key * hkey1 hval1 [hkey2 hval2...]
     * key不存在，创建键为key的Stream流，并往流里添加消息
     * key存在，往流里添加消息
     */
    RecordId xAdd(String key, Map<String, Object> map);

    /**
     * 查看Stream的详情
     * XINFO STREAM key
     */
    StreamInfo.XInfoStream xInfo(String key);

    /**
     * 查看Stream的消息个数
     * XLEN key
     */
    Long xLen(String key);

    /**
     * 查询消息
     * XRANGE key start end [COUNT count]
     * range：表示查询区间，比如区间(消息ID，消息ID2)，查询消息ID到消息ID2之间的消息，特殊值("-","+")表示流中可能的最小ID和最大ID
     * Range.unbounded()：查询所有
     * Range.closed(消息ID，消息ID2)：查询[消息ID，消息ID2]
     * Range.open(消息ID，消息ID2)：查询(消息ID，消息ID2)
     * limit：表示查询出来后限制显示个数
     * Limit.limit().count(限制个数)
     */
    List<MapRecord<String, Object, Object>> xRange(String key, Range<String> range, RedisZSetCommands.Limit limit);

    default List<MapRecord<String, Object, Object>> xRange(String key, Range<String> range) {
        return this.xRange(key, range, RedisZSetCommands.Limit.unlimited());
    }

    /**
     * 查询消息
     * XREVRANGE key end start [COUNT count]
     * xReverseRange用法跟xRange一样，只是最后显示的时候是反序的，即消息ID从大到小显示
     */
    List<MapRecord<String, Object, Object>> xReverseRange(String key, Range<String> range, RedisZSetCommands.Limit limit);

    default List<MapRecord<String, Object, Object>> xReverseRange(String key, Range<String> range) {
        return this.xReverseRange(key, range, RedisZSetCommands.Limit.unlimited());
    }

    /**
     * 批量删除消息
     * XDEL key ID [ID ...]
     */
    Long xDel(String key, String... recordIds);

    /**
     * 修剪/保留消息
     * XTRIM key MAXLEN | MINID [~] count
     * count：保留消息个数，当count是具体的消息ID时，表示移除ID小于count这个ID的所有消息
     * approximateTrimming：近似
     * 等于false时，表示精确保留count个个数的消息，不多不少只能是count
     * 等于true时，表示近似保留count个个数的消息，不能少于count，但可以稍微多余count(前提条件是数据量多于200个)
     */
    default Long xTrim(String key, long count) {
        return this.xTrim(key, count, false);
    }

    Long xTrim(String key, long count, boolean approximateTrimming);

    /**
     * 创建消费组
     * XGROUP CREATE key groupname id-or-$
     * XGROUP SETID key groupname id-or-$ (消费组已创建，重新设置读取消息顺序)
     * id为0表示组从stream的第一条数据开始读，
     * id为$表示组从新的消息开始读取。(默认)
     */
    default String xGroupCreate(String key, String group) {
        return xGroupCreate(key, ReadOffset.latest(), group);
    }

    String xGroupCreate(String key, ReadOffset offset, String group);

    /**
     * 销毁消费组
     * XGROUP DESTROY key groupname
     */
    Boolean xGroupDestroy(String key, String group);

    /**
     * 查看消费组详情
     * XINFO GROUPS key
     */
    StreamInfo.XInfoGroups xInfoGroups(String key);

    /**
     * 读取消息
     * XREAD [COUNT count] [BLOCK milliseconds] STREAMS key[key ...] id[id ...]
     * 从一个或者多个流中读取数据
     * 特殊ID=0-0：从队列最先添加的消息读取
     * 特殊ID=$：只接收从我们阻塞的那一刻开始通过XADD添加到流的消息，对已经添加的历史消息不感兴趣
     * 在阻塞模式中，可以使用$，表示最新的消息ID。（在非阻塞模式下$无意义）。
     */
    List<MapRecord<String, Object, Object>> xRead(StreamReadOptions options, StreamOffset<String>... offsets);

    /**
     * 读取消息，强制带消费组、消费者
     * XREADGROUP GROUP group consumer [COUNT count] [BLOCK milliseconds] [NOACK] STREAMS key[key ...] ID[ID ...]
     * 特殊符号 0-0：表示从pending列表重新读取消息，不支持阻塞，无法读取的过程自动ack
     * 特殊符号 > ：表示只接收比消费者晚创建的消息，之前的消息不管
     * 特殊符号 $ ：在xReadGroup中使用是无意义的，报错提示：ERR The $ ID is meaningless in the context of XREADGROUP
     */
    List<MapRecord<String, Object, Object>> xReadGroup(Consumer consumer, StreamReadOptions options, StreamOffset<String>... offsets);

    /**
     * 消费者详情
     * XINFO CONSUMERS key group
     */
    StreamInfo.XInfoConsumers xInfoConsumers(String key, String group);

    /**
     * 删除消费者
     * XGROUP DELCONSUMER key groupname consumername
     */
    Boolean xGroupDelConsumer(String key, Consumer consumer);

    /**
     * Pending Entries List (PEL)
     * XPENDING key group [consumer] [start end count]
     * 查看指定消费组的待处理列表
     */
    PendingMessagesSummary xPending(String key, String group);

    /**
     * 查看指定消费者的待处理列表
     */
    default PendingMessages xPending(String key, Consumer consumer) {
        return this.xPending(key, consumer, Range.unbounded(), -1L);
    }

    PendingMessages xPending(String key, Consumer consumer, Range<?> range, long count);

    /**
     * 消息确认(从PEL中删除一条或多条消息)
     * XACK key group ID[ID ...]
     */
    Long xAck(String key, String group, String... recordIds);

    /**
     * 消息转移
     * XCLAIM key group consumer min-idle-time ID[ID ...]
     * idleTime：转移条件，进入PEL列表的时间大于空闲时间
     */
    default List<ByteRecord> xClaim(String key, String group, String consumer, long idleTime, String recordId) {
        return xClaim(key, group, consumer, idleTime, RecordId.of(recordId));
    }

    List<ByteRecord> xClaim(String key, String group, String consumer, long idleTime, RecordId... recordIds);
}
