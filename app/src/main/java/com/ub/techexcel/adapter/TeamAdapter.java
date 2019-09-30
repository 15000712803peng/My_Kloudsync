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


public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.RecycleHolder> {
    private List<TeamSpaceBean> list;
    private Context context;

    public TeamAdapter(Context context, List<TeamSpaceBean> list) {
        this.context = context;
        this.list = list;
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
        View view = LayoutInflater.from(context).inflate(R.layout.team_item, parent, false);
        RecycleHolder holder = new RecycleHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecycleHolder holder, int position) {
        final TeamSpaceBean item = list.get(position);
        holder.documetname.setText(item.getName());


        holder.lin_favour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemLectureListener.onItem(item);
            }
        });
        if (item.isSelect()) {
            holder.select.setVisibility(View.VISIBLE);
        } else {
            holder.select.setVisibility(View.GONE);
        }

//        switch (position % 5) {
//            case 0:
//                holder.icon.setImageResource(R.drawable.avtar_1);
//                break;
//            case 1:
//                holder.icon.setImageResource(R.drawable.avtar_2);
//                break;
//            case 2:
//                holder.icon.setImageResource(R.drawable.avtar_3);
//                break;
//            case 3:
//                holder.icon.setImageResource(R.drawable.avtar_4);
//                break;
//            case 4:
//                holder.icon.setImageResource(R.drawable.avtar_5);
//                break;
//            default:
//                holder.icon.setImageResource(R.drawable.avtar_6);
//                break;
//        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class RecycleHolder extends RecyclerView.ViewHolder {

        TextView documetname;
        LinearLayout lin_favour;
        ImageView select;
        TextView icon;

        public RecycleHolder(View itemView) {
            super(itemView);
            documetname = itemView.findViewById(R.id.documetname);
            lin_favour = itemView.findViewById(R.id.lin_favour);
            select = itemView.findViewById(R.id.selectimage);
            icon = itemView.findViewById(R.id.icon);
        }

    }

}


