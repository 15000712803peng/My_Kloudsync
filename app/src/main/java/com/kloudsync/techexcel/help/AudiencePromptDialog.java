package com.kloudsync.techexcel.help;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.tool.NetWorkHelp;
import com.ub.kloudsync.activity.Document;

import org.json.JSONObject;

public class AudiencePromptDialog implements OnClickListener{

    private Context mContext;
    private TextView cancelText;

    public AudiencePromptDialog(Context context) {
        this.mContext = context;
        getPopupWindowInstance();
        dialog.getWindow().setWindowAnimations(R.style.PopupAnimation3);
    }

    public Dialog dialog;

    public void getPopupWindowInstance() {
        if (null != dialog) {
            dialog.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }
    private View popupWindow;
    @SuppressWarnings("deprecation")
    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        popupWindow = layoutInflater.inflate(R.layout.dialog_role_audience, null);
        cancelText = popupWindow.findViewById(R.id.tv_yes);
        cancelText.setOnClickListener(this);
        dialog = new Dialog(mContext, R.style.my_dialog);
        dialog.setContentView(popupWindow);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.5f);
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_yes:
                dialog.dismiss();
                break;
        }
    }

    public boolean isShowing(){
        if(dialog != null){
            return dialog.isShowing();
        }
        return false;
    }

    public void cancel(){
        if(dialog != null){
            dialog.cancel();
        }
    }

    public void show() {
        if(dialog != null && !dialog.isShowing())
            dialog.show();
    }


}
