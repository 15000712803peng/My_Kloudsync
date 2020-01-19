package com.kloudsync.techexcel.school;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.HeaderRecyclerAdapter;
import com.kloudsync.techexcel.adapter.OrganizationAdapter;
import com.kloudsync.techexcel.bean.Company;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.DialogSelectSchool;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.info.School;
import com.kloudsync.techexcel.response.InvitationsResponse;
import com.kloudsync.techexcel.search.ui.OrganizationSearchActivity;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.ui.InvitationsActivity;
import com.kloudsync.techexcel.view.ClearEditText;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.kloudsync.activity.TeamSpaceInterfaceListener;
import com.ub.kloudsync.activity.TeamSpaceInterfaceTools;
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

public class SelectSchoolActivity extends Activity implements View.OnClickListener {

    private RecyclerView organiztionList;
    private LinearLayout lin_main;
    private RelativeLayout backLayout;
    private ClearEditText et_search;
    private TextView tv_OK;
    private ArrayList<School> mlist = new ArrayList<>();
    private ArrayList<School> eList = new ArrayList<>();
    private OrganizationAdapter sAdapter;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private School school;
    private TeamSpaceBean teamSpaceBean = new TeamSpaceBean();
    private LinearLayout invitationsLayout;
    private TextView invitationsText;
    private TextView titleText;
    private TextView rightTitleText;
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

