package com.kloudsync.techexcel.bean;

/**
 * Created by tonyan on 2019/11/22.
 */

public class WebVedio {

    private int actionType;
    private int stat;
    private float time;
    private int vid;
    private String url;
    private int type;
    private int save;
    private boolean isPreparing;
    private boolean isPrepared;
    private boolean isExecuted;
    private long savetime;

    public long getSavetime() {
        return savetime;
    }

    public void setSavetime(long savetime) {
        this.savetime = savetime;
    }

    public boolean isExecuted() {
        return isExecuted;
    }

    public void setExecuted(boolean executed) {
        isExecuted = executed;
    }

    public boolean isPreparing() {
        return isPreparing;
    }

    public void setPreparing(boolean preparing) {
        isPreparing = preparing;
    }

    public boolean isPrepared() {
        return isPrepared;
    }

    public void setPrepared(boolean prepared) {
        isPrepared = prepared;
    }

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public int getStat() {
        return stat;
    }

    public void setStat(int stat) {
        this.stat = stat;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public int getVid() {
        return vid;
    }

    public void setVid(int vid) {
        this.vid = vid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSave() {
        return save;
    }

    public void setSave(int save) {
        this.save = save;
    }

    @Override
    public String toString() {
        return "WebVedio{" +
                "actionType=" + actionType +
                ", stat=" + stat +
                ", time=" + time +
                ", vid=" + vid +
                ", url='" + url + '\'' +
                ", type=" + type +
                ", save=" + save +
                ", isPreparing=" + isPreparing +
                ", isPrepared=" + isPrepared +
                ", isExecuted=" + isExecuted +
                ", savetime=" + savetime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WebVedio webVedio = (WebVedio) o;

        if (stat != webVedio.stat) return false;
        return savetime == webVedio.savetime;
    }

    @Override
    public int hashCode() {
        int result = stat;
        result = 31 * result + (int) (savetime ^ (savetime >>> 32));
        return result;
    }
}
