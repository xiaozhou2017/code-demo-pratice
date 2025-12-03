package com.example.demo.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author markchou
 * @createtime 2025/11/13
 */
@Configuration
@Slf4j
public class RedisConfig {
//    /**
//     * 配置Redis Sentinel连接工厂
//     */
//    @Bean
//    public JedisConnectionFactory jedisConnectionFactory() {
//        try {
//            log.info("初始化 Redis Sentinel 连接工厂...");
//
//            // 创建哨兵配置
//            RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration()
//                    .master("mymaster");
//
//            // 添加哨兵节点（这些配置会被application.yml中的配置覆盖）
//            sentinelConfig.sentinel("127.0.0.1", 26379);
//            sentinelConfig.sentinel("127.0.0.1", 26380);
//            sentinelConfig.sentinel("127.0.0.1", 26381);
//
//            // 创建连接工厂
//            JedisConnectionFactory factory = new JedisConnectionFactory(sentinelConfig);
//            factory.afterPropertiesSet();
//
//            log.info("✅ Redis Sentinel 连接工厂初始化成功");
//            return factory;
//
//        } catch (Exception e) {
//            log.error("❌ Redis Sentinel 连接工厂初始化失败", e);
//            throw new RuntimeException("Redis连接初始化失败", e);
//        }
//    }
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 关键配置：key使用字符串序列化
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // value使用JSON序列化
        Jackson2JsonRedisSerializer<Object> jsonSerializer =
                new Jackson2JsonRedisSerializer<>(Object.class);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        // 处理LocalDateTime等Java8时间类型
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 处理多态类型
//        objectMapper.activateDefaultTyping(
//                objectMapper.getPolymorphicTypeValidator(),
//                ObjectMapper.DefaultTyping.NON_FINAL
//        );

        jsonSerializer.setObjectMapper(objectMapper);

        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }

}
