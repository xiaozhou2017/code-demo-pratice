package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.SysUser;
import com.example.demo.service.ISysUserService;
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
}
