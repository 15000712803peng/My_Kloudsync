package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.SoundTrack;
import com.kloudsync.techexcel.tool.PopupWindowUtil;
import com.ub.techexcel.bean.SoundtrackBean;

/**
 * Created by wang on 2017/9/18.
 */

public class PopSoundtrackOperations implements View.OnClickListener {


    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private View view;

    LinearLayout yinxiangedit;
    LinearLayout yinxiangdelete;
    LinearLayout yinxiangplay;
    LinearLayout sharemore;

    private SoundTrack soundTrack;

    private  OnSoundtrackOperationListener soundtrackOperationListener;

    public interface OnSoundtrackOperationListener {

        void editSoundTrack(SoundTrack soundTrack);

        void deleteSoundTrack(SoundTrack soundTrack);

        void playSoundTrack(SoundTrack soundTrack);


        void sharePopup(SoundTrack soundTrack);
    }

    public void setSoundtrackOperationListener(OnSoundtrackOperationListener soundtrackOperationListener) {
        this.soundtrackOperationListener = soundtrackOperationListener;
    }

    public  PopSoundtrackOperations(Context context) {
        this.mContext = context;
        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }


    public void initPopuptWindow() {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.pop_soundtrack_operations, null);

        yinxiangedit = (LinearLayout) view.findViewById(R.id.yinxiangedit);
        yinxiangdelete = (LinearLayout) view.findViewById(R.id.yinxiangdelete);
        yinxiangplay = (LinearLayout) view.findViewById(R.id.yinxiangplay);
        sharemore = (LinearLayout) view.findViewById(R.id.sharemore);
        yinxiangedit.setOnClickListener(this);
        sharemore.setOnClickListener(this);
        yinxiangdelete.setOnClickListener(this);
        yinxiangplay.setOnClickListener(this);
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
    public void show(View v, SoundTrack soundTrack) {
        this.soundTrack = soundTrack;
        if (mPopupWindow != null) {
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
                if(soundtrackOperationListener != null){
                    soundtrackOperationListener.editSoundTrack(soundTrack);
                }
                break;
            case R.id.yinxiangdelete:
                dismiss();
                if(soundtrackOperationListener != null){
                    soundtrackOperationListener.deleteSoundTrack(soundTrack);
                }
                break;
            case R.id.yinxiangplay:
                dismiss();
                if(soundtrackOperationListener != null){
                    soundtrackOperationListener.playSoundTrack(soundTrack);
                }
                break;

            case R.id.sharemore:
                dismiss();
                if(soundtrackOperationListener != null){
                    soundtrackOperationListener.sharePopup(soundTrack);
                }

                break;
            default:
                break;
        }
    }
}
