package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.service.ConnectService;

import com.ub.techexcel.bean.UpcomingLesson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 2017/9/18.
 */

public class MeetingYinxiangSharePopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public Dialog mDialog;
    private View view;
    private LinearLayout wechat,qq,peertime;

    private static FavoritePoPListener mFavoritePoPListener;

    public interface FavoritePoPListener {

        void weChat();

        void QQ();

        void peertime();
    }

    public void setFavoritePoPListener(FavoritePoPListener documentPoPListener) {
        this.mFavoritePoPListener = documentPoPListener;
    }


    public void getPopwindow(Context context) {
        this.mContext = context;
        width = mContext.getResources().getDisplayMetrics().widthPixels;
        getPopupWindowInstance();
//        mDialog.getWindow().setWindowAnimations(R.style.PopupAnimation5);
    }

    public void getPopupWindowInstance() {
        if (null != mDialog) {
            mDialog.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }


    @SuppressLint("WrongConstant")
    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.meetingyinxiangsharepopup, null);
        wechat=view.findViewById(R.id.wechat);
        qq=view.findViewById(R.id.qq);
        peertime=view.findViewById(R.id.peertime);
        wechat.setOnClickListener(this);
        qq.setOnClickListener(this);
        peertime.setOnClickListener(this);

        mDialog = new Dialog(mContext, R.style.my_dialog);
        mDialog.setContentView(view);
        mDialog.getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
        lp.width = mContext.getResources().getDisplayMetrics().widthPixels * 3 / 5;
        mDialog.getWindow().setAttributes(lp);


    }


    @SuppressLint("NewApi")
    public void StartPop(View v) {
        if (mDialog != null) {
            mDialog.show();
        }
    }


    public boolean isShowing() {
        return mDialog.isShowing();
    }

    public void dismiss() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.wechat:
                dismiss();
                mFavoritePoPListener.weChat();
                break;
            case R.id.qq:
                dismiss();
                mFavoritePoPListener.QQ();
                break;
            case R.id.peertime:
                dismiss();
                mFavoritePoPListener.peertime();
                break;

            default:
                break;
        }
    }


}
