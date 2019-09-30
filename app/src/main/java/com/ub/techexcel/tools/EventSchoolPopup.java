package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.kloudsync.techexcel.R;

/**
 * Created by wang on 2017/9/18.
 */

public class EventSchoolPopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private View view;
    private LinearLayout myschoolll;
    private LinearLayout currentschoolll;

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
        view = layoutInflater.inflate(R.layout.eventchange_popup, null);
        myschoolll = (LinearLayout) view.findViewById(R.id.myschoolll);
        currentschoolll = (LinearLayout) view.findViewById(R.id.currentschoolll);
        myschoolll.setOnClickListener(this);
        currentschoolll.setOnClickListener(this);
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


    @SuppressLint("NewApi")
    public void StartPop(View v) {
        if (mPopupWindow != null) {

            mPopupWindow.getContentView().measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
            int i = mPopupWindow.getContentView().getMeasuredWidth();

            mPopupWindow.showAsDropDown(v,300,0);
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
            case R.id.myschoolll:
                dismiss();
                webCamPopupListener.changeOptions(0);
                break;
            case R.id.currentschoolll:
                dismiss();
                webCamPopupListener.changeOptions(1);
                break;
            default:
                break;
        }
    }


}
