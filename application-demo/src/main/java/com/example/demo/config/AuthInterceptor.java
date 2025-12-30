package com.example.demo.config;

import com.example.demo.threadLocal.UserContext;
import com.example.demo.threadLocal.UserSession;
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
        // 从请求头中获取用户信息（示例，实际可能从Token解析）
        String userId = request.getHeader("X-User-Id");
        String username = request.getHeader("X-Username");
        if (userId != null && username != null) {
            // 将用户信息存入ThreadLocal
            UserContext.setCurrentUser(new UserSession(userId, username));
        }
        return true;
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求处理完毕后，无论如何都要清理ThreadLocal
        UserContext.clear();
    }
}
