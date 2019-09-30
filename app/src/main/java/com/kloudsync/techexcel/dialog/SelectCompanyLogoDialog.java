package com.kloudsync.techexcel.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.kloudsync.techexcel.R;


public class SelectCompanyLogoDialog implements View.OnClickListener {

    public Context mContext;
    public int width;
    public Dialog dialog;
    private View viewroot;
    private TextView cancelText, selectPhoneText, selectAlbumText;
    private LogoOptionsListener logoOptionsListener;

    public interface LogoOptionsListener {
        void fromPhoto();

        void fromAlbum();
    }

    public SelectCompanyLogoDialog(Context context) {
        getPopwindow(context);
    }


    public void setLogoOptionsListener(LogoOptionsListener logoOptionsListener) {
        this.logoOptionsListener = logoOptionsListener;
    }

    public void getPopwindow(Context context) {
        this.mContext = context;
        width = mContext.getResources().getDisplayMetrics().widthPixels;
        getPopupWindowInstance();
    }

    public void getPopupWindowInstance() {
        if (null != dialog) {
            dialog.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }


    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        viewroot = layoutInflater.inflate(R.layout.dialog_select_company_logo, null);
        cancelText = viewroot.findViewById(R.id.txt_cancel);
        selectPhoneText = viewroot.findViewById(R.id.txt_select_phone);
        selectAlbumText = viewroot.findViewById(R.id.txt_select_album);
        selectPhoneText.setOnClickListener(this);
        selectAlbumText.setOnClickListener(this);
        cancelText.setOnClickListener(this);
        dialog = new Dialog(mContext, R.style.bottom_dialog);
        dialog.setContentView(viewroot);
        dialog.getWindow().setWindowAnimations(R.style.dialogwindowAnim);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = this.width;
        dialog.getWindow().setAttributes(params);

    }


    public void show() {
        if (dialog != null) {
            dialog.show();
        }
    }

    public boolean isShowing() {
        return dialog.isShowing();
    }

    public void cancel() {
        if (dialog != null) {
            dialog.cancel();
            dialog = null;
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_select_phone:
                if (logoOptionsListener != null) {
                    logoOptionsListener.fromPhoto();
                }
                cancel();
                break;
            case R.id.txt_select_album:
                if (logoOptionsListener != null) {
                    logoOptionsListener.fromAlbum();
                }
                cancel();
                break;
            case R.id.txt_cancel:
                cancel();
                break;
            default:
                break;
        }
    }

}
