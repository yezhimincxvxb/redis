package com.yzm.redis09.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yzm.redis09.config.ObjectMapperConfig;
import com.yzm.redis09.service.RedisStreamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class RedisStreamController {

    private final ObjectMapper objectMapper = ObjectMapperConfig.objectMapper;
    private final RedisStreamService redisStreamService;

    public RedisStreamController(RedisStreamService redisStreamService) {
        this.redisStreamService = redisStreamService;
    }

    @GetMapping("/add")
    public void add(
            @RequestParam("key") String key,
            @RequestParam("id") Integer id,
            @RequestParam("name") String name) {

        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("name", name);
        System.out.println("recordId = " + redisStreamService.xAdd(key, map));
    }

    @GetMapping("/padd")
    public void add(
            @RequestParam("key") String key,
            @RequestParam("id") Integer id) {

        for (int i = id; i < id + 100; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", i);
            map.put("name", i);
            System.out.println("recordId = " + redisStreamService.xAdd(key, map));
        }
    }

    @GetMapping("/info")
    public void info(@RequestParam("key") String key) throws JsonProcessingException {
        StreamInfo.XInfoStream info = redisStreamService.xInfo(key);
        System.out.println("xInfo = " + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(info));
    }

    @GetMapping("/len")
    public void len(@RequestParam("key") String key) {
        System.out.println("xLen = " + redisStreamService.xLen(key));
    }

    @GetMapping("/range")
    public void range(
            @RequestParam("key") String key,
            @RequestParam(value = "start", required = false) String start,
            @RequestParam(value = "end", required = false) String end,
            @RequestParam(value = "count", required = false) Integer count) throws JsonProcessingException {

        // Range.unbounded() <==> ["-","+"]，查询所有
        Range<String> range = start == null ? Range.unbounded() : Range.closed(start, end);
        // Limit.unlimited() <==> -1，不限制数量
        RedisZSetCommands.Limit limit = count == null ? RedisZSetCommands.Limit.unlimited() : RedisZSetCommands.Limit.limit().count(count);
        List<MapRecord<String, Object, Object>> records = redisStreamService.xRange(key, range, limit);
        System.out.println("records = " + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(records));
    }

    @GetMapping("/del")
    public void del(@RequestParam("key") String key, @RequestParam("recordId") String recordId) {
        System.out.println("xDel = " + redisStreamService.xDel(key, recordId));
    }

    @GetMapping("/trim")
    public void trim(
            @RequestParam("key") String key,
            @RequestParam("count") int count,
            @RequestParam("at") boolean at) {
        System.out.println("xTrim = " + redisStreamService.xTrim(key, count, at));
    }

    @GetMapping("/createGroup")
    public void createGroup(@RequestParam("key") String key, @RequestParam("group") String group) {
        System.out.println("xGroupCreate = " + redisStreamService.xGroupCreate(key, group));
    }

    @GetMapping("/destroyGroup")
    public void destroyGroup(@RequestParam("key") String key, @RequestParam("group") String group) {
        System.out.println("xGroupDestroy = " + redisStreamService.xGroupDestroy(key, group));
    }

    @GetMapping("/groups")
    public void groups(@RequestParam("key") String key) throws JsonProcessingException {
        StreamInfo.XInfoGroups groups = redisStreamService.xInfoGroups(key);
        System.out.println("groups = " + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(groups));
    }

    @GetMapping("/read")
    public void read(
            @RequestParam("key") String key,
            @RequestParam(value = "count") int count,
            @RequestParam(value = "ack") boolean ack,
            @RequestParam(value = "block", required = false) Long block,
            @RequestParam(value = "mode") int mode) {

        log.info("开始 。。。");
        // count：限制读取消息个数
        StreamReadOptions options = StreamReadOptions.empty().count(count);
        // xRead 不支持ack，报错：ERR The NOACK option is only supported by XREADGROUP. You called XREAD instead.
        if (ack) options = options.autoAcknowledge();
        // block：是否阻塞，并设置超时时间
        if (block != null && block >= 0L) options = options.block(Duration.ofSeconds(block));

        StreamOffset<String> offset;
        if (mode == 1) {
            // 特殊符号 0-0：表示从队列最先添加的消息读取，不支持阻塞
            offset = StreamOffset.fromStart(key);
        } else if (mode == 2) {
            // 特殊符号 > ：xRead 不支持，报错ERR The > ID can be specified only when calling XREADGROUP using the GROUP <group> <consumer> option.
            offset = StreamOffset.create(key, ReadOffset.lastConsumed());
        } else {
            // 特殊符号 $ ：xRead需要在阻塞模式下使用，非阻塞无意义
            offset = StreamOffset.create(key, ReadOffset.latest());
        }

        List<MapRecord<String, Object, Object>> read = redisStreamService.xRead(options, offset);
        System.out.println("read = " + read);
        log.info("结束 。。。");
    }

    @GetMapping("/readG")
    public void readG(
            @RequestParam("key") String key,
            @RequestParam("group") String group,
            @RequestParam("cons") String cons,
            @RequestParam(value = "count") int count,
            @RequestParam(value = "ack") boolean ack,
            @RequestParam(value = "block", required = false) Long block,
            @RequestParam(value = "mode") int mode) {

        log.info("开始 。。。");
        // count：限制读取消息个数
        StreamReadOptions options = StreamReadOptions.empty().count(count);
        // autoAcknowledge：读取后消息若不ack确认，该消息就会进入消费者的pending列表；ack则不会
        if (ack) options = options.autoAcknowledge();
        // block：是否阻塞，并设置超时时间
        if (block != null && block >= 0L) options = options.block(Duration.ofSeconds(block));

        StreamOffset<String> offset;
        if (mode == 1) {
            // 特殊符号 0-0：表示从pending列表重新读取消息，不支持阻塞，无法读取的过程中自动ack
            offset = StreamOffset.fromStart(key);
        } else if (mode == 2) {
            // 特殊符号 > ：表示只接收比消费者晚创建的消息，之前的消息不管
            offset = StreamOffset.create(key, ReadOffset.lastConsumed());
        } else {
            // 特殊符号 $ ：在xReadGroup中使用是无意义的，报错提示：ERR The $ ID is meaningless in the context of XREADGROUP
            offset = StreamOffset.create(key, ReadOffset.latest());
        }

        List<MapRecord<String, Object, Object>> read = redisStreamService.xReadGroup(Consumer.from(group, cons), options, offset);
        System.out.println("xReadGroup = " + read);
        log.info("结束 。。。");
    }


    @GetMapping("/consumers")
    public void consumers(@RequestParam("key") String key, @RequestParam("group") String group) throws JsonProcessingException {
        StreamInfo.XInfoConsumers consumers = redisStreamService.xInfoConsumers(key, group);
        System.out.println("consumers = " + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(consumers));
    }

    @GetMapping("/delConsumer")
    public void delConsumer(@RequestParam("key") String key,
                            @RequestParam("group") String group,
                            @RequestParam("cons") String cons) {
        System.out.println("delConsumer = " + redisStreamService.xGroupDelConsumer(key, Consumer.from(group, cons)));
    }


    @GetMapping("/pending")
    public void pending(@RequestParam("key") String key, @RequestParam(value = "group") String group) throws JsonProcessingException {
        PendingMessagesSummary pending = redisStreamService.xPending(key, group);
        System.out.println("pendingSummary = " + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(pending));
    }

    @GetMapping("/pending2")
    public void pending2(
            @RequestParam("key") String key,
            @RequestParam(value = "group") String group,
            @RequestParam(value = "cons") String cons) throws JsonProcessingException {
        PendingMessages pending = redisStreamService.xPending(key, Consumer.from(group, cons));
        System.out.println("pending = " + pending);
//        System.out.println("pending = " + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(pending));
    }

    @GetMapping("/ack")
    public void ack(@RequestParam("key") String key,
                    @RequestParam(value = "group") String group,
                    @RequestParam(value = "reId") String reId) {
        System.out.println("ack = " + redisStreamService.xAck(key, group, reId));
    }

    @GetMapping("/claim")
    public void xClaim(
            @RequestParam("key") String key,
            @RequestParam(value = "group") String group,
            @RequestParam(value = "cons") String cons,
            @RequestParam("time") long idleTime,
            @RequestParam("reid") String reid) {
        List<ByteRecord> byteRecords = redisStreamService.xClaim(key, group, cons, idleTime, reid);
        if (!byteRecords.isEmpty()) {
            ByteRecord record = byteRecords.get(0);
            System.out.println(record.getId());
            System.out.println(record.getValue());
        }
    }

}
