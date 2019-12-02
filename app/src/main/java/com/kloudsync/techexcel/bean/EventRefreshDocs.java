package com.kloudsync.techexcel.bean;

import java.util.List;

/**
 * Created by tonyan on 2019/11/27.
 */

public class EventRefreshDocs {
    private boolean refresh;
    private List<MeetingDocument> documents;
    private int itemId;

    public boolean isRefresh() {
        return refresh;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }

    public List<MeetingDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<MeetingDocument> documents) {
        this.documents = documents;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
}
