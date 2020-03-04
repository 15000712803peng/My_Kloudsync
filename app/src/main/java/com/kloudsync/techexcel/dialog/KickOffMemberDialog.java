package com.kloudsync.techexcel.dialog;

import android.app.Activity;
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
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.MeetingMember;
import com.kloudsync.techexcel.bean.RequestContactData;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.message.HelloFriendMessage;
import com.kloudsync.techexcel.help.MeetingKit;
import com.kloudsync.techexcel.tool.MessageTool;
import com.kloudsync.techexcel.tool.SocketMessageManager;
import com.kloudsync.techexcel.view.ClearEditText;
import com.ub.techexcel.tools.ServiceInterfaceTools;
import com.ub.techexcel.tools.Tools;

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

public class KickOffMemberDialog implements OnClickListener {

    private Context mContext;
    private SimpleDraweeView memberImage;
    private TextView promtText,nameText;
    private MeetingConfig meetingConfig;
    private MeetingMember meetingMember;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                dialog.dismiss();
                break;
            case R.id.ok:
                SocketMessageManager.getManager(mContext).sendMessage_KickMember(meetingConfig,meetingMember);
//                MeetingKit.getInstance().requestMeetingMembers(meetingConfig);
                dismiss();
                break;
            default:
                break;
        }
    }

    public KickOffMemberDialog(Context context) {
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
        view = layoutInflater.inflate(R.layout.dialog_kick_off_member, null);
        cancel = (TextView) view.findViewById(R.id.cancel);
        ok = (TextView) view.findViewById(R.id.ok);
        memberImage = view.findViewById(R.id.img_member);
        promtText = view.findViewById(R.id.txt_prompt);
        nameText = view.findViewById(R.id.txt_name);
        dialog = new Dialog(mContext, R.style.my_dialog);
        dialog.setContentView(view);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        if(Tools.isOrientationPortrait((Activity)mContext)){
            lp.width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.75f);
        }else {
            lp.width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.45f);
        }

        dialog.getWindow().setAttributes(lp);
        cancel.setOnClickListener(this);
        ok.setOnClickListener(this);
    }

    public void show(MeetingConfig meetingConfig,MeetingMember meetingMember) {
        this.meetingConfig = meetingConfig;
        this.meetingMember = meetingMember;
        if (meetingMember.getAvatarUrl() != null) {
            memberImage.setImageURI(Uri.parse(meetingMember.getAvatarUrl()));
        }

        nameText.setText(meetingMember.getUserName());
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
