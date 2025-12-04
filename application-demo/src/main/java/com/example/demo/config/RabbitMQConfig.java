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

    @Bean
    public Queue topicQueue1() {
        return new Queue("topic.queue1", true);
    }

    @Bean
    public Queue topicQueue2() {
        return new Queue("topic.queue2", true);
    }

    // 从配置文件中读取交换机名称，这里我们使用一个新的Topic交换机

    private String topicExchangeName;

    // 声明多个队列，用于不同的路由模式
    @Bean
    public Queue topicQueueInfo() {
        return QueueBuilder.durable("demo.topic.queue.info").build();
    }

    @Bean
    public Queue topicQueueError() {
        return QueueBuilder.durable("demo.topic.queue.error").build();
    }

    @Bean
    public Queue topicQueueAllLogs() {
        return QueueBuilder.durable("demo.topic.queue.all").build();
    }

    // 声明一个持久化的Topic交换机
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange("demo.topic.exchange", true, false);
    }

    // 绑定：处理所有以 ".info" 结尾的路由键消息（如：order.created.info）
    @Bean
    public Binding bindingInfo() {
        return BindingBuilder.bind(topicQueueInfo()).to(topicExchange()).with("*.info");
    }

    // 绑定：处理所有以 ".error" 结尾的路由键消息（如：payment.process.error）
    @Bean
    public Binding bindingError() {
        return BindingBuilder.bind(topicQueueError()).to(topicExchange()).with("*.error");
    }

    // 绑定：处理所有 "log" 开头的消息（如：log, log.app, log.app.error 等）
    @Bean
    public Binding bindingAllLogs() {
        return BindingBuilder.bind(topicQueueAllLogs()).to(topicExchange()).with("log.#");
    }
}
