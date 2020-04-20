package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.LessionInCourse;


public class StudentLessionDialog implements View.OnClickListener {

    public Context mContext;
    public int width;
    public Dialog mPopupWindow;
    private View view;
    ImageView closeImage;
    TextView prepareText;
    LessionInCourse lessionInCourse;
    TextView courseNameText;


    public interface OnStudentPreparedOptionsListener {
        void onStudentPreparedClosed();
        void onStudentPreparedLession();

    }

    private OnStudentPreparedOptionsListener onPreparedOptionsListener;


    public void setOnPreparedOptionsListener(OnStudentPreparedOptionsListener onPreparedOptionsListener) {
        this.onPreparedOptionsListener = onPreparedOptionsListener;
    }

    public StudentLessionDialog(Context context) {
        this.mContext = context;
        width = mContext.getResources().getDisplayMetrics().widthPixels;
        getPopupWindowInstance();
    }

    public void getPopupWindowInstance() {
        if (null != mPopupWindow) {
            mPopupWindow.cancel();
            return;
        } else {
            initPopuptWindow();
        }
    }


    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.dialog_student_lession, null);
//        recordsync.setText("Sync");
        mPopupWindow = new Dialog(mContext, R.style.my_dialog);
        mPopupWindow.setContentView(view);
        closeImage = view.findViewById(R.id.image_close);
        prepareText = view.findViewById(R.id.txt_prepare);
        courseNameText = view.findViewById(R.id.txt_course_name);
        prepareText.setOnClickListener(this);
        closeImage.setOnClickListener(this);
        mPopupWindow.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowManager.LayoutParams params = mPopupWindow.getWindow().getAttributes();
        if (Tools.isOrientationPortrait((Activity) mContext)) {
            params.width = mContext.getResources().getDisplayMetrics().widthPixels * 5 / 6;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        } else {
            params.width = mContext.getResources().getDisplayMetrics().widthPixels * 3 / 5;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        }

        mPopupWindow.setCancelable(false);
        mPopupWindow.getWindow().setAttributes(params);

    }


    @SuppressLint("NewApi")
    public void show(LessionInCourse lessionInCourse) {
        this.lessionInCourse = lessionInCourse;
        if(this.lessionInCourse != null){
            courseNameText.setText(this.lessionInCourse.getTitle());
        }
        if (mPopupWindow != null) {
            mPopupWindow.show();
        }
    }

    public boolean isShowing() {
        return mPopupWindow.isShowing();
    }

    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_close:

                if (onPreparedOptionsListener != null) {
                    onPreparedOptionsListener.onStudentPreparedClosed();
                }
                dismiss();
                break;
            case R.id.txt_prepare:
                dismiss();
                if (onPreparedOptionsListener != null) {
                    onPreparedOptionsListener.onStudentPreparedLession();
                }
                break;
            default:
                break;
        }
    }


}
