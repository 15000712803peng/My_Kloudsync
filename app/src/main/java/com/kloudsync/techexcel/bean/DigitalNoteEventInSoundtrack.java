package com.kloudsync.techexcel.bean;


public class DigitalNoteEventInSoundtrack {
    private int actionType;
    private long time;
    private int page;
    private long syncId;
    private DigitalNoteDataInSoundtrack data;
    boolean isExecuted;

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public DigitalNoteDataInSoundtrack getData() {
        return data;
    }

    public void setData(DigitalNoteDataInSoundtrack data) {
        this.data = data;
    }

    public long getSyncId() {
        return syncId;
    }

    public void setSyncId(long syncId) {
        this.syncId = syncId;
    }

    public boolean isExecuted() {
        return isExecuted;
    }

    public void setExecuted(boolean executed) {
        isExecuted = executed;
    }
}
