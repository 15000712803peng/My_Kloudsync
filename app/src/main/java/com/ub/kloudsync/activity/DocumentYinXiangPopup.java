package com.ub.kloudsync.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.ub.techexcel.bean.SoundtrackBean;

/**
 * Created by wang on 2017/9/18.
 */

public class DocumentYinXiangPopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public Dialog mPopupWindow;
    private View view;

    private LinearLayout yinxiangedit;
    private LinearLayout yinxiangdelete;
    private LinearLayout yinxiangplay;
    private LinearLayout yinxiangshare;
    private TextView closebnt;
    private TextView title;


    private static FavoritePoPListener mFavoritePoPListener;

    public interface FavoritePoPListener {

        void share();

        void edit();

        void delete();

        void open();

        void  dismiss();

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
        view = layoutInflater.inflate(R.layout.yinxiangoperationpopup, null);

        yinxiangedit = (LinearLayout) view.findViewById(R.id.yinxiangedit);
        yinxiangdelete = (LinearLayout) view.findViewById(R.id.yinxiangdelete);
        yinxiangplay = (LinearLayout) view.findViewById(R.id.yinxiangplay);
        yinxiangshare = (LinearLayout) view.findViewById(R.id.yinxiangshare);
        title = (TextView) view.findViewById(R.id.title);
        closebnt = (TextView) view.findViewById(R.id.cancel);

        yinxiangedit.setOnClickListener(this);
        yinxiangdelete.setOnClickListener(this);
        yinxiangplay.setOnClickListener(this);
        yinxiangshare.setOnClickListener(this);
        closebnt.setOnClickListener(this);

        yinxiangplay.setVisibility(View.GONE);

//        mPopupWindow = new Dialog(view, ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        mPopupWindow = new Dialog(mContext, R.style.bottom_dialog);
        mPopupWindow.setContentView(view);
        mPopupWindow.getWindow().setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = mPopupWindow.getWindow().getAttributes();
        lp.width = mContext.getResources().getDisplayMetrics().widthPixels;
        mPopupWindow.getWindow().setAttributes(lp);
        mPopupWindow.getWindow().setWindowAnimations(R.style.dialogwindowAnim);

    }


    @SuppressLint("NewApi")
    public void StartPop(SoundtrackBean soundtrackBean) {
        if (mPopupWindow != null) {
//            mPopupWindow.showAsDropDown(v);
//            int windowPos[] = PopupWindowUtil.calculatePopWindowPos(v, view, 100);
            title.setText(soundtrackBean.getTitle());
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
            case R.id.yinxiangshare:
                dismiss();
                mFavoritePoPListener.share();
                break;
            case R.id.yinxiangplay:
                dismiss();
                break;
            case R.id.yinxiangdelete:
                dismiss();
                mFavoritePoPListener.delete();
                break;
            case R.id.yinxiangedit:
                dismiss();
                mFavoritePoPListener.edit();
                break;
            case R.id.cancel:
                dismiss();
                break;
            default:
                break;
        }
    }

}
