package com.kloudsync.techexcel.help;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mining.app.zxing.MipcaActivityCapture;
import com.ub.friends.activity.AddFriendsActivity;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.school.SelectSchoolActivity;

public class PopContactHAHA {

    public Context mContext;

    private int width, height;

    public void getPopwindow(Context context) {
        this.mContext = context;

        width = mContext.getResources().getDisplayMetrics().widthPixels;
        height = mContext.getResources().getDisplayMetrics().heightPixels;

        getPopupWindowInstance();
        mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
    }


    public PopupWindow mPopupWindow;

    public void getPopupWindowInstance() {
        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }

    private LinearLayout lin_addfriend;
    private LinearLayout lin_scan;
    private RelativeLayout rl_school;
    private TextView tv_seschool;

    @SuppressWarnings("deprecation")
    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View popupWindow = layoutInflater.inflate(R.layout.popup_contactright, null);

        lin_addfriend = (LinearLayout) popupWindow.findViewById(R.id.lin_addfriend);
        lin_scan = (LinearLayout) popupWindow.findViewById(R.id.lin_scan);
        rl_school = (RelativeLayout) popupWindow.findViewById(R.id.rl_school);
        tv_seschool = (TextView) popupWindow.findViewById(R.id.tv_seschool);

        mPopupWindow = new PopupWindow(popupWindow,  width * 2 / 5,
                height / 5, false);

        mPopupWindow.getWidth();
        mPopupWindow.getHeight();

        lin_addfriend.setOnClickListener(new myOnClick());
        lin_scan.setOnClickListener(new myOnClick());
        rl_school.setOnClickListener(new myOnClick());

        showSchoolName();


        // 使其聚焦
        mPopupWindow.setFocusable(true);
        // 设置允许在外点击消失
        mPopupWindow.setOutsideTouchable(true);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }


    private SharedPreferences sharedPreferences;
    private void showSchoolName() {

        sharedPreferences = mContext.getSharedPreferences(AppConfig.LOGININFO,
                mContext.MODE_PRIVATE);
        int SchoolId = sharedPreferences.getInt("SchoolID", -1);
        String schoolName = sharedPreferences.getString("SchoolName", null);
        if (-1 == SchoolId || SchoolId == AppConfig.SchoolID) {
            tv_seschool.setText(mContext.getResources().getString(R.string.My_School));
        } else {
            tv_seschool.setText(schoolName);
        }
    }


    private class myOnClick implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.lin_addfriend:
                    IjumpYoumustbejump(AddFriendsActivity.class);
                    Intent i;
                    break;
                case R.id.lin_scan:
                    IjumpYoumustbejump(MipcaActivityCapture.class);
                    break;
                case R.id.rl_school:
                    IjumpYoumustbejump(SelectSchoolActivity.class);
                    break;

                default:
                    break;
            }

        }

    }

    private void IjumpYoumustbejump(Class haha) {
        Intent i = new Intent(mContext, haha);
        i.putExtra("currentposition", 0);
        mContext.startActivity(i);
        mPopupWindow.dismiss();
    }

    public void StartPop(View v) {
        mPopupWindow.showAsDropDown(v);
    }


}
