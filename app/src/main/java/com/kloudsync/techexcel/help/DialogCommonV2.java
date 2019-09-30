package com.kloudsync.techexcel.help;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

import com.kloudsync.techexcel.R;

public abstract class DialogCommonV2 {

    private AlertDialog dlgGetWindow = null;// 对话框
    private Window window;

    private static DialogDismissListener dialogdismissListener;

    public interface DialogDismissListener {
        void DialogDismiss();
    }

    public void setPoPDismissListener(
            DialogDismissListener dialogdismissListener) {
        DialogCommonV2.dialogdismissListener = dialogdismissListener;
    }

    public void showDialog(Context context) {
        int width = context.getResources().getDisplayMetrics().widthPixels;
        dlgGetWindow = new AlertDialog.Builder(context).create();
        dlgGetWindow.show();
        window = dlgGetWindow.getWindow();
        window.setWindowAnimations(R.style.DialogAnimation);
        window.setContentView(setDialogLayout());
        WindowManager.LayoutParams layoutParams = dlgGetWindow.getWindow()
                .getAttributes();
        layoutParams.width = width * 5 / 7;
        dlgGetWindow.getWindow().setAttributes(layoutParams);
        fillContent(window);

    }

    protected class HandleClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_yes:
                    dlgGetWindow.dismiss();
                    break;
                default:
                    break;
            }

        }

    }

    protected abstract int setDialogLayout();

    protected abstract void fillContent(Window window);

}