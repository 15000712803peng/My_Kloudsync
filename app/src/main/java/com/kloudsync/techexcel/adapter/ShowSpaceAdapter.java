package com.kloudsync.techexcel.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.info.Space;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pingfan on 2017/12/11.
 */

public class ShowSpaceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<Space> mlist = new ArrayList<>();
    private int itemID = -1;


    public ShowSpaceAdapter(List<Space> mlist) {
        this.mlist = mlist;
    }
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }


    public void UpdateRV(List<Space> mlist, int itemID) {
        this.mlist = mlist;
        this.itemID = itemID;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_showsp, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        final Space space = mlist.get(position);
        holder.tv_name.setText(space.getName());
        holder.tv_document.setText(space.getAttachmentCount() + " documents");
        holder.tv_document.setVisibility(space.getAttachmentCount() > 0? View.VISIBLE : View.VISIBLE);

        if(space.getName().length() > 1) {
            holder.tv_avatar.setText(space.getName().substring(0, 2));
        }else{
            holder.tv_avatar.setText("");
        }
        switch (position % 3){
            case 0:
                holder.tv_avatar.setBackgroundResource(R.drawable.orange_cicle);
                break;
            case 1:
                holder.tv_avatar.setBackgroundResource(R.drawable.circle_expand);
                break;
            case 2:
                holder.tv_avatar.setBackgroundResource(R.drawable.blue_circle);
                break;
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(position);
            }
        });
        if(itemID == space.getItemID()){
            holder.img_se.setVisibility(View.VISIBLE);
            holder.tv_name.setTextColor(holder.tv_name.getContext().getResources().getColor(R.color.skyblue));
            holder.tv_document.setTextColor(holder.tv_name.getContext().getResources().getColor(R.color.skyblue));
        } else {
            holder.img_se.setVisibility(View.GONE);
            holder.tv_name.setTextColor(holder.tv_name.getContext().getResources().getColor(R.color.darkgrey));
            holder.tv_document.setTextColor(holder.tv_name.getContext().getResources().getColor(R.color.newgrey));
        }
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        TextView tv_document;
        TextView tv_avatar;
        ImageView img_se;
        ViewHolder(View view) {
            super(view);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_document = (TextView) view.findViewById(R.id.tv_document);
            tv_avatar = (TextView) view.findViewById(R.id.tv_avatar);
            img_se = (ImageView) view.findViewById(R.id.img_se);
        }
    }
}
