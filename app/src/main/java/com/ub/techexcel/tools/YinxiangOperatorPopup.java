package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.docment.WeiXinApi;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.tool.PopupWindowUtil;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.mm.sdk.platformtools.Util;
import com.ub.techexcel.adapter.YinXiangAdapter;
import com.ub.techexcel.adapter.YinXiangAdapter2;
import com.ub.techexcel.bean.SoundtrackBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 2017/9/18.
 */

public class YinxiangOperatorPopup implements View.OnClickListener {


    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private View view;

    LinearLayout yinxiangedit;
    LinearLayout yinxiangdelete;
    LinearLayout yinxiangplay;
    LinearLayout yinxiangshare;
    LinearLayout copyUrl;
    LinearLayout shareInApp;
    LinearLayout sharemore;

    private static FavoritePoPListener mFavoritePoPListener;

    public interface FavoritePoPListener {

        void editYinxiang();

        void deleteYinxiang();

        void playYinxiang();

        void shareYinxiang();

        void copyUrl();

        void shareInApp();

        void sharePopup();
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
        view = layoutInflater.inflate(R.layout.yinxiangoperatorpopup, null);

        yinxiangedit = (LinearLayout) view.findViewById(R.id.yinxiangedit);
        yinxiangdelete = (LinearLayout) view.findViewById(R.id.yinxiangdelete);
        yinxiangplay = (LinearLayout) view.findViewById(R.id.yinxiangplay);
        yinxiangshare = (LinearLayout) view.findViewById(R.id.yinxiangshare);
        copyUrl = (LinearLayout) view.findViewById(R.id.copyurl);
        shareInApp = (LinearLayout) view.findViewById(R.id.shareinapp);
        sharemore = (LinearLayout) view.findViewById(R.id.sharemore);


        yinxiangedit.setOnClickListener(this);
        sharemore.setOnClickListener(this);
        yinxiangdelete.setOnClickListener(this);
        yinxiangplay.setOnClickListener(this);
        yinxiangshare.setOnClickListener(this);
        copyUrl.setOnClickListener(this);
        shareInApp.setOnClickListener(this);

        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setAnimationStyle(R.style.anination3);

    }


    @SuppressLint("NewApi")
    public void StartPop(View v, SoundtrackBean soundtrackBean) {
        if (mPopupWindow != null) {
            if (soundtrackBean.isHidden()) {
                yinxiangedit.setVisibility(View.GONE);
            } else {
                yinxiangedit.setVisibility(View.VISIBLE);
            }
//            mPopupWindow.showAtLocation(v, Gravity.RIGHT, 0, 0);


            int windowPos[] = PopupWindowUtil.calculatePopWindowPos(v, view , 50);
            int height = mContext.getResources().getDisplayMetrics().heightPixels;

            Log.e("duang", height + ":" + windowPos[1]);
            int xOff = 20; // 可以自己调整偏移
            windowPos[0] -= xOff;
            mPopupWindow.showAtLocation(v, Gravity.TOP | Gravity.START, windowPos[0], windowPos[1]);

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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.yinxiangedit:
                dismiss();
                mFavoritePoPListener.editYinxiang();
                break;
            case R.id.yinxiangdelete:
                dismiss();
                mFavoritePoPListener.deleteYinxiang();
                break;
            case R.id.yinxiangplay:
                dismiss();
                mFavoritePoPListener.playYinxiang();
                break;
            case R.id.yinxiangshare:
                dismiss();
                mFavoritePoPListener.shareYinxiang();
                break;
            case R.id.copyurl:
                dismiss();
                mFavoritePoPListener.copyUrl();
                break;
            case R.id.shareinapp:
                dismiss();
                mFavoritePoPListener.shareInApp();
                break;
            case R.id.sharemore:
                dismiss();
                mFavoritePoPListener.sharePopup();
                break;
            default:
                break;
        }
    }


}
