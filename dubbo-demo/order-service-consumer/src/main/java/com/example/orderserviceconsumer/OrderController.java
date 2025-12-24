// 文件路径：src/main/java/com/example/orderserviceconsumer/controller/OrderController.java
package com.example.orderserviceconsumer;


import com.alibaba.fastjson.JSONObject;
import entitiy.TestUser;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import service.UserService;

@RestController
public class OrderController {

    // 核心：使用 @DubboReference 注入远程服务的代理
    @DubboReference(version = "1.0.0", group = "user-group")
    private UserService userService;

    @GetMapping("/order/user/{id}")
    public String getOrderWithUser(@PathVariable Long id) {
        // 像调用本地方法一样调用远程服务
        TestUser testUser = userService.getUserById(id);

        return "订单信息 - 关联用户：" + JSONObject.toJSONString(testUser);
    }
}
