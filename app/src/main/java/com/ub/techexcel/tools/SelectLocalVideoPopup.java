package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;


public class SelectLocalVideoPopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public Dialog mPopupWindow;
    private View viewroot;
    private RelativeLayout as_rl_takephoto, as_rl_filephoto, as_rl_cancel, as_rl_deletephoto;
    private FavoritePoPListener mFavoritePoPListener;
    private TextView as_rl_deletephoto_line;
    private TextView cameratv, takephone;


    public interface FavoritePoPListener {

        void takeVideo();

        void filePhoto();
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

        takephone = viewroot.findViewById(R.id.takephone);
        cameratv = viewroot.findViewById(R.id.cameratv);

        takephone.setText("拍视频");
        cameratv.setText("从本地选择");

        as_rl_takephoto.setOnClickListener(this);
        as_rl_filephoto.setOnClickListener(this);
        as_rl_cancel.setOnClickListener(this);
        as_rl_deletephoto.setOnClickListener(this);

//        mPopupWindow = new PopupWindow(viewroot, ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT, false);
//        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                dismiss();
//                mFavoritePoPListener.dismiss();
//            }
//        });
//        mPopupWindow.setFocusable(true);
//        mPopupWindow.setOutsideTouchable(true);
//        mPopupWindow.setAnimationStyle(R.style.dialogwindowAnim);
//        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());


        mPopupWindow = new Dialog(mContext, R.style.my_dialog);
        mPopupWindow.setContentView(viewroot);
        mPopupWindow.getWindow().setGravity(Gravity.BOTTOM);
//        mPopupWindow.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowManager.LayoutParams params = mPopupWindow.getWindow().getAttributes();
        params.width = mContext.getResources().getDisplayMetrics().widthPixels;
//        View root = ((Activity) mActivity).getWindow().getDecorView();
//        params.height = mActivity.getResources().getDisplayMetrics().heightPixels;
        mPopupWindow.getWindow().setAttributes(params);


    }


    @SuppressLint("NewApi")
    public void StartPop(View v) {
//        mPopupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        mPopupWindow.show();
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
                mFavoritePoPListener.takeVideo();
                break;
            case R.id.as_rl_filephoto:
                dismiss();
                mFavoritePoPListener.filePhoto();
                break;
            default:
                break;
        }
    }

}
