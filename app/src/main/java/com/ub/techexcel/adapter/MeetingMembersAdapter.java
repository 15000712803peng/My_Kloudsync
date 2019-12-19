package com.ub.techexcel.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.MeetingMember;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.view.CircleImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 2017/8/18.
 */

public class MeetingMembersAdapter extends RecyclerView.Adapter<MeetingMembersAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private List<MeetingMember> meetingMembers = new ArrayList<>();
    public ImageLoader imageLoader;

    public interface OnMemberClickedListener{
        void onMemberClicked(MeetingMember meetingMember);
    }

    private OnMemberClickedListener onMemberClickedListener;


    public void setOnMemberClickedListener(OnMemberClickedListener onMemberClickedListener) {
        this.onMemberClickedListener = onMemberClickedListener;
    }

    public MeetingMembersAdapter(Context context, List<MeetingMember> members) {
        inflater = LayoutInflater.from(context);
        meetingMembers.clear();
        meetingMembers.addAll(members);
        imageLoader = new ImageLoader(context);
    }

    public List<MeetingMember> getmDatas() {
        return meetingMembers;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View a) {
            super(a);
        }

        CircleImageView icon;
        TextView name;
        TextView idently;
        LinearLayout bgisshow;
        LinearLayout headll;
    }

    public void updateMembers(List<MeetingMember> members){
        Log.e("MeetingMembersAdapter","updateMembers:" + members);
        meetingMembers.clear();
        meetingMembers.addAll(members);
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return meetingMembers.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.meeting_member_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.icon = (CircleImageView) view.findViewById(R.id.studenticon);
        viewHolder.name = (TextView) view.findViewById(R.id.studentname);
        viewHolder.idently = (TextView) view.findViewById(R.id.identlyTv);
        viewHolder.bgisshow = (LinearLayout) view.findViewById(R.id.bgisshow);
        viewHolder.headll = (LinearLayout) view.findViewById(R.id.headll);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
      final  MeetingMember member = meetingMembers.get(position);
        holder.name.setText(member.getUserName());

        String url = member.getAvatarUrl();
        if (null == url || url.length() < 1) {
            holder.icon.setImageResource(R.drawable.hello);
        } else {
            imageLoader.DisplayImage(url, holder.icon);
        }

        if (member.getPresenter() == 1) {
            holder.idently.setVisibility(View.VISIBLE);
            holder.idently.setText(R.string.presenter);
            holder.bgisshow.setBackgroundResource(R.drawable.course_bg1);
        } else {
            holder.bgisshow.setBackgroundResource(R.drawable.course_bg2);
            holder.idently.setText("");
        }

        if (member.getIsOnline() == 1) {
            holder.headll.setAlpha(1.0f);
            holder.headll.setClickable(true);
        } else {
            holder.headll.setAlpha(0.5f);
            holder.headll.setClickable(false);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(member.getPresenter() != 1){
                    onMemberClickedListener.onMemberClicked(member);
                }
            }
        });


    }
}
