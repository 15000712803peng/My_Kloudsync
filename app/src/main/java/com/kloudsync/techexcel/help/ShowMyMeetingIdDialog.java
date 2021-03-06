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

public class ShowMyMeetingIdDialog implements OnClickListener {
    public Context mContext;
    public Dialog dialog;
    private LinearLayout lin_enter;
    private TextView myroom;
    private String rooid;

    public interface InviteOptionsLinstener {
        void enter();
    }

    private InviteOptionsLinstener optionsLinstener;


    public void setOptionsLinstener(InviteOptionsLinstener optionsLinstener) {
        this.optionsLinstener = optionsLinstener;
    }

    public ShowMyMeetingIdDialog(Context context, String rooid) {
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
        lin_enter = view.findViewById(R.id.lin_enter);
        lin_enter.setOnClickListener(this);
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
            case R.id.lin_enter:
                if (optionsLinstener != null) {
                    optionsLinstener.enter();
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


}
