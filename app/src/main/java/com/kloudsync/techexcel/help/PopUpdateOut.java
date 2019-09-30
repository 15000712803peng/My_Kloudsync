package com.kloudsync.techexcel.help;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kloudsync.techexcel.R;

public class PopUpdateOut {

    public Context mContext;

    private static PopUpdateOutDismissListener popUpdateOutDismissListener;

    private boolean flag;

    public interface PopUpdateOutDismissListener {
        void PopDismiss(boolean isUpdate);
    }

    public void setPoPDismissListener(PopUpdateOutDismissListener popUpdateOutDismissListener) {
        PopUpdateOut.popUpdateOutDismissListener = popUpdateOutDismissListener;
    }

    public void getPopwindow(Context context) {
        this.mContext = context;

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

    private TextView tv_yes, tv_no;

    @SuppressWarnings("deprecation")
    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View popupWindow = layoutInflater.inflate(R.layout.pop_out_update, null);

        tv_yes = (TextView) popupWindow.findViewById(R.id.tv_yes);
        tv_no = (TextView) popupWindow.findViewById(R.id.tv_no);


        mPopupWindow = new PopupWindow(popupWindow, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, false);

        mPopupWindow.getWidth();
        mPopupWindow.getHeight();

        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (popUpdateOutDismissListener != null) {
                    popUpdateOutDismissListener.PopDismiss(flag);
                }
            }
        });

        tv_yes.setOnClickListener(new myOnClick());
        tv_no.setOnClickListener(new myOnClick());


        // 使其聚焦
        mPopupWindow.setFocusable(true);
        // 设置允许在外点击消失
        mPopupWindow.setOutsideTouchable(true);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }


    private class myOnClick implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_yes:
                    DissmissPop(true);
                    break;
                case R.id.tv_no:
                    DissmissPop(false);
                    break;

                default:
                    break;
            }

        }


    }

    private void DissmissPop(boolean isupdate) {
        flag = isupdate;
        mPopupWindow.dismiss();
    }

    public void StartPop(View v) {
        mPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }


}
