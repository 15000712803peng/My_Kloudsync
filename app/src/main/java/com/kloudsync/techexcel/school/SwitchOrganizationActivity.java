package com.kloudsync.techexcel.school;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.OrganizationAdapterV2;
import com.kloudsync.techexcel.bean.Company;
import com.kloudsync.techexcel.bean.CompanySubsystem;
import com.kloudsync.techexcel.bean.EventRefreshTab;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.DialogSelectSchool;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.info.School;
import com.kloudsync.techexcel.response.InvitationsResponse;
import com.kloudsync.techexcel.search.ui.OrganizationSearchActivity;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.tool.CustomSyncRoomTool;
import com.kloudsync.techexcel.ui.InvitationsActivity;
import com.kloudsync.techexcel.ui.MainActivity;
import com.kloudsync.techexcel.view.ClearEditText;
import com.ub.kloudsync.activity.SwitchSpaceActivity;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.kloudsync.activity.TeamSpaceInterfaceListener;
import com.ub.kloudsync.activity.TeamSpaceInterfaceTools;
import com.ub.techexcel.service.ConnectService;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SwitchOrganizationActivity extends Activity implements View.OnClickListener {

    private ExpandableListView organiztionList;
    private LinearLayout lin_main;
    private RelativeLayout backLayout;
    private ClearEditText et_search;
    private TextView okText;
    private ArrayList<School> mlist = new ArrayList<>();
    private ArrayList<School> eList = new ArrayList<>();
    private OrganizationAdapterV2 sAdapter;
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
//                    SaveSchoolInfo();
                    GetSchoolInfo();
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

    private void GetSchoolInfo() {
        editor = sharedPreferences.edit();
        LoginGet lg = new LoginGet();
        lg.setSchoolTeamGetListener(new LoginGet.SchoolTeamGetListener() {
            @Override
            public void getST(School school) {
                Log.e("GetShoolInfo", "school:" + school);
                if (school != null) {
                    TeamSpaceBean teamSpaceBean = school.getTeamSpaceBean();
                    List<CompanySubsystem> companySubsystems = school.getSubsystems();
                    if (companySubsystems == null || companySubsystems.size() == 0) { //没有选子项
                        AppConfig.selectedSubSystemId = "";
                        AppConfig.SchoolID = school.getSchoolID();
                        editor.putInt("SchoolID", AppConfig.SchoolID);
                        editor.putString("SchoolName", school.getSchoolName());
                    } else {
                        AppConfig.selectedSubSystemId = school.getSubsystems().get(0).getSubSystemId();
                        AppConfig.SchoolID = Integer.parseInt(AppConfig.selectedSubSystemId);
                        editor.putInt("SchoolID", AppConfig.SchoolID);
                        editor.putString("SchoolName", school.getSubsystems().get(0).getSubSystemName());
                    }
                    editor.putInt("realSchoolID", school.getSchoolID());
                    editor.putString("teamname", teamSpaceBean.getName());
                    editor.putInt("teamid", teamSpaceBean.getItemID());
                    editor.commit();
                } else {
                    editor.putString("SchoolName", "");
                    editor.putString("teamname", "");
                    editor.putInt("teamid", -1);
                    editor.commit();
                }
                getCompanyNameList();
            }
        });
        lg.GetUserPreference(this, 10001 + "");
    }

    public void getCompanyNameList() {
        String url = AppConfig.URL_MEETING_BASE + "company_custom_display_name/name_list?companyId=" + AppConfig.SchoolID;
        ServiceInterfaceTools.getinstance().getCompanyDisplayNameList(url, ServiceInterfaceTools.GETCOMPANYDISPLAYNAMELIST, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                JSONObject jsonObject = (JSONObject) object;
                CustomSyncRoomTool.getInstance(SwitchOrganizationActivity.this).setCustomSyncRoomContent(jsonObject);
                MainActivity.RESUME = true;
                EventBus.getDefault().post(new TeamSpaceBean());
//                EventBus.getDefault().post(new EventRefreshTab());
                finish();
            }
        });
    }


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
        setContentView(R.layout.activity_switch_organization);
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
                mlist = list;
                sAdapter.setCompanies(mlist);
                okText.setVisibility(View.VISIBLE);
                int index = sAdapter.setSelectCompany(GetSaveInfo(), AppConfig.selectedSubSystemId);
                if (index > 0) {
//                    organiztionList.expandGroup(index);
                    getCompanySubsystemsAndExpand(sAdapter.getSelectCompanyId(), index);
                }
                SetMySchool();
            }
        });
        loginGet.GetSchoolInfo(getApplicationContext());
    }


    private void getCompanySubsystemsAndExpand(final String companyId, final int index) {
        LoginGet loginGet = new LoginGet();
        loginGet.setMyCompanySubsystemsGetListener(new LoginGet.MyCompanySubsystemsGetListener() {
            @Override
            public void getCompanySubsystems(List<CompanySubsystem> list) {
                if (list != null && list.size() > 0) {
                    sAdapter.setSubsystems(list, Integer.parseInt(companyId), AppConfig.selectedSubSystemId);
                    organiztionList.expandGroup(index);
                }
            }
        });
        loginGet.getCompanySubsystems(this, companyId);
    }


    private void getCompanySubsystems(final String companyId) {
        LoginGet loginGet = new LoginGet();
        loginGet.setMyCompanySubsystemsGetListener(new LoginGet.MyCompanySubsystemsGetListener() {
            @Override
            public void getCompanySubsystems(List<CompanySubsystem> list) {
                if (list != null && list.size() > 0) {
                    sAdapter.setSubsystems(list, Integer.parseInt(companyId), AppConfig.selectedSubSystemId);
                }
            }
        });
        loginGet.getCompanySubsystems(this, companyId);
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
        sAdapter = new OrganizationAdapterV2(this);
        View headerView = getLayoutInflater().inflate(R.layout.organization_header, organiztionList, false);
        searchLayout = headerView.findViewById(R.id.search_layout);
        searchLayout.setOnClickListener(this);
        organiztionList.addHeaderView(headerView);
        organiztionList.setAdapter(sAdapter);
        organiztionList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (!sAdapter.getSelectCompanyId().equals(id + "")) {
                    if (!TextUtils.isEmpty(sAdapter.getSelectCompanyId())) {
                        sAdapter.clearSelectedSubsystem(Integer.parseInt(sAdapter.getSelectCompanyId()));
                    }
                    sAdapter.setSelectCompany(Integer.parseInt(id + ""), "");
                    List<CompanySubsystem> subsystems = sAdapter.getSelectCompany().getSubsystems();
                    if (subsystems != null && subsystems.size() > 0) {
                        for (CompanySubsystem subsystem : subsystems) {
                            subsystem.setSelected(false);
                        }
                    }
                    sAdapter.notifyDataSetChanged();
                } else {
//                    organiztionList.
                    List<CompanySubsystem> subsystems = sAdapter.getSelectCompany().getSubsystems();
                    if (subsystems != null && subsystems.size() > 0) {
                        for (CompanySubsystem subsystem : subsystems) {
                            subsystem.setSelected(false);
                        }
                    }
                    sAdapter.getSelectCompany().setSubSystemSelected(false);
                    sAdapter.notifyDataSetChanged();
                }
                if (sAdapter.getGroup(groupPosition).getSubsystems() == null) {
                    getCompanySubsystems(id + "");
                }
                return false;
            }
        });
        organiztionList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                for (int i = 0; i < sAdapter.getGroupCount(); ++i) {
                    if (i == groupPosition) {
                        continue;
                    }
                    organiztionList.collapseGroup(i);

                }
            }
        });
        organiztionList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Log.e("onChildClick", "clicked");
                CompanySubsystem subsystem = sAdapter.getGroup(groupPosition).getSubsystems().get(childPosition);
                for (CompanySubsystem subsystem1 : sAdapter.getGroup(groupPosition).getSubsystems()) {
                    subsystem1.setSelected(false);
                }
                subsystem.setSelected(!subsystem.isSelected());
                sAdapter.getGroup(groupPosition).setSubSystemSelected(true);
                sAdapter.notifyDataSetChanged();

                return false;
            }
        });
        backLayout.setOnClickListener(this);
        okText.setOnClickListener(this);
        invitationsLayout = (LinearLayout) headerView.findViewById(R.id.layout_invitations);
        invitationsText = (TextView) headerView.findViewById(R.id.txt_invitations);
        invitationsLayout.setOnClickListener(this);
        titleText.setText(getResources().getString(R.string.pc_sorganization));
        rightTitleText.setVisibility(View.GONE);
    }




    private int GetSaveInfo() {
        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        return sharedPreferences.getInt("realSchoolID", -1);
    }

    private void findView() {
        backLayout = (RelativeLayout) findViewById(R.id.layout_back);
        organiztionList = (ExpandableListView) findViewById(R.id.list_organization);
        et_search = (ClearEditText) findViewById(R.id.et_search);
        okText = (TextView) findViewById(R.id.txt_ok);
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
            case R.id.txt_ok:
                if (sAdapter.getSelectCompany() != null) {
                    school = sAdapter.getSelectCompany();
                    getMyTeamList();
                }
//                ShowPop(v);
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
        ds.EditCancel(SwitchOrganizationActivity.this, school);
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
                        AUUserInfo(sAdapter.getSelectSubsystem());
                    }
                });

    }


    private void AUUserInfo(CompanySubsystem subsystem) {

        final JSONObject jsonObject = format(subsystem);
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

    private JSONObject format(CompanySubsystem subsystem) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("FieldID", 10001);
//            jsonObject.put("PreferenceValue", 0);
            jsonObject.put("PreferenceText", format2(subsystem) + "");
//            jsonObject.put("PreferenceMemo", "");
        } catch (JSONException e) {
            // TODO Auto-generated catch block

            e.printStackTrace();
        }

        return jsonObject;
    }

    private JSONObject format2(CompanySubsystem companySubsystem) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("SchoolID", school.getSchoolID());
            jsonObject.put("SchoolName", school.getSchoolName());
            if (companySubsystem != null) {
                jsonObject.put("TeamID", -1);
                jsonObject.put("TeamName", "");
                jsonObject.put("SubSystemData", format3(companySubsystem));
            } else {
                if (teamSpaceBean != null) {
                    jsonObject.put("TeamId", teamSpaceBean.getItemID());
                    jsonObject.put("TeamName", TextUtils.isEmpty(teamSpaceBean.getName()) ? "" : teamSpaceBean.getName());
                } else {
                    jsonObject.put("TeamID", -1);
                    jsonObject.put("TeamName", "");
                }

            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return jsonObject;
    }

    private JSONObject format3(CompanySubsystem subsystem) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("selectedSubSystemId", subsystem.getSubSystemId());
            jsonObject.put("selectedSubSystemType", subsystem.getType());
            jsonObject.put("subSystemName", subsystem.getSubSystemName());

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
    private void getInvitations() {
        ServiceInterfaceTools.getinstance().getInvitations().enqueue(new Callback<InvitationsResponse>() {
            @Override
            public void onResponse(Call<InvitationsResponse> call, Response<InvitationsResponse> response) {
                if (response != null && response.isSuccessful()) {
                    handleInvitations(response.body().getRetData());
                }
            }

            @Override
            public void onFailure(Call<InvitationsResponse> call, Throwable t) {

            }
        });
    }

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
