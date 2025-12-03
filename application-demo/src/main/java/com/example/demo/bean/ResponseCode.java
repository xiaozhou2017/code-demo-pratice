package com.example.demo.bean;

/**
 * 响应码定义
 *
 *
 * @version 1.0
 * @Date 2017/11/10
 */
public enum ResponseCode {

    /**
     * 响应码信息
     */
    SUCCESS(0, "[提示]成功"),

    INVALID_OPENID_INFO(10000, "[提示]无效的OpenId"),

    AUTH_INFO(10001, "[提示]无操作权限"),

    PARAM_INFO(10002, "[提示]缺少参数或值为空"),

    PARAM_ERROR(10003, "[提示]参数不合法"),

    LOGIN_INFO(10004, "[提示]请重新登录"),

    MIDEA_TYPE_ERROR(10005, "[提示]浏览器无法解析服务端媒体类型"),

    EXPIRATION_TOKEN_INFO(40001, "[提示]当前客户端UserToken令牌已过期"),

    INVALID_TOKEN_INFO(40002, "[提示]当前客户端是无效的UserToken令牌"),

    NO_USER_TOKEN_INFO(10007, "[提示]缺少UserToken令牌参数"),

    EXPIRATION_ACCESS_TOKEN_INFO(40003, "[提示]授权AccessToken令牌已过期"),

    SIGNATURE_ERROR(40005, "[提示]客户端签名错误"),

    NO_ACCESS_TOKEN_INFO(10007, "[提示]缺少AccessToken令牌参数"),

    DATA_EXIT_INFO(10009, "[提示]您访问的数据不存在"),

    TIPS_ERROR(20001, "[提示]业务逻辑信息"),

    CONTENT_OVER_ERROR(20002, "[提示]操作失败, 某个数据字段内容超出"),

    RELATION_ERROR(20003, "[提示]操作失败, 某个数据字段存在关联依赖"),

    PARAMS_ERROR(20004, "[服务器]操作失败, 某个参数出现异常"),

    BIND_ERROR(20005, "[提示]客户端传入参数有误"),

    MISSING_FIELD_ERROR(20006, "[提示]操作失败, 缺少数据字段或违反约束"),

    FILE_UPLOAD_ERROR(20007, "[提示]上传文件出现异常, 可能是大小超出"),

    PAGE_INDEX_ERROR(30001, "[提示]客户端缺少分页索引参数"),

    PAGE_SIZE_ERROR(30002, "[提示]客户端缺少分页大小参数"),

    FILE_UPLOAD_MAX_ERROR(30008, "[提示]客户端上传文件大小超出范围"),

    REQUEST_TYPE_ERROR(40005, "[提示]Request 请求类型错误"),

    SYSTEM_ERROR(50000, "[服务器]运行时异常，请联系系统管理员"),

    NULL_ERROR(50001, "[服务器]空值异常"),

    CONVERT_ERROR(50002, "[服务器]数据类型转换异常"),

    IO_ERROR(50003, "[服务器]IO异常"),

    METHOD_ERROR(50004, "[服务器]未知方法异常"),

    ARRAY_ERROR(50005, "[服务器]数组越界异常"),

    NOWEB_ERROR(50006, "[服务器]网络异常"),

    PARSE_ERROR(50007, "[服务器]请求主体参数不能为空或解析错误"),

    MATH_ERROR(50008, "[提示]客户端传入参数与服务端类型不匹配"), ;

    private int code;
    private String msg;

    ResponseCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
