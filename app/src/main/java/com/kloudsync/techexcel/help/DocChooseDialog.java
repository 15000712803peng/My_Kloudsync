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

public class DocChooseDialog implements OnClickListener {

    public Context mContext;

    private SelectedOptionListener selectedOptionListener;

    public interface SelectedOptionListener {
        void selectFromAlbum();

        void selectFromDocs();
    }

    public void setSelectedOptionListener(SelectedOptionListener selectedOptionListener) {
        this.selectedOptionListener = selectedOptionListener;
    }

    public DocChooseDialog(Context context) {
        this.mContext = context;
        getPopupWindowInstance();

    }

    public Dialog mPopupWindow;

    public void getPopupWindowInstance() {
        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }


    private TextView tv_album, tv_album2;
    private LinearLayout lin_album1,
            lin_album2,
            lin_album3,
            lin_album4;

    private TextView cancelText;

    @SuppressWarnings("deprecation")
    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View popupWindow = layoutInflater.inflate(R.layout.dialog_select_doc, null);
        tv_album = (TextView) popupWindow.findViewById(R.id.tv_album);
        tv_album2 = (TextView) popupWindow.findViewById(R.id.tv_album2);
        lin_album1 = (LinearLayout) popupWindow.findViewById(R.id.lin_album1);
        lin_album2 = (LinearLayout) popupWindow.findViewById(R.id.lin_album2);
        lin_album3 = (LinearLayout) popupWindow.findViewById(R.id.lin_album3);
        lin_album4 = (LinearLayout) popupWindow.findViewById(R.id.lin_album4);
        cancelText = (TextView) popupWindow.findViewById(R.id.cancel);
        mPopupWindow = new Dialog(mContext, R.style.bottom_dialog);
        cancelText.setOnClickListener(this);
        mPopupWindow.setContentView(popupWindow);
        mPopupWindow.getWindow().setWindowAnimations(R.style.PopupAnimation5);
        mPopupWindow.getWindow().setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams params = mPopupWindow.getWindow().getAttributes();
        params.width = mContext.getResources().getDisplayMetrics().widthPixels;
        mPopupWindow.getWindow().setAttributes(params);
        lin_album1.setOnClickListener(this);
        lin_album2.setOnClickListener(this);
        lin_album3.setOnClickListener(this);
        lin_album4.setOnClickListener(this);
    }


    public void show() {
        mPopupWindow.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin_album1:
                mPopupWindow.dismiss();
                if (selectedOptionListener != null) {
                    selectedOptionListener.selectFromAlbum();
                }
                break;
            case R.id.lin_album2:
                mPopupWindow.dismiss();
                if (selectedOptionListener != null) {
                    selectedOptionListener.selectFromDocs();
                }
                break;
            case R.id.lin_album3:
                mPopupWindow.dismiss();
                break;
            case R.id.lin_album4:
                break;

            case R.id.cancel:
                if(mPopupWindow != null){
                    mPopupWindow.dismiss();
                }

            default:
                break;
        }
    }

}
