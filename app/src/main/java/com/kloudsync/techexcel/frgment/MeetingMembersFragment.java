package com.kloudsync.techexcel.frgment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.EventRefreshMembers;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.MeetingMember;
import com.kloudsync.techexcel.bean.MeetingType;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;
import com.kloudsync.techexcel.view.CircleImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by tonyan on 2019/11/9.
 */

public class MeetingMembersFragment extends MyFragment{

    private RecyclerView membersList;
    int type;
    private MeetingConfig meetingConfig;
    private MeetingMembersAdapter membersAdapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getInt("type");
        meetingConfig = (MeetingConfig) getArguments().getSerializable("meeting_config");
        EventBus.getDefault().register(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }


    @Override
    protected void lazyLoad() {
        Log.e("MeetingMembersFragment","lazyLoad");
    }

    private View view;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshMeetingMembers(EventRefreshMembers refreshMembers) {
        this.meetingConfig = refreshMembers.getMeetingConfig();
        loadMembers();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view == null){
            view = inflater.inflate(R.layout.fragment_meeting_members,container,false);
            membersList = view.findViewById(R.id.list_members);
            loadMembers();
        }
        return view;
    }

    List<MeetingMember> meetingMembers = new ArrayList<>();

    private void loadMembers(){
        meetingMembers.clear();
        if(type == 1){
            meetingMembers.addAll(meetingConfig.getMeetingMembers());
            Collections.sort(meetingMembers);
        }else if(type == 2){
            meetingMembers.addAll(meetingConfig.getMeetingAuditor());
        }else if(type == 3){
            meetingMembers.addAll(meetingConfig.getMeetingInvitors());
        }

        membersList.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));

        if (membersAdapter == null) {
            membersAdapter = new MeetingMembersAdapter(getActivity(), meetingMembers);
            membersList.setAdapter(membersAdapter);
        } else {
            membersAdapter.updateMembers(meetingMembers);
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View view) {
            super(view);
            icon = (CircleImageView) view.findViewById(R.id.member_icon);
            name = (TextView) view.findViewById(R.id.name);
            presenter = view.findViewById(R.id.txt_presenter);
            me = view.findViewById(R.id.txt_is_me);
            type = view.findViewById(R.id.txt_type);
        }
        public CircleImageView icon;
        public TextView name;
        public TextView presenter;
        public TextView me;
        public TextView type;

    }

    public class MeetingMembersAdapter extends RecyclerView.Adapter<ViewHolder> {
        private LayoutInflater inflater;
        private List<MeetingMember> meetingMembers = new ArrayList<>();
        public ImageLoader imageLoader;


        public MeetingMembersAdapter(Context context, List<MeetingMember> members) {
            inflater = LayoutInflater.from(context);
            meetingMembers.clear();
            meetingMembers.addAll(members);
            imageLoader = new ImageLoader(context);
        }

        public List<MeetingMember> getmDatas() {
            return meetingMembers;
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
        public  ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.meeting_member, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
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

            if(member.getPresenter() == 1){
                holder.presenter.setVisibility(View.VISIBLE);
            }else {
                holder.presenter.setVisibility(View.GONE);
            }

            if((member.getUserId()+"").equals(AppConfig.UserID)){
                holder.me.setVisibility(View.VISIBLE);
            }else{
                holder.me.setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

        }
    }


}
