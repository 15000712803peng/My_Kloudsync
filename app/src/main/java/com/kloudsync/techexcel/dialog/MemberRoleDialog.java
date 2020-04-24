package com.kloudsync.techexcel.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.RoleInTeam;


public class MemberRoleDialog implements View.OnClickListener {

    public Context mContext;
    public int width;
    public Dialog dialog;
    private View viewroot;
    private TextView inviteAsMemberText, inviteAsAdminText;
    private MemberRoleSelectedListener roleSelectedListener;
    private TextView roleText;
    private ImageView memberRoleImage;
    private ImageView adminRoleImage;

    public interface MemberRoleSelectedListener {
        void inviteAsMember(TextView roleText, RoleInTeam role);

        void inviteAsAdmin(TextView roleText, RoleInTeam role);
    }

    private RoleInTeam role;

    public RoleInTeam getRole() {
        return role;
    }


    public void setRole(RoleInTeam role, TextView roleText) {
        this.role = role;
        this.roleText = roleText;
        if (role.getTeamRole() == RoleInTeam.ROLE_ADMIN) {
            memberRoleImage.setVisibility(View.INVISIBLE);
            adminRoleImage.setVisibility(View.VISIBLE);
        } else if (role.getTeamRole() == RoleInTeam.ROLE_MEMBER) {
            memberRoleImage.setVisibility(View.VISIBLE);
            adminRoleImage.setVisibility(View.INVISIBLE);
        }

    }

    public MemberRoleDialog(Context context) {
        getPopwindow(context);
    }

    public MemberRoleSelectedListener getRoleSelectedListener() {
        return roleSelectedListener;
    }

    public void setRoleSelectedListener(MemberRoleSelectedListener roleSelectedListener) {
        this.roleSelectedListener = roleSelectedListener;
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
        viewroot = layoutInflater.inflate(R.layout.dialog_member_role, null);
        inviteAsMemberText = viewroot.findViewById(R.id.txt_invite_as_member);
        inviteAsAdminText = viewroot.findViewById(R.id.txt_invite_as_admin);
        memberRoleImage = viewroot.findViewById(R.id.image_invite_as_member);
        adminRoleImage = viewroot.findViewById(R.id.image_invite_as_admin);
        inviteAsMemberText.setOnClickListener(this);
        inviteAsAdminText.setOnClickListener(this);
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
            case R.id.txt_invite_as_member:
                if (roleText != null) {
                    roleText.setText(mContext.getString(R.string.member));
                }
                if (roleSelectedListener != null) {
                    roleSelectedListener.inviteAsMember(roleText, role);
                }
                cancel();
                break;
            case R.id.txt_invite_as_admin:
                if (roleText != null) {
                    roleText.setText(mContext.getString(R.string.admin));
                }
                if (roleSelectedListener != null) {
                    roleSelectedListener.inviteAsAdmin(roleText, role);
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
