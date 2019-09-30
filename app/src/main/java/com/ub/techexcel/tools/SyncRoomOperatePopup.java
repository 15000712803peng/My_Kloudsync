package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.ub.techexcel.bean.SyncRoomBean;

/**
 * Created by wang on 2017/9/18.
 */

public class SyncRoomOperatePopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public Dialog mPopupWindow;
    private View view;
    private RelativeLayout vieww, delete, move;
    private static FavoritePoPListener mFavoritePoPListener;
    private TextView title;
    private ImageView closebnt;

    public interface FavoritePoPListener {

        void delete();

        void view();

        void move();

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
        view = layoutInflater.inflate(R.layout.syncroomoperatepopup, null);
        vieww = (RelativeLayout) view.findViewById(R.id.view);
        delete = (RelativeLayout) view.findViewById(R.id.delete);
        title = (TextView) view.findViewById(R.id.title);
        closebnt = (ImageView) view.findViewById(R.id.closebnt);
        move = (RelativeLayout) view.findViewById(R.id.move);
        vieww.setOnClickListener(this);
        closebnt.setOnClickListener(this);
        delete.setOnClickListener(this);
        move.setOnClickListener(this);
        mPopupWindow = new Dialog(mContext, R.style.bottom_dialog);
        mPopupWindow.setContentView(view);
        mPopupWindow.getWindow().setWindowAnimations(R.style.dialogwindowAnim);
        mPopupWindow.getWindow().setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = mPopupWindow.getWindow().getAttributes();
        lp.width = mContext.getResources().getDisplayMetrics().widthPixels;
        mPopupWindow.getWindow().setAttributes(lp);
    }


    @SuppressLint("NewApi")
    public void StartPop(View v, SyncRoomBean syncRoomBean) {
        if (mPopupWindow != null) {
//            mPopupWindow.showAsDropDown(v);
            title.setText(syncRoomBean.getName());
            mPopupWindow.show();
            mFavoritePoPListener.open();
        }
    }

    public boolean isShowing() {
        return mPopupWindow.isShowing();
    }

    public void dismiss() {
        if (mPopupWindow != null) {
            mFavoritePoPListener.dismiss();
            mPopupWindow.dismiss();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.view:
                dismiss();
                mFavoritePoPListener.view();
                break;
            case R.id.delete:
                dismiss();
                mFavoritePoPListener.delete();
                break;
            case R.id.move:
                dismiss();
                mFavoritePoPListener.move();
                break;
            case R.id.closebnt:
                dismiss();
                break;
            default:
                break;
        }
    }

}
