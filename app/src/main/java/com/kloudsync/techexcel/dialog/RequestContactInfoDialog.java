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
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.message.HelloFriendMessage;
import com.kloudsync.techexcel.tool.MessageTool;
import com.kloudsync.techexcel.view.ClearEditText;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

public class RequestContactInfoDialog implements OnClickListener {

    private RequestContactData contactData;
    private Context mContext;
    private SimpleDraweeView contactImage;
    private TextView nameText;
    private TextView phoneText;
    private TextView promtText;
    private static final String OK_OPTIONS_CHAT = "CHAT";
    private static final String OK_OPTIONS_OTHER_COMPANY = "OTHER_COMPANY";
    private static final String OK_OPTIONS_SAME_COMPANY = "SAME_COMPANY";

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                dialog.dismiss();
                break;
            case R.id.ok:
                String operation = (String) ok.getTag();
                if (operation.equals(OK_OPTIONS_CHAT)) {
                    RongIM.getInstance().startConversation(mContext, Conversation.ConversationType.PRIVATE, contactData.getRongCloudId() + "", contactData.getUserName());

                } else if (operation.equals(OK_OPTIONS_OTHER_COMPANY)) {
                    Observable.just("Request").observeOn(Schedulers.io()).map(new Function<String, JSONObject>() {
                        @Override
                        public JSONObject apply(String s) throws Exception {
                            return ServiceInterfaceTools.getinstance().syncApplyChat(contactData.getUserId(), AppConfig.SchoolID);
                        }
                    }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<JSONObject>() {
                        @Override
                        public void accept(JSONObject jsonObject) throws Exception {
                            if (jsonObject.has("code")) {
                                int code = jsonObject.getInt("code");
                                if (code == 0) {
                                    sendHelloFriendMessage();
                                    RongIM.getInstance().startConversation(mContext, Conversation.ConversationType.PRIVATE, contactData.getRongCloudId() + "", contactData.getUserName());
                                }
                            }
                        }
                    }).subscribe();

                } else if (operation.equals(OK_OPTIONS_SAME_COMPANY)) {
                    Observable.just("Request").observeOn(Schedulers.io()).map(new Function<String, JSONObject>() {
                        @Override
                        public JSONObject apply(String s) throws Exception {
                            return ServiceInterfaceTools.getinstance().syncAddContact(contactData.getUserId(), AppConfig.SchoolID);
                        }
                    }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<JSONObject>() {
                        @Override
                        public void accept(JSONObject jsonObject) throws Exception {
                            if (jsonObject.has("code")) {
                                int code = jsonObject.getInt("code");
                                if (code == 0) {
                                    sendHelloFriendMessage();
                                    RongIM.getInstance().startConversation(mContext, Conversation.ConversationType.PRIVATE, contactData.getRongCloudId() + "", contactData.getUserName());
                                } else if (code == 37) {
                                    String msg = jsonObject.getString("msg");
                                    if (TextUtils.isEmpty(msg)) {
                                        msg = mContext.getString(R.string.operate_failure);
                                    }
                                    new CenterToast.Builder(mContext).setSuccess(false).setMessage(msg).create();
                                }
                            }
                        }

                    }).subscribe();
                }
                dialog.dismiss();
                break;

            default:
                break;
        }
    }

    private void sendHelloFriendMessage() {
        HelloFriendMessage friendMsg = new HelloFriendMessage();
        friendMsg.setRongCloudId(contactData.getRongCloudId() + "");
        MessageTool.sendMessage(friendMsg, friendMsg.getRongCloudId(), Conversation.ConversationType.PRIVATE, new IRongCallback.ISendMediaMessageCallback() {
            @Override
            public void onProgress(io.rong.imlib.model.Message message, int i) {
                Log.e("sendHello", "onProgress");

            }

            @Override
            public void onCanceled(io.rong.imlib.model.Message message) {
                Log.e("sendHello", "onCanceled");

            }

            @Override
            public void onAttached(io.rong.imlib.model.Message message) {
                Log.e("sendHello", "onAttached");

            }

            @Override
            public void onSuccess(io.rong.imlib.model.Message message) {
                Log.e("sendHello", "onSuccess");
            }

            @Override
            public void onError(io.rong.imlib.model.Message message, RongIMClient.ErrorCode errorCode) {
                Log.e("sendHello", "onError:" + message.getContent() + ",errorcode:" + errorCode);
            }
        });
    }


    public RequestContactInfoDialog(Context context) {
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

    private TextView cancel, ok;
    private ClearEditText editText;
    private View view;

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

    public void show(RequestContactData contactData, String phone) {
        Log.e("RequestContactInfoDialog", "dialog_show:" + 2);
        this.contactData = contactData;
        if (contactData.getAvatarUrl() != null) {
            contactImage.setImageURI(Uri.parse(contactData.getAvatarUrl()));
        }
        if (!TextUtils.isEmpty(contactData.getUserName())) {
            nameText.setText(contactData.getUserName());
        }

        if (!TextUtils.isEmpty(phone)) {
            phoneText.setText(phone);
        }

        fillViewByContactData(contactData);

        if (dialog != null) {
            dialog.show();
        }
    }

    private void fillViewByContactData(RequestContactData contactData) {
        if (contactData.isIfMyContact()) {
            promtText.setText(mContext.getString(R.string.prompt_is_my_personal_contact));
            ok.setText(mContext.getString(R.string.mtChat));
            ok.setTag(OK_OPTIONS_CHAT);
        } else {
            if (contactData.isIfInSameCompany()) {
                if (contactData.isIfMyContact()) {
                    promtText.setText(mContext.getString(R.string.prompt_is_my_personal_contact));
                    ok.setText(mContext.getString(R.string.mtChat));
                    ok.setTag(OK_OPTIONS_CHAT);
                } else {
                    promtText.setText(mContext.getString(R.string.prompt_is_in_same_company));
                    ok.setText("申请聊天");
                    ok.setTag(OK_OPTIONS_SAME_COMPANY);
                }

            } else {
                if (contactData.isIfMyContact()) {
                    promtText.setText(mContext.getString(R.string.prompt_is_my_personal_contact));
                    ok.setText(mContext.getString(R.string.mtChat));
                    ok.setTag(OK_OPTIONS_CHAT);
                } else {
                    promtText.setText(mContext.getString(R.string.prompt_is_in_other_company));
                    ok.setText(mContext.getString(R.string.satOk));
                    ok.setTag(OK_OPTIONS_OTHER_COMPANY);
                }

            }
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
