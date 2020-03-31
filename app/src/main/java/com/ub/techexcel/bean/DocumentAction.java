package com.ub.techexcel.bean;

public class DocumentAction {

    private int attachmentId;

    private int syncId;

    private String zippedActionData;

    private int index;

    private int total;

    public int getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(int attachmentId) {
        this.attachmentId = attachmentId;
    }

    public int getSyncId() {
        return syncId;
    }

    public void setSyncId(int syncId) {
        this.syncId = syncId;
    }

    public String getZippedActionData() {
        return zippedActionData;
    }

    public void setZippedActionData(String zippedActionData) {
        this.zippedActionData = zippedActionData;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
