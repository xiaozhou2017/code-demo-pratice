package service;

import entitiy.TestUser;

import java.util.List;

/**
 * @author markchou
 * @createtime 2025/12/24
 */
public interface UserService {
    TestUser getUserById(Long id);
    List<TestUser> findAllUsers();
}
