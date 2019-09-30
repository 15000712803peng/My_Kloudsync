package com.kloudsync.techexcel.help;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.view.RoundProgressBar;

public class Popupdate {

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

    public void getPopwindow(Context context, String title) {
        this.mContext = context;
        this.title = title;

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

    private TextView tv_name, tv_cancel;
    private TextView tv_title;
    private RoundProgressBar rpb_update;

    @SuppressWarnings("deprecation")
    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View popupWindow = layoutInflater.inflate(R.layout.pop_update, null);

        tv_cancel = (TextView) popupWindow.findViewById(R.id.tv_cancel);
        tv_name = (TextView) popupWindow.findViewById(R.id.tv_name);
        tv_title = (TextView) popupWindow.findViewById(R.id.tv_title);
        rpb_update = (RoundProgressBar) popupWindow.findViewById(R.id.rpb_update);
        tv_name.setText(title);
        rpb_update.setProgress(0);

        mPopupWindow = new PopupWindow(popupWindow, width,
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

        tv_cancel.setOnClickListener(new myOnClick());

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
                case R.id.tv_cancel:
                    if(popCancelListener != null){
                        popCancelListener.Cancel();
                    }
                    DissmissPop();
                    break;

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
        rpb_update.setProgress(pb);
    }

    public void DissmissPop() {
        if (mPopupWindow.isShowing())
            mPopupWindow.dismiss();
    }

    public void ChangeName(String name) {
        tv_title.setText(name);
        rpb_update.setCricleProgressColor(mContext.getResources().getColor(R.color.green));
    }

    public void StartPop(View v) {
        mPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }


}
