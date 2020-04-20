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

public class PopSpeakerWindowMore extends PopupWindow implements View.OnClickListener {

    private Context context;
    View view;
    private TextView smallText, bigText, largeText;
    private SharedPreferences sharedPreferences;

    public interface OnSizeSettingChanged {
        void onSmallSelected();

        void onBigSelected();

        void onLargeSelected();
    }

    private OnSizeSettingChanged onSizeSettingChanged;


    public PopSpeakerWindowMore(Context context) {
        super(context);
        this.context = context;
        initalize();
    }

    private void initalize() {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.pop_speaker_window_more, null);
        smallText = view.findViewById(R.id.txt_small);
        bigText = view.findViewById(R.id.txt_big);
        largeText = view.findViewById(R.id.txt_large);
        smallText.setOnClickListener(this);
        bigText.setOnClickListener(this);
        largeText.setOnClickListener(this);
        setContentView(view);
        initWindow();
    }

    private void initWindow() {
        this.setWidth(context.getResources().getDimensionPixelOffset(R.dimen.dp_160));
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        this.update();

    }

    public void showAtBottom(View view) {
        sharedPreferences = context.getSharedPreferences(AppConfig.LOGININFO,
                MODE_PRIVATE);
        String sizeMode = sharedPreferences.getString("speaker_size_mode", "small");
        if (sizeMode.equals("small")) {
            smallText.setTextColor(Color.parseColor("#3D78FB"));
            bigText.setTextColor(Color.parseColor("#999999"));
            largeText.setTextColor(Color.parseColor("#999999"));
        } else if (sizeMode.equals("big")) {
            smallText.setTextColor(Color.parseColor("#999999"));
            bigText.setTextColor(Color.parseColor("#3D78FB"));
            largeText.setTextColor(Color.parseColor("#999999"));
        } else if (sizeMode.equals("large")) {
            smallText.setTextColor(Color.parseColor("#999999"));
            bigText.setTextColor(Color.parseColor("#999999"));
            largeText.setTextColor(Color.parseColor("#3D78FB"));
        }
        showAsDropDown(view, -12, 25);
    }

    @Override
    public void onClick(View view) {
        String sizeMode = sharedPreferences.getString("speaker_size_mode", "small");
        switch (view.getId()) {
            case R.id.txt_small:
                if (sizeMode.equals("small")) {
                    dismiss();
                    return;
                }

                sharedPreferences.edit().putString("speaker_size_mode", "small").commit();
                if (onSizeSettingChanged != null) {
                    onSizeSettingChanged.onSmallSelected();
                }
                dismiss();
                break;
            case R.id.txt_big:
                if (sizeMode.equals("big")) {
                    dismiss();
                    return;
                }
                sharedPreferences.edit().putString("speaker_size_mode", "big").commit();
                if (onSizeSettingChanged != null) {
                    onSizeSettingChanged.onBigSelected();
                }
                dismiss();
                break;
            case R.id.txt_large:
                if (sizeMode.equals("large")) {
                    dismiss();
                    return;
                }
                sharedPreferences.edit().putString("speaker_size_mode", "large").commit();
                if (onSizeSettingChanged != null) {
                    onSizeSettingChanged.onLargeSelected();
                }
                dismiss();
                break;
            default:
                break;
        }
    }


    public void setOnSizeSettingChanged(OnSizeSettingChanged onSizeSettingChanged) {
        this.onSizeSettingChanged = onSizeSettingChanged;
    }
}
