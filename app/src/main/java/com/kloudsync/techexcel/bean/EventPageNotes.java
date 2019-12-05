package com.kloudsync.techexcel.bean;

import java.util.List;

/**
 * Created by tonyan on 2019/11/28.
 */

public class EventPageNotes {
    private int pageNumber;
    private List<NoteDetail> notes;

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public List<NoteDetail> getNotes() {
        return notes;
    }

    public void setNotes(List<NoteDetail> notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "EventPageNotes{" +
                "pageNumber=" + pageNumber +
                ", notes=" + notes +
                '}';
    }
}
