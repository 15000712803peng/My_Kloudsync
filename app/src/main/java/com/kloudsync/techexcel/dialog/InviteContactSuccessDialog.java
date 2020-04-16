package com.kloudsync.techexcel.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class InviteContactSuccessDialog implements OnClickListener {

    private Context mContext;


    private TextView ok,phoneText;
    private View view;
    String phone;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                dialog.dismiss();
                break;
            case R.id.ok:

                dialog.dismiss();
                break;

            default:
                break;
        }
    }

    public InviteContactSuccessDialog(Context context) {
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

    @SuppressWarnings("deprecation")
    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.dialog_invite_contact_succ, null);
        ok = (TextView) view.findViewById(R.id.ok);

        phoneText = view.findViewById(R.id.txt_phone);
        dialog = new Dialog(mContext, R.style.my_dialog);
        dialog.setContentView(view);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.75f);
        dialog.getWindow().setAttributes(lp);
        ok.setOnClickListener(this);
    }

    public void show(String phone) {
        this.phone = phone;
        phoneText.setText(phone);

        if (dialog != null) {
            dialog.show();
        }
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public boolean isShowing() {
        if (dialog != null) {
            return dialog.isShowing();
        }
        return false;
    }

}
