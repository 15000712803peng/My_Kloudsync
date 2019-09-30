package com.ub.techexcel.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.ub.kloudsync.activity.Document;

import java.util.List;

/**
 * Created by wang on 2018/2/8.
 */

public class CurrentTeamAdapter extends RecyclerView.Adapter<CurrentTeamAdapter.RecycleHolder> {
    private List<Document> list;
    private Context context;

    public CurrentTeamAdapter(Context context, List<Document> list) {
        this.context = context;
        this.list = list;
    }

    public interface OnItemLectureListener {
        void onItem(Document lesson);
    }

    public void setOnItemLectureListener(OnItemLectureListener onItemLectureListener) {
        this.onItemLectureListener = onItemLectureListener;
    }

    private OnItemLectureListener onItemLectureListener;

    @Override
    public RecycleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.document_item, parent, false);
        RecycleHolder holder = new RecycleHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecycleHolder holder, int position) {
        final Document item = list.get(position);
        holder.documetname.setText(item.getTitle());
        holder.lin_favour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemLectureListener != null) {
                    onItemLectureListener.onItem(item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class RecycleHolder extends RecyclerView.ViewHolder {
        TextView documetname;
        LinearLayout lin_favour;

        public RecycleHolder(View itemView) {
            super(itemView);
            documetname = (TextView) itemView.findViewById(R.id.documetname);
            lin_favour = (LinearLayout) itemView.findViewById(R.id.lin_favour);
        }
    }

}


