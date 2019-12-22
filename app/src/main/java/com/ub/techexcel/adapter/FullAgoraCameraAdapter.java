package com.ub.techexcel.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
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
import com.kloudsync.techexcel.httpgetimage.ImageLoader;
import com.kloudsync.techexcel.view.CircleImageView;
import com.ub.service.activity.WatchCourseActivity2;
import com.ub.service.activity.WatchCourseActivity3;
import com.ub.techexcel.bean.AgoraBean;
import com.ub.techexcel.bean.AgoraMember;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.agora.openlive.ui.VideoViewEventListener;

/**
 * Created by wang on 2018/8/8.
 */

public class FullAgoraCameraAdapter extends RecyclerView.Adapter<FullAgoraCameraAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<AgoraMember> members;
    private Context mContext;
    private ImageLoader imageLoader;

    public List<AgoraMember> getUsers() {
        return members;
    }


    public FullAgoraCameraAdapter(Context context) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
        members = new ArrayList<>();
        imageLoader = new ImageLoader(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.full_camera_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ViewHolder myHolder = holder;
        Log.e("FullAgoraCameraAdapter", "members_size:" + members.size());
        final AgoraMember member = members.get(position);
        final FrameLayout holderView = (FrameLayout) myHolder.vedioLayout;
        holderView.removeAllViews();
        int height = mContext.getResources().getDisplayMetrics().heightPixels;
        Rect frame = new Rect();
        ((Activity) mContext).getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int notifiheight = frame.top;
        height -= notifiheight;
        int framelayoutHeight;
        if (members.size() <= 2) {
            framelayoutHeight = height;
        } else if (members.size() > 2 && members.size() <= 10) {
            framelayoutHeight = height / 2;
        } else {
            int line = ((members.size() % 5) == 0 ? 0 : 1);
            framelayoutHeight = height / (members.size() / 5 + line);
        }

        SurfaceView target = member.getSurfaceView();
        holderView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, framelayoutHeight));
        if (!member.isMuteVideo()) {
            target.setVisibility(View.VISIBLE);
            stripSurfaceView(target);
            Log.e("FullAgoraCameraAdapter", "add_surface_view:" + framelayoutHeight);
            holderView.addView(target, 0, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, framelayoutHeight));
        } else {
            Log.e("FullAgoraCameraAdapter", "surface_view_gone");
            stripSurfaceView(target);
            target.setVisibility(View.INVISIBLE);
        }

        if (TextUtils.isEmpty(member.getUserName())) {
            myHolder.nameText.setVisibility(View.GONE);
            myHolder.nameText.setText("");
        } else {
            myHolder.nameText.setVisibility(View.VISIBLE);
            myHolder.nameText.setText(member.getUserName());
        }

        if(member.isMuteAudio()){
            holder.audioStatusImage.setImageResource(R.drawable.icon_command_mic_disable);
        }else {
            holder.audioStatusImage.setImageResource(R.drawable.icon_command_mic_enabel);
        }

        if(TextUtils.isEmpty(member.getIconUrl())){
            holder.iconImage.setImageResource(R.drawable.hello);
        }else {
            imageLoader.DisplayImage(member.getIconUrl(), holder.iconImage);
        }

        if(member.isMuteVideo()){
            holder.vedioStatusImage.setImageResource(R.drawable.icon_command_webcam_disable);
            holder.iconImage.setVisibility(View.VISIBLE);

        }else {
            holder.vedioStatusImage.setImageResource(R.drawable.icon_command_webcam_enable);
            holder.iconImage.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        int sizeLimit = members.size();
        return sizeLimit;
    }

    public AgoraMember getItem(int position) {
        return members.get(position);
    }

    public void setMembers(List<AgoraMember> datas) {
        if (datas != null) {
            members.clear();
            for (AgoraMember mData : datas) {
                Log.e("bigsetData", mData + "        ");
                SurfaceView surfaceView = mData.getSurfaceView();
                members.add(mData);
            }
        }
        notifyDataSetChanged();
    }

    public void muteOrOpenCamera(int myId, boolean isMute) {
        int index = members.indexOf(new AgoraMember(myId));
        if (index >= 0 && index < members.size()) {
            members.get(index).setMuteVideo(isMute);
            Log.e("muteOrOpenCamera", "isMute:" + isMute);
            notifyDataSetChanged();
        }
    }

    protected final void stripSurfaceView(SurfaceView view) {
        ViewParent parent = view.getParent();
        if (parent != null) {
            ((FrameLayout) parent).removeView(view);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View view) {
            super(view);
            vedioLayout = view.findViewById(R.id.layout_vedio);
            nameText = view.findViewById(R.id.txt_name);
            audioStatusImage = view.findViewById(R.id.image_audio_status);
            vedioStatusImage = view.findViewById(R.id.image_vedio_status);
            iconImage = view.findViewById(R.id.member_icon);
        }

        public FrameLayout vedioLayout;
        public TextView nameText;
        public ImageView audioStatusImage;
        public ImageView vedioStatusImage;
        public CircleImageView iconImage;
    }

    public void reset() {
        for (AgoraMember member : this.members) {
            SurfaceView surfaceView = member.getSurfaceView();
            stripSurfaceView(surfaceView);
        }
        this.members.clear();
        notifyDataSetChanged();
    }

    public void muteVideo(AgoraMember member,boolean isMute){
        int index = this.members.indexOf(member);
        if(index >= 0){
            this.members.get(index).setMuteVideo(isMute);
            notifyItemChanged(index);
        }
    }

    public void muteAudio(AgoraMember member,boolean isMute){
        int index = this.members.indexOf(member);
        if(index >= 0){
            this.members.get(index).setMuteAudio(isMute);
            notifyItemChanged(index);
        }
    }


    public void addUser(AgoraMember user) {

        if (members.contains(user)) {
            return;
        }
        this.members.add(user);
        Collections.sort(members);
        notifyItemInserted(this.members.indexOf(user));
    }

    public void removeUser(AgoraMember user) {
        int index = members.indexOf(user);
        if(index >= 0){
            this.members.remove(user);
            Collections.sort(members);
            notifyItemRemoved(this.members.indexOf(user));
        }

    }

}
