package com.kloudsync.techexcel.help;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.kloudsync.techexcel.R;

public class InviteNewContactDialog implements OnClickListener{

    private Context mContext;
    private TextView tv_yes,tv_cancel;
    private EditText edittext;


    public InviteNewContactDialog(Context context) {
        this.mContext = context;
        getPopupWindowInstance();
        dialog.getWindow().setWindowAnimations(R.style.PopupAnimation3);
    }

    public interface InviteOptionsLinstener {

        void inviteFromPhone(String phone);
    }


    private InviteOptionsLinstener optionsLinstener;


    public void setOptionsLinstener(InviteOptionsLinstener optionsLinstener) {
        this.optionsLinstener = optionsLinstener;
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
    private View popupWindow;

    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        popupWindow = layoutInflater.inflate(R.layout.invitenewcontactdialog, null);
        tv_yes = popupWindow.findViewById(R.id.tv_yes);
        tv_yes.setOnClickListener(this);
        tv_cancel = popupWindow.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(this);
        edittext = popupWindow.findViewById(R.id.edittext);
        dialog = new Dialog(mContext, R.style.my_dialog);
        dialog.setContentView(popupWindow);

        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.8f);
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_cancel:
                dialog.dismiss();
                break;
            case R.id.tv_yes:
                optionsLinstener.inviteFromPhone(edittext.getText().toString());
                dialog.dismiss();
                break;
        }
    }

    public boolean isShowing(){
        if(dialog != null){
            return dialog.isShowing();
        }
        return false;
    }

    public void cancel(){
        if(dialog != null){
            dialog.cancel();
        }
    }

    public void show() {
        if(dialog != null && !dialog.isShowing())
            dialog.show();
    }


}
