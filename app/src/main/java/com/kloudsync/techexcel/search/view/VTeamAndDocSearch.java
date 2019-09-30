package com.kloudsync.techexcel.search.view;

import com.ub.kloudsync.activity.Document;
import com.ub.kloudsync.activity.TeamSpaceBean;

import java.util.List;

public interface VTeamAndDocSearch {
    void showSpacesLoading();

    void showDocsLoading();

    void showEmptySpaces();

    void showEmptyDocs();

    void showSpaces(List<TeamSpaceBean> spacesData);

    void showDoces(List<Document> docsData);

    void showEmpty();
}
