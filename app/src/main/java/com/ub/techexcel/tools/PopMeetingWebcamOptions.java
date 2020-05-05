package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.EventSelectSpeakerMode;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.config.AppConfig;

import com.kloudsync.techexcel.dialog.CenterToast;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class PopMeetingWebcamOptions implements View.OnClickListener {
    private Context mContext;
    private PopupWindow mPopupWindow;
    private View view;
    private RelativeLayout oneLayout, twoLayout, threeLayout, fourLayout;
    private RelativeLayout hideAllLayout;
    private ImageView oneImage, twoImage, fourImage;
    private SharedPreferences sharedPreferences;
    private MeetingConfig meetingConfig;
    private TextView videoSizeText;

    public interface OnDisplayModeChanged {
        void displayModeChanged(int mode);

        void onGoToVideoClicked();
    }

    private OnDisplayModeChanged onDisplayModeChanged;

    public void setOnDisplayModeChanged(OnDisplayModeChanged onDisplayModeChanged) {
        this.onDisplayModeChanged = onDisplayModeChanged;
    }

    public PopMeetingWebcamOptions(Context context) {
        this.mContext = context;
        sharedPreferences = mContext.getSharedPreferences(AppConfig.LOGININFO, Context.MODE_PRIVATE);
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
        view = layoutInflater.inflate(R.layout.pop_meeting_webcam_options, null);
        oneLayout = (RelativeLayout) view.findViewById(R.id.layout_one);
        twoLayout = (RelativeLayout) view.findViewById(R.id.layout_two);
        threeLayout = (RelativeLayout) view.findViewById(R.id.layout_three);
        fourLayout = (RelativeLayout) view.findViewById(R.id.layout_four);
        hideAllLayout = (RelativeLayout) view.findViewById(R.id.layout_hide);
        videoSizeText = view.findViewById(R.id.txt_video_size);
        oneImage = view.findViewById(R.id.image_option_one);
        hideAllLayout.setOnClickListener(this);
        twoImage = view.findViewById(R.id.image_option_two);
        fourImage = view.findViewById(R.id.image_option_four);
        oneLayout.setOnClickListener(this);
        twoLayout.setOnClickListener(this);
        threeLayout.setOnClickListener(this);
        fourLayout.setOnClickListener(this);
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


    int displayMode;

    @SuppressLint("NewApi")
    public void show(View v, MeetingConfig meetingConfig) {
        this.meetingConfig = meetingConfig;
        if (meetingConfig.getPresenterId().equals(AppConfig.UserID)) {
            threeLayout.setVisibility(View.VISIBLE);
        } else {
            threeLayout.setVisibility(View.GONE);
        }

        if(meetingConfig.getCameraDiplayMode() == 0){
            hideAllLayout.setVisibility(View.VISIBLE);
        }else {
            hideAllLayout.setVisibility(View.GONE);
        }

//        if (meetingConfig.getCameraDiplayMode() == 0) {
//            fourLayout.setVisibility(View.VISIBLE);
//        } else {
//            fourLayout.setVisibility(View.GONE);
//        }

        displayMode = meetingConfig.getCameraDiplayMode();
        if (displayMode == 0) {
            twoImage.setImageResource(R.drawable.icon_webcan_options_two_selected);
        } else if (displayMode == 1) {
            oneImage.setImageResource(R.drawable.icon_webcan_options_one_selected);
        } else if (displayMode == 2) {
            oneImage.setImageResource(R.drawable.icon_webcan_options_one_selected);
        }
        String sizeMode = sharedPreferences.getString("speaker_size_mode", "small");
        if(sizeMode.equals("small")){
            videoSizeText.setText(mContext.getString(R.string.video_size) + "-S");
        }else if(sizeMode.equals("big")){
            videoSizeText.setText(mContext.getString(R.string.video_size) + "-M");
        }else if(sizeMode.equals("large")){
            videoSizeText.setText(mContext.getString(R.string.video_size) + "-L");
        }
        mPopupWindow.showAsDropDown(v, 0, dp2px(mContext, 5));
    }

    public int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public boolean isShowing() {
        if (mPopupWindow == null) {
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


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_one:
                if (displayMode == 2) {
                    dismiss();
                    return;
                }

                meetingConfig.setCameraDiplayMode(2);
                if (onDisplayModeChanged != null) {
                    onDisplayModeChanged.displayModeChanged(2);
                }


                dismiss();
                break;
            case R.id.layout_two:
                if (displayMode == 0) {
                    dismiss();
                    return;
                }


                meetingConfig.setCameraDiplayMode(0);
                if (onDisplayModeChanged != null) {
                    onDisplayModeChanged.displayModeChanged(0);
                }

                dismiss();
                break;

            case R.id.layout_hide:
                if (displayMode == 1) {
                    dismiss();
                    return;
                }

                meetingConfig.setCameraDiplayMode(1);
                if (onDisplayModeChanged != null) {
                    onDisplayModeChanged.displayModeChanged(1);
                }

                dismiss();
                break;
            case R.id.layout_three:
                if (onDisplayModeChanged != null) {
                    onDisplayModeChanged.onGoToVideoClicked();
                }
                dismiss();
                break;
            case R.id.layout_four:
                fourImage.setImageResource(R.drawable.icon_webcan_options_four_selected);
                EventSelectSpeakerMode selectSpeakerMode = new EventSelectSpeakerMode();
                selectSpeakerMode.setContainer(fourLayout);
                EventBus.getDefault().post(selectSpeakerMode);
                break;
            default:
                break;
        }
    }

    private void requestChangeDisplayMode(final int mode) {
        Observable.just("change_mode").observeOn(Schedulers.io()).map(new Function<String, JSONObject>() {
            @Override
            public JSONObject apply(String s) throws Exception {
                return ServiceInterfaceTools.getinstance().syncChangeCameraDisplayMode(mode);
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject jsonObject) throws Exception {
                if (jsonObject.has("code")) {
//                    if (jsonObject.getInt("code") == 0) {
//                        sharedPreferences.edit().putInt("display_mode", mode).commit();
//                        if (onDisplayModeChanged != null) {
//                            onDisplayModeChanged.displayModeChanged(mode);
//                        }
//                        dismiss();
//                    }
                }
            }
        }).subscribe();
    }

}
