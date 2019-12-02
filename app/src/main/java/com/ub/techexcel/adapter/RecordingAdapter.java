package com.ub.techexcel.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.ub.techexcel.bean.Record;

import java.util.List;

public class RecordingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Record> mlist;

    private Context mContext;


    public RecordingAdapter(Context context, List<Record> mlist) {
        this.mContext = context;
        this.mlist = mlist;

    }

    public interface FavoritePoPListener2 {

        void playYinxiang(Record soundtrackBean);
    }

    private FavoritePoPListener2 favoritePoPListener;

    public void setFavoritePoPListener(FavoritePoPListener2 favoritePoPListener) {
        this.favoritePoPListener = favoritePoPListener;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recording_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        final Record record = mlist.get(position);
        holder.title.setText(record.getTitle());
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoritePoPListener.playYinxiang(record);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
        }
    }
}
