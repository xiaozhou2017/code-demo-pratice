package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.example.demo.bean.ResponseData;
import com.example.demo.bean.ResponseUtil;
import com.example.demo.config.BloomFilterConfig;
import com.example.demo.config.JwtUtils;
import com.example.demo.entity.PayAccountGroupEntity;
import com.example.demo.entity.SysUser;
import com.example.demo.repository.PayAccountGroupRepository;
import com.example.demo.service.ISysUserService;
import com.example.demo.service.UserServiceConsumerClient;
import com.example.demo.service.impl.SysUserServiceImpl;
import com.example.demo.threadLocal.UserContext;
import com.example.demo.threadLocal.UserSession;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author markchou
 * @createtime 2025/11/11
 */
@RestController
@Tag(name = "用户管理", description = "用户相关的增删改查接口")
@RequestMapping("/api/redis")
@RequiredArgsConstructor
@Slf4j
public class DemoController {


    private final ISysUserService service;
    private final PayAccountGroupRepository payAccountGroupService;
    private final SysUserServiceImpl serviceiml;
    private final RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/api/redirect")
    public RedirectView redirectToRedissonWithView() {
        RedirectView redirectView = new RedirectView("http://localhost:8082/api/redis/api/redisson");
        // 可以设置为 301（永久重定向）或 302（临时重定向）
        // redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY); // 301
        return redirectView;
    }

    @GetMapping("/api")
    @Operation(summary = "根据用户ID获取用户信息", description = "传入用户ID，返回对应的用户详细信息")
    public ResponseData request() throws Exception {

        UserSession user = UserContext.getCurrentUser();
        System.out.println("当前session 用户 "+JSONObject.toJSONString(user));
        EntityWrapper<SysUser> userWrapper = new EntityWrapper<>();
        userWrapper.eq("account_", "admin");
        Object redisUser = redisTemplate.opsForValue().get("test:sysPageList1");
        if(redisUser!=null){
            ObjectMapper objectMapper=new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            SysUser sysUser = objectMapper.readValue(JSONObject.toJSONString(redisUser), SysUser.class);

            return ResponseUtil.success(sysUser);
        }
        SysUser SysUser = service.selectOne(userWrapper);
//        Page<SysUser> page = new Page<>(0, 10);
//        Page<SysUser> sysUserPage = service.selectPage(page);
        redisTemplate.opsForValue().set("test:sysPageList1",SysUser,5, TimeUnit.HOURS);


        return ResponseUtil.success(SysUser);
    }
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    BloomFilterConfig bloomFilterConfig;
    @GetMapping("/api/redisson")
    @Operation(summary = "根据用户ID获取用户信息", description = "传入用户ID，返回对应的用户详细信息")
    public ResponseData redissonRequest(@RequestParam String userId) {

        // 1. 使用布隆过滤器进行前置校验
        RBloomFilter<String> bloomFilter = bloomFilterConfig.getUserBloomFilter();

        // 如果用户账号肯定不存在于系统，直接返回，防止缓存穿透
        if (!bloomFilter.contains(userId)) {
            log.warn("用户账号不存在，被布隆过滤器拦截: {}", userId);
            return ResponseUtil.failure("用户不存在");
        }

        EntityWrapper<SysUser> userWrapper = new EntityWrapper<>();
        userWrapper.eq("account_", userId);

        // 2. 使用Redisson的RBucket接口获取缓存
        String cacheKey = "user:info:" + userId;
        RBucket<SysUser> userBucket = redissonClient.getBucket(cacheKey);
        SysUser redisUser = userBucket.get();

        if (redisUser != null) {
            // Redisson自动处理序列化，无需手动JSON转换
            log.debug("从缓存中获取用户信息: {}", userId);
            return ResponseUtil.success(redisUser);
        }

        // 3. 从数据库查询
        log.debug("缓存未命中，查询数据库: {}", userId);
        SysUser sysUser = service.selectOne(userWrapper);

        if (sysUser == null) {
            // 数据库也不存在，缓存空值（短时间），防止同一恶意请求重复攻击
            userBucket.set(new SysUser(), 5, TimeUnit.MINUTES); // 缓存空对象5分钟
            log.warn("用户不存在，已缓存空值: {}", userId);
            return ResponseUtil.failure("用户不存在");
        }

        // 4. 将真实用户信息存入缓存
        userBucket.set(sysUser, 5, TimeUnit.HOURS);

        return ResponseUtil.success(sysUser);
    }
    @GetMapping("/xnll")
    @Operation(summary = "根据用户ID获取用户信息", description = "传入用户ID，返回对应的用户详细信息")
    public ResponseData xnll() throws JsonProcessingException {

        List<PayAccountGroupEntity> cachedList = (List<PayAccountGroupEntity>)
                redisTemplate.opsForValue().get("test:PayAccountGroupEntity");

        if (cachedList != null && !cachedList.isEmpty()) {
            return ResponseUtil.success(cachedList);
        }
        Specification<PayAccountGroupEntity> spec = (root, query, cb) ->
                cb.equal(root.get("remark"), "1");
//        List<PayAccountGroupEntity> all = payAccountGroupService.findAll();
//        Optional<PayAccountGroupEntity> admin = all;
        Page<PayAccountGroupEntity> page = new Page<>(0, 10);
        PageRequest createTime = PageRequest.of(0, 10, Sort.by("createTime").descending());
        org.springframework.data.domain.Page<PayAccountGroupEntity> all1 = payAccountGroupService.findAll(spec, createTime);
        redisTemplate.opsForValue().set("test:PayAccountGroupEntity",all1.getContent(),15, TimeUnit.SECONDS);

        return ResponseUtil.success(all1.getContent());
    }


