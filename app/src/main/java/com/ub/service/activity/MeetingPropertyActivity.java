package com.ub.service.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.AddGroupActivity2;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.service.ConnectService;
import com.ub.techexcel.adapter.NewMeetingContactAdapter;
import com.ub.techexcel.bean.ServiceBean;

import org.feezu.liuli.timeselector.TimeSelector;
import org.feezu.liuli.timeselector.Utils.TextUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wang on 2017/6/19.
 */
public class MeetingPropertyActivity extends Activity implements View.OnClickListener {

    private ImageView cancel;
    private TextView submit;
    private EditText meetingname;
    private TextView meetingstartdate;
    private TextView meetingenddate;
    private TextView meetingduration;
    private RelativeLayout enddatell, startdatell;
    private TextView invitecontact;
    private RecyclerView mRecyclerView;
    private SharedPreferences sharedPreferences;
    private int schoolId;
    private List<Customer> customerList = new ArrayList<>();
    private ServiceBean bean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meetingproperty);
        bean = (ServiceBean) getIntent().getSerializableExtra("servicebean");
        initView();
    }


    private void initView() {
        cancel = (ImageView) findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        submit = (TextView) findViewById(R.id.submit);
        submit.setOnClickListener(this);
        meetingname = (EditText) findViewById(R.id.meetingname);
        meetingname.setText(bean.getName());
        meetingname.setSelection((bean.getName()).length());
        meetingduration = (TextView) findViewById(R.id.meetingduration);
        invitecontact = (TextView) findViewById(R.id.invitecontact);
        invitecontact.setOnClickListener(this);
        meetingstartdate = (TextView) findViewById(R.id.tv_p_start);
        meetingenddate = (TextView) findViewById(R.id.tv_p_end);
        startdatell = (RelativeLayout) findViewById(R.id.startdaterl);
        enddatell = (RelativeLayout) findViewById(R.id.enddaterl);
        startdatell.setOnClickListener(this);
        enddatell.setOnClickListener(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycleview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        schoolId = sharedPreferences.getInt("SchoolID", -1);

        String start = "0:00", end = "0:00";
        String year = "";
        final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        final SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
        if (!TextUtil.isEmpty(bean.getPlanedStartDate())) {
            startsecond = Long.parseLong(bean.getPlanedStartDate());
            Date curDate = new Date(startsecond);
            start = formatter.format(curDate);
            year = formatter2.format(curDate);
            meetingstartdate.setText(year + " " + start);
        }
        if (!TextUtil.isEmpty(bean.getPlanedEndDate())) {
            endsecond = Long.parseLong(bean.getPlanedEndDate());
            Date curDate = new Date(endsecond);
            end = formatter.format(curDate);
            year = formatter2.format(curDate);
            meetingenddate.setText(year + " " + end);
        }

//        customerList = (List<Customer>) bean.get
//        NewMeetingContactAdapter newMeetingContactAdapter = new NewMeetingContactAdapter(this, customerList);
//        mRecyclerView.setAdapter(newMeetingContactAdapter);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                finish();
                overridePendingTransition(R.anim.tran_in7, R.anim.tran_out7);
                break;
            case R.id.startdaterl:
                selectDate();
                break;
            case R.id.enddaterl:  //en
                selectTime();
                break;
            case R.id.invitecontact:
                Intent intent3 = new Intent(this,
                        AddGroupActivity2.class);
                startActivityForResult(intent3, 0x1101);
                break;
            case R.id.submit:
                submit();
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x1101 && resultCode == 3) {
            customerList = (List<Customer>) data.getSerializableExtra("customerList");
            NewMeetingContactAdapter newMeetingContactAdapter = new NewMeetingContactAdapter(this, customerList);
            mRecyclerView.setAdapter(newMeetingContactAdapter);
        }
    }

    long endsecond;

    //结束时间
    private void selectTime() {

        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date curDate = new Date(startsecond);
        String str = formatter.format(curDate);
        TimeSelector timeSelector = new TimeSelector(MeetingPropertyActivity.this, new TimeSelector.ResultHandler() {
            @Override
            public void handle(String time) {
                try {
                    endsecond = formatter.parse(time).getTime();
                    if (endsecond != 0) {
                        meetingenddate.setVisibility(View.GONE);
                        meetingenddate.setVisibility(View.VISIBLE);
                        if (endsecond > startsecond) {
                            meetingenddate.setText(time);
                            long duration = endsecond - startsecond;
                            long days = duration / (1000 * 60 * 60 * 24);                       //以天为单位取整
                            long hour = (duration % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);               //以小时为单位取整
                            long min = duration % 86400000 % 3600000 / 60000;       //以分钟为单位取整
                            long seconds = duration % 86400000 % 3600000 % 60000 / 1000;   //以秒为单位取整

                            Log.e("laoyu", "天" + days + "小时" + hour + "分" + min);
                            meetingduration.setText(days + "天" + hour + "时" + min + "分");
                        } else {
                            meetingenddate.setVisibility(View.VISIBLE);
                            meetingenddate.setVisibility(View.GONE);
                            Toast.makeText(MeetingPropertyActivity.this, "结束时间不能大于开始时间!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        meetingenddate.setVisibility(View.VISIBLE);
                        meetingenddate.setVisibility(View.GONE);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, str, "2030-12-31 00:00");
        timeSelector.show();

    }


    long startsecond;

    //开始时间
    private void selectDate() {

        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date curDate = new Date(startsecond);
        String str = formatter.format(curDate);
        TimeSelector timeSelector = new TimeSelector(MeetingPropertyActivity.this, new TimeSelector.ResultHandler() {
            @Override
            public void handle(String time) {
                try {
                    startsecond = formatter.parse(time).getTime();
                    if (startsecond != 0) {
                        meetingstartdate.setVisibility(View.GONE);
                        meetingstartdate.setVisibility(View.VISIBLE);
                        if (endsecond != 0) {
                            if (endsecond > startsecond) {
                                meetingstartdate.setText(time);
                            } else {
                                meetingstartdate.setVisibility(View.VISIBLE);
                                meetingstartdate.setVisibility(View.GONE);
                                Toast.makeText(MeetingPropertyActivity.this, "结束时间不能大于开始时间!", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            meetingstartdate.setText(time);
                        }
                    } else {
                        meetingstartdate.setVisibility(View.VISIBLE);
                        meetingstartdate.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, str, "2030-12-31 00:00");
        timeSelector.show();


    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x1001:
                    AppConfig.newlesson = true;
                    finish();
                    break;
            }
        }
    };

    private void submit() {
        if (startsecond == 0 || endsecond == 0) {
            Toast.makeText(this, "please select date", Toast.LENGTH_LONG).show();
            return;
        }
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("LessonID", bean.getId());
                    jsonObject.put("Title", meetingname.getText().toString());
                    jsonObject.put("Description", meetingduration.getText().toString());
                    jsonObject.put("StartDate", startsecond);
                    jsonObject.put("EndDate", endsecond);

                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < customerList.size(); i++) {
                        JSONObject j = new JSONObject();
                        j.put("LessonID", bean.getId());
                        j.put("MemberID", customerList.get(i).getUserID());
                        j.put("Role", 1);
                        jsonArray.put(j);
                    }
                    JSONObject j2 = new JSONObject();
                    j2.put("MemberID", AppConfig.UserID);
                    j2.put("Role", 2);
                    jsonArray.put(j2);
                    jsonObject.put("LessonMembers", jsonArray);

                    JSONArray jj = new JSONArray();
                    jj.put(jsonObject);

                    JSONObject returnJson = ConnectService.submitDataByJson4(AppConfig.URL_PUBLIC + "Lesson/CreateOrUpdateLessons", jj.toString());
                    Log.e("CreateOrUpdateLessons", jj.toString() + "    " + returnJson.toString());
                    int retCode = returnJson.getInt("RetCode");
                    switch (retCode) {
                        case 0:
                            Message msg = Message.obtain();
                            msg.what = 0x1001;
                            handler.sendMessage(msg);
                            break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start(ThreadManager.getManager());

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            overridePendingTransition(R.anim.tran_in7, R.anim.tran_out7);
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
