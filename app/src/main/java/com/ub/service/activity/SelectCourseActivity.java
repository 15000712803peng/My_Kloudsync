package com.ub.service.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.service.ConnectService;
import com.ub.techexcel.adapter.LessonAdapter;
import com.ub.techexcel.bean.CourseLesson;
import com.ub.techexcel.bean.Lesson;
import com.ub.techexcel.bean.ServiceBean;
import com.ub.techexcel.tools.CalListviewHeight;
import com.umeng.analytics.MobclickAgent;

import org.feezu.liuli.timeselector.TimeSelector;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wang on 2017/6/19.
 */
public class SelectCourseActivity extends Activity implements View.OnClickListener {

    private RelativeLayout selectuser, selectcourse;
    private TextView cancel;
    private TextView cti, sl;
    private ListView listview;
    private LinearLayout ll2;
    private TextView addlesson;
    private TextView coursetempatename, coursename, studentname;
    private ServiceBean student = null;
    private CourseLesson courseLesson = new CourseLesson();
    private LessonAdapter lessonAdapter;
    private ScrollView scrollView;
    private List<Lesson> list = new ArrayList<>();
    public static SelectCourseActivity instance;
    private Lesson mLesson;
    private TextView submit;
    private SharedPreferences sharedPreferences;
    private int schoolId;
    private CheckBox onecheck, twocheck;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectcourse);
        initView();
        lessonAdapter = new LessonAdapter(SelectCourseActivity.this, list);
        lessonAdapter.setOnLessonListenering(new LessonAdapter.LessonListenering() {
            @Override
            public void delete(int position) {
                list.remove(position);
                lessonAdapter.notifyDataSetChanged();
                CalListviewHeight.setListViewHeightBasedOnChildren(listview);
            }

            @Override
            public void selectLectures(Lesson lesson) {
                mLesson = lesson;
                Intent intent = new Intent(SelectCourseActivity.this,
                        LectureListActivity.class);
                intent.putExtra("courseLessonid", courseLesson.getCourseID());
                intent.putExtra("mLectures", (Serializable) mLesson.getLectures());
                startActivity(intent);
            }

            @Override
            public void selectTime(final Lesson lesson) {
                final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date curDate = new Date(System.currentTimeMillis());
                String str = formatter.format(curDate);
                TimeSelector timeSelector = new TimeSelector(SelectCourseActivity.this, new TimeSelector.ResultHandler() {
                    @Override
                    public void handle(String time) {
                        try {
                            Long second = formatter.parse(time).getTime();
                            lesson.setStartData(second);
                            lesson.setEndData(second + 2 * 60 * 60 * 1000);
                            lessonAdapter.notifyDataSetChanged();
                            CalListviewHeight.setListViewHeightBasedOnChildren(listview);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, str, "2030-12-31 00:00");
                timeSelector.show();
            }
        });
        listview.setAdapter(lessonAdapter);
        instance = this;
    }


    @Override
    protected void onResume() {
        if (AppConfig.ISONRESUME) {  //學生
            AppConfig.ISONRESUME = false;
            student = AppConfig.tempServiceBean;
            studentname.setText(student.getCustomer().getName());
        }
        if (AppConfig.ISCOURSE) {  // COURSE
            AppConfig.ISCOURSE = false;
            courseLesson = AppConfig.tempCourse;
            coursename.setText(courseLesson.getTitle());
            coursetempatename.setText(courseLesson.getTitle());
            cti.setVisibility(View.VISIBLE);
            ll2.setVisibility(View.VISIBLE);
            sl.setVisibility(View.VISIBLE);
            listview.setVisibility(View.GONE);
            addlesson.setVisibility(View.VISIBLE);
            // 清除所有的lessons
            list.clear();
            lessonAdapter.notifyDataSetChanged();
            CalListviewHeight.setListViewHeightBasedOnChildren(listview);
        }
        if (AppConfig.ISLECTURE) {  //Lecture
            AppConfig.ISLECTURE = false;
            List<CourseLesson> lectures = mLesson.getLectures();
            lectures.clear();
            lectures.addAll(AppConfig.templectures);
            lessonAdapter.notifyDataSetChanged();
            CalListviewHeight.setListViewHeightBasedOnChildren(listview);
        }
        MobclickAgent.onPageStart("AddServiceFirst");
        MobclickAgent.onResume(this);
        super.onResume();
    }

    private void initView() {
        selectuser = (RelativeLayout) findViewById(R.id.selectuser);
        selectuser.setOnClickListener(this);
        selectcourse = (RelativeLayout) findViewById(R.id.selectcourse);
        selectcourse.setOnClickListener(this);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        cti = (TextView) findViewById(R.id.cti);
        sl = (TextView) findViewById(R.id.sl);
        ll2 = (LinearLayout) findViewById(R.id.ll2);
        listview = (ListView) findViewById(R.id.listview);
        addlesson = (TextView) findViewById(R.id.addlesson);
        onecheck = (CheckBox) findViewById(R.id.onecheck);
        twocheck = (CheckBox) findViewById(R.id.twocheck);
        onecheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    twocheck.setChecked(false);
                }
            }
        });
        twocheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    onecheck.setChecked(false);
                }
            }
        });
        cti.setVisibility(View.GONE);
        ll2.setVisibility(View.GONE);
        sl.setVisibility(View.GONE);
        listview.setVisibility(View.GONE);
        addlesson.setVisibility(View.GONE);
        addlesson.setOnClickListener(this);
        coursetempatename = (TextView) findViewById(R.id.coursetempatename);
        coursename = (TextView) findViewById(R.id.coursename);
        studentname = (TextView) findViewById(R.id.studentname);
        cancel = (TextView) findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        submit = (TextView) findViewById(R.id.submit);
        submit.setOnClickListener(this);
        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        schoolId = sharedPreferences.getInt("SchoolID", -1);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.selectuser:
                Intent intent3 = new Intent(SelectCourseActivity.this,
                        SelectUserActivity.class);
                intent3.putExtra("schoolId", schoolId);
                startActivity(intent3);
                break;
            case R.id.selectcourse:
                if (student == null) {
                    Toast.makeText(SelectCourseActivity.this, getString(R.string.selectstudent), Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(SelectCourseActivity.this,
                        CourseListActivity.class);
                int  lessontype=2;
                if(onecheck.isChecked()){
                    lessontype=0;
                }else if(twocheck.isChecked()){
                    lessontype=1;
                }
                intent.putExtra("lessontype", lessontype);
                intent.putExtra("schoolId", schoolId);
                startActivity(intent);
                break;
            case R.id.addlesson:
                listview.setVisibility(View.VISIBLE);
                Lesson c = new Lesson();
                c.setTitle("LESSON" + (list.size() + 1));
                c.setLessonId(0);
                List<CourseLesson> LL = new ArrayList<>();
                c.setLectures(LL);
                list.add(c);
                lessonAdapter.notifyDataSetChanged();
                CalListviewHeight.setListViewHeightBasedOnChildren(listview);
                Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
                break;
            case R.id.cancel:
                finish();
                overridePendingTransition(R.anim.tran_in7, R.anim.tran_out7);
                break;
            case R.id.submit:
                if (courseLesson == null || courseLesson.getCourseID() <= 0) {
                    Toast.makeText(SelectCourseActivity.this, getString(R.string.selectcoursefirst), Toast.LENGTH_LONG).show();
                } else {
                    submitLesson();
                }
                break;
        }
    }


    private void submitLesson() {
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    final JSONArray array = new JSONArray();
                    for (int i = 0; i < list.size(); i++) {
                        Lesson lesson = list.get(i);
                        List<CourseLesson> lectures = lesson.getLectures();
                        String lecs = "";
                        for (int j = 0; j < lectures.size(); j++) {
                            CourseLesson l = lectures.get(j);
                            if (j == 0) {
                                lecs += l.getLectureID() + "";
                            } else {
                                lecs += "," + l.getLectureID();
                            }
                        }
                        JSONObject js = new JSONObject();
                        js.put("LessonID", lesson.getLessonId());
                        js.put("CourseID", courseLesson.getCourseID());
                        js.put("Title", lesson.getTitle());
                        js.put("Description", lesson.getDescription());
                        js.put("LectureIDs", lecs);
                        js.put("StartDate", lesson.getStartData());
                        js.put("EndDate", lesson.getEndData());
                        js.put("SchoolID", schoolId);
                        js.put("ClassroomID", AppConfig.ClassRoomID.replace("-", ""));

                        JSONObject j = new JSONObject();
                        j.put("MemberID", student.getCustomer().getUserID());
                        j.put("Role", 1);

                        JSONObject j2 = new JSONObject();
                        j2.put("MemberID", AppConfig.UserID);
                        j2.put("Role", 2);
                        JSONArray jsonArray = new JSONArray();
                        jsonArray.put(j);
                        jsonArray.put(j2);

                        js.put("LessonMembers", jsonArray);

                        array.put(js);
                    }
                    JSONObject json = ConnectService.submitDataByJsonArray(AppConfig.URL_PUBLIC + "Lesson/CreateOrUpdateLessons", array);
                    Log.e("CreateOrUpdateLessons", json.toString());
                    int retCode = json.getInt("RetCode");
                    switch (retCode) {
                        case 0:
                            Message msg = Message.obtain();
                            msg.what = 0x1001;
                            handler.sendMessage(msg);
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }).start(((App) getApplication()).getThreadMgr());
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
