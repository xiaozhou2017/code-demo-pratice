package com.example.demo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data; // 记得使用 Lombok 注解简化代码

@Data // 自动生成 getter, setter, toString 等方法
@Document(collection = "users") // 指定该实体类对应 MongoDB 中的 "users" 集合
public class MongoUser {
    @Id // 标记为主键
    private String id; // MongoDB 默认使用 String 类型的 ObjectId
    private String name;
    private Integer age;
    private String email;
    private String city;
    private String status;
}
