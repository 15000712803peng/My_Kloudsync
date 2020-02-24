package com.ub.techexcel.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.HeaderRecyclerAdapter;
import com.kloudsync.techexcel.bean.Team;

public class NewTeamAdapter extends HeaderRecyclerAdapter<Team> {

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.team_item, parent, false));
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, final int realPosition, Team data) {
        final Team item = mDatas.get(realPosition);
        ItemHolder holder = (ItemHolder) viewHolder;
        holder.documetname.setText(item.getName());
        if (item.isSelected()) {
            holder.select.setSelected(true);
        } else {
            holder.select.setSelected(false);
        }

    }

    class ItemHolder extends RecyclerView.ViewHolder {
        TextView documetname;
        LinearLayout itemLayout;
        ImageView select;

        public ItemHolder(View itemView) {
            super(itemView);
            documetname = itemView.findViewById(R.id.documetname);
            itemLayout = itemView.findViewById(R.id.lin_favour);
            select = itemView.findViewById(R.id.selectimage);
        }
    }


}


