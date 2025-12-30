package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author markchou
 * @createtime 2025/12/25
 */
@Configuration
@EnableAsync
public class ThreadPoolConfig {
    @Bean(name = "myTaskExecutor")
    public ThreadPoolTaskExecutor myTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数（通常根据CPU核心数设定）
        executor.setCorePoolSize(2);
        // 设置最大线程数
        executor.setMaxPoolSize(5);
        // 设置队列容量（用于存放等待执行的任务）
        executor.setQueueCapacity(10);
        // 设置线程名的前缀，便于日志调试
        executor.setThreadNamePrefix("MyAsync-");
        // 设置非核心线程的空闲存活时间（秒）
        executor.setKeepAliveSeconds(60);
        // 【核心】设置自定义拒绝策略
        executor.setRejectedExecutionHandler(new MyCustomRejectionHandler());
        // 初始化线程池
        executor.initialize();
        return executor;
    }
}
