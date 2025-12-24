package com.example.userserviceprovider;

import entitiy.TestUser;
import org.apache.dubbo.config.annotation.DubboService;
import service.UserService;
import java.util.Arrays;
import java.util.List;

@DubboService(version = "1.0.0", group = "user-group")
public class UserServiceImpl implements UserService {
    @Override
    public TestUser getUserById(Long id) {
        TestUser testUser=new TestUser();
        testUser.setId(id);
        testUser.setName("zhouwenwy");
        return testUser;
    }

    @Override
    public List<TestUser> findAllUsers() {
        TestUser testUser=new TestUser();
        testUser.setId(2L);
        testUser.setName("zhouwenwy33");

        return Arrays.asList(testUser);
    }
}
