package com.kloudsync.techexcel.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.kloudsync.techexcel.R;


public class AddWxDocDialog implements View.OnClickListener {
    public Context mContext;
    public int width;
    public Dialog dialog;
    private View view;
    private TextView saveInSpaceText;
    private TextView saveInFavoriteText;
    private TextView cancelText;
    String filePath;


    public interface OnDocSavedListener {
        void onSaveSpace(String path);

        void onSaveFavorite(String path);

    }

    private OnDocSavedListener savedListener;


    public void setSavedListener(OnDocSavedListener savedListener) {
        this.savedListener = savedListener;
    }

    public AddWxDocDialog(Context context, String filePath) {
        this.mContext = context;
        this.filePath = filePath;
        initDialog();

    }

    public void initDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.dialog_add_wx_doc, null);
        dialog = new Dialog(mContext, R.style.bottom_dialog);
        saveInSpaceText = view.findViewById(R.id.txt_save_in_space);
        saveInFavoriteText = view.findViewById(R.id.txt_save_in_favorite);
        cancelText = view.findViewById(R.id.txt_cancel);
        saveInFavoriteText.setOnClickListener(this);
        saveInSpaceText.setOnClickListener(this);
        cancelText.setOnClickListener(this);
        width = mContext.getResources().getDisplayMetrics().widthPixels;
        dialog.setContentView(view);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setWindowAnimations(R.style.PopupAnimation5);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = mContext.getResources().getDisplayMetrics().widthPixels;
        dialog.getWindow().setAttributes(lp);
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
            case R.id.txt_save_in_space:
                if (savedListener != null) {
                    savedListener.onSaveSpace(filePath);
                }
                dialog.dismiss();

                break;
            case R.id.txt_save_in_favorite:
                if (savedListener != null) {
                    savedListener.onSaveFavorite(filePath);
                }
                dialog.dismiss();
                break;
            case R.id.txt_cancel:
                dialog.dismiss();
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
