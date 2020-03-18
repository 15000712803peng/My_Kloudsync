package com.kloudsync.techexcel.bean;

/**
 * Created by tonyan on 2020/1/6.
 */

public class EventOpenOrCloseBluethoothNote {
    private String noteId;
    private int status;

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
