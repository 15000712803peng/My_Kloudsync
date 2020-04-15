package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.SoundTrack;
import com.kloudsync.techexcel.tool.PopupWindowUtil;

/**
 * Created by wang on 2017/9/18.
 */

public class AccompanyMoreOperations implements View.OnClickListener {


    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private View view;

    LinearLayout syncll;
    LinearLayout syncllnoaction;
    LinearLayout deletell;
    LinearLayout operation;
    LinearLayout cancelll;
    private TextView synccontent;


    private  OnSoundtrackOperationListener soundtrackOperationListener;

    public interface OnSoundtrackOperationListener {

        void syncAccompany();

        void sync();

        void delete();

        void cancel();

        void cancelNewVoice();

    }

    public void setSoundtrackOperationListener(OnSoundtrackOperationListener soundtrackOperationListener) {
        this.soundtrackOperationListener = soundtrackOperationListener;
    }

    public AccompanyMoreOperations(Context context) {
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
        view = layoutInflater.inflate(R.layout.accompany_operations, null);

        syncll = (LinearLayout) view.findViewById(R.id.syncll);
        syncllnoaction = (LinearLayout) view.findViewById(R.id.syncllnoaction);
        deletell = (LinearLayout) view.findViewById(R.id.deletell);
        operation = (LinearLayout) view.findViewById(R.id.operation);
        cancelll = (LinearLayout) view.findViewById(R.id.cancelll);
        synccontent =  view.findViewById(R.id.synccontent);
        syncll.setOnClickListener(this);
        deletell.setOnClickListener(this);
        syncllnoaction.setOnClickListener(this);
        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);

    }


    @SuppressLint("NewApi")
    public void show(View v, int isAccompanyOrMusic,int selectAccompanyType) {
        if (mPopupWindow != null) {
            if(isAccompanyOrMusic==1){
                cancelll.setVisibility(View.GONE);
                operation.setVisibility(View.VISIBLE);
            }else{
                cancelll.setVisibility(View.VISIBLE);
                operation.setVisibility(View.GONE);
                cancelll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                        if(soundtrackOperationListener != null){
                            soundtrackOperationListener.cancel();
                        }
                    }
                });
            }
            if(selectAccompanyType==0){ //伴奏音乐
                synccontent.setText("制作同步伴奏带");
                syncllnoaction.setVisibility(View.VISIBLE);
            }else{  //伴奏音想
                synccontent.setText("重新同步伴奏带");
                syncllnoaction.setVisibility(View.GONE);
            }
            mPopupWindow.showAsDropDown(v);
        }
    }
    @SuppressLint("NewApi")
    public void showNewVoice(View v) {
        if (mPopupWindow != null) {
            cancelll.setVisibility(View.VISIBLE);
            cancelll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                    if(soundtrackOperationListener != null){
                        soundtrackOperationListener.cancelNewVoice();
                    }
                }
            });
            operation.setVisibility(View.GONE);
            mPopupWindow.showAsDropDown(v);
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
            case R.id.syncll:
                dismiss();
                if(soundtrackOperationListener != null){
                   soundtrackOperationListener.syncAccompany();
                }
                break;
            case R.id.syncllnoaction:
                dismiss();
                if(soundtrackOperationListener != null){
                   soundtrackOperationListener.sync();
                }
                break;
            case R.id.deletell:
                dismiss();
                if(soundtrackOperationListener != null){
                    soundtrackOperationListener.delete();
                }
                break;
            case R.id.cancelll:
                dismiss();
                if(soundtrackOperationListener != null){
                    soundtrackOperationListener.cancel();
                }
                break;
            default:
                break;
        }
    }
}
