package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.GroupAdapter;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.start.LoginGet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 2017/9/18.
 */

public class SharedInAppPopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private View view;
    private ImageView cancel;
    private TextView send,cancel2;

    private static FavoritePoPListener mFavoritePoPListener;

    public interface FavoritePoPListener {

        void select(List<Customer> list);

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
        view = layoutInflater.inflate(R.layout.shareinapppopup, null);
        cancel = (ImageView) view.findViewById(R.id.cancel);
        cancel2 = (TextView) view.findViewById(R.id.cancel2);
        send = (TextView) view.findViewById(R.id.send);
        cancel.setOnClickListener(this);
        send.setOnClickListener(this);
        cancel2.setOnClickListener(this);
        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                dismiss();
            }
        });
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }




    @SuppressLint("NewApi")
    public void StartPop(View v) {
        if (mPopupWindow != null) {
            mPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
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
            case R.id.cancel:
                dismiss();
                break;
            case R.id.cancel2:
                dismiss();
                break;
            case R.id.send:
                dismiss();
                break;
            default:
                break;
        }
    }

}
