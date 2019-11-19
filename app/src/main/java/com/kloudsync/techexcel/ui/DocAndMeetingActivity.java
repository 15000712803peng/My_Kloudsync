package com.kloudsync.techexcel.ui;


import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.kloudsync.techexcel.bean.EventSocketMessage;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.MeetingType;
import com.kloudsync.techexcel.help.UserData;
import com.kloudsync.techexcel.tool.SocketMessageManager;

import org.greenrobot.eventbus.Subscribe;

/**
 * Created by tonyan on 2019/11/19.
 */

public class DocAndMeetingActivity extends BaseDocAndMeetingActivity{

    private MeetingConfig meetingConfig;

    private SocketMessageManager messageManager;

    @Override
    public void showErrorPage() {

    }

    @Override
    public void initData() {
            meetingConfig = getConfig();
            messageManager = SocketMessageManager.getManager(this);
            messageManager.sendMessage_JoinMeeting(meetingConfig);
    }

    private MeetingConfig getConfig(){
        Intent data = getIntent();
        if(meetingConfig == null){
            meetingConfig = new MeetingConfig();
        }
        meetingConfig.setType(data.getIntExtra("meeting_type", MeetingType.DOC));
        meetingConfig.setMeetingId(data.getStringExtra("meeting_id"));
        meetingConfig.setLessionId(data.getIntExtra("lession_id",0));
        meetingConfig.setUserToken(UserData.getUserToken(this));
        return meetingConfig;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(messageManager != null){
            messageManager.release();
        }
    }

    @Subscribe
    public void handleSocketMessage(EventSocketMessage socketMessage){
        Log.e("DocAndMeetingActivity","socket_message:" + socketMessage);
        String action = socketMessage.getAction();
        if(TextUtils.isEmpty(action)){
            return;
        }
        switch (action){
            case SocketMessageManager.MESSAGE_LEAVE_MEETING:

                break;

            case SocketMessageManager.MESSAGE_JOIN_MEETING:

                break;

        }
    }
}
