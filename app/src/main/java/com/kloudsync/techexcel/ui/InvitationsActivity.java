package com.kloudsync.techexcel.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
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
import com.kloudsync.techexcel.bean.EventAutoLogin;
import com.kloudsync.techexcel.bean.EventCompanyClicked;
import com.kloudsync.techexcel.bean.LoginData;
import com.kloudsync.techexcel.bean.LoginResult;
import com.kloudsync.techexcel.bean.RongCloudData;
import com.kloudsync.techexcel.bean.SimpleCompanyData;
import com.kloudsync.techexcel.bean.UserPreferenceData;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.response.NetworkResponse;
import com.kloudsync.techexcel.start.LoginActivity;
import com.kloudsync.techexcel.start.LoginGet;
import com.ub.techexcel.service.ConnectService;
import com.ub.techexcel.tools.ServiceInterfaceTools;
import com.ub.techexcel.tools.Tools;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvitationsActivity extends BaseActivity implements View.OnClickListener {


    private RecyclerView invitationList;
    private InvitationsAdapter adapter;
    private List<SimpleCompanyData> companies;
    private TextView nextText;
    private TextView skipText;
    private String companiesJson;
    private int from;
    private static final int FROM_LOGIN = 1;
    private static final int FROM_PERSONAL_CENTER = 2;
    private SharedPreferences sharedPreferences;

    @Override
    protected int setLayout() {
        return R.layout.activity_invitations;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        companiesJson = getIntent().getStringExtra("companies");
        from = getIntent().getIntExtra("from", 0);
        companies = new Gson().fromJson(companiesJson, new TypeToken<List<SimpleCompanyData>>() {
        }.getType());
        invitationList = findViewById(R.id.list_invitation);
        skipText = findViewById(R.id.txt_skip);
        skipText.setOnClickListener(this);
        invitationList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        adapter = new InvitationsAdapter();
        invitationList.setAdapter(adapter);
        adapter.setCompanies(companies);
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
                    SimpleCompanyData simpleCompanyData = adapter.getSelectedCompanies();
                    if (simpleCompanyData == null) {
//                        Toast.makeText(this, "请先选择要接受的邀请", Toast.LENGTH_SHORT).show();
                        goToWelcomeCreateCompany();
                        return;
                    }

                    addOrUpdateUserPreference(simpleCompanyData);
                }
                break;
            case R.id.txt_skip:
                goToWelcomeCreateCompany();
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

    private void goToWelcomeCreateCompany() {
        Intent intent = new Intent(this, WelcomeAndCreateActivity.class);
        intent.putExtra("companies", new Gson().toJson(companies));
        intent.putExtra("from", 1);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
//        finish();
    }

    private void initBySelected() {
        if (adapter != null && adapter.getSelectedCompanies() != null) {
            skipText.setVisibility(View.VISIBLE);
            nextText.setText("加入");
        } else {
            skipText.setVisibility(View.INVISIBLE);
            nextText.setText("下一步");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCompanyClicked(EventCompanyClicked companyClicked) {
        initBySelected();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void autoLogin(EventAutoLogin login) {
        String name = sharedPreferences.getString("name", null);
        String pwd = LoginGet.DecodeBase64Password(sharedPreferences.getString("password", null));
        String telephone = sharedPreferences.getString("telephone", null);
        Log.e("autoLogin","name:" + name + ",pwd:" + pwd + ",telephone:" + telephone);
        processLogin(name,pwd,telephone);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void addOrUpdateUserPreference(final SimpleCompanyData companyData) {

        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    JSONObject textObject = new JSONObject();
                    try {
                        jsonObject.put("FieldID", 10001);
                        textObject.put("SchoolID", companyData.getSchoolID());
                        textObject.put("SchoolName", companyData.getSchoolName());
                        textObject.put("SubSystemData", "");
                        jsonObject.put("PreferenceText", textObject + "");
                        jsonObject.put("PreferenceMemo", "");
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    JSONObject response = ConnectService.submitDataByJson(
                            AppConfig.URL_PUBLIC
                                    + "User/AddOrUpdateUserPreference", jsonObject);
                    Log.e("AddOrUpdateUserPreference", jsonObject.toString() + ",response:" + response);
                    String retcode = response.getString("RetCode");
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        EventBus.getDefault().post(new EventAutoLogin());
                    } else {

                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }


    Disposable loginDisposable;

    private void processLogin(final String name, final String password, final String phoneNumber) {

        loginDisposable = Observable.just("login").observeOn(Schedulers.io()).map(new Function<String, String>() {
            @Override
            public String apply(String str) throws Exception {
                try {
                    Response<NetworkResponse<LoginData>> response = ServiceInterfaceTools.getinstance().login(name, password).execute();
                    if (response == null || !response.isSuccessful() || response.body() == null) {

                    } else {
                        if (response.body().getRetCode() == AppConfig.RETCODE_SUCCESS) {


                        } else {
                        }
                    }
                } catch (UnknownHostException e) {

                } catch (SocketTimeoutException exception) {

                }
                return str;
            }
        }).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                JSONObject response = ServiceInterfaceTools.getinstance().syncGetUserPreference();
                if (response.has("RetCode")) {
                    if (response.getInt("RetCode") == 0) {
                        UserPreferenceData userPreferenceData = new Gson().fromJson(response.toString(), UserPreferenceData.class);
                        if (userPreferenceData.getRetData() == null) {

                        } else {
                            Observable.just("login").observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                                @Override
                                public void accept(String s) throws Exception {
                                    goToMainActivity();
                                }
                            });
                        }

                    } else {

                    }
                }
            }
        }).subscribe();

    }


}
