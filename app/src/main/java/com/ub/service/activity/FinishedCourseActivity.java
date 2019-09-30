package com.ub.service.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.start.LoginGet;
import com.ub.techexcel.adapter.ServiceAdapter2;
import com.ub.techexcel.adapter.ServiceAdapter3;
import com.ub.techexcel.bean.ServiceBean;
import com.ub.techexcel.tools.ServiceTool;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

/**
 * Created by wang on 2018/1/12.
 */

public class FinishedCourseActivity extends Activity implements View.OnClickListener {

    private LinearLayout backll;
    private ListView inProgressListview;
    private ListView completedListview;
    private View inprogressView, completedView;
    private List<View> viewList = new ArrayList<>();
    private List<List<ServiceBean>> mlist = new ArrayList<>();
    private List<ServiceBean> mList1 = new ArrayList<>(),
            mList2 = new ArrayList<>(),
            mList3 = new ArrayList<>();
    private ServiceAdapter2 completeServiceAdapter;
    private ServiceAdapter2 inprogressServiceAdapter;
    private TextView title;
    private ViewPager mViewPager;
    private TextView inprogressunderline;
    private TextView inprogressTv, completedTv;
    private TextView[] mTitleViews = new TextView[2];
    private int currIndex = 0;
    private int width;
    //灰色以及相对应的RGB值
    private int mGrayColor;
    private int mGrayRed;
    private int mGrayGreen;
    private int mGrayBlue;
    //绿色以及相对应的RGB值
    private int mGreenColor;
    private int mGreenRed;
    private int mGreenGreen;
    private int mGreenBlue;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppConfig.LOAD_FINISH:
                    inprogressServiceAdapter = new ServiceAdapter2(FinishedCourseActivity.this, mList1, true, 0);
                    inProgressListview.setAdapter(inprogressServiceAdapter);

