package com.kloudsync.techexcel.frgment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.Course;
import com.kloudsync.techexcel.bean.EventStartMeeting;
import com.kloudsync.techexcel.bean.LessionInCourse;
import com.kloudsync.techexcel.bean.MeetingType;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.PopShareMeeting;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.ui.DocAndMeetingActivity;
import com.kloudsync.techexcel.ui.LessionActivity;
import com.kloudsync.techexcel.ui.MeetingViewActivity;
import com.ub.service.activity.MeetingPropertyActivity;
import com.ub.techexcel.adapter.CourseAdapter;
import com.ub.techexcel.adapter.ServiceAdapter2;
import com.ub.techexcel.bean.ServiceBean;
import com.ub.techexcel.service.ConnectService;
import com.ub.techexcel.tools.CourseLessionMoreOperationPopup;
import com.ub.techexcel.tools.MeetingMoreOperationPopup;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by tonyan on 2019/11/9.
 */

public class CourseListFragment extends MyFragment implements CourseLessionMoreOperationPopup.LessionOptionsListener {

    private ListView meetingList;
    int type;
    String keyWord;
    private List<Course> mList = new ArrayList<>();
    LinearLayout noMeetingLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getInt("type");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void lazyLoad() {
        Log.e("MeetingFragment", "lazyLoad");
//        new ApiTask(new Runnable() {
//            @Override
//            public void run() {
//                requestMeeting();
//            }
//        }).start(ThreadManager.getManager());
    }

    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_course_list, container, false);
            noMeetingLayout = view.findViewById(R.id.layout_no_meeting);
            meetingList = view.findViewById(R.id.list_meeting);
        }

        doLoad();

        return view;
    }

    private void doLoad() {
        Observable.just(type).observeOn(Schedulers.io()).map(new Function<Integer, List<Course>>() {
            @Override
            public List<Course> apply(Integer integer) throws Exception {
                return requestCourses();
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<Course>>() {
            @Override
            public void accept(List<Course> serviceBeans) throws Exception {
                loadCourses(serviceBeans);
            }
        });

    }

    // https://api.peertime.cn/peertime/V1/RecurringMeeting/GetMeetingList?companyId=4924&listType=0&pageIndex=0&pageSize=10&teamID=1983459

    private List<Course> requestCourses() {

        String url = "";
        if (keyWord == null || keyWord.equals("")) {
            url = AppConfig.URL_PUBLIC
                    + "RecurringMeeting/GetMeetingList?companyId=" + AppConfig.SchoolID + "&pageIndex=0&pageSize=200&listType=" + type;
        } else {
            try {
                url = AppConfig.URL_PUBLIC
                        + "RecurringMeeting/GetMeetingList?companyId= +" + AppConfig.SchoolID + "&pageIndex=0&pageSize=200&listType=" + type + "&keyword=" + URLEncoder.encode(LoginGet.getBase64Password(keyWord), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        JSONObject returnJson = ConnectService
                .getIncidentbyHttpGet(url);
        Log.e("RecurringMeeting/GetMeetingList", url + "  " + returnJson.toString());
        return parseData(returnJson);
    }

    private List<Course> parseData(JSONObject returnJson) {
        Gson gson = new Gson();
        try {
            int retCode = returnJson.getInt("RetCode");
            switch (retCode) {
                case AppConfig.RETCODE_SUCCESS:
                    JSONArray retdata = returnJson.getJSONArray("RetData");
                    mList.clear();
                    for (int i = 0; i < retdata.length(); i++) {
                        JSONObject service = retdata.getJSONObject(i);
                        Course bean = gson.fromJson(service.toString(), Course.class);
                        mList.add(bean);
                    }
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return mList;

    }

    public void loadCourses(List<Course> courses) {
        if (courses == null || courses.size() == 0) {
            noMeetingLayout.setVisibility(View.VISIBLE);
            meetingList.setVisibility(View.GONE);
        } else {
            noMeetingLayout.setVisibility(View.GONE);
            meetingList.setVisibility(View.VISIBLE);
            courses = sortBydata(courses);
            courseAdapter = new CourseAdapter(getActivity(), courses, true, 0);
            courseAdapter.setType(type);
            courseAdapter.setLessionOptionsListener(this);
            meetingList.setAdapter(courseAdapter);

        }
    }


    private void shareDocumentDialog(final ServiceBean document) {
        final PopShareMeeting psk = new PopShareMeeting();
        psk.getPopwindow(getActivity(), document);
        psk.startPop();
    }

    CourseAdapter courseAdapter;

    private List<Course> sortBydata(List<Course> serviceBeanList) {
        Collections.sort(serviceBeanList, new Comparator<Course>() {
            @Override
            public int compare(Course s1, Course s2) {
                String x1 = s1.getStartDate();
                String x2 = s2.getStartDate();
                if (TextUtils.isEmpty(x1)) {
                    x1 = "0";
                }
                if (TextUtils.isEmpty(x2)) {
                    x2 = "0";
                }
                if (Long.parseLong(x1) > Long.parseLong(x2)) {
                    return -1;
                } else if (Long.parseLong(x1) == Long.parseLong(x2)) {
                    return 0;
                }
                return 1;
            }
        });

        for (Course bean : serviceBeanList) {
            String planedsatrtdate = bean.getStartDate();
            if (TextUtils.isEmpty(planedsatrtdate)) {
                bean.setDateType(4);
            } else {
                long today = System.currentTimeMillis();
                long planed = Long.parseLong(planedsatrtdate);
                long diff = diffTime();
                long xx = planed - today;
                if (xx < 0) {
                    bean.setDateType(4);//今天之前的  已结束的
                } else if (xx >= 0 && xx < diff) {
                    bean.setDateType(1); //今天的
//                    bean.setMins((int) (xx / 1000 / 60));
                } else if (xx >= diff && xx < 86400000 * 2) {
                    bean.setDateType(2); //明天的
                } else if (xx >= 86400000 * 2) {
                    bean.setDateType(3);//后天及以后
                }
            }
        }
        return serviceBeanList;

    }

    private Dialog dialog;

    private void deleteMeeting(final Course bean) {
        final LayoutInflater inflater = LayoutInflater
                .from(getActivity());
        View windov = inflater.inflate(R.layout.confirmservice, null);
        windov.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        windov.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                new ApiTask(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = com.kloudsync.techexcel.service.ConnectService.getIncidentDataattachment(AppConfig.URL_PUBLIC + "Lesson/Delete?lessonID=" + bean.getMeetingID());
                        try {
                            int retcode = jsonObject.getInt("RetCode");
                            if (retcode == 0) {
                                doLoad();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start(ThreadManager.getManager());

            }
        });
//        dialog = new AlertDialog.Builder(getActivity()).show();
//        Window dialogWindow = dialog.getWindow();
//        WindowManager m = getActivity().getWindowManager();
//        Display d = m.getDefaultDisplay();
//        WindowManager.LayoutParams p = dialogWindow.getAttributes();
//        p.width = (int) (d.getWidth() * 0.8);
//        dialogWindow.setAttributes(p);
//        dialog.setCanceledOnTouchOutside(true);
//        dialog.setContentView(windov);


        dialog = new Dialog(getActivity(), R.style.bottom_dialog);

        dialog.setContentView(windov);
        dialog.getWindow().setWindowAnimations(R.style.dialogwindowAnim);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = getActivity().getResources().getDisplayMetrics().widthPixels;
        dialog.getWindow().setAttributes(params);
        dialog.show();
    }


    private long diffTime() {
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long diff = cal.getTimeInMillis() - System.currentTimeMillis();
        return diff;
    }

    @Override
    public void delete(LessionInCourse lession) {

    }

    @Override
    public void edit(LessionInCourse lession) {

    }

    @Override
    public void open(final LessionInCourse lession) {
        Observable.just(lession).observeOn(Schedulers.io()).map(new Function<LessionInCourse, JSONObject>() {
            @Override
            public JSONObject apply(LessionInCourse lessionInCourse) throws Exception {
                return ServiceInterfaceTools.getinstance().syncGetCourseRole(lession.getMeetingID()+"");
            }
        }).doOnNext(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject jsonObject) throws Exception {
                if(jsonObject.has("RetCode")){
                    if(jsonObject.getInt("RetCode") == 0){
                        if(jsonObject.has("RetData")){
                            JSONObject retData = jsonObject.getJSONObject("RetData");
                            if(retData != null && retData.has("RoleInLesson")){
                                int role = retData.getInt("RoleInLesson");
                                startLession(lession,role);
                            }
                        }
                    }
                }
            }
        }).subscribe();
    }

    public void startLession(LessionInCourse lessionInCourse,int role) {
        Intent intent = new Intent(getActivity(), LessionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //-----
        intent.putExtra("meeting_id", AppConfig.ClassRoomID);
        intent.putExtra("meeting_type", MeetingType.DOC);
        intent.putExtra("lession_id", lessionInCourse.getMeetingID());
        intent.putExtra("is_host", true);
        intent.putExtra("from_meeting", true);
        intent.putExtra("lession_role",role);
        startActivity(intent);
    }

    /**
     * 开始会议接口回调
     */
    public interface OnStartMeetingCallBackListener {
        void startMeetingCallBack();
    }

    OnStartMeetingCallBackListener onStartMeetingCallBackListener;

    public void setOnStartMeetingCallBackListener(OnStartMeetingCallBackListener onStartMeetingCallBackListener) {
        this.onStartMeetingCallBackListener = onStartMeetingCallBackListener;
    }

}
