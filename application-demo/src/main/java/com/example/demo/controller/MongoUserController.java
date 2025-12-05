package com.example.demo.controller;

import com.example.demo.entity.MongoUser;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@Tag(name = "mongo用户管理", description = "用户相关的增删改查接口")
@RequestMapping("/api/users") // 统一 API 路径
public class MongoUserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<MongoUser> createMongoUser(@RequestBody MongoUser user) {
        MongoUser savedMongoUser = userService.saveUser(user);
        return ResponseEntity.ok(savedMongoUser);
    }

    @GetMapping
    public ResponseEntity<List<MongoUser>> getAllMongoUsers() {
        List<MongoUser> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<MongoUser>> getMongoUserById(@PathVariable String id) {
        Optional<MongoUser> user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMongoUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("MongoUser deleted successfully.");
    }


    public ResponseEntity<String> getMongoUserByConditions(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("MongoUser deleted successfully.");
    }
    @GetMapping("/complex")
    public List<MongoUser> findComplexUsers(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") Integer age,
            @RequestParam(required = false) String status) {
        return userService.findComplexUsers(name, age, status);
    }
}
