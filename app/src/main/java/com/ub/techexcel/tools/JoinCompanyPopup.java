package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.EventJoinMeeting;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.service.ConnectService;
import com.ub.service.activity.SocketService;

import com.ub.techexcel.bean.UpcomingLesson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by wang on 2017/9/18.
 */

public class JoinCompanyPopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public Dialog mPopupWindow;
    private View view;
    private TextView joinText;
    private EditText roomet;
    private String code;
    private TextView cancelText;
    public interface OnJoinCompanyClickedListener{
        void joinCompanyClick(String code);
    }

    private OnJoinCompanyClickedListener joinCompanyClickedListener;


    public void setJoinCompanyClickedListener(OnJoinCompanyClickedListener joinCompanyClickedListener) {
        this.joinCompanyClickedListener = joinCompanyClickedListener;
    }

    public void getPopwindow(Context context) {
        this.mContext = context;
        width = mContext.getResources().getDisplayMetrics().widthPixels;
        getPopupWindowInstance();
        mPopupWindow.getWindow().setWindowAnimations(R.style.PopupAnimation5);
    }

    public void getPopupWindowInstance() {
        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }


    @SuppressLint("WrongConstant")
    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.dialog_join_company, null);
        joinText = (TextView) view.findViewById(R.id.join);
        roomet = (EditText) view.findViewById(R.id.roomet);
        joinText.setOnClickListener(this);
        cancelText = view.findViewById(R.id.cancel);
        cancelText.setOnClickListener(this);
        mPopupWindow = new Dialog(mContext, R.style.bottom_dialog);
        mPopupWindow.setContentView(view);
        mPopupWindow.getWindow().setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = mPopupWindow.getWindow().getAttributes();
        lp.width = mContext.getResources().getDisplayMetrics().widthPixels;
        mPopupWindow.getWindow().setAttributes(lp);

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
            case R.id.join:
                if (!Tools.isFastClick()) {
                    InputMethodManager imm = (InputMethodManager)
                            mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(roomet.getWindowToken(), 0);
                    code = roomet.getText().toString();
                    if (!TextUtils.isEmpty(code)) {
//                        checkClassRoomExist(roomid);
                        if(joinCompanyClickedListener != null){
                            joinCompanyClickedListener.joinCompanyClick(code);
                        }

                    } else {
                        Toast.makeText(mContext, mContext.getString(R.string.hint_input_invite_code), Toast.LENGTH_LONG).show();
                    }
                }
                dismiss();
                break;

            case R.id.cancel:
                dismiss();
                break;

            default:
                break;
        }
    }



}
