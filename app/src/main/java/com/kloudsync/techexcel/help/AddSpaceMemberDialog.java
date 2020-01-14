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
import com.kloudsync.techexcel.bean.TeamMember;

public class AddSpaceMemberDialog implements OnClickListener {
    public Context mContext;
    public Dialog dialog;
    LinearLayout lin_company;
    LinearLayout lin_invite;
    TextView lin_invite_tv,cancel;

    public interface InviteOptionsLinstener {
        void fromCompany();

        void formInvite();
    }


    private InviteOptionsLinstener optionsLinstener;


    public void setOptionsLinstener(InviteOptionsLinstener optionsLinstener) {
        this.optionsLinstener = optionsLinstener;
    }

    public AddSpaceMemberDialog(Context context) {
        mContext = context;
        initDialog();
    }

    public void initDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.addspacememberdialog, null);
        dialog = new Dialog(mContext, R.style.bottom_dialog);
        lin_company = view.findViewById(R.id.lin_company);
        lin_invite = view.findViewById(R.id.lin_invite);
        cancel = view.findViewById(R.id.cancel);
        lin_invite_tv = view.findViewById(R.id.lin_invite_tv);

        lin_company.setOnClickListener(this);
        lin_invite.setOnClickListener(this);
        cancel.setOnClickListener(this);


        dialog.setContentView(view);
        dialog.getWindow().setWindowAnimations(R.style.PopupAnimation5);
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = mContext.getResources().getDisplayMetrics().widthPixels;
        dialog.getWindow().setAttributes(params);
    }

    public void show(int type) {
        dialog.show();

        if(type==0){
            lin_invite_tv.setText(mContext.getString(R.string.invite_form_admin));
        }else if(type==1){
            lin_invite_tv.setText(mContext.getString(R.string.invite_form_new));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin_company:
                if (optionsLinstener != null) {
                    optionsLinstener.fromCompany();
                }
                dismiss();
                break;
            case R.id.lin_invite:
                if (optionsLinstener != null) {
                    optionsLinstener.formInvite();
                }
                dismiss();
                break;
            case R.id.cancel:
                dismiss();
            default:
                break;
        }
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }


}
