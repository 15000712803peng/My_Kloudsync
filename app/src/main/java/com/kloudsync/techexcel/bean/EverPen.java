package com.kloudsync.techexcel.bean;

/**
 * Created by tonyan on 2020/1/15.
 */

public class EverPen {
    private String macAddress;
    private String name;
    private boolean isConnected;

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EverPen everPen = (EverPen) o;

        return macAddress != null ? macAddress.equals(everPen.macAddress) : everPen.macAddress == null;
    }

    @Override
    public int hashCode() {
        return macAddress != null ? macAddress.hashCode() : 0;
    }

    public EverPen(String macAddress){
        this.macAddress = macAddress;
    }

    public EverPen(){

    }
}
