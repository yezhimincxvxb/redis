package com.yzm.redis02.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class ObjectMapperConfig {

    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Bean(name = "myObjectMapper")
    public ObjectMapper objectMapper() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        return new ObjectMapper()
                // 转换为格式化的json(控制台打印时，自动格式化规范)
                //.enable(SerializationFeature.INDENT_OUTPUT)
                // Include.ALWAYS  是序列化对像所有属性(默认)
                // Include.NON_NULL 只有不为null的字段才被序列化,属性为NULL 不序列化
                // Include.NON_EMPTY 如果为null或者 空字符串和空集合都不会被序列化
                // Include.NON_DEFAULT 属性为默认值不序列化
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                // 如果是空对象的时候,不抛异常
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                // 反序列化的时候如果多了其他属性,不抛出异常
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                // 取消时间的转化格式,默认是时间戳,可以取消,同时需要设置要表现的时间格式
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .setDateFormat(new SimpleDateFormat(PATTERN))
                // 对LocalDateTime序列化跟反序列化
                .registerModule(javaTimeModule)

                .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
                // 此项必须配置，否则会报java.lang.ClassCastException: java.util.LinkedHashMap cannot be cast to XXX
                .enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY)
                ;
    }

    static class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
        @Override
        public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(value.format(DateTimeFormatter.ofPattern(PATTERN)));
        }
    }

    static class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext deserializationContext) throws IOException {
            return LocalDateTime.parse(p.getValueAsString(), DateTimeFormatter.ofPattern(PATTERN));
        }
    }

}
