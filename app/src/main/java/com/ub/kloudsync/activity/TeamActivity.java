package com.ub.kloudsync.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.kloudsync.techexcel.config.AppConfig;
import com.ub.techexcel.adapter.TeamAdapter;
import com.kloudsync.techexcel.R;

import java.util.ArrayList;
import java.util.List;

public class TeamActivity extends Activity {

    private RecyclerView mTeamRecyclerView;
    private List<TeamUser> mTeamUserData = new ArrayList<>();
    private int itemID;
    private TextView teamspacename;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.documentteam);
        initView();
        itemID = getIntent().getIntExtra("ItemID", 0);
        getTeamItem();

    }

    private void initView() {
        mTeamRecyclerView = (RecyclerView) findViewById(R.id.recycleview);
        mTeamRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        teamspacename= (TextView) findViewById(R.id.teamspacename);
    }

    public void getTeamItem() {

        TeamSpaceInterfaceTools.getinstance().getTeamItem(AppConfig.URL_PUBLIC + "TeamSpace/Item?itemID=" + itemID, TeamSpaceInterfaceTools.GETTEAMITEM, new TeamSpaceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                TeamSpaceBean teamSpaceBean= (TeamSpaceBean) object;
                teamspacename.setText(teamSpaceBean.getName());
                mTeamUserData=teamSpaceBean.getMemberList();

            }
        });

    }
}
