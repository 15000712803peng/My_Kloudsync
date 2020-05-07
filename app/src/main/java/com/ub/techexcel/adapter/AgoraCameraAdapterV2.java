package com.ub.techexcel.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.EventShowFullAgora;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;
import com.kloudsync.techexcel.view.CircleImageView;
import com.ub.techexcel.bean.AgoraMember;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AgoraCameraAdapterV2 extends RecyclerView.Adapter<AgoraCameraAdapterV2.ViewHolder> {

    private LayoutInflater inflater;
    private List<AgoraMember> users;
    private Context mContext;
    private ImageLoader imageLoader;
    private int viewType;
    private int displayType = TYPE_ACTIVIE_SPEAKER;
    public static final int TYPE_ALL = 1;
    public static final int TYPE_ACTIVIE_SPEAKER = 2;
    public static final int TYPE_SELECT_SPEAKER = 3;
    private AgoraMember speakerMember;

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public List<AgoraMember> getUsers() {
        return users;
    }

    public AgoraCameraAdapterV2(Context context) {
        this.mContext = context;
        inflater = LayoutInflater.from(mContext);
        users = new ArrayList<>();
        imageLoader = new ImageLoader(context);
    }

    private OnCameraOptionsListener onCameraOptionsListener;

    public void setOnCameraOptionsListener(OnCameraOptionsListener onCameraOptionsListener) {
        this.onCameraOptionsListener = onCameraOptionsListener;
    }

    public interface OnCameraOptionsListener {
        void onCameraFrameClick(View itemView, AgoraMember member, int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = inflater.inflate(R.layout.small_camera_item, parent, false);
        } else if (viewType == 1) {
            view = inflater.inflate(R.layout.medium_camera_item, parent, false);
        } else if (viewType == 2) {
            view = inflater.inflate(R.layout.large_camera_item, parent, false);
        } else {
            view = inflater.inflate(R.layout.small_camera_item, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return viewType;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final ViewHolder myHolder = holder;
        AgoraMember user = null;
        if (displayType == TYPE_ALL || displayType == TYPE_SELECT_SPEAKER) {
            user = users.get(position);
        } else {
            user = speakerMember;
        }
        Log.e("check_speaker", "onBindViewHolder,display_type:" + displayType + ",speakerMember:" + speakerMember);

        if (user == null) {
            return;
        }

        holder.vedioFrame.removeAllViews();
        if (holder.vedioFrame.getChildCount() == 0) {
            View d = inflater.inflate(R.layout.framelayout_head, null);
            TextView videoname = (TextView) d.findViewById(R.id.videoname);
            ViewParent parent = videoname.getParent();
            if (parent != null) {
                ((RelativeLayout) parent).removeView(videoname);
            }
            holder.vedioFrame.addView(videoname);
            SurfaceView target = user.getSurfaceView();
            Log.e("agora_camera_adapter", "is_mute_video:" + user.isMuteVideo() + ",surfaceview:" + target);
            if (!user.isMuteVideo()) {
                if (target != null) {
                    user.setSurfaceShowing(true);
                    target.setVisibility(View.VISIBLE);
                    stripSurfaceView(target);
                    holder.vedioFrame.addView(target, 0, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                }
            } else {
                user.setSurfaceShowing(false);
                Log.e("onBindViewHolder", "target_gone");
                if (target != null) {
                    stripSurfaceView(target);
                    target.setVisibility(View.INVISIBLE);
                }

            }

            if (user.isSelect()) {
                holder.selectMemberContainer.setBackgroundResource(R.drawable.bg_select_member);
                holder.selectPeopleImage.setImageResource(R.drawable.icon_select_people);
            } else {
                holder.selectMemberContainer.setBackgroundResource(R.drawable.shape_transparent);
                holder.selectPeopleImage.setImageResource(R.drawable.shape_transparent);

            }

            final AgoraMember _user = user;
            myHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onCameraOptionsListener != null) {
                        onCameraOptionsListener.onCameraFrameClick(myHolder.itemView, _user, position);
                    }
//                    showFull(user);

                }
            });

            //---
            if (!TextUtils.isEmpty(user.getUserName())) {
                holder.nameText.setText(user.getUserName());
            } else {
                holder.nameText.setText("");
            }

            if (user.isMuteAudio()) {
                holder.audioStatusImage.setImageResource(R.drawable.microphone);
            } else {
                holder.audioStatusImage.setImageResource(R.drawable.microphone_enable);
                user.setHaveShowUnMuteAudioImage(true);
            }

            if (TextUtils.isEmpty(user.getIconUrl())) {
                holder.iconImage.setImageResource(R.drawable.hello);
            } else {
                imageLoader.DisplayImage(user.getIconUrl(), holder.iconImage);
            }

            if (user.isMuteVideo()) {
                holder.iconImage.setVisibility(View.VISIBLE);
            } else {
                holder.iconImage.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (displayType == TYPE_ALL || displayType == TYPE_SELECT_SPEAKER) {
            return users.size();
        } else {
            return 1;
        }
    }

    public AgoraMember getItem(int position) {
        if (displayType == TYPE_ALL || displayType == TYPE_SELECT_SPEAKER) {
            return users.get(position);
        } else if (displayType == TYPE_ACTIVIE_SPEAKER) {
            return speakerMember;
        }

        return speakerMember;

    }

    protected final void stripSurfaceView(SurfaceView view) {
        ViewParent parent = view.getParent();
        if (parent != null) {
            ((FrameLayout) parent).removeView(view);
        }
    }

    public void muteOrOpenCamera(int myId, boolean isMute) {
        int index = users.indexOf(new AgoraMember(myId));
        if (index >= 0 && index < users.size()) {
            users.get(index).setMuteVideo(isMute);
            Log.e("muteOrOpenCamera", "isMute:" + isMute);
            notifyDataSetChanged();
        }
    }

    public void setMembers(List<AgoraMember> users) {
        if (users != null) {
            this.users.clear();
            this.users.addAll(users);
            Collections.sort(users);
        }
        if(displayType == TYPE_ALL){
            notifyDataSetChanged();
        }

    }

    public void addUser(AgoraMember user) {
        int index = users.indexOf(user);
        if (index >= 0) {
            AgoraMember member = users.get(index);
            member.setMuteVideo(user.isMuteVideo());
            member.setMuteAudio(user.isMuteAudio());
            if (member.getSurfaceView() == null) {
                member.setSurfaceView(user.getSurfaceView());
                if (!user.isMuteVideo()) {
                    member.setMuteVideo(user.isMuteVideo());
                    if (displayType == TYPE_ALL) {
                        notifyItemChanged(index);
                    }
                }
            }
            Log.e("AgoraCameraAdapter", "contain,user,return");
            return;
        }
        Log.e("AgoraCameraAdapter", "do add,user");

        this.users.add(user);
        Collections.sort(users);
        if (displayType == TYPE_ALL) {
            notifyItemInserted(this.users.indexOf(user));
        }

    }

    public void removeUser(AgoraMember user) {
        int index = users.indexOf(user);
        if (index >= 0) {
            this.users.remove(user);
//            Collections.sort(users);
            notifyItemRemoved(index);

        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View view) {
            super(view);
            vedioFrame = view.findViewById(R.id.video_view_container);
            nameText = view.findViewById(R.id.txt_name);
            audioStatusImage = view.findViewById(R.id.image_audio_status);
            iconImage = view.findViewById(R.id.member_icon);
            selectMemberContainer = view.findViewById(R.id.select_peple_container);
            selectPeopleImage = view.findViewById(R.id.icon_select_people);
        }

        public FrameLayout vedioFrame;
        public TextView nameText;
        public ImageView audioStatusImage;
        public CircleImageView iconImage;
        public RelativeLayout selectMemberContainer;
        public ImageView selectPeopleImage;
    }

    public void reset() {
        for (AgoraMember member : this.users) {
            SurfaceView surfaceView = member.getSurfaceView();
            if (surfaceView != null) {
                stripSurfaceView(surfaceView);
            }
        }
        this.users.clear();
        notifyDataSetChanged();
    }

    public void muteVideo(AgoraMember member, boolean isMute) {
        int index = this.users.indexOf(member);
        if (index >= 0) {
            this.users.get(index).setMuteVideo(isMute);
            notifyItemChanged(index);
        }
    }

    public void muteAudio(AgoraMember member, boolean isMute) {
        int index = this.users.indexOf(member);
        Log.e("AgoraCameraAdapter", "muteAudio:" + isMute + ",index:" + index);
        if (index >= 0) {
            this.users.get(index).setMuteAudio(isMute);
            notifyItemChanged(index);
        }
    }

    public void refreshAgoraMember(AgoraMember agoraMember) {
        int index = this.users.indexOf(agoraMember);
        if (index >= 0) {
            Log.e("AgoraCameraAdapter", "refresh_agora_member");
            notifyItemChanged(index);
        }
    }

    public void refreshVideoStatus(AgoraMember member) {

        int index = this.users.indexOf(member);

        if (index >= 0) {
            AgoraMember agoraMember = this.users.get(index);

            if (member.isMuteVideo() == agoraMember.isMuteVideo()) {
                if (!agoraMember.isMuteVideo()) {
                    if (!agoraMember.isSurfaceShowing()) {
                        notifyItemChanged(index);
                    }
                } else {
                    if (agoraMember.isSurfaceShowing()) {
                        notifyItemChanged(index);
                    }
                }
            }

        }
    }

    public void refreshAudioStatus(AgoraMember member) {
        int index = this.users.indexOf(member);

        if (index >= 0) {
            AgoraMember agoraMember = this.users.get(index);

            if (!(agoraMember.isMuteAudio() == member.isMuteAudio())) {
                Log.e("refreshAudioStatus", "member:" + member.isMuteAudio() + ",agoraMember:" + agoraMember.isMuteAudio());
                agoraMember.setMuteAudio(member.isMuteAudio());
                notifyItemChanged(index);

            } else {
                Log.e("refreshAudioStatus", "member:" + member.isMuteAudio() + ",agoraMember:" + agoraMember.isMuteAudio());
                Log.e("refreshAudioStatus", "have_show_un_mute:" + agoraMember.isHaveShowUnMuteAudioImage());
                if (!agoraMember.isMuteAudio()) {
                    if (!agoraMember.isHaveShowUnMuteAudioImage()) {
                        notifyItemChanged(index);
                    }
                }
            }

        }
    }

    public void showFull(int position) {
        EventShowFullAgora showFullAgora = new EventShowFullAgora();
        showFullAgora.setAgoraMember(users.get(position));
        EventBus.getDefault().post(showFullAgora);
    }

    public void showFull(AgoraMember member) {
        EventShowFullAgora showFullAgora = new EventShowFullAgora();
        showFullAgora.setAgoraMember(member);
        EventBus.getDefault().post(showFullAgora);
    }

    public void setMySelfVedioSurface(SurfaceView surface, int userId) {
        int index = this.users.indexOf(new AgoraMember(userId));
        if (index >= 0) {
            if (this.users.get(index).getSurfaceView() == null) {
                this.users.get(index).setSurfaceView(surface);
                notifyItemChanged(index);
            }
        }
    }

    public void refreshMyAgoraStatus(AgoraMember agoraMember) {
        int index = this.users.indexOf(agoraMember);
//        Log.e("refreshMyAgoraStatus","notifyItemChanged,user:" + this.users.get(index));
        if (index >= 0) {
            AgoraMember _member = this.users.get(index);
            _member.setMuteAudio(agoraMember.isMuteAudio());
            _member.setMuteVideo(agoraMember.isMuteVideo());
            Log.e("refreshMyAgoraStatus", "notifyItemChanged,user:" + this.users.get(index));
            notifyItemChanged(index);
        }
    }

    public void clearSelectedMember() {

        if(speakerMember != null){
            speakerMember.setSelect(false);
            notifyItemChanged(0);
        }

        for (int i = 0; i < this.users.size(); ++i) {
            AgoraMember member = this.users.get(i);
            if (member.isSelect()) {
                member.setSelect(false);
                notifyItemChanged(i);
                break;
            }
        }
    }

    public void refreshSize(int size) {
        viewType = size;
        notifyDataSetChanged();
    }

    public void changeDisplayType(int displayType, AgoraMember agoraMember) {
        if (this.displayType != displayType) {
            notifyDataSetChanged();
            this.displayType = displayType;
        }
        this.speakerMember = agoraMember;

    }

    public void initSpeakerMember(AgoraMember speakerMember){
        this.speakerMember = speakerMember;
        this.speakerMember.setSelect(false);
    }



    public void setSpeakerMember(AgoraMember speakerMember) {
        this.speakerMember = speakerMember;
        this.speakerMember.setSelect(false);
        Log.e("check_speaker", "setSpeakerMember,display_type:" + displayType + ",speakerMember:" + speakerMember);
        notifyItemChanged(0);
    }

    public void expandedListForSelected() {
        this.displayType = TYPE_SELECT_SPEAKER;
        clearSelectedMember();
        notifyDataSetChanged();
    }
}
