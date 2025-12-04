package com.example.demo.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${myapp.rabbitmq.queue}")
    private String queueName;

    @Value("${myapp.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${myapp.rabbitmq.routing-key}")
    private String routingKey;

    // 声明一个持久化的直连交换机
    @Bean
    public DirectExchange demoExchange() {
        return new DirectExchange(exchangeName, true, false);
    }

    // 声明一个持久化的队列
    @Bean
    public Queue demoQueue() {
        return QueueBuilder.durable(queueName).build();
    }

    // 将队列与交换机绑定，并指定路由键
    @Bean
    public Binding binding() {
        return BindingBuilder.bind(demoQueue()).to(demoExchange()).with(routingKey);
    }
}
