package com.kloudsync.techexcel.pc.ui;

import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.BaseActivity;
import com.kloudsync.techexcel.bean.ContactDetailData;
import com.kloudsync.techexcel.bean.FriendContact;

import io.rong.imkit.RongIM;

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
        chatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(friendContact != null){
                                    RongIM.getInstance().startPrivateChat(ContactDetailActivity.this,
                        friendContact.getRongCloudId()+"", friendContact.getUserName());
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

            if(!TextUtils.isEmpty(contactDetail.getPrimaryPhone())){
                phoneText.setText(contactDetail.getPrimaryPhone());
            }
        }
    }
}
