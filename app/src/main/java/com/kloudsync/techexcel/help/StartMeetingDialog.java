package com.kloudsync.techexcel.help;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.TeamMember;

public class StartMeetingDialog implements OnClickListener {
    public Context mContext;
    public Dialog dialog;
    private LinearLayout lin_cancel;
    private TextView myroom;
    private String rooid;
    private TextView startmeeting;
    private CheckBox checkbox;
    private RelativeLayout editrel;

    public interface InviteOptionsLinstener {
        void enter();
    }

    private InviteOptionsLinstener optionsLinstener;


    public void setOptionsLinstener(InviteOptionsLinstener optionsLinstener) {
        this.optionsLinstener = optionsLinstener;
    }

    public StartMeetingDialog(Context context, String rooid) {
        mContext = context;
        this.rooid = rooid;
        initDialog();
    }

    public void initDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.showmymeetingiddialog, null);
        dialog = new Dialog(mContext, R.style.bottom_dialog);
        myroom = view.findViewById(R.id.myroom);
        myroom.setText(mContext.getString(R.string.mymeetingid) + " " + rooid);
        lin_cancel = view.findViewById(R.id.lin_cancel);
        startmeeting = view.findViewById(R.id.startmeeting);
        editrel = view.findViewById(R.id.editrel);
        checkbox = view.findViewById(R.id.checkbox);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    editrel.setVisibility(View.VISIBLE);
                }else{
                    editrel.setVisibility(View.GONE);
                }
            }
        });
        lin_cancel.setOnClickListener(this);
        startmeeting.setOnClickListener(this);
        dialog.setContentView(view);
        dialog.getWindow().setWindowAnimations(R.style.PopupAnimation5);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = mContext.getResources().getDisplayMetrics().widthPixels;
        dialog.getWindow().setAttributes(params);
    }

    public void show() {
        if(dialog != null){
            dialog.show();
        }
    }

    public boolean isShowing(){
        if(dialog != null){
            return dialog.isShowing();
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startmeeting:
                if (optionsLinstener != null) {
                    optionsLinstener.enter();
                }
                dismiss();
                break;
            case R.id.lin_cancel:
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


}
