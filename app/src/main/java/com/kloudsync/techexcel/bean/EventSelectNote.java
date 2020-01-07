package com.kloudsync.techexcel.bean;

import com.ub.techexcel.bean.Note;

import org.json.JSONObject;

/**
 * Created by tonyan on 2020/1/7.
 */

public class EventSelectNote {
    private int linkId;
    private JSONObject linkProperty;
    private Note note;
    private int newLinkId;

    public int getNewLinkId() {
        return newLinkId;
    }

    public void setNewLinkId(int newLinkId) {
        this.newLinkId = newLinkId;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public int getLinkId() {
        return linkId;
    }

    public void setLinkId(int linkId) {
        this.linkId = linkId;
    }

    public JSONObject getLinkProperty() {
        return linkProperty;
    }

    public void setLinkProperty(JSONObject linkProperty) {
        this.linkProperty = linkProperty;
    }
}
