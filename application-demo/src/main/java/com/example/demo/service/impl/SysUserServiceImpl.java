package com.example.demo.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.example.demo.entity.SysUser;
import com.example.demo.mapper.SysUserMapper;
import com.example.demo.service.ISysUserService;
import com.example.demo.service.UserInnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 用户管理 服务实现类
 * </p>
 *
 * @author mark zhou
 * @since 2025-11-12
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    /**
     * 正常业务方法 - 事务成功提交
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean createUserSuccess(SysUser user) {
        // 第一次数据库操作
        boolean saveResult = this.insert(user);
        if (!saveResult) {
            throw new RuntimeException("用户创建失败");
        }

        // 模拟其他业务操作，如更新用户信息
        user.setUser_name("updated@" + user.getUser_name() + ".com");
        boolean updateResult = this.updateById(user);

        return saveResult && updateResult;
    }

    /**
     * 测试回滚的方法 - 模拟业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean createUserThenRollback(SysUser user) {
        // 第一次插入操作
        boolean firstSave = this.insert(user);
        if (!firstSave) {
            throw new RuntimeException("第一次保存失败");
        }

        // 模拟第二次操作前异常，触发回滚
        if (user.getUser_name() != null) {
            throw new RuntimeException("模拟业务异常，触发事务回滚");
        }

        // 这里的代码不会执行
        SysUser anotherUser = new SysUser();
        anotherUser.setUser_name("test_account");
        return this.insert(anotherUser);
    }

    /**
     * 测试检查型异常默认不回滚
     * 默认只对RuntimeException回滚
     */
    @Transactional // 不指定rollbackFor，测试默认行为
    public Boolean createUserWithCheckedException(SysUser user) throws Exception {
        this.insert(user);

        // 检查型异常默认不回滚
        throw new Exception("检查型异常，测试默认不回滚行为");
    }

    /**
     * 测试自调用事务失效问题
     * 这个方法内部调用其他事务方法，测试事务传播
     */
    public Boolean testSelfInvocation(SysUser user) {
        // 第一次保存
        boolean firstSave = this.insert(user);

        // 自调用事务方法 - 这里事务可能会失效！
        try {
            // 因为是通过this调用，而不是通过Spring代理调用
            this.createUserThenRollback(createAnotherUser());
        } catch (Exception e) {
            System.out.println("捕获到异常: " + e.getMessage());
        }

        return firstSave;
    }

    private SysUser createAnotherUser() {
        SysUser user = new SysUser();
        user.setUser_name("temp_account_" + System.currentTimeMillis());
        user.setAccount("临时用户");
        return user;
    }

    /**
     * 测试REQUIRES_NEW传播行为
     */
    @Autowired
    UserInnerService userInnerService;
    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean testRequiresNewPropagation(SysUser user) {
        // 第一个事务操作
        this.insert(user);

        try {
            // 这里应该通过另一个Service的方法测试REQUIRES_NEW
             userInnerService.innerMethodWithRequiresNew(user);
        } catch (Exception e) {
            System.out.println("内部方法异常，但外部事务应该正常提交");
        }

        return true;
    }

}
