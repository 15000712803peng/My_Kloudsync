package com.kloudsync.techexcel.bean.params;

public class EventPlayMeetingChangeDocument {

    private int itemId;
    private int pageNumber;
    private boolean isPlayMeeting;


    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public boolean isPlayMeeting() {
        return isPlayMeeting;
    }

    public void setPlayMeeting(boolean playMeeting) {
        isPlayMeeting = playMeeting;
    }
}
