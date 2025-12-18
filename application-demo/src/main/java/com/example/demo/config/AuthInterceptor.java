package com.example.demo.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



/**
 * @author markchou
 * @createtime 2025/12/15
 */
@Component
public class AuthInterceptor implements HandlerInterceptor{

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 在Controller方法执行前调用，常用于登录校验、权限验证
//        String token = request.getHeader("Authorization");
//        if (StringUtils.isBlank(token)) {
//            response.setStatus(500);
//            return false; // 请求中断
//        }
        return true; // 继续执行
    }
}
