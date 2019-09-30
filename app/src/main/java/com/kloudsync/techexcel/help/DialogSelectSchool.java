package com.kloudsync.techexcel.help;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.FavoriteAdapter;
import com.kloudsync.techexcel.info.School;
import com.ub.kloudsync.activity.Document;

import java.util.ArrayList;

public class DialogSelectSchool {

    private AlertDialog dlgGetWindow = null;// 对话框
    private Window window;
    private TextView tv_OK;
    private TextView tv_name;
    private Context mContext;

    private boolean flag;

    private School school;

    private int width;
    private int height;

    private ArrayList<Document> mlist = new ArrayList<Document>();
    private FavoriteAdapter fAdapter;

    private static DialogDismissListener dialogdismissListener;

    public interface DialogDismissListener {
        void PopSelect(boolean isSelect);
    }

    public void setPoPDismissListener(
            DialogDismissListener dialogdismissListener) {
        DialogSelectSchool.dialogdismissListener = dialogdismissListener;
    }

    public void EditCancel(Context context,School school) {
        this.mContext = context;
        this.school = school;

        width = context.getResources().getDisplayMetrics().widthPixels;
        height = context.getResources().getDisplayMetrics().heightPixels;

        dlgGetWindow = new AlertDialog.Builder(context, R.style.dialog).create();
        dlgGetWindow.show();
        window = dlgGetWindow.getWindow();
        window.setWindowAnimations(R.style.PopupAnimation3);
        window.setContentView(R.layout.pop_selectschool);

        WindowManager.LayoutParams layoutParams = dlgGetWindow.getWindow()
                .getAttributes();
        layoutParams.width = width * 2 / 3;
        dlgGetWindow.getWindow().setAttributes(layoutParams);

        dlgGetWindow.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(dialogdismissListener != null){
                    dialogdismissListener.PopSelect(flag);
                }
            }
        });

        ShowSchoolInfo();
    }

    private void ShowSchoolInfo() {
        tv_OK = (TextView) window.findViewById(R.id.tv_OK);
        tv_name = (TextView) window.findViewById(R.id.tv_name);

        tv_name.setText("\"" + school.getSchoolName() + "\"");

        tv_OK.setOnClickListener(new myOnClick());
    }

    private class myOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_OK:
                    DissmissPop(true);
                    break;

                default:
                    break;
            }

        }


    }

    private void DissmissPop(boolean isupdate) {
        flag = isupdate;
        dlgGetWindow.dismiss();
    }


}
