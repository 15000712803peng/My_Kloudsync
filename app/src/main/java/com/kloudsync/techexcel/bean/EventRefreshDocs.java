package com.kloudsync.techexcel.bean;

import java.util.List;

/**
 * Created by tonyan on 2019/11/27.
 */

public class EventRefreshDocs {
    private boolean refresh;
    private List<MeetingDocument> documents;
    private int itemId;
    private int pageNumber;

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

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
