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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WebVedio webVedio = (WebVedio) o;

        if (actionType != webVedio.actionType) return false;
        if (vid != webVedio.vid) return false;
        return url != null ? url.equals(webVedio.url) : webVedio.url == null;
    }

    @Override
    public int hashCode() {
        int result = actionType;
        result = 31 * result + vid;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }
}
