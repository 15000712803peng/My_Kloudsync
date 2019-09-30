package com.kloudsync.techexcel.help;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kloudsync.techexcel.R;

public class Popupdate2 {

    public Context mContext;
    public String title;
    private int width;


    private static PopDismissListener popDismissListener;

    public interface PopDismissListener {

        void PopDismiss();
    }

    public void setPoPDismissListener(PopDismissListener popDismissListener) {
        this.popDismissListener = popDismissListener;
    }


    private static PopCancelListener popCancelListener;

    public interface PopCancelListener {

        void Cancel();
    }

    public void setPopCancelListener(PopCancelListener popCancelListener) {
        this.popCancelListener = popCancelListener;
    }

    public void getPopwindow(Context context) {
        this.mContext = context;

        float density = mContext.getResources().getDisplayMetrics().density;
        width = mContext.getResources().getDisplayMetrics().widthPixels;
        int height = mContext.getResources().getDisplayMetrics().heightPixels;
        width = width > height? height:width;
        width -= 50 * density;
        getPopupWindowInstance();
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

    private TextView tv_pb;
    private TextView tv_title;
    private ProgressBar rpb_update;

    @SuppressWarnings("deprecation")
    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View popupWindow = layoutInflater.inflate(R.layout.pop_update2, null);

        tv_pb = (TextView) popupWindow.findViewById(R.id.tv_pb);
        tv_title = (TextView) popupWindow.findViewById(R.id.tv_title);
        rpb_update = (ProgressBar) popupWindow.findViewById(R.id.rpb_update);

        mPopupWindow = new PopupWindow(popupWindow, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, false);

        mPopupWindow.getWidth();
        mPopupWindow.getHeight();

        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (popDismissListener != null) {
                    popDismissListener.PopDismiss();
                }
            }
        });


        // 使其聚焦
        mPopupWindow.setFocusable(true);
        // 设置允许在外点击消失
        mPopupWindow.setOutsideTouchable(true);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
//        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }


    private class myOnClick implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                default:
                    break;
            }
        }
    }

    public void setProgress(long total, long current) {
        if (!mPopupWindow.isShowing()) {
            return;
        }
        int pb = (int) (current * 100 / total);
        tv_pb.setText(pb + "%");
        if(100 == pb){
            mPopupWindow.dismiss();
        }
    }

    public void DissmissPop() {
        ChangeName("Downloading...");
        if (mPopupWindow.isShowing())
            mPopupWindow.dismiss();
    }

    public void StartPop(View v) {
        if (!mPopupWindow.isShowing())
            mPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    public void setProgress2(long pb) {
        if (!mPopupWindow.isShowing()) {
            return;
        }
        tv_pb.setText(pb + "%");
        if (100 == pb) {
            DissmissPop();
        }
    }

    public void ChangeName(String title) {
        tv_title.setText(title);
    }

}
