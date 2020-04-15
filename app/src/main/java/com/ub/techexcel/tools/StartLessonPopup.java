package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.service.ConnectService;
import com.ub.techexcel.bean.CourseLesson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 2017/9/18.
 */

public class StartLessonPopup {

    public Context mContext;
    public int width;
    public int height;
    public PopupWindow mPopupWindow;
    private View view;
    private LinearLayout layout;
    private TextView textview;
    private ListView listview;
    private List<CourseLesson> courseLessons = new ArrayList<>();
//    private CourseAdapter courseAdapter;
    private boolean isLecture = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x1102:
                    textview.setText("Lecture List");
                    isLecture = true;
//                    courseAdapter = new CourseAdapter(mContext, (List<CourseLesson>) msg.obj);
//                    listview.setAdapter(courseAdapter);
                    break;
            }
        }
    };

    public void getPopwindow(Context context, LinearLayout layout, List<CourseLesson> courseLessons) {
        this.mContext = context;
        this.layout = layout;
        this.courseLessons = courseLessons;

        width = mContext.getResources().getDisplayMetrics().widthPixels;
        height = mContext.getResources().getDisplayMetrics().heightPixels;
        getPopupWindowInstance();
    }

    public void getPopupWindowInstance() {
        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }

    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.courselist_popup, null);

        textview = (TextView) view.findViewById(R.id.textview);
        listview = (ListView) view.findViewById(R.id.listview);

        textview.setText("Course List");
        isLecture = false;
//        courseAdapter = new CourseAdapter(mContext, courseLessons);
//        listview.setAdapter(courseAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (isLecture) {
                    webCamPopupListener.onItem(lectures.get(i));
                } else {
                    getLectureList(courseLessons.get(i));
                }

            }
        });

        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                webCamPopupListener.dismiss();
            }
        });

        mPopupWindow.setFocusable(true);//这里必须设置为true才能点击区域外或者消失
        mPopupWindow.setTouchable(true);//这个控制PopupWindow内部控件的点击事件
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.update();

    }

    List<CourseLesson> lectures = new ArrayList<CourseLesson>();

    private void getLectureList(final CourseLesson courseLesson) {
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = ConnectService.getIncidentbyHttpGet(AppConfig.URL_PUBLIC + "Lecture/List?courseID=" + courseLesson.getCourseID());
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
        }).start(ThreadManager.getManager());

    }

    public interface StartLessonPopupListener {
        void dismiss();

        void open();

        void onItem(CourseLesson lesson);
    }

    public void setStartLessonPopupListener(StartLessonPopupListener webCamPopupListener) {
        this.webCamPopupListener = webCamPopupListener;
    }

    private StartLessonPopupListener webCamPopupListener;


    @SuppressLint("NewApi")
    public void StartPop(View v) {
        if (mPopupWindow != null) {
            mPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
            webCamPopupListener.open();
        }
    }

    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }


}
