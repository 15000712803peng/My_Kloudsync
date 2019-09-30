package com.kloudsync.techexcel.search.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.HeaderRecyclerAdapter;
import com.kloudsync.techexcel.adapter.OrganizationAdapter;
import com.kloudsync.techexcel.app.BaseActivity;
import com.kloudsync.techexcel.bean.Team;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.info.School;
import com.kloudsync.techexcel.response.NResponse;
import com.kloudsync.techexcel.response.OrganizationsResponse;
import com.kloudsync.techexcel.search.view.VOrganizationSearch;
import com.kloudsync.techexcel.ui.MainActivity;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.kloudsync.activity.TeamSpaceInterfaceListener;
import com.ub.kloudsync.activity.TeamSpaceInterfaceTools;
import com.ub.techexcel.adapter.TeamAdapterV2;
import com.ub.techexcel.service.ConnectService;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class OrganizationSearchActivity extends BaseActivity implements VOrganizationSearch, View.OnClickListener, TextWatcher, TeamAdapterV2.OnItemClickListener {

    private RecyclerView organizationList;
    private TextView cancelText;
    String searchStr;
    EditText searchEdit;
    private ImageView clearEditImage;
    private RelativeLayout noDataLayout;
    private ProgressBar loadingBar;
    private TextView messageText;
    SharedPreferences userPreferences;
    OrganizationAdapter adapter;
    int schoolId;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case AppConfig.FAILED:
                    Toast.makeText(getApplicationContext(), R.string.operate_failure, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void saveAndRefresh(School school, TeamSpaceBean teamSpaceBean) {
        SharedPreferences.Editor editor = userPreferences.edit();
        AppConfig.SchoolID = school.getSchoolID();
        editor.putInt("SchoolID", school.getSchoolID());
        editor.putString("SchoolName", school.getSchoolName());
        editor.putString("teamname", teamSpaceBean.getName());
        editor.putInt("teamid", teamSpaceBean.getItemID());
        editor.commit();
        EventBus.getDefault().post(new TeamSpaceBean());
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        schoolId = getIntent().getIntExtra("school_id", 0);
        adapter = new OrganizationAdapter(schoolId);
        organizationList.setAdapter(adapter);
        adapter.setOnItemClickListener(new HeaderRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Object data) {
                School school = (School) data;
                if (school.getSchoolID() != schoolId) {
                    adapter.setSelectedId(school.getSchoolID());
                    getSchoolTeams((School) data);
                }
            }
        });
    }

    @Override
    protected int setLayout() {
        return R.layout.activity_organization_search;
    }

    @Override
    protected void initView() {
        organizationList = findViewById(R.id.list_organization);
        organizationList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        cancelText = findViewById(R.id.tv_cancel);
        cancelText.setOnClickListener(this);
        searchEdit = findViewById(R.id.et_search);
        searchEdit.addTextChangedListener(this);
        clearEditImage = findViewById(R.id.img_clear_edit);
        clearEditImage.setOnClickListener(this);
        noDataLayout = findViewById(R.id.no_data_lay);
        loadingBar = findViewById(R.id.loading_progress);
        messageText = findViewById(R.id.txt_msg);
    }

    @Override
    public void showLoading() {
        loadingBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void showEmpty(String message) {
        noDataLayout.setVisibility(View.VISIBLE);
        loadingBar.setVisibility(View.INVISIBLE);
        organizationList.setVisibility(View.GONE);
        messageText.setText(message);
    }

    @Override
    public void showOrganizations(List<School> schools, String keyword) {
        loadingBar.setVisibility(View.INVISIBLE);
        noDataLayout.setVisibility(View.INVISIBLE);
//        teamAdapter.setTeams(teams);
        organizationList.setVisibility(View.VISIBLE);
        adapter.setKeyword(keyword);
        adapter.setDatas(schools);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                hideInput();
                finish();
                break;
            case R.id.img_clear_edit:
                searchEdit.setText("");
            default:
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        handler.removeCallbacks(editRunnable);
        handler.postDelayed(editRunnable, 600);
    }


    private Runnable editRunnable = new Runnable() {
        @Override
        public void run() {
            editCompleted();
        }
    };

    private void editCompleted() {
        searchStr = searchEdit.getText().toString().trim();
        if (TextUtils.isEmpty(searchStr)) {
            showEmpty("");
            return;
        }
        search(searchStr, this);
    }

    private void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && getCurrentFocus() != null) {
            if (getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideInput();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void search(final String searchStr, final VOrganizationSearch view) {
        showLoading();
        Observable.just(searchStr).observeOn(Schedulers.io()).map(new Function<String, NResponse<OrganizationsResponse>>() {
            @Override
            public NResponse<OrganizationsResponse> apply(String searchStr) throws Exception {
                NResponse<OrganizationsResponse> response = new NResponse<>();
                try {
                    response.setResponse(ServiceInterfaceTools.getinstance().searchOrganizations(searchStr).execute());
                } catch (UnknownHostException e) {
                    return response.setNull(true);
                } catch (SocketTimeoutException exception) {
                    return response.setNull(true);
                }
                return response;
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<NResponse<OrganizationsResponse>>() {
            @Override
            public void accept(NResponse<OrganizationsResponse> response) throws Exception {
                handleResponse(response, searchStr);
            }
        }).subscribe();

    }

    private void handleResponse(NResponse<OrganizationsResponse> res, String keyword) {
        if (res == null || res.isNull()) {
            showEmpty(getString(R.string.rc_network_error));
            return;
        }
        Response<OrganizationsResponse> response = res.getResponse();
        if (response.isSuccessful()) {
            int errorCode = response.body().getRetCode();
            if (errorCode == 0) {
                List<School> schools = response.body().getRetData();
                if (schools != null && schools.size() > 0) {
                    showOrganizations(schools, keyword);
                } else {
                    showEmpty(getString(R.string.no_data));
                }
            } else {
                showEmpty(response.body().getErrorMessage());
            }

        }

    }

    @Override
    public void onItemClick(Team teamData) {

    }

    ;

    public void getSchoolTeams(final School school) {
        TeamSpaceInterfaceTools.getinstance().getTeamSpaceList(AppConfig.URL_PUBLIC + "TeamSpace/List?companyID=" + school.getSchoolID() + "&type=1&parentID=0",
                TeamSpaceInterfaceTools.GETTEAMSPACELIST, new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        List<TeamSpaceBean> list = (List<TeamSpaceBean>) object;
                        TeamSpaceBean teamSpaceBean = new TeamSpaceBean();
                        if (list.size() > 0) {
                            teamSpaceBean = list.get(0);
                        }
                        updateUserInfo(school, teamSpaceBean);
                    }
                });

    }

    private void updateUserInfo(final School school, final TeamSpaceBean teamSpaceBean) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("FieldID", 10001);
            JSONObject obj = new JSONObject();
            obj.put("SchoolID", school.getSchoolID());
            obj.put("TeamID", teamSpaceBean.getItemID());
            obj.put("SchoolName", school.getSchoolName());
            obj.put("TeamName", TextUtils.isEmpty(teamSpaceBean.getName()) ? "" : teamSpaceBean.getName());
            jsonObject.put("PreferenceText", obj + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                saveAndRefresh(school, teamSpaceBean);
                            }
                        });
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

}
