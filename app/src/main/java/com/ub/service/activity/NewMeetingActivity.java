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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.Attendee;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.AddGroupActivity2;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.service.ConnectService;
import com.kloudsync.techexcel.view.DatePickPop;
import com.kloudsync.techexcel.view.DurationPickPop;
import com.ub.techexcel.adapter.NewMeetingContactAdapter;
import com.ub.techexcel.bean.ServiceBean;
import com.ub.techexcel.tools.JoinMeetingPopup;
import com.ub.techexcel.tools.SelectMeetingDurationDialog;

import org.feezu.liuli.timeselector.TimeSelector;
import org.feezu.liuli.timeselector.Utils.TextUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wang on 2017/6/19.
 */
public class NewMeetingActivity extends Activity implements View.OnClickListener,SelectMeetingDurationDialog.OnDurationSelectedLinstener{

    private ImageView cancel;
    private Button submit;
    private EditText meetingname;
    private TextView meetingstartdate;
    private TextView meetingenddate;
    private TextView meetingduration, tv_p_schedule_size;

    private RelativeLayout as_rl_contact, startdaterl, enddaterl;
    private TextView invitecontact;
    //private RecyclerView mRecyclerView;
    private NewMeetingContactAdapter newMeetingContactAdapter;
    private SharedPreferences sharedPreferences;
    private int schoolId;
    private List<Customer> customerList = new ArrayList<>();
    private SimpleDraweeView as_img_contact_one, as_img_contact_two, as_img_contact_three;
    private ImageView durationArrowImage;
    private CheckBox checkbox;
    private LinearLayout inputmeetingsecret;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private long curDuration=45*1000*60;
    private ServiceBean bean;
    List<Attendee> attendees=new ArrayList<Attendee>();
    private int index=0;

    /**
     * 新建会议的时候开始时间
     * */
    private long createMeetStartSecond;
    /**
     * 新建会议的时候结束时间
     * */
    private long createMeetEndSecond;
    /**
     * 编辑会议的时候开始时间
     * */
    private long editMeetStartSecond;
    /**
     * 编辑会议的时候结束时间
     * */
    private long editMeetEndSecond;

