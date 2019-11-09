package com.ub.techexcel.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.tool.TextTool;
import com.ub.kloudsync.activity.TeamSpaceBean;

import org.feezu.liuli.timeselector.Utils.TextUtil;

import java.util.List;

public class SpaceAdapter extends RecyclerView.Adapter<SpaceAdapter.RecycleHolder> {

    private List<TeamSpaceBean> list;
    private Context context;
    private boolean isSyncRoom;
    private boolean isSwitch;
    boolean fromSearch;
    String keyword;


    public void clearSelect() {
        if (list != null) {
            for (TeamSpaceBean space : list) {
                space.setSelect(false);
            }
        }
    }

    public void setFromSearch(boolean fromSearch, String keyword) {
        this.fromSearch = fromSearch;
        this.keyword = keyword;
    }

    public void setSpaces(List<TeamSpaceBean> spaces) {
        list.clear();
        list.addAll(spaces);
        notifyDataSetChanged();
    }

    public SpaceAdapter(Context context, List<TeamSpaceBean> list, boolean isSyncRoom, boolean isSwitch) {
        this.context = context;
        this.isSwitch = isSwitch;
        this.list = list;
        this.isSyncRoom = isSyncRoom;
    }

    public SpaceAdapter(Context context, List<TeamSpaceBean> list) {
        this.context = context;
        this.list = list;
        isSyncRoom = false;
        isSwitch = false;
    }

    public interface OnItemLectureListener {
        void onItem(TeamSpaceBean teamSpaceBean);

        void select(TeamSpaceBean teamSpaceBean);
    }

    public void setOnItemLectureListener(OnItemLectureListener onItemLectureListener) {
        this.onItemLectureListener = onItemLectureListener;
    }

    private OnItemLectureListener onItemLectureListener;


    @Override
    public RecycleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.space_item, parent, false);
        RecycleHolder holder = new RecycleHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecycleHolder holder, int position) {

        final TeamSpaceBean item = list.get(position);
        holder.documetname.setText(item.getName());
        if (item.getName().length() > 0) {
            if (fromSearch) {
                if (!TextUtil.isEmpty(keyword)) {
                    holder.documetname.setText(TextTool.setSearchColor(Color.parseColor("#72AEFF"), item.getName(), keyword));
                }
            } else {
                holder.tv_sort.setText(item.getName().substring(0, 1).toUpperCase());
            }
        } else {
            holder.tv_sort.setText("");
        }

//        holder.tv_sort.setBackgroundResource(R.drawable.circle_expand);



        if (isSyncRoom) {
            holder.attachmentcount.setText(item.getAttachmentCount() + " " + context.getString(R.string.syncroom));
//            holder.attachmentcount.setText(item.getSyncRoomCount() == 0 ? "" : item.getSyncRoomCount() + " SyncRooms");
//            if (item.getSyncRoomCount() == 0) {
//                holder.attachmentcount.setVisibility(View.GONE);
//            } else {
//                holder.attachmentcount.setVisibility(View.VISIBLE);
//            }
        } else {
            holder.attachmentcount.setText(item.getAttachmentCount() + " " + context.getString(R.string.documents));
//            holder.attachmentcount.setText(item.getAttachmentCount() == 0 ? "" : item.getAttachmentCount() + " documents");
//
//            if (item.getAttachmentCount() == 0) {
//                holder.attachmentcount.setVisibility(View.GONE);
//            } else {
//                holder.attachmentcount.setVisibility(View.VISIBLE);
//            }
        }


        holder.spacerelativelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSwitch) {
                    onItemLectureListener.select(item);
                } else {
                    onItemLectureListener.onItem(item);
                }
            }
        });

        if (item.isSelect()) {
            holder.selectimage.setVisibility(View.VISIBLE);
        } else {
            holder.selectimage.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class RecycleHolder extends RecyclerView.ViewHolder {

        TextView documetname, tv_sort, attachmentcount;

        RelativeLayout spacerelativelayout, countrl;
        ImageView selectimage;

        public RecycleHolder(View itemView) {
            super(itemView);
            documetname = (TextView) itemView.findViewById(R.id.name);
            tv_sort = (TextView) itemView.findViewById(R.id.tv_sort);
            attachmentcount = (TextView) itemView.findViewById(R.id.attachmentcount);
            selectimage = (ImageView) itemView.findViewById(R.id.selectimage);
            spacerelativelayout = (RelativeLayout) itemView.findViewById(R.id.spacerelativelayout);
            countrl = (RelativeLayout) itemView.findViewById(R.id.countrl);
        }

    }

}


