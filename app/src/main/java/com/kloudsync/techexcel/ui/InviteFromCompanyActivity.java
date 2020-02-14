package com.kloudsync.techexcel.ui;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.InviteFromCompanyAdapter;
import com.kloudsync.techexcel.app.BaseActivity;
import com.kloudsync.techexcel.bean.CompanyContact;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.AudiencePromptDialog;
import com.kloudsync.techexcel.help.InviteNewContactDialog;
import com.kloudsync.techexcel.response.CompanyContactsResponse;
import com.kloudsync.techexcel.response.InviteResponse;
import com.kloudsync.techexcel.response.NResponse;
import com.kloudsync.techexcel.response.NetworkResponse;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.greenrobot.eventbus.EventBus;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InviteFromCompanyActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

    private TextView titleText;
    private RelativeLayout backLayout;
    private RecyclerView contactList;
    TextView invatenewcontact;
    InviteFromCompanyAdapter adapter;
    int teamId;
    int spaceId;
    private EditText searchEdit;
    Handler handler = new Handler();
    RelativeLayout titleRightLayout;

    @Override
    protected int setLayout() {
        return R.layout.activity_invite_from_company;
    }

    @Override
    protected void initView() {
        teamId = getIntent().getIntExtra("team_id", 0);
        spaceId = getIntent().getIntExtra("space_id", 0);
        titleText = findViewById(R.id.tv_title);
        titleText.setText(getString(R.string.invite_form_company));
        contactList = findViewById(R.id.list_contact);
        invatenewcontact = findViewById(R.id.invatenewcontact);
        invatenewcontact.setOnClickListener(this);
        noDataLayout = findViewById(R.id.no_data_lay);
        messageText = findViewById(R.id.txt_msg);
        titleRightLayout = findViewById(R.id.layout_title_right);
        titleRightLayout.setVisibility(View.VISIBLE);
        titleRightLayout.setOnClickListener(this);
        contactList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new InviteFromCompanyAdapter(this);
        View headerView = getLayoutInflater().inflate(R.layout.company_contact_search_header, contactList, false);
        loadingBar = headerView.findViewById(R.id.loading_progress);
        searchEdit = headerView.findViewById(R.id.edit_input);
        searchEdit.setHint(getString(R.string.inputphoneorusername));
        searchEdit.addTextChangedListener(this);
        adapter.setHeaderView(headerView);
        backLayout = findViewById(R.id.layout_back);
        backLayout.setOnClickListener(this);
        contactList.setAdapter(adapter);

        getCompanyContacts("");
    }

    private InviteNewContactDialog inviteNewContactDialog;
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_back:
                finish();
                break;
            case R.id.layout_title_right:
                if (adapter != null) {
                    List<CompanyContact> contacts = adapter.getSelectedContacts();
                    if (contacts.size() == 0) {
                        Toast.makeText(getApplicationContext(), "please select contact first", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    requestAddAdmin(contacts);
                }
                break;
            case R.id.invatenewcontact:
                if (inviteNewContactDialog != null) {
                    if (inviteNewContactDialog.isShowing()) {
                        inviteNewContactDialog.cancel();
                        inviteNewContactDialog = null;
                    }
                }
                inviteNewContactDialog = new InviteNewContactDialog(this);
                inviteNewContactDialog.setOptionsLinstener(new InviteNewContactDialog.InviteOptionsLinstener() {
                    @Override
                    public void inviteFromPhone(String phone) {
//                        Toast.makeText(InviteFromCompanyActivity.this,phone,Toast.LENGTH_LONG).show();
                        inviteNewContact(phone);
                    }
                });
                inviteNewContactDialog.show();
                break;
        }
    }




    private void inviteNewContact(String mobile){

        ServiceInterfaceTools.getinstance().inviteNewToCompany(mobile, 3, teamId,  0).enqueue(new Callback<InviteResponse>() {
            @Override
            public void onResponse(Call<InviteResponse> call, Response<InviteResponse> response) {
                if (response != null && response.isSuccessful()) {
                    Log.e("duang123", response.body().toString() + "   :");
                    if (response.body().getRetCode() == AppConfig.RETCODE_SUCCESS) {
                        Toast.makeText(getApplicationContext(), "邀请成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        String msg = getString(R.string.operate_failure);
                        if (!TextUtils.isEmpty(response.body().getErrorMessage())) {
                            msg = response.body().getErrorMessage();
                        }
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<InviteResponse> call, Throwable t) {
            }
        });
    }




    private void requestAddAdmin(List<CompanyContact> contacts) {
        ServiceInterfaceTools.getinstance().inviteCompanyMemberAsTeamAdmin(teamId + "", contacts).enqueue(new Callback<NetworkResponse>() {
            @Override
            public void onResponse(Call<NetworkResponse> call, Response<NetworkResponse> response) {
                if (response != null && response.isSuccessful()) {
                    if (response.body().getRetCode() == 0) {
                        Toast.makeText(getApplicationContext(), "add team admin success", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "add failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<NetworkResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "add failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getCompanyContacts(String keyword) {

//        ServiceInterfaceTools.getinstance().searchCompanyContactInTeam(teamId + "", keyword).enqueue(new Callback<CompanyContactsResponse>() {
//            @Override
//            public void onResponse(Call<CompanyContactsResponse> call, Response<CompanyContactsResponse> response) {
//                if (response != null && response.isSuccessful()) {
//                    Log.e("success", "response:" + response.body());
//                    List<CompanyContact> contacts = response.body().getRetData();
//                    if (contacts == null) {
//                        contacts = new ArrayList<>();
//                    }
//                    adapter.setDatas(contacts);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<CompanyContactsResponse> call, Throwable t) {
//                Log.e("fail", "response:" + call);
//            }
//        });

        String url = AppConfig.URL_PUBLIC + "TeamSpace/SearchContact?companyID=" + AppConfig.SchoolID + "&spaceID=" + teamId + "&keyword=&pageIndex=0&pageSize=10";
        ServiceInterfaceTools.getinstance().getSearchContact(url, ServiceInterfaceTools.GETSEARCHCONTACT, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                List<CompanyContact> contacts = new ArrayList<>();
                contacts.addAll((List<CompanyContact>) object);
                if (contacts.size() == 0) {
                    contacts = new ArrayList<>();
                }
                adapter.setDatas(contacts);
            }
        });




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

    private void showLoading() {
        loadingBar.setVisibility(View.VISIBLE);
    }

    private void search(final String searchStr) {
        showLoading();
        Observable.just(searchStr).observeOn(Schedulers.io()).map(new Function<String, NResponse<CompanyContactsResponse>>() {
            @Override
            public NResponse<CompanyContactsResponse> apply(String searchStr) throws Exception {
                NResponse<CompanyContactsResponse> response = new NResponse<>();
                try {
                    response.setResponse(ServiceInterfaceTools.getinstance().searchCompanyContactInTeam(teamId + "", searchStr).execute());
                } catch (UnknownHostException e) {
                    return response.setNull(true);
                } catch (SocketTimeoutException exception) {
                    return response.setNull(true);
                }
                return response;
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<NResponse<CompanyContactsResponse>>() {
            @Override
            public void accept(NResponse<CompanyContactsResponse> teamSearchResponseResponse) throws Exception {
                handleResponse(teamSearchResponseResponse, searchStr);
            }
        }).subscribe();

    }

    String searchStr = "";

    private void editCompleted() {
        searchStr = searchEdit.getText().toString().trim();
        if (TextUtils.isEmpty(searchStr)) {
            search("");
            return;
        }
        search(searchStr);
    }


    private RelativeLayout noDataLayout;
    private ProgressBar loadingBar;
    private TextView messageText;

    public void showEmpty(String message) {
        noDataLayout.setVisibility(View.VISIBLE);
        loadingBar.setVisibility(View.INVISIBLE);
        adapter.setDatas(new ArrayList<CompanyContact>());
        messageText.setText(message);
    }

    private void handleResponse(NResponse<CompanyContactsResponse> res, String keyword) {
        if (res == null || res.isNull()) {
            showEmpty(getString(R.string.rc_network_error));
            return;
        }
        Response<CompanyContactsResponse> response = res.getResponse();
        if (response.isSuccessful()) {
            int errorCode = response.body().getRetCode();
            if (errorCode == 0) {
                List<CompanyContact> teams = response.body().getRetData();
                if (teams != null && teams.size() > 0) {
                    showContacts(teams, keyword);
                } else {
                    showEmpty(getString(R.string.no_data));
                }
            } else {
                showEmpty(response.body().getErrorMessage());
            }

        }

    }

    public void showContacts(List<CompanyContact> contacts, String keyword) {
        loadingBar.setVisibility(View.INVISIBLE);
        noDataLayout.setVisibility(View.INVISIBLE);
        contactList.setVisibility(View.VISIBLE);
        adapter.setKeyword(keyword);
        adapter.setDatas(contacts);


    }
}
