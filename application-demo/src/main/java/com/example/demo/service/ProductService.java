package com.example.demo.service;
import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class ProductService {
    private final AtomicInteger taskCounter = new AtomicInteger(0);
    private final AtomicInteger rejectedCounter = new AtomicInteger(0);
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    @Qualifier("myTaskExecutor")
    private ThreadPoolTaskExecutor executor;

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public Optional<Product> findById(String id) {
        return productRepository.findById(id);
    }

    public Iterable<Product> findAll() {
        return productRepository.findAll();
    }

    public List<Product> findByName(String name) {
        return productRepository.findByName(name);
    }

    public List<Product> findByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public void deleteById(String id) {
        productRepository.deleteById(id);
    }

    // 使用我们自定义的线程池Bean
    @Async("myTaskExecutor")
    public CompletableFuture<String> executeLongRunningTask(String taskId) {
        try {
            // 模拟一个耗时操作
            Thread.sleep(2000);
            String result = "任务 " + taskId + " 执行完成";
            int currentTaskNum = taskCounter.incrementAndGet();
            log.info("🎯 开始执行任务 {}，当前任务序号: {}，执行线程: {}", taskId, currentTaskNum, Thread.currentThread().getName());

            return CompletableFuture.completedFuture(result);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return CompletableFuture.failedFuture(e);
        }
    }
    /**
     * 批量提交任务来触发拒绝策略
     */
    public CompletableFuture<Map<String, Object>> submitBatchTasks(int taskCount) {
        log.info("🚀 开始批量提交 {} 个任务", taskCount);

        List<CompletableFuture<String>> futures = new ArrayList<>();
        Map<String, Object> result = new HashMap<>();
        // 从应用上下文中获取代理对象
        ProductService proxy = applicationContext.getBean(ProductService.class);
        // 记录开始时间
        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= taskCount; i++) {
            String taskId = "BATCH-TASK-" + i;
            try {
                CompletableFuture<String> future = proxy.executeLongRunningTask(taskId);
                futures.add(future);
                log.info("📤 已提交任务: {}", taskId);
            } catch (Exception e) {
                if (e.getCause() instanceof RejectedExecutionException) {
                    int rejectedCount = rejectedCounter.incrementAndGet();
                    log.warn("⛔ 任务 {} 被拒绝策略拦截！拒绝计数: {}", taskId, rejectedCount);
                }
            }

//            // 稍微延迟一下，模拟真实场景的任务提交间隔
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
        }

        // 等待所有任务完成（或部分完成）
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    long endTime = System.currentTimeMillis();
                    long totalTime = endTime - startTime;

                    result.put("totalTasks", taskCount);
                    result.put("submittedTasks", futures.size());
                    result.put("rejectedTasks", rejectedCounter.get());
                    result.put("totalTime", totalTime + "ms");
                    result.put("completedTasks", futures.stream()
                            .filter(CompletableFuture::isDone)
                            .count());

                    log.info("📊 批量任务执行统计: {}", result);
                    return result;
                });
    }

    /**
     * 获取当前线程池状态
     */
    public Map<String, Object> getThreadPoolStatus() {
        ThreadPoolExecutor threadPoolExecutor = executor.getThreadPoolExecutor();

        Map<String, Object> status = new HashMap<>();
        status.put("activeTasks", taskCounter.get());
        status.put("rejectedTasks", rejectedCounter.get());
        status.put("poolSize", threadPoolExecutor.getPoolSize());
        status.put("activeCount", threadPoolExecutor.getActiveCount());
        status.put("queueSize", threadPoolExecutor.getQueue().size());
        status.put("completedTasks", threadPoolExecutor.getCompletedTaskCount());

        return status;
    }
}
