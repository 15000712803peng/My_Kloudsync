package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.service.ConnectService;
import com.ub.techexcel.adapter.ServiceAdapter4;
import com.ub.techexcel.bean.LineItem;
import com.ub.techexcel.bean.ServiceBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class SyncRoomMeetingPopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private View view;
    private ImageView adddocument;
    private TextView upcoming, finished, inprogressunderline;
    private ViewPager mViewPager;
    private List<ServiceBean> mList1 = new ArrayList<>();
    private List<ServiceBean> mList2 = new ArrayList<>();
    private ListView serviceListView1;
    private ListView serviceListView2;
    private ServiceAdapter4 serviceAdapter1;
    private ServiceAdapter4 serviceAdapter2;
    private List<View> viewList = new ArrayList<>();

    public void getPopwindow(Context context) {
        this.mContext = context;
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
        view = layoutInflater.inflate(R.layout.syncroom_meeting_popup, null);

        adddocument = (ImageView) view.findViewById(R.id.adddocument);
        adddocument.setOnClickListener(this);
        upcoming = (TextView) view.findViewById(R.id.upcoming);
        upcoming.setOnClickListener(this);
        finished = (TextView) view.findViewById(R.id.finished);
        finished.setOnClickListener(this);

        inprogressunderline = (TextView) view.findViewById(R.id.inprogressunderline);
        int screenW = dp2px(mContext, 320);

        width = screenW / 2;
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) inprogressunderline.getLayoutParams();
        lp.width = width / 2; //设置滑动条的宽度为view的1/4
        lp.leftMargin = width / 4;
        inprogressunderline.setLayoutParams(lp);


        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);

        layoutInflater = LayoutInflater.from(mContext);
        View allView1 = layoutInflater.inflate(R.layout.tabone, null);
        View allView2 = layoutInflater.inflate(R.layout.tabtwo, null);

        serviceListView1 = (ListView) allView1.findViewById(R.id.serviceList);
        serviceListView2 = (ListView) allView2.findViewById(R.id.serviceList);
        viewList.add(allView1);
        viewList.add(allView2);

        mViewPager.setAdapter(new ServicePagerAdapter());
        mViewPager.setCurrentItem(0);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                float currMarginLeft = width * positionOffset + position * width + width / 4;
                RelativeLayout.LayoutParams redLp = (RelativeLayout.LayoutParams) inprogressunderline.getLayoutParams();
                redLp.leftMargin = (int) currMarginLeft;
                inprogressunderline.setLayoutParams(redLp);
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        setDefault();
                        upcoming.setTextColor(mContext.getResources().getColor(R.color.skyblue));
                        break;
                    case 1:
                        setDefault();
                        finished.setTextColor(mContext.getResources().getColor(R.color.skyblue));
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        mPopupWindow = new PopupWindow(view, dp2px(mContext, 320),
                ViewGroup.LayoutParams.MATCH_PARENT, false);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                webCamPopupListener.dismiss();
            }
        });
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupWindow.update();
        mPopupWindow.setAnimationStyle(R.style.anination3);

    }


    private View outView;
    private String syncroomid;

    @SuppressLint("NewApi")
    public void StartPop(View v, String syncroomid) {
        this.outView = v;
        this.syncroomid = syncroomid;
        if (mPopupWindow != null) {
            webCamPopupListener.open();
            mPopupWindow.showAtLocation(v, Gravity.RIGHT, 0, 0);
            getSyncRoomMeeting(syncroomid);
        }
    }

    public boolean isShowing() {
        return mPopupWindow.isShowing();
    }

    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x1102:
                    serviceAdapter1 = new ServiceAdapter4(mContext, mList1);
                    serviceListView1.setAdapter(serviceAdapter1);
                    serviceAdapter2 = new ServiceAdapter4(mContext, mList2);
                    serviceListView2.setAdapter(serviceAdapter2);
                    break;
            }
            super.handleMessage(msg);
        }
    };


    public void getSyncRoomMeeting(final String syncroomid) {
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = ConnectService.getIncidentbyHttpGet(AppConfig.URL_PUBLIC + "SyncRoom/SyncRoomMeeting?syncRoomID=" + syncroomid + "&type=1");
                Log.e("dddddddd", jsonObject.toString());
                formatServiceData(jsonObject);
            }
        }).start(ThreadManager.getManager());
    }


    private void formatServiceData(JSONObject returnJson) {
        try {
            int retCode = returnJson.getInt("RetCode");
            switch (retCode) {
                case AppConfig.RETCODE_SUCCESS:
                    JSONArray retdata = returnJson.getJSONArray("RetData");
                    mList1.clear();
                    mList2.clear();
                    for (int i = 0; i < retdata.length(); i++) {
                        JSONObject service = retdata.getJSONObject(i);
                        ServiceBean bean = new ServiceBean();
                        int statusID = service.getInt("Status");
                        bean.setStatusID(statusID);
                        bean.setId(service.getInt("LessonID"));
                        bean.setRoleinlesson(service.getInt("Role"));
                        bean.setPlanedEndDate(service.getString("PlanedEndDate"));
                        bean.setPlanedStartDate(service.getString("PlanedStartDate"));
                        bean.setCourseName(service.getString("CourseName"));
                        bean.setUserName(service.getString("StudentNames"));
                        bean.setName(service.getString("Title"));
                        bean.setTeacherName(service.getString("TeacherNames"));
                        bean.setTeacherCount(service.getInt("TeacherCount"));
                        bean.setStudentCount(service.getInt("StudentCount"));
                        bean.setFinished(service.getInt("IsFinished") == 1 ? true : false);
                        if (bean.isFinished()) {
                            mList2.add(bean);
                        } else {
                            mList1.add(bean);
                        }
                    }
                    Message message = Message.obtain();
                    message.what = 0x1102;
                    handler.sendMessage(message);
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface WebCamPopupListener {

        void changeOptions(LineItem syncRoomBean);

        void dismiss();

        void open();

    }

    public void setWebCamPopupListener(WebCamPopupListener webCamPopupListener) {
        this.webCamPopupListener = webCamPopupListener;
    }

    private WebCamPopupListener webCamPopupListener;


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.upcoming:
                mViewPager.setCurrentItem(0);
                setDefault();
                upcoming.setTextColor(mContext.getResources().getColor(R.color.skyblue));
                break;
            case R.id.finished:
                mViewPager.setCurrentItem(1);
                setDefault();
                finished.setTextColor(mContext.getResources().getColor(R.color.skyblue));
                break;
            case R.id.adddocument:
                SyncRoomAddMeetingPopup syncRoomAddMeetingPopup = new SyncRoomAddMeetingPopup();
                syncRoomAddMeetingPopup.getPopwindow(mContext);
                syncRoomAddMeetingPopup.setFavoritePoPListener(new SyncRoomAddMeetingPopup.FavoritePoPListener() {
                    @Override
                    public void success() {
                        getSyncRoomMeeting(syncroomid);
                    }
                });
                syncRoomAddMeetingPopup.StartPop(outView, syncroomid);
                break;
            default:
                break;
        }
    }


    private void setDefault() {
        upcoming.setTextColor(mContext.getResources().getColor(R.color.c5));
        finished.setTextColor(mContext.getResources().getColor(R.color.c5));
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

    public int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