    @Autowired
    JwtUtils jwtUtils;
    @GetMapping("/token")
    @Operation(summary = "获取token", description = "获取token")
    public ResponseData token(@RequestParam String username) {

        // 生成 Token
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username); // 放入用户名
        String accessToken = jwtUtils.generateAccessToken(claims);
        String refreshToken = jwtUtils.generateRefreshToken(claims);
        HashMap map=new HashMap();
        map.put("accessToken",accessToken);
        map.put("refreshToken",refreshToken);
        map.put("tokenType", "Bearer");
        map.put("expiresIn", "3600000"); // Access Token过期秒数

        return ResponseUtil.success(map);
    }


    @GetMapping("/validationToken")
    @Operation(summary = "validationToken", description = "validationToken")
    public ResponseData validationToken(@RequestParam String token) {

        // 生成 Token
        if (token == null) {
            return ResponseUtil.failure("401");
        }
        Claims claims = jwtUtils.validateAccessToken(token);

        if (claims != null) {
            // 令牌有效
            return ResponseUtil.success();
        } else {
            // 令牌无效（已由JwtUtils记录具体原因）
            return ResponseUtil.failure("验证失败");
        }
    }


    @GetMapping("/refreshToken")
    @Operation(summary = "refreshToken", description = "refreshToken")
    public ResponseData refreshToken(@RequestParam String refreshToken) {

        // 生成 Token
        if (refreshToken == null) {
            return ResponseUtil.failure("401");
        }
        Claims claims = jwtUtils.validateRefreshToken(refreshToken);

        if (claims != null) {
            String newAccessToken = jwtUtils.generateAccessToken(claims);
            String newRefreshToken = jwtUtils.generateAccessToken(claims);
            Map<String, Object> tokenData = new HashMap<>();
            tokenData.put("accessToken", newAccessToken);
            tokenData.put("tokenType", "Bearer");
//            tokenData.put("expiresIn", claims.get); // 返回秒数
            tokenData.put("refreshToken", newRefreshToken);
            // 令牌有效
            return ResponseUtil.success(tokenData);
        } else {
            // 令牌无效（已由JwtUtils记录具体原因）
            return ResponseUtil.failure("验证失败");
        }
    }





    @GetMapping("/getRedisLockTest")
    @Operation(summary = "根据用户ID获取用户信息", description = "传入用户ID，返回对应的用户详细信息")
    public ResponseData getRedisLockTest() throws JsonProcessingException {

        // 1. 先尝试从缓存获取数据
        List<PayAccountGroupEntity> cachedList = (List<PayAccountGroupEntity>)
                redisTemplate.opsForValue().get("test:PayAccountGroupEntity");

        if (cachedList != null && !cachedList.isEmpty()) {
            return ResponseUtil.success(cachedList);
        }

        // 2. 缓存不存在，使用分布式锁进行数据库查询和缓存重建
        return getDataWithDistributedLock();
    }

    /**
     * 使用分布式锁保护缓存重建过程
     */
    private ResponseData getDataWithDistributedLock() {
        // 定义锁的key，确保唯一性
        String lockKey = "lock:PayAccountGroupEntity:xnll";
        RLock lock = redissonClient.getLock(lockKey);

        boolean isLocked = false;
        try {
            // 尝试获取锁，等待最多3秒，锁持有30秒自动释放（防止死锁）
            isLocked = lock.tryLock(3, 30, TimeUnit.SECONDS);

            if (isLocked) {
                // 获取锁成功，再次检查缓存（双重检查锁模式）
                List<PayAccountGroupEntity> cachedList = (List<PayAccountGroupEntity>)
                        redisTemplate.opsForValue().get("test:PayAccountGroupEntity");

                if (cachedList != null && !cachedList.isEmpty()) {
                    return ResponseUtil.success(cachedList);
                }

                // 执行数据库查询
                List<PayAccountGroupEntity> result = queryFromDatabase();

                // 将结果存入缓存，设置过期时间防止脏数据
                if (result != null && !result.isEmpty()) {
                    redisTemplate.opsForValue().set("test:PayAccountGroupEntity",
                            result, 15, TimeUnit.SECONDS);
                }

                return ResponseUtil.success(result);
            } else {
                // 获取锁失败，可以等待重试或返回默认值
                // 这里选择等待100ms后重试整个方法
                Thread.sleep(100);
                return xnllWithRetry();
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // 记录日志并返回错误响应
            return ResponseUtil.failure("系统繁忙，请稍后重试");
        } finally {
            // 确保释放锁
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 从数据库查询数据的核心逻辑
     */
    private List<PayAccountGroupEntity> queryFromDatabase() {
        try {
            Specification<PayAccountGroupEntity> spec = (root, query, cb) ->
                    cb.equal(root.get("remark"), "1");

            PageRequest createTime = PageRequest.of(0, 10, Sort.by("createTime").descending());
            org.springframework.data.domain.Page<PayAccountGroupEntity> pageResult =
                    payAccountGroupService.findAll(spec, createTime);

            return pageResult.getContent();
        } catch (Exception e) {
            // 记录日志
            throw new RuntimeException("数据查询失败");
        }
    }

    /**
     * 重试机制（可选）
     */
    private ResponseData xnllWithRetry() {
        // 简单的重试逻辑，实际项目中可以使用更复杂的重试策略
        try {
            return getRedisLockTest();
        } catch (Exception e) {
            return ResponseUtil.failure("请求处理失败");
        }
    }




    /**
     * 测试正常事务提交
     */
    @PostMapping("/success")
    public ResponseEntity<Map<String, Object>> testNormalTransaction(@RequestBody SysUser user) {
        try {
            log.info("开始正常事务测试，用户: {}", user.getUser_name());
            Boolean result = serviceiml.createUserSuccess(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", result);
            response.put("message", "事务正常提交测试完成");
            response.put("user", user);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("正常事务测试失败", e);
            return ResponseEntity.status(500).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    /**
     * 测试事务回滚
     */
    @PostMapping("/rollback")
    public ResponseEntity<Map<String, Object>> testRollbackTransaction(@RequestBody SysUser user) {
        try {
            log.info("开始事务回滚测试，用户: {}", user.getUser_name());
            // 这个方法会抛出异常触发回滚
            Boolean result = serviceiml.createUserThenRollback(user);

            // 如果执行到这里，说明回滚未发生
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "事务应该回滚但未发生");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.info("事务回滚按预期发生: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "事务已回滚: " + e.getMessage());
            response.put("expected", true);
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 测试检查型异常的事务行为
     */
    @PostMapping("/checked-exception")
    public ResponseEntity<Map<String, Object>> testCheckedException(@RequestBody SysUser user) {
        try {
            log.info("测试检查型异常事务行为，用户: {}", user.getUser_name());
            Boolean result = serviceiml.createUserWithCheckedException(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", result);
            response.put("message", "检查型异常测试完成");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.info("检查型异常捕获: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "检查型异常: " + e.getMessage());
            response.put("exceptionType", e.getClass().getSimpleName());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 测试自调用事务失效
     */
    @PostMapping("/self-invoke")
    public ResponseEntity<Map<String, Object>> testSelfInvocation(@RequestBody SysUser user) {
        try {
            log.info("测试自调用事务失效，用户: {}", user.getUser_name());
            Boolean result = serviceiml.testSelfInvocation(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", result);
            response.put("message", "自调用测试完成");
            response.put("user", user);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("自调用测试异常", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "自调用测试异常: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 查询用户（用于验证数据是否持久化）
     */
    @PostMapping("/test-create")
    public String create(@RequestBody SysUser user) {
        Boolean aBoolean = serviceiml.testRequiresNewPropagation(user);
        return "CHEN";
    }


    /**
     * 查询用户（用于验证数据是否持久化）
     */
    @GetMapping("/{id}")
    public ResponseEntity<SysUser> getUserById(@PathVariable Long id) {
        SysUser user = serviceiml.selectById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 获取所有用户（用于验证测试结果）
     */
    @GetMapping
    public ResponseEntity<List<SysUser>> getAllUsers() {
        List<SysUser> users = serviceiml.selectList(null);
        return ResponseEntity.ok(users);
    }


    private final RestTemplate restTemplate;
    @GetMapping("/order/user/{id}")
    public String getOrderWithUser(@PathVariable Long id) {
        String url =  "http://localhost:8061/order/user/" + id;
        return restTemplate.getForObject(url, String.class);
    }


    private final UserServiceConsumerClient userServiceConsumerClient;
    @GetMapping("/getFeignClient/user/{id}")
    public String getFeignClient(@PathVariable Long id) {
        return userServiceConsumerClient.getOrderWithUser(id);
    }
}
