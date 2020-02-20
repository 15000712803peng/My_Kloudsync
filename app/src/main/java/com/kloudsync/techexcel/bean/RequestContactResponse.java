package com.kloudsync.techexcel.bean;

/**
 * Created by tonyan on 2020/2/20.
 */

public class RequestContactResponse {
    private int code;
    private String msg;
    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    private RequestContactData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public RequestContactData getData() {
        return data;
    }

    public void setData(RequestContactData data) {
        this.data = data;
    }
}
