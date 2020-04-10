package com.ub.techexcel.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.SoundTrack;
import com.ub.techexcel.bean.SoundtrackBean;
import com.ub.techexcel.tools.Tools;
import com.ub.techexcel.tools.YinxiangOperatorPopup;

import java.util.ArrayList;
import java.util.List;


public class SoundtrackAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SoundTrack> soundTracks = new ArrayList<>();
    private Context mContext;
    private Uri defaultImageUri;
    private View outView;

    public interface OnSoundtrackClickedListener{
        void onSoundtrackClicked(View itemView,SoundTrack soundTrack);
    }

    private OnSoundtrackClickedListener onSoundtrackClickedListener;


    public void setOnSoundtrackClickedListener(OnSoundtrackClickedListener onSoundtrackClickedListener) {
        this.onSoundtrackClickedListener = onSoundtrackClickedListener;
    }
private MeetingConfig meetingConfig;
    public SoundtrackAdapter(Context context, MeetingConfig meetingConfig) {
        this.mContext = context;
        this.meetingConfig=meetingConfig;
        defaultImageUri = Tools.getUriFromDrawableRes(context, R.drawable.hello);
    }



    public void setSoundTracks(List<SoundTrack> soundTracks){
        this.soundTracks.clear();
        this.soundTracks.addAll(soundTracks);
        notifyDataSetChanged();
    }


    public void setView(View outView) {
        this.outView = outView;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.soundtrack_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        final SoundTrack soundTrack = soundTracks.get(position);
        holder.title.setText(soundTrack.getTitle());
        holder.username.setText(soundTrack.getUserName());
        holder.duration.setText(soundTrack.getDuration());
        if (position == soundTracks.size() - 1) {
            holder.divider.setVisibility(View.VISIBLE);
        } else {
            holder.divider.setVisibility(View.GONE);
        }
        if(meetingConfig.getSystemType()==0){
            holder.soundtype.setVisibility(View.GONE);
        }else{  //教育
            holder.soundtype.setVisibility(View.VISIBLE);
        }
        holder.soundtype.setText(soundTrack.getMusicType()==0?"伴奏音乐":"演唱");

        String url = soundTrack.getAvatarUrl();
        Uri imageUri2;
        if (!TextUtils.isEmpty(url)) {
            imageUri2 = Uri.parse(url);
        } else {
            imageUri2 = defaultImageUri;
        }
        holder.image.setImageURI(imageUri2);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onSoundtrackClickedListener != null){
                    onSoundtrackClickedListener.onSoundtrackClicked(holder.operationmore,soundTrack);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return soundTracks.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        RelativeLayout operation;
        TextView username;
        TextView soundtype;
        TextView duration;
        SimpleDraweeView image;
        View divider;
        ImageView operationmore;

        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            username = (TextView) view.findViewById(R.id.username);
            soundtype = (TextView) view.findViewById(R.id.soundtype);
            duration = (TextView) view.findViewById(R.id.duration);
            operation = (RelativeLayout) view.findViewById(R.id.operation);
            image = (SimpleDraweeView) view.findViewById(R.id.image);
            operationmore = view.findViewById(R.id.operationmore);
            divider = view.findViewById(R.id.divider);
        }
    }
}
