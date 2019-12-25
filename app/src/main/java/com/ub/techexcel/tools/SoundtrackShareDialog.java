package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.SoundTrack;

/**
 * Created by wang on 2017/9/18.
 */

public class SoundtrackShareDialog implements View.OnClickListener {

    public Context mContext;
    public int width;
    public Dialog mDialog;
    private View view;
    private LinearLayout wechat,app,link;
    private SoundTrack soundTrack;


    public interface OnSoundtrackShareOptionsClickListener {

        void wechatShareSoundtrack(SoundTrack soundTrack);

        void appShareSoundtrack(SoundTrack soundTrack);

        void copySoundtrackLink(SoundTrack soundTrack);
    }

    private OnSoundtrackShareOptionsClickListener soundtrackShareOptionsClickListener;


    public void setSoundtrackShareOptionsClickListener(OnSoundtrackShareOptionsClickListener soundtrackShareOptionsClickListener) {
        this.soundtrackShareOptionsClickListener = soundtrackShareOptionsClickListener;
    }

    public SoundtrackShareDialog(Context context){
        mContext = context;
        initPopuptWindow();
    }

    @SuppressLint("WrongConstant")
    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.pop_soundtrack_share, null);
        wechat=view.findViewById(R.id.wechat);
        app=view.findViewById(R.id.app);
        link=view.findViewById(R.id.link);
        wechat.setOnClickListener(this);
        app.setOnClickListener(this);
        link.setOnClickListener(this);
        mDialog = new Dialog(mContext, R.style.my_dialog);
        mDialog.setContentView(view);
        mDialog.getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
        lp.width = mContext.getResources().getDisplayMetrics().widthPixels * 3 / 5;
        mDialog.getWindow().setAttributes(lp);
    }

    @SuppressLint("NewApi")
    public void show(SoundTrack soundTrack) {
        this.soundTrack = soundTrack;
        if (mDialog != null) {
            mDialog.show();
        }
    }


    public boolean isShowing() {
        if(mDialog != null){
            return mDialog.isShowing();
        }
        return false;
    }

    public void dismiss() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.wechat:
                if(soundtrackShareOptionsClickListener != null){
                    soundtrackShareOptionsClickListener.wechatShareSoundtrack(soundTrack);
                }
                dismiss();
                break;
            case R.id.app:
                if(soundtrackShareOptionsClickListener != null){
                    soundtrackShareOptionsClickListener.appShareSoundtrack(soundTrack);
                }
                dismiss();
                break;
            case R.id.link:
                if(soundtrackShareOptionsClickListener != null){
                    soundtrackShareOptionsClickListener.copySoundtrackLink(soundTrack);
                }
                dismiss();
                break;
            default:
                break;
        }
    }


}
