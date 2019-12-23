package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;

/**
 * Created by wang on 2017/9/18.
 */

public class InviteUserPopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public Dialog mPopupWindow;
    private View view;
    private TextView copyLink;
    private TextView email;
    private TextView htmlUrl;
    private TextView idString;
    private ImageView closebtn;
    private String lessonId;
    private ClipboardManager myClipboard;

    public void getPopwindow(Context context, String lessonId) {
        this.mContext = context;
        this.lessonId = lessonId;
        width = mContext.getResources().getDisplayMetrics().widthPixels;
        myClipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
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
        view = layoutInflater.inflate(R.layout.inviteuser, null);
        copyLink = (TextView) view.findViewById(R.id.copylink);
        email = (TextView) view.findViewById(R.id.email);
        htmlUrl = (TextView) view.findViewById(R.id.htmlurl);
        idString = (TextView) view.findViewById(R.id.idstring);
        closebtn = (ImageView) view.findViewById(R.id.closebtn);
        htmlUrl.setText("https://peertime.cn/join?id=" + lessonId);
        idString.setText("2.Enter meeting ID:" + lessonId);
        copyLink.setOnClickListener(this);
        closebtn.setOnClickListener(this);
        email.setOnClickListener(this);
        mPopupWindow = new Dialog(mContext, R.style.my_dialog);
        mPopupWindow.setContentView(view);
//        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                invitePopupListener.dismiss();
//            }
//        });
//        mPopupWindow.setFocusable(true);
//        mPopupWindow.setOutsideTouchable(true);
    }


    @SuppressLint("NewApi")
    public void StartPop() {
        if (mPopupWindow != null) {
            invitePopupListener.open();
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

    public interface InvitePopupListener {
        void copyLink();

        void email(String url);

        void dismiss();

        void open();
    }

    public void setInvitePopupListener(InvitePopupListener invitePopupListener) {
        this.invitePopupListener = invitePopupListener;
    }

    private InvitePopupListener invitePopupListener;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.copylink:
                invitePopupListener.copyLink();
                ClipData myClip;
                String text = htmlUrl.getText().toString();
                myClip = ClipData.newPlainText("text", text);
                myClipboard.setPrimaryClip(myClip);
                Toast.makeText(mContext, "Link Copied", Toast.LENGTH_SHORT).show();
                dismiss();
                break;
            case R.id.email:
                invitePopupListener.email(htmlUrl.getText().toString());
                dismiss();
                break;
            case R.id.closebtn:
                dismiss();
                break;
            default:
                break;
        }
    }

}
