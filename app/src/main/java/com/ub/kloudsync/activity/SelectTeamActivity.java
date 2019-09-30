package com.ub.kloudsync.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.BaseActivity;
import com.kloudsync.techexcel.config.AppConfig;
import com.ub.techexcel.adapter.TeamAdapter;

import java.util.ArrayList;
import java.util.List;

public class SelectTeamActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView teamList;
    private TeamAdapter teamAdapter;
    private List<TeamSpaceBean> mCurrentTeamData = new ArrayList<>();
    private RelativeLayout backLayout;
    int teamID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAllTeamList();
    }

    @Override
    protected int setLayout() {
        return R.layout.activity_select_team;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void initView() {
        teamID = getIntent().getIntExtra("team_id", -1);
        teamList = (RecyclerView) findViewById(R.id.list_team);
        teamList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        backLayout = (RelativeLayout) findViewById(R.id.layout_back);
        backLayout.setOnClickListener(this);
        teamAdapter = new TeamAdapter(this, mCurrentTeamData);
        teamList.setAdapter(teamAdapter);
        teamAdapter.setOnItemLectureListener(new TeamAdapter.OnItemLectureListener() {
            @Override
            public void onItem(TeamSpaceBean team) {
                Intent data = new Intent();
                data.putExtra("team_id", team.getItemID());
                data.putExtra("team_name", team.getName());
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }

    public void getAllTeamList() {
        TeamSpaceInterfaceTools.getinstance().getTeamSpaceList(AppConfig.URL_PUBLIC + "TeamSpace/List?companyID=" + AppConfig.SchoolID + "&type=1&parentID=0",
                TeamSpaceInterfaceTools.GETTEAMSPACELIST, new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        List<TeamSpaceBean> teams = (List<TeamSpaceBean>) object;
                        refreshList(teams);

                    }
                });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_back:
                finish();
                break;
        }

    }

    private void refreshList(List<TeamSpaceBean> teams) {
        mCurrentTeamData.clear();
        mCurrentTeamData.addAll(teams);
        for (int i = 0; i < mCurrentTeamData.size(); i++) {
            TeamSpaceBean teamSpaceBean1 = mCurrentTeamData.get(i);
            if (teamSpaceBean1.getItemID() == teamID) {
                teamSpaceBean1.setSelect(true);
            } else {
                teamSpaceBean1.setSelect(false);
            }
        }
        teamAdapter.notifyDataSetChanged();
    }

}
