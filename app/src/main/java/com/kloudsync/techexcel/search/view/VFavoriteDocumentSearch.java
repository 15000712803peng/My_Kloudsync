package com.kloudsync.techexcel.search.view;

import com.ub.kloudsync.activity.Document;

import java.util.List;

public interface VFavoriteDocumentSearch {
    void showLoading();

    void showEmpty(String message);

    void showDocuments(List<Document> documents, String keyword);
}
