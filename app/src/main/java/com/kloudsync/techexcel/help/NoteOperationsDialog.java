package com.kloudsync.techexcel.help;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.ub.kloudsync.activity.Document;
import com.ub.techexcel.adapter.UserNotesAdapter;
import com.ub.techexcel.bean.Note;

public class NoteOperationsDialog implements OnClickListener {
    private Context mContext;
    private Note note;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.lin_edit:
                if(notesOperationsListener != null)
                dialog.dismiss();
                break;
            case R.id.lin_delete:

                dialog.dismiss();
                break;
            case R.id.lin_share:

                dialog.dismiss();
                break;
            case R.id.cancel:
                if(dialog != null){
                    dialog.dismiss();
                }

                break;

            default:
                break;
        }
    }

    private UserNotesAdapter.OnNotesOperationsListener notesOperationsListener;

    public UserNotesAdapter.OnNotesOperationsListener getNotesOperationsListener() {
        return notesOperationsListener;
    }

    public void setNotesOperationsListener(UserNotesAdapter.OnNotesOperationsListener notesOperationsListener) {
        this.notesOperationsListener = notesOperationsListener;
    }

    public void getPopwindow(Context context) {
        this.mContext = context;
        getPopupWindowInstance();

    }

    public Dialog dialog;

    public void getPopupWindowInstance() {
        if (null != dialog) {
            dialog.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }

    private LinearLayout lin_share;
    private LinearLayout lin_edit;
    private LinearLayout lin_delete;
    private TextView img_close;
    private TextView tv_name;
    private View popupWindow;

    @SuppressWarnings("deprecation")
    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        popupWindow = layoutInflater.inflate(R.layout.pop_note_operations, null);
        lin_share = (LinearLayout) popupWindow.findViewById(R.id.lin_share);
        lin_edit = (LinearLayout) popupWindow.findViewById(R.id.lin_edit);
        lin_delete = (LinearLayout) popupWindow.findViewById(R.id.lin_delete);
        img_close = (TextView) popupWindow.findViewById(R.id.cancel);
        tv_name = (TextView) popupWindow.findViewById(R.id.tv_name);

        dialog = new Dialog(mContext, R.style.bottom_dialog);
        dialog.setContentView(popupWindow);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
        dialog.getWindow().setWindowAnimations(R.style.PopupAnimation5);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = mContext.getResources().getDisplayMetrics().widthPixels;
        dialog.getWindow().setAttributes(lp);
        lin_share.setOnClickListener(this);
        lin_edit.setOnClickListener(this);
        lin_delete.setOnClickListener(this);
        img_close.setOnClickListener(this);
    }

    public void show(Note note) {
        this.note = note;
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }

    }


}
