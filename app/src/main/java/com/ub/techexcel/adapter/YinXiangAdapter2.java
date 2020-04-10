package com.ub.techexcel.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.ub.techexcel.bean.SoundtrackBean;
import com.ub.techexcel.tools.Tools;

import java.util.List;

public class YinXiangAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SoundtrackBean> mlist;
    private List<SoundtrackBean> allList;
    private Context mContext;
    private Uri defaultImageUri;
    private MeetingConfig meetingConfig;

    public YinXiangAdapter2(Context context, List<SoundtrackBean> mlist, List<SoundtrackBean> allList, MeetingConfig meetingConfig) {
        this.mContext = context;
        this.mlist = mlist;
        this.allList = allList;
        this.meetingConfig=meetingConfig;

        for (int i = 0; i < allList.size(); i++) {
            SoundtrackBean soundtrackBean = allList.get(i);
            for (int i1 = 0; i1 < mlist.size(); i1++) {
                SoundtrackBean soundtrackBean1 = mlist.get(i1);
                if (soundtrackBean.getSoundtrackID() == soundtrackBean1.getSoundtrackID()) {
                    soundtrackBean.setCheck(true);
                }
            }
        }
        defaultImageUri = Tools.getUriFromDrawableRes(context, R.drawable.hello);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.yinxiang_item2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;

        final SoundtrackBean soundtrackBean = allList.get(position);
        holder.title.setText(soundtrackBean.getTitle());
        holder.username.setText(soundtrackBean.getUserName());
        holder.duration.setText(soundtrackBean.getDuration());

        holder.checkbox.setChecked(soundtrackBean.isCheck());


        if(meetingConfig.getSystemType()==0){
            holder.soundtype.setVisibility(View.GONE);
        }else{  //教育
            holder.soundtype.setVisibility(View.VISIBLE);
        }
        holder.soundtype.setText(soundtrackBean.getMusicType()==0?"伴奏音乐":"演唱");

        if (soundtrackBean.isCheck()) {
            holder.checkbox.setEnabled(false);
        } else {
            holder.checkbox.setEnabled(true);
        }

        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                soundtrackBean.setCheck(b);
            }
        });

        String url2 = soundtrackBean.getAvatarUrl();
        Uri imageUri2;
        if (!TextUtils.isEmpty(url2)) {
            imageUri2 = Uri.parse(url2);
        } else {
            imageUri2 = defaultImageUri;
        }
        holder.image.setImageURI(imageUri2);
    }

    @Override
    public int getItemCount() {
        return allList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView username;
        TextView soundtype;
        TextView duration;
        SimpleDraweeView image;
        CheckBox checkbox;

        ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            username = (TextView) view.findViewById(R.id.username);
            soundtype = (TextView) view.findViewById(R.id.soundtype);
            duration = (TextView) view.findViewById(R.id.duration);
            image = (SimpleDraweeView) view.findViewById(R.id.image);
            checkbox = (CheckBox) view.findViewById(R.id.checkbox);
        }
    }
}
