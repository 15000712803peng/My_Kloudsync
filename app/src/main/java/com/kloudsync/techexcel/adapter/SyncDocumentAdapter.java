package com.kloudsync.techexcel.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.ub.techexcel.bean.LineItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pingfan on 2017/12/11.
 */

public class SyncDocumentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {


    private List<LineItem> mlist = new ArrayList<>();
    private Context context;

    private OnItemClickListener onItemClickListener = null;

    public static interface OnItemClickListener {
        void Onitems(int position);
    }

    public void setItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public SyncDocumentAdapter(List<LineItem> mlist) {
        this.mlist = mlist;
    }

    public SyncDocumentAdapter(Context context, List<LineItem> mlist) {
        this.mlist = mlist;
        this.context = context;
    }

    public void UpdateRV(List<LineItem> mlist) {
        this.mlist = mlist;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sdocument, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        LineItem item = mlist.get(position);
        holder.tv_synccount.setText(item.getSyncRoomCount() + "");
        holder.tv_name.setText(item.getCreatedBy());
        holder.img_head.setImageURI(item.getCreatedByAvatar());
        String url = item.getUrl();
        if (url.contains("<") && url.contains(">")) {
            url = url.substring(0, url.lastIndexOf("<")) + "1" + url.substring(url.lastIndexOf("."), url.length());
        }
        Uri imageUri = Uri.parse(url);
        holder.sdv_bg.setImageURI(imageUri);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.Onitems(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView img_head;
        SimpleDraweeView sdv_bg;
        TextView tv_synccount;
        TextView tv_name;

        ViewHolder(View view) {
            super(view);
            tv_synccount = (TextView) view.findViewById(R.id.tv_synccount);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            img_head = (SimpleDraweeView) view.findViewById(R.id.img_head);
            sdv_bg = (SimpleDraweeView) view.findViewById(R.id.sdv_bg);
        }
    }
}
