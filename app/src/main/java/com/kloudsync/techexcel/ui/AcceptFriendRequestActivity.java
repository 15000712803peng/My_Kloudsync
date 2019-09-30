package com.kloudsync.techexcel.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.BaseActivity;
import com.kloudsync.techexcel.bean.Company;
import com.kloudsync.techexcel.bean.Friend;
import com.kloudsync.techexcel.response.FriendResponse;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AcceptFriendRequestActivity extends BaseActivity implements View.OnClickListener {
    TextView titleText;
    RelativeLayout backLayout;
    TextView skipText;
    int from;
    RecyclerView friendList;
    FriendAdapter adapter;
    private static final int FROM_LOGIN = 1;
    private static final int FROM_PERSONAL_CENTER = 2;
    List<Company> companies;
    int currentIndex = -1;
    private TextView promptText;
    private TextView nextText;
    private TextView welcomeText;
    private TextView noFriendsText;

    @Override
    protected int setLayout() {
        return R.layout.activity_accept_friend_request;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        processCompanyFriend(0);
    }

    @Override
    protected void initView() {
        from = getIntent().getIntExtra("from", 0);
        companies = new Gson().fromJson(getIntent().getStringExtra("companies"), new TypeToken<List<Company>>() {
        }.getType());
        titleText = (TextView) findViewById(R.id.tv_title);
        titleText.setText("Welcome to join");
        backLayout = findViewById(R.id.layout_back);
        backLayout.setOnClickListener(this);
        skipText = findViewById(R.id.txt_right_title);
        skipText.setText("Skip");
        skipText.setOnClickListener(this);
        friendList = findViewById(R.id.list_friends);
        promptText = findViewById(R.id.txt_promp);
        nextText = findViewById(R.id.txt_next);
        welcomeText = findViewById(R.id.txt_welcome);
        noFriendsText = findViewById(R.id.txt_no_friends);
        friendList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new FriendAdapter();
        friendList.setAdapter(adapter);
        nextText.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
                    List<Friend> friends = adapter.getSelectFriends();
                    String[] ids = new String[friends.size()];
                    for (int i = 0; i < friends.size(); ++i) {
                        ids[i] = friends.get(i).getRongCloudID();
                    }
                    ServiceInterfaceTools.getinstance().acceptFriendsRequest(ids);
                }
                if (currentIndex == companies.size() - 1) {
                    goToMainActivity();
                } else {
                    processCompanyFriend(currentIndex + 1);
                }
                break;
        }
    }

    private void processCompanyFriend(final int index) {
        final Company company = companies.get(currentIndex);
        ServiceInterfaceTools.getinstance().friendRequest(company.getCompanyID()).enqueue(new Callback<FriendResponse>() {
            @Override
            public void onResponse(Call<FriendResponse> call, Response<FriendResponse> response) {
                if (response != null && response.isSuccessful()) {
                    currentIndex = index;
                    handle(response.body().getRetData(), company);
                }
            }

            @Override
            public void onFailure(Call<FriendResponse> call, Throwable t) {

            }
        });
    }

    private void handle(final List<Friend> friends, final Company company) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                welcomeText.setText("Welcome to join " + company.getCompanyName());
                if (friends == null || friends.size() == 0) {
                    noFriendsText.setVisibility(View.GONE);
                    promptText.setVisibility(View.GONE);
                    adapter.setFriends(new ArrayList<Friend>());
                } else {
                    noFriendsText.setVisibility(View.GONE);
                    promptText.setText("Hi," + company.getCompanyName() + "公司有以下的用户想成为你的个人好友，请确认他们的好友请求");
                    adapter.setFriends(friends);
                }
            }
        });

    }

    class FriendAdapter extends RecyclerView.Adapter {
        List<Friend> friends = new ArrayList<>();

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new FriendHolder(getLayoutInflater().inflate(R.layout.item_friend, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final Friend friend = friends.get(position);
            FriendHolder friendHolder = (FriendHolder) holder;
            friendHolder.nameText.setText(friend.getUserName());
            friendHolder.checkBox.setChecked(friend.isSelected());
            friendHolder.itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    friend.setSelected(!friend.isSelected());
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return friends.size();
        }

        class FriendHolder extends RecyclerView.ViewHolder {
            ImageView friendIcon;
            TextView nameText;
            CheckBox checkBox;
            RelativeLayout itemLayout;

            public FriendHolder(View itemView) {
                super(itemView);
                friendIcon = itemView.findViewById(R.id.image_friend_icon);
                nameText = itemView.findViewById(R.id.txt_name);
                checkBox = itemView.findViewById(R.id.checkbox);
                itemLayout = itemView.findViewById(R.id.layout_item);
            }
        }

        public List<Friend> getSelectFriends() {
            List<Friend> selFriends = new ArrayList<>();
            for (Friend friend : friends) {
                if (friend.isSelected()) {
                    selFriends.add(friend);
                }
            }
            return selFriends;
        }

        public void setFriends(List<Friend> friends) {
            this.friends.clear();
            this.friends.addAll(friends);
            notifyDataSetChanged();
        }
    }


    public void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}
