package com.kloudsync.techexcel.bean;

import java.util.List;

public class DevicesData {
    private boolean enableBind;
    private List<TvDevice> deviceList;

    public boolean isEnableBind() {
        return enableBind;
    }

    public void setEnableBind(boolean enableBind) {
        this.enableBind = enableBind;
    }

    public List<TvDevice> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<TvDevice> deviceList) {
        this.deviceList = deviceList;
    }
}
