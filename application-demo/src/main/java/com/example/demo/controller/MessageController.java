package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.SysUser;
import com.example.demo.service.ISysUserService;
import com.example.demo.service.TopicMessageSender;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class MessageController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${myapp.rabbitmq.exchange}")
    private String exchange;

    @Value("${myapp.rabbitmq.routing-key}")
    private String routingKey;
    @Autowired
    private  ISysUserService iSysUserService;
    @GetMapping("/send")
    public String sendMessage(@RequestParam String message) {
        SysUser sysUser= iSysUserService.selectById(1l);
        rabbitTemplate.convertAndSend(exchange, routingKey, JSONObject.toJSONString(sysUser));
        return "消息发送成功: " + message;
    }
    @Autowired
    private TopicMessageSender topicMessageSender;
    @GetMapping("/test")
    public String sendTopicMessage() {
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
        return "消息发送成功: ";
    }

}
