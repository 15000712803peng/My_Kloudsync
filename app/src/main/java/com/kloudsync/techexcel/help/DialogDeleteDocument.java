package com.kloudsync.techexcel.help;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.kloudsync.techexcel.R;

public class DialogDeleteDocument {
    private Dialog dlgGetWindow = null;// 对话框
    private Window window;
    private TextView txt_cancel;
    private TextView txt_delete;
    private Context mContext;
    private DialogDelDocListener delDocListener;

    public interface DialogDelDocListener {
        void delDoc();
    }

    public void setDelDocListener(DialogDelDocListener delDocListener) {
        this.delDocListener = delDocListener;
    }

    public void EditCancel(Context context) {
        this.mContext = context;
        dlgGetWindow = new Dialog(mContext, R.style.bottom_dialog);
        window = dlgGetWindow.getWindow();
        window.setWindowAnimations(R.style.PopupAnimation5);
        window.setContentView(R.layout.dialog_delete_document);
        txt_cancel = window.findViewById(R.id.tv_cancel);
        txt_delete = window.findViewById(R.id.tv_delete);
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams layoutParams = dlgGetWindow.getWindow()
                .getAttributes();
        layoutParams.width = mContext.getResources().getDisplayMetrics().widthPixels;
        dlgGetWindow.getWindow().setAttributes(layoutParams);
        txt_cancel.setOnClickListener(new MyOnClick());
        txt_delete.setOnClickListener(new MyOnClick());
        dlgGetWindow.show();
    }

    private class MyOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_delete:
                    if (delDocListener != null) {
                        delDocListener.delDoc();
                        dlgGetWindow.dismiss();
                    }
                    break;
                case R.id.tv_cancel:
                    dlgGetWindow.dismiss();
                    break;

                default:
                    break;
            }

        }

    }

}
