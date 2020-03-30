package com.ub.service.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.service.ConnectService;
import com.kloudsync.techexcel.ui.DocAndMeetingActivityV2;
import com.kloudsync.techexcel.ui.MeetingViewActivity;
import com.ub.techexcel.adapter.NotifyAdapter;
import com.ub.techexcel.bean.LineItem;
import com.ub.techexcel.bean.ServiceBean;
import com.ub.techexcel.bean.UpcomingLesson;
import com.ub.techexcel.tools.SpliteSocket;
import com.ub.techexcel.tools.Tools;

import org.java_websocket.WebSocket;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 2017/11/30.
 */

public class NotifyActivity extends Activity implements View.OnClickListener {

    private LinearLayout backll;
    private TextView courseid;
    private ListView courseList;
    private List<ServiceBean> mList = new ArrayList<>();
    private NotifyAdapter notifyAdapter;
    private TextView joinroom;
    private EditText roomet;
    private int lessionid;
    private LinearLayout rootlayout;
    private UpcomingLesson lesson = null;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x1101:
                    notifyAdapter = new NotifyAdapter(NotifyActivity.this, mList);
                    notifyAdapter.setOnModifyCourseListener(new NotifyAdapter.OnModifyCourseListener() {
                        @Override
                        public void join(int position) {
                            Intent intent = new Intent(NotifyActivity.this, MeetingViewActivity.class);
                            intent.putExtra("userid", mList.get(position).getUserId());
                            intent.putExtra("meetingId", mList.get(position).getId() + "");
                            intent.putExtra("teacherid", mList.get(position).getTeacherId());
                            intent.putExtra("identity", mList.get(position).getRoleinlesson());
                            intent.putExtra("isInstantMeeting", 0);
                            startActivity(intent);
                        }

                        @Override
                        public void leave(int position) {
                            if (AppConfig.webSocketClient.getReadyState() == WebSocket.READYSTATE.OPEN) {
                                notifyend(mList.get(position).getId() + "");
                                //取消加入课程
                                sendStringBySocket2("1@REMOVE_JOIN_MEETING_NOTICE", AppConfig.UserToken, mList.get(position).getId() + "");
                                //刪除此条记录
                                mList.remove(position);
                                notifyAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                    courseList.setAdapter(notifyAdapter);
                    sendstatussocket();
                    break;

                case 0x1003:  // CheckClassRoomExist
                    int retdata2 = (int) msg.obj;
                    if (retdata2 == 0) { //不存在
                        Toast.makeText(NotifyActivity.this, "你加入的课堂不存在!", Toast.LENGTH_LONG).show();
                    } else if (retdata2 == 1) { // 存在
                        getClassRoomLessonID(roomid);
                    }
                    break;
                case 0x1001:   // getClassRoomLessonID
                    lessionid = (int) msg.obj;
                    getClassRoomTeacherID(roomid);
                    break;
                case 0x1004:  // getClassRoomTeacherID
                    teacherid = (int) msg.obj;
                    if (lessionid == -1) {     //看看老师是否正在上课
                        getUpcomingLessonList(teacherid + "");
                    } else {
                        Intent ii = new Intent(NotifyActivity.this, DocAndMeetingActivityV2.class);
                        ii.putExtra("meetingId", roomid + "");
                        ii.putExtra("identity", 1);  // 学生
                        ii.putExtra("ishavedefaultpage",true);
                        ii.putExtra("lessionId", lessionid + "");
                        ii.putExtra("isInstantMeeting", 1);
                        ii.putExtra("teacherid", teacherid + "");
                        startActivity(ii);
                        finish();
                    }
                    break;
                case 0x1305:
                    for (int i = 0; i < upcomingLessonList.size(); i++) {
                        if (upcomingLessonList.get(i).getIsOnGoing() == 1) {
                            lesson = upcomingLessonList.get(i);
                        }
                    }
                    if (null == lesson) {  // 进去等待
                        Intent ii = new Intent(NotifyActivity.this, DocAndMeetingActivityV2.class);
                        ii.putExtra("meetingId", roomid + "");
                        ii.putExtra("identity", 1);  // 学生
                        ii.putExtra("lessionId", lessionid + "");
                        ii.putExtra("ishavedefaultpage",true);
                        ii.putExtra("isInstantMeeting", 1);
                        ii.putExtra("teacherid", teacherid + "");
                        startActivity(ii);
                        finish();
                    } else {
                        if (lesson.getIsInClassroom() == 1) {
                            Intent ii = new Intent(NotifyActivity.this, DocAndMeetingActivityV2.class);
                            ii.putExtra("meetingId", roomid + "");
                            ii.putExtra("identity", 1);  // 学生
                            ii.putExtra("ishavedefaultpage",true);
                            ii.putExtra("lessionId", lessionid + "");
                            ii.putExtra("isInstantMeeting", 1);
                            ii.putExtra("teacherid", teacherid + "");
                            startActivity(ii);
                            finish();
                        } else {
                            Intent ii = new Intent(NotifyActivity.this, MeetingViewActivity.class);
                            ii.putExtra("meetingId", lesson.getLessonID() + "");
                            ii.putExtra("identity", 1);  // 学生
                            ii.putExtra("lessionId", lessionid + "");
                            ii.putExtra("isInstantMeeting", 0);
                            ii.putExtra("teacherid", teacherid + "");
                            startActivity(ii);
                            finish();
                        }
                    }
                    break;
            }
        }
    };

    private PopupWindow joinasPopup;
    private int teacherid;
    private RelativeLayout upcomeingrel;
    private TextView coursename;
    private TextView upcomeingtv;

    private void initjoinasPopup() {

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater
                .inflate(R.layout.joinaspop, null);

        upcomeingrel = (RelativeLayout) view.findViewById(R.id.upcomeingrel);
        coursename = (TextView) view.findViewById(R.id.coursename);
        upcomeingtv = (TextView) view.findViewById(R.id.upcomeingtv);
        upcomeingtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lesson.getIsInClassroom() == 1) {
                    Intent ii = new Intent(NotifyActivity.this, DocAndMeetingActivityV2.class);
                    ii.putExtra("meetingId", roomid + "");
                    ii.putExtra("identity", 1);  // 学生
                    ii.putExtra("lessionId", lessionid + "");
                    ii.putExtra("isInstantMeeting", 1);
                    ii.putExtra("teacherid", teacherid + "");
                    startActivity(ii);
                    finish();
                } else {
                    Intent ii = new Intent(NotifyActivity.this, MeetingViewActivity.class);
                    ii.putExtra("meetingId", lesson.getLessonID() + "");
                    ii.putExtra("identity", 1);  // 学生
                    ii.putExtra("lessionId", lessionid + "");
                    ii.putExtra("isInstantMeeting", 0);
                    ii.putExtra("teacherid", teacherid + "");
                    startActivity(ii);
                    finish();
                }
            }
        });

        RelativeLayout joinrel1 = (RelativeLayout) view.findViewById(R.id.joinrel1);
        joinrel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinasPopup.dismiss();
                Log.e("notifity", roomid + "    " + lessionid + "   " + teacherid + "   ");
                Intent ii = new Intent(NotifyActivity.this, DocAndMeetingActivityV2.class);
                ii.putExtra("meetingId", roomid + "");
                ii.putExtra("identity", 1);  // 学生
                ii.putExtra("lessionId", lessionid + "");
                ii.putExtra("isInstantMeeting", 1);
                ii.putExtra("teacherid", teacherid + "");
                startActivity(ii);
                finish();
            }
        });

        RelativeLayout joinrel2 = (RelativeLayout) view.findViewById(R.id.joinrel2);
        joinrel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ii = new Intent(NotifyActivity.this, DocAndMeetingActivityV2.class);
                ii.putExtra("meetingId", roomid + "");
                ii.putExtra("identity", 1);  // 学生
                ii.putExtra("lessionId", lessionid + "");
                ii.putExtra("isInstantMeeting", 1);
                ii.putExtra("teacherid", teacherid + "");
                startActivity(ii);
                finish();
            }
        });

        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinasPopup.dismiss();
            }
        });
        WindowManager wm = (WindowManager)
                getSystemService(WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        joinasPopup = new PopupWindow(view, width - 30,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        joinasPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                getWindow().getDecorView().setAlpha(1.0f);
            }
        });
        joinasPopup.setBackgroundDrawable(new BitmapDrawable());
        joinasPopup.setAnimationStyle(R.style.anination2);
        joinasPopup.setFocusable(true);

    }


    private void notifyend(String meetingId) {
        for (int i = 0; i < AppConfig.progressCourse.size(); i++) {
            if (AppConfig.progressCourse.get(i).getMeetingId().equals(meetingId)) {
                AppConfig.progressCourse.remove(i);
                Intent removeintent = new Intent();
                removeintent.setAction(getResources().getString(R.string.Receive_Course));
                sendBroadcast(removeintent);
                break;
            }
        }
    }

    //改变已读状态
    private void sendstatussocket() {
        String index = "";
        //设置所有course已读
        for (int i = 0; i < AppConfig.progressCourse.size(); i++) {
            if (!AppConfig.progressCourse.get(i).isStatus()) {
                index += AppConfig.progressCourse.get(i).getMeetingId() + ",";
            }
        }
        if (index.length() > 0) {
            index = index.substring(0, index.length() - 1);
        }
        try {
            JSONObject loginjson = new JSONObject();
            loginjson.put("action", "UPDATE_TO_JOIN_MEETING_READ_STATUS");
            loginjson.put("sessionId", AppConfig.UserToken);
            loginjson.put("meetingIds", index);
            String ss = loginjson.toString();
            SpliteSocket.sendMesageBySocket(ss);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    private void sendStringBySocket2(String action, String sessionId, String meetingId2) {
        try {
            JSONObject loginjson = new JSONObject();
            loginjson.put("action", action);
            loginjson.put("sessionId", sessionId);
            loginjson.put("meetingIds", meetingId2);
            String ss = loginjson.toString();
            SpliteSocket.sendMesageBySocket(ss);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notify);
        initView();
        initjoinasPopup();
        getProgressItems();

    }


    private void getProgressItems() {
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                String ss = "";
                for (int i = 0; i < AppConfig.progressCourse.size(); i++) {
                    String s = AppConfig.progressCourse.get(i).getMeetingId();
                    if (i == 0) {
                        ss += s;
                    } else {
                        ss += "," + s;
                    }
                }
                JSONObject courseJson = ConnectService.getIncidentbyHttpGet(AppConfig.URL_PUBLIC + "Lesson/Items?lessonIDs=" + ss);
                Log.e("---------------", AppConfig.URL_PUBLIC + "Items?serviceIDs=" + ss + "            " + courseJson.toString());
                formatCourse(courseJson);
            }
        }).start(ThreadManager.getManager());
    }


    private void formatCourse(JSONObject courseJson) {
        try {
            int retcode = courseJson.getInt("RetCode");
            switch (retcode) {
                case AppConfig.RETCODE_SUCCESS:
                    JSONArray retdata = courseJson.getJSONArray("RetData");
                    for (int i = 0; i < retdata.length(); i++) {
                        JSONObject service = retdata.getJSONObject(i);
                        ServiceBean bean = new ServiceBean();
                        String name = service.getString("Name");
                        bean.setName((name == null || name.equals("null")) ? ""
                                : name);
                        int concernid = service.getInt("ConcernID");
                        bean.setConcernID(concernid);

                        bean.setCategoryID(service.getInt("CategoryID"));
                        bean.setSubCategoryID(service.getInt("SubCategoryID"));
                        int statusID = service.getInt("StatusID");
                        bean.setStatusID(statusID);
                        bean.setId(service.getInt("ID"));

                        bean.setUserId(service.getInt("StudentID") + "");
                        bean.setTeacherId(service.getString("TeacherID"));
                        bean.setRoleinlesson(service.getInt("RoleInLesson"));

                        JSONObject user = service.getJSONObject("User");
                        Customer customer = new Customer();
                        customer.setUserID(user.getString("ID"));
                        customer.setUrl(user.getString("AvatarUrl"));
                        customer.setName(user.getString("Name"));
                        bean.setCustomer(customer);

                        // lineitems
                        JSONArray lineitems = service.getJSONArray("LineItems");
                        List<LineItem> items = new ArrayList<LineItem>();
                        for (int j = 0; j < lineitems.length(); j++) {
                            JSONObject lineitem = lineitems.getJSONObject(j);
                            LineItem item = new LineItem();
                            item.setIncidentID(lineitem.getInt("IncidentID"));
                            item.setEventID(lineitem.getInt("EventID"));

                            String linename = lineitem.getString("EventName");
                            item.setEventName((linename == null || linename
                                    .equals("null")) ? "" : linename);

                            item.setFileName(lineitem.getString("FileName"));
                            if (TextUtils.isEmpty(lineitem.getString("AttachmentH5Url"))) {
                                item.setUrl(lineitem.getString("AttachmentUrl"));
                                item.setHtml5(false);
                            } else {
                                item.setUrl(lineitem.getString("AttachmentH5Url"));
                                item.setHtml5(true);
                            }

                            item.setAttachmentID(lineitem.getString("AttachmentID"));
                            items.add(item);
                        }
                        bean.setLineItems(items);
                        mList.add(bean);
                        handler.sendEmptyMessage(0x1101);
                    }
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void initView() {
        rootlayout = (LinearLayout) findViewById(R.id.rootlayout);
        backll = (LinearLayout) findViewById(R.id.backll);
        courseList = (ListView) findViewById(R.id.courseList);
        backll.setOnClickListener(this);
        joinroom = (TextView) findViewById(R.id.joinroom2);
        roomet = (EditText) findViewById(R.id.roomet);
        joinroom.setOnClickListener(this);
        courseid = (TextView) findViewById(R.id.courseid);
        courseid.setText(AppConfig.ClassRoomID);
    }


    private void getClassRoomLessonID(final String classRoomId) {
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = ConnectService.getIncidentbyHttpGet(AppConfig.URL_PUBLIC + "Lesson/GetClassRoomLessonID?classRoomID=" + classRoomId);
                    Log.e("getClassRoomLessonID", jsonObject.toString()); // {"RetCode":0,"ErrorMessage":null,"DetailMessage":null,"RetData":-1}
                    int retCode = jsonObject.getInt("RetCode");
                    switch (retCode) {
                        case 0:
                            int retdate = jsonObject.getInt("RetData");
                            Message msg = Message.obtain();
                            msg.what = 0x1001;
                            msg.obj = retdate;
                            handler.sendMessage(msg);
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());

    }


    private void checkClassRoomExist(final String classRoomId) {
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = ConnectService.getIncidentbyHttpGet(AppConfig.URL_PUBLIC + "Lesson/CheckClassRoomExist?classroomID=" + classRoomId);
                    Log.e("getClassRoomLessonID3", jsonObject.toString()); // {"RetCode":0,"ErrorMessage":null,"DetailMessage":null,"RetData":-1}
                    int retCode = jsonObject.getInt("RetCode");
                    switch (retCode) {
                        case 0:
                            int retdate = jsonObject.getInt("RetData");
                            Message msg = Message.obtain();
                            msg.what = 0x1003;
                            msg.obj = retdate;
                            handler.sendMessage(msg);
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());

    }

    private void getClassRoomTeacherID(final String classRoomId) {
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = ConnectService.getIncidentbyHttpGet(AppConfig.URL_PUBLIC + "Lesson/GetClassRoomTeacherID?classroomID=" + classRoomId);
                    Log.e("getClassRoomLessonID4", jsonObject.toString()); // {"RetCode":0,"ErrorMessage":null,"DetailMessage":null,"RetData":-1}
                    int retCode = jsonObject.getInt("RetCode");
                    switch (retCode) {
                        case 0:
                            int retdate = jsonObject.getInt("RetData");
                            Message msg = Message.obtain();
                            msg.what = 0x1004;
                            msg.obj = retdate;
                            handler.sendMessage(msg);
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }


    private String roomid;

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.backll:
                finish();
                overridePendingTransition(R.anim.tran_in7, R.anim.tran_out7);
                break;
            case R.id.joinroom2:
                if (!Tools.isFastClick()) {
                    InputMethodManager imm = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(roomet.getWindowToken(), 0);
                    roomid = roomet.getText().toString();
                    if (!TextUtils.isEmpty(roomid)) {
                        checkClassRoomExist(roomid);
                    } else {
                        Toast.makeText(NotifyActivity.this, getString(R.string.joinroom), Toast.LENGTH_LONG).show();
                    }
                }
                break;
            default:
                break;
        }
    }


    private List<UpcomingLesson> upcomingLessonList = new ArrayList<>();

    private void getUpcomingLessonList(final String teacherid) {
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = ConnectService.getIncidentbyHttpGet(AppConfig.URL_PUBLIC + "Lesson/UpcomingLessonList?teacherID=" + teacherid);
                    Log.e("upcoming", teacherid + "   " + jsonObject.toString());
                    int retCode = jsonObject.getInt("RetCode");
                    switch (retCode) {
                        case 0:
                            JSONArray jsonArray = jsonObject.getJSONArray("RetData");
                            upcomingLessonList.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject js = jsonArray.getJSONObject(i);
                                UpcomingLesson upcomingLesson = new UpcomingLesson();
                                upcomingLesson.setLessonID(js.getString("LessonID"));
                                upcomingLesson.setTitle(js.getString("Title"));
                                upcomingLesson.setStartDate(js.getString("StartDate"));
                                upcomingLesson.setTeacherID(js.getString("TeacherID"));
                                upcomingLesson.setStudentID(js.getString("StudentID"));
                                upcomingLesson.setCourseID(js.getString("CourseID"));
                                upcomingLesson.setCourseName(js.getString("CourseName"));
                                upcomingLesson.setLectureIDs(js.getString("LectureIDs"));
                                upcomingLesson.setIsInClassroom(js.getInt("IsInClassroom"));
                                upcomingLesson.setIsOnGoing(js.getInt("IsOnGoing"));
                                upcomingLessonList.add(upcomingLesson);
                            }
                            Message message = Message.obtain();
                            message.what = 0x1305;
                            handler.sendMessage(message);
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




