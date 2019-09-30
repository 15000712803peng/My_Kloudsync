package com.kloudsync.techexcel.search.ui;

import android.content.Context;
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
import com.kloudsync.techexcel.adapter.SearchContactAdapter;
import com.kloudsync.techexcel.app.BaseActivity;
import com.kloudsync.techexcel.bean.Team;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.search.view.VContactSearch;
import com.ub.techexcel.adapter.TeamAdapterV2;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.rong.imkit.RongIM;

public class ContactSearchActivity extends BaseActivity implements VContactSearch, View.OnClickListener, TextWatcher, TeamAdapterV2.OnItemClickListener, SearchContactAdapter.OnItemClickListener {

    private RecyclerView list;
    private TextView cancelText;
    String searchStr;
    EditText searchEdit;
    private ImageView clearEditImage;
    private RelativeLayout noDataLayout;
    private ProgressBar loadingBar;
    private TextView messageText;
    SharedPreferences userPreferences;
    private SearchContactAdapter adapter;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

            }
        }
    };
    List<Customer> customers;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
    }

    @Override
    protected int setLayout() {
        return R.layout.activity_search;
    }

    @Override
    protected void initView() {
        customers = (List<Customer>) getIntent().getExtras().getSerializable("customer_list");
        if (customers == null) {
            customers = new ArrayList<>();
        }
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
    public void showContacts(List<Customer> conversations, String keyword) {
        loadingBar.setVisibility(View.INVISIBLE);
        noDataLayout.setVisibility(View.INVISIBLE);
        adapter = new SearchContactAdapter();
        list.setVisibility(View.VISIBLE);
        adapter.setKeyword(keyword);
        adapter.setCustomers(conversations);
        adapter.setOnItemClickListener(this);
        list.setAdapter(adapter);

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

    private void search(final String searchStr, final VContactSearch view) {
        showLoading();
        Observable.just(searchStr).observeOn(Schedulers.io()).map(new Function<String, List<Customer>>() {
            @Override
            public List<Customer> apply(String searchStr) throws Exception {
                List<Customer> results = new ArrayList<>();
                for (Customer customer : ContactSearchActivity.this.customers) {
                    if (customer.getName().contains(searchStr)) {
                        results.add(customer);
                    }
                }
                return results;
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<List<Customer>>() {
            @Override
            public void accept(List<Customer> results) throws Exception {
                handleResponse(results, searchStr);
            }
        }).subscribe();

    }


    private void handleResponse(List<Customer> customers, String keyword) {

        if (customers != null && customers.size() > 0) {
            showContacts(customers, keyword);
        } else {
            showEmpty(getString(R.string.no_data));
        }
    }

    @Override
    public void onItemClick(Team teamData) {

    }

    @Override
    public void onItemClick(int position, Customer customer) {
        if (customer.isEnableChat()) {
            AppConfig.Name = customer.getName();
            AppConfig.isUpdateDialogue = true;
            RongIM.getInstance().startPrivateChat(this,
                    customer.getUBAOUserID(), customer.getName());
        }
    }
}
