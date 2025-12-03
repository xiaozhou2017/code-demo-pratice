package com.example.demo.repository;

import com.example.demo.entity.PayAccountGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayAccountGroupRepository extends
        JpaRepository<PayAccountGroupEntity, Long>,
        JpaSpecificationExecutor<PayAccountGroupEntity> {

    // 添加一些常用的查询方法
    Optional<PayAccountGroupEntity> findByAccount(String account);
    List<PayAccountGroupEntity> findByType(String type);
    List<PayAccountGroupEntity> findByEnable(Integer enable);
    List<PayAccountGroupEntity> findByAgentId(Long agentId);
}
