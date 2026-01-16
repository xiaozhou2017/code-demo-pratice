package com.example.demo.controller;

import com.example.demo.config.RateLimiter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class BasicRateLimitController {


    // 基础用法：每秒最多1次请求
    @RateLimiter(key = "basic_api", count = 1, time = 1, timeUnit = TimeUnit.SECONDS)
    @GetMapping("/api/basic")
    public String basicApi() {
        return "基础接口响应";
    }

    // 针对用户限流：每个用户每秒最多请求5次
    @RateLimiter(key = "#userId", count = 5, time = 1, timeUnit = TimeUnit.SECONDS)
    @GetMapping("/api/user")
    public String userApi(@RequestParam String userId) {
        return "用户接口响应，用户ID: " + userId;
    }

    // 带超时等待的限流：每秒1次，最多等待2秒
    @RateLimiter(key = "wait_api", count = 1, time = 1, timeout = 2000)
    @GetMapping("/api/wait")
    public String waitApi() {
        return "带等待的接口响应";
    }
}
