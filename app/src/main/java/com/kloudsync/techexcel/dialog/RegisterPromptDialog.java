package com.kloudsync.techexcel.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.start.RegisterActivity;
import com.kloudsync.techexcel.start.RegisterActivityStepOne;

public class RegisterPromptDialog implements OnClickListener {

    public Context mContext;


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                mPopupWindow.dismiss();
                break;
            case R.id.ok:
                Intent intent=new Intent(mContext, RegisterActivityStepOne.class);
                mContext.startActivity(intent);
                mPopupWindow.dismiss();
                break;
            default:
                break;
        }
    }



    public RegisterPromptDialog(Context context) {
        this.mContext = context;
        getPopupWindowInstance();
        mPopupWindow.getWindow().setWindowAnimations(R.style.PopupAnimation3);
    }

    public Dialog mPopupWindow;

    public void getPopupWindowInstance() {
        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }

    private TextView cancel, ok;
    private View popupWindow;



    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        popupWindow = layoutInflater.inflate(R.layout.registerpromptdialog, null);
        cancel = (TextView) popupWindow.findViewById(R.id.cancel);
        ok = (TextView) popupWindow.findViewById(R.id.ok);
        mPopupWindow = new Dialog(mContext, R.style.my_dialog);
        mPopupWindow.setContentView(popupWindow);
        WindowManager.LayoutParams lp = mPopupWindow.getWindow().getAttributes();
        lp.width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.8f);
        mPopupWindow.getWindow().setAttributes(lp);
        cancel.setOnClickListener(this);
        ok.setOnClickListener(this);

    }

    public void show() {
        if (mPopupWindow != null) {
            mPopupWindow.show();
        }
    }


}
