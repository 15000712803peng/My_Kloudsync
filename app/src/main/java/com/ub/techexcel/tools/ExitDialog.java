package com.ub.techexcel.tools;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.MeetingType;
import com.kloudsync.techexcel.config.AppConfig;

public class ExitDialog extends Dialog implements DialogInterface.OnDismissListener,View.OnClickListener{

    private Context context;
    private TextView titleText;
    private TextView saveAndLeaveText;
    private TextView leaveText;
    private TextView cancelText;
    private View firstDivider;
    private MeetingConfig meetingConfig;
    private boolean isEndMeeting;

    public boolean isEndMeeting() {
        return isEndMeeting;
    }

    public void setEndMeeting(boolean endMeeting) {
        isEndMeeting = endMeeting;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        dialog = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.save_and_leave:
                if(dialogClickListener != null){
                    dialogClickListener.onSaveAndLeaveClick();
                }
                dismiss();
                break;
            case R.id.leave:
                if(dialogClickListener != null){
                    dialogClickListener.onLeaveClick();
                }
                dismiss();
                break;
            case R.id.cancel:
                dismiss();
                break;
        }
    }

    @Override
    public void show() {
        super.show();
        if(meetingConfig.getType() == MeetingType.MEETING){
            saveAndLeaveText.setVisibility(View.VISIBLE);
        }else {
            saveAndLeaveText.setVisibility(View.VISIBLE);
        }
    }

    private ExitDialogClickListener dialogClickListener;


    public void setDialogClickListener(ExitDialogClickListener dialogClickListener) {
        this.dialogClickListener = dialogClickListener;
    }

    public interface ExitDialogClickListener {
        void onSaveAndLeaveClick();
        void onLeaveClick();
    }

    public ExitDialog(Context context, MeetingConfig meetingConfig) {
        super(context, R.style.confirmDialog);
        this.context = context;
        this.meetingConfig = meetingConfig;
        setOnDismissListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_exit_doc, null);
        setContentView(view);
	    getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        saveAndLeaveText = view.findViewById(R.id.save_and_leave);
        leaveText = view.findViewById(R.id.leave);
        cancelText = view.findViewById(R.id.cancel);
        firstDivider = view.findViewById(R.id.first_divider);
        titleText = view.findViewById(R.id.title);
        saveAndLeaveText.setOnClickListener(this);
        leaveText.setOnClickListener(this);
        cancelText.setOnClickListener(this);
        initByConfig();
        /*Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics();
        lp.width = (int) (d.widthPixels * 0.8);
        dialogWindow.setAttributes(lp);*/

    }



    private void initByConfig(){
        if(meetingConfig.getType() != MeetingType.MEETING){
            titleText.setText(context.getString(R.string.title_to_leave));
            if(!meetingConfig.isDocModifide()){
                saveAndLeaveText.setVisibility(View.GONE);
                firstDivider.setVisibility(View.GONE);
            }else {
                saveAndLeaveText.setVisibility(View.VISIBLE);
                firstDivider.setVisibility(View.VISIBLE);
            }
        }else {
            if(isEndMeeting){
	            if (AppConfig.systemType == 0) {
		            titleText.setText(R.string.title_to_end_meeting);
	            } else {
		            titleText.setText(R.string.title_to_end_course);
	            }
                saveAndLeaveText.setText(R.string.save_and_End);
                leaveText.setText(R.string.End);
            }else {
	            if (AppConfig.systemType == 0) {
		            titleText.setText(R.string.title_to_leave_meeting);
	            } else {
		            titleText.setText(R.string.title_to_leave_course);
	            }
                saveAndLeaveText.setText(R.string.save_and_leave);
                leaveText.setText(R.string.mtLeave);
            }
            saveAndLeaveText.setVisibility(View.VISIBLE);
            firstDivider.setVisibility(View.VISIBLE);

        }
    }
}