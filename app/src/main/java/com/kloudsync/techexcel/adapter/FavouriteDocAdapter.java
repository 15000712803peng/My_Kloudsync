package com.kloudsync.techexcel.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.ub.kloudsync.activity.Document;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FavouriteDocAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private List<Document> documents = new ArrayList<>();
    private OnItemClickListener onItemClickListener = null;

    public interface OnItemClickListener {
        void onItemClick(Document favorite);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick((Document) v.getTag());
        }
    }

    public FavouriteDocAdapter(List<Document> documents) {
        this.documents = documents;
    }

    public void setDocuments(List<Document> documents) {
        if (this.documents != null) {
            this.documents.clear();
            this.documents.addAll(documents);
        } else {
            this.documents = documents;
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doc_favorite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        final Document favorite = documents.get(position);
        holder.tv_favour.setText(favorite.getTitle());
        holder.tv_synccount.setText(favorite.getSyncCount() + "");
        holder.lin_sync.setVisibility((0 == favorite.getSyncCount()) ? View.GONE : View.VISIBLE);
        holder.itemView.setOnClickListener(this);
        String createData = new SimpleDateFormat("YYYY-MM-dd HH:MM:SS").format(Long.parseLong(favorite.getCreatedDate()));
        holder.time.setText("" + createData);
        holder.itemView.setTag(favorite);
    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_favour;
        TextView tv_synccount;
        LinearLayout lin_favour;
        LinearLayout lin_sync;
        TextView time;

        ViewHolder(View view) {
            super(view);
            tv_favour = (TextView) view.findViewById(R.id.tv_favour);
            tv_synccount = (TextView) view.findViewById(R.id.tv_synccount);
            lin_favour = (LinearLayout) view.findViewById(R.id.lin_favour);
            lin_sync = (LinearLayout) view.findViewById(R.id.lin_sync);
            time = view.findViewById(R.id.txt_time);
        }
    }
}
