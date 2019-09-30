package com.kloudsync.techexcel.search.view;

import com.ub.kloudsync.activity.TeamSpaceBean;

import java.util.List;

public interface VSpaceSearch {
    void showLoading();

    void showEmpty(String message);

    void showSpaces(List<TeamSpaceBean> spaces, String keyword);
}