    private boolean mIsFirstEnrer=true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newmeeting2);
        bean = (ServiceBean) getIntent().getSerializableExtra("servicebean");
        initView();
        if(bean!=null){
            submit.setEnabled(true);
            as_rl_contact.setEnabled(true);
            meetingname.setText(bean.getName());
            if (!TextUtil.isEmpty(bean.getPlanedStartDate())) {
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
                String start = "0:00", end = "0:00";
                String year = "";
                editMeetStartSecond = Long.parseLong(bean.getPlanedStartDate());
                editMeetEndSecond = Long.parseLong(bean.getPlanedEndDate());
                Date curDate = new Date(editMeetStartSecond);
                start = formatter.format(curDate);
                year = formatter2.format(curDate);
                meetingstartdate.setText(year + " " + start);
                curDuration=Long.parseLong(bean.getPlanedEndDate()) - Long.parseLong(bean.getPlanedStartDate());
                setDurationText((int)(curDuration/ (1000 * 60)));

                com.alibaba.fastjson.JSONArray jsonArray = com.alibaba.fastjson.JSONArray.parseArray(bean.getMembers());

                if (jsonArray != null) {
                    for(int i = 0; i < jsonArray.size(); i++) {
                        Attendee attendee=new Attendee();
                        com.alibaba.fastjson.JSONObject jsonObject = jsonArray.getJSONObject(i);
                        attendee.setMemberName(jsonObject.getString("MemberName"));
                        attendee.setAvatarUrl(jsonObject.getString("AvatarUrl"));
                        attendee.setRole(jsonObject.getInteger("Role"));
                        if(attendee.getRole()==1){
                            attendees.add(attendee);
                        }
                    }
                }
                if (attendees.size() >= 3) {
                    as_img_contact_one.setVisibility(View.VISIBLE);
                    as_img_contact_two.setVisibility(View.VISIBLE);
                    as_img_contact_three.setVisibility(View.VISIBLE);
                    tv_p_schedule_size.setVisibility(View.VISIBLE);
                    tv_p_schedule_size.setText(attendees.size() + "total");
                    as_img_contact_one.setImageURI(Uri.parse(attendees.get(0).getAvatarUrl()));
                    as_img_contact_two.setImageURI(Uri.parse(attendees.get(1).getAvatarUrl()));
                    as_img_contact_three.setImageURI(Uri.parse(attendees.get(2).getAvatarUrl()));
                } else if (attendees.size() >= 2) {
                    as_img_contact_one.setVisibility(View.VISIBLE);
                    as_img_contact_two.setVisibility(View.VISIBLE);
                    tv_p_schedule_size.setVisibility(View.VISIBLE);
                    tv_p_schedule_size.setText(attendees.size() + "total");
                    as_img_contact_one.setImageURI(Uri.parse(attendees.get(0).getAvatarUrl()));
                    as_img_contact_two.setImageURI(Uri.parse(attendees.get(1).getAvatarUrl()));
                    as_img_contact_three.setVisibility(View.GONE);
                } else if (attendees.size() >= 1) {
                    as_img_contact_one.setVisibility(View.VISIBLE);
                    tv_p_schedule_size.setVisibility(View.VISIBLE);
                    tv_p_schedule_size.setText(attendees.size() + "total");
                    as_img_contact_one.setImageURI(Uri.parse(attendees.get(0).getAvatarUrl()));
                    as_img_contact_two.setVisibility(View.GONE);
                    as_img_contact_three.setVisibility(View.GONE);
                } else {
                    as_img_contact_one.setVisibility(View.GONE);
                    as_img_contact_two.setVisibility(View.GONE);
                    as_img_contact_three.setVisibility(View.GONE);
                    tv_p_schedule_size.setVisibility(View.GONE);
                }
            }
        }else {
            submit.setEnabled(false);
            as_rl_contact.setEnabled(false);
        }
    }

    private void setDurationText(int diff){
        String[] durations = getResources().getStringArray(R.array.time_duration);
        String text="";

        switch (diff){
            case 15:
                index=0;
                text=durations[0];
                break;
            case 30:
                index=1;
                text=durations[1];
                break;
            case 45:
                index=2;
                text=durations[2];
                break;
            case 60:
                index=3;
                text=durations[3];
                break;
            case 90:
                index=4;
                text=durations[4];
                break;
            case 120:
                index=5;
                text=durations[5];
                break;
            case 180:
                index=6;
                text=durations[6];
                break;
            case 240:
                index=7;
                text=durations[7];
                break;
            case 300:
                index=8;
                text=durations[8];
                break;
            case 360:
                index=9;
                text=durations[9];
                break;
            case 420:
                index=10;
                text=durations[10];
                break;
            case 480:
                index=11;
                text=durations[11];
                break;

        }
        meetingduration.setText(text);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x1101 && resultCode == 3) {
            customerList = (List<Customer>) data.getSerializableExtra("customerList");
            if (customerList.size() >= 3) {
                as_img_contact_one.setVisibility(View.VISIBLE);
                as_img_contact_two.setVisibility(View.VISIBLE);
                as_img_contact_three.setVisibility(View.VISIBLE);
                tv_p_schedule_size.setVisibility(View.VISIBLE);
                tv_p_schedule_size.setText(customerList.size() + "total");
                as_img_contact_one.setImageURI(Uri.parse(customerList.get(0).getUrl()));
                as_img_contact_two.setImageURI(Uri.parse(customerList.get(1).getUrl()));
                as_img_contact_three.setImageURI(Uri.parse(customerList.get(2).getUrl()));
            } else if (customerList.size() >= 2) {
                as_img_contact_one.setVisibility(View.VISIBLE);
                as_img_contact_two.setVisibility(View.VISIBLE);
                tv_p_schedule_size.setVisibility(View.VISIBLE);
                tv_p_schedule_size.setText(customerList.size() + "total");
                as_img_contact_one.setImageURI(Uri.parse(customerList.get(0).getUrl()));
                as_img_contact_two.setImageURI(Uri.parse(customerList.get(1).getUrl()));
                as_img_contact_three.setVisibility(View.GONE);
            } else if (customerList.size() >= 1) {
                as_img_contact_one.setVisibility(View.VISIBLE);
                tv_p_schedule_size.setVisibility(View.VISIBLE);
                tv_p_schedule_size.setText(customerList.size() + "total");
                as_img_contact_one.setImageURI(Uri.parse(customerList.get(0).getUrl()));
                as_img_contact_two.setVisibility(View.GONE);
                as_img_contact_three.setVisibility(View.GONE);
            } else {
                as_img_contact_one.setVisibility(View.GONE);
                as_img_contact_two.setVisibility(View.GONE);
                as_img_contact_three.setVisibility(View.GONE);
                tv_p_schedule_size.setVisibility(View.GONE);
            }
            int nameLength = meetingname.getText().toString().trim().length();
            int startDateLength = meetingstartdate.getText().toString().trim().length();
            int endDateLength = meetingenddate.toString().trim().length();
            if (nameLength>0&&startDateLength>0&&endDateLength>0){
                submit.setEnabled(true);
            }else {
                submit.setEnabled(false);
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
        cancel = (ImageView) findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(this);
        durationArrowImage = findViewById(R.id.image_duration_arrow);
        inputmeetingsecret = findViewById(R.id.inputmeetingsecret);
        checkbox = findViewById(R.id.checkbox);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    inputmeetingsecret.setVisibility(View.VISIBLE);
                }else{
                    inputmeetingsecret.setVisibility(View.GONE);
                }
            }
        });
        durationArrowImage.setOnClickListener(this);
        meetingname = (EditText) findViewById(R.id.meetingname);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd HH:mm:ss");
        String time = simpleDateFormat.format(new Date());
        meetingduration = (TextView) findViewById(R.id.meetingduration);
        meetingduration.setOnClickListener(this);
        tv_p_schedule_size = (TextView) findViewById(R.id.tv_p_schedule_size);

        as_rl_contact = (RelativeLayout) findViewById(R.id.as_rl_contact);
        as_rl_contact.setOnClickListener(this);
        meetingstartdate = (TextView) findViewById(R.id.meetingstartdate);
        startdaterl = (RelativeLayout) findViewById(R.id.startdaterl);

        enddaterl = (RelativeLayout) findViewById(R.id.enddaterl);
        meetingenddate = (TextView) findViewById(R.id.meetingenddate);

        startdaterl.setOnClickListener(this);
        enddaterl.setOnClickListener(this);
        as_img_contact_one = findViewById(R.id.as_img_contact_one);
        as_img_contact_two = findViewById(R.id.as_img_contact_two);
        as_img_contact_three = findViewById(R.id.as_img_contact_three);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        schoolId = sharedPreferences.getInt("SchoolID", -1);

        meetingname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(mIsFirstEnrer){
                    mIsFirstEnrer=false;
                }else {
                    int nameLength = meetingname.getText().toString().trim().length();
                    int startDateLength = meetingstartdate.getText().toString().trim().length();
                    int endDateLength = meetingenddate.toString().trim().length();
                    if (nameLength>0&&startDateLength>0&&endDateLength>0){
                        submit.setEnabled(true);
                    }else {
                        submit.setEnabled(false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
                //selectDate();
                showDatePop();
                break;
            //结束时间
            case R.id.enddaterl:
                //selectTime();
                break;
            case R.id.as_rl_contact:
                Intent intent3 = new Intent(this,
                        AddGroupActivity2.class);
                if(bean!=null){
                    intent3.putExtra("attendees", (Serializable) attendees);
                }
                startActivityForResult(intent3, 0x1101);
                break;
            case R.id.submit:
                if(bean!=null){
                    editMeetingSubmit();
                }else {
                    submit();
                }
                break;
            case R.id.image_duration_arrow:
            case R.id.meetingduration:
                //showJoinMeetingDialog();
                showDurationPop();
                break;
        }
    }
//    //结束时间
//    private void selectTime() {
//        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        Date curDate = new Date(System.currentTimeMillis());
//        String str = formatter.format(curDate);
//        TimeSelector timeSelector = new TimeSelector(NewMeetingActivity.this, new TimeSelector.ResultHandler() {
//            @Override
//            public void handle(String time) {
//                try {
//                    endsecond = formatter.parse(time).getTime();
//                    if (endsecond != 0) {
//                        meetingenddate.setVisibility(View.VISIBLE);
//                        if (endsecond > startsecond) {
//                            meetingenddate.setText(time);
//                            long duration = endsecond - startsecond;
//                            long days = duration / (1000 * 60 * 60 * 24);                       //以天为单位取整
//                            long hour = (duration % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);               //以小时为单位取整
//                            long min = duration % 86400000 % 3600000 / 60000;       //以分钟为单位取整
//                            long seconds = duration % 86400000 % 3600000 % 60000 / 1000;   //以秒为单位取整
//
//                            Log.e("laoyu", "天" + days + "小时" + hour + "分" + min);
//                            meetingduration.setText(days + "天" + hour + "时" + min + "分");
//                        } else {
//                            meetingenddate.setVisibility(View.GONE);
//                            Toast.makeText(NewMeetingActivity.this, "结束时间不能大于开始时间!", Toast.LENGTH_LONG).show();
//                        }
//                    } else {
//                        meetingenddate.setVisibility(View.GONE);
//                    }
//
//                    int nameLength = meetingname.getText().toString().trim().length();
//                    int startDateLength = meetingstartdate.getText().toString().trim().length();
//                    int endDateLength = meetingenddate.toString().trim().length();
//                    if (nameLength>0&&startDateLength>0&&endDateLength>0){
//                        submit.setEnabled(true);
//                    }else {
//                        submit.setEnabled(false);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }, str, "2030-12-31 00:00");
//        timeSelector.show();
//
//    }
//    //开始时间
//    private void selectDate() {
//        Date curDate = new Date(System.currentTimeMillis());
//        String str = formatter.format(curDate);
//        TimeSelector timeSelector = new TimeSelector(NewMeetingActivity.this, new TimeSelector.ResultHandler() {
//            @Override
//            public void handle(String time) {
//                try {
//
//                    startsecond = formatter.parse(time).getTime();
//                    if (startsecond != 0) {
//                        meetingstartdate.setVisibility(View.VISIBLE);
//                        if (endsecond != 0) {
//                            if (endsecond > startsecond) {
//                                meetingstartdate.setText(time);
//                           /* long duration = endsecond - startsecond;
//                            long hour= duration  / 3600000;               //以小时为单位取整
//                            long min = duration % 86400000 % 3600000 / 60000;       //以分钟为单位取整
//                            long seconds = duration % 86400000 % 3600000 % 60000 / 1000;   //以秒为单位取整
//                            meetingduration.setText(hour+"小时");*/
//                            } else {
//                                meetingstartdate.setVisibility(View.GONE);
//                                Toast.makeText(NewMeetingActivity.this, "结束时间不能大于开始时间!", Toast.LENGTH_LONG).show();
//                            }
//                        } else {
//                            meetingstartdate.setText(time);
//                        }
//                    } else {
//                        meetingstartdate.setVisibility(View.GONE);
//                    }
//                    int nameLength = meetingname.getText().toString().trim().length();
//                    int startDateLength = meetingstartdate.getText().toString().trim().length();
//                    int endDateLength = meetingenddate.toString().trim().length();
//                    if (nameLength>0&&startDateLength>0&&endDateLength>0){
//                        submit.setEnabled(true);
//                    }else {
//                        submit.setEnabled(false);
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }, str, "2030-12-31 00:00");
//        timeSelector.show();
//
//
//    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x1001:
                    AppConfig.newlesson = true;
                    setResult(1);
                    finish();
                    break;
            }
        }
    };

    /**
     * 新增会议
     * */
    private void submit() {
        if (meetingname.length() == 0) {
            Toast.makeText(this, getResources().getString(R.string.schnames), Toast.LENGTH_LONG).show();
            return;
        }
        if (createMeetStartSecond == 0) {
            Toast.makeText(this, getResources().getString(R.string.schdate), Toast.LENGTH_LONG).show();
            return;
        }
        createMeetEndSecond = createMeetStartSecond + curDuration;
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("Title", meetingname.getText().toString());
                    jsonObject.put("Description", meetingduration.getText().toString());
                    jsonObject.put("StartDate", createMeetStartSecond);
                    jsonObject.put("EndDate", createMeetEndSecond);
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

    /**
     * 编辑会议
     * */
    private void editMeetingSubmit() {
        editMeetEndSecond = editMeetStartSecond + curDuration;
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("LessonID", bean.getId());
                    jsonObject.put("Title", meetingname.getText().toString());
                    jsonObject.put("Description", meetingduration.getText().toString());
                    jsonObject.put("StartDate", editMeetStartSecond);
                    jsonObject.put("EndDate", editMeetEndSecond);

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

    SelectMeetingDurationDialog durationDialog;

    private void showJoinMeetingDialog() {
        if (durationDialog != null) {
            if (durationDialog.isShowing()) {
                durationDialog.dismiss();
            }
            durationDialog = null;
        }

        durationDialog = new SelectMeetingDurationDialog(this);
        durationDialog.setOnDurationSelectedLinstener(this);
        durationDialog.show();
    }

    SelectMeetingDurationDialog.DurationData durationData;

    @Override
    public void onDuratonSelected(SelectMeetingDurationDialog.DurationData duration) {
        this.durationData= duration;
        meetingduration.setText(duration.duration);
    }
    public void showDatePop() {
        DatePickPop datePickPop=new DatePickPop(this);
        datePickPop.show();
        datePickPop.setOnTimeCallBackListener(new DatePickPop.OnTimeCallBackListener() {
            @Override
            public void onTimeCallBack(String showTime, String valueTime) {
                try{
                    if(bean==null){//新增会议
                        createMeetStartSecond = formatter.parse(valueTime).getTime();
                    }else {//编辑会议
                        editMeetStartSecond = formatter.parse(valueTime).getTime();
                    }
                    meetingstartdate.setText(showTime);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void showDurationPop(){
        DurationPickPop durationPickPop=new DurationPickPop(this);
        if(bean!=null){
            durationPickPop.setInitPosition(index);
        }else {
            durationPickPop.setInitPosition(2);
        }
        durationPickPop.show();
        durationPickPop.setOnDurationCallBackListener(new DurationPickPop.OnDurationCallBackListener() {
            @Override
            public void onDurationCallBack(String show, long value) {
                meetingduration.setText(show);
                curDuration=value;
            }
        });
    }
}
