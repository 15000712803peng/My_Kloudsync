package com.kloudsync.techexcel.personal;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ub.techexcel.bean.TempletedCourse;
import com.ub.techexcel.tools.TempleteCourse_interface;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.TemplateAdapter;
import com.kloudsync.techexcel.config.AppConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MyCourseTemplateActivity extends Activity {

    private TextView tv_back;
    private RecyclerView rv_template;
    private TemplateAdapter tadapter;
    private List<TempletedCourse> templetedCourseList = new ArrayList<>();
    private TextView myCourseTv;
    private TextView systemCourseTv;
    private Retrofit retrofit;
    private TempleteCourse_interface request;
    private CheckBox mCheckBox;
    private SharedPreferences sharedPreferences;
    private int schoolId;
    private boolean isMySchool;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coursetemplate);
        initView();
        GetData(CourseTemplateType.ALL);
    }

    private void GetData(CourseTemplateType type) {
     /*   (@Header("UserToken") String userToken, @Query("listType") int listType,
        @Query("type") int type,
        @Query("templateType") int templateType,
        @Query("SchoolID") int SchoolID,
        @Query("TeacherID") int TeacherID,
        @Query("sortBy") int sortBy,
        @Query("order") int order,
        @Query("pageIndex") int pageIndex,
        @Query("pageSize") int pageSize*/

        Call<ResponseBody> call;
        if (isMySchool) { // 传TeacherID 不传SchoolID
            call = request.getCourseByTemplete(AppConfig.UserToken, CourseListType.TEMPLATE.VALUE, -1,
                    type.VALUE, Integer.parseInt(AppConfig.UserID), CourseSortBy.COURSEID.VALUE, 1, -1, 10);
        } else {
            call = request.getCourseByTemplete2(AppConfig.UserToken, CourseListType.TEMPLATE.VALUE, -1,
                    type.VALUE, schoolId, CourseSortBy.COURSEID.VALUE, 1, -1, 10);
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        String responsedate = response.body().string();
                        Log.e("callback", responsedate);
                        format(responsedate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });


        //rxjava
      /*  Observable<ResponseBody> observable = request.getCourseByRxJava(AppConfig.UserToken, CourseListType.TEMPLATE.VALUE, -1,
                type.VALUE, 3249, Integer.parseInt(AppConfig.UserID), CourseSortBy.COURSEID.VALUE, 1, -1, 10);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody value) {
                        try {
                            Log.e("rxjavacallback", value.string() + "");
                           //format(value.string());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
*/

    }

    private void format(String responsedate) {
        try {
            JSONObject jsonObject = new JSONObject(responsedate);
            JSONArray jsonArray = jsonObject.getJSONArray("RetData");
            templetedCourseList.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                TempletedCourse templetedCourse = new TempletedCourse();
                templetedCourse.setAuthorCost(jsonObject1.getInt("AuthorCost"));
                templetedCourse.setAuthorLessonCost(jsonObject1.getInt("AuthorLessonCost"));
                templetedCourse.setTitle(jsonObject1.getString("Title"));
//                templetedCourse.setTeacherName(jsonObject1.getString("TeacherName"));
                templetedCourse.setAuthorLessonCount(jsonObject1.getInt("AuthorLessonCount"));
                templetedCourseList.add(templetedCourse);
            }
            tadapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initView() {
        tv_back = (TextView) findViewById(R.id.tv_back);
        systemCourseTv = (TextView) findViewById(R.id.systemCourseTv);
        myCourseTv = (TextView) findViewById(R.id.mycourseTv);
        systemCourseTv.setOnClickListener(new MyOnClick());
        myCourseTv.setOnClickListener(new MyOnClick());
        rv_template = (RecyclerView) findViewById(R.id.rv_template);
        tv_back.setOnClickListener(new MyOnClick());
        mCheckBox = (CheckBox) findViewById(R.id.checkbox);
        rv_template.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(AppConfig.URL_PUBLIC)
                .build();
        request = retrofit.create(TempleteCourse_interface.class);
        tadapter = new TemplateAdapter(templetedCourseList);
        rv_template.setAdapter(tadapter);

        sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        schoolId = sharedPreferences.getInt("SchoolID", -1);
        if (schoolId == AppConfig.SchoolID) {
            isMySchool = true;
        } else {
            isMySchool = false;
        }

    }

    protected class MyOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_back:
                    finish();
                    break;
                case R.id.mycourseTv:
                    GetData(CourseTemplateType.ALL);
                    myCourseTv.setTextColor(getResources().getColor(R.color.skyblue));
                    systemCourseTv.setTextColor(getResources().getColor(R.color.black));
                    break;
                case R.id.systemCourseTv:
                    GetData(CourseTemplateType.SYSTEM);
                    myCourseTv.setTextColor(getResources().getColor(R.color.black));
                    systemCourseTv.setTextColor(getResources().getColor(R.color.skyblue));
                    break;
                default:
                    break;
            }
        }
    }


    public enum CourseTemplateType {
        ALL(0),
        SYSTEM(1),
        PERSONAL(2);
        private int VALUE;

        CourseTemplateType(int value) {
            this.VALUE = value;
        }
    }

    public enum CourseListType {
        TEMPLATE(1),
        ACTIVE(2),
        FINISHED(3);
        private int VALUE;
        CourseListType(int value) {
            this.VALUE = value;
        }
    }

    public enum CourseSortBy {
        COURSEID(0),
        COURSENAME(1),
        TYPE(2),
        STARTDATE(3),
        TEACHER(4),
        STATUS(5),
        COST(6);
        private int VALUE;
        CourseSortBy(int value) {
            this.VALUE = value;
        }
    }
}
