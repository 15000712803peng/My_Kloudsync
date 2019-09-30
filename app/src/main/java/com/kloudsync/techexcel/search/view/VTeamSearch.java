package com.kloudsync.techexcel.search.view;

import com.kloudsync.techexcel.bean.Team;

import java.util.List;

public interface VTeamSearch {
    void showLoading();

    void showEmpty(String message);

    void showTeams(List<Team> team, String keyword);
}
