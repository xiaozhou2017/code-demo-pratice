package com.example.demo;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.example.demo.entity.SysUser;
import com.example.demo.service.impl.SysUserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Slf4j
@TestPropertySource(locations = "classpath:application-local-nacos.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public  class SysUserServiceImplTest {

    @Autowired
    private SysUserServiceImpl sysUserService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    /**
     * 测试事务正常提交
     */
    @Test
    @Transactional
    public void testCreateUserSuccess() {
        // 准备测试数据
        SysUser user = new SysUser();
        user.setAccount("test_account");
        user.setUser_name("测试用户");
        user.setEmail_("test@example.com");

        // 执行测试
        Boolean result = sysUserService.createUserSuccess(user);

        // 验证结果
        assertTrue(result);

        // 验证数据确实插入到数据库
        SysUser savedUser = sysUserService.selectOne(
                new EntityWrapper<SysUser>().eq("account_", "test_account")
        );
        assertNotNull(savedUser);
        assertEquals("测试用户", savedUser.getUser_name());
        log.info("✅ 事务提交测试通过 - 用户已成功创建");
    }
}
