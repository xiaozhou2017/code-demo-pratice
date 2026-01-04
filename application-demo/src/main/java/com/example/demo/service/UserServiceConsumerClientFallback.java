package com.example.demo.service;

import org.springframework.stereotype.Component;

@Component // 关键：声明为Spring组件
public class UserServiceConsumerClientFallback implements UserServiceConsumerClient {

    @Override
    public String getOrderWithUser(Long id) {
        // 这里是服务不可用时的降级响应
        return "用户服务暂时不可用，请稍后再试。";
    }
}
