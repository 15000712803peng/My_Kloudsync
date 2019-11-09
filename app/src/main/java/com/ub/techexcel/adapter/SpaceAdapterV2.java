package com.ub.techexcel.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.ub.kloudsync.activity.TeamSpaceBean;

import java.util.List;

public class SpaceAdapterV2 extends RecyclerView.Adapter<SpaceAdapterV2.RecycleHolder> {

    private List<TeamSpaceBean> spaces;
    private Context context;
    private boolean isSyncRoom;
    private ImageView selImage;
    boolean isSelected;

    public SpaceAdapterV2(Context context, List<TeamSpaceBean> spaces, boolean isSyncRoom) {
        this.context = context;
        this.spaces = spaces;
        this.isSyncRoom = isSyncRoom;
    }

    public SpaceAdapterV2(Context context, List<TeamSpaceBean> spaces) {
        this.context = context;
        this.spaces = spaces;
    }

    public void setSpaces(List<TeamSpaceBean> spaces) {
        if (spaces != null) {
            this.spaces.clear();
            this.spaces.addAll(spaces);
        } else {
            this.spaces = spaces;
        }
        notifyDataSetChanged();
    }


    public interface OnItemClickListener {
        void onItemClick(TeamSpaceBean teamSpaceBean);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private OnItemClickListener onItemClickListener;


    @Override
    public RecycleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.space_item2, parent, false);
        RecycleHolder holder = new RecycleHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecycleHolder holder, int position) {

        final TeamSpaceBean item = spaces.get(position);
        holder.documetname.setText(item.getName());
        if (item.getName().length() > 0) {
            holder.tv_sort.setText(item.getName().substring(0, 1).toUpperCase());
        } else {
            holder.tv_sort.setText("");
        }
        holder.attachmentcount.setText(item.getAttachmentCount() + " documents");
        if (isSyncRoom) {
//            holder.attachmentcount.setVisibility(View.GONE);
            holder.attachmentcount.setText(item.getAttachmentCount() + " " + context.getString(R.string.syncroom));

        } else {
            holder.attachmentcount.setText(item.getAttachmentCount() + " " + context.getString(R.string.documents));
//            holder.attachmentcount.setVisibility(View.VISIBLE);

        }
        if (item.isSelect()) {
            holder.selImage.setVisibility(View.VISIBLE);
        } else {
            holder.selImage.setVisibility(View.INVISIBLE);
        }
        holder.spaceItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSelected();
                item.setSelect(!item.isSelect());
                notifyDataSetChanged();
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(item);
                }
            }
        });

    }

    private void clearSelected() {
        for (TeamSpaceBean item : spaces) {
            item.setSelect(false);
        }
    }

    @Override
    public int getItemCount() {
        return spaces.size();
    }

    class RecycleHolder extends RecyclerView.ViewHolder {

        TextView documetname, tv_sort, attachmentcount;
        ImageView selImage;
        LinearLayout spaceItemLayout;

        public RecycleHolder(View itemView) {
            super(itemView);
            documetname = (TextView) itemView.findViewById(R.id.name);
            tv_sort = (TextView) itemView.findViewById(R.id.tv_sort);
            attachmentcount = (TextView) itemView.findViewById(R.id.attachmentcount);
            selImage = itemView.findViewById(R.id.image_sel);
            spaceItemLayout = itemView.findViewById(R.id.layout_space_item);
        }

    }


}


