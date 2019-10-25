package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.content.Context;
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

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.TvDevice;
import com.kloudsync.techexcel.tool.PopupWindowUtil;
import com.ub.techexcel.bean.SoundtrackBean;

/**
 * Created by wang on 2017/9/18.
 */

public class TvDevicesOperatorPopup implements View.OnClickListener {


    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private View view;
    private TextView divider;
    private RelativeLayout opentransfer, closetransfer, deviceout;
    private ImageView opentransferimage, closetransferimage;

    private static FavoritePoPListener mFavoritePoPListener;

    public interface FavoritePoPListener {

        void openTransfer();

        void closeTransfer();

        void logout();


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
        view = layoutInflater.inflate(R.layout.tvdeviceoperatorpopup, null);
        opentransfer = view.findViewById(R.id.opentransfer);
        closetransfer = view.findViewById(R.id.closetransfer);

        deviceout = view.findViewById(R.id.deviceout);
        divider = view.findViewById(R.id.divider);
        opentransfer.setOnClickListener(this);
        closetransfer.setOnClickListener(this);
        deviceout.setOnClickListener(this);
        opentransferimage = view.findViewById(R.id.opentransferimage);
        closetransferimage = view.findViewById(R.id.closetransferimage);
        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
//        mPopupWindow.setAnimationStyle(R.style.anination3);

    }


    @SuppressLint("NewApi")
    public void StartPop(View v, TvDevice device, boolean isbeike) {
        if (mPopupWindow != null) {
            int windowPos[] = PopupWindowUtil.calculatePopWindowPos(v, view, 50);
            int height = mContext.getResources().getDisplayMetrics().heightPixels;
            Log.e("duang", height + ":" + windowPos[1]);
            int xOff = 20; // 可以自己调整偏移
            windowPos[0] -= xOff;
            mPopupWindow.showAtLocation(v, Gravity.TOP | Gravity.START, windowPos[0], windowPos[1]);
            if (device.isOpenVoice()) {
                opentransferimage.setVisibility(View.VISIBLE);
                closetransferimage.setVisibility(View.GONE);
            } else {
                opentransferimage.setVisibility(View.GONE);
                closetransferimage.setVisibility(View.VISIBLE);
            }
            if (isbeike|| TextUtils.isEmpty(device.getUserID())) {
                opentransfer.setVisibility(View.GONE);
                closetransfer.setVisibility(View.GONE);
                divider.setVisibility(View.GONE);
            } else {
                opentransfer.setVisibility(View.VISIBLE);
                closetransfer.setVisibility(View.VISIBLE);
                divider.setVisibility(View.VISIBLE);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.opentransfer:
                opentransferimage.setVisibility(View.VISIBLE);
                closetransferimage.setVisibility(View.GONE);
                mFavoritePoPListener.openTransfer();
                break;
            case R.id.closetransfer:
                opentransferimage.setVisibility(View.GONE);
                closetransferimage.setVisibility(View.VISIBLE);
                mFavoritePoPListener.closeTransfer();
                break;
            case R.id.deviceout:
                dismiss();
                mFavoritePoPListener.logout();
                break;
            default:
                break;
        }
    }


}
