package com.ub.service.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ub.techexcel.bean.NotifyBean;
import com.ub.techexcel.bean.SendMessageBean;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wang on 2017/11/17.
 */

public class AlertDialogActivity extends Activity implements View.OnClickListener {

    private TextView join, cancel;
    private JSONObject jsonObject;
    private int isNewCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.alertdialog);

        isNewCourse = getIntent().getIntExtra("isNewCourse", 0);
        join = (TextView) findViewById(R.id.join);
        join.setOnClickListener(this);
        cancel = (TextView) findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        try {
            jsonObject = new JSONObject(getIntent().getStringExtra("jsonObject"));
            Log.e("--------kkk",jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.join:
                final SendMessageBean sendMessageBean = new SendMessageBean();
                try {
                    sendMessageBean.setRoleType(jsonObject.getString("roleType"));
                    sendMessageBean.setMeetingId(jsonObject.getString("meetingID"));
                    sendMessageBean.setActiontype(jsonObject.getInt("actionType"));
                    sendMessageBean.setIncidentID(jsonObject.getString("incidentID"));
                    sendMessageBean.setTargetID(jsonObject.getString("targetID"));
                    sendMessageBean.setSourceID(jsonObject.getString("sourceID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent;
                if (isNewCourse == 0) {
                    intent = new Intent(AlertDialogActivity.this, WatchCourseActivity2.class);
                    intent.putExtra("isInstantMeeting", 0);
                } else {
                    intent = new Intent(AlertDialogActivity.this, WatchCourseActivity3.class);
                    intent.putExtra("isInstantMeeting", 1);
                    try {
                        intent.putExtra("lessionId", jsonObject.getString("itemId") + "");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                intent.putExtra("url", sendMessageBean.getAttachmentUrl());
                intent.putExtra("userid", sendMessageBean.getTargetID());
                intent.putExtra("meetingId", sendMessageBean.getMeetingId());
                intent.putExtra("teacherid", sendMessageBean.getSourceID());
                intent.putExtra("identity", Integer.parseInt(sendMessageBean.getRoleType()));
                intent.putExtra("incidentID", sendMessageBean.getIncidentID());
                startActivity(intent);
                finish();
                break;
            case R.id.cancel:
                try {
                    notifyleave(jsonObject.getString("meetingID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                finish();
                break;
            default:
                break;
        }
    }

    private void notifyleave(String meetingId) {
        boolean isExist = false;
        for (int i = 0; i < AppConfig.progressCourse.size(); i++) {
            if (AppConfig.progressCourse.get(i).getMeetingId().equals(meetingId)) {
                isExist = true;
                break;
            }
        }
        if (!isExist) {
            NotifyBean notifyBean = new NotifyBean();
            notifyBean.setMeetingId(meetingId);
            notifyBean.setStatus(false);
            AppConfig.progressCourse.add(notifyBean);
        }
        Intent intent = new Intent();
        intent.setAction(getResources().getString(R.string.Receive_Course));
        sendBroadcast(intent);
    }
}
