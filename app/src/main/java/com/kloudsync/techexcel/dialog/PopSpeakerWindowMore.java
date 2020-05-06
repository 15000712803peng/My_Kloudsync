package com.kloudsync.techexcel.dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.EventChangeCameraSize;
import com.kloudsync.techexcel.bean.EventKickOffMember;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.MeetingMember;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.tool.DensityUtil;

import org.greenrobot.eventbus.EventBus;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by tonyan on 2019/12/20.
 */

public class PopSpeakerWindowMore implements View.OnClickListener {

    private Context context;
    private PopupWindow mPopupWindow;

    View view;
    private TextView smallText, bigText, largeText;
    private SharedPreferences sharedPreferences;

    public interface OnSizeSettingChanged {
        void onSmallSelected();

        void onBigSelected();

        void onLargeSelected();
    }

    public  PopSpeakerWindowMore(Context context) {
        this.context = context;
        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
            return;
        } else {
            initalize();
        }
    }

    private OnSizeSettingChanged onSizeSettingChanged;



    private void initalize() {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.pop_speaker_window_more, null);
        smallText = view.findViewById(R.id.txt_small);
        bigText = view.findViewById(R.id.txt_big);
        largeText = view.findViewById(R.id.txt_large);
        smallText.setOnClickListener(this);
        bigText.setOnClickListener(this);
        largeText.setOnClickListener(this);
        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);

        initWindow();
    }

    private void initWindow() {
        mPopupWindow.setWidth(context.getResources().getDimensionPixelOffset(R.dimen.dp_160));
        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.update();

    }

    public void showAtBottom(View view) {
        sharedPreferences = context.getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        String sizeMode = sharedPreferences.getString("speaker_size_mode", "small");
        if (sizeMode.equals("small")) {
            smallText.setTextColor(Color.parseColor("#3D78FB"));
            bigText.setTextColor(Color.parseColor("#ffffff"));
            largeText.setTextColor(Color.parseColor("#ffffff"));
        } else if (sizeMode.equals("big")) {
            smallText.setTextColor(Color.parseColor("#ffffff"));
            bigText.setTextColor(Color.parseColor("#3D78FB"));
            largeText.setTextColor(Color.parseColor("#ffffff"));
        } else if (sizeMode.equals("large")) {
            smallText.setTextColor(Color.parseColor("#ffffff"));
            bigText.setTextColor(Color.parseColor("#ffffff"));
            largeText.setTextColor(Color.parseColor("#3D78FB"));
        }
//        mPopupWindow.showAtLocation(view,Gravity.RIGHT,0,0);
        mPopupWindow.showAsDropDown(view, dp2px(context, 205), -dp2px(context, 56f));

    }


    public int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public void onClick(View view) {
        String sizeMode = sharedPreferences.getString("speaker_size_mode", "small");
        switch (view.getId()) {
            case R.id.txt_small:
                if (sizeMode.equals("small")) {
                    mPopupWindow.dismiss();
                    return;
                }

                sharedPreferences.edit().putString("speaker_size_mode", "small").commit();
                if (onSizeSettingChanged != null) {
                    onSizeSettingChanged.onSmallSelected();
                }
                EventChangeCameraSize changeCameraSize  = new EventChangeCameraSize();
                changeCameraSize.setSize(0);
                EventBus.getDefault().post(changeCameraSize);
                mPopupWindow.dismiss();
                break;
            case R.id.txt_big:
                if (sizeMode.equals("big")) {
                    mPopupWindow.dismiss();
                    return;
                }

                sharedPreferences.edit().putString("speaker_size_mode", "big").commit();
                EventChangeCameraSize changeCameraSize1  = new EventChangeCameraSize();
                changeCameraSize1.setSize(1);
                EventBus.getDefault().post(changeCameraSize1);
                mPopupWindow.dismiss();
                if (onSizeSettingChanged != null) {
                    onSizeSettingChanged.onBigSelected();
                }
                mPopupWindow.dismiss();
                break;
            case R.id.txt_large:
                if (sizeMode.equals("large")) {
                    mPopupWindow.dismiss();
                    return;
                }
                EventChangeCameraSize changeCameraSize2  = new EventChangeCameraSize();
                changeCameraSize2.setSize(2);
                EventBus.getDefault().post(changeCameraSize2);
                sharedPreferences.edit().putString("speaker_size_mode", "large").commit();
                if (onSizeSettingChanged != null) {
                    onSizeSettingChanged.onLargeSelected();
                }
                mPopupWindow.dismiss();
                break;
            default:
                break;
        }
    }

    public void dismiss(){
        if(mPopupWindow != null){
            mPopupWindow.dismiss();
        }
    }


    public void setOnSizeSettingChanged(OnSizeSettingChanged onSizeSettingChanged) {
        this.onSizeSettingChanged = onSizeSettingChanged;
    }

    public boolean isShowing(){
        if(mPopupWindow  != null){
            return mPopupWindow.isShowing();
        }
        return false;
    }
}
