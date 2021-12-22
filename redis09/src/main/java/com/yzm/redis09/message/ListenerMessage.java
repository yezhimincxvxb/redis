    package com.yzm.redis09.message;

    import lombok.extern.slf4j.Slf4j;
    import org.springframework.data.redis.connection.stream.MapRecord;
    import org.springframework.data.redis.stream.StreamListener;

    /**
     * redis stream监听消息
     */
    @Slf4j
    public class ListenerMessage implements StreamListener<String, MapRecord<String, String, String>> {

        @Override
        public void onMessage(MapRecord<String, String, String> message) {
            log.info("监听");
            System.out.println("message id " + message.getId());
            System.out.println("stream " + message.getStream());
            System.out.println("body " + message.getValue());
        }

    }
