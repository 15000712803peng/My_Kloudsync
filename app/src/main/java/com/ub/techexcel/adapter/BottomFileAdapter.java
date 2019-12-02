package com.ub.techexcel.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.MeetingDocument;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;
import com.kloudsync.techexcel.view.CircleImageView;
import com.kloudsync.techexcel.view.RoundProgressBar;
import com.ub.techexcel.bean.LineItem;

import java.util.ArrayList;
import java.util.List;


public class BottomFileAdapter extends RecyclerView.Adapter<BottomFileAdapter.ViewHolder> {


    private LayoutInflater inflater;
    private List<MeetingDocument> mDatas = new ArrayList<>();
    public ImageLoader imageLoader;
    private OnDocumentClickListener onDocumentClickListener;
    public Context context;
    private int documentId;

    public void setDocumentId(List<MeetingDocument> documents,int documentId) {
        this.documentId = documentId;
        mDatas.clear();
        mDatas.addAll(documents);
        if(mDatas.size() <0){
            return;
        }

        for(MeetingDocument document : mDatas){
            if(document.getItemID() == documentId){
                document.setSelect(true);
            }else {
                document.setSelect(false);
            }
        }
    }

    private void clearSelected(){

        for(MeetingDocument document : mDatas){
            document.setSelect(false);
        }
    }

    public interface OnDocumentClickListener {
        void onDocumentClick(MeetingDocument document);
    }

    public void setOnDocumentClickListener(OnDocumentClickListener onDocumentClickListener) {
        this.onDocumentClickListener = onDocumentClickListener;
    }

    public BottomFileAdapter(Context context, List<MeetingDocument> datas) {
        inflater = LayoutInflater.from(context);
        mDatas.clear();
        mDatas.addAll(datas);
        imageLoader = new ImageLoader(context);
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View a) {
            super(a);
        }

        CircleImageView icon;
        TextView name, identlyTv;
        LinearLayout headll, bgisshow, bgisshow2;
        RoundProgressBar rpb_update;
    }


    @Override
    public int getItemCount() {
        return mDatas.size();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.icon = (CircleImageView) view.findViewById(R.id.studenticon);
        viewHolder.name = (TextView) view.findViewById(R.id.studentname);
        viewHolder.identlyTv = (TextView) view.findViewById(R.id.identlyTv);
        viewHolder.headll = (LinearLayout) view.findViewById(R.id.headll);
        viewHolder.bgisshow = (LinearLayout) view.findViewById(R.id.bgisshow);
        viewHolder.bgisshow2 = (LinearLayout) view.findViewById(R.id.bgisshow2);
        viewHolder.rpb_update = (RoundProgressBar) view.findViewById(R.id.rpb_update);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final MeetingDocument document = mDatas.get(position);
            holder.bgisshow2.setVisibility(View.GONE);
            holder.rpb_update.setVisibility(View.GONE);
            holder.bgisshow.setVisibility(View.VISIBLE);
            holder.icon.setVisibility(View.VISIBLE);
            holder.name.setText((position + 1) + "");
            holder.icon.setImageResource(R.drawable.documento);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onDocumentClickListener != null){
                        clearSelected();
                        document.setSelect(true);
                        onDocumentClickListener.onDocumentClick(document);
                        notifyDataSetChanged();
                    }
                }
            });

            if (document.isSelect()) {
                holder.bgisshow.setBackgroundResource(R.drawable.course_bg1);
            } else {
                holder.bgisshow.setBackgroundResource(R.drawable.course_bg2);
            }

    }

    public void setProgress(long total, long current, LineItem att) {
        int pb = (int) (current * 100 / total);
        att.setProgress(pb);
        notifyDataSetChanged();
    }

}
