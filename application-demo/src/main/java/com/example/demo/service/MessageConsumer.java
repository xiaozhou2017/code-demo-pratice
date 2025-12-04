package com.example.demo.service;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class MessageConsumer {

    @RabbitListener(queues = "${myapp.rabbitmq.queue}")
    public void receiveMessage(String messageBody, Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            // 1. 打印并处理消息
            System.out.println("接收到消息: " + messageBody);

            // 2. 模拟业务处理逻辑，这里根据您的需求实现
            // 例如，处理订单、保存日志等
            processMessage(messageBody);

            // 3. 处理成功，手动确认消息
            // basicAck(deliveryTag, multiple)
            // deliveryTag: 消息的唯一标识
            // multiple: 是否批量确认，设置为false
            channel.basicAck(deliveryTag, false);
            System.out.println("消息已确认");

        } catch (Exception e) {
            // 4. 处理失败，拒绝消息并重新放回队列
            // basicReject(deliveryTag, requeue)
            // requeue: 是否重新入队，true表示放回队列，false表示丢弃或进入死信队列
            channel.basicReject(deliveryTag, true);
            System.out.println("消息处理失败，已重新放回队列: " + e.getMessage());
        }
    }

    private void processMessage(String message) {
        // 在这里实现您具体的业务逻辑
        // 例如：解析JSON、操作数据库、调用其他服务等
        if (message.contains("test")) {
            // 模拟一个处理逻辑
            System.out.println("处理测试消息...");
        }
    }
}
