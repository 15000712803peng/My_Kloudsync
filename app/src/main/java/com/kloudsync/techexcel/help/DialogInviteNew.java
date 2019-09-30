package com.kloudsync.techexcel.help;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.docment.SearchContactActivity;
import com.kloudsync.techexcel.ui.InviteFromPhoneActivity;

public class DialogInviteNew {

    private AlertDialog dlgGetWindow = null;// 对话框
    private Window window;
    private TextView tv_sfc;
    private TextView tv_invite;
    private Context mContext;


    private int itemID;


    public void EditCancel(Context context, int itemID) {
        this.mContext = context;
        this.itemID = itemID;

        int height = context.getResources().getDisplayMetrics().heightPixels;

        dlgGetWindow = new AlertDialog.Builder(context).create();
        dlgGetWindow.setView(new EditText(mContext));
        dlgGetWindow.show();
        window = dlgGetWindow.getWindow();
        window.setWindowAnimations(R.style.PopupAnimation3);
        window.setContentView(R.layout.dialog_invitenew);
        window.setBackgroundDrawable(new ColorDrawable(mContext.getResources().getColor(R.color.white)));
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        WindowManager.LayoutParams layoutParams = dlgGetWindow.getWindow()
                .getAttributes();
        layoutParams.y = height / 2;
        dlgGetWindow.getWindow().setAttributes(layoutParams);

        ShowTSInfo();
    }

    private void ShowTSInfo() {
        tv_sfc = window.findViewById(R.id.tv_sfc);
        tv_invite = window.findViewById(R.id.tv_invite);

        tv_sfc.setOnClickListener(new MyOnClick());
        tv_invite.setOnClickListener(new MyOnClick());

    }

    protected class MyOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_sfc:
                    GoToSC();
                    break;
                case R.id.tv_invite:
                    GoToIN();
                    break;

            }
        }
    }

    private void GoToSC() {
        Intent intent = new Intent(mContext, SearchContactActivity.class);
        intent.putExtra("itemID", itemID);
        mContext.startActivity(intent);
        dlgGetWindow.dismiss();
    }

    private void GoToIN() {
        Intent intent = new Intent(mContext, InviteFromPhoneActivity.class);
        intent.putExtra("itemID", itemID);
        mContext.startActivity(intent);

        dlgGetWindow.dismiss();
    }


}
