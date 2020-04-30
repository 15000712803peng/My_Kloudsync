package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.config.AppConfig;

import com.kloudsync.techexcel.dialog.CenterToast;

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
    private RelativeLayout oneLayout, twoLayout, threeLayout;
    private SharedPreferences sharedPreferences;
    private MeetingConfig meetingConfig;

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
        oneLayout.setOnClickListener(this);
        twoLayout.setOnClickListener(this);
        threeLayout.setOnClickListener(this);
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

        displayMode = meetingConfig.getCameraDiplayMode();
        if (displayMode == 0) {
            twoLayout.setBackgroundColor(Color.parseColor("#9EBBFD"));
        } else if (displayMode == 1) {
            oneLayout.setBackgroundColor(Color.parseColor("#9EBBFD"));
        } else if (displayMode == 2) {
            oneLayout.setBackgroundColor(Color.parseColor("#9EBBFD"));
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
            case R.id.layout_three:
                if (onDisplayModeChanged != null) {
                    onDisplayModeChanged.onGoToVideoClicked();
                }
                dismiss();
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
