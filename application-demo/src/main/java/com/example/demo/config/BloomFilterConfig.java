package com.example.demo.config;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.example.demo.entity.SysUser;
import com.example.demo.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@Slf4j
public class BloomFilterConfig {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private ISysUserService userService;

    private RBloomFilter<String> userBloomFilter;

    @PostConstruct
    public void initBloomFilter() {
        // 获取布隆过滤器实例
        userBloomFilter = redissonClient.getBloomFilter("userBloomFilter");

        // 初始化布隆过滤器：预计10万用户，误判率1%
        userBloomFilter.tryInit(100000L, 0.01);

        // 将数据库中所有存在的用户账号预先加载到布隆过滤器中
        EntityWrapper<SysUser> wrapper = new EntityWrapper<>();
        wrapper.setParamAlias("account_");
        List<SysUser> allUsers = userService.selectList(wrapper);

        for (SysUser user : allUsers) {
            userBloomFilter.add(user.getAccount_());
        }

        StringBuilder debugInfo = new StringBuilder();
        debugInfo.append("布隆过滤器调试信息：\n");
        debugInfo.append("布隆过滤器名称：").append(userBloomFilter.getName()).append("\n");
        debugInfo.append("位数组大小（bits）：").append(userBloomFilter.getSize()).append("\n");
        debugInfo.append("哈希函数个数：").append(userBloomFilter.getHashIterations()).append("\n");
        debugInfo.append("预期插入元素数量：").append(userBloomFilter.getExpectedInsertions()).append("\n");
        debugInfo.append("当前已插入元素数量（近似值）：").append(userBloomFilter.count()).append("\n");
        debugInfo.append("期望误判率：").append(userBloomFilter.getFalseProbability()).append("\n");
        debugInfo.append("是否已初始化：").append(userBloomFilter.isExists()).append("\n");
        log.info(debugInfo.toString());
    }

    public RBloomFilter<String> getUserBloomFilter() {
        return userBloomFilter;
    }
}
