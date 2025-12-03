package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.example.demo.bean.ResponseData;
import com.example.demo.bean.ResponseUtil;
import com.example.demo.entity.PayAccountGroupEntity;
import com.example.demo.entity.SysUser;
import com.example.demo.repository.PayAccountGroupRepository;
import com.example.demo.service.ISysUserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisFactory;

import java.awt.print.Pageable;
import java.util.List;
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
    ThreadLocal threadLocal=new ThreadLocal();
        private final ExecutorService servicelocal=Executors.newFixedThreadPool(3);
    //
    private  ISysUserService service;

    private  PayAccountGroupRepository payAccountGroupService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    public void setUserService(ISysUserService userService,PayAccountGroupRepository payAccountGroupRepository) {
        this.service = userService;
        this.payAccountGroupService = payAccountGroupRepository;
    }

    @GetMapping("/api")
    @Operation(summary = "根据用户ID获取用户信息", description = "传入用户ID，返回对应的用户详细信息")
    public ResponseData request() throws JsonProcessingException {
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
    private int callCount = 0;
    public void infiniteRecursion() {
        callCount++;
        if (callCount % 1000 == 0) {
            System.out.println("递归深度: "+ callCount);
        }
        infiniteRecursion();
    }
}
