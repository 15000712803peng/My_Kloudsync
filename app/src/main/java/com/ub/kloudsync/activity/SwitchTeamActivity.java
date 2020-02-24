package com.ub.kloudsync.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.HeaderRecyclerAdapter;
import com.kloudsync.techexcel.bean.Team;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.response.TeamsResponse;
import com.kloudsync.techexcel.search.ui.TeamSearchActivity;
import com.ub.techexcel.adapter.NewTeamAdapter;
import com.ub.techexcel.service.ConnectService;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SwitchTeamActivity extends Activity implements View.OnClickListener {

    private RecyclerView mTeamRecyclerView;
    private NewTeamAdapter mTeamAdapter;
    private List<Team> mCurrentTeamData = new ArrayList<>();
    private LinearLayout lin_add;
    private RelativeLayout backLayout;
    private Button createbtn;
    private LinearLayout searchLayout;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private TextView titleText;
    private boolean isSync;
    int role;
    Switch allSwitch;
    RelativeLayout switchLayout;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case AppConfig.AddOrUpdateUserPreference:
                    String result = (String) msg.obj;
                    SaveSchoolInfo();
                    break;
                case AppConfig.FAILED:
                    result = (String) msg.obj;
                    Toast.makeText(getApplicationContext(), result,
                            Toast.LENGTH_LONG).show();
                    break;

                default:
                    break;
            }
        }

    };
    private TextView mTvCreateProject;

    private void SaveSchoolInfo() {
        editor = sharedPreferences.edit();
        editor.putString("teamname", teamSpaceBeans.getName());
        editor.putInt("teamid", teamSpaceBeans.getItemID());
        editor.commit();
        EventBus.getDefault().post(new TeamSpaceBean());
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.switchteamlayout);
        isSync = getIntent().getBooleanExtra("isSync", false);
        role = getIntent().getIntExtra("role", 0);
        initView();
        getTeamList();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setHeaderByRole(int role) {
        /*if (role == 7 || role == 8) {
            View addHeader = getLayoutInflater().inflate(R.layout.add_team_header, mTeamRecyclerView, false);
            lin_add = (LinearLayout) addHeader.findViewById(R.id.lin_add);
            lin_add.setOnClickListener(this);
            mTeamAdapter.setHeaderView(addHeader);
            switchLayout.setVisibility(View.VISIBLE);
        } else {
            switchLayout.setVisibility(View.VISIBLE);
        }*/
    }

    private void initView() {
        mTeamRecyclerView = (RecyclerView) findViewById(R.id.recycleview);
        mTeamRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        backLayout = (RelativeLayout) findViewById(R.id.layout_back);
        backLayout.setOnClickListener(this);
        createbtn = (Button) findViewById(R.id.createbtn);
        createbtn.setOnClickListener(this);
        mTeamAdapter = new NewTeamAdapter();
        allSwitch = findViewById(R.id.switch_all_teams);
        switchLayout = findViewById(R.id.layout_switch);
        mTvCreateProject = findViewById(R.id.tv_create_project);
        mTvCreateProject.setOnClickListener(this);
        allSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getTeamListBySwitch(isChecked);
            }
        });
        switchLayout.setOnClickListener(this);
