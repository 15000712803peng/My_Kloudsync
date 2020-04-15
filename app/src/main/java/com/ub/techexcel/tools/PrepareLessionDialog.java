package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.ub.kloudsync.activity.Document;
import com.ub.techexcel.bean.SoundtrackBean;

import org.feezu.liuli.timeselector.Utils.TextUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class PrepareLessionDialog implements View.OnClickListener {

    public Context mContext;
    public int width;
    public Dialog mPopupWindow;
    private View view;
    ImageView closeImage;
    TextView prepareText, tryText, startText;

    public interface OnPreparedOptionsListener {
        void onPreparedClosed();

        void onPreparedLession();

        void onTryLession();

        void onStartLession();
    }

    private OnPreparedOptionsListener onPreparedOptionsListener;


    public void setOnPreparedOptionsListener(OnPreparedOptionsListener onPreparedOptionsListener) {
        this.onPreparedOptionsListener = onPreparedOptionsListener;
    }

    public PrepareLessionDialog(Context context) {
        this.mContext = context;
        width = mContext.getResources().getDisplayMetrics().widthPixels;
        getPopupWindowInstance();
    }

    public void getPopupWindowInstance() {
        if (null != mPopupWindow) {
            mPopupWindow.cancel();
            return;
        } else {
            initPopuptWindow();
        }
    }


    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.dialog_course_prepare, null);
//        recordsync.setText("Sync");
        mPopupWindow = new Dialog(mContext, R.style.my_dialog);
        mPopupWindow.setContentView(view);
        closeImage = view.findViewById(R.id.image_close);
        prepareText = view.findViewById(R.id.txt_prepare);
        tryText = view.findViewById(R.id.txt_try);
        startText = view.findViewById(R.id.txt_start);
        closeImage.setOnClickListener(this);
        prepareText.setOnClickListener(this);
        tryText.setOnClickListener(this);
        startText.setOnClickListener(this);
        mPopupWindow.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowManager.LayoutParams params = mPopupWindow.getWindow().getAttributes();
        if (Tools.isOrientationPortrait((Activity) mContext)) {
            params.width = mContext.getResources().getDisplayMetrics().widthPixels * 5 / 6;
            View root = ((Activity) mContext).getWindow().getDecorView();
            params.height = mContext.getResources().getDisplayMetrics().heightPixels * 1 / 2;
        } else {
            params.width = mContext.getResources().getDisplayMetrics().widthPixels * 3 / 5;
            View root = ((Activity) mContext).getWindow().getDecorView();
            params.height = mContext.getResources().getDisplayMetrics().heightPixels * 4 / 5 + 60;
        }

        mPopupWindow.setCancelable(false);
        mPopupWindow.getWindow().setAttributes(params);

    }


    @SuppressLint("NewApi")
    public void show() {
        if (mPopupWindow != null) {
            mPopupWindow.show();
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
            case R.id.image_close:

                if (onPreparedOptionsListener != null) {
                    onPreparedOptionsListener.onPreparedClosed();
                }
                dismiss();
                break;
            case R.id.txt_prepare:
                dismiss();
                if (onPreparedOptionsListener != null) {
                    onPreparedOptionsListener.onPreparedLession();
                }
                break;
            case R.id.txt_try:

                if (onPreparedOptionsListener != null) {
                    onPreparedOptionsListener.onTryLession();
                }
                dismiss();
                break;
            case R.id.txt_start:
                if (onPreparedOptionsListener != null) {
                    onPreparedOptionsListener.onStartLession();
                }
                dismiss();
                break;
            default:
                break;
        }
    }

    SelectAudioForCreatingSyncDialog selectAudioDialog;

    private void showSelectAudioDialog() {
        if (selectAudioDialog != null) {
            selectAudioDialog.cancel();
        }
        selectAudioDialog = new SelectAudioForCreatingSyncDialog(mContext);
        selectAudioDialog.show();
    }


}
