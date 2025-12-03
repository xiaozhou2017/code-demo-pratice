package com.example.demo.entity;

import com.baomidou.mybatisplus.enums.IdType;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotations.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 用户管理
 * </p>
 *
 * @author mark zhou
 * @since 2025-11-12
 */
@TableName("sys_user")
public class SysUser extends Model<SysUser> {

  private static final long serialVersionUID = 1L;

  @TableId(value = "id_", type = IdType.AUTO)
  private Long id_;
  /**
   * 登陆帐户
   */
  private String account_;
  /**
   * 密码
   */
  private String password_;
  /**
   * 用户类型(1普通用户2管理员3系统用户)
   */
  private String user_type;
  /**
   * 姓名
   */
  private String user_name;
  /**
   * 姓名拼音
   */
  private String name_pinyin;
  /**
   * 性别(0:未知;1:男;2:女)
   */
  private Integer sex_;
  /**
   * 头像
   */
  private String avatar_;
  /**
   * 电话
   */
  private String phone_;
  /**
   * 邮箱
   */
  private String email_;
  /**
   * 身份证号码
   */
  private String id_card;
  /**
   * 微信
   */
  private String wei_xin;
  /**
   * 微博
   */
  private String wei_bo;
  /**
   * QQ
   */
  private String qq_;
  /**
   * 出生日期
   */
  private LocalDate birth_day;
  /**
   * 部门编号
   */
  private Long dept_id;
  /**
   * 职位
   */
  private String position_;
  /**
   * 详细地址
   */
  private String address_;
  /**
   * 工号
   */
  private String staff_no;
  /**
   * 是否删除
   */
  private Integer is_del;
  private Integer enable_;
  private String remark_;
  private LocalDateTime create_time;
  private Long create_by;
  private LocalDateTime update_time;
  private Long update_by;
  /**
   * 当前金额
   */
  private Double money;


  public Long getId_() {
    return id_;
  }

  public void setId_(Long id_) {
    this.id_ = id_;
  }

  public String getAccount_() {
    return account_;
  }

  public void setAccount_(String account_) {
    this.account_ = account_;
  }

  public String getPassword_() {
    return password_;
  }

  public void setPassword_(String password_) {
    this.password_ = password_;
  }

  public String getUser_type() {
    return user_type;
  }

  public void setUser_type(String user_type) {
    this.user_type = user_type;
  }

  public String getUser_name() {
    return user_name;
  }

  public void setUser_name(String user_name) {
    this.user_name = user_name;
  }

  public String getName_pinyin() {
    return name_pinyin;
  }

  public void setName_pinyin(String name_pinyin) {
    this.name_pinyin = name_pinyin;
  }

  public Integer getSex_() {
    return sex_;
  }

  public void setSex_(Integer sex_) {
    this.sex_ = sex_;
  }

  public String getAvatar_() {
    return avatar_;
  }

  public void setAvatar_(String avatar_) {
    this.avatar_ = avatar_;
  }

  public String getPhone_() {
    return phone_;
  }

  public void setPhone_(String phone_) {
    this.phone_ = phone_;
  }

  public String getEmail_() {
    return email_;
  }

  public void setEmail_(String email_) {
    this.email_ = email_;
  }

  public String getId_card() {
    return id_card;
  }

  public void setId_card(String id_card) {
    this.id_card = id_card;
  }

  public String getWei_xin() {
    return wei_xin;
  }

  public void setWei_xin(String wei_xin) {
    this.wei_xin = wei_xin;
  }

  public String getWei_bo() {
    return wei_bo;
  }

  public void setWei_bo(String wei_bo) {
    this.wei_bo = wei_bo;
  }

  public String getQq_() {
    return qq_;
  }

  public void setQq_(String qq_) {
    this.qq_ = qq_;
  }

  public LocalDate getBirth_day() {
    return birth_day;
  }

  public void setBirth_day(LocalDate birth_day) {
    this.birth_day = birth_day;
  }

  public Long getDept_id() {
    return dept_id;
  }

  public void setDept_id(Long dept_id) {
    this.dept_id = dept_id;
  }

  public String getPosition_() {
    return position_;
  }

  public void setPosition_(String position_) {
    this.position_ = position_;
  }

  public String getAddress_() {
    return address_;
  }

  public void setAddress_(String address_) {
    this.address_ = address_;
  }

  public String getStaff_no() {
    return staff_no;
  }

  public void setStaff_no(String staff_no) {
    this.staff_no = staff_no;
  }

  public Integer getIs_del() {
    return is_del;
  }

  public void setIs_del(Integer is_del) {
    this.is_del = is_del;
  }

  public Integer getEnable_() {
    return enable_;
  }

  public void setEnable_(Integer enable_) {
    this.enable_ = enable_;
  }

  public String getRemark_() {
    return remark_;
  }

  public void setRemark_(String remark_) {
    this.remark_ = remark_;
  }

  public LocalDateTime getCreate_time() {
    return create_time;
  }

  public void setCreate_time(LocalDateTime create_time) {
    this.create_time = create_time;
  }

  public Long getCreate_by() {
    return create_by;
  }

  public void setCreate_by(Long create_by) {
    this.create_by = create_by;
  }

  public LocalDateTime getUpdate_time() {
    return update_time;
  }

  public void setUpdate_time(LocalDateTime update_time) {
    this.update_time = update_time;
  }

  public Long getUpdate_by() {
    return update_by;
  }

  public void setUpdate_by(Long update_by) {
    this.update_by = update_by;
  }

  public Double getMoney() {
    return money;
  }

  public void setMoney(Double money) {
    this.money = money;
  }

  @Override
  protected Serializable pkVal() {
    return this.id_;
  }

  @Override
  public String toString() {
    return "SysUser{" +
            ", id_=" + id_ +
            ", account_=" + account_ +
            ", password_=" + password_ +
            ", user_type=" + user_type +
            ", user_name=" + user_name +
            ", name_pinyin=" + name_pinyin +
            ", sex_=" + sex_ +
            ", avatar_=" + avatar_ +
            ", phone_=" + phone_ +
            ", email_=" + email_ +
            ", id_card=" + id_card +
            ", wei_xin=" + wei_xin +
            ", wei_bo=" + wei_bo +
            ", qq_=" + qq_ +
            ", birth_day=" + birth_day +
            ", dept_id=" + dept_id +
            ", position_=" + position_ +
            ", address_=" + address_ +
            ", staff_no=" + staff_no +
            ", is_del=" + is_del +
            ", enable_=" + enable_ +
            ", remark_=" + remark_ +
            ", create_time=" + create_time +
            ", create_by=" + create_by +
            ", update_time=" + update_time +
            ", update_by=" + update_by +
            ", money=" + money +
            "}";
  }
}
