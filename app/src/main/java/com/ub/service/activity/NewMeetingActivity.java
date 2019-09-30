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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.AddGroupActivity2;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.service.ConnectService;
import com.ub.techexcel.adapter.NewMeetingContactAdapter;

import org.feezu.liuli.timeselector.TimeSelector;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wang on 2017/6/19.
 */
public class NewMeetingActivity extends Activity implements View.OnClickListener {

    private ImageView cancel;
    private TextView submit;
    private EditText meetingname;
    private TextView meetingstartdate,tv_p_start;
    private TextView meetingenddate,tv_p_end;
    private TextView meetingduration,tv_p_schedule,tv_p_schedule_size;

    private RelativeLayout as_rl_contact,startdaterl,enddaterl;
    private TextView invitecontact;
    //private RecyclerView mRecyclerView;
    private NewMeetingContactAdapter newMeetingContactAdapter;
    private SharedPreferences sharedPreferences;
    private int schoolId;
    private List<Customer> customerList = new ArrayList<>();
    private SimpleDraweeView as_img_contact_one, as_img_contact_two, as_img_contact_three;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newmeeting2);
        // EventBus.getDefault().register(this);
        initView();

    }

/*    @Subscribe(threadMode = ThreadMode.MAIN)
    public void post(List<Customer> ll) {
        if (ll != null && ll.size() > 0) {
            customerList.clear();
            customerList.addAll(ll);
            //newMeetingContactAdapter = new NewMeetingContactAdapter(this, customerList);
            //mRecyclerView.setAdapter(newMeetingContactAdapter);

        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0x1101 && resultCode == 3){

            customerList = (List<Customer>) data.getSerializableExtra("customerList");
            /*newMeetingContactAdapter = new NewMeetingContactAdapter(this, customerList);
            mRecyclerView.setAdapter(newMeetingContactAdapter);*/
            if (customerList.size() >= 3) {
                tv_p_schedule.setVisibility(View.GONE);
                as_img_contact_one .setVisibility(View.VISIBLE);
                as_img_contact_two .setVisibility(View.VISIBLE);
                as_img_contact_three .setVisibility(View.VISIBLE);
                tv_p_schedule_size.setVisibility(View.VISIBLE);
                tv_p_schedule_size.setText(customerList.size()+"total");
                as_img_contact_one.setImageURI(Uri.parse(customerList.get(0).getUrl()));
                as_img_contact_two.setImageURI(Uri.parse(customerList.get(1).getUrl()));
                as_img_contact_three.setImageURI(Uri.parse(customerList.get(2).getUrl()));
            } else if (customerList.size() >= 2) {
                tv_p_schedule.setVisibility(View.GONE);
                as_img_contact_one .setVisibility(View.VISIBLE);
                as_img_contact_two .setVisibility(View.VISIBLE);
                tv_p_schedule_size.setVisibility(View.VISIBLE);
                tv_p_schedule_size.setText(customerList.size()+"total");
                as_img_contact_one.setImageURI(Uri.parse(customerList.get(0).getUrl()));
                as_img_contact_two.setImageURI(Uri.parse(customerList.get(1).getUrl()));
                as_img_contact_three.setVisibility(View.GONE);
            } else if (customerList.size() >= 1) {
                tv_p_schedule.setVisibility(View.GONE);
                as_img_contact_one .setVisibility(View.VISIBLE);
                tv_p_schedule_size.setVisibility(View.VISIBLE);
                tv_p_schedule_size.setText(customerList.size()+"total");
                as_img_contact_one.setImageURI(Uri.parse(customerList.get(0).getUrl()));
                as_img_contact_two.setVisibility(View.GONE);
                as_img_contact_three.setVisibility(View.GONE);
            } else {
                tv_p_schedule.setVisibility(View.VISIBLE);
                as_img_contact_one.setVisibility(View.GONE);
                as_img_contact_two.setVisibility(View.GONE);
                as_img_contact_three.setVisibility(View.GONE);
                tv_p_schedule_size.setVisibility(View.GONE);
            }

        }
    }
    @Override
    protected void onDestroy() {
 /*       if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }*/
        super.onDestroy();
    }

    private void initView() {
        tv_p_schedule = (TextView) findViewById(R.id.tv_p_schedule);
        cancel = (ImageView) findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        submit = (TextView) findViewById(R.id.submit);
        submit.setOnClickListener(this);
        meetingname = (EditText) findViewById(R.id.meetingname);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd HH:mm:ss");
        String time = simpleDateFormat.format(new Date());
        //meetingname.setText(AppConfig.UserName + time);
        //meetingname.setSelection((AppConfig.UserName + time).length());
        meetingduration = (TextView) findViewById(R.id.meetingduration);
        tv_p_schedule_size = (TextView) findViewById(R.id.tv_p_schedule_size);

        as_rl_contact = (RelativeLayout) findViewById(R.id.as_rl_contact);
        as_rl_contact.setOnClickListener(this);
        meetingstartdate = (TextView) findViewById(R.id.meetingstartdate);
        tv_p_start = (TextView) findViewById(R.id.tv_p_start);
        startdaterl = (RelativeLayout) findViewById(R.id.startdaterl);

        enddaterl = (RelativeLayout) findViewById(R.id.enddaterl);
        meetingenddate = (TextView) findViewById(R.id.meetingenddate);
        tv_p_end = (TextView) findViewById(R.id.tv_p_end);

        startdaterl.setOnClickListener(this);
        enddaterl.setOnClickListener(this);
        //mRecyclerView = (RecyclerView) findViewById(R.id.recycleview);

        as_img_contact_one = findViewById(R.id.as_img_contact_one);
        as_img_contact_two = findViewById(R.id.as_img_contact_two);
        as_img_contact_three = findViewById(R.id.as_img_contact_three);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        //mRecyclerView.setLayoutManager(gridLayoutManager);
        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        schoolId = sharedPreferences.getInt("SchoolID", -1);


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                finish();
                overridePendingTransition(R.anim.tran_in7, R.anim.tran_out7);
                break;
            //开始时间
            case R.id.startdaterl:
                selectDate();
                break;
            //结束时间
            case R.id.enddaterl:
                selectTime();
                break;
            case R.id.as_rl_contact:
                Intent intent3 = new Intent(this,
                        AddGroupActivity2.class);
                startActivityForResult(intent3,0x1101);
                break;
            case R.id.submit:
                submit();
                break;
        }
    }

    long endsecond;

    //结束时间
    private void selectTime() {
//        Calendar calendar = Calendar.getInstance();
//        new TimePickerDialog(NewMeetingActivity.this, new TimePickerDialog.OnTimeSetListener() {
//            @Override
//            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                meetingstarttime.setText(hourOfDay + ":" + minute);
//            }
//        }, calendar.get(Calendar.HOUR), Calendar.MINUTE, true).show();


        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        TimeSelector timeSelector = new TimeSelector(NewMeetingActivity.this, new TimeSelector.ResultHandler() {
            @Override
            public void handle(String time) {
                try {
                    endsecond = formatter.parse(time).getTime();
                    if(endsecond !=0){
                        tv_p_end.setVisibility(View.GONE);
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
                            tv_p_end.setVisibility(View.VISIBLE);
                            meetingenddate.setVisibility(View.GONE);
                            Toast.makeText(NewMeetingActivity.this, "结束时间不能大于开始时间!", Toast.LENGTH_LONG).show();
                        }
                    }else {
                        tv_p_end.setVisibility(View.VISIBLE);
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
//        Calendar calendar = Calendar.getInstance();
//        new DatePickerDialog(NewMeetingActivity.this,
//                new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker view, int year,
//                                          int monthOfYear, int dayOfMonth) {
//                        meetingstartdate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
//                    }
//                }
//                , calendar.get(Calendar.YEAR)
//                , calendar.get(Calendar.MONTH)
//                , calendar.get(Calendar.DAY_OF_MONTH)).show();

        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        TimeSelector timeSelector = new TimeSelector(NewMeetingActivity.this, new TimeSelector.ResultHandler() {
            @Override
            public void handle(String time) {
                try {
                    startsecond = formatter.parse(time).getTime();
                    if(startsecond != 0){
                        tv_p_start.setVisibility(View.GONE);
                        meetingstartdate.setVisibility(View.VISIBLE);
                        if (endsecond != 0) {
                            if (endsecond > startsecond) {
                                meetingstartdate.setText(time);
                           /* long duration = endsecond - startsecond;
                            long hour= duration  / 3600000;               //以小时为单位取整
                            long min = duration % 86400000 % 3600000 / 60000;       //以分钟为单位取整
                            long seconds = duration % 86400000 % 3600000 % 60000 / 1000;   //以秒为单位取整
                            meetingduration.setText(hour+"小时");*/
                            } else {
                                tv_p_start.setVisibility(View.VISIBLE);
                                meetingstartdate.setVisibility(View.GONE);
                                Toast.makeText(NewMeetingActivity.this, "结束时间不能大于开始时间!", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            meetingstartdate.setText(time);
                        }
                    }else {
                        tv_p_start.setVisibility(View.VISIBLE);
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
        if (meetingname.length() == 0) {
            Toast.makeText(this, getResources().getString(R.string.schnames), Toast.LENGTH_LONG).show();
            return;
        }
        if (startsecond == 0 || endsecond == 0) {
            Toast.makeText(this, getResources().getString(R.string.schdate), Toast.LENGTH_LONG).show();
            return;
        }

        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("Title", meetingname.getText().toString());
                    jsonObject.put("Description", meetingduration.getText().toString());
                    jsonObject.put("StartDate", startsecond);
                    jsonObject.put("EndDate", endsecond);
                    jsonObject.put("LessonType", 5);
                    jsonObject.put("SchoolID", schoolId);
                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < customerList.size(); i++) {
                        JSONObject j = new JSONObject();
                        j.put("MemberID", customerList.get(i).getUserID());
                        j.put("Role", 1);
                        jsonArray.put(j);
                    }
                    JSONObject j2 = new JSONObject();
                    j2.put("MemberID", AppConfig.UserID);
                    j2.put("Role", 2);
                    jsonArray.put(j2);
                    jsonObject.put("LessonMembers", jsonArray);
                    Log.e("fff", "submit4");
                    JSONObject returnJson = ConnectService.submitDataByJson(AppConfig.URL_PUBLIC + "Lesson/AddScheduleMeetingLesson", jsonObject);
                    Log.e("AddScheduleMeeting", jsonObject.toString() + "    " + returnJson.toString());
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
