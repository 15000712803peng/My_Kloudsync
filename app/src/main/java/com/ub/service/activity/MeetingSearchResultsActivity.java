package com.ub.service.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.ub.techexcel.adapter.ServiceAdapter2;
import com.ub.techexcel.bean.ServiceBean;
import com.ub.techexcel.tools.MeetingMoreOperationPopup;
import com.ub.techexcel.tools.ServiceTool;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MeetingSearchResultsActivity extends Activity implements View.OnClickListener,TextWatcher {

    EditText et_search;
    ImageView img_clear_edit;
    TextView tv_cancel;
    ListView  listView;
    private TextView statustxt;

    private List<List<ServiceBean>> mlist = new ArrayList<>();  //所有课程的list集合

    private List<ServiceBean> mList1 = new ArrayList<>();

    private int type=0;   //  0,  1, 2

    private ServiceAdapter2 serviceAdapter;


    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case AppConfig.LOAD_FINISH:
                    serviceAdapter = new ServiceAdapter2(MeetingSearchResultsActivity.this, mList1, true, type);

                    serviceAdapter.setFromSearch(true, keyword);

                    listView.setAdapter(serviceAdapter);

                    serviceAdapter.setOnModifyServiceListener(new ServiceAdapter2.OnModifyServiceListener() {
                        @Override
                        public void select(final ServiceBean bean) {
                            MeetingMoreOperationPopup meetingMoreOperationPopup = new MeetingMoreOperationPopup();
                            meetingMoreOperationPopup.getPopwindow(MeetingSearchResultsActivity.this);
                            meetingMoreOperationPopup.setFavoritePoPListener(new MeetingMoreOperationPopup.FavoritePoPListener() {
                                @Override
                                public void delete() {

                                }

                                @Override
                                public void view() {

                                }

                                @Override
                                public void edit() {

                                }

                                @Override
                                public void startMeeting() {
                                    Intent intent = new Intent(MeetingSearchResultsActivity.this, WatchCourseActivity2.class);
                                    intent.putExtra("userid", bean.getUserId());
                                    intent.putExtra("meetingId", bean.getId() + "");
                                    intent.putExtra("filemeetingId", bean.getId() + "");
                                    intent.putExtra("teacherid", bean.getTeacherId());
                                    intent.putExtra("identity", bean.getRoleinlesson());
                                    intent.putExtra("isInstantMeeting", 0);
                                    intent.putExtra("isStartCourse", true);
                                    startActivity(intent);
                                }

                                @Override
                                public void dismiss() {
                                }

                                @Override
                                public void open() {
                                }

                            });
                            meetingMoreOperationPopup.StartPop(listView, bean, type);
                        }
                    });
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetingsearchresults);

        type=getIntent().getIntExtra("type",0);
        initView();

    }

    private void initView() {

        et_search = findViewById(R.id.et_search);
        img_clear_edit = findViewById(R.id.img_clear_edit);
        tv_cancel = findViewById(R.id.tv_cancel);
        listView = findViewById(R.id.list_doc);
        tv_cancel.setOnClickListener(this);
        et_search.addTextChangedListener(this);
        statustxt=findViewById(R.id.statustxt);
        switch (type){
            case 0:
                statustxt.setText(getString(R.string.upcoming));
                break;
            case 1:
                statustxt.setText(getString(R.string.pastdue));
                break;
            case 2:
                statustxt.setText(getString(R.string.pastfinish));
                break;
        }


    }


    /**
     * 获得所有原始数据
     */
    private void getAllServiceData(String keyword) {
        SharedPreferences sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                Context.MODE_PRIVATE);
        int schoolId = sharedPreferences.getInt("SchoolID", -1);

        List<ServiceBean> list = new ArrayList<>();
        mList1.clear();

        ExecutorService executorService = Executors.newFixedThreadPool(1);

        executorService.execute(new ServiceTool(type, list,keyword));

        executorService.shutdown();
        while (true) {
            if (executorService.isTerminated()) {
                List<ServiceBean> serviceBeanList = sortBydata(list);
                for (ServiceBean bean : serviceBeanList) {
                    mList1.add(bean);
                }
                Collections.reverse(mList1);
                handler.sendEmptyMessage(AppConfig.LOAD_FINISH);
                break;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private List<ServiceBean> sortBydata(List<ServiceBean> serviceBeanList) {
        Collections.sort(serviceBeanList, new Comparator<ServiceBean>() {
            @Override
            public int compare(ServiceBean s1, ServiceBean s2) {
                String x1 = s1.getPlanedStartDate();
                String x2 = s2.getPlanedStartDate();
                if (TextUtils.isEmpty(x1)) {
                    x1 = "0";
                }
                if (TextUtils.isEmpty(x2)) {
                    x2 = "0";
                }
                if (Long.parseLong(x1) > Long.parseLong(x2)) {
                    return 1;
                }
                if (Long.parseLong(x1) == Long.parseLong(x2)) {
                    return 0;
                }
                return -1;
            }
        });
        for (ServiceBean bean : serviceBeanList) {
            String planedsatrtdate = bean.getPlanedStartDate();
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
                    bean.setMins((int) (xx / 1000 / 60));
                } else if (xx >= diff && xx < 86400000 * 2) {
                    bean.setDateType(2); //明天的
                } else if (xx >= 86400000 * 2) {
                    bean.setDateType(3);//后天及以后
                }
            }
        }
        return serviceBeanList;

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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                hideInput();
                finish();
                break;
        }
    }

    @Override
    protected void onStop() {
        hideInput();
        super.onStop();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }


    private String keyword;
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        keyword=charSequence.toString();

        getAllServiceData(keyword);

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    private void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && getCurrentFocus() != null) {
            if (getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }


}
