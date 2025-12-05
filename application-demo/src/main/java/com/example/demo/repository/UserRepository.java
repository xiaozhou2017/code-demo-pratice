package com.example.demo.repository;

import com.example.demo.entity.MongoUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<MongoUser, String> {
    // 根据方法名自动实现查询：通过邮箱查找用户
    MongoUser findByEmail(String email);
}
