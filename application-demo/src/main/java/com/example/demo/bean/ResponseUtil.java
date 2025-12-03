package com.example.demo.bean;

public class ResponseUtil {

    public static ResponseData success(Object object) {
        ResponseData responseData = new ResponseData();
        responseData.setCode(0);
        responseData.setMessage("执行成功");
        responseData.setData(object);
        return responseData;
    }

    public static ResponseData success(Object object, String msg) {
        ResponseData responseData = new ResponseData();
        responseData.setCode(0);
        responseData.setMessage(msg);
        responseData.setData(object);
        return responseData;
    }

    public static ResponseData success() {
        return success(null);
    }

    public static ResponseData failure(String msg) {
        ResponseData responseData = new ResponseData();
        responseData.setCode(1);
        responseData.setMessage(msg);
        responseData.setData(null);
        return responseData;
    }

    public static ResponseData failure() {
        return failure("执行失败");
    }

}
