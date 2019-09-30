package com.kloudsync.techexcel.ui;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.InvitationsAdapter;
import com.kloudsync.techexcel.app.BaseActivity;
import com.kloudsync.techexcel.bean.Company;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.response.NetworkResponse;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvitationsActivity extends BaseActivity implements View.OnClickListener {

    TextView titleText;
    RecyclerView invitationList;
    RelativeLayout backLayout;
    InvitationsAdapter adapter;
    View divider;
    List<Company> companies;
    TextView skipText;
    TextView nextText;
    String companiesJson;
    int from;
    private static final int FROM_LOGIN = 1;
    private static final int FROM_PERSONAL_CENTER = 2;
    @Override
    protected int setLayout() {
        return R.layout.activity_invitations;
    }

    @Override
    protected void initView() {
        companiesJson = getIntent().getStringExtra("companies");
        from = getIntent().getIntExtra("from", 0);
        companies = new Gson().fromJson(companiesJson, new TypeToken<List<Company>>() {
        }.getType());
        invitationList = findViewById(R.id.list_invitation);
        titleText = findViewById(R.id.tv_title);
        titleText.setText("Invitations");
        divider = findViewById(R.id.title_divider);
        divider.setVisibility(View.GONE);
        backLayout = findViewById(R.id.layout_back);
        backLayout.setOnClickListener(this);
        invitationList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        adapter = new InvitationsAdapter();
        invitationList.setAdapter(adapter);
        adapter.setCompanies(companies);
        skipText = findViewById(R.id.txt_right_title);
        skipText.setOnClickListener(this);
        nextText = findViewById(R.id.txt_next);
        nextText.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_back:
                finish();
                break;
            case R.id.txt_right_title:
                if (from == FROM_LOGIN) {
                    goToMainActivity();
                } else if (from == FROM_PERSONAL_CENTER) {
                    finish();
                }
                break;
            case R.id.txt_next:
                if (adapter != null) {
                    String selectedCompanies = adapter.getSelectedCompanies();
                    if (TextUtils.isEmpty(selectedCompanies)) {
                        Toast.makeText(this, "请先选择要接受的邀请", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String[] ids = selectedCompanies.split(",");
                    if (ids != null && ids.length > 0) {
                        ServiceInterfaceTools.getinstance().acceptInvitations(ids).enqueue(new Callback<NetworkResponse>() {
                            @Override
                            public void onResponse(Call<NetworkResponse> call, Response<NetworkResponse> response) {
                                if (response != null && response.isSuccessful()) {
                                    if (response.body().getRetCode() == AppConfig.RETCODE_SUCCESS) {
                                        getToFriendRequest();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<NetworkResponse> call, Throwable t) {

                            }
                        });
                    }
                }
                break;
            default:
                break;
        }
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void getToFriendRequest() {
        Intent intent = new Intent(this, AcceptFriendRequestActivity.class);
        intent.putExtra("companies", companiesJson);
        intent.putExtra("from", 1);
        startActivity(intent);

    }


}
