package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.kloudsync.techexcel.R;

public class PopMeetingMore implements View.OnClickListener {

    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private View view;
    private RelativeLayout debug, muteall, unmuteall, docrecord, startrecord;



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
        view = layoutInflater.inflate(R.layout.moreaction_popup, null);
        debug = (RelativeLayout) view.findViewById(R.id.debug);
        muteall = (RelativeLayout) view.findViewById(R.id.muteall);
        docrecord = (RelativeLayout) view.findViewById(R.id.docrecord);
        startrecord = (RelativeLayout) view.findViewById(R.id.startrecord);
        unmuteall = (RelativeLayout) view.findViewById(R.id.unmuteall);
        debug.setOnClickListener(this);
        muteall.setOnClickListener(this);
        unmuteall.setOnClickListener(this);
        docrecord.setOnClickListener(this);
        startrecord.setOnClickListener(this);
        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                dismiss();
            }
        });
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setOutsideTouchable(true);

    }


    @SuppressLint("NewApi")
    public void show(View v) {
//        this.ishavepresenter = ishavepresenter;
//        if (mPopupWindow != null) {
//            if (ishavepresenter) {
//                  // (145/2+35/2)
//            } else {
//                unmuteall.setVisibility(View.GONE);
//                muteall.setVisibility(View.GONE);
//                docrecord.setVisibility(View.GONE);
//                mPopupWindow.showAsDropDown(v, -dp2px(mContext, 220), -dp2px(mContext, 40));  // (45/2+35/2)
//            }
//        }
        unmuteall.setVisibility(View.VISIBLE);
        muteall.setVisibility(View.VISIBLE);
        docrecord.setVisibility(View.VISIBLE);
        mPopupWindow.showAsDropDown(v, -dp2px(mContext, 220), -dp2px(mContext, 92.5f));
    }

    public int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    public boolean isShowing() {
        return mPopupWindow.isShowing();
    }

    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }


    public interface InvitePopupListener {
        
        void mute();

        void unmute();

        void debug();

        void docrecord();

    }

    public void setInvitePopupListener(InvitePopupListener invitePopupListener) {
        this.invitePopupListener = invitePopupListener;
    }

    private InvitePopupListener invitePopupListener;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.debug:
                invitePopupListener.debug();
                dismiss();
                break;
            case R.id.muteall:
                invitePopupListener.mute();
                dismiss();
                break;
            case R.id.unmuteall:
                invitePopupListener.unmute();
                dismiss();
                break;
            case R.id.docrecord:
                invitePopupListener.docrecord();
                dismiss();
                break;
            default:
                break;
        }
    }

}
