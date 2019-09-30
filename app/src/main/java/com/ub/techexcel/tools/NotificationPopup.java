package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kloudsync.techexcel.R;

/**
 * Created by wang on 2017/9/18.
 */

public class NotificationPopup {

    public Context mContext;
    public int width;
    public int height;
    public PopupWindow mPopupWindow;
    private View view;
    private TextView textview;

    private String phoneNumber;
    private int identity;

    public void getPopwindow(Context context, int identity, String phoneNumber) {
        this.mContext = context;
        width = mContext.getResources().getDisplayMetrics().widthPixels;
        height = mContext.getResources().getDisplayMetrics().heightPixels;
        this.identity = identity;
        this.phoneNumber = phoneNumber;
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
        view = layoutInflater.inflate(R.layout.notification_popup, null);
        TextView callmenowbtn = (TextView) view.findViewById(R.id.callnowbtn);
        TextView callmelaterbtn = (TextView) view.findViewById(R.id.callmelaterbtn);
        TextView end_conference = (TextView) view.findViewById(R.id.end_conference);
        if (identity == 2) { //老师
            end_conference.setVisibility(View.VISIBLE);
        } else {
            end_conference.setVisibility(View.GONE);
        }
        final EditText editText = (EditText) view.findViewById(R.id.edit_number);
        editText.setText(phoneNumber);
        if (!TextUtils.isEmpty(phoneNumber)) {
            editText.setSelection(phoneNumber.length());
        }
        callmelaterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webCamPopupListener.callLater(editText.getText().toString());
                mPopupWindow.dismiss();
            }
        });
        callmenowbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webCamPopupListener.callMe(editText.getText().toString());
                mPopupWindow.dismiss();
            }
        });
        end_conference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webCamPopupListener.endConference();
                mPopupWindow.dismiss();
            }
        });
        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                webCamPopupListener.dismiss();
            }
        });
        mPopupWindow.setFocusable(true);//这里必须设置为true才能点击区域外或者消失
        mPopupWindow.setTouchable(true);//这个控制PopupWindow内部控件的点击事件
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.update();
    }

    public interface StartLessonPopupListener {
        void dismiss();

        void open();

        void callMe(String phoneNumber);

        void callLater(String phoneNumber);

        void endConference();
    }

    public void setStartLessonPopupListener(StartLessonPopupListener webCamPopupListener) {
        this.webCamPopupListener = webCamPopupListener;
    }

    private StartLessonPopupListener webCamPopupListener;


    @SuppressLint("NewApi")
    public void StartPop(View v) {
        if (mPopupWindow != null) {
            mPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
            webCamPopupListener.open();
        }
    }

    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    public boolean isShowing(){
        return mPopupWindow.isShowing();
    }


}
