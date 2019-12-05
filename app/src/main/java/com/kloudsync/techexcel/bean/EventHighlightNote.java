package com.kloudsync.techexcel.bean;

import java.util.List;

/**
 * Created by tonyan on 2019/11/28.
 */

public class EventHighlightNote {
    private int pageNumber;
    NoteDetail note;

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public NoteDetail getNote() {
        return note;
    }

    public void setNote(NoteDetail note) {
        this.note = note;
    }


}