//        setHeaderByRole(role);
        mTeamAdapter.setOnItemClickListener(new HeaderRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Object data) {
                teamSpaceBeans = (Team) data;
                for (int i = 0; i < mCurrentTeamData.size(); i++) {
                    Team team = mCurrentTeamData.get(i);
                    if (team.getItemID() == ((Team) data).getItemID()) {
                        team.setSelected(true);
                    } else {
                        team.setSelected(false);
                    }
                }
                mTeamAdapter.notifyDataSetChanged();
                SwitchOK();
            }
        });
        mTeamRecyclerView.setAdapter(mTeamAdapter);
        searchLayout = findViewById(R.id.search_layout);
        searchLayout.setOnClickListener(this);
        titleText = findViewById(R.id.tv_title);
        titleText.setText("Switch Team");
    }

    public void getTeamList() {
        ServiceInterfaceTools.getinstance().getCompanyTeams(AppConfig.SchoolID + "").enqueue(new Callback<TeamsResponse>() {
            @Override
            public void onResponse(Call<TeamsResponse> call, Response<TeamsResponse> response) {
                sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                        MODE_PRIVATE);
                int itemid = sharedPreferences.getInt("teamid", 0);
                if (response != null && response.isSuccessful()) {
                    List<Team> list = response.body().getRetData();
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    mCurrentTeamData.clear();
                    mCurrentTeamData.addAll(list);
                    for (int i = 0; i < mCurrentTeamData.size(); i++) {
                        Team team = mCurrentTeamData.get(i);
                        if (team.getItemID() == itemid) {
                            team.setSelected(true);
                        } else {
                            team.setSelected(false);
                        }
                    }
                    mTeamAdapter.setDatas(mCurrentTeamData);
                }

            }

            @Override
            public void onFailure(Call<TeamsResponse> call, Throwable t) {

            }
        });

    }


    private static final int REQUEST_CREATE_NEW_TEAM = 1;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*case R.id.lin_add:
                Intent intent = new Intent(this, CreateNewTeamActivityV2.class);
                startActivityForResult(intent, REQUEST_CREATE_NEW_TEAM);
                break;*/
            case R.id.tv_create_project:
                Intent intent = new Intent(this, CreateNewTeamActivityV2.class);
                startActivityForResult(intent, REQUEST_CREATE_NEW_TEAM);
                break;
            case R.id.layout_back:
                finish();
                break;
            case R.id.createbtn:
                SwitchOK();
                break;
            case R.id.search_layout:
                Intent searchInent = new Intent(this, TeamSearchActivity.class);
                startActivity(searchInent);
                break;
            case R.id.switch_all_teams:
                allSwitch.setChecked(!allSwitch.isChecked());
                break;

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CREATE_NEW_TEAM) {
            getTeamList();
        }
    }

    private void SwitchOK() {

        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        editor = sharedPreferences.edit();
        AUUserInfo();
    }

    private void AUUserInfo() {

        final JSONObject jsonObject = format();
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject responsedata = ConnectService.submitDataByJson(
                            AppConfig.URL_PUBLIC
                                    + "User/AddOrUpdateUserPreference", jsonObject);
                    Log.e("返回的jsonObject", jsonObject.toString() + "");
                    Log.e("返回的responsedata", responsedata.toString() + "");
                    String retcode = responsedata.getString("RetCode");
                    Message msg = new Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        msg.what = AppConfig.AddOrUpdateUserPreference;
                        msg.obj = responsedata.toString();
                    } else {
                        msg.what = AppConfig.FAILED;
                        String ErrorMessage = responsedata.getString("ErrorMessage");
                        msg.obj = ErrorMessage;
                    }
                    handler.sendMessage(msg);
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

    Team teamSpaceBeans = new Team();

    private JSONObject format2() {
        JSONObject jsonObject = new JSONObject();
        try {

            for (int i = 0; i < mCurrentTeamData.size(); i++) {
                Team team = mCurrentTeamData.get(i);
                if (team.isSelected()) {
                    jsonObject.put("TeamID", team.getItemID());
                    jsonObject.put("TeamName", TextUtils.isEmpty(team.getName()) ? "" : team.getName());
                    teamSpaceBeans = team;
                    break;
                }
            }
            jsonObject.put("SchoolID", sharedPreferences.getInt("SchoolID", -1));
            jsonObject.put("SchoolName", sharedPreferences.getString("SchoolName", null));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonObject;
    }

    private void getTeamListBySwitch(boolean isChecked) {
        if (isChecked) {
            getAllTeamList();
        } else {
            getTeamList();
        }
    }

    public void getAllTeamList() {

        ServiceInterfaceTools.getinstance().getAllTeams(AppConfig.SchoolID + "").enqueue(new Callback<TeamsResponse>() {
            @Override
            public void onResponse(Call<TeamsResponse> call, Response<TeamsResponse> response) {
                if (response != null && response.isSuccessful()) {
                    List<Team> list = response.body().getRetData();
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    mCurrentTeamData.clear();
                    mCurrentTeamData.addAll(list);
                    int itemid = sharedPreferences.getInt("teamid", 0);
                    for (int i = 0; i < mCurrentTeamData.size(); i++) {
                        Team team = mCurrentTeamData.get(i);
                        if (team.getItemID() == itemid) {
                            team.setSelected(true);
                        } else {
                            team.setSelected(false);
                        }
                    }
                    mTeamAdapter.setDatas(mCurrentTeamData);
                } else {
                    getAllTeamsFailed();
                }
            }

            @Override
            public void onFailure(Call<TeamsResponse> call, Throwable t) {
                getAllTeamsFailed();
            }
        });

    }

    private void getAllTeamsFailed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), R.string.operate_failure, Toast.LENGTH_SHORT).show();
                allSwitch.setChecked(false);
            }
        });
    }


}
