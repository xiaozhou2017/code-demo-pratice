package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.Arrays;

// 首先将Lua脚本字符串定义并注册为Spring Bean
@Component
public class StockService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    // 定义Lua脚本
    private static final String STOCK_DECREASE_SCRIPT =
            "local current_stock = tonumber(redis.call('GET', KEYS[1]))\n" +
                    "if not current_stock then return nil end\n" +
                    "if current_stock >= tonumber(ARGV[1]) then\n" +
                    "    return redis.call('DECRBY', KEYS[1], ARGV[1])\n" +
                    "else\n" +
                    "    return -1\n" +
                    "end";

    public boolean decreaseStock(String productId, int quantity) {
        RedisScript<Long> script = new DefaultRedisScript<>(STOCK_DECREASE_SCRIPT, Long.class);
        Long result = redisTemplate.execute(script, Arrays.asList("stock:" + productId), String.valueOf(quantity));

        if (result != null) {
            if (result == -1) {
                System.out.println("库存不足");
                return false;
            } else {
                System.out.println("扣减成功，剩余库存：" + result);
                return true;
            }
        } else {
            System.out.println("商品库存信息不存在");
            return false;
        }
    }
}
