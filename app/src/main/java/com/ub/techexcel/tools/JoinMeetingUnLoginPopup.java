package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.EventJoinMeeting;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.LoadingDialog;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.service.ConnectService;
import com.kloudsync.techexcel.start.JoinMeetingActivity;
import com.kloudsync.techexcel.ui.DocAndMeetingActivity;
import com.ub.service.activity.SocketService;
import com.ub.techexcel.bean.UpcomingLesson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.MODE_PRIVATE;
import static com.kloudsync.techexcel.config.AppConfig.ClassRoomID;

/**
 * Created by wang on 2017/9/18.
 */

public class JoinMeetingUnLoginPopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public Dialog mPopupWindow;
    private View view;
    private TextView joinroom2;
    private EditText roomet;

    private TextView cancelText;
    private TextView meetingidcontent;


    public void getPopwindow(Context context) {
        this.mContext = context;
        width = mContext.getResources().getDisplayMetrics().widthPixels;
        getPopupWindowInstance();
        mPopupWindow.getWindow().setWindowAnimations(R.style.PopupAnimation5);
    }

    public void getPopupWindowInstance() {
        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }


    @SuppressLint("WrongConstant")
    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.newmeetingunlogin, null);
        joinroom2 = (TextView) view.findViewById(R.id.joinroom2);
        roomet = (EditText) view.findViewById(R.id.roomet);
        joinroom2.setOnClickListener(this);
        cancelText = view.findViewById(R.id.cancel);
        meetingidcontent = view.findViewById(R.id.meetingidcontent);
        cancelText.setOnClickListener(this);
        mPopupWindow = new Dialog(mContext, R.style.bottom_dialog);
        mPopupWindow.setContentView(view);
        mPopupWindow.getWindow().setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = mPopupWindow.getWindow().getAttributes();
        lp.width = mContext.getResources().getDisplayMetrics().widthPixels;
        mPopupWindow.getWindow().setAttributes(lp);
        roomet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String meetingId = roomet.getText().toString().trim();
                if (!TextUtils.isEmpty(meetingId)) {
                    joinroom2.setBackgroundResource(R.drawable.do_join_bg);
                } else {
                    joinroom2.setBackgroundResource(R.drawable.join_bg);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    private String meetingId;

    @SuppressLint("NewApi")
    public void show(String meetingId) {
        this.meetingId=meetingId;
        meetingidcontent.setText(mContext.getString(R.string.Klassroom_ID)+": "+meetingId);
        joinroom2.setBackgroundResource(R.drawable.join_bg);
        if (mPopupWindow != null) {
            mPopupWindow.show();
        }
    }


    public boolean isShowing() {
        return mPopupWindow.isShowing();
    }

    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.joinroom2:
                if (!Tools.isFastClick()) {
                    InputMethodManager imm = (InputMethodManager)
                            mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(roomet.getWindowToken(), 0);
                    joinmeeeting();
                }
                dismiss();
                break;
            case R.id.cancel:
                dismiss();
                break;
            default:
                break;
        }
    }

    private void joinmeeeting() {
        if(!TextUtils.isEmpty(meetingId)){
            doJoin(meetingId.toUpperCase());
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
                                        JSONObject result =ServiceInterfaceTools.getinstance().createOrUpdateInstantAccout(url,  roomet.getText().toString());
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
                                        Toast.makeText(mContext, "输入正确的会议ID", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    } else {
                        // 会议id不合法或者不存在
                        Observable.just("loading_main_thread").observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                                Toast.makeText(mContext, "输入正确的会议ID", Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(mContext, DocAndMeetingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("meeting_id", eventJoinMeeting.getMeetingId());
        intent.putExtra("meeting_type", 0);
        intent.putExtra("lession_id", eventJoinMeeting.getLessionId());
        intent.putExtra("meeting_role", eventJoinMeeting.getRole());
        intent.putExtra("from_meeting", true);
        mContext.startActivity(intent);
    }

    private void goToWatingMeeting(EventJoinMeeting eventJoinMeeting){
        Intent intent = new Intent(mContext, DocAndMeetingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("meeting_id", eventJoinMeeting.getMeetingId());
        intent.putExtra("meeting_type", 0);
        intent.putExtra("lession_id", -1);
        intent.putExtra("meeting_role", eventJoinMeeting.getRole());
        intent.putExtra("from_meeting", true);
        mContext.startActivity(intent);
    }

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    private void saveLoginData(JSONObject data) {

        sharedPreferences = mContext.getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        editor = sharedPreferences.edit();
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

            ((Activity)mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startWBService();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    Intent service;
    private void startWBService() {
        service = new Intent(mContext, SocketService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mContext.startForegroundService(service);
        } else {
            mContext.startService(service);
        }
    }








}
