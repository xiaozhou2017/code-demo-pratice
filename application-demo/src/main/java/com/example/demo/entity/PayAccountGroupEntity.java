package com.example.demo.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * @author markchou
 * @createtime 2025/11/12
 */
@Entity
@Table(name = "pay_account_group")
public class PayAccountGroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 添加主键生成策略
    @Column(name = "id")
    private Long id;  // 改为 Long 类型

    @Column(name = "account", length = 24)
    private String account;

    @Column(name = "accountId", length = 11)
    private String accountId;

    @Column(name = "remark", length = 200)
    private String remark;

    @Column(name = "type", length = 20)
    private String type;

    @Column(name = "limitAmount", precision = 10, scale = 2)  // 修正精度
    private Double limitAmount;

    @Column(name = "enable")
    private Integer enable;

    @Column(name = "is_del")
    private Integer isDel;

    @Column(name = "create_time")
    private Timestamp createTime;

    @Column(name = "update_time")
    private Timestamp updateTime;

    @Column(name = "agentId")
    private Long agentId;

    // 必须有无参构造函数
    public PayAccountGroupEntity() {
    }

    // 有参构造函数（可选）
    public PayAccountGroupEntity(String account, String accountId, String type) {
        this.account = account;
        this.accountId = accountId;
        this.type = type;
    }

    // Getter 和 Setter 方法（不要加JPA注解）
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(Double limitAmount) {
        this.limitAmount = limitAmount;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public Integer getIsDel() {
        return isDel;
    }

    public void setIsDel(Integer isDel) {
        this.isDel = isDel;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    // 简化 equals 和 hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PayAccountGroupEntity that = (PayAccountGroupEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "PayAccountGroupEntity{" +
                "id=" + id +
                ", account='" + account + '\'' +
                ", accountId='" + accountId + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
