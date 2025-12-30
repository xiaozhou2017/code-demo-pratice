package com.example.demo.controller;


import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.example.demo.entity.Product;
import com.example.demo.service.ProductService;

import com.example.demo.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Es 接口相关", description = "用户相关的增删改查接口") // 接口模块标签

@Slf4j
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private StockService stockService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "创建新产品")
    @SentinelResource(value = "getUserById", blockHandler = "handleFlowException")
    public ResponseEntity<Product> createProduct(
            @RequestBody Product product) {
        Product saved = productService.save(product);
        return ResponseEntity.ok(saved);
    }
    // 降级/限流处理方法
    public ResponseEntity handleFlowException(Long id, BlockException ex) {
        return  ResponseEntity.ok("触发熔断，服务暂时不可用");
    }
    @GetMapping
    public ResponseEntity<Iterable<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取用户", description = "通过用户的唯一ID来查询其详细信息")
    public ResponseEntity<Product> getProduct(@PathVariable String id) {
        Optional<Product> product = productService.findById(id);
        return product.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<Iterable<Product>> searchProducts(@RequestParam String name) {
        return ResponseEntity.ok(productService.findByName(name));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Service is running");
    }


    @PostMapping("/order")
    public ResponseEntity<Boolean> order(@RequestParam String id,
                                         @RequestParam Integer quantity) {
        boolean result = stockService.decreaseStock(id, quantity);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/run-task")
    public CompletableFuture<String> runTask() {
        // 调用异步方法
        return productService.executeLongRunningTask("TASK-001");
    }

    @GetMapping("/run-batch-tasks")
    public CompletableFuture<Map<String, Object>> runBatchTasks(
            @RequestParam(defaultValue = "10") int count) {
        return productService.submitBatchTasks(count);
    }

    /**
     * 查看线程池状态
     */
    @GetMapping("/pool-status")
    public Map<String, Object> getPoolStatus() {
        return productService.getThreadPoolStatus();
    }

    /**
     * 压力测试 - 快速提交大量任务
     */
    @GetMapping("/stress-test")
    public CompletableFuture<Map<String, Object>> stressTest(
            @RequestParam(defaultValue = "20") int count) {
        log.info("🔥 开始压力测试，提交 {} 个任务", count);

        List<CompletableFuture<String>> futures = new ArrayList<>();
        Map<String, Object> result = new HashMap<>();

        // 快速提交，不等待间隔
        for (int i = 1; i <= count; i++) {
            String taskId = "STRESS-TASK-" + i;
            try {
                CompletableFuture<String> future = productService.executeLongRunningTask(taskId);
                futures.add(future);
            } catch (Exception e) {
                log.error("提交任务失败: {}", taskId, e);
            }
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> {

                    result.put("totalSubmitted", futures.size());
                    result.put("message", "压力测试完成，观察控制台日志查看拒绝策略效果");
                    return result;
                });
    }
}
