package com.kloudsync.techexcel.ui;

import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.kloudsync.techexcel.response.CompanyContactsResponse;
import com.kloudsync.techexcel.response.NResponse;
import com.kloudsync.techexcel.response.NetworkResponse;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;

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

public class InviteFromSpaceActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

    private TextView titleText;
    private RelativeLayout backLayout;
    private RecyclerView contactList;
    InviteFromCompanyAdapter adapter;
    int teamId;
    int spaceId;
    private EditText searchEdit;
    Handler handler = new Handler();
    RelativeLayout titleRightLayout;
    private boolean isAddAdmin = true;

    @Override
    protected int setLayout() {
        return R.layout.activity_invite_from_company;
    }

    @Override
    protected void initView() {
        teamId = getIntent().getIntExtra("team_id", 0);
        spaceId = getIntent().getIntExtra("space_id", 0);
        isAddAdmin = getIntent().getBooleanExtra("isAddAdmin", true);
        titleText = findViewById(R.id.tv_title);
        titleText.setText(getString(R.string.invite_form_company));
        contactList = findViewById(R.id.list_contact);
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
        searchEdit.addTextChangedListener(this);
        adapter.setHeaderView(headerView);
        backLayout = findViewById(R.id.layout_back);
        backLayout.setOnClickListener(this);
        contactList.setAdapter(adapter);

        getCompanyContacts("");
    }

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
                    if (isAddAdmin) {
                        requestAddAdmin(contacts);
                    } else {
                        inviteCompanyMemberToSpace(contacts);
                    }
                }

                break;
        }
    }

    private void requestAddAdmin(List<CompanyContact> contacts) {
        String hh = "";
        for (int i = 0; i < contacts.size(); i++) {
            if (i == 0) {
                hh = contacts.get(i).getUserID();
            } else {
                hh = hh + "," + contacts.get(i).getUserID();
            }
        }
        String url = AppConfig.URL_PUBLIC + "TeamSpace/AddAdminMember?CompanyID=" + AppConfig.SchoolID + "&TeamSpaceID=" + teamId + "&MemberList="+hh;
        ServiceInterfaceTools.getinstance().addAdminMember(url, ServiceInterfaceTools.ADDADMINMEMBER, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void inviteCompanyMemberToSpace(List<CompanyContact> contacts) {
        String url = AppConfig.URL_PUBLIC + "Invite/InviteCompanyMemberToSpace";
        ServiceInterfaceTools.getinstance().inviteCompanyMemberToSpace(url, ServiceInterfaceTools.INVITECOMPANYMEMBERTOSPACE, teamId, contacts, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void getCompanyContacts(String keyword) {

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

    }

    private Runnable editRunnable = new Runnable() {
        @Override
        public void run() {
            editCompleted();
        }
    };

    private void showLoading() {
    }

    private void search(final String searchStr) {

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
