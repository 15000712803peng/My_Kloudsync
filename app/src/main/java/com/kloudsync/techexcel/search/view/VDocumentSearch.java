package com.kloudsync.techexcel.search.view;

import com.ub.kloudsync.activity.Document;

import java.util.List;

public interface VDocumentSearch {
    void showLoading();

    void showEmpty(String message);

    void showDocuments(List<Document> document, String keyword);
}
