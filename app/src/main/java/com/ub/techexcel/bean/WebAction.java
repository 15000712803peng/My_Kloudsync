package com.ub.techexcel.bean;

import android.support.annotation.NonNull;

import com.kloudsync.techexcel.bean.WebVedio;

public class WebAction implements Comparable<WebAction>{

    private int index;
    private int SoundtrackID;
    private long Time;
    private String Data;
    private int AttachmentID;
    private String PageNumber;
    private int SaveObject;
    private boolean executed;
    private String savedLocalUrl;
    private WebVedio webVedio;


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public WebVedio getWebVedio() {
        return webVedio;
    }

    public void setWebVedio(WebVedio webVedio) {
        this.webVedio = webVedio;
    }

    public String getSavedLocalUrl() {
        return savedLocalUrl;
    }

    public void setSavedLocalUrl(String savedLocalUrl) {
        this.savedLocalUrl = savedLocalUrl;
    }

    public boolean isExecuted() {
        return executed;
    }

    public void setExecuted(boolean executed) {
        this.executed = executed;
    }

    public int getSoundtrackID() {
        return SoundtrackID;
    }

    public void setSoundtrackID(int soundtrackID) {
        SoundtrackID = soundtrackID;
    }

    public long getTime() {
        return Time;
    }

    public void setTime(long time) {
        Time = time;
    }

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }

    public int getAttachmentID() {
        return AttachmentID;
    }

    public void setAttachmentID(int attachmentID) {
        AttachmentID = attachmentID;
    }

    public String getPageNumber() {
        return PageNumber;
    }

    public void setPageNumber(String pageNumber) {
        PageNumber = pageNumber;
    }

    public int getSaveObject() {
        return SaveObject;
    }

    public void setSaveObject(int saveObject) {
        SaveObject = saveObject;
    }


    @Override
    public int compareTo(@NonNull WebAction o) {
        return (int)(getTime() - o.getTime());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WebAction webAction = (WebAction) o;

        if (Time != webAction.Time) return false;
        return Data != null ? Data.equals(webAction.Data) : webAction.Data == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (Time ^ (Time >>> 32));
        result = 31 * result + (Data != null ? Data.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "WebAction{" +
                "index=" + index +
                ", Time=" + Time +
                '}';
    }
}
