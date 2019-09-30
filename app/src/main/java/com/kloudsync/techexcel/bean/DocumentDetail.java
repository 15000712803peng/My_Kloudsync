package com.kloudsync.techexcel.bean;

import com.ub.kloudsync.activity.Document;

import java.util.List;

public class DocumentDetail {
    private int TotalCount;
    private List<Document> DocumentList;

    public int getTotalCount() {
        return TotalCount;
    }

    public void setTotalCount(int totalCount) {
        TotalCount = totalCount;
    }

    public List<Document> getDocumentList() {
        return DocumentList;
    }

    public void setDocumentList(List<Document> documentList) {
        DocumentList = documentList;
    }
}
