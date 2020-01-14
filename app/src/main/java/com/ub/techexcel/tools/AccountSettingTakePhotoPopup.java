package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;


public class AccountSettingTakePhotoPopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private View viewroot;
    private RelativeLayout as_rl_takephoto, as_rl_filephoto, as_rl_cancel, as_rl_deletephoto;
    private FavoritePoPListener mFavoritePoPListener;
    private TextView as_rl_deletephoto_line;


    public interface FavoritePoPListener {

        void takePhoto();

        void filePhoto();

        void fileDeletePhoto();

        void dismiss();

        void open();


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
        viewroot = layoutInflater.inflate(R.layout.account_setting_photo_popwindow, null);

        as_rl_takephoto = (RelativeLayout) viewroot.findViewById(R.id.as_rl_takephoto);
        as_rl_filephoto = (RelativeLayout) viewroot.findViewById(R.id.as_rl_filephoto);
        as_rl_deletephoto = (RelativeLayout) viewroot.findViewById(R.id.as_rl_deletephoto);
        as_rl_cancel = (RelativeLayout) viewroot.findViewById(R.id.as_rl_cancel);
        as_rl_deletephoto_line = (TextView) viewroot.findViewById(R.id.as_rl_deletephoto_line);

        as_rl_takephoto.setOnClickListener(this);
        as_rl_filephoto.setOnClickListener(this);
        as_rl_cancel.setOnClickListener(this);
        as_rl_deletephoto.setOnClickListener(this);

        mPopupWindow = new PopupWindow(viewroot, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                dismiss();
                mFavoritePoPListener.dismiss();
            }
        });
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setAnimationStyle(R.style.dialogwindowAnim);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }


    public void setVisible() {

        as_rl_deletephoto.setVisibility(View.VISIBLE);
        as_rl_deletephoto_line.setVisibility(View.VISIBLE);

    }

    @SuppressLint("NewApi")
    public void StartPop(View v) {
        mPopupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        mFavoritePoPListener.open();

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
            case R.id.as_rl_cancel:
                dismiss();
                break;
            case R.id.as_rl_takephoto:
                dismiss();
                mFavoritePoPListener.takePhoto();
                break;
            case R.id.as_rl_filephoto:
                dismiss();
                mFavoritePoPListener.filePhoto();
                break;
            case R.id.as_rl_deletephoto:
                dismiss();
                mFavoritePoPListener.fileDeletePhoto();
                break;
            default:
                break;
        }
    }

}
