package com.example.demo;

import com.example.demo.service.TopicMessageSender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TopicMessageTest {

    @Autowired
    private TopicMessageSender topicMessageSender;

    @Test
    public void testTopicPatterns() throws InterruptedException {
        System.out.println("=== 开始Topic模式测试 ===");

        // 测试1：发送INFO消息（应被 *.info 队列消费）
        topicMessageSender.sendMessage("order.info", "订单创建成功");
        topicMessageSender.sendInfoMessage("payment", "支付处理完成");

        // 测试2：发送ERROR消息（应被 *.error 队列消费）
        topicMessageSender.sendMessage("user.error", "用户登录失败");
        topicMessageSender.sendErrorMessage("inventory", "库存不足");

        // 测试3：发送LOG消息（应被 log.# 队列消费，同时如果匹配 *.info 或 *.error 也会被相应队列消费）
        topicMessageSender.sendMessage("log.app.start", "应用程序启动");
        topicMessageSender.sendMessage("log.app.error", "应用程序发生错误");
        topicMessageSender.sendMessage("log", "简单的日志消息");

        // 等待消费者处理消息
        Thread.sleep(3000);
        System.out.println("=== Topic模式测试完成 ===");
    }
}
