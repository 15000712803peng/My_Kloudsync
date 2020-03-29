package com.kloudsync.techexcel.dialog;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.RequestContactData;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class ContactOutsideKloudDialog implements OnClickListener {

    private Context mContext;
    private SimpleDraweeView contactImage;
    private TextView nameText;
    private TextView phoneText;
    private TextView promtText;
    private TextView cancel, ok;
    private View view;
    String phone;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                dialog.dismiss();
                break;
            case R.id.ok:
                Observable.just("Request_Invite").observeOn(Schedulers.io()).map(new Function<String, JSONObject>() {
                    @Override
                    public JSONObject apply(String s) throws Exception {
                        return ServiceInterfaceTools.getinstance().syncInviteNewToCompany(phone);

                    }
                }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject jsonObject) throws Exception {
                        if(jsonObject.has("RetCode")){
                            if(jsonObject.getInt("RetCode") == 0){
                                // 邀请接口调用成功
                                showInviteSuccessDialog();
                            }else {
                                new CenterToast.Builder(mContext).setSuccess(false).setMessage(mContext.getString(R.string.operate_failure)).create().show();
                            }
                        }else {
                            new CenterToast.Builder(mContext).setSuccess(false).setMessage(mContext.getString(R.string.operate_failure)).create().show();
                        }
                        dismiss();
                    }
                }).subscribe();
//                dialog.dismiss();
                break;

            default:
                break;
        }
    }

    private InviteContactSuccessDialog inviteContactSuccessDialog;

    private void showInviteSuccessDialog(){
        if(inviteContactSuccessDialog != null){
            if(inviteContactSuccessDialog.isShowing()){
                inviteContactSuccessDialog.dismiss();
            }
            inviteContactSuccessDialog = null;
        }
        inviteContactSuccessDialog = new InviteContactSuccessDialog(mContext);
        inviteContactSuccessDialog.show(phone);
    }

    public ContactOutsideKloudDialog(Context context) {
        this.mContext = context;
        getPopupWindowInstance();
        dialog.getWindow().setWindowAnimations(R.style.PopupAnimation3);
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

    @SuppressWarnings("deprecation")
    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.dialog_requesr_contact_info, null);
        cancel = (TextView) view.findViewById(R.id.cancel);
        ok = (TextView) view.findViewById(R.id.ok);
        contactImage = view.findViewById(R.id.img_contact);
        nameText = view.findViewById(R.id.txt_name);
        phoneText = view.findViewById(R.id.txt_phone);
        promtText = view.findViewById(R.id.txt_prompt);
        dialog = new Dialog(mContext, R.style.my_dialog);
        dialog.setContentView(view);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.75f);
        dialog.getWindow().setAttributes(lp);
        cancel.setOnClickListener(this);
        ok.setOnClickListener(this);
    }

    public void show(String phone) {
        this.phone = phone;
        if (!TextUtils.isEmpty(phone)) {
            nameText.setText(phone);
        }
        phoneText.setVisibility(View.GONE);
        promtText.setText("该用户不存在kloud系统中,\n点击添加邀请成为公司联系人");

        if (dialog != null) {
            dialog.show();
        }
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public boolean isShowing() {
        if (dialog != null) {
            return dialog.isShowing();
        }
        return false;
    }

}
