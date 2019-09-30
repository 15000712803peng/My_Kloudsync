package com.ub.service.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.BaseActivity;
import com.kloudsync.techexcel.config.MeetingConfig;
import com.ub.techexcel.bean.AgoraUser;

import java.util.ArrayList;
import java.util.List;

import io.agora.service.KloudAgoraManager;
import io.agora.ui.AgoraUsersAdapter;

public class MeetingActivity extends BaseActivity implements KloudAgoraManager.AgoraEventListener {

    MeetingConfig meetingConfig;
    KloudAgoraManager agoraManager;
    List<AgoraUser> agoraUsers;
    RecyclerView agoraList;
    AgoraUsersAdapter agoraUsersAdapter;
    public static final int MSG_REFRESH_SCREEN = 1;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (MeetingActivity.this == null || isDestroyed() || isFinishing()) {
                return;
            }
            switch (msg.what) {
                case MSG_REFRESH_SCREEN:
                    showScreenLayout.removeAllViews();
                    if (!TextUtils.isEmpty(screenId)) {
                        showScreenLayout.addView(agoraManager.addScreenSurface(getApplicationContext(), screenId), 0, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        sendMessageDelayed(obtainMessage(MSG_REFRESH_SCREEN), 1000);
                    }
                    break;
            }
        }
    };

    private void meetingConfig() {
        meetingConfig = new MeetingConfig();
        meetingConfig.setHostId(getIntent().getStringExtra("host_id"));
        meetingConfig.setPresenterId(getIntent().getStringExtra("presenter_id"));
        meetingConfig.setMeetingId(getIntent().getStringExtra("meeting_id"));
        meetingConfig.setMeetingRole(MeetingConfig.MeetingRole.match(getIntent().getIntExtra("meeting_role", 0)));
    }

    String meetingId = "";

    private void initAgora() {
        agoraUsers = new ArrayList<>();
        agoraManager = KloudAgoraManager.getInstance();
        agoraManager.init(getApplicationContext());
        agoraManager.setMeetingConfig(meetingConfig);
        agoraManager.setAgoraEventListener(this);
        meetingId = meetingConfig.getMeetingId().split(",")[0];
        agoraManager.getWorkerThread().joinChannel("H4226");
        Log.e("MeetingActivity", "meeting_id:" + meetingId.toUpperCase());
    }

    @Override
    protected int setLayout() {
        return R.layout.activity_meeting;
    }

    @Override
    protected void initView() {
        agoraList = (RecyclerView) findViewById(R.id.list_agora);
        agoraList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        showScreenLayout = findViewById(R.id.layout_show_screen);
        meetingConfig();
        initAgora();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        agoraManager.getWorkerThread().leaveChannel("H4226");
        agoraManager.release();
        handler.removeMessages(MSG_REFRESH_SCREEN);
    }

    @Override
    public void onSelfJoined(final String id) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addAgoraUserVedio(agoraManager.addSelfVedio(getApplicationContext(), id));
            }
        });

    }

    @Override
    public void onMeetingMemeberJoined(final String id) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addAgoraUserVedio(agoraManager.addMemberVedio(getApplicationContext(), id));
            }
        });
    }

    String screenId;
    FrameLayout showScreenLayout;

    @Override
    public void showMemberScreen(final String id) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e("MeetingActivity", "showMemberScreen:" + id);
                screenId = id;

                showScreenLayout.setVisibility(View.VISIBLE);
                showScreenLayout.removeAllViews();
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.width = getResources().getDisplayMetrics().widthPixels;
                params.height = getResources().getDisplayMetrics().heightPixels;
                showScreenLayout.addView(agoraManager.addScreenSurface(getApplicationContext(), id), 0, params);
                if (agoraUsersAdapter != null) {
                    agoraUsersAdapter.notifyDataSetChanged();
                }
                handler.sendMessageDelayed(handler.obtainMessage(MSG_REFRESH_SCREEN), 1000);
            }
        });

    }

    private void addAgoraUserVedio(AgoraUser user) {
        if (agoraUsersAdapter == null) {
            agoraUsersAdapter = new AgoraUsersAdapter(MeetingActivity.this);
            agoraUsersAdapter.addUser(user);
            if (agoraList != null) {
                agoraList.setAdapter(agoraUsersAdapter);
            }
        } else {
            agoraUsersAdapter.addUser(user);
        }
    }

    private void refreshAgoraUsersVedioList(final List<AgoraUser> agoraUsers) {
        if (agoraUsersAdapter == null) {
            agoraUsersAdapter = new AgoraUsersAdapter(MeetingActivity.this);
            agoraUsersAdapter.setUsers(agoraUsers);
            if (agoraList != null) {
                agoraList.setAdapter(agoraUsersAdapter);
            }
        } else {
            agoraUsersAdapter.setUsers(agoraUsers);
        }
    }
}
