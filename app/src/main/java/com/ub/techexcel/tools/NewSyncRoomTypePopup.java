package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kloudsync.techexcel.R;

/**
 * Created by wang on 2017/9/18.
 */

public class NewSyncRoomTypePopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private View view;
    private TextView cancel, ok, service, discussion, leads;

    private static FavoritePoPListener mFavoritePoPListener;

    public interface FavoritePoPListener {

        void select(int type);

        void open();

        void dismiss();

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
        view = layoutInflater.inflate(R.layout.newsyncroomtype, null);

        cancel = (TextView) view.findViewById(R.id.cancel);
        ok = (TextView) view.findViewById(R.id.ok);
        service = (TextView) view.findViewById(R.id.service);
        discussion = (TextView) view.findViewById(R.id.discussion);
        leads = (TextView) view.findViewById(R.id.leads);

        cancel.setOnClickListener(this);
        ok.setOnClickListener(this);
        service.setOnClickListener(this);
        discussion.setOnClickListener(this);
        leads.setOnClickListener(this);

        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mFavoritePoPListener.dismiss();
                dismiss();
            }
        });

        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }


    @SuppressLint("NewApi")
    public void StartPop(View v, int type, boolean isRight) {
        if (mPopupWindow != null) {
            mFavoritePoPListener.open();
            if (isRight) {
                mPopupWindow.setAnimationStyle(R.style.anination3);
                mPopupWindow.showAtLocation(v, Gravity.RIGHT, 0, 0);
            } else {
                mPopupWindow.setAnimationStyle(R.style.dialogwindowAnim);
                mPopupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
            }

            teamType = type;
            switch (type) {
                case 0:
                    leads.setTextColor(mContext.getResources().getColor(R.color.qiangrey));
                    discussion.setTextColor(mContext.getResources().getColor(R.color.qiangrey));
                    service.setTextColor(mContext.getResources().getColor(R.color.qiangrey));
                    break;
                case 1:
                    leads.setTextColor(mContext.getResources().getColor(R.color.qiangrey));
                    discussion.setTextColor(mContext.getResources().getColor(R.color.qiangrey));
                    service.setTextColor(mContext.getResources().getColor(R.color.black));
                    break;
                case 2:
                    leads.setTextColor(mContext.getResources().getColor(R.color.qiangrey));
                    discussion.setTextColor(mContext.getResources().getColor(R.color.black));
                    service.setTextColor(mContext.getResources().getColor(R.color.qiangrey));
                    break;
                case 3:
                    leads.setTextColor(mContext.getResources().getColor(R.color.black));
                    discussion.setTextColor(mContext.getResources().getColor(R.color.black));
                    service.setTextColor(mContext.getResources().getColor(R.color.qiangrey));
                    break;
            }

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


    private int teamType = 0;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                dismiss();
                break;
            case R.id.ok:
                dismiss();
                if (teamType == 0) {
                } else {
                    mFavoritePoPListener.select(teamType);
                }
                break;
            case R.id.service:
                teamType = 1;
                leads.setTextColor(mContext.getResources().getColor(R.color.qiangrey));
                discussion.setTextColor(mContext.getResources().getColor(R.color.qiangrey));
                service.setTextColor(mContext.getResources().getColor(R.color.black));
                break;
            case R.id.discussion:
                teamType = 2;
                leads.setTextColor(mContext.getResources().getColor(R.color.qiangrey));
                discussion.setTextColor(mContext.getResources().getColor(R.color.black));
                service.setTextColor(mContext.getResources().getColor(R.color.qiangrey));
                break;
            case R.id.leads:
                teamType = 3;
                leads.setTextColor(mContext.getResources().getColor(R.color.black));
                discussion.setTextColor(mContext.getResources().getColor(R.color.qiangrey));
                service.setTextColor(mContext.getResources().getColor(R.color.qiangrey));
                break;


            default:
                break;
        }
    }

}
