package com.ub.techexcel.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.ub.kloudsync.activity.TeamSpaceBean;

import java.util.List;

/**
 * Created by wang on 2018/2/8.
 */

public class SpaceAdapter2 extends RecyclerView.Adapter<SpaceAdapter2.RecycleHolder> {

    private List<TeamSpaceBean> list;
    private Context context;
    private boolean isSyncRoom;

    public SpaceAdapter2(Context context, List<TeamSpaceBean> list, boolean isSyncRoom) {
        this.context = context;
        this.list = list;
        this.isSyncRoom = isSyncRoom;
    }

    public interface OnItemLectureListener {
        void onItem(TeamSpaceBean teamSpaceBean);
    }

    public void setOnItemLectureListener(OnItemLectureListener onItemLectureListener) {
        this.onItemLectureListener = onItemLectureListener;
    }

    private OnItemLectureListener onItemLectureListener;



    @Override
    public RecycleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.space_item2, parent, false);
        RecycleHolder holder = new RecycleHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecycleHolder holder, int position) {

        final TeamSpaceBean item = list.get(position);
        holder.documetname.setText(item.getName());
        if (item.getName().length() > 0) {
            holder.tv_sort.setText(item.getName().substring(0, 1).toUpperCase());
        } else {
            holder.tv_sort.setText("");
        }

        holder.attachmentcount.setText(item.getAttachmentCount() == 0 ? "" : item.getAttachmentCount() + "");
        holder.syncroomcount.setText(item.getSyncRoomCount() == 0 ? "" : item.getSyncRoomCount() + "");

        if (isSyncRoom) {
            holder.syncroomcount.setVisibility(View.VISIBLE);
            holder.attachmentcount.setVisibility(View.GONE);
        } else {
            holder.syncroomcount.setVisibility(View.GONE);
            holder.attachmentcount.setVisibility(View.VISIBLE);
        }


        holder.spacerelativelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                onItemLectureListener.onItem(item);

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class RecycleHolder extends RecyclerView.ViewHolder {

        TextView documetname, tv_sort, attachmentcount, syncroomcount;

        RelativeLayout spacerelativelayout;

        public RecycleHolder(View itemView) {
            super(itemView);
            documetname = (TextView) itemView.findViewById(R.id.name);
            tv_sort = (TextView) itemView.findViewById(R.id.tv_sort);
            attachmentcount = (TextView) itemView.findViewById(R.id.attachmentcount);
            syncroomcount = (TextView) itemView.findViewById(R.id.syncroomcount);
            spacerelativelayout = (RelativeLayout) itemView.findViewById(R.id.spacerelativelayout);
        }

    }

}


