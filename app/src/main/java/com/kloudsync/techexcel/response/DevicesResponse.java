package com.kloudsync.techexcel.response;

import com.kloudsync.techexcel.bean.DevicesData;

public class DevicesResponse {
    private int code;
    private String msg;
    private DevicesData data;

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

    public DevicesData getData() {
        return data;
    }

    public void setData(DevicesData data) {
        this.data = data;
    }
}
