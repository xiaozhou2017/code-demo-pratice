package com.example.demo.config;

import com.github.f4b6a3.uuid.UuidCreator;
import java.util.UUID;

public class UuidV7Utils {
    /**
     * 生成一个UUIDv7字符串
     */
    public static String generate() {
        return UuidCreator.getTimeOrderedEpoch().toString();
    }

    /**
     * 生成一个UUIDv7对象
     */
    public static UUID generateUuid() {
        return UuidCreator.getTimeOrderedEpoch();
    }
}
