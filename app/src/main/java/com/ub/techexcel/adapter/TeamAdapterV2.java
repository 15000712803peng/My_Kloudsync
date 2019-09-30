package com.ub.techexcel.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.Team;
import com.kloudsync.techexcel.tool.TextTool;

import java.util.List;

/**
 * Created by wang on 2018/2/8.
 */

public class TeamAdapterV2 extends RecyclerView.Adapter<TeamAdapterV2.RecycleHolder> {
    private List<Team> teams;
    private Context context;
    int currentTeamId;
    String keyword;

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public TeamAdapterV2(Context context, List<Team> teams) {
        this.context = context;
        this.teams = teams;
    }

    public void setTeams(List<Team> teams) {
        if (this.teams != null) {
            this.teams.clear();
            this.teams.addAll(teams);
        } else {
            this.teams = teams;
        }
        notifyDataSetChanged();

    }

    public int getCurrentTeamId() {
        return currentTeamId;
    }

    public void setCurrentTeamId(int currentTeamId) {
        this.currentTeamId = currentTeamId;
    }

    public interface OnItemClickListener {
        void onItemClick(Team teamData);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private OnItemClickListener onItemClickListener;

    @Override
    public RecycleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.team_item_v2, parent, false);
        RecycleHolder holder = new RecycleHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecycleHolder holder, int position) {
        final Team team = teams.get(position);
        if (!TextUtils.isEmpty(keyword)) {
            holder.teamName.setText(TextTool.setSearchColor(Color.parseColor("#72AEFF"), team.getName(), keyword));
        } else {
            holder.teamName.setText(team.getName());
        }
//        holder.teamName.setText(team.getName());
        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    if (team.getItemID() != currentTeamId) {
                        onItemClickListener.onItemClick(team);
                    }
                }
            }
        });
        if (team.getItemID() == currentTeamId) {
            holder.selectImage.setVisibility(View.VISIBLE);
        } else {
            holder.selectImage.setVisibility(View.GONE);
        }

//        switch (position % 5) {
//            case 0:
//                holder.teamIcon.setImageResource(R.drawable.avtar_1);
//                break;
//            case 1:
//                holder.teamIcon.setImageResource(R.drawable.avtar_2);
//                break;
//            case 2:
//                holder.teamIcon.setImageResource(R.drawable.avtar_3);
//                break;
//            case 3:
//                holder.teamIcon.setImageResource(R.drawable.avtar_4);
//                break;
//            case 4:
//                holder.teamIcon.setImageResource(R.drawable.avtar_5);
//                break;
//            default:
//                holder.teamIcon.setImageResource(R.drawable.avtar_6);
//                break;
//        }

    }

    @Override
    public int getItemCount() {
        return teams.size();
    }

    class RecycleHolder extends RecyclerView.ViewHolder {

        TextView teamName;
        TextView spacesCount;
        LinearLayout itemLayout;
        ImageView selectImage;
        TextView teamIcon;

        public RecycleHolder(View itemView) {
            super(itemView);
            teamName = (TextView) itemView.findViewById(R.id.txt_team_name);
            spacesCount = itemView.findViewById(R.id.txt_spaces_count);
            itemLayout = (LinearLayout) itemView.findViewById(R.id.layout_item);
            selectImage = (ImageView) itemView.findViewById(R.id.image_sel);
            teamIcon = (TextView) itemView.findViewById(R.id.txt_team_icon);
        }

    }

}


