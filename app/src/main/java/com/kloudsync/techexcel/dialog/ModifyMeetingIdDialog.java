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

public class ModifyMeetingIdDialog implements OnClickListener {
    public Context mContext;
    String currentMeetingId;
    private OnModifyClickListner modifyClickListner;

    public void setModifyClickListner(OnModifyClickListner modifyClickListner) {
        this.modifyClickListner = modifyClickListner;
    }


    public void setCurrentMeetingId(String currentMeetingId) {
        this.currentMeetingId = currentMeetingId;
        editText.setText(currentMeetingId);
    }

    private Handler handler = new Handler() {
        @SuppressLint("NewApi")
        public void handleMessage(Message msg) {

            switch (msg.what) {

            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                mPopupWindow.dismiss();
                break;
            case R.id.ok:
                String newId = editText.getText().toString().trim();
                if (TextUtils.isEmpty(newId)) {
                    Toast.makeText(mContext, "id should not be null", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (modifyClickListner != null) {
                    modifyClickListner.modifyClick(newId);
                }
                mPopupWindow.dismiss();
                break;

            default:
                break;
        }
    }


    public interface OnModifyClickListner {
        void modifyClick(String newId);
    }

    public ModifyMeetingIdDialog(Context context) {
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
    private EditText editText;
    private View popupWindow;

    @SuppressWarnings("deprecation")
    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        popupWindow = layoutInflater.inflate(R.layout.dialog_modify_meeting_id, null);
        cancel = (TextView) popupWindow.findViewById(R.id.cancel);
        ok = (TextView) popupWindow.findViewById(R.id.ok);
        editText = (EditText) popupWindow.findViewById(R.id.et_title);
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
