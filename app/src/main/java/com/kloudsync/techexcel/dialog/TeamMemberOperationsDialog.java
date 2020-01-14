package com.kloudsync.techexcel.dialog;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.TeamMember;
import com.ub.techexcel.tools.Tools;

import org.feezu.liuli.timeselector.Utils.TextUtil;


public class TeamMemberOperationsDialog implements View.OnClickListener {
    public Context mContext;
    public int width;
    public Dialog dialog;
    private View view;
    private LinearLayout requestFriendLayout;
    private LinearLayout adminLayout;
    private LinearLayout removeLayout;
    private ImageView img_close;
    private TextView nameText;
    private TextView adminText;
    TeamMember member;
    ImageView memberIconImage;

    int myRole;

    public void getPopwindow(Context context) {
        this.mContext = context;

    }

    public TeamMemberOperationsDialog(Context context, TeamMember member, int myRole) {
        mContext = context;
        this.member = member;
        this.myRole = myRole;
        initDialog(member);
    }

    public void initDialog(TeamMember member) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.dialog_team_member_operations, null);
        memberIconImage = view.findViewById(R.id.image_member);
        nameText = view.findViewById(R.id.txt_name);
        requestFriendLayout = view.findViewById(R.id.layout_requset_friend);
        adminLayout = view.findViewById(R.id.layout_admin);
        adminText = view.findViewById(R.id.txt_admin);
        removeLayout = view.findViewById(R.id.layout_remove);
        requestFriendLayout.setOnClickListener(this);
        adminLayout.setOnClickListener(this);
        removeLayout.setOnClickListener(this);

        img_close = (ImageView) view.findViewById(R.id.img_close);
        img_close.setOnClickListener(this);
        dialog = new Dialog(mContext, R.style.bottom_dialog);
        width = mContext.getResources().getDisplayMetrics().widthPixels;
        dialog.setContentView(view);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setWindowAnimations(R.style.PopupAnimation5);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = mContext.getResources().getDisplayMetrics().widthPixels;
        dialog.getWindow().setAttributes(lp);
        fillDialogByMember(member, myRole);
    }

    private void fillDialogByMember(TeamMember member, int myRole) {
        if (!TextUtil.isEmpty(member.getMemberName())) {
            nameText.setText(member.getMemberName());
        }
        Uri iconUri;
        if (!TextUtils.isEmpty(member.getMemberAvatar())) {
            iconUri = Uri.parse(member.getMemberAvatar());
        } else {
            iconUri = Tools.getUriFromDrawableRes(mContext, R.drawable.hello);
        }
        memberIconImage.setImageURI(iconUri);
        int memberType = member.getMemberType();
        if (myRole == TeamMember.TYPE_OWNER) {
            if (memberType == TeamMember.TYPE_ADMIN) {
                requestFriendLayout.setVisibility(View.VISIBLE);
                adminText.setText("Cancel team admin");
                adminLayout.setTag(TeamMember.OPERATION_CANCEL_ADMIN);
                adminLayout.setVisibility(View.VISIBLE);
                removeLayout.setVisibility(View.GONE);
            } else if (memberType == TeamMember.TYPE_OWNER) {
                requestFriendLayout.setVisibility(View.VISIBLE);
                adminLayout.setVisibility(View.GONE);
                removeLayout.setVisibility(View.GONE);
            } else if (memberType == TeamMember.TYPE_MEMBER) {
                requestFriendLayout.setVisibility(View.VISIBLE);
                adminText.setText("Set team admin");
                adminLayout.setTag(TeamMember.OPERATION_SET_ADMIN);
                adminLayout.setVisibility(View.VISIBLE);
                removeLayout.setVisibility(View.GONE);
            }
        } else if (myRole == TeamMember.TYPE_ADMIN) {
            if (memberType == TeamMember.TYPE_ADMIN) {
                requestFriendLayout.setVisibility(View.VISIBLE);
                adminLayout.setVisibility(View.GONE);
                removeLayout.setVisibility(View.GONE);
            } else if (memberType == TeamMember.TYPE_OWNER) {
                requestFriendLayout.setVisibility(View.VISIBLE);
                adminLayout.setVisibility(View.GONE);
                removeLayout.setVisibility(View.GONE);
            } else if (memberType == TeamMember.TYPE_MEMBER) {
                requestFriendLayout.setVisibility(View.VISIBLE);
                adminText.setText("Set team admin");
                adminLayout.setTag(TeamMember.OPERATION_SET_ADMIN);
                adminLayout.setVisibility(View.VISIBLE);
                removeLayout.setVisibility(View.GONE);
            }
        } else if (myRole == TeamMember.TYPE_MEMBER) {
            requestFriendLayout.setVisibility(View.VISIBLE);
            adminLayout.setVisibility(View.GONE);
            removeLayout.setVisibility(View.GONE);
        }

    }

    public boolean isShowing() {
        return dialog.isShowing();
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_requset_friend:
                dismiss();
                break;
            case R.id.layout_admin:
                dismiss();
                break;
            case R.id.layout_remove:
                dismiss();
                break;
            case R.id.img_close:
                dismiss();
                break;
            default:
                break;
        }
    }

    public void show() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

}
