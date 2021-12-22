package com.yzm.redis09.config;

import com.yzm.redis09.message.ListenerMessage;
import com.yzm.redis09.message.ListenerMessage2;
import com.yzm.redis09.message.ListenerMessage3;
import com.yzm.redis09.message.ListenerMessage4;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.ErrorHandler;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * redis stream 配置
 */
@Configuration
public class RedisStreamConfig {

    public static final String QUEUE = "y_queue";
    public static final String[] GROUPS = {"y_group", "y_group_2", "y_group_3"};
    public static final String[] CONSUMERS = {"consumer_1", "consumer_2", "consumer_3", "consumer_4"};

    private final RedisTemplate<String, Object> redisTemplate;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    public RedisStreamConfig(RedisTemplate<String, Object> redisTemplate, ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        this.redisTemplate = redisTemplate;
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
    }

    // 启动项目，创建队列并绑定消费组
    @PostConstruct
    public void initQueue() {
        Boolean hasKey = redisTemplate.hasKey(QUEUE);
        if (hasKey == null || !hasKey) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("id", 1);
            map.put("name", "1");
            redisTemplate.opsForStream().add(QUEUE, map);
        }

        StreamInfo.XInfoGroups groups = redisTemplate.opsForStream().groups(QUEUE);
        List<String> groupNames = groups.stream().map(StreamInfo.XInfoGroup::groupName).collect(Collectors.toList());

        for (String group : GROUPS) {
            if (!groupNames.contains(group)) {
                redisTemplate.opsForStream().createGroup(QUEUE, group);
            }
        }
    }

    // 创建配置对象
    @Bean
    public StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ?> streamMessageListenerContainerOptions() {
        return StreamMessageListenerContainer
                .StreamMessageListenerContainerOptions
                .builder()
                // 一次性最多拉取多少条消息
                .batchSize(1)
                // 执行消息轮询的执行器
                .executor(this.threadPoolTaskExecutor)
                // 消息消费异常的handler
                .errorHandler(new ErrorHandler() {
                    @Override
                    public void handleError(Throwable t) {
                        // throw new RuntimeException(t);
                        t.printStackTrace();
                    }
                })
                // 序列化器
                .serializer(new StringRedisSerializer())
                // 超时时间，设置为0，表示不超时（超时后会抛出异常）
                .pollTimeout(Duration.ofSeconds(10))
                .build();
    }

    // 根据配置对象创建监听容器对象
    @Bean
    public StreamMessageListenerContainer<String, ?> streamMessageListenerContainer(RedisConnectionFactory factory) {
        StreamMessageListenerContainer<String, ?> listenerContainer = StreamMessageListenerContainer.create(factory, streamMessageListenerContainerOptions());
        listenerContainer.start();
        return listenerContainer;
    }

    /**
     * 订阅者1，消费组group1，收到消息后自动确认，与订阅者2为竞争关系，消息仅被其中一个消费
     */
    @Bean
    public Subscription subscription(StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer) {
        return streamMessageListenerContainer.receiveAutoAck(
                Consumer.from(GROUPS[0], CONSUMERS[0]),
                StreamOffset.create(QUEUE, ReadOffset.lastConsumed()),
                new ListenerMessage()
        );
    }

    /**
     * 订阅者2，消费组group1，收到消息后自动确认，与订阅者1为竞争关系，消息仅被其中一个消费
     */
    @Bean
    public Subscription subscription2(StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer) {
        return streamMessageListenerContainer.receiveAutoAck(
                Consumer.from(GROUPS[0], CONSUMERS[1]),
                StreamOffset.create(QUEUE, ReadOffset.lastConsumed()),
                new ListenerMessage2()
        );
    }

    /**
     * 订阅者3，消费组group2，收到消息后不自动确认，手动确认，需要用户选择合适的时机确认，与订阅者1和2非竞争关系，即使消息被订阅者1或2消费，亦可消费
     * <p>
     * 当某个消息被ACK，PEL列表就会减少
     */
    @Bean
    public Subscription subscription3(StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer) {
        return streamMessageListenerContainer.receive(
                Consumer.from(GROUPS[1], CONSUMERS[2]),
                StreamOffset.create(QUEUE, ReadOffset.lastConsumed()),
                new ListenerMessage3(GROUPS[1], redisTemplate)
        );
    }

    /**
     * 订阅者4
     * 如果忘记确认（ACK），则PEL列表会不断增长占用内存
     * 如果服务器宕机，重启连接后将再次收到PEL中的消息ID列表
     * 如果忘记确认（ACK），则PEL列表会不断增长占用内存
     * 如果服务器宕机，重启连接后将再次收到PEL中的消息ID列表
     */
    @Bean
    public Subscription subscription4(StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer) {
        return streamMessageListenerContainer.receive(
                Consumer.from(GROUPS[2], CONSUMERS[3]),
                StreamOffset.create(QUEUE, ReadOffset.lastConsumed()),
                new ListenerMessage4()
        );
    }
}
