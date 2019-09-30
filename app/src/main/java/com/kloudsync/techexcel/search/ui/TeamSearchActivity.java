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
import com.kloudsync.techexcel.app.BaseActivity;
import com.kloudsync.techexcel.bean.Team;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.response.NResponse;
import com.kloudsync.techexcel.response.TeamSearchResponse;
import com.kloudsync.techexcel.search.view.VTeamSearch;
import com.kloudsync.techexcel.ui.MainActivity;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.techexcel.adapter.TeamAdapterV2;
import com.ub.techexcel.service.ConnectService;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class TeamSearchActivity extends BaseActivity implements VTeamSearch, View.OnClickListener, TextWatcher, TeamAdapterV2.OnItemClickListener {

    private RecyclerView teamList;
    private TeamAdapterV2 teamAdapter;
    private TextView cancelText;
    String searchStr;
    EditText searchEdit;
    private ImageView clearEditImage;
    private RelativeLayout noDataLayout;
    private ProgressBar loadingBar;
    int companyID = -1;
    int teamID = -1;
    private TextView messageText;
    int currentTeamId;
    SharedPreferences userPreferences;
    private static final int MSG_UPDAE_TEAM_SUCC = 100;
    private static final int MSG_UPDAE_TEAM_FAIL = 101;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDAE_TEAM_SUCC:
                    switchOK((JSONObject) msg.obj);
                    break;
                case MSG_UPDAE_TEAM_FAIL:
                    if (teamAdapter != null) {
                        teamAdapter.setCurrentTeamId(currentTeamId);
                        teamAdapter.notifyDataSetChanged();
                    }
                    Toast.makeText(getApplication(), R.string.operate_failure, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        teamAdapter = new TeamAdapterV2(this, new ArrayList<Team>());
        teamAdapter.setOnItemClickListener(this);
        teamList.setAdapter(teamAdapter);
        userPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        currentTeamId = userPreferences.getInt("teamid", 0);
    }

    @Override
    protected int setLayout() {
        return R.layout.activity_team_search;
    }

    @Override
    protected void initView() {
        companyID = getIntent().getIntExtra("company_id", -1);
        teamID = getIntent().getIntExtra("team_id", -1);
        teamList = findViewById(R.id.list_team);
        teamList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
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
        teamList.setVisibility(View.GONE);
        messageText.setText(message);
    }

    @Override
    public void showTeams(List<Team> teams, String keyword) {
        loadingBar.setVisibility(View.INVISIBLE);
        noDataLayout.setVisibility(View.INVISIBLE);
        teamAdapter.setCurrentTeamId(currentTeamId);
        teamAdapter.setKeyword(keyword);
        teamAdapter.setTeams(teams);
        teamList.setVisibility(View.VISIBLE);

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

    private void search(final String searchStr, final VTeamSearch view) {
        showLoading();
        Observable.just(searchStr).observeOn(Schedulers.io()).map(new Function<String, NResponse<TeamSearchResponse>>() {
            @Override
            public NResponse<TeamSearchResponse> apply(String searchStr) throws Exception {
                NResponse<TeamSearchResponse> response = new NResponse<>();
                try {
                    response.setResponse(ServiceInterfaceTools.getinstance().searchTeams(searchStr).execute());
                } catch (UnknownHostException e) {
                    return response.setNull(true);
                } catch (SocketTimeoutException exception) {
                    return response.setNull(true);
                }
                return response;
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<NResponse<TeamSearchResponse>>() {
            @Override
            public void accept(NResponse<TeamSearchResponse> teamSearchResponseResponse) throws Exception {
                handleResponse(teamSearchResponseResponse, searchStr);
            }
        }).subscribe();

    }

    private void handleResponse(NResponse<TeamSearchResponse> res, String keyword) {
        if (res == null || res.isNull()) {
            showEmpty(getString(R.string.rc_network_error));
            return;
        }
        Response<TeamSearchResponse> response = res.getResponse();
        if (response.isSuccessful()) {
            int errorCode = response.body().getRetCode();
            if (errorCode == 0) {
                List<Team> teams = response.body().getRetData();
                if (teams != null && teams.size() > 0) {
                    showTeams(teams, keyword);
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
        teamAdapter.setCurrentTeamId(teamData.getItemID());
        teamAdapter.notifyDataSetChanged();
        requestUpdateTeam(teamData);
    }


    private void requestUpdateTeam(final Team team) {

        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject params = prepareParams(team);
                    JSONObject responsedata = ConnectService.submitDataByJson(
                            AppConfig.URL_PUBLIC
                                    + "User/AddOrUpdateUserPreference", params);
                    Log.e("response", responsedata + "");
                    String retcode = responsedata.getString("RetCode");
                    Message msg = new Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        msg.what = MSG_UPDAE_TEAM_SUCC;
                        msg.obj = params;
                    } else {
                        msg.what = MSG_UPDAE_TEAM_FAIL;
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

    private JSONObject prepareParams(Team team) {
        JSONObject params = new JSONObject();
        JSONObject text = new JSONObject();
        try {
            params.put("FieldID", 10001);
            text.put("TeamID", team.getItemID());
            text.put("TeamName", TextUtils.isEmpty(team.getName()) ? "" : team.getName());
            text.put("SchoolID", userPreferences.getInt("SchoolID", -1));
            text.put("SchoolName", userPreferences.getString("SchoolName", null));
            params.put("PreferenceText", text + "");
            params.put("TextObj", text);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return params;
    }

    private void switchOK(JSONObject params) {
        try {
            userPreferences.edit().putInt("teamid", params.getJSONObject("TextObj").getInt("TeamID")).commit();
            userPreferences.edit().putString("teamname", params.getJSONObject("TextObj").getString("TeamName")).commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), R.string.operate_success, Toast.LENGTH_SHORT).show();
        hideInput();
        EventBus.getDefault().post(new TeamSpaceBean());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(TeamSearchActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }, 300);

    }
}
