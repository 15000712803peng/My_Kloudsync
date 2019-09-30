package com.ub.service.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.contact.UserDetail;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.info.CommonUse;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.start.LoginGet.DetailGetListener;
import com.kloudsync.techexcel.start.LoginGet.DialogGetListener;
import com.ub.techexcel.adapter.ServiceAdapter;
import com.ub.techexcel.adapter.ServiceAdapter.OnModifyServiceListener;
import com.ub.techexcel.bean.LineItem;
import com.ub.techexcel.bean.ServiceBean;
import com.ub.techexcel.service.ConnectService;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

public class MyOrderActivity extends Activity implements OnClickListener,
        OnPageChangeListener {

    private ServiceAdapter serviceAdapter1, serviceAdapter2, serviceAdapter3;
    private ListView serviceListView1, serviceListView2, serviceListView3;
    private List<ServiceBean> mList1 = new ArrayList<ServiceBean>(),
            mList2 = new ArrayList<ServiceBean>(),
            mList3 = new ArrayList<ServiceBean>();
    private TextView allServiceTv, serviceStatusTv, allTypeTv;
    private LinearLayout backll;

    private TextView underline;
    private Customer custometer = new Customer();

    private ViewPager mViewPager;
    private List<View> mViews = new ArrayList<View>();
    private LayoutInflater mInflater;
    private Map<Integer, String> hashMap = new HashMap<Integer, String>();
    private Semaphore semaphore = new Semaphore(0);
    private ArrayList<CommonUse> main = new ArrayList<CommonUse>();
    private String userid;
    private LinearLayout defaultPage;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppConfig.LOAD_FINISH:
                    if (mList3.size() > 0) {
                        defaultPage.setVisibility(View.GONE);
                        serviceAdapter1 = new ServiceAdapter(MyOrderActivity.this,
                                mList1, true);
                        serviceListView1.setAdapter(serviceAdapter1);
                        serviceAdapter2 = new ServiceAdapter(MyOrderActivity.this,
                                mList2, true);
                        serviceListView2.setAdapter(serviceAdapter2);
                        serviceAdapter3 = new ServiceAdapter(MyOrderActivity.this,
                                mList3, true);
                        serviceListView3.setAdapter(serviceAdapter3);
                        serviceAdapter1
                                .setOnModifyServiceListener(new OnModifyServiceListener() {
                                    @Override
                                    public void onBeginStudy(int position) {
                                        // TODO Auto-generated method stub
                                        ConfirmFinish(mList1.get(position));
                                    }

                                    @Override
                                    public void viewCourse(int position) {
                                        // 根据serviceid获得service基本信息
                                        getServiceDetail(mList1.get(position));
                                    }

                                    @Override
                                    public void enterCustomerDetail(int position) {
                                        // TODO Auto-generated method stub
                                        Intent intent = new Intent(
                                                MyOrderActivity.this,
                                                UserDetail.class);
                                        intent.putExtra("UserID",
                                                mList1.get(position).getCustomer()
                                                        .getUserID());
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void sendSMS(int position) {
                                        // TODO Auto-generated method stub
                                        GoToDialog(mList1.get(position)
                                                .getCustomer());
                                    }
                                });
                        serviceAdapter2
                                .setOnModifyServiceListener(new OnModifyServiceListener() {
                                    @Override
                                    public void onBeginStudy(int position) {
                                        // TODO Auto-generated method stub

                                    }

                                    @Override
                                    public void viewCourse(int position) {
                                        // TODO Auto-generated method stub

                                    }

                                    @Override
                                    public void enterCustomerDetail(int position) {
                                        // TODO Auto-generated method stub
                                        Intent intent = new Intent(
                                                MyOrderActivity.this,
                                                UserDetail.class);
                                        intent.putExtra("UserID",
                                                mList2.get(position).getCustomer()
                                                        .getUserID());
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void sendSMS(int position) {
                                        // TODO Auto-generated method stub
                                        GoToDialog(mList2.get(position)
                                                .getCustomer());
                                    }
                                });
                        serviceAdapter3
                                .setOnModifyServiceListener(new OnModifyServiceListener() {
                                    @Override
                                    public void onBeginStudy(int position) { // 确认结束
                                        ConfirmFinish(mList3.get(position));
                                    }

                                    @Override
                                    public void viewCourse(int position) { // 修改服务
                                        getServiceDetail(mList3.get(position));
                                    }

                                    @Override
                                    public void enterCustomerDetail(int position) { // 用户信息
                                        // TODO Auto-generated method stub
                                        Intent intent = new Intent(
                                                MyOrderActivity.this,
                                                UserDetail.class);
                                        intent.putExtra("UserID",
                                                mList3.get(position).getCustomer()
                                                        .getUserID());
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void sendSMS(int position) {
                                        // TODO Auto-generated method stub
                                        GoToDialog(mList3.get(position)
                                                .getCustomer());
                                    }
                                });
                    }

                    break;
                case AppConfig.CHOICE_FINISH:
                    // semaphore.release();
                    break;
                case AppConfig.CONFIRM_SERVICE: // 结束服务
                    semaphore.release();

                    Intent ii = new Intent();
                    ii.setAction("com.ubao.techexcel.frgment");
                    sendBroadcast(ii);

                    loadData();
                    break;
                case 0x1102: // 修改服务
                    Intent intent = new Intent(MyOrderActivity.this,
                            SelectCourseActivity.class);
                    intent.putExtra("service", (ServiceBean) msg.obj);
                    AppConfig.ISMODIFY_SERVICE = true;
                    startActivity(intent);
                    break;
                case 0x1101:
                    getKWSolutionContent((ServiceBean) msg.obj);
                    break;
                default:
                    break;
            }
        }

        ;
    };

    /**
     * 发消息
     *
     * @param customer
     */
    public void GoToDialog(final Customer customer) {
        AppConfig.Name = customer.getName();
        /*RongContext
				.getInstance()
				.getUserInfoCache()
				.put(customer.getUBAOUserID(),
						new UserInfo(customer.getUBAOUserID(), customer
								.getName(), null));*/
        RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
            @Override
            public UserInfo getUserInfo(String s) {
                return new UserInfo(
                        customer.getUBAOUserID(),
                        customer.getName(),
                        null);
            }
        }, true);
        RongIM.getInstance().startPrivateChat(MyOrderActivity.this,
                customer.getUBAOUserID(), customer.getName());
    }

    /**
     * 服务基本信息
     *
     * @param servicebean
     */
    private void getServiceDetail(final ServiceBean servicebean) {
        // TODO Auto-generated method stub
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                JSONObject returnjson = ConnectService
                        .getIncidentbyHttpGet(AppConfig.URL_PUBLIC
                                + "Service/Item?ServiceID="
                                + servicebean.getId());
                formatServiceData2(returnjson, servicebean);
            }
        }).start(((App) getApplication()).getThreadMgr());
    }

    private void formatServiceData2(JSONObject returnJson,
                                    ServiceBean serviceBean) {
        try {
            int retCode = returnJson.getInt("RetCode");
            switch (retCode) {
                case AppConfig.RETCODE_SUCCESS:
                    JSONObject service = returnJson.getJSONObject("RetData");
                    String des = service.getString("Description");
                    serviceBean
                            .setDescription((des == null || des.equals("null")) ? ""
                                    : des);
                    String comment = service.getString("Comment");
                    serviceBean.setComment((comment == null || comment
                            .equals("null")) ? "" : comment);
                    JSONObject linkedSolution = service
                            .getJSONObject("LinkedSolution");
                    serviceBean.setLinkedSolutionID(linkedSolution.getInt("ID"));
                    serviceBean.setLinkedSolutionName(linkedSolution
                            .getString("Name"));

                    Message message = Message.obtain();
                    message.obj = serviceBean;
                    message.what = 0x1101;
                    handler.sendMessage(message);
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 健康方案信息
     *
     * @param serviceBean
     */
    private void getKWSolutionContent(final ServiceBean serviceBean) {
        // TODO Auto-generated method stub
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                JSONObject returnjson = ConnectService
                        .getIncidentbyHttpGet(AppConfig.URL_PUBLIC
                                + "Service/Solution?ServiceID="
                                + serviceBean.getId() + "&LinkedSolutionID="
                                + serviceBean.getLinkedSolutionID());
                formatHealthList(returnjson, serviceBean);
            }
        }).start(((App) getApplication()).getThreadMgr());
    }

    ;

    private void formatHealthList(JSONObject retJson, ServiceBean sbean) {
        try {
            int retCode = retJson.getInt("RetCode");
            switch (retCode) {
                case AppConfig.RETCODE_SUCCESS:
                    JSONObject retdataobject = retJson.getJSONObject("RetData");
                    sbean.setLinkedSolutionName(retdataobject.getString("Name"));
                    JSONArray array = retdataobject.getJSONArray("LineItems");
                    List<LineItem> lineItems = new ArrayList<LineItem>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        LineItem bean = new LineItem();
                        String name = object.getString("Name");
                        lineItems.add(bean);
                    }
                    sbean.setLineItems(lineItems);
                    Message message = Message.obtain();
                    message.obj = sbean;
                    message.what = 0x1102;
                    handler.sendMessage(message);
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myorder);
        userid = getIntent().getStringExtra("userId");
        initView();
        initViewPager();
        InitImageView();
        getConcernList();
        loadData();

    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (AppConfig.ISONRESUME) {
            AppConfig.ISONRESUME = false;
            semaphore.release();
            loadData();
        }

        MobclickAgent.onPageStart("MyOrderActivity");
        MobclickAgent.onResume(this); // 统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MyOrderActivity");
        MobclickAgent.onPause(this);
    }

    /**
     * 确认结束服务
     *
     * @param serviceBean
     */
    private AlertDialog dialog;

    private void ConfirmFinish(final ServiceBean serviceBean) {

        final LayoutInflater inflater = LayoutInflater
                .from(getApplicationContext());
        View windov = inflater.inflate(R.layout.confirmservice, null);
        windov.findViewById(R.id.no).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        windov.findViewById(R.id.yes).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                new ApiTask(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        try {
                            JSONObject submitjson = new JSONObject();
                            submitjson.put("ID", serviceBean.getId());
                            submitjson.put("StatusID", 1);
                            JSONObject jsonObject = ConnectService
                                    .submitDataByJson(AppConfig.URL_PUBLIC
                                            + "/Service/Forward", submitjson);
                            if (jsonObject.getInt("RetCode") == 0) {
                                handler.obtainMessage(AppConfig.CONFIRM_SERVICE)
                                        .sendToTarget();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start(((App) getApplication()).getThreadMgr());
                dialog.dismiss();
            }
        });
        dialog = new AlertDialog.Builder(MyOrderActivity.this).show();
        Window dialogWindow = dialog.getWindow();
        WindowManager m = this.getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        p.width = (int) (d.getWidth() * 0.8);
        dialogWindow.setAttributes(p);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(windov);

    }

    private void getConcernList() {
        // TODO Auto-generated method stub
        LoginGet loginGet = new LoginGet();
        loginGet.setDialogGetListener(new DialogGetListener() {

            @Override
            public void getUseful(ArrayList<CommonUse> list) {
                // TODO Auto-generated method stub

            }

            @Override
            public void getCH(ArrayList<CommonUse> list) {
                // TODO Auto-generated method stub
                main = new ArrayList<CommonUse>();
                main.addAll(list);
                getCustomerList();

            }

        });

        loginGet.ConcernHierarchyRequest(this);

    }

    private void getCustomerList() {
        LoginGet get = new LoginGet();
        get.setDetailGetListener(new DetailGetListener() {
            @Override
            public void getUser(Customer user) {
                // TODO Auto-generated method stub
                custometer = user;
                semaphore.release();
            }

            @Override
            public void getMember(Customer member) {
                // TODO Auto-generated method stub
            }
        });
        get.CustomerDetailRequest(MyOrderActivity.this, userid);

    }

    /**
     * get service list
     */
    private void loadData() {
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    semaphore.acquire();
                    JSONObject returnJson = ConnectService
                            .getIncidentbyHttpGet(AppConfig.URL_PUBLIC
                                    + "Service/List?PageIndex=0&PageSize=20&UserID="
                                    + userid);
                    formatServiceData(returnJson);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start(((App) getApplication()).getThreadMgr());
    }

    private void formatServiceData(JSONObject returnJson) {
        Log.e("returnJson", returnJson.toString());
        mList1.clear();
        mList2.clear();
        mList3.clear();
        try {
            int retCode = returnJson.getInt("RetCode");
            switch (retCode) {
                case AppConfig.RETCODE_SUCCESS:
                    JSONArray retdata = returnJson.getJSONArray("RetData");
                    for (int i = 0; i < retdata.length(); i++) {
                        JSONObject service = retdata.getJSONObject(i);
                        ServiceBean bean = new ServiceBean();
                        String name = service.getString("Name");
                        bean.setName((name == null || name.equals("null")) ? ""
                                : name);
                        int concernid = service.getInt("ConcernID");
                        bean.setConcernID(concernid);

                        if (main.size() > 0) { // 关注点
                            for (CommonUse commuse : main) {
                                if (commuse.getID() == concernid) {
                                    String conName = commuse.getName();
                                    bean.setConcernName(conName);
                                    break;
                                }
                            }
                        }
                        bean.setCategoryID(service.getInt("CategoryID"));
                        bean.setSubCategoryID(service.getInt("SubCategoryID"));
                        int statusID = service.getInt("StatusID");
                        bean.setStatusID(statusID);

                        bean.setId(service.getInt("ID"));
                        bean.setIfClose(service.getInt("IfClosed"));

                        Customer customer = new Customer();
                        customer.setUserID(service.getInt("UserID") + "");
                        customer.setUrl(custometer.getUrl());
                        customer.setUBAOUserID(custometer.getUBAOUserID());
                        customer.setName(custometer.getName());
                        bean.setCustomer(customer);

                        // lineitems
                        JSONArray lineitems = service.getJSONArray("LineItems");
                        List<LineItem> items = new ArrayList<LineItem>();
                        for (int j = 0; j < lineitems.length(); j++) {
                            JSONObject lineitem = lineitems.getJSONObject(j);
                            LineItem item = new LineItem();
                            String linename = lineitem.getString("Name");
                            items.add(item);
                        }
                        bean.setLineItems(items);
                        // 获得用户的服务
                        mList3.add(bean); // 所有服务
                        if (bean.getStatusID() == 322) { // 进行中
                            mList1.add(bean);
                        }
                        if (bean.getStatusID() == 1) { // 已结束
                            mList2.add(bean);
                        }

                    }
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        handler.obtainMessage(AppConfig.LOAD_FINISH).sendToTarget();
    }

    private void initViewPager() {
        mViewPager.setAdapter(new PagerAdapter() {
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(mViews.get(position));
                return mViews.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                container.removeView(mViews.get(position));
            }

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                return mViews.size();
            }
        });
        mViewPager.setOnPageChangeListener(this);
        mViewPager.setCurrentItem(0, true);
        currIndex = 0;
    }

    private void initView() {

        allServiceTv = (TextView) findViewById(R.id.allService);
        serviceStatusTv = (TextView) findViewById(R.id.serviceStatus);
        allTypeTv = (TextView) findViewById(R.id.allType);
        underline = (TextView) findViewById(R.id.underline);
        backll = (LinearLayout) findViewById(R.id.backll);
        backll.setOnClickListener(this);
        defaultPage = (LinearLayout) findViewById(R.id.defaultpage);
        allServiceTv.setTextColor(getResources().getColor(R.color.c1));
        allServiceTv.setOnClickListener(this);
        serviceStatusTv.setOnClickListener(this);
        allTypeTv.setOnClickListener(this);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mInflater = LayoutInflater.from(this);

        View view1 = mInflater.inflate(R.layout.tabone, null);
        View view2 = mInflater.inflate(R.layout.tabtwo, null);
        View view3 = mInflater.inflate(R.layout.tabthree, null);
        serviceListView1 = (ListView) view1.findViewById(R.id.serviceList);
        serviceListView2 = (ListView) view2.findViewById(R.id.serviceList);
        serviceListView3 = (ListView) view3.findViewById(R.id.serviceList);

        serviceListView1.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Intent intent = new Intent(MyOrderActivity.this,
                        ServiceDetailActivity.class);
                intent.putExtra("id", mList1.get(arg2).getId());
                startActivity(intent);
            }
        });
        serviceListView2.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(MyOrderActivity.this,
                        ServiceDetailActivity.class);
                intent.putExtra("id", mList2.get(arg2).getId());
                startActivity(intent);
            }
        });
        serviceListView3.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(MyOrderActivity.this,
                        ServiceDetailActivity.class);
                intent.putExtra("id", mList3.get(arg2).getId());
                startActivity(intent);
            }
        });
        mViews.clear();
        mViews.add(view1);
        mViews.add(view2);
        mViews.add(view3);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.allService:
                setHeadColor((TextView) view);
                mViewPager.setCurrentItem(0, true);
                break;
            case R.id.serviceStatus:
                setHeadColor((TextView) view);
                mViewPager.setCurrentItem(1, true);
                break;
            case R.id.allType:
                setHeadColor((TextView) view);
                mViewPager.setCurrentItem(2, true);
                break;
            case R.id.backll:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * PagerAdapter 设置head颜色
     *
     * @param view
     */
    @SuppressLint("NewApi")
    private void setHeadColor(TextView view) {
        allServiceTv.setTextColor(getResources().getColor(R.color.c5));
        serviceStatusTv.setTextColor(getResources().getColor(R.color.c5));
        allTypeTv.setTextColor(getResources().getColor(R.color.c5));
        view.setTextColor(getResources().getColor(R.color.c1));
    }

    int one, two;
    private int offset = 0;// 动画图片偏移量
    private int currIndex = 0;// 当前页卡编号

    private void InitImageView() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        one = screenW / 3;
        two = one * 2;
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageSelected(int arg0) {
        // TODO Auto-generated method stub
        Animation animation = null;
        switch (arg0) {
            case 0:
                setHeadColor((TextView) allServiceTv);
                if (currIndex == 1) {
                    animation = new TranslateAnimation(one, 0, 0, 0);
                } else if (currIndex == 2) {
                    animation = new TranslateAnimation(two, 0, 0, 0);
                }
                break;
            case 1:
                setHeadColor((TextView) serviceStatusTv);
                if (currIndex == 0) {
                    animation = new TranslateAnimation(offset, one, 0, 0);
                } else if (currIndex == 2) {
                    animation = new TranslateAnimation(two, one, 0, 0);
                }
                break;
            case 2:
                setHeadColor((TextView) allTypeTv);
                if (currIndex == 0) {
                    animation = new TranslateAnimation(offset, two, 0, 0);
                } else if (currIndex == 1) {
                    animation = new TranslateAnimation(one, two, 0, 0);
                }
                break;
            default:
                break;
        }
        currIndex = arg0;
        animation.setFillAfter(true);// True:图片停在动画结束位置
        animation.setDuration(200);
        underline.startAnimation(animation);
    }

}
