package com.example.orderserviceconsumer;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class OrderServiceConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceConsumerApplication.class, args);
    }

}
