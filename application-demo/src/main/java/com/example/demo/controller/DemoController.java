package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.example.demo.bean.ResponseData;
import com.example.demo.bean.ResponseUtil;
import com.example.demo.config.JwtUtils;
import com.example.demo.entity.PayAccountGroupEntity;
import com.example.demo.entity.SysUser;
import com.example.demo.repository.PayAccountGroupRepository;
import com.example.demo.service.ISysUserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.aop.framework.AopProxyUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisFactory;

import javax.annotation.PostConstruct;
import java.awt.print.Pageable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author markchou
 * @createtime 2025/11/11
 */
@RestController
@Tag(name = "用户管理", description = "用户相关的增删改查接口")
@RequestMapping("/api/redis")
//@RequiredArgsConstructor
public class DemoController {


    private  ISysUserService service;

    private  PayAccountGroupRepository payAccountGroupService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    public void setUserService(ISysUserService userService,PayAccountGroupRepository payAccountGroupRepository) {
        this.service = userService;
        this.payAccountGroupService = payAccountGroupRepository;
    }

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

    @GetMapping("/api/redisson")
    @Operation(summary = "根据用户ID获取用户信息", description = "传入用户ID，返回对应的用户详细信息")
    public ResponseData redissonRequest() {
        // 使用RedissonClient替代RedisTemplate
        EntityWrapper<SysUser> userWrapper = new EntityWrapper<>();
        userWrapper.eq("account_", "admin");

        // 使用Redisson的RBucket接口获取缓存
        RBucket<SysUser> userBucket = redissonClient.getBucket("test:sysPageList2");
        SysUser redisUser = userBucket.get();

        if(redisUser != null){
            // Redisson自动处理序列化，无需手动JSON转换
            return ResponseUtil.success(redisUser);
        }
        // 从数据库查询
        SysUser sysUser = service.selectOne(userWrapper);
        // 使用Redisson设置缓存，包含5小时过期时间
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
}
