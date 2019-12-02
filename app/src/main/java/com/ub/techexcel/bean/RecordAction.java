package com.ub.techexcel.bean;

import android.support.annotation.NonNull;

import com.kloudsync.techexcel.bean.WebVedio;

public class RecordAction implements Comparable<RecordAction>{

    private int SoundtrackID;
    private long Time;
    private String Data;
    private int AttachmentID;
    private String PageNumber;
    private int SaveObject;
    private boolean executed;
    private String savedLocalUrl;
    private WebVedio webVedio;

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
    public int compareTo(@NonNull RecordAction o) {
        return (int)(getTime() - o.getTime());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecordAction that = (RecordAction) o;

        return Time == that.Time;
    }

    @Override
    public int hashCode() {
        return (int) (Time ^ (Time >>> 32));
    }

    @Override
    public String toString() {
        return "RecordAction{" +
                "SoundtrackID=" + SoundtrackID +
                ", Time=" + Time +
                ", Data='" + Data + '\'' +
                ", AttachmentID=" + AttachmentID +
                ", PageNumber='" + PageNumber + '\'' +
                ", SaveObject=" + SaveObject +
                ", executed=" + executed +
                ", savedLocalUrl='" + savedLocalUrl + '\'' +
                '}';
    }
}
