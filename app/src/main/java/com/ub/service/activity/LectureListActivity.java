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
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.service.ConnectService;
import com.ub.techexcel.adapter.LectureListAdapter;
import com.ub.techexcel.bean.CourseLesson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 2018/2/8.
 */

public class LectureListActivity extends Activity implements View.OnClickListener {
    private LinearLayout backll;
    private List<CourseLesson> lectures = new ArrayList<CourseLesson>();
    private ListView listView;
    private LectureListAdapter courseAdapter;
    private int courseID;

    private List<CourseLesson> mLectures = new ArrayList<>();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x1102:

                    for(int i=0;i<lectures.size();i++){
                        for(int j=0;j<mLectures.size();j++){
                            if(lectures.get(i).getLectureID()==mLectures.get(j).getLectureID()){
                                lectures.get(i).setSelect(true);
                                break;
                            }
                        }
                    }

                    courseAdapter = new LectureListAdapter(LectureListActivity.this, lectures);
                    listView.setAdapter(courseAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            CourseLesson c=lectures.get(i);
                            if(c.isSelect()){
                                c.setSelect(false);
                            }else{
                                c.setSelect(true);
                            }
                            courseAdapter.notifyDataSetChanged();
                        }

                    });
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lecturelist);
        initView();
        courseID = getIntent().getIntExtra("courseLessonid", 0);
        mLectures = (List<CourseLesson>) getIntent().getSerializableExtra("mLectures");
        getLectureList(courseID);
    }

    private void initView() {

        backll = (LinearLayout) findViewById(R.id.backll);
        backll.setOnClickListener(this);
        listView = (ListView) findViewById(R.id.lectureList);

    }


    private void getLectureList(final int courseLessonid) {
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = ConnectService.getIncidentbyHttpGet(AppConfig.URL_PUBLIC + "Lecture/List?courseID=" + courseLessonid);
                    Log.e("---------LectureList", jsonObject.toString());
                    int retcode = jsonObject.getInt("RetCode");
                    switch (retcode) {
                        case 0:
                            JSONArray array = jsonObject.getJSONArray("RetData");
                            lectures.clear();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject js = array.getJSONObject(i);
                                CourseLesson courseLesson = new CourseLesson();
                                courseLesson.setCourseID(js.getInt("CourseID"));
                                courseLesson.setLectureID(js.getInt("LectureID"));
                                courseLesson.setTitle(js.getString("Title"));
                                lectures.add(courseLesson);
                            }
                            Message msg = Message.obtain();
                            msg.obj = lectures;
                            msg.what = 0x1102;
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backll:
                AppConfig.ISLECTURE = true;
                AppConfig.templectures.clear();
                for(int i=0;i<lectures.size();i++){
                    if(lectures.get(i).isSelect()){
                        AppConfig.templectures.add(lectures.get(i));
                    }
                }
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

            AppConfig.ISLECTURE = true;
            AppConfig.templectures.clear();
            for(int i=0;i<lectures.size();i++){
                if(lectures.get(i).isSelect()){
                    AppConfig.templectures.add(lectures.get(i));
                }
            }
            finish();
            overridePendingTransition(R.anim.tran_in7, R.anim.tran_out7);
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }
}
