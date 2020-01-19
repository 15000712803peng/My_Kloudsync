package com.kloudsync.techexcel.ui;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.BaseActivity;
import com.kloudsync.techexcel.bean.EventJoinMeeting;
import com.kloudsync.techexcel.personal.CreateOrganizationActivityV2;
import com.ub.techexcel.tools.JoinMeetingPopup;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by tonyan on 2020/1/19.
 */

public class WelcomeAndCreateActivity extends BaseActivity implements View.OnClickListener{
    TextView createText;
    TextView joinMeetingText;
    @Override
    protected int setLayout() {
        return R.layout.activity_welcome_to_create;
    }

    @Override
    protected void initView() {
        createText = findViewById(R.id.txt_create);
        createText.setOnClickListener(this);
        joinMeetingText = findViewById(R.id.txt_join_meeting);
        joinMeetingText.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txt_create:
                goToCreate();
                break;
            case R.id.txt_join_meeting:
                break;
                default:
                    break;
        }
    }

    private void goToCreate(){
        Intent intent = new Intent(this, CreateOrganizationActivityV2.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    JoinMeetingPopup joinMeetingPopup;
    private void showJoinDialog(){
        if (joinMeetingPopup == null) {
            joinMeetingPopup = new JoinMeetingPopup();
            joinMeetingPopup.getPopwindow(this);
            joinMeetingPopup.setFavoritePoPListener(new JoinMeetingPopup.FavoritePoPListener() {
                @Override
                public void dismiss() {

                }

                @Override
                public void open() {

                }
            });
        }
        joinMeetingPopup.StartPop(createText);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void joinMeeting(EventJoinMeeting eventJoinMeeting) {
        if (eventJoinMeeting.getLessionId() <= 0) {
            Toast.makeText(this, "加入的meeting不存在或没有开始", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, DocAndMeetingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //-----
        intent.putExtra("meeting_id", eventJoinMeeting.getMeetingId());
        intent.putExtra("meeting_type", 0);
        intent.putExtra("lession_id", eventJoinMeeting.getLessionId());
        intent.putExtra("meeting_role", eventJoinMeeting.getRole());
        intent.putExtra("from_meeting", true);
        startActivity(intent);
    }
}
