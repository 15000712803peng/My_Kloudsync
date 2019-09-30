package com.ub.service.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.service.ConnectService;
import com.ub.techexcel.adapter.UpcomeingAdapter;
import com.ub.techexcel.bean.CourseLesson;
import com.ub.techexcel.bean.UpcomingLesson;
import com.ub.techexcel.tools.StartLessonPopup;
import com.ub.techexcel.tools.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 2017/11/30.
 */

public class MyKlassroomActivity extends Activity implements View.OnClickListener {

    private LinearLayout backll;
    private TextView courseid;
    private TextView joinmycourse;
    private TextView joinmycourse2;
    private int lessionid;
    private ListView listview;
    private List<UpcomingLesson> upcomingLessonList = new ArrayList<>();
    private UpcomeingAdapter upcomeingAdapter;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x1001:   // start meeting
                    lessionid = (int) msg.obj;
                    if (lessionid == -1) {
                        addInstantLesson(AppConfig.ClassRoomID);
                    } else {
                        Intent intent = new Intent(MyKlassroomActivity.this, WatchCourseActivity3.class);
                        intent.putExtra("meetingId", AppConfig.ClassRoomID + "");
                        intent.putExtra("identity", 2);
                        intent.putExtra("lessionId", lessionid + "");
                        intent.putExtra("ishavedefaultpage",true);
                        intent.putExtra("isInstantMeeting", 1);
                        intent.putExtra("teacherid", AppConfig.UserID.replace("-", ""));
                        intent.putExtra("isStartCourse", true);
                        startActivity(intent);
                        finish();
                    }
                    break;
                case 0x1002:  //  addInstantLesson
                    Log.e("getClassRoomLessonID", "加入课程成功");
                    int lessionid2 = (int) msg.obj;  // 新建分配的lessionid
                    Intent intent = new Intent(MyKlassroomActivity.this, WatchCourseActivity3.class);
                    intent.putExtra("meetingId", AppConfig.ClassRoomID + "");
                    intent.putExtra("identity", 2);
                    intent.putExtra("ishavedefaultpage",true);
                    intent.putExtra("lessionId", lessionid2 + "");
                    intent.putExtra("isInstantMeeting", 1);
                    intent.putExtra("teacherid", AppConfig.UserID.replace("-", ""));
                    intent.putExtra("isStartCourse", true);
                    startActivity(intent);
                    finish();
                    break;
                case 0x1305:
                    upcomeingAdapter = new UpcomeingAdapter(MyKlassroomActivity.this, upcomingLessonList, R.layout.selectcourse_item);
                    upcomeingAdapter.setStartMeetingListenering(new UpcomeingAdapter.StartMeetingListenering() {
                        @Override
                        public void viewOldCourse(int position) {
                            Intent intent = new Intent(MyKlassroomActivity.this, WatchCourseActivity2.class);
                            intent.putExtra("userid", upcomingLessonList.get(position).getStudentID());
                            intent.putExtra("meetingId", upcomingLessonList.get(position).getLessonID() + "");
                            intent.putExtra("teacherid", upcomingLessonList.get(position).getTeacherID());
                            intent.putExtra("identity", 2);
                            intent.putExtra("isInstantMeeting", 0);
                            startActivity(intent);
                            finish();
                        }
                    });
                    listview.setAdapter(upcomeingAdapter);
                    break;
                case 0x1105:  //start lesson
                    int lessonid = (int) msg.obj;
                    Intent lessonintent = new Intent(MyKlassroomActivity.this, WatchCourseActivity3.class);
                    lessonintent.putExtra("meetingId", AppConfig.ClassRoomID + "");
                    lessonintent.putExtra("identity", 2);
                    lessonintent.putExtra("lessionId", lessonid + "");
                    lessonintent.putExtra("isInstantMeeting", 1);
                    lessonintent.putExtra("teacherid", AppConfig.UserID.replace("-", ""));
                    lessonintent.putExtra("isStartCourse", true);
                    lessonintent.putExtra("isInClassroom", true);

