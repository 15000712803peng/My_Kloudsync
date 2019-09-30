package com.kloudsync.techexcel.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.view.RoundProgressBar;

public class UploadFileDialog {
    public Context mContext;
    private PopDismissListener popDismissListener;

    public interface PopDismissListener {
        void PopDismiss();
    }

    public void setPoPDismissListener(PopDismissListener popDismissListener) {
        this.popDismissListener = popDismissListener;
    }


    private static PopCancelListener popCancelListener;

    public interface PopCancelListener {

        void Cancel();
    }

    public void setPopCancelListener(PopCancelListener popCancelListener) {
        this.popCancelListener = popCancelListener;
    }

    public UploadFileDialog(Context context) {
        this.mContext = context;
        getPopupWindowInstance();
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

    private TextView tv_name, cancelText;
    private TextView tv_title;
    private RoundProgressBar rpb_update;

    @SuppressWarnings("deprecation")
    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.dialog_update_doc, null);
        cancelText = (TextView) view.findViewById(R.id.txt_cancel);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        rpb_update = (RoundProgressBar) view.findViewById(R.id.rpb_update);
        rpb_update.setProgress(0);
        dialog = new Dialog(mContext, R.style.my_dialog);
        dialog.setContentView(view);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog = null;
            }
        });
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = (mContext.getResources().getDisplayMetrics().widthPixels) * 3 / 4;
        cancelText.setOnClickListener(new myOnClick());
    }

    private class myOnClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.txt_cancel:
                    if (dialog != null) {
                        dialog.cancel();
                    }
                    break;
                default:
                    break;
            }

        }

    }

    public void setProgress(long total, long current) {
        if (!dialog.isShowing()) {
            return;
        }
        int pb = (int) (current * 100 / total);
        rpb_update.setProgress(pb);
    }

    public void setProgress(int progress) {
        if (!dialog.isShowing()) {
            return;
        }
        rpb_update.setProgress(progress);
    }


    public void setTile(String name) {
        tv_title.setText(name);
        rpb_update.setCricleProgressColor(mContext.getResources().getColor(R.color.green));
    }

    public void show() {
        if (dialog != null) {
            dialog.show();
        }
    }

    public void cancel() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog.cancel();
            dialog = null;
        }
    }

    public boolean isShowing() {
        if (dialog != null && dialog.isShowing()) {
            return true;
        }
        return false;
    }

}
