package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.service.ConnectService;
import com.ub.techexcel.adapter.NewMeetingContactAdapter;

import org.feezu.liuli.timeselector.TimeSelector;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wang on 2017/9/18.
 */

public class SyncRoomAddMeetingPopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private View view;
    private ImageView cancel;
    private TextView submit;
    private EditText meetingname;
    private TextView meetingstartdate;
    private TextView meetingstarttime;
    private EditText meetingduration;
    private LinearLayout starttimell, startdatell;
    private TextView invitecontact;
    private RecyclerView mRecyclerView;


    private static FavoritePoPListener mFavoritePoPListener;

    public interface FavoritePoPListener {

        void success();

    }

    public void setFavoritePoPListener(FavoritePoPListener documentPoPListener) {
        this.mFavoritePoPListener = documentPoPListener;
    }


    public void getPopwindow(Context context) {
        this.mContext = context;
        width = mContext.getResources().getDisplayMetrics().widthPixels;
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
        view = layoutInflater.inflate(R.layout.syncroomaddmeetingpopup, null);


        cancel = (ImageView) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        submit = (TextView) view.findViewById(R.id.submit);
        submit.setOnClickListener(this);
        meetingname = (EditText) view.findViewById(R.id.meetingname);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd HH:mm:ss");
        String time = simpleDateFormat.format(new Date());
        meetingname.setText(AppConfig.UserName + time);
        meetingname.setSelection((AppConfig.UserName + time).length());
        meetingduration = (EditText) view.findViewById(R.id.meetingduration);

        invitecontact = (TextView) view.findViewById(R.id.invitecontact);
        invitecontact.setOnClickListener(this);
        meetingstartdate = (TextView) view.findViewById(R.id.meetingstartdate);
        meetingstarttime = (TextView) view.findViewById(R.id.meetingstarttime);
        startdatell = (LinearLayout) view.findViewById(R.id.startdatell);
        starttimell = (LinearLayout) view.findViewById(R.id.starttimell);
        startdatell.setOnClickListener(this);
        starttimell.setOnClickListener(this);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycleview);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);
        gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(gridLayoutManager);


        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                dismiss();
            }
        });
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }


    private View outView;
    private String syncroomid;

    @SuppressLint("NewApi")
    public void StartPop(View v,String syncroomid) {

        this.outView = v;
        this.syncroomid = syncroomid;
        if (mPopupWindow != null) {
            mPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
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


    private List<Customer> customerList=new ArrayList<>();
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                dismiss();
                break;
            case R.id.startdatell:
                selectDate();
                break;
            case R.id.starttimell:
                selectTime();
                break;
            case R.id.invitecontact:

                InviteOthersPopup inviteOthersPopup = new InviteOthersPopup();
                inviteOthersPopup.getPopwindow(mContext);
                inviteOthersPopup.setFavoritePoPListener(new InviteOthersPopup.FavoritePoPListener() {
                    @Override
                    public void select(List<Customer> list) {
                        customerList.clear();
                        customerList.addAll(list);
                        NewMeetingContactAdapter newMeetingContactAdapter = new NewMeetingContactAdapter(mContext, list);
                        mRecyclerView.setAdapter(newMeetingContactAdapter);
                    }
                });
                inviteOthersPopup.StartPop(outView);
                break;
            case R.id.submit:
                submit();
                break;

            default:
                break;
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x1001:
                    dismiss();
                    mFavoritePoPListener.success();
                    break;
            }
        }
    };
    private void submit() {
        if (startsecond == 0 || endsecond == 0) {
            Toast.makeText(mContext, "please select date", Toast.LENGTH_LONG).show();
            return;
        }
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("Title", meetingname.getText().toString());
                    jsonObject.put("Description", meetingduration.getText().toString());
                    jsonObject.put("StartDate", startsecond);
                    jsonObject.put("EndDate", endsecond);
                    jsonObject.put("CompanyID", AppConfig.SchoolID);
                    jsonObject.put("SyncRoomID", syncroomid);

                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < customerList.size(); i++) {
                        JSONObject j = new JSONObject();
                        j.put("MemberID", customerList.get(i).getUserID());
                        j.put("Role", 1);
                        jsonArray.put(j);
                    }
                    JSONObject j2 = new JSONObject();
                    j2.put("MemberID", AppConfig.UserID);
                    j2.put("Role", 2);
                    jsonArray.put(j2);
                    jsonObject.put("MemberList", jsonArray);
                    JSONObject returnJson = ConnectService.submitDataByJson(AppConfig.URL_PUBLIC + "SyncRoom/CreateMeetingFromSyncRoom", jsonObject);
                    Log.e("CreateOrUpdateLessons", jsonObject.toString() + "    " + returnJson.toString());
                    int retCode = returnJson.getInt("RetCode");
                    switch (retCode) {
                        case 0:
                            Message msg = Message.obtain();
                            msg.what = 0x1001;
                            handler.sendMessage(msg);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start(ThreadManager.getManager());

    }

    Long endsecond;

    private void selectTime() {
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        TimeSelector timeSelector = new TimeSelector(mContext, new TimeSelector.ResultHandler() {
            @Override
            public void handle(String time) {
                try {
                    endsecond = formatter.parse(time).getTime();
                    meetingstarttime.setText(time);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, str, "2030-12-31 00:00");
        timeSelector.show();

    }


    Long startsecond;

    private void selectDate() {

        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        TimeSelector timeSelector = new TimeSelector(mContext, new TimeSelector.ResultHandler() {
            @Override
            public void handle(String time) {
                try {
                    startsecond = formatter.parse(time).getTime();
                    meetingstartdate.setText(time);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, str, "2030-12-31 00:00");
        timeSelector.show();


    }

}
