package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;

/**
 * Created by wang on 2017/9/18.
 */

public class NewTeamTypePopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private View view;
    private TextView cancel, ok, bothdocument, documentonly;

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
        view = layoutInflater.inflate(R.layout.newteamtype, null);

        cancel = (TextView) view.findViewById(R.id.cancel);
        ok = (TextView) view.findViewById(R.id.ok);
        documentonly = (TextView) view.findViewById(R.id.documentonly);
        bothdocument = (TextView) view.findViewById(R.id.bothdocument);

        cancel.setOnClickListener(this);
        ok.setOnClickListener(this);
        documentonly.setOnClickListener(this);
        bothdocument.setOnClickListener(this);

        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mFavoritePoPListener.dismiss();
                dismiss();
            }
        });
        mPopupWindow.setAnimationStyle(R.style.dialogwindowAnim);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }


    @SuppressLint("NewApi")
    public void StartPop(View v,int type) {
        if (mPopupWindow != null) {
            mFavoritePoPListener.open();
            mPopupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
            teamType=type;
            switch (type){
                case 0:
                    bothdocument.setTextColor(mContext.getResources().getColor(R.color.qiangrey));
                    documentonly.setTextColor(mContext.getResources().getColor(R.color.qiangrey));
                    break;
                case 1:
                    bothdocument.setTextColor(mContext.getResources().getColor(R.color.qiangrey));
                    documentonly.setTextColor(mContext.getResources().getColor(R.color.black));
                    break;
                case 2:
                    bothdocument.setTextColor(mContext.getResources().getColor(R.color.black));
                    documentonly.setTextColor(mContext.getResources().getColor(R.color.qiangrey));
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
                if(teamType==0){
                }else{
                    mFavoritePoPListener.select(teamType);
                }
                break;
            case R.id.bothdocument:
                teamType = 2;
                bothdocument.setTextColor(mContext.getResources().getColor(R.color.black));
                documentonly.setTextColor(mContext.getResources().getColor(R.color.qiangrey));
                break;
            case R.id.documentonly:
                bothdocument.setTextColor(mContext.getResources().getColor(R.color.qiangrey));
                documentonly.setTextColor(mContext.getResources().getColor(R.color.black));
                teamType = 1;
                break;

            default:
                break;
        }
    }

}
