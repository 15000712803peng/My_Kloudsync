package com.kloudsync.techexcel.bean;

import com.ub.kloudsync.activity.Document;

import java.util.List;

public class MessageDocList {
    private List<Document> docList;

    public MessageDocList() {
    }

    public MessageDocList(List<Document> docList) {
        this.docList = docList;
    }

    public List<Document> getDocList() {
        return docList;
    }

    public void setDocList(List<Document> docList) {
        this.docList = docList;
    }
}
