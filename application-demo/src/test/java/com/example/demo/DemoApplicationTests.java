package com.example.demo;
import com.example.demo.entity.PayAccountGroupEntity;
import com.example.demo.entity.SysUser;
import com.example.demo.repository.PayAccountGroupRepository;
import com.example.demo.service.ISysUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

//@RunWith(SpringRunner.class) // 对于 JUnit 4
//@SpringBootTest // 启动 Spring 上下文
//@SpringBootTest
@ExtendWith(MockitoExtension.class)
class DemoApplicationTests {

    @Test
    void contextLoads() {
        System.out.println("hello world");
        test();
    }
    @Test
    public void test(){
        int[] arr = {5, 3, 8, 4, 2, 7, 1, 6};
        String[] strArr = {"ac", "adx", "asx", "cx", "bx", "dx", "vx", "ex"};
        int[] reversed = IntStream.of(arr)
                .boxed()
                .sorted((a, b) -> a - b)  // 自定义比较器实现倒序
                .mapToInt(Integer::intValue)
                .toArray();
        System.out.println("一行代码: " + Arrays.toString(reversed));
        Arrays.sort(strArr);
        System.out.println("1. 升序排序: " + Arrays.toString(strArr));
        // 方法1：Stream倒序（推荐）
        String[] method1 = Arrays.stream(strArr)
                .sorted((a, b) -> b.compareTo(a))
                .toArray(String[]::new);
        System.out.println("方法1 - Stream倒序: " + Arrays.toString(method1));


    }

    @Test
    public void testReflex() throws Exception {
        Class<?> sysUserClass = Class.forName("com.example.demo.entity.SysUser");
        // 2. 创建实例
        Object sysUser = sysUserClass.getDeclaredConstructor().newInstance();
        System.out.println("创建实例: " + sysUser);
        // 3. 设置属性值
        setProperty(sysUser, "id_", 1L);
        setProperty(sysUser, "account_", "admin");
        setProperty(sysUser, "user_name", "张三");
        setProperty(sysUser, "email_", "admin@example.com");
        setProperty(sysUser, "phone_", "13800138000");
        setProperty(sysUser, "sex_", 1);
        setProperty(sysUser, "birth_day", LocalDate.of(1990, 1, 1));
        setProperty(sysUser, "create_time", LocalDateTime.now());
        setProperty(sysUser, "money", 1000.50);


        Method toStringMethod = sysUserClass.getMethod("toString");
        String str = (String) toStringMethod.invoke(sysUser);
        System.out.println(" systemUser.getName():"+ str);
        Method pkVal = sysUserClass.getDeclaredMethod("pkVal");
        int modifiers = pkVal.getModifiers();
        System.out.println("修饰符: " + Modifier.toString(modifiers));
        System.out.println("是否是public: " + Modifier.isPublic(modifiers));
        System.out.println("是否是protected: " + Modifier.isProtected(modifiers));
        System.out.println("是否是private: " + Modifier.isPrivate(modifiers));
        System.out.println("是否需要setAccessible: " + !Modifier.isPublic(modifiers));
        pkVal.setAccessible(true);
        Serializable invoke = (Serializable)pkVal.invoke(sysUser);

        System.out.println(" systemUser.invoke():"+ invoke);


    }
    /**
     * 设置属性值
     */
    private  void
    setProperty(Object obj, String fieldName, Object value) throws Exception {
        Class<?> clazz = obj.getClass();

        // 获取字段
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);

        // 设置值
        field.set(obj, value);

        System.out.println("设置属性: " + fieldName + " = " + value);
    }
    @Test
    public void testlocalthread(){

            ExecutorService executor = Executors.newFixedThreadPool(2);

            for (int i = 0; i < 5; i++) {
                executor.execute(() -> {
                    System.out.println(Thread.currentThread().getName() + " 执行任务");
                });
            }

            executor.shutdown();

    }
    @Mock
    private ISysUserService service;
    @Mock
    private PayAccountGroupRepository payAccountGroupService;

    @Test
    public void testlocalthreadSubmit() throws ExecutionException, InterruptedException {

        // 模拟行为
        SysUser mockUser = new SysUser();
        // 设置mockUser的属性
        when(service.selectById(anyString())).thenReturn(mockUser);
        PayAccountGroupEntity mockGroup = new PayAccountGroupEntity();
        when(payAccountGroupService.getById(anyLong())).thenReturn(mockGroup);
        int corePoolSize = 2;
        int maximumPoolSize = 4;
        long keepAliveTime = 10;
        TimeUnit unit = TimeUnit.SECONDS;
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(2);
        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();

        ExecutorService executor = new ThreadPoolExecutor(
                corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler
        );

//        List<Callable<Object>> tasks = new ArrayList<>();
        List<Callable<Object>> tasks = Arrays.asList(
                () -> service.selectById("1"),
                () -> payAccountGroupService.getById(1L)
        );
        List<Future<Object>> futures = executor.invokeAll(tasks, 5, TimeUnit.SECONDS);

        // 获取结果
        SysUser sysUser = (SysUser) futures.get(0).get();
        PayAccountGroupEntity payAccountGroup = (PayAccountGroupEntity) futures.get(1).get();

        System.out.println("用户信息: " + sysUser);
        System.out.println("账户组信息: " + payAccountGroup);

        executor.shutdown();

    }






}
