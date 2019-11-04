package com.ub.kloudsync.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.EventSpaceData;
import com.kloudsync.techexcel.bean.UserInCompany;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.search.ui.SpaceSearchActivity;
import com.kloudsync.techexcel.tool.KloudCache;
import com.ub.techexcel.adapter.SpaceAdapter;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SwitchSpaceActivity extends Activity implements View.OnClickListener {

    private RecyclerView mTeamRecyclerView;
    private List<TeamSpaceBean> spacesList = new ArrayList<>();
    private SpaceAdapter spaceAdapter;
    private RelativeLayout lin_add;
    private RelativeLayout backLayout;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int spaceId;
    private int teamId;
    private boolean isSyncRoom;

    private static final int REQUEST_CREATE_NEW_SPACE = 1;
    private TextView titleText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.switchspacelayout);
        spaceId = getIntent().getIntExtra("ItemID", 0);
        isSyncRoom = getIntent().getBooleanExtra("isSyncRoom", false);
        teamId = getIntent().getIntExtra("team_id", 0);
        initView();
    }


    @Override
    protected void onResume() {
        super.onResume();
        getSpaceList();
    }

    private void initView() {
        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
//        teamId = sharedPreferences.getInt("teamid", 0);

        mTeamRecyclerView = (RecyclerView) findViewById(R.id.recycleview);
        mTeamRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        lin_add = (RelativeLayout) findViewById(R.id.lin_add);
        lin_add.setOnClickListener(this);
        titleText = (TextView) findViewById(R.id.tv_title);
        titleText.setText("switch space");
        backLayout = (RelativeLayout) findViewById(R.id.layout_back);
        backLayout.setOnClickListener(this);

        searchLayout = findViewById(R.id.search_layout);
        searchLayout.setOnClickListener(this);
        spaceAdapter = new SpaceAdapter(SwitchSpaceActivity.this, spacesList, isSyncRoom, true);
        mTeamRecyclerView.setAdapter(spaceAdapter);
        spaceAdapter.setOnItemLectureListener(new SpaceAdapter.OnItemLectureListener() {
            @Override
            public void onItem(TeamSpaceBean teamSpaceBean) {

            }

            @Override
            public void select(TeamSpaceBean teamSpaceBean) {
                for (int i = 0; i < spacesList.size(); i++) {
                    TeamSpaceBean teamSpaceBean1 = spacesList.get(i);
                    if (teamSpaceBean1.getItemID() == teamSpaceBean.getItemID()) {
                        teamSpaceBean1.setSelect(true);
                    } else {
                        teamSpaceBean1.setSelect(false);
                    }
                }
                spaceAdapter.notifyDataSetChanged();
                Intent intent = getIntent();
                intent.putExtra("selectSpace", (Serializable) teamSpaceBean);
                Log.e("space","space:" + teamSpaceBean);
                EventSpaceData spaceData = new EventSpaceData();
                spaceData.setSpaceId(teamSpaceBean.getItemID());
                spaceData.setSpaceName(teamSpaceBean.getName());
                EventBus.getDefault().post(spaceData);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        handleRolePemission(KloudCache.getInstance(this).getUserInfo());
    }

    private void getSpaceList() {
        TeamSpaceInterfaceTools.getinstance().getTeamSpaceList(AppConfig.URL_PUBLIC + "TeamSpace/List?companyID=" + AppConfig.SchoolID + "&type=2&parentID=" + teamId,
                TeamSpaceInterfaceTools.GETTEAMSPACELIST, new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        List<TeamSpaceBean> list = (List<TeamSpaceBean>) object;
                        spacesList.clear();
                        spacesList.addAll(list);
                        for (int i = 0; i < spacesList.size(); i++) {
                            TeamSpaceBean teamSpaceBean1 = spacesList.get(i);
                            if (teamSpaceBean1.getItemID() == spaceId) {
                                teamSpaceBean1.setSelect(true);
                            } else {
                                teamSpaceBean1.setSelect(false);
                            }
                        }
                        spaceAdapter.notifyDataSetChanged();
                    }
                });
    }

    private LinearLayout searchLayout;


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin_add:
                Intent intent = new Intent(this, CreateNewSpaceActivityV2.class);
                intent.putExtra("ItemID", teamId);
                startActivityForResult(intent, REQUEST_CREATE_NEW_SPACE);
                break;
            case R.id.layout_back:
                finish();
                break;
            case R.id.search_layout:
                Intent searchIntent = new Intent(this, SpaceSearchActivity.class);
                searchIntent.putExtra("team_id", teamId);
                startActivity(searchIntent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CREATE_NEW_SPACE) {
                getSpaceList();
            }
        }
    }

    private void handleRolePemission(UserInCompany user) {
        if (user == null) {
            return;
        }
        if (user.getRole() == 7 || user.getRole() == 8) {
            lin_add.setVisibility(View.VISIBLE);
        } else {
            if (user.getRoleInTeam() == null) {
                return;
            }
            if (user.getRoleInTeam().getTeamRole() == 0) {
                lin_add.setVisibility(View.GONE);
            } else if (user.getRoleInTeam().getTeamRole() > 0) {
                lin_add.setVisibility(View.VISIBLE);
            }
        }
    }


}


