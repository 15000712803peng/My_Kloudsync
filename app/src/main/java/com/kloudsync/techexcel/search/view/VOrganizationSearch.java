package com.kloudsync.techexcel.search.view;

import com.kloudsync.techexcel.info.School;

import java.util.List;

public interface VOrganizationSearch {
    void showLoading();

    void showEmpty(String message);

    void showOrganizations(List<School> schools, String keyword);
}
