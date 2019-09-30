package com.kloudsync.techexcel.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.ub.kloudsync.activity.TeamSpaceBean;

import java.util.ArrayList;
import java.util.List;

public class SpaceSelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<TeamSpaceBean> spaces = new ArrayList<>();
    private int currentSpaceId = -1;
    private OnItemClickListener mOnItemClickListener = null;

    public interface OnItemClickListener {
        void onItemClick(int spaceId);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void update(List<TeamSpaceBean> mlist, int currentSpaceId) {
        this.currentSpaceId = currentSpaceId;
        this.spaces.clear();
        this.spaces.addAll(mlist);
        notifyDataSetChanged();
    }

    public void setSpaces(List<TeamSpaceBean> spaces) {
        this.spaces = spaces;
    }

    public List<TeamSpaceBean> getSpaces() {
        return spaces;
    }

    public void setCurrentSpaceId(int currentSpaceId) {
        this.currentSpaceId = currentSpaceId;
    }

    public int getCurrentSpaceId() {
        return currentSpaceId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.space_item_sel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        final TeamSpaceBean space = spaces.get(position);
        holder.tv_name.setText(space.getName());
        holder.tv_document.setText(space.getAttachmentCount() + " documents");
        holder.tv_document.setVisibility(space.getAttachmentCount() > 0 ? View.VISIBLE : View.VISIBLE);
//        if (space.getName().length() > 1) {
//            holder.tv_avatar.setText(space.getName().substring(0, 2));
//        } else {
//            holder.tv_avatar.setText("");
//        }
        holder.tv_avatar.setBackgroundResource(R.drawable.circle_expand);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(space.getItemID());
                }
            }
        });
        if (currentSpaceId == space.getItemID()) {
            holder.img_sel.setVisibility(View.VISIBLE);
            holder.tv_name.setTextColor(holder.tv_name.getContext().getResources().getColor(R.color.skyblue));
            holder.tv_document.setTextColor(holder.tv_name.getContext().getResources().getColor(R.color.skyblue));
        } else {
            holder.img_sel.setVisibility(View.GONE);
            holder.tv_name.setTextColor(holder.tv_name.getContext().getResources().getColor(R.color.darkgrey));
            holder.tv_document.setTextColor(holder.tv_name.getContext().getResources().getColor(R.color.newgrey));
        }
    }

    @Override
    public int getItemCount() {
        return spaces.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        TextView tv_document;
        TextView tv_avatar;
        ImageView img_sel;

        ViewHolder(View view) {
            super(view);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_document = (TextView) view.findViewById(R.id.tv_document);
            tv_avatar = (TextView) view.findViewById(R.id.tv_avatar);
            img_sel = (ImageView) view.findViewById(R.id.img_sel);
        }
    }
}
