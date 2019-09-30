package com.ub.service.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ub.techexcel.adapter.ServiceAdapter2;
import com.ub.techexcel.bean.ServiceBean;
import com.ub.techexcel.tools.ServiceTool;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.start.LoginGet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wang on 2018/1/12.
 */

public class MyCourseActivity extends Activity implements View.OnClickListener {

    private LinearLayout backll;
    private ListView listview;
    private List<List<ServiceBean>> mlist = new ArrayList<>();
    private List<ServiceBean> mList1 = new ArrayList<>(),
            mList2 = new ArrayList<>(),
            myList = new ArrayList<>();
    private ServiceAdapter2 serviceAdapter3;
    private TextView title;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppConfig.LOAD_FINISH:
                    myList = sortBydata(myList);
                    serviceAdapter3 = new ServiceAdapter2(MyCourseActivity.this, myList, true,2);
                    listview.setAdapter(serviceAdapter3);

                    break;
                default:
                    break;
            }
        }
    };

    private List<ServiceBean> sortBydata(List<ServiceBean> mList1) {

        Collections.sort(mList1, new Comparator<ServiceBean>() {
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
        for (ServiceBean bean : mList1) {
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
        List<ServiceBean> list = new ArrayList<>();

        for (ServiceBean bean : mList1) {
            if (bean.getDateType() == 1) {
                list.add(bean);
            }
        }

        for (ServiceBean bean : mList1) {
            if (bean.getDateType() == 2) {
                list.add(bean);
            }
        }

        for (ServiceBean bean : mList1) {
            if (bean.getDateType() == 3) {
                list.add(bean);
            }
        }

        for (ServiceBean bean : mList1) {
            if (bean.getDateType() == 4) {
                list.add(bean);
            }
        }

        for (ServiceBean bean : list) {
            if (bean.getDateType() == 1) {
                bean.setShow(true);
                break;
            }
        }
        for (ServiceBean bean : list) {
            if (bean.getDateType() == 2) {
                bean.setShow(true);
                break;
            }
        }
        for (ServiceBean bean : list) {
            if (bean.getDateType() == 3) {
                bean.setShow(true);
                break;
            }
        }
        for (ServiceBean bean : list) {
            if (bean.getDateType() == 4) {
                bean.setShow(true);
                break;
            }
        }
        return list;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mycourselist);
        backll = (LinearLayout) findViewById(R.id.backll);
        backll.setOnClickListener(this);
        listview = (ListView) findViewById(R.id.serviceList);
        title = (TextView) findViewById(R.id.title);
        title.setText(getResources().getString(R.string.mycourse));

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MyCourseActivity.this,
                        ServiceDetailActivity.class);
                intent.putExtra("id", myList.get(i).getId());
                startActivity(intent);
            }
        });

        getCustomerList();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backll:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        handler.removeMessages(AppConfig.LOAD_FINISH);
        super.onDestroy();
    }

    private ArrayList<Customer> custometList = new ArrayList<Customer>();

    private void getCustomerList() {
        final LoginGet loginGet = new LoginGet();
        loginGet.setLoginGetListener(new LoginGet.LoginGetListener() {
            @Override
            public void getMember(ArrayList<Customer> list) {

            }

            @Override
            public void getCustomer(ArrayList<Customer> list) {
                custometList = list;
                getLoginUser();
            }
        });
        loginGet.CustomerRequest(this);
    }


    private void getLoginUser() {
        final LoginGet loginget = new LoginGet();
        loginget.setDetailGetListener(new LoginGet.DetailGetListener() {

            @Override
            public void getUser(Customer CustomerDetailRequest) {
                custometList.add(CustomerDetailRequest);
                getAllServiceData(mlist);
            }

            @Override
            public void getMember(Customer member) {

            }
        });
        loginget.CustomerDetailRequest(this, AppConfig.UserID);

    }

    /**
     * 获得所有原始数据
     *
     * @param pjList
     */
    private void getAllServiceData(List<List<ServiceBean>> pjList) {
        SharedPreferences sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        int  schoolId= sharedPreferences.getInt("SchoolID", -1);
        List<ServiceBean> list0 = new ArrayList<>();
        List<ServiceBean> list1 = new ArrayList<>();
        List<ServiceBean> list2 = new ArrayList<>();
        List<ServiceBean> list3 = new ArrayList<>();
        List<ServiceBean> list4 = new ArrayList<>();
        mList1.clear();
        mList2.clear();
        myList.clear();

        pjList.clear();

        pjList.add(list0);
        pjList.add(list1);
        pjList.add(list2);
        pjList.add(list3);
        pjList.add(list4);

        ExecutorService executorService = Executors.newFixedThreadPool(pjList
                .size());
        for (int i = 0; i < pjList.size(); i++) {
            executorService.execute(new ServiceTool(i, pjList.get(i),""));
        }
        executorService.shutdown();
        while (true) {
            if (executorService.isTerminated()) {
                Log.e("大小", list4.size() + "  ");
                for (int i = 0; i < list0.size(); i++) {
                    ServiceBean s = list0.get(i);
                    if (s.getStatusID() == 322) {
                        mList1.add(s);
                    }
                }
                for (int i = 0; i < list1.size(); i++) {
                    ServiceBean s = list1.get(i);
                    if (s.getStatusID() == 322) {
                        mList1.add(s);
                    }
                }
                for (int i = 0; i < list2.size(); i++) {
                    ServiceBean s = list2.get(i);
                    if (s.getStatusID() == 1) {
                        mList2.add(s);
                    }
                }
                for (int i = 0; i < list3.size(); i++) {
                    ServiceBean s = list3.get(i);
                    if (s.getStatusID() == 1) {
                        mList2.add(s);
                    }
                }
                for (int i = 0; i < list4.size(); i++) {
                    ServiceBean s = list4.get(i);
                    myList.add(s);
                }
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


}
