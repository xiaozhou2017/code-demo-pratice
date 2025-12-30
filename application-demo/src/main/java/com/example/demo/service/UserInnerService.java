package com.example.demo.service;

import com.example.demo.entity.SysUser;
import com.example.demo.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

// 1. 首先，创建一个独立的Service，用于执行REQUIRES_NEW事务
@Service
public class UserInnerService {

    @Autowired
    private SysUserMapper userMapper; // 假设的Mapper

    /**
     * 使用REQUIRES_NEW传播行为的方法
     * 无论外部是否存在事务，都会启动一个新事务。外部事务会被挂起。
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void innerMethodWithRequiresNew(SysUser user) {
        // 第二个事务操作（内部事务）
        userMapper.insert(user);
        // 模拟一个可能失败的业务操作

        throw new RuntimeException("REQUIRES_NEW内部方法模拟失败");

    }
}
