package com.example.demo.entity;


public class TPayType {

  private long id;
  private String code;
  private String name;
  private double codeRate;
  private String account;


  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  public double getCodeRate() {
    return codeRate;
  }

  public void setCodeRate(double codeRate) {
    this.codeRate = codeRate;
  }


  public String getAccount() {
    return account;
  }

  public void setAccount(String account) {
    this.account = account;
  }

}
