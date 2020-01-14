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

public class SpaceMemberOperationDialog implements OnClickListener {
    public Context mContext;
    public Dialog dialog;
    LinearLayout lin_setadmin;
    LinearLayout lin_delete;
    LinearLayout startchatll;
    TextView cancel;
    private TeamMember member;
    private View line;

    public void setDeleteHidden() {
        if(lin_delete!=null){
            lin_delete.setVisibility(View.GONE);
        }
    }

    public interface InviteOptionsLinstener {
        void setAdmin();

        void clean();
    }


    private InviteOptionsLinstener optionsLinstener;


    public void setOptionsLinstener(InviteOptionsLinstener optionsLinstener) {
        this.optionsLinstener = optionsLinstener;
    }

    public SpaceMemberOperationDialog(Context context, TeamMember member) {
        mContext = context;
        this.member = member;
        initDialog();
    }

    public void initDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.spacememberoperation, null);
        dialog = new Dialog(mContext, R.style.bottom_dialog);
        lin_setadmin = view.findViewById(R.id.lin_setadmin);
        lin_delete = view.findViewById(R.id.lin_delete);
        startchatll = view.findViewById(R.id.startchatll);
        cancel = view.findViewById(R.id.cancel);
        line = view.findViewById(R.id.line);

        lin_setadmin.setOnClickListener(this);
        lin_delete.setOnClickListener(this);
        startchatll.setOnClickListener(this);
        cancel.setOnClickListener(this);

        if (member.getMemberType() == 1) {
            lin_setadmin.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
        } else if (member.getMemberType() == 0) {
            lin_setadmin.setVisibility(View.VISIBLE);
        }

        dialog.setContentView(view);
        dialog.getWindow().setWindowAnimations(R.style.PopupAnimation5);
        dialog.getWindow().setGravity(Gravity.BOTTOM);

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
            case R.id.lin_setadmin:
                if (optionsLinstener != null) {
                    optionsLinstener.setAdmin();
                }
                dismiss();
                break;
            case R.id.lin_delete:
                if (optionsLinstener != null) {
                    optionsLinstener.clean();
                }
                dismiss();
                break;
            case R.id.cancel:
                dismiss();
            case R.id.startchatll:
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
