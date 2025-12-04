package com.example.demo.aop;


import com.alibaba.fastjson.JSONObject;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.UUID;

/**
 *@author markchou
 *@createtime 2025/11/12
 */
@Component
@Aspect
public class WebControllerAOP {
    private static final Logger logger = LoggerFactory.getLogger(WebControllerAOP.class);
    @Pointcut(value="execution(* com.example.demo.controller.*.*(..))")
    public void excuteService(){

    };

    @Around(value="excuteService()")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        // 生成请求ID
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("requestId", requestId);

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();
        Enumeration<String> headerNames = request.getHeaderNames();
        // 遍历所有头名称
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            // 根据头名称获取其对应的值
            String headerValue = request.getHeader(headerName);
            // 打印到头名称和值
            logger.info(headerName + ": " + headerValue);
        }

        HttpServletResponse response = sra.getResponse();

        String requestURI = request.getRequestURI();
        String remoteAddr = request.getRemoteAddr();

        // 记录请求信息
        logger.info("[请求开始] ID: {}", requestId);
        logger.info("请求路径: {}", requestURI);
        logger.info("客户端IP: {}", remoteAddr);
        logger.info("方法: {}.{}",
                signature.getDeclaringType().getSimpleName(),
                signature.getMethod().getName());
        logger.info("参数: {}", JSONObject.toJSONString(request.getParameterMap()));

        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();

        // 记录响应信息
        if (response != null) {
            logger.info("响应状态: {}", response.getStatus());
        }

        if (result != null) {
            String resultJson = JSONObject.toJSONString(result);
            logger.info("响应内容: {}", resultJson);
            logger.info("响应大小: {} 字符", resultJson.length());
        }

        logger.info("[请求完成] ID: {}, 耗时: {}ms", requestId, endTime - startTime);

        // 清理MDC
        MDC.remove("requestId");

        return result;
    }

    private String truncateIfNeeded(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "...";
    }
}
