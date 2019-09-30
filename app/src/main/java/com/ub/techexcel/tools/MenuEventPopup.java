package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;

/**
 * Created by wang on 2017/9/18.
 */

public class MenuEventPopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private View view;
    private LinearLayout newprivatelesson;
    private LinearLayout newpubliclesson;
    private LinearLayout newmeeting;
    private LinearLayout scantv;
    private RelativeLayout myschool;
    private TextView tt;

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
        view = layoutInflater.inflate(R.layout.menuevent_popup, null);

        newprivatelesson = (LinearLayout) view.findViewById(R.id.newprivatelesson);
        newpubliclesson = (LinearLayout) view.findViewById(R.id.newpubliclesson);
        newmeeting = (LinearLayout) view.findViewById(R.id.newmeeting);
        scantv = (LinearLayout) view.findViewById(R.id.scan);
        myschool = (RelativeLayout) view.findViewById(R.id.myschool);
        tt = (TextView) view.findViewById(R.id.tt);

        newprivatelesson.setOnClickListener(this);
        newpubliclesson.setOnClickListener(this);
        newmeeting.setOnClickListener(this);
        scantv.setOnClickListener(this);
        myschool.setOnClickListener(this);


        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mPopupWindow.dismiss();
            }
        });
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupWindow.update();
    }
    private SharedPreferences sharedPreferences;
    private void showSchoolName() {

        sharedPreferences = mContext.getSharedPreferences(AppConfig.LOGININFO,
                mContext.MODE_PRIVATE);
        int SchoolId = sharedPreferences.getInt("SchoolID", -1);
        String schoolName = sharedPreferences.getString("SchoolName", null);
        if (-1 == SchoolId || SchoolId == AppConfig.SchoolID) {
           tt.setText(mContext.getResources().getString(R.string.My_School));
        } else {
            tt.setText(schoolName);
        }
    }

    @SuppressLint("NewApi")
    public void StartPop(View v) {
        if (mPopupWindow != null) {
            showSchoolName();
            mPopupWindow.showAsDropDown(v, 0, 0);

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

    public interface WebCamPopupListener {
        void changeOptions(int position);
    }

    public void setWebCamPopupListener(WebCamPopupListener webCamPopupListener) {
        this.webCamPopupListener = webCamPopupListener;
    }

    private WebCamPopupListener webCamPopupListener;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.newprivatelesson:
                dismiss();
                webCamPopupListener.changeOptions(0);
                break;
            case R.id.newpubliclesson:
                dismiss();
                webCamPopupListener.changeOptions(1);
                break;
            case R.id.newmeeting:
                dismiss();
                webCamPopupListener.changeOptions(2);
                break;
            case R.id.scan:
                dismiss();
                webCamPopupListener.changeOptions(3);
                break;
            case R.id.myschool:
                dismiss();
                webCamPopupListener.changeOptions(4);
                break;
            default:
                break;
        }
    }


}
