package com.ub.service.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.service.ConnectService;
import com.ub.techexcel.adapter.CourseAdapter;
import com.ub.techexcel.bean.CourseLesson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 2018/2/8.
 */

public class CourseListActivity extends Activity implements View.OnClickListener {
    private LinearLayout backll;
    private List<CourseLesson> courseLessons = new ArrayList<>();
    private ListView listView;
    private CourseAdapter courseAdapter;
    private int schoolId;
    private boolean isMyschool;
    private int lessontype;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case AppConfig.LOAD_FINISH:
//                    courseAdapter = new CourseAdapter(CourseListActivity.this, courseLessons);
                    listView.setAdapter(courseAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            AppConfig.ISCOURSE = true;
                            AppConfig.tempCourse = courseLessons.get(i);
                            finish();
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_courselist);
        initView();
        getCourselist();
    }

    private void initView() {
        backll = (LinearLayout) findViewById(R.id.backll);
        backll.setOnClickListener(this);
        listView = (ListView) findViewById(R.id.courseList);
        lessontype = getIntent().getIntExtra("lessontype", 2);
        schoolId = getIntent().getIntExtra("schoolId", -1);
        if (AppConfig.SchoolID == schoolId || schoolId == -1) {  // 是my school
            isMyschool = true;
        } else {
            isMyschool = false;
        }
    }


    private void getCourselist() {
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject;
                    if (isMyschool) {
                        jsonObject = ConnectService.getIncidentbyHttpGet(AppConfig.URL_PUBLIC + "Course/List?listType=1&pageIndex=-1&type=-1&TeacherID=" + AppConfig.UserID);
                    } else {
                        jsonObject = ConnectService.getIncidentbyHttpGet(AppConfig.URL_PUBLIC + "Course/List?listType=1&pageIndex=-1&schoolID=" + schoolId + "&type=" + lessontype);
                    }
                    Log.e("---------Courselist", isMyschool + "  " + jsonObject.toString());
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
                            handler.sendEmptyMessage(AppConfig.LOAD_FINISH);
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

            default:
                break;
        }

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
