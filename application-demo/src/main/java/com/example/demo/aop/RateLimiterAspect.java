package com.example.demo.aop;

import com.example.demo.config.RateLimiter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class RateLimiterAspect {

    @Autowired
    private RedissonClient redissonClient;

    @Around("@annotation(rateLimiter)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimiter rateLimiter) throws Throwable {
        // 1. 生成限流器的Key，支持SpEL表达式动态解析
        String limitKey = generateLimitKey(joinPoint, rateLimiter);

        // 2. 获取限流器实例
        RRateLimiter rRateLimiter = redissonClient.getRateLimiter(limitKey);

        // 3. 初始化限流策略（仅在首次设置生效）
        long rateIntervalInMillis = rateLimiter.timeUnit().toMillis(rateLimiter.time());
        rRateLimiter.trySetRate(rateLimiter.rateType(), rateLimiter.count(),
                rateIntervalInMillis, RateIntervalUnit.MILLISECONDS);

        // 4. 尝试获取令牌
        boolean acquired;
        if (rateLimiter.timeout() > 0) {
            acquired = rRateLimiter.tryAcquire(1, rateLimiter.timeout(), TimeUnit.MILLISECONDS);
        } else {
            acquired = rRateLimiter.tryAcquire(1);
        }

        if (!acquired) {
            // 可根据需要返回自定义结果或抛出异常
            return "请求过于频繁，请稍后再试";
        }

        // 5. 执行目标方法
        return joinPoint.proceed();
    }

    /**
     * 生成限流Key，支持SpEL表达式解析
     */
    private String generateLimitKey(ProceedingJoinPoint joinPoint, RateLimiter rateLimiter) {
        String key = rateLimiter.key();

        // 如果key包含SpEL表达式（如#userId），则进行解析
        if (key.contains("#")) {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            Object[] args = joinPoint.getArgs();
            String[] paramNames = signature.getParameterNames();

            StandardEvaluationContext context = new StandardEvaluationContext();
            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }

            ExpressionParser parser = new SpelExpressionParser();
            Expression expression = parser.parseExpression(key);
            key = expression.getValue(context, String.class);
        }

        return "rate_limit:" +
                joinPoint.getSignature().getDeclaringTypeName() + ":" +
                joinPoint.getSignature().getName() + ":" +
                key;
    }
}
