package com.kloudsync.techexcel.bean;

/**
 * Created by tonyan on 2019/12/31.
 */

public class EventShowNotePage {
    private DocumentPage notePage;
    private int attachmendId;

    public int getAttachmendId() {
        return attachmendId;
    }

    public void setAttachmendId(int attachmendId) {
        this.attachmendId = attachmendId;
    }

    public DocumentPage getNotePage() {
        return notePage;
    }

    public void setNotePage(DocumentPage notePage) {
        this.notePage = notePage;
    }
}
