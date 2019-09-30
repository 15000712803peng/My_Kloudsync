package com.kloudsync.techexcel.help;

import android.content.Context;
import android.content.Intent;

import com.kloudsync.techexcel.ui.InviteFromPhoneActivity;

public class InviteManager implements InviteNewDialog.InviteOptionsLinstener {
    InviteNewDialog dialog;
    public static final int INVITE_BOTH_FROM_PHONE_AND_CONTACT = 1;
    public static final int INVITE_JUST_FROM_PHONE = 2;
    public static final int INVITE_JUST_FROM_CONTACT = 3;
    private static InviteManager instance;
    Context context;
    private int spaceId;
    private int type;

    private InviteManager(Context context) {
        this.context = context;
        dialog = new InviteNewDialog(context);
        dialog.setOptionsLinstener(this);
    }

    public InviteManager spaceId(int spaceId) {
        this.spaceId = spaceId;
        return this;
    }

    public InviteManager type(int type) {
        this.type = type;
        init(type);
        return this;
    }

    private void init(int type) {
        switch (type) {
            case INVITE_BOTH_FROM_PHONE_AND_CONTACT:
                break;
            case INVITE_JUST_FROM_PHONE:
                dialog.setInviteFromContactLayoutGone();
                break;
            case INVITE_JUST_FROM_CONTACT:
                dialog.setInviteFromPhoneLayoutGone();
                break;
        }
    }

    @Override
    public void inviteFromContactOption() {

    }

    @Override
    public void inviteNewOption() {
        Intent intent = new Intent(context, InviteFromPhoneActivity.class);
        context.startActivity(intent);
    }

    public static InviteManager getInstance(Context context) {
        synchronized (InviteManager.class) {
            if (instance == null) {
                instance = new InviteManager(context);
            }
        }
        return instance;
    }
}
