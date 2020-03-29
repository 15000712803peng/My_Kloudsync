package com.ub.techexcel.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tonyan on 2020/1/27.
 */

public class PartWebActions {
    private long startTime;
    private long endTime;
    private String url;
    private List<WebAction> webActions = new ArrayList<>();

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<WebAction> getWebActions() {
        return webActions;
    }

    public void setWebActions(List<WebAction> webActions) {
        this.webActions = webActions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PartWebActions that = (PartWebActions) o;

        if (startTime != that.startTime) return false;
        if (endTime != that.endTime) return false;
        return url != null ? url.equals(that.url) : that.url == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (startTime ^ (startTime >>> 32));
        result = 31 * result + (int) (endTime ^ (endTime >>> 32));
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }

//    @Override
//    public String toString() {
//        return "PartWebActions{" +
//                "startTime=" + startTime +
//                ", endTime=" + endTime +
//                ", url='" + url + '\'' +
//                ", webActions=" + webActions +
//                '}';
//    }
}
