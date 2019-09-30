package com.kloudsync.techexcel.bean;

public class TvDevice {
    private String deviceName;
    private String deviceSessionId;
    private String loginTime;
    private boolean enableBind;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceSessionId() {
        return deviceSessionId;
    }

    public void setDeviceSessionId(String deviceSessionId) {
        this.deviceSessionId = deviceSessionId;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public boolean isEnableBind() {
        return enableBind;
    }

    public void setEnableBind(boolean enableBind) {
        this.enableBind = enableBind;
    }
}
