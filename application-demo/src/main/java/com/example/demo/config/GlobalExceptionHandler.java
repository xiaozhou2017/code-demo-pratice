package com.example.demo.config;

import com.example.demo.bean.ResponseData;
import com.example.demo.bean.ResponseUtil;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author markchou
 * @createtime 2025/12/15
 */
//@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseData handleException(Exception e) {
        // 记录日志等
        return ResponseUtil.failure("系统错误");
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseData handleBusinessException(RuntimeException e) {
        // 记录日志等
        return ResponseUtil.failure("自定义错误");
    }
}
