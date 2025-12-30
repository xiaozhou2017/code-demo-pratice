package com.example.demo.threadLocal;

/**
 * @author markchou
 * @createtime 2025/12/25
 */
public class UserContext {
    public static final  ThreadLocal<UserSession> threadLocal=new ThreadLocal<>();

    public static void setCurrentUser(UserSession user) {
        threadLocal.set(user);
    }

    public static UserSession getCurrentUser() {
        return threadLocal.get();
    }
    // 最关键的一步：防止内存泄漏和数据污染
    public static void clear() {
        threadLocal.remove();
    }
}