    private void SaveSchoolInfo() {
        editor = sharedPreferences.edit();
        AppConfig.SchoolID = school.getSchoolID();
        editor.putInt("SchoolID", school.getSchoolID());
        editor.putString("SchoolName", school.getSchoolName());
        editor.putString("teamname", teamSpaceBean.getName());
        editor.putInt("teamid", teamSpaceBean.getItemID());
        editor.commit();
        EventBus.getDefault().post(new TeamSpaceBean());
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_school);
        findView();
        initView();
        //获取组织信息
        getAllSchool();
        //检测是否有人邀请
//        getInvitations();
    }

    private void getAllSchool() {
        LoginGet loginGet = new LoginGet();
        loginGet.setMySchoolGetListener(new LoginGet.MySchoolGetListener() {
            @Override
            public void getSchool(ArrayList<School> list) {
//                School.selectedId = GetSaveInfo();
//                Collections.sort(list);
                mlist = list;
                sAdapter.setDatas(mlist, GetSaveInfo());
                SetMySchool();
            }
        });
        loginGet.GetSchoolInfo(getApplicationContext());
    }

    private void SetMySchool() {
        int id = GetSaveInfo();
        for (int i = 0; i < mlist.size(); i++) {
            if (mlist.get(i).getSchoolID() == id) {
                school = mlist.get(i);
                break;
            }
        }
    }

    LinearLayout searchLayout;
    private void initView() {
//        editSchool();
        sAdapter = new OrganizationAdapter(GetSaveInfo());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        organiztionList.setLayoutManager(layoutManager);
        View headerView = getLayoutInflater().inflate(R.layout.organization_header, organiztionList, false);
        searchLayout = headerView.findViewById(R.id.search_layout);
        searchLayout.setOnClickListener(this);
        sAdapter.setHeaderView(headerView);
        //搜索
        sAdapter.setOnItemClickListener(new HeaderRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Object data) {
                school = (School) data;
                sAdapter.setDatas(mlist, school.getSchoolID());
                if (school.getSchoolID() != GetSaveInfo()) {
                    getMyTeamList();
                }
            }
        });
        organiztionList.setAdapter(sAdapter);
        backLayout.setOnClickListener(this);
        tv_OK.setOnClickListener(this);
        invitationsLayout = (LinearLayout) headerView.findViewById(R.id.layout_invitations);
        invitationsText = (TextView) headerView.findViewById(R.id.txt_invitations);
        invitationsLayout.setOnClickListener(this);
        titleText.setText(getResources().getString(R.string.pc_sorganization));
        rightTitleText.setVisibility(View.GONE);
    }

    private int GetSaveInfo() {
        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        return sharedPreferences.getInt("SchoolID", -1);
    }

    private void findView() {
        backLayout = (RelativeLayout) findViewById(R.id.layout_back);
        organiztionList = (RecyclerView) findViewById(R.id.rv_ss);
        et_search = (ClearEditText) findViewById(R.id.et_search);
        tv_OK = (TextView) findViewById(R.id.tv_OK);
        lin_main = (LinearLayout) findViewById(R.id.lin_main);
        titleText = (TextView) findViewById(R.id.tv_title);
        rightTitleText = (TextView) findViewById(R.id.txt_right_title);
    }

    private void editSchool() {
        et_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                eList.clear();
                for (int i = 0; i < mlist.size(); i++) {
                    School sc = mlist.get(i);
                    String name = et_search.getText().toString();
                    String getName = sc.getSchoolName().toLowerCase();//转小写
                    String nameb = name.toLowerCase();//转小写
                    if (getName.contains(nameb.toString())
                            && name.length() > 0) {
                        eList.add(sc);
                    }
                }
//                if (et_search.length() != 0) {
//                    sAdapter.UpdateRV2(eList);
//                } else {
//                    sAdapter.UpdateRV2(mlist);
//                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_back:
//                ActivityCompat.finishAfterTransition(SelectSchoolActivity.this);
                finish();
                break;
            case R.id.tv_OK:
                ShowPop(v);
                break;
            case R.id.layout_invitations:
                if (companies != null && companies.size() > 0) {
                    Intent intent = new Intent(this, InvitationsActivity.class);
                    intent.putExtra("companies", new Gson().toJson(companies));
                    intent.putExtra("from", 2);
                    startActivity(intent);
                }
                break;
            case R.id.search_layout:
                Intent searchIntent = new Intent(this, OrganizationSearchActivity.class);
                searchIntent.putExtra("school_id", GetSaveInfo());
                startActivity(searchIntent);
                break;
        }
    }


    private void ShowPop(View v) {

        if (school == null || mlist.size() == 0) {
            return;
        }
        DialogSelectSchool ds = new DialogSelectSchool();
        ds.setPoPDismissListener(new DialogSelectSchool.DialogDismissListener() {
            @Override
            public void PopSelect(boolean isSelect) {
                BackChange(1.0f);
                if (isSelect) {
                    if (school.getSchoolID() != GetSaveInfo()) {
                        getMyTeamList();
                    } else {
//                        ActivityCompat.finishAfterTransition(SelectSchoolActivity.this);
                        finish();
                    }
                }
            }
        });
        ds.EditCancel(SelectSchoolActivity.this, school);
        BackChange(0.5f);

    }

    public void getMyTeamList() {

        TeamSpaceInterfaceTools.getinstance().getTeamSpaceList(AppConfig.URL_PUBLIC + "TeamSpace/List?companyID=" + school.getSchoolID() + "&type=1&parentID=0",
                TeamSpaceInterfaceTools.GETTEAMSPACELIST, new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        List<TeamSpaceBean> list = (List<TeamSpaceBean>) object;
                        Log.e("ddddddd", list.size() + "");
                        teamSpaceBean = new TeamSpaceBean();
                        if (list.size() > 0) {
                            teamSpaceBean = list.get(0);
                        }
                        AUUserInfo();
                    }
                });

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

    private JSONObject format2() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("SchoolID", school.getSchoolID());
            jsonObject.put("TeamID", teamSpaceBean.getItemID());
            jsonObject.put("SchoolName", school.getSchoolName());
            jsonObject.put("TeamName", TextUtils.isEmpty(teamSpaceBean.getName()) ? "" : teamSpaceBean.getName());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void BackChange(float value) {
        lin_main.animate().alpha(value);
        lin_main.animate().setDuration(500);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    //检测是否有人邀请
//    private void getInvitations() {
//        ServiceInterfaceTools.getinstance().getInvitations().enqueue(new Callback<InvitationsResponse>() {
//            @Override
//            public void onResponse(Call<InvitationsResponse> call, Response<InvitationsResponse> response) {
//                if (response != null && response.isSuccessful()) {
//                    handleInvitations(response.body().getRetData());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<InvitationsResponse> call, Throwable t) {
//
//            }
//        });
//    }

    public void handleInvitations(List<Company> companies) {
        this.companies = companies;
        if (companies == null || companies.size() == 0) {
            invitationsLayout.setVisibility(View.GONE);
            invitationsText.setVisibility(View.GONE);
        } else {
            invitationsLayout.setVisibility(View.VISIBLE);
            invitationsText.setVisibility(View.VISIBLE);
            invitationsText.setText("There are " + companies.size() + " organization invited you to join,do you want to confirm the invitation now?");
        }

    }

    private List<Company> companies;

}
