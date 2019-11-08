package com.ub.kloudsync.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
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
import com.kloudsync.techexcel.bean.Team;
import com.kloudsync.techexcel.bean.UserInCompany;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.search.ui.SpaceSearchActivity;
import com.kloudsync.techexcel.tool.KloudCache;
import com.ub.techexcel.adapter.SpaceAdapter;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

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

    private TextView projectText;
    private String projectName;
    RelativeLayout switchTeamLayout;

    private static final int REQUEST_CREATE_NEW_SPACE = 1;
    private TextView titleText;
    private static final int REQUEST_SELECT_TEAM = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.switchspacelayout);
        spaceId = getIntent().getIntExtra("ItemID", 0);
        isSyncRoom = getIntent().getBooleanExtra("isSyncRoom", false);
        teamId = getIntent().getIntExtra("team_id", 0);
        projectName = getIntent().getStringExtra("project_name");
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
        titleText.setText(R.string.select_a_dir);
        backLayout = (RelativeLayout) findViewById(R.id.layout_back);
        backLayout.setOnClickListener(this);

        projectText = findViewById(R.id.txt_team_name);
        projectText.setText(projectName);
        searchLayout = findViewById(R.id.search_layout);
        searchLayout.setOnClickListener(this);
        switchTeamLayout = findViewById(R.id.layout_switch_team);
        switchTeamLayout.setOnClickListener(this);
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
                switchOK(teamSpaceBean);
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
                break;
            case R.id.layout_switch_team:
                selectTeam();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CREATE_NEW_SPACE) {
                getSpaceList();
            }else if(requestCode == REQUEST_SELECT_TEAM){

                    teamId = data.getIntExtra("team_id", -1);
                    projectName = data.getStringExtra("team_name");
                    projectText.setText(projectName);
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

    private void selectTeam() {
        Intent intent = new Intent(this, SelectTeamActivity.class);
        intent.putExtra("team_id", teamId);
        startActivityForResult(intent, REQUEST_SELECT_TEAM);
    }

    private void switchOK(final TeamSpaceBean space) {

        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        editor = sharedPreferences.edit();
        final JSONObject jsonObject = format();
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject responsedata = com.ub.techexcel.service.ConnectService.submitDataByJson(
                            AppConfig.URL_PUBLIC
                                    + "User/AddOrUpdateUserPreference", jsonObject);
                    Log.e("返回的jsonObject", jsonObject.toString() + "");
                    Log.e("返回的responsedata", responsedata.toString() + "");
                    String retcode = responsedata.getString("RetCode");
                    Message msg = new Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        saveTeam(space);
                    } else {
                        msg.what = AppConfig.FAILED;
                        String ErrorMessage = responsedata.getString("ErrorMessage");
                        msg.obj = ErrorMessage;
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }


    private JSONObject format() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("FieldID", 10001);
//            jsonObject.put("PreferenceValue", 0);
            jsonObject.put("PreferenceText", format2() + "");
//            jsonObject.put("PreferenceMemo", "");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonObject;
    }



    private JSONObject format2() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("TeamID", teamId);
            jsonObject.put("TeamName", projectName);
            jsonObject.put("SchoolID", sharedPreferences.getInt("SchoolID", -1));
            jsonObject.put("SchoolName", sharedPreferences.getString("SchoolName", null));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonObject;
    }

    private void saveTeam(TeamSpaceBean teamSpaceBean) {
        editor = sharedPreferences.edit();
        editor.putString("teamname", projectName);
        editor.putInt("teamid",teamId);
        editor.commit();
        EventBus.getDefault().post(new TeamSpaceBean());
        Intent intent = getIntent();
        intent.putExtra("selectSpace", (Serializable) teamSpaceBean);
        intent.putExtra("teamname", projectName);
        intent.putExtra("teamid",teamId);
        Log.e("space","space:" + teamSpaceBean);
        EventSpaceData spaceData = new EventSpaceData();
        spaceData.setTeamId(teamId);
        spaceData.setTeamName(projectName);
        spaceData.setSpaceId(teamSpaceBean.getItemID());
        spaceData.setSpaceName(teamSpaceBean.getName());
        EventBus.getDefault().post(spaceData);
        setResult(RESULT_OK, intent);
        finish();
    }


}


