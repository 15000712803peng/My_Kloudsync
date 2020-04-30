package com.kloudsync.techexcel.start;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.EventJoinMeeting;
import com.kloudsync.techexcel.bean.LoginData;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.service.ConnectService;
import com.kloudsync.techexcel.ui.DocAndMeetingActivity;
import com.ub.service.activity.SocketService;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.kloudsync.techexcel.config.AppConfig.ClassRoomID;

public class JoinMeetingActivity extends Activity implements View.OnClickListener {

    private TextView joinmeeting;
    private EditText meetingid;
    private EditText meetingname;
    private ImageView arrowback;
    private TextView meetingidhead,meetingnamehead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joinmeeting);
        initView();
        startWBService();
    }
    Intent service;
    private void startWBService() {
        service = new Intent(getApplicationContext(), SocketService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(service);
        } else {
            startService(service);
        }
    }
    private void initView() {
        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        editor = sharedPreferences.edit();
        meetingid = findViewById(R.id.meetingid);
        meetingname = findViewById(R.id.meetingname);
        joinmeeting = findViewById(R.id.joinmeeting);
        meetingidhead = findViewById(R.id.meetingidhead);
        meetingnamehead = findViewById(R.id.meetingnamehead);
        joinmeeting.setOnClickListener(this);
        arrowback = findViewById(R.id.arrowback);
        arrowback.setOnClickListener(this);
        meetingid.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("输入过程中执行该方法", "文字变化");
                if (!TextUtils.isEmpty(s)&&s.length()>0){
                    meetingidhead.setVisibility(View.VISIBLE);
                }else{
                    meetingidhead.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                Log.e("输入前确认执行该方法", "开始输入");
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("输入结束执行该方法", "输入结束");
            }
        });
        meetingname.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)&&s.length()>0){
                    meetingnamehead.setVisibility(View.VISIBLE);
                }else{
                    meetingnamehead.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.joinmeeting:
                joinmeeeting();
                break;
            case R.id.arrowback:
                finish();
                break;
        }
    }

    private void joinmeeeting() {
          String meetingidc=meetingid.getText().toString();
          if(!TextUtils.isEmpty(meetingidc)){
              doJoin(meetingidc.toUpperCase());
          }else {
              Toast.makeText(this, getString(R.string.joinroom), Toast.LENGTH_LONG).show();
          }
    }

    private void doJoin(final String meetingRoom) {
        Observable.just(meetingRoom).observeOn(Schedulers.io()).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String meetingId) throws Exception {
                JSONObject result = ConnectService.getIncidentbyHttpGet(AppConfig.URL_PUBLIC + "User/CheckLessonOrClassroomExist?id=" + meetingId);
                Log.e("do_join", AppConfig.URL_PUBLIC + AppConfig.URL_PUBLIC + "User/CheckLessonOrClassroomExist?id=" + meetingId+ ",result:" + result);
                if (result.has("RetCode")) {
                    int retCode = result.getInt("RetCode");
                    if (retCode == 0) {
                        if (result.has("RetData")) {
                            if (result.getInt("RetData") == 1) {
                                final EventJoinMeeting joinMeeting = new EventJoinMeeting();
                                joinMeeting.setMeetingId(meetingRoom);
                                Observable.just(joinMeeting).observeOn(Schedulers.newThread()).doOnNext(new Consumer<EventJoinMeeting>() {
                                    @Override
                                    public void accept(EventJoinMeeting eventJoinMeeting) throws Exception {
                                        String url= AppConfig.URL_PUBLIC+"User/CreateOrUpdateInstantAccout";
                                        JSONObject result =ServiceInterfaceTools.getinstance().createOrUpdateInstantAccout(url,  meetingname.getText().toString());
                                        Log.e("do_join", url+ ",result:" + result);
                                        if (result.has("RetCode")) {
                                            int code = result.getInt("RetCode");
                                            if (code == 0) {
                                                  JSONObject data=result.getJSONObject("RetData");
                                                  saveLoginData(data);
                                            }
                                        }
                                    }
                                }).doOnNext(new Consumer<EventJoinMeeting>() {
                                    @Override
                                    public void accept(EventJoinMeeting eventJoinMeeting) throws Exception {
                                        JSONObject result = ConnectService.getIncidentbyHttpGet(AppConfig.URL_PUBLIC + "Lesson/GetClassRoomLessonID?classRoomID=" + meetingRoom);
                                        Log.e("do_join", AppConfig.URL_PUBLIC + "Lesson/GetClassRoomLessonID?classRoomID=" + meetingRoom + ",result:" + result);
                                        if (result.has("RetCode")) {
                                            int retCode = result.getInt("RetCode");
                                            if (retCode == 0) {
                                                joinMeeting.setLessionId(result.getInt("RetData"));
                                                joinMeeting.setMeetingId(meetingRoom);
                                                joinMeeting.setOrginalMeetingId(meetingRoom);
                                            }
                                        }
                                    }
                                }).doOnNext(new Consumer<EventJoinMeeting>() {
                                    @Override
                                    public void accept(EventJoinMeeting eventJoinMeeting) throws Exception {
                                        if (joinMeeting.getLessionId() <= 0) {
                                            JSONObject result = ConnectService.getIncidentbyHttpGet(AppConfig.URL_PUBLIC + "Lesson/GetClassRoomTeacherID?classroomID=" + meetingRoom);
                                            Log.e("do_join", AppConfig.URL_PUBLIC + "Lesson/GetClassRoomTeacherID?classroomID=" + meetingRoom + ",result:" + result);
                                            if (result.has("RetCode")) {
                                                int retCode = result.getInt("RetCode");
                                                if (retCode == 0) {
                                                    int hostId = result.getInt("RetData");
                                                    eventJoinMeeting.setHostId(hostId);
                                                }
                                            }
                                        }

                                    }
                                }).doOnNext(new Consumer<EventJoinMeeting>() {
                                    @Override
                                    public void accept(EventJoinMeeting eventJoinMeeting) throws Exception {
                                        if (eventJoinMeeting.getLessionId() != -1) {
                                            JSONObject result = ServiceInterfaceTools.getinstance().syncGetJoinMeetingDefaultStatus(eventJoinMeeting.getOrginalMeetingId());
                                            Log.e("do_join", "syncGetJoinMeetingDefaultStatus" + ",result:" + result);
                                            if (result.has("code")) {
                                                int code = result.getInt("code");
                                                if (code == 0) {
                                                    JSONObject data = result.getJSONObject("data");
                                                    eventJoinMeeting.setRole(data.getInt("role"));
                                                }
                                            }
                                        }
                                    }
                                }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<EventJoinMeeting>() {
                                    @Override
                                    public void accept(EventJoinMeeting eventJoinMeeting) throws Exception {
//                                        EventBus.getDefault().post(eventJoinMeeting);
                                        enterMeeting(eventJoinMeeting);
                                    }
                                }).subscribe();
                            } else {
                                Observable.just("loading_main_thread").observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                                    @Override
                                    public void accept(String s) throws Exception {
                                        Toast.makeText(JoinMeetingActivity.this, "输入正确的会议ID", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    } else {
                        // 会议id不合法或者不存在
                        Observable.just("loading_main_thread").observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                                Toast.makeText(JoinMeetingActivity.this, "输入正确的会议ID", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        }).subscribe();

    }

    private void enterMeeting(EventJoinMeeting eventJoinMeeting) {
        Log.e("do_join", "eventJoinMeeting:" + eventJoinMeeting);
        if (eventJoinMeeting.getLessionId() == -1) {
            goToWatingMeeting(eventJoinMeeting);
            return;
        }
        Intent intent = new Intent(JoinMeetingActivity.this, DocAndMeetingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("meeting_id", eventJoinMeeting.getMeetingId());
        intent.putExtra("meeting_type", 0);
        intent.putExtra("lession_id", eventJoinMeeting.getLessionId());
        intent.putExtra("meeting_role", eventJoinMeeting.getRole());
        intent.putExtra("from_meeting", true);
        startActivity(intent);
    }

    private void goToWatingMeeting(EventJoinMeeting eventJoinMeeting){
        Intent intent = new Intent(JoinMeetingActivity.this, DocAndMeetingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("meeting_id", eventJoinMeeting.getMeetingId());
        intent.putExtra("meeting_type", 0);
        intent.putExtra("lession_id", -1);
        intent.putExtra("meeting_role", eventJoinMeeting.getRole());
        intent.putExtra("from_meeting", true);
        startActivity(intent);
    }

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    private void saveLoginData(JSONObject data) {
        try {
            AppConfig.UserToken = data.getString("UserToken");
            AppConfig.UserID = data.getInt("UserID") + "";
            AppConfig.UserName = data.getString("Name");
            AppConfig.SchoolID = 0;
            AppConfig.Role = 0;
            ClassRoomID = data.getString("ClassRoomID");
            editor.putString("UserID", AppConfig.UserID);
            editor.putString("UserToken", AppConfig.UserToken);
            editor.putString("Name", AppConfig.UserName);
            editor.putString("MeetingId",ClassRoomID);
            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
