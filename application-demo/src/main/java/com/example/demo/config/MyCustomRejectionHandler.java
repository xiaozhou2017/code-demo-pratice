package com.example.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

@Component
@Slf4j
public class MyCustomRejectionHandler implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        // 1. 记录详细的警告日志，包括任务信息和线程池状态
        log.warn("任务被执行器拒绝. 任务: {}, 活动线程数: {}, 已完成任务数: {}, 队列大小: {}",
                r.toString(),
                executor.getActiveCount(),
                executor.getCompletedTaskCount(),
                executor.getQueue().size());

        // 2. 示例：将关键任务信息存入Redis或数据库，以便后续通过定时任务重试[1](@ref)
        // saveTaskToRedisForRetry(r);

        // 3. Fallback 处理：这里示例由调用者线程执行一个简化版的逻辑
        // 注意：复杂的任务可能不适合在此处直接运行，以免阻塞提交线程
        log.info("由调用者线程执行降级逻辑。");
        // 此处可以调用一个降级服务的方法
        // degradationService.executeFallback(r);

        // 或者，如果任务非常关键且不能丢失，可以尝试等待一段时间后重新放入队列（需谨慎，防止循环阻塞）
        // try {
        //     executor.getQueue().offer(r, 60, TimeUnit.SECONDS);
        // } catch (InterruptedException e) {
        //     Thread.currentThread().interrupt();
        //     log.error("重试放入任务队列时被中断", e);
        // }
    }
}
