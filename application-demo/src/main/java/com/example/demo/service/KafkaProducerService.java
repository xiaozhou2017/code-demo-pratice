package com.example.demo.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${app.kafka.topics.user-events}")
    private String userEventsTopic;

    @Value("${app.kafka.topics.order-events}")
    private String orderEventsTopic;

    @Value("${app.kafka.topics.notification}")
    private String notificationTopic;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * 发送用户事件消息
     */
    public void sendUserEvent(String key, String message) {
        kafkaTemplate.send(userEventsTopic, key, message);
    }

    /**
     * 发送订单事件消息（异步，带回调）
     */
    public CompletableFuture<SendResult<String, String>> sendOrderEvent(String key, String message) {
        return kafkaTemplate.send(orderEventsTopic, key, message)
                .completable()
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        System.out.println("消息发送成功: " + result.getRecordMetadata());
                    } else {
                        System.err.println("消息发送失败: " + ex.getMessage());
                    }
                });
    }

    /**
     * 发送通知消息（带回调的旧版API）
     */
    public CompletableFuture<SendResult<String, String>> sendNotification(String message) {
        return kafkaTemplate.send(notificationTopic, message)
                .completable()
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        System.out.println("通知发送成功: " + result.getRecordMetadata().offset());
                    } else {
                        System.err.println("通知发送失败: " + ex.getMessage());
                    }
                });
    }
    /**
     * 发送消息到指定主题
     */
    public void sendToTopic(String topic, String key, String message) {
        kafkaTemplate.send(topic, key, message);
    }

    /**
     * 发送消息到指定主题和分区
     */
    public void sendToTopicWithPartition(String topic, Integer partition, String key, String message) {
        kafkaTemplate.send(topic, partition, key, message);
    }
}
