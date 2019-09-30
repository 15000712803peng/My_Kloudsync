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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.BaseActivity;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.response.NResponse;
import com.kloudsync.techexcel.response.NetworkResponse;
import com.kloudsync.techexcel.search.view.VSpaceSearch;
import com.ub.kloudsync.activity.SpaceDocumentsActivity;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.techexcel.adapter.SpaceAdapter;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import java.io.Serializable;
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

public class SpaceSearchActivity extends BaseActivity implements VSpaceSearch, View.OnClickListener, TextWatcher {

    private RecyclerView list;
    private SpaceAdapter adapter;
    private TextView cancelText;
    String searchStr;
    EditText searchEdit;
    private ImageView clearEditImage;
    private RelativeLayout noDataLayout;
    private ProgressBar loadingBar;
    int teamID = -1;
    private TextView messageText;
    int currentTeamId;
    SharedPreferences userPreferences;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

            }
        }
    };

    private void selectOk(TeamSpaceBean space) {
        adapter.clearSelect();
        space.setSelect(true);
        adapter.notifyDataSetChanged();
        Intent intent = new Intent(this, SpaceDocumentsActivity.class);
        intent.putExtra("selectSpace", (Serializable) space);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new SpaceAdapter(this, new ArrayList<TeamSpaceBean>());
        adapter.setOnItemLectureListener(new SpaceAdapter.OnItemLectureListener() {
            @Override
            public void onItem(TeamSpaceBean teamSpaceBean) {
                selectOk(teamSpaceBean);
            }

            @Override
            public void select(TeamSpaceBean teamSpaceBean) {

            }
        });
        list.setAdapter(adapter);
        userPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        currentTeamId = userPreferences.getInt("teamid", 0);
    }

    @Override
    protected int setLayout() {
        return R.layout.activity_search;
    }

    @Override
    protected void initView() {
        teamID = getIntent().getIntExtra("team_id", -1);
        list = findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
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
        list.setVisibility(View.GONE);
        messageText.setText(message);
    }

    @Override
    public void showSpaces(List<TeamSpaceBean> spaces, String keyword) {
        loadingBar.setVisibility(View.INVISIBLE);
        noDataLayout.setVisibility(View.INVISIBLE);
        adapter.setFromSearch(true, keyword);
        adapter.setSpaces(spaces);
        list.setVisibility(View.VISIBLE);

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

    private void search(final String searchStr, final VSpaceSearch view) {
        showLoading();
        Observable.just(searchStr).observeOn(Schedulers.io()).map(new Function<String, NResponse<NetworkResponse<List<TeamSpaceBean>>>>() {
            @Override
            public NResponse<NetworkResponse<List<TeamSpaceBean>>> apply(String searchStr) throws Exception {
                NResponse<NetworkResponse<List<TeamSpaceBean>>> response = new NResponse<>();
                try {
                    response.setResponse(ServiceInterfaceTools.getinstance().searchSapces(teamID + "", searchStr).execute());
                } catch (UnknownHostException e) {
                    return response.setNull(true);
                } catch (SocketTimeoutException exception) {
                    return response.setNull(true);
                }
                return response;
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<NResponse<NetworkResponse<List<TeamSpaceBean>>>>() {
            @Override
            public void accept(NResponse<NetworkResponse<List<TeamSpaceBean>>> teamSearchResponseResponse) throws Exception {
                handleResponse(teamSearchResponseResponse, searchStr);
            }
        }).subscribe();

    }

    private void handleResponse(NResponse<NetworkResponse<List<TeamSpaceBean>>> res, String keyword) {
        if (res == null || res.isNull()) {
            showEmpty(getString(R.string.rc_network_error));
            return;
        }
        Response<NetworkResponse<List<TeamSpaceBean>>> response = res.getResponse();
        if (response.isSuccessful()) {
            int errorCode = response.body().getRetCode();
            if (errorCode == 0) {
                List<TeamSpaceBean> teams = response.body().getRetData();
                if (teams != null && teams.size() > 0) {
                    showSpaces(teams, keyword);
                } else {
                    showEmpty(getString(R.string.no_data));
                }
            } else {
                showEmpty(response.body().getErrorMessage());
            }

        }

    }

}
