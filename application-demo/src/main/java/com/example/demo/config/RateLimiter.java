package com.example.demo.config;

import org.redisson.api.RateType;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author markchou
 * @createtime 2026/1/16
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {
    String key() default "rate_limit";
    long time() default 1;
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    long count() default 1;
    long timeout() default 0;
    RateType rateType() default RateType.OVERALL;
}
