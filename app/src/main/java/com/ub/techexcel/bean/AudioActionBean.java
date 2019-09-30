package com.ub.techexcel.bean;

public class AudioActionBean {

    private int time;

    //{"type":21,"page":1,"save":1,"d":"M260,248 L261,247 L268,243 L302,228 L343,216 L384,209 L412,207"
    // ,"id":"758a0d82-f78d-167b-44f5-c4c26c897376","w":"0.5","color":"#ff0000","CW":699,"CH":989,"tar":"","time":4956}
    private String data;


    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
