package com.kloudsync.techexcel.pc.ui;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.BaseActivity;
import com.kloudsync.techexcel.bean.ContactDetailData;
import com.kloudsync.techexcel.bean.FriendContact;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.message.HelloFriendMessage;
import com.kloudsync.techexcel.tool.MessageTool;
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

/**
 * Created by tonyan on 2020/2/22.
 */

public class ContactDetailActivity extends BaseActivity {
    private ContactDetailData contactDetail;
    private FriendContact friendContact;
    private ImageView contactImage;
    private TextView contactName;
    private TextView phoneText;
    private TextView mailText;
    private TextView descText;
    private ImageView backImage;
    private RelativeLayout chatLayout;
    private TextView chatOrApplyText;

    @Override
    protected int setLayout() {
        return R.layout.activity_contact_detail;
    }

    @Override
    protected void initView() {
        contactDetail = new Gson().fromJson(getIntent().getStringExtra("contact_detail"), ContactDetailData.class);
        friendContact = new Gson().fromJson(getIntent().getStringExtra("friend_contact"), FriendContact.class);
        contactImage = findViewById(R.id.img_contact);
        contactName = findViewById(R.id.txt_contact_name);
        phoneText = findViewById(R.id.txt_phone);
        mailText = findViewById(R.id.txt_mail);
        descText = findViewById(R.id.txt_describe);
        backImage = findViewById(R.id.image_back);
        chatLayout = findViewById(R.id.layout_chat);
        chatOrApplyText = findViewById(R.id.txt_chat_or_apply);

        if (friendContact.getStatus() == 1) {
            chatOrApplyText.setText("聊天");
        } else {
            chatOrApplyText.setText("申请聊天");
        }

        chatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (friendContact != null) {
                    if (friendContact.getStatus() == 1) {
                        RongIM.getInstance().startPrivateChat(ContactDetailActivity.this,
                                friendContact.getRongCloudId() + "", friendContact.getUserName());
                    } else {
                        Observable.just("Request").observeOn(Schedulers.io()).map(new Function<String, JSONObject>() {
                            @Override
                            public JSONObject apply(String s) throws Exception {
                                return ServiceInterfaceTools.getinstance().syncApplyChat(friendContact.getUserId(), AppConfig.SchoolID);
                            }
                        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<JSONObject>() {
                            @Override
                            public void accept(JSONObject jsonObject) throws Exception {
                                if(jsonObject.has("code")){
                                    int code = jsonObject.getInt("code");
                                    if(code == 0){
                                        sendHelloFriendMessage();
                                        RongIM.getInstance().startConversation(ContactDetailActivity.this, Conversation.ConversationType.PRIVATE, friendContact.getRongCloudId()+"", friendContact.getUserName());

                                    }
                                }
                            }
                        }).subscribe();
                    }

                }
            }
        });
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initViewsByContact(contactDetail);
    }

    private void sendHelloFriendMessage(){
        HelloFriendMessage friendMsg = new HelloFriendMessage();
        friendMsg.setRongCloudId(friendContact.getRongCloudId() +"");
        MessageTool.sendMessage(friendMsg, friendMsg.getRongCloudId(), Conversation.ConversationType.PRIVATE, new IRongCallback.ISendMediaMessageCallback() {
            @Override
            public void onProgress(io.rong.imlib.model.Message message, int i) {
                Log.e("sendHello","onProgress");

            }

            @Override
            public void onCanceled(io.rong.imlib.model.Message message) {
                Log.e("sendHello","onCanceled");

            }

            @Override
            public void onAttached(io.rong.imlib.model.Message message) {
                Log.e("sendHello","onAttached");

            }

            @Override
            public void onSuccess(io.rong.imlib.model.Message message) {
                Log.e("sendHello","onSuccess");
            }

            @Override
            public void onError(io.rong.imlib.model.Message message, RongIMClient.ErrorCode errorCode) {
                Log.e("sendHello","onError:" + message.getContent() + ",errorcode:" + errorCode);
            }
        });
    }

    private void initViewsByContact(ContactDetailData contactDetail) {
        if (contactDetail != null) {
            if (contactDetail.getAvatarUrl() != null) {
                contactImage.setImageURI(Uri.parse(contactDetail.getAvatarUrl()));
            }
            if (!TextUtils.isEmpty(contactDetail.getUserName())) {
                contactName.setText(contactDetail.getUserName());
            }
            if (!TextUtils.isEmpty(contactDetail.getEmail())) {
                mailText.setText(contactDetail.getEmail());
            }
            if (!TextUtils.isEmpty(contactDetail.getDescription())) {
                descText.setText(contactDetail.getDescription());
            }

            if (!TextUtils.isEmpty(contactDetail.getPrimaryPhone())) {
                phoneText.setText(contactDetail.getPrimaryPhone());
            }
        }
    }
}
