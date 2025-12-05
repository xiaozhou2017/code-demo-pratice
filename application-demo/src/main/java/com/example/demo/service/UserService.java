package com.example.demo.service;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.example.demo.entity.MongoUser;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public MongoUser saveUser(MongoUser user) {
        return userRepository.save(user);
    }

    public List<MongoUser> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<MongoUser> getUserById(String id) {
        return userRepository.findById(id);
    }

    public MongoUser getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 复杂多条件查询示例
     * 意图：查询年龄大于18岁且姓名以"张"开头，或者状态为"ACTIVE"的用户，结果按年龄降序排列
     */
    public List<MongoUser> findComplexUsers(String namePrefix, Integer minAge, String status) {
        // 创建Query对象
        Query query = new Query();

        // 创建一个列表，用于动态收集需要OR组合的条件
        List<Criteria> orCriteriaList = new ArrayList<>();

        // 1. 构建AND条件块：姓名模糊匹配且年龄大于最小值 (仅在namePrefix和minAge均有效时启用)
        if (!StringUtils.isBlank(namePrefix) && minAge != null && minAge > 0) {
            Criteria andCriteria = new Criteria();
            andCriteria.andOperator(
                    Criteria.where("name").regex("^" + namePrefix), // 模糊匹配姓名前缀
                    Criteria.where("age").gt(minAge)                // {年龄大于最小值}
            );
            orCriteriaList.add(andCriteria); // 将此AND条件块作为一个整体加入OR列表
        }
        // 如果上述AND条件不满足，则检查单个条件，避免有效条件因另一个条件为空而被忽略
        else {
            // 1a. 单独处理姓名前缀条件
            if (!StringUtils.isBlank(namePrefix)) {
                orCriteriaList.add(Criteria.where("name").regex("^" + namePrefix));
            }
            // 1b. 单独处理最小年龄条件
            if (minAge != null && minAge > 0) {
                orCriteriaList.add(Criteria.where("age").gt(minAge));
            }
        }

        // 2. 构建状态条件 (仅在status有效时启用)
        if (!StringUtils.isBlank(status)) {
            orCriteriaList.add(Criteria.where("status").is(status));
        }

        // 3. 构建主查询逻辑
        // 如果存在有效的OR条件，则将它们用OR组合起来；否则，构建一个空查询（即查询所有）
        if (!orCriteriaList.isEmpty()) {
            Criteria mainCriteria = new Criteria().andOperator(orCriteriaList.toArray(new Criteria[0]));
            query.addCriteria(mainCriteria);
        }
        // 如果orCriteriaList为空，说明所有条件都无效，query将保持为空，即匹配所有文档

        // 4. 设置排序（按年龄降序）
        query.with(Sort.by(Sort.Direction.DESC, "age"));

        // 5. （可选）设置返回字段，排除不需要的字段（例如密码）以提高效率
        query.fields().include("name", "age", "email", "status");

        // 执行查询
        return mongoTemplate.find(query, MongoUser.class, "users"); // "users"是集合名
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
}
