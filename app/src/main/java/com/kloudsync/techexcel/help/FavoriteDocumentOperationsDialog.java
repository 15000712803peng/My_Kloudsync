package com.kloudsync.techexcel.help;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.ub.kloudsync.activity.Document;

public class FavoriteDocumentOperationsDialog implements OnClickListener {
    public Context mContext;
    private static PopDocumentListener popDocumentListener;
    Document lesson;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
                /*case R.id.tv_view:
                    popDocumentListener.PopView();
                    mPopupWindow.dismiss();
                    break;*/
            case R.id.lin_edit:
                popDocumentListener.PopEdit();
                mPopupWindow.dismiss();
                break;
            case R.id.lin_delete:
                popDocumentListener.PopDelete();
                mPopupWindow.dismiss();
                break;
            case R.id.lin_share:
                popDocumentListener.PopShare();
                mPopupWindow.dismiss();
                break;
            case R.id.img_close:
                mPopupWindow.dismiss();
                break;

            default:
                break;
        }
    }

    public interface PopDocumentListener {
        void PopView();

        void PopDelete();

        void PopEdit();

        void PopShare();

        void PopMove();

        void PopBack();
    }

    public void setPoPMoreListener(PopDocumentListener popDocumentListener) {
        FavoriteDocumentOperationsDialog.popDocumentListener = popDocumentListener;
    }

    public void getPopwindow(Context context, Document lesson) {
        this.mContext = context;
        this.lesson = lesson;
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

    private LinearLayout lin_share;
    private LinearLayout lin_move;
    private LinearLayout lin_edit;
    private LinearLayout lin_delete;
    private ImageView img_close;
    private TextView tv_name;
    private View popupWindow;
    private View moveDivider;

    @SuppressWarnings("deprecation")
    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        popupWindow = layoutInflater.inflate(R.layout.pop_document, null);

        lin_share = (LinearLayout) popupWindow.findViewById(R.id.lin_share);
        lin_move = (LinearLayout) popupWindow.findViewById(R.id.lin_move);
        lin_edit = (LinearLayout) popupWindow.findViewById(R.id.lin_edit);
        lin_delete = (LinearLayout) popupWindow.findViewById(R.id.lin_delete);
        img_close = (ImageView) popupWindow.findViewById(R.id.img_close);
        tv_name = (TextView) popupWindow.findViewById(R.id.tv_name);
        moveDivider = popupWindow.findViewById(R.id.divider_move);
        moveDivider.setVisibility(View.GONE);
        tv_name.setText(lesson.getTitle());

        mPopupWindow = new Dialog(mContext, R.style.bottom_dialog);
        mPopupWindow.setContentView(popupWindow);
        mPopupWindow.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (popDocumentListener != null) {
                    popDocumentListener.PopBack();
                }
            }
        });
        mPopupWindow.getWindow().setWindowAnimations(R.style.PopupAnimation5);
        mPopupWindow.getWindow().setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = mPopupWindow.getWindow().getAttributes();
        lp.width = mContext.getResources().getDisplayMetrics().widthPixels;
        mPopupWindow.getWindow().setAttributes(lp);
        lin_share.setOnClickListener(this);
        lin_move.setVisibility(View.GONE);
        lin_edit.setOnClickListener(this);
        lin_delete.setOnClickListener(this);
        img_close.setOnClickListener(this);
    }

    public void show() {
        if (mPopupWindow != null && !mPopupWindow.isShowing()) {
            mPopupWindow.show();
        }
    }


}