                    completeServiceAdapter = new ServiceAdapter2(FinishedCourseActivity.this, mList3, true, 2);
                    completedListview.setAdapter(completeServiceAdapter);

                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finishcourselist);
        backll = (LinearLayout) findViewById(R.id.backll);
        backll.setOnClickListener(this);

        title = (TextView) findViewById(R.id.title);
        title.setText("Activity");
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        inprogressunderline = (TextView) findViewById(R.id.inprogressunderline);

        inprogressTv = (TextView) findViewById(R.id.inprogressTv);
        completedTv = (TextView) findViewById(R.id.completedTv);
        inprogressTv.setOnClickListener(this);
        completedTv.setOnClickListener(this);
        mTitleViews[0] = inprogressTv;
        mTitleViews[1] = completedTv;

        LayoutInflater layoutInflater = getLayoutInflater();
        inprogressView = layoutInflater.inflate(R.layout.tabone, null);
        completedView = layoutInflater.inflate(R.layout.tabtwo, null);
        inProgressListview = (ListView) inprogressView.findViewById(R.id.serviceList);
        completedListview = (ListView) completedView.findViewById(R.id.serviceList);
        viewList.add(inprogressView);
        viewList.add(completedListview);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        width = screenW / 2;
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) inprogressunderline.getLayoutParams();
        lp.width = width / 2;
        lp.leftMargin = width / 4;
        inprogressunderline.setLayoutParams(lp);
        mViewPager.setAdapter(new ServicePagerAdapter());
        mViewPager.setCurrentItem(currIndex);

        initColor();
        setHeadColor(inprogressTv);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.e("positionOffset", position + "  " + positionOffset);
                float currMarginLeft = width * positionOffset + position * width + width / 4;
                RelativeLayout.LayoutParams redLp = (RelativeLayout.LayoutParams) inprogressunderline.getLayoutParams();
                redLp.leftMargin = (int) currMarginLeft;
                inprogressunderline.setLayoutParams(redLp);
                if (positionOffset > 0) {
                    if (positionOffset < 0.5) {
                        mTitleViews[position].setTextColor(mGreenColor);
                        mTitleViews[position + 1].setTextColor(getGrayToGreen(positionOffset));
                    } else {
                        mTitleViews[position].setTextColor(getGreenToGray(positionOffset));
                        mTitleViews[position + 1].setTextColor(mGreenColor);
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub
                switch (position) {
                    case 0:
                        setHeadColor(inprogressTv);
                        break;
                    case 1:
                        setHeadColor(completedTv);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });

        inProgressListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(FinishedCourseActivity.this,
                        ServiceDetailActivity.class);
                intent.putExtra("id", mList1.get(i).getId());
                startActivity(intent);
            }
        });

        completedListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(FinishedCourseActivity.this,
                        ServiceDetailActivity.class);
                intent.putExtra("id", mList3.get(i).getId());
                startActivity(intent);
            }
        });

        getCustomerList();

    }


    class ServicePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewList.get(position));
            return viewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewList.get(position));
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backll:
                finish();
                break;
            case R.id.inprogressTv:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.completedTv:
                mViewPager.setCurrentItem(1);
                break;
        }
    }


    private ArrayList<Customer> custometList = new ArrayList<>();

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


    //登录账号信息
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
        int schoolId = sharedPreferences.getInt("SchoolID", -1);

        List<ServiceBean> list0 = new ArrayList<>();
        List<ServiceBean> list1 = new ArrayList<>();
        List<ServiceBean> list2 = new ArrayList<>();

        pjList.clear();
        pjList.add(list0); //即将开始
        pjList.add(list1); //已过期
        pjList.add(list2);  //已结束

        ExecutorService executorService = Executors.newFixedThreadPool(pjList.size());
        for (int i = 0; i < pjList.size(); i++) {
            executorService.execute(new ServiceTool(i, pjList.get(i),""));
        }
        executorService.shutdown();
        while (true) {
            if (executorService.isTerminated()) {

                List<ServiceBean> serviceBeanList = sortBydata(list0);
                for (ServiceBean bean : serviceBeanList) {
                    mList1.add(bean);
                }

                List<ServiceBean> serviceBeanList1 = sortBydata(list1);
                for (ServiceBean bean : serviceBeanList1) {
                    mList2.add(bean);
                }

                List<ServiceBean> serviceBeanList2 = sortBydata(list2);
                for (ServiceBean bean : serviceBeanList2) {
                    mList3.add(bean);
                }

                Collections.reverse(mList1);
                Collections.reverse(mList2);
                Collections.reverse(mList3);

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


    private void initColor() {
        mGrayColor = getResources().getColor(R.color.c5);
        mGrayRed = Color.red(mGrayColor);
        mGrayGreen = Color.green(mGrayColor);
        mGrayBlue = Color.blue(mGrayColor);
        mGreenColor = getResources().getColor(R.color.c1);
        mGreenRed = Color.red(mGreenColor);
        mGreenGreen = Color.green(mGreenColor);
        mGreenBlue = Color.blue(mGreenColor);
    }

    private void setHeadColor(TextView textView) {
        textView.setTextColor(mGreenColor);
    }

    /**
     * 偏移量在 0——0.5区间 ，左边一项颜色不变，右边一项颜色从灰色变为绿色，根据两点式算出RGB变化函数，组合出颜色
     *
     * @param positionOffset
     * @return
     */
    private int getGrayToGreen(float positionOffset) {
        int red = (int) (positionOffset * (mGreenRed - mGrayRed) * 2 + mGrayRed);
//           0.5*(red-mGrayRed) =positionOffset * (mGreenRed - mGrayRed)
        int green = (int) (positionOffset * (mGreenGreen - mGrayGreen) * 2 + mGrayGreen);
        int blue = (int) ((positionOffset) * (mGreenBlue - mGrayBlue) * 2 + mGrayBlue);
        Log.d("why ", "#### " + red + "  " + green + "  " + blue);
        return Color.argb(255, red, green, blue);
    }

    /**
     * 偏移量在 0.5--1 区间，颜色从绿色变成灰色，根据两点式算出变化RGB随偏移量变化函数，组合出颜色
     *
     * @param positionOffset
     * @return
     */
    private int getGreenToGray(float positionOffset) {
        int red = (int) (positionOffset * (mGrayRed - mGreenRed) * 2 + 2 * mGreenRed - mGrayRed);
        int green = (int) (positionOffset * (mGrayGreen - mGreenGreen) * 2 + 2 * mGreenGreen - mGrayGreen);
        int blue = (int) (positionOffset * (mGrayBlue - mGreenBlue) * 2 + 2 * mGreenBlue - mGrayBlue);
        Log.d("why ", "#### " + red + "  " + green + "  " + blue);
        return Color.argb(255, red, green, blue);
    }

    @Override
    protected void onDestroy() {
        handler.removeMessages(AppConfig.LOAD_FINISH);
        super.onDestroy();
    }

}
