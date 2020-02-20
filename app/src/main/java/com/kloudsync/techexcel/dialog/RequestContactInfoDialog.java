package com.kloudsync.techexcel.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.RequestContactData;
import com.kloudsync.techexcel.view.ClearEditText;

public class RequestContactInfoDialog implements OnClickListener {

    private RequestContactData contactData;
    private Context mContext;
    private SimpleDraweeView contactImage;
    private TextView nameText;
    private TextView phoneText;

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


    public RequestContactInfoDialog(Context context) {
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

    private TextView cancel, ok;
    private ClearEditText editText;
    private View view;

    @SuppressWarnings("deprecation")
    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.dialog_requesr_contact_info, null);
        cancel = (TextView) view.findViewById(R.id.cancel);
        ok = (TextView) view.findViewById(R.id.ok);
        contactImage = view.findViewById(R.id.img_contact);
        nameText = view.findViewById(R.id.txt_name);
        phoneText = view.findViewById(R.id.txt_phone);

        dialog = new Dialog(mContext, R.style.my_dialog);
        dialog.setContentView(view);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.75f);
        dialog.getWindow().setAttributes(lp);
        cancel.setOnClickListener(this);
        ok.setOnClickListener(this);
    }

    public void show(RequestContactData contactData, String phone) {
        this.contactData = contactData;
        if (contactData.getAvatarUrl() != null) {
            contactImage.setImageURI(Uri.parse(contactData.getAvatarUrl()));
        }
        if (!TextUtils.isEmpty(contactData.getUserName())) {
            nameText.setText(contactData.getUserName());
        }

        if (TextUtils.isEmpty(phone)) {
            phoneText.setText(phone);
        }

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