                    startActivity(lessonintent);
                    finish();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myklassroom);
        initView();
        getCourselist();
        getUpcomingLessonList(AppConfig.UserID.replace("-", ""));

    }


    private void initView() {

        backll = (LinearLayout) findViewById(R.id.backll);
        backll.setOnClickListener(this);
        joinmycourse = (TextView) findViewById(R.id.joinmycourse);
        joinmycourse2 = (TextView) findViewById(R.id.joinmycourse2);
        courseid = (TextView) findViewById(R.id.courseid);
        courseid.setText(AppConfig.ClassRoomID.replace("-", ""));
        joinmycourse.setOnClickListener(this);
        joinmycourse2.setOnClickListener(this);
        listview = (ListView) findViewById(R.id.courseList);

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

    private void addInstantLesson(final String classRoomId) {
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js = new JSONObject();
                    js.put("classroomID", classRoomId);
                    js.put("addBlankPage", 0);
                    JSONObject jsonObject = ConnectService.submitDataByJson(AppConfig.URL_PUBLIC + "Lesson/AddInstantLesson?classroomID=" + classRoomId+"&addBlankPage=0", js);
                    Log.e("getClassRoomLessonID2", jsonObject.toString()); // {"RetCode":0,"ErrorMessage":null,"DetailMessage":null,"RetData":2477}
                    int retCode = jsonObject.getInt("RetCode");
                    switch (retCode) {
                        case 0:
                            int retdate = jsonObject.getInt("RetData");
                            Message msg = Message.obtain();
                            msg.what = 0x1002;
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

    private void getUpcomingLessonList(final String teacherid) {
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = ConnectService.getIncidentbyHttpGet(AppConfig.URL_PUBLIC + "Lesson/UpcomingLessonList?teacherID=" + teacherid);
                    Log.e("upcoming", jsonObject.toString());
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


    private List<CourseLesson> courseLessons = new ArrayList<>();

    private void getCourselist() {
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = ConnectService.getIncidentbyHttpGet(AppConfig.URL_PUBLIC + "Course/List?listType=1");
                    Log.e("---------Courselist", jsonObject.toString());
                    int retcode = jsonObject.getInt("RetCode");
                    switch (retcode) {
                        case 0:
                            JSONArray array = jsonObject.getJSONArray("RetData");
                            courseLessons.clear();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject js = array.getJSONObject(i);
                                CourseLesson courseLesson = new CourseLesson();
                                courseLesson.setCourseID(js.getInt("CourseID"));
                                courseLesson.setTitle(js.getString("Title"));
                                courseLessons.add(courseLesson);
                            }
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backll:
                finish();
                overridePendingTransition(R.anim.tran_in7, R.anim.tran_out7);
                break;
            case R.id.joinmycourse:
                if (!Tools.isFastClick()) {
                    StartLessonPopup startLessonPopup = new StartLessonPopup();
                    startLessonPopup.getPopwindow(MyKlassroomActivity.this, (LinearLayout) findViewById(R.id.layout), courseLessons);
                    startLessonPopup.setStartLessonPopupListener(new StartLessonPopup.StartLessonPopupListener() {
                        @Override
                        public void dismiss() {
                            getWindow().getDecorView().setAlpha(1.0f);
                        }

                        @Override
                        public void open() {
                            getWindow().getDecorView().setAlpha(0.5f);
                        }

                        @Override
                        public void onItem(CourseLesson lesson) {
                            createLesson(lesson);
                        }
                    });
                    startLessonPopup.StartPop(joinmycourse);
                }
                break;
            case R.id.joinmycourse2:
                if (!Tools.isFastClick()) {
                    if (TextUtils.isEmpty(AppConfig.ClassRoomID)) {
                        Toast.makeText(MyKlassroomActivity.this, "你加入的课堂不存在!", Toast.LENGTH_LONG).show();
                    } else {
                        getClassRoomLessonID(AppConfig.ClassRoomID);
                    }
                }
                break;
            default:
                break;
        }

    }


    private void createLesson(final CourseLesson lesson) {
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject submitjson = new JSONObject();
                    submitjson.put("Title", lesson.getTitle());
                    submitjson.put("LessonID", "0");
                    submitjson.put("Description", "");
                    submitjson.put("CourseID", lesson.getCourseID());
                    submitjson.put("classroomID", AppConfig.ClassRoomID);
                    submitjson.put("LectureIDs", lesson.getLectureID());
                    submitjson.put("StartDate", System.currentTimeMillis());
                    submitjson.put("StudentID", "-1");
                    submitjson.put("EndDate", System.currentTimeMillis() + 2 * 60 * 60 * 1000);
                    JSONObject returnjson = ConnectService.submitDataByJson(AppConfig.URL_PUBLIC + "Lesson/CreateLesson", submitjson);
                    Log.e("---------returnjson", submitjson.toString() + "----------" + returnjson.toString());
                    int retcode = returnjson.getInt("RetCode");
                    switch (retcode) {
                        case 0:
                            int lessonid = returnjson.getInt("RetData");
                            Message msg = Message.obtain();
                            msg.obj = lessonid;
                            msg.what = 0x1105;
                            handler.sendMessage(msg);
                            break;
                    }
                } catch (JSONException e) {
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




