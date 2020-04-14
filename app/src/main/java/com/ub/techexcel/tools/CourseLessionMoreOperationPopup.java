package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.LessionInCourse;
import com.ub.techexcel.bean.ServiceBean;

/**
 * Created by wang on 2017/9/18.
 */

public class CourseLessionMoreOperationPopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public Dialog mPopupWindow;
    private View viewroot;
    private LinearLayout delete, open, edit;
    private TextView title;
    private LessionOptionsListener lessionOptionsListener;
    private TextView closebnt;
    private LessionInCourse lession;

    public interface LessionOptionsListener {

        void delete(LessionInCourse lession);

        void edit(LessionInCourse lession);

        void open(LessionInCourse lession);

    }


    public void setLessionOptionsListener(LessionOptionsListener lessionOptionsListener) {
        this.lessionOptionsListener = lessionOptionsListener;
    }

    public void getPopwindow(Context context) {
        this.mContext = context;
        width = mContext.getResources().getDisplayMetrics().widthPixels;
        getPopupWindowInstance();
    }

    public void getPopupWindowInstance() {
        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }

    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        viewroot = layoutInflater.inflate(R.layout.lession_more_popup, null);
        delete = (LinearLayout) viewroot.findViewById(R.id.delete);
        open = (LinearLayout) viewroot.findViewById(R.id.open);
        edit = (LinearLayout) viewroot.findViewById(R.id.editmeeting);
        closebnt = (TextView) viewroot.findViewById(R.id.cancel);
        closebnt.setOnClickListener(this);
        delete.setOnClickListener(this);
        open.setOnClickListener(this);
        edit.setOnClickListener(this);
        title = viewroot.findViewById(R.id.title);
        mPopupWindow = new Dialog(mContext, R.style.bottom_dialog);
        mPopupWindow.setContentView(viewroot);
        mPopupWindow.getWindow().setWindowAnimations(R.style.dialogwindowAnim);
        mPopupWindow.getWindow().setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams params = mPopupWindow.getWindow().getAttributes();
        params.width = mContext.getResources().getDisplayMetrics().widthPixels;
        mPopupWindow.getWindow().setAttributes(params);
    }

    @SuppressLint("NewApi")
    public void show(LessionInCourse lession) {
        this.lession = lession;
        if (mPopupWindow != null) {
            title.setText(lession.getTitle());
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
            case R.id.cancel:
                dismiss();
                break;
            case R.id.delete:
                if(lessionOptionsListener != null){
                    lessionOptionsListener.delete(lession);
                }
                dismiss();
                break;
            case R.id.open:
                if(lessionOptionsListener != null){
                    lessionOptionsListener.open(lession);
                }
                dismiss();
                break;
            case R.id.editmeeting:
                if(lessionOptionsListener != null){
                    lessionOptionsListener.edit(lession);
                }
                dismiss();
                break;

            default:
                break;
        }
    }

}
