package com.example.demo.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * <p>
 * 用户管理 服务类
 * </p>
 *
 * @author mark zhou
 * @since 2025-11-12
 */
@FeignClient(name = "user-service-consumer",fallback = UserServiceConsumerClientFallback.class)
public interface UserServiceConsumerClient  {

    /**
     * 调用 user-service-consumer 的 /order/user/{id} 接口
     * 路径和方法签名需要与被调用方保持一致
     */
    @GetMapping("/order/user/{id}")
    String getOrderWithUser(@PathVariable("id") Long id);

}
