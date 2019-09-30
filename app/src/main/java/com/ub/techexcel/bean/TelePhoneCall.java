package com.ub.techexcel.bean;

public class TelePhoneCall {


    public TelePhoneCall(boolean isCall) {
        this.isCall = isCall;
    }

    boolean isCall;

    public boolean isCall() {
        return isCall;
    }

    public void setCall(boolean call) {
        isCall = call;
    }
}
