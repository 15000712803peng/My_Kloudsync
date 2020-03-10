package com.kloudsync.techexcel.bean;

import com.ub.techexcel.bean.Note;

/**
 * Created by tonyan on 2019/11/28.
 */

public class EventFloatingNote {
    private int linkId;
    private int noteId;
    private Note note;

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public int getLinkId() {
        return linkId;
    }

    public void setLinkId(int linkId) {
        this.linkId = linkId;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "EventNote{" +
                "linkId=" + linkId +
                ", note=" + note +
                '}';
    }
}
