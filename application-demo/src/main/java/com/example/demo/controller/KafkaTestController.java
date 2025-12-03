package com.example.demo.controller;

import com.example.demo.service.KafkaProducerService;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/kafka")
public class KafkaTestController {

    private final KafkaProducerService kafkaProducer;

    public KafkaTestController(KafkaProducerService kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    /**
     * 发送用户事件
     */
    @PostMapping("/user-event")
    public ResponseEntity<String> sendUserEvent(
            @RequestParam String key,
            @RequestParam String message) {

        kafkaProducer.sendUserEvent(key, message);
        return ResponseEntity.ok("用户事件消息已发送");
    }

    /**
     * 发送订单事件（异步）
     */
    @PostMapping("/order-event")
    public CompletableFuture<ResponseEntity<String>> sendOrderEvent(
            @RequestParam String key,
            @RequestParam String message) {

        return kafkaProducer.sendOrderEvent(key, message)
                .thenApply(result -> ResponseEntity.ok("订单事件消息已发送"))
                .exceptionally(ex -> ResponseEntity.badRequest().body("发送失败: " + ex.getMessage()));
    }

    /**
     * 发送通知消息
     */
    @PostMapping("/notification")
    public CompletableFuture<ResponseEntity<String>> sendNotification(@RequestParam String message) {
        return kafkaProducer.sendNotification(message)
                .thenApply(result -> ResponseEntity.ok("通知消息已发送"))
                .exceptionally(ex -> {
                    System.err.println("发送失败: " + ex.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("发送失败: " + ex.getMessage());
                });
    }

    @PostMapping("/notification/batch")
    public CompletableFuture<ResponseEntity<String>> sendBatchNotifications(@RequestParam String messageContent, @RequestParam int count) {
        List<CompletableFuture<SendResult<String, String>>> futures = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String individualMessage = String.format("[%d] %s", i, messageContent);
            futures.add(kafkaProducer.sendNotification(individualMessage));
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> ResponseEntity.ok("批量通知消息已发送，数量: " + count))
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("发送失败: " + ex.getMessage()));
    }
}
