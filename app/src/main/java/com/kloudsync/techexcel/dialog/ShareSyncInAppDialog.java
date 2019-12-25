package com.kloudsync.techexcel.dialog;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.HeaderRecyclerAdapter;
import com.kloudsync.techexcel.bean.MeetingDocument;
import com.kloudsync.techexcel.bean.SoundTrack;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ShareTool;
import com.kloudsync.techexcel.info.MyFriend;
import com.kloudsync.techexcel.response.NetworkResponse;
import com.ub.techexcel.bean.LineItem;
import com.ub.techexcel.bean.SoundtrackBean;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ShareSyncInAppDialog implements View.OnClickListener, HeaderRecyclerAdapter.OnItemClickListener, IRongCallback.ISendMessageCallback {
    public Context mContext;
    public int width;
    public int heigth;
    public Dialog dialog;
    private View view;
    private ViewFlipper viewFlipper;
    private View selectFriendView;
    private View sendFriendView;
    private ImageView closeImage;
    private RecyclerView friendList;
    private SingleFriendAdapter friendAdapter;
    private MeetingDocument document;
    private SoundTrack soundTrack;

    //--------------
    private ImageView friendImage;
    private TextView friendName;
    private ImageView docThumbImage;

    private ImageView syncImage;
    private TextView syncName;
    private TextView syncDuration;
    private TextView cancelText;
    private TextView sendText;
    private TextView sendTitleText;
    private RelativeLayout friendLayout;

    public void getPopwindow(Context context) {
        this.mContext = context;
    }

    public ShareSyncInAppDialog(Context context) {
        mContext = context;
        initDialog();
        getFriendList();
    }

    public SoundTrack getSync() {
        return soundTrack;
    }

    public void setSync(SoundTrack soundTrack) {
        this.soundTrack = soundTrack;
    }

    public void setDocument(MeetingDocument document) {
        this.document = document;
    }

    public void initDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.dialog_share_sync, null);
        dialog = new Dialog(mContext, R.style.my_dialog);
        sendTitleText = view.findViewById(R.id.txt_send_title);
        selectFriendView = layoutInflater.inflate(R.layout.view_select_friend, null);
        sendFriendView = layoutInflater.inflate(R.layout.view_send_doc_to_friend, null);
        friendImage = sendFriendView.findViewById(R.id.image_friend_icon);
        friendLayout = sendFriendView.findViewById(R.id.layout_friend);
        friendLayout.setOnClickListener(this);
        friendName = sendFriendView.findViewById(R.id.txt_friend_name);
        docThumbImage = sendFriendView.findViewById(R.id.image_doc_thumb);
        syncImage = sendFriendView.findViewById(R.id.image_sync);
        syncName = sendFriendView.findViewById(R.id.txt_sync_name);
        cancelText = sendFriendView.findViewById(R.id.txt_cancel);
        sendText = sendFriendView.findViewById(R.id.txt_send);
        cancelText.setOnClickListener(this);
        sendText.setOnClickListener(this);
        syncDuration = sendFriendView.findViewById(R.id.txt_sync_duration);
        viewFlipper = view.findViewById(R.id.view_flipper);
        friendList = selectFriendView.findViewById(R.id.list);
        friendList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        friendAdapter = new SingleFriendAdapter();
        friendAdapter.setOnItemClickListener(this);
        friendList.setAdapter(friendAdapter);
        closeImage = view.findViewById(R.id.img_close);
        closeImage.setOnClickListener(this);
        viewFlipper.addView(selectFriendView);
        viewFlipper.addView(sendFriendView);
        width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * (0.4f));
        heigth = (int) (mContext.getResources().getDisplayMetrics().heightPixels * (0.9f));
        viewFlipper = view.findViewById(R.id.view_flipper);
        dialog.setContentView(view);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = width;
        lp.height = heigth;
        dialog.getWindow().setAttributes(lp);
    }


    public boolean isShowing() {
        return dialog.isShowing();
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.cancel();
            dialog = null;
        }
    }

    private MyFriend currentFrind;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_close:
                dismiss();
                break;
            case R.id.image_back:
                if (viewFlipper != null) {
                    viewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext,
                            R.anim.flipper_right_in));
                    viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext,
                            R.anim.flipper_right_out));
                    viewFlipper.showPrevious();
                }
                break;
            case R.id.img_space_close:
                dismiss();
                break;
            case R.id.txt_cancel:
                dismiss();
                break;
            case R.id.txt_send:
                ShareTool.shareDocumentToFriend("", document, currentFrind, this);
                break;
            case R.id.layout_friend:
                if (viewFlipper != null) {
                    viewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext,
                            R.anim.flipper_right_in));
                    viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext,
                            R.anim.flipper_right_out));
                    viewFlipper.showPrevious();
                    sendTitleText.setText("Share Sync to");
                }
                break;
        }
    }

    public void show() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    private void initSendView(MyFriend friend) {
        if (!TextUtils.isEmpty(friend.getAvatarUrl())) {
            friendImage.setImageURI(Uri.parse(friend.getAvatarUrl()));
        }
        friendName.setText(friend.getName());
        if (!TextUtils.isEmpty(document.getSourceFileUrl())) {
            docThumbImage.setImageURI(Uri.parse(document.getSourceFileUrl()));
        }
        if (!TextUtils.isEmpty(soundTrack.getAvatarUrl())) {
            syncImage.setImageURI(Uri.parse(soundTrack.getAvatarUrl()));
        }
        if (!TextUtils.isEmpty(soundTrack.getUserName())) {
            syncName.setText(soundTrack.getUserName());
        }
        if (!TextUtils.isEmpty(soundTrack.getDuration())) {
            syncDuration.setText(soundTrack.getDuration());
        }

    }

    public void getFriendList() {
        ServiceInterfaceTools.getinstance().getFriendList().enqueue(new Callback<NetworkResponse<List<MyFriend>>>() {
            @Override
            public void onResponse(Call<NetworkResponse<List<MyFriend>>> call, Response<NetworkResponse<List<MyFriend>>> response) {
                if (response != null && response.isSuccessful() && response.body() != null) {
                    if (response.body().getRetCode() == AppConfig.RETCODE_SUCCESS) {
                        List<MyFriend> friends = response.body().getRetData();
                        if (friends == null) {
                            friends = new ArrayList<>();
                        }
                        friendAdapter.setDatas(friends);
                    }
                }
            }

            @Override
            public void onFailure(Call<NetworkResponse<List<MyFriend>>> call, Throwable t) {

            }
        });

    }

    @Override
    public void onItemClick(int position, Object data) {
        currentFrind = (MyFriend) data;
        sendTitleText.setText("Share Sync to " + currentFrind.getName());
        initSendView(currentFrind);
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext,
                R.anim.flipper_left_in));
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext,
                R.anim.flipper_left_out));
        viewFlipper.showNext();
    }

    @Override
    public void onAttached(Message message) {

    }

    @Override
    public void onSuccess(Message message) {
        new CenterToast.Builder(mContext).setSuccess(true).setMessage("分享成功").create().show();
        dismiss();
    }

    @Override
    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
        new CenterToast.Builder(mContext).setSuccess(false).setMessage("分享失败").create().show();
        dismiss();
    }

    public class SingleFriendAdapter extends HeaderRecyclerAdapter<MyFriend> {

        @Override
        public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
            return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_friend_item, parent, false));
        }

        @Override
        public void onBind(RecyclerView.ViewHolder viewHolder, final int realPosition, MyFriend data) {
            ItemHolder holder = (ItemHolder) viewHolder;
            holder.teamNameText.setText(data.getName());
            Uri imageUri = null;
            if (!TextUtils.isEmpty(data.getAvatarUrl())) {
                imageUri = Uri.parse(data.getAvatarUrl());
            }
            holder.friendImage.setImageURI(imageUri);
        }

        class ItemHolder extends RecyclerView.ViewHolder {
            TextView teamNameText;
            ImageView friendImage;

            public ItemHolder(View itemView) {
                super(itemView);
                teamNameText = itemView.findViewById(R.id.txt_name);
                friendImage = itemView.findViewById(R.id.image_friend_icon);

            }
        }
    }


}
