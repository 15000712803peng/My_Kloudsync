package com.kloudsync.techexcel.help;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;

public class InviteContactDialog implements OnClickListener {
    public Context mContext;
    public Dialog dialog;
    TextView inviteFromContactText;
    TextView inviteNewText;
    LinearLayout inviteFromPhoneLayout;
    LinearLayout inviteFromContactLayout;
    TextView titleText;

    public interface InviteOptionsLinstener {
        void inviteFromContactOption();
        void inviteNewOption();
    }


    public void setTitle(String title) {
        titleText.setText(title);
    }

    private InviteOptionsLinstener optionsLinstener;


    public void setOptionsLinstener(InviteOptionsLinstener optionsLinstener) {
        this.optionsLinstener = optionsLinstener;
    }

    public InviteContactDialog(Context context) {
        mContext = context;
        initDialog();
    }

    public void initDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.dialog_invite_contact, null);
        dialog = new Dialog(mContext, R.style.bottom_dialog);
        inviteFromPhoneLayout = view.findViewById(R.id.layout_invite_from_phone);
        inviteFromContactLayout = view.findViewById(R.id.layout_invite_from_contact);
        titleText = view.findViewById(R.id.txt_dialog_title);
        dialog.setContentView(view);
        dialog.getWindow().setWindowAnimations(R.style.PopupAnimation5);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        inviteFromContactText = view.findViewById(R.id.txt_invite_from_contact);
        inviteNewText = view.findViewById(R.id.txt_invite_new);
        inviteNewText.setOnClickListener(this);
        inviteFromContactText.setOnClickListener(this);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = mContext.getResources().getDisplayMetrics().widthPixels;
        dialog.getWindow().setAttributes(params);
    }

    public void show() {
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_invite_from_contact:
                if (optionsLinstener != null) {
                    optionsLinstener.inviteFromContactOption();
                }
                dismiss();
                break;
            case R.id.txt_invite_new:
                if (optionsLinstener != null) {
                    optionsLinstener.inviteNewOption();
                }
                dismiss();
                break;
            default:
                break;
        }
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void setInviteFromPhoneLayoutGone() {
        if (inviteFromPhoneLayout != null) {
            inviteFromPhoneLayout.setVisibility(View.GONE);
        }
    }

    public void setInviteFromContactLayoutGone() {
        if (inviteFromContactLayout != null) {
            inviteFromContactLayout.setVisibility(View.GONE);
        }
    }

    public boolean isShowing(){
        if(dialog != null){
            return dialog.isShowing();
        }
        return false;
    }


}
