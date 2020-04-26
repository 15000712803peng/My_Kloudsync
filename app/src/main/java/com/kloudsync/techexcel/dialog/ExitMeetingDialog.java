package com.kloudsync.techexcel.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;

public class ExitMeetingDialog implements OnClickListener {
    private Context mContext;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                mPopupWindow.dismiss();
                break;
            default:
                break;
        }
    }

    public boolean isShowing(){
        if(mPopupWindow != null){
            return mPopupWindow.isShowing();
        }
        return false;
    }

    public void hide(){
        if(mPopupWindow != null){
            mPopupWindow.hide();
        }
    }



    public Dialog mPopupWindow;

    public ExitMeetingDialog(Context context) {
        this.mContext = context;
        initPopuptWindow();
    }

    private TextView cancel;
    private View popupWindow;

    @SuppressWarnings("deprecation")
    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        popupWindow = layoutInflater.inflate(R.layout.dialog_exit_meeting, null);
        cancel = (TextView) popupWindow.findViewById(R.id.cancel);
        mPopupWindow = new Dialog(mContext, R.style.my_dialog);
        mPopupWindow.setContentView(popupWindow);
        WindowManager.LayoutParams lp = mPopupWindow.getWindow().getAttributes();
        lp.width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.8f);
        mPopupWindow.getWindow().setAttributes(lp);
        cancel.setOnClickListener(this);


    }

    public void show() {
        if (mPopupWindow != null) {
            mPopupWindow.show();
        }

    }


}
