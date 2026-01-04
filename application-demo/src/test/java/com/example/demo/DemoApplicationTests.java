package com.example.demo;

import com.example.demo.config.JwtUtils;
import com.example.demo.config.UuidV7Utils;
import com.example.demo.controller.DemoController;
import com.example.demo.entity.PayAccountGroupEntity;
import com.example.demo.entity.SysUser;
import com.example.demo.repository.PayAccountGroupRepository;
import com.example.demo.service.ISysUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//@RunWith(SpringRunner.class) // 对于 JUnit 4
//@SpringBootTest // 启动 Spring 上下文
//@SpringBootTest
@ExtendWith(MockitoExtension.class)
class DemoApplicationTests {

    private MockMvc mockMvc;

    @Mock // 关键：如果你的Controller依赖JwtUtils，你需要模拟它
    private JwtUtils jwtUtils;

    @InjectMocks
    private DemoController demoController; // 被测试的Controller，JwtUtils会被注入

    @BeforeEach
    public void setup() {
        // 构建MockMvc实例，只配置这一个控制器
        ReflectionTestUtils.setField(demoController, "jwtUtils", jwtUtils);
        mockMvc = MockMvcBuilders.standaloneSetup(demoController).build();
    }

    @Test
    public void testToken() throws Exception {
        // 1. 为JwtUtils的模拟对象设定行为
        when(jwtUtils.generateAccessToken(anyMap())).thenReturn("mock-access-token");
        when(jwtUtils.generateRefreshToken(anyMap())).thenReturn("mock-refresh-token");

        // 2. 使用MockMvc模拟HTTP请求，而不是直接调用Controller方法
        MvcResult mvcResult = mockMvc.perform(get("/api/redis/token")
                .param("username", "zhouwenuwen"))
                .andDo(print()) // 打印请求详细信息，有助于调试
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("mock-access-token"))
                .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();    // 2. 进行任意自定义验证
        String content = response.getContentAsString();

        System.out.println("结果："+content);

        assertThat(content).contains("accessToken");


    }
    @Test
    public void test() {
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
        System.out.println(" systemUser.getName():" + str);
        Method pkVal = sysUserClass.getDeclaredMethod("pkVal");
        int modifiers = pkVal.getModifiers();
        System.out.println("修饰符: " + Modifier.toString(modifiers));
        System.out.println("是否是public: " + Modifier.isPublic(modifiers));
        System.out.println("是否是protected: " + Modifier.isProtected(modifiers));
        System.out.println("是否是private: " + Modifier.isPrivate(modifiers));
        System.out.println("是否需要setAccessible: " + !Modifier.isPublic(modifiers));
        pkVal.setAccessible(true);
        Serializable invoke = (Serializable) pkVal.invoke(sysUser);

        System.out.println(" systemUser.invoke():" + invoke);


    }

    /**
     * 设置属性值
     */
    private void
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
    public void testlocalthread() {

        ExecutorService executor = Executors.newFixedThreadPool(2, r -> {
            Thread t=new Thread(r);
            t.setName("wodexaincheng");
            t.setDaemon(true);
            t.setPriority(1);
            return t;
        });

        for (int i = 0; i < 5; i++) {
            executor.execute(() -> {
                System.out.println(Thread.currentThread().getName() + " 执行任务");
            });
        }
        executor.shutdown();

    }

    @Test
    public void testThread() throws ExecutionException, InterruptedException {

//        Thread t3 =new Thread(()->{
//            try {
//                t1.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            System.out.println("当前线程名T3: "+Thread.currentThread().getName());
//        });
//
//        Thread t2 =new Thread(()->{
//            try {
//                t1.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            System.out.println("当前线程名T2: "+Thread.currentThread().getName());
//        });
//        t3.start();
//
//        t1.start();
//        t2.start();

//        BlockingQueue blockingQueue=new ArrayBlockingQueue(2);
//        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 2, 1000, TimeUnit.HOURS, blockingQueue
//        );
//        threadPoolExecutor.submit(()->{
//            System.out.println(Thread.currentThread().getName());
//        });
//        threadPoolExecutor.shutdown();
//
//
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("当前线程名2: " + Thread.currentThread().getName());
//            }
//        };
//        Thread t2= new Thread(runnable,"task xiancheng");
//        t2.start();
//
//
//        FutureTask<Object> futureTask = new FutureTask<>(()->"测试");
//        Thread thread=new Thread(futureTask);
//        thread.start();
//        Object o = futureTask.get();
//        Thread thread3=new Thread(()->{
//            System.out.println("异步任务结果: 12312");
//        });
//        thread3.start();
//        System.out.println("异步任务结果: " + JSONObject.toJSONString(o));

        ThreadFactory factory = r -> {
            Thread thread = new Thread(r);
            thread.setName("我的ThreadPoolExecutor线程---");
            return thread;
        };
//        ExecutorService executor = Executors.newFixedThreadPool(2, factory);
        ExecutorService executor = Executors.newSingleThreadExecutor( factory);
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 2,
                3, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10), factory);
        Runnable simpleTask = () -> {
            System.out.println("执行Runnable任务");
        };

        Callable<Object> callable = Executors.callable(simpleTask);

        Future<Object> submit = executor.submit(callable);

        submit.get();

        CompletableFuture<String> stringCompletableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程: " + Thread.currentThread().getName()); // 打印线程名
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "xiao ming";
        }, threadPoolExecutor).completeOnTimeout("超时", 500, TimeUnit.MILLISECONDS);

        CompletableFuture<String> stringCompletableFuture1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程: " + Thread.currentThread().getName()); // 打印线程名
            return " love ";
        },threadPoolExecutor);
        CompletableFuture<String> stringCompletableFuture2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程: " + Thread.currentThread().getName()); // 打印线程名
            return "xiao hua";
        },threadPoolExecutor);

        CompletableFuture<String> finalResult = stringCompletableFuture.
                thenCombine(stringCompletableFuture1, (name1, name2) -> name1 + name2).thenCombine(stringCompletableFuture2, (name1, name2) -> name1 + name2);
        System.out.println(finalResult.join());

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


    @Test
    public void generateBatchUuid() {
        // 批量生成10个UUIDv7，观察其有序性
        List<String> uuidList = IntStream.range(0, 10)
                .mapToObj(i -> UuidV7Utils.generate())
                .collect(Collectors.toList());
        System.out.println("生成的UUIDv7列表（按时间有序）：");
        System.out.println("==================================================");
        for (int i = 0; i < uuidList.size(); i++) {
            System.out.printf("%2d: %s%n", i + 1, uuidList.get(i));
            System.out.flush();
        }

        System.out.println("==================================================");
        System.out.flush();
        // 验证有序性：提取时间戳部分进行简单分析
        if (uuidList.size() > 1) {
            System.out.println("有序性分析：");
            String first = uuidList.get(0);
            String last = uuidList.get(uuidList.size() - 1);

            // UUIDv7的前缀是时间戳，可以直接比较字符串顺序
            int comparison = first.compareTo(last);
            if (comparison < 0) {
                System.out.println("✓ UUIDv7 严格按时间递增");
            }

            // 显示前几个字符的时间戳部分对比
            System.out.printf("首UUID前缀: %s%n", first.substring(0, 8));
            System.out.printf("尾UUID前缀: %s%n", last.substring(0, 8));
        }
//
//        return uuidList;
    }
}
