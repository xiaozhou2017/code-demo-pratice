package com.example.demo.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KafkaConsumerService {

    /**
     * 监听用户事件主题（单条消息处理）
     */
    @KafkaListener(
            topics = "${app.kafka.topics.user-events}",
            groupId = "user-events-consumer",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeUserEvent(
            @Payload String message,
            Acknowledgment acknowledgment,
            @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
            @Header(KafkaHeaders.OFFSET) long offset

    ) {
        try {
            System.out.printf("收到用户事件消息 -> Key: %s, Partition: %d, Offset: %d, Message: %s%n",
                    key, partition, offset, message);

            // 业务处理逻辑
            processUserEvent(message);
            acknowledgment.acknowledge();
        }catch (Exception e) {
            System.err.println("收到用户事件消息失败: " + e.getMessage());
            // 可根据业务需求决定是否重试
        }
    }

    /**
     * 监听订单事件主题（手动提交偏移量）
     */
    @KafkaListener(
            topics = "${app.kafka.topics.order-events}",
            groupId = "order-events-consumer",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeOrderEvent(
            @Payload String message,
            Acknowledgment acknowledgment,
            @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        try {
            System.out.println("收到订单事件: " + message);
            System.out.printf("收到订单事件消息 -> Key: %s, Partition: %d, Offset: %d, Message: %s%n",
                    key, partition, offset, message);
            // 业务处理逻辑
            processOrderEvent(message);

            // 手动确认消息处理完成
            acknowledgment.acknowledge();
        } catch (Exception e) {
            System.err.println("处理订单事件失败: " + e.getMessage());
            // 可根据业务需求决定是否重试
        }
    }

    /**
     * 批量消费消息
     */
    @KafkaListener(
            topics = "${app.kafka.topics.notification}",
            groupId = "notification-batch-consumer",
            containerFactory = "batchFactory"
    )
    public void consumeNotifications(List<String> messages,Acknowledgment ack) {
        System.out.println("通知发送成功，数量: " + messages.size());
        try {
            for (String message : messages) {
                processNotification(message);
            }
            ack.acknowledge(); // 必须手动提交
        } catch (Exception e) {
            // 处理异常，此时不提交ack，消息会重投
        }
    }

    /**
     * 使用正则表达式匹配多个主题
     */
    @KafkaListener(
            topicPattern = ".*-events-topic",
            groupId = "pattern-consumer"
    )
    public void consumePatternTopics(String message) {
        System.out.println("收到模式匹配主题消息: " + message);
    }

    private void processUserEvent(String message) {
        // 处理用户事件的业务逻辑
        System.out.println("处理用户事件: " + message);
    }

    private void processOrderEvent(String message) {
        // 处理订单事件的业务逻辑
        System.out.println("处理订单事件: " + message);
    }

    private void processNotification(String message) {
        // 处理通知的业务逻辑
        System.out.println("处理通知: " + message);
    }
}
