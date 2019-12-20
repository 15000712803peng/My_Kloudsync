package com.kloudsync.techexcel.help;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.MeetingMember;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.tool.MeetingSettingCache;
import com.kloudsync.techexcel.tool.SocketMessageManager;
import com.kloudsync.techexcel.ui.DocAndMeetingActivity;

import org.greenrobot.eventbus.EventBus;

public class SetPresenterDialog implements DialogInterface.OnDismissListener, OnClickListener {

    int width;
    private Activity host;
    private Dialog dialog;
    private RelativeLayout setPresenterLayout;
    private TextView cancelText;
    MeetingMember meetingMember;

    @Override
    public void onDismiss(DialogInterface dialog) {
        dialog = null;
    }

    public interface OnSetPresenterClickedListener{
        void onSetPresenterClicked(MeetingMember meetingMember);
    }
    OnSetPresenterClickedListener onSetPresenterClickedListener;


    public void setOnSetPresenterClickedListener(OnSetPresenterClickedListener onSetPresenterClickedListener) {
        this.onSetPresenterClickedListener = onSetPresenterClickedListener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_set_presenter:
                if(onSetPresenterClickedListener != null && meetingMember != null){
                    onSetPresenterClickedListener.onSetPresenterClicked(meetingMember);
                }
                dismiss();
                break;
            case R.id.cancel:
                dismiss();
                break;
        }
    }

    public void dismiss(){
        if(dialog != null){
            dialog.dismiss();
        }
    }


    public SetPresenterDialog(Activity host) {
        getPopupWindow(host);
    }

    public void getPopupWindow(Activity host) {
        if (null != dialog) {
            dialog.dismiss();
            return;
        } else {
            init(host);
        }
    }


    public void init(Activity host) {
        LayoutInflater layoutInflater = LayoutInflater.from(host);
        View view = layoutInflater.inflate(R.layout.layout_set_presenter, null);
        setPresenterLayout  = view.findViewById(R.id.layout_set_presenter);
        setPresenterLayout.setOnClickListener(this);
        cancelText =view.findViewById(R.id.cancel);
        cancelText.setOnClickListener(this);
        width = (int) (host.getResources().getDisplayMetrics().widthPixels);
        dialog = new Dialog(host, R.style.bottom_dialog);
        dialog.setContentView(view);
        dialog.setOnDismissListener(this);
        dialog.getWindow().setWindowAnimations(R.style.anination2);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = width - 50;
        dialog.getWindow().setAttributes(lp);

    }

    public void show(MeetingMember meetingMember,Activity host) {
        this.meetingMember = meetingMember;
        this.host = host;
        dialog.show();

    }

    public boolean isShowing() {
        if (dialog != null) {
            return dialog.isShowing();
        }
        return false;
    }

    public void hide() {
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = null;
    }



}
