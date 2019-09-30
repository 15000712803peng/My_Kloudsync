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
import com.ub.techexcel.bean.ServiceBean;

/**
 * Created by wang on 2017/9/18.
 */

public class MeetingMoreOperationPopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public Dialog mPopupWindow;
    private View viewroot;
    private RelativeLayout view, delete, startMeeting, edit;
    private FavoritePoPListener mFavoritePoPListener;
    private TextView title;
    private ImageView closebnt;
    private TextView startmeetingContext,editContext;

    public interface FavoritePoPListener {

        void delete();

        void view();

        void edit();

        void startMeeting();

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
        viewroot = layoutInflater.inflate(R.layout.meetingmorepopup, null);
        view = (RelativeLayout) viewroot.findViewById(R.id.view);
        delete = (RelativeLayout) viewroot.findViewById(R.id.delete);
        startMeeting = (RelativeLayout) viewroot.findViewById(R.id.satrtmeeting);
        edit = (RelativeLayout) viewroot.findViewById(R.id.editmeeting);
        startmeetingContext=(TextView) viewroot.findViewById(R.id.startmeetingcontext);
        editContext=(TextView) viewroot.findViewById(R.id.editcontext);;
        title = (TextView) viewroot.findViewById(R.id.title);
        closebnt = (ImageView) viewroot.findViewById(R.id.closebnt);
        closebnt.setOnClickListener(this);
        view.setOnClickListener(this);
        delete.setOnClickListener(this);
        startMeeting.setOnClickListener(this);
        edit.setOnClickListener(this);
        mPopupWindow = new Dialog(mContext, R.style.bottom_dialog);

        mPopupWindow.setContentView(viewroot);
        mPopupWindow.getWindow().setWindowAnimations(R.style.dialogwindowAnim);
        mPopupWindow.getWindow().setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams params = mPopupWindow.getWindow().getAttributes();
        params.width = mContext.getResources().getDisplayMetrics().widthPixels;
        mPopupWindow.getWindow().setAttributes(params);
    }

    @SuppressLint("NewApi")
    public void StartPop(View v, ServiceBean syncRoomBean,int isShow) {
        if (mPopupWindow != null) {
            title.setText(syncRoomBean.getName()+"");
            if(isShow==0){
                startmeetingContext.setText(mContext.getResources().getString(R.string.sMeeting));
                editContext.setText(mContext.getResources().getString(R.string.Edit));
                startMeeting.setVisibility(View.VISIBLE);
                edit.setVisibility(View.VISIBLE);
            }else if(isShow==1){
                startMeeting.setVisibility(View.GONE);
                edit.setVisibility(View.GONE);

            } else if (isShow==2) {
                startmeetingContext.setText(mContext.getResources().getString(R.string.sProperty));
                editContext.setText(mContext.getResources().getString(R.string.share));
                startMeeting.setVisibility(View.VISIBLE);
                edit.setVisibility(View.VISIBLE);
            }

            mPopupWindow.show();
            mFavoritePoPListener.open();
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
            case R.id.closebnt:
                dismiss();
                break;
            case R.id.view:
                dismiss();
                mFavoritePoPListener.view();
                break;
            case R.id.delete:
                dismiss();
                mFavoritePoPListener.delete();
                break;
            case R.id.satrtmeeting:
                dismiss();
                mFavoritePoPListener.startMeeting();
                break;
            case R.id.editmeeting:
                dismiss();
                mFavoritePoPListener.edit();
                break;
            default:
                break;
        }
    }

}
