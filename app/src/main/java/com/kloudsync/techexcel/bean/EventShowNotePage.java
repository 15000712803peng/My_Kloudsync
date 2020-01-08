package com.kloudsync.techexcel.bean;

/**
 * Created by tonyan on 2019/12/31.
 */

public class EventShowNotePage {
    private DocumentPage notePage;
    private int attachmendId;
    private long noteId;

    public long getNoteId() {
        return noteId;
    }

    public void setNoteId(long noteId) {
        this.noteId = noteId;
    }

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

    @Override
    public String toString() {
        return "EventShowNotePage{" +
                "notePage=" + notePage +
                ", attachmendId=" + attachmendId +
                '}';
    }
}
