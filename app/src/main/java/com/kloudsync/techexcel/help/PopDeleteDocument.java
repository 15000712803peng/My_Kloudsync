package com.kloudsync.techexcel.help;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.kloudsync.techexcel.R;

public class PopDeleteDocument {

    public Context mContext;

    private static PopDeleteDismissListener popDeleteDismissListener;

    public interface PopDeleteDismissListener {
        void PopDelete();
        void Open();
        void Close();
    }

    public void setPoPDismissListener(PopDeleteDismissListener popDeleteDismissListener) {
        PopDeleteDocument.popDeleteDismissListener = popDeleteDismissListener;
    }

    public void getPopwindow(Context context) {
        this.mContext = context;
        getPopupWindowInstance();
        mPopupWindow.getWindow().setWindowAnimations(R.style.PopupAnimation5);
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

    private TextView tv_delete, tv_cancel;

    @SuppressWarnings("deprecation")
    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View popupWindow = layoutInflater.inflate(R.layout.pop_delete_ask, null);
        tv_delete = (TextView) popupWindow.findViewById(R.id.tv_delete);
        tv_cancel = (TextView) popupWindow.findViewById(R.id.tv_cancel);
        mPopupWindow = new Dialog(mContext, R.style.bottom_dialog);
        mPopupWindow.setContentView(popupWindow);
        mPopupWindow.getWindow().setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = mPopupWindow.getWindow().getAttributes();
        lp.width = mContext.getResources().getDisplayMetrics().widthPixels;
        mPopupWindow.getWindow().setAttributes(lp);
        tv_delete.setOnClickListener(new myOnClick());
        tv_cancel.setOnClickListener(new myOnClick());
        mPopupWindow.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (popDeleteDismissListener != null) {
                    popDeleteDismissListener.Close();
                }
            }
        });

    }

    private class myOnClick implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_cancel:
                    mPopupWindow.dismiss();
                    break;
                case R.id.tv_delete:
                    popDeleteDismissListener.PopDelete();
                    mPopupWindow.dismiss();
                    break;

                default:
                    break;
            }

        }


    }

    public void StartPop(View v) {
        mPopupWindow.show();
        popDeleteDismissListener.Open();
    }


}
