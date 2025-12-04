package com.example.demo.service;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TopicMessageSender {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Value("${myapp.rabbitmq.topic.exchange:demo.topic.exchange}")
    private String topicExchangeName;

    /**
     * 发送消息到Topic交换机
     * @param routingKey 路由键，决定消息被路由到哪些队列
     * @param message 消息内容
     */
    public void sendMessage(String routingKey, String message) {
        System.out.println("发送消息到Topic交换机，路由键: " + routingKey + ", 内容: " + message);
        this.rabbitTemplate.convertAndSend(topicExchangeName, routingKey, message);
    }

    // 便捷方法，发送不同类型消息
    public void sendInfoMessage(String serviceName, String message) {
        sendMessage(serviceName + ".info", message);
    }

    public void sendErrorMessage(String serviceName, String message) {
        sendMessage(serviceName + ".error", message);
    }

    public void sendLogMessage(String logLevel, String message) {
        sendMessage("log." + logLevel, message);
    }
}
