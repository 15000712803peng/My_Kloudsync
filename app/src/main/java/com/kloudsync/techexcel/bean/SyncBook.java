package com.kloudsync.techexcel.bean;

/**
 * Created by tonyan on 2019/10/15.
 */

public class SyncBook {
    private int SyncBookID;
    private String OutlineInfo;
    private int ID;

    public int getSyncBookID() {
        return SyncBookID;
    }

    public void setSyncBookID(int syncBookID) {
        SyncBookID = syncBookID;
    }

    public String getOutlineInfo() {
        return OutlineInfo;
    }

    public void setOutlineInfo(String outlineInfo) {
        OutlineInfo = outlineInfo;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
