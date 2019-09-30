package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wang on 2017/9/18.
 */

public class DetchedPopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private View view;
    private RelativeLayout layout;
    private TextView secondValue;
    private TextView keepIt;
    private TextView closeNow;
    private Timer mTimer = new Timer();
    private int second = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (second >= 0) {
                secondValue.setText(second + "");
                second = second - 1;
            } else {
                detchedPopupListener.closeNow();
            }
        }
    };


    public void getPopwindow(Context context, RelativeLayout layout,int countDown) {
        this.mContext = context;
        this.layout = layout;
        this.second=countDown;
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
        view = layoutInflater.inflate(R.layout.detect_dialog, null);
        secondValue = (TextView) view.findViewById(R.id.secondValue);
        keepIt = (TextView) view.findViewById(R.id.keepit);
        closeNow = (TextView) view.findViewById(R.id.closeNow);
        keepIt.setOnClickListener(this);
        closeNow.setOnClickListener(this);
        mPopupWindow = new PopupWindow(view, width / 2,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                layout.setAlpha(1.0f);
                mTimer.cancel();
                mTimer = null;
            }
        });
        mPopupWindow.setFocusable(false);
        mPopupWindow.setOutsideTouchable(false);
    }


    @SuppressLint("NewApi")
    public void StartPop(View v) {
        if (mPopupWindow != null) {
            layout.setAlpha(0.5f);
            mPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

            mTimer = new Timer();
            //启动倒计时
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(0);
                }
            }, 1000, 1000);

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

    public interface DetchedPopupListener {
        void closeNow();
    }

    public void setDetchedPopupListener(DetchedPopupListener detchedPopupListener) {
        this.detchedPopupListener = detchedPopupListener;
    }

    private DetchedPopupListener detchedPopupListener;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.keepit:
                dismiss();
                break;
            case R.id.closeNow:
                detchedPopupListener.closeNow();
                break;
            default:
                break;
        }
    }

}
