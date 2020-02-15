package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import com.kloudsync.techexcel.R;

public class PopMeetingMore implements View.OnClickListener {
    private Context mContext;
    private PopupWindow mPopupWindow;
    private View view;
    private RelativeLayout debugLayout,muteAllLayout,unMuteAllLayout;

    public PopMeetingMore(Context context) {
        this.mContext = context;
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
        view = layoutInflater.inflate(R.layout.pop_meeting_action_more, null);
        debugLayout = (RelativeLayout) view.findViewById(R.id.layout_debug);
        muteAllLayout = (RelativeLayout) view.findViewById(R.id.layout_mute_all);
        unMuteAllLayout = (RelativeLayout) view.findViewById(R.id.layout_unmute_all);
        debugLayout.setOnClickListener(this);
        muteAllLayout.setOnClickListener(this);
        unMuteAllLayout.setOnClickListener(this);
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
    public void show(View v,OnMoreActionsListener moreActionsListener) {
        this.onMoreActionsListener = moreActionsListener;
        mPopupWindow.showAsDropDown(v, -dp2px(mContext, 220), -dp2px(mContext, 56f));
    }

    public int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public boolean isShowing() {
        if(mPopupWindow == null){
            return false;
        }
        return mPopupWindow.isShowing();
    }

    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
        mPopupWindow = null;
    }

    public interface OnMoreActionsListener {
        
        void userMuteAll();

        void userUnmuteAll();

    }

    private OnMoreActionsListener onMoreActionsListener;

    public void setOnMoreActionsListener(OnMoreActionsListener onMoreActionsListener) {
        this.onMoreActionsListener = onMoreActionsListener;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_debug:
                break;
            case R.id.layout_mute_all:
                if(this.onMoreActionsListener != null){
                    this.onMoreActionsListener.userMuteAll();
                }
                dismiss();
                break;
            case R.id.layout_unmute_all:
                if(this.onMoreActionsListener != null){
                    this.onMoreActionsListener.userUnmuteAll();
                }
                dismiss();
                break;
            default:
                break;
        }
    }

}
