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
import com.kloudsync.techexcel.adapter.SimpleChatAdapter;
import com.kloudsync.techexcel.app.BaseActivity;
import com.kloudsync.techexcel.bean.Team;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.search.view.VChatSearch;
import com.ub.techexcel.adapter.TeamAdapterV2;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;

public class ChatSearchActivity extends BaseActivity implements VChatSearch, View.OnClickListener, TextWatcher, TeamAdapterV2.OnItemClickListener, SimpleChatAdapter.OnItemClickListener {

    private RecyclerView list;
    private TextView cancelText;
    String searchStr;
    EditText searchEdit;
    private ImageView clearEditImage;
    private RelativeLayout noDataLayout;
    private ProgressBar loadingBar;
    private TextView messageText;
    SharedPreferences userPreferences;
    private SimpleChatAdapter adapter;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

            }
        }
    };
    List<Conversation> conversations;

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
        conversations = (List<Conversation>) getIntent().getExtras().getSerializable("conversation_list");
        if (conversations == null) {
            conversations = new ArrayList<>();
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
    public void showChats(List<Conversation> conversations, String keyword) {
        loadingBar.setVisibility(View.INVISIBLE);
        noDataLayout.setVisibility(View.INVISIBLE);
        adapter = new SimpleChatAdapter();
        list.setVisibility(View.VISIBLE);
        adapter.setKeyword(keyword);
        adapter.setConversations(conversations);
        adapter.setItemClickListener(this);
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

    private void search(final String searchStr, final VChatSearch view) {
        showLoading();
        Observable.just(searchStr).observeOn(Schedulers.io()).map(new Function<String, List<Conversation>>() {
            @Override
            public List<Conversation> apply(String searchStr) throws Exception {
                List<Conversation> results = new ArrayList<>();
                for (Conversation conversation : ChatSearchActivity.this.conversations) {
                    if (conversation.getSenderUserName().contains(searchStr)) {
                        results.add(conversation);
                    }
                }
                return results;
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<List<Conversation>>() {
            @Override
            public void accept(List<Conversation> teamSearchResponseResponse) throws Exception {
                handleResponse(teamSearchResponseResponse, searchStr);
            }
        }).subscribe();

    }


    private void handleResponse(List<Conversation> conversations, String keyword) {

        if (conversations != null && conversations.size() > 0) {
            showChats(conversations, keyword);
        } else {
            showEmpty(getString(R.string.no_data));
        }
    }

    @Override
    public void onItemClick(Team teamData) {

    }

    @Override
    public void onItemClick(int position, Conversation conversation) {
        RongIM.getInstance().startConversation(this, Conversation.ConversationType.PRIVATE, conversation.getTargetId(), conversation.getSenderUserName());

    }
}
