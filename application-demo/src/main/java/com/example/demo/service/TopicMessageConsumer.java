package com.example.demo.service;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class TopicMessageConsumer {

    /**
     * 消费者1：专门处理 INFO 级别的消息
     * 绑定键：*.info (例如：order.info, user.info, payment.info)
     */
    @RabbitListener(queues = "demo.topic.queue.info")
    public void receiveInfoMessage(String messageBody, Message message, Channel channel) throws IOException {
        processMessage(messageBody, message, channel, "INFO消费者");
    }

    /**
     * 消费者2：专门处理 ERROR 级别的消息
     * 绑定键：*.error (例如：order.error, user.error, payment.error)
     */
    @RabbitListener(queues = "demo.topic.queue.error")
    public void receiveErrorMessage(String messageBody, Message message, Channel channel) throws IOException {
        processMessage(messageBody, message, channel, "ERROR消费者");
    }

    /**
     * 消费者3：处理所有以 log 开头的消息
     * 绑定键：log.# (例如：log, log.app, log.app.error, log.app.info)
     */
    @RabbitListener(queues = "demo.topic.queue.all")
    public void receiveAllLogsMessage(String messageBody, Message message, Channel channel) throws IOException {
        processMessage(messageBody, message, channel, "ALL-LOGS消费者");
    }

    /**
     * 统一的消息处理方法
     */
    private void processMessage(String messageBody, Message message, Channel channel, String consumerName) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();

        try {
            System.out.println("[" + consumerName + "] 接收到消息 - 路由键: " + routingKey + ", 内容: " + messageBody);

            // 根据不同的消息类型执行不同的业务逻辑
            if (consumerName.contains("INFO")) {
                processInfoMessage(messageBody);
            } else if (consumerName.contains("ERROR")) {
                processErrorMessage(messageBody);
            } else {
                processLogMessage(messageBody);
            }

            // 手动确认消息
            channel.basicAck(deliveryTag, false);
            System.out.println("[" + consumerName + "] 消息已确认");

        } catch (Exception e) {
            System.err.println("[" + consumerName + "] 消息处理失败: " + e.getMessage());
            // 处理失败，拒绝消息并重新放回队列（可根据业务需求调整为false进入死信队列）
            channel.basicReject(deliveryTag, true);
        }
    }

    private void processInfoMessage(String message) {
        // 处理INFO消息的业务逻辑，例如记录操作日志、更新状态等
        System.out.println("处理INFO消息: " + message);
    }

    private void processErrorMessage(String message) {
        // 处理ERROR消息的业务逻辑，例如发送告警、记录错误日志等
        System.out.println("处理ERROR消息: " + message);
    }

    private void processLogMessage(String message) {
        // 处理所有日志消息的业务逻辑，例如存储到日志系统
        System.out.println("处理LOG消息: " + message);
    }
}
