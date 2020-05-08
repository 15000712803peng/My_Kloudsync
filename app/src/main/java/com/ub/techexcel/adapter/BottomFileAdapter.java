package com.ub.techexcel.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.MeetingDocument;
import com.kloudsync.techexcel.help.DocAndMeetingFileListPopupwindow;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;
import com.kloudsync.techexcel.view.RoundProgressBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class BottomFileAdapter extends RecyclerView.Adapter<BottomFileAdapter.ViewHolder> {


    private LayoutInflater inflater;
    private List<MeetingDocument> mDatas = new ArrayList<>();
    public ImageLoader imageLoader;
    private OnDocumentClickListener onDocumentClickListener;
    public Context context;
    private int documentId;
    private MeetingDocument tempDocument;
    private DocAndMeetingFileListPopupwindow mDocAndMeetingFileListPopupwindow;

    public void addTempDocument(MeetingDocument tempDocument){
        this.tempDocument = tempDocument;
        mDatas.add(tempDocument);
        notifyDataSetChanged();
    }



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

        SimpleDraweeView icon;
        TextView name/*, identlyTv*/, mTvYinXiangCount, mTvCreateDate;
        LinearLayout headll/*, bgisshow*/, bgisshow2, mLlyYinXiangCount;
        RoundProgressBar rpb_update;
        ImageView mIvItemDocMore;
    }


    @Override
    public int getItemCount() {
        return mDatas.size();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.icon = view.findViewById(R.id.studenticon);
        viewHolder.name = (TextView) view.findViewById(R.id.studentname);
//        viewHolder.identlyTv = (TextView) view.findViewById(R.id.identlyTv);
        viewHolder.headll = (LinearLayout) view.findViewById(R.id.headll);
//        viewHolder.bgisshow = (LinearLayout) view.findViewById(R.id.bgisshow);
        viewHolder.bgisshow2 = (LinearLayout) view.findViewById(R.id.bgisshow2);
        viewHolder.rpb_update = (RoundProgressBar) view.findViewById(R.id.rpb_update);
        viewHolder.mIvItemDocMore = view.findViewById(R.id.iv_item_doc_more);
        viewHolder.mTvCreateDate = view.findViewById(R.id.tv_item_cteate_date);
        viewHolder.mLlyYinXiangCount = view.findViewById(R.id.lly_item_doc_yin_xiang_count);
        viewHolder.mTvYinXiangCount = view.findViewById(R.id.tv_item_yin_xiang_count);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final MeetingDocument document = mDatas.get(position);
            if(document.isTemp()){
                holder.itemView.setOnClickListener(null);
                holder.bgisshow2.setVisibility(View.VISIBLE);
                holder.rpb_update.setVisibility(View.VISIBLE);
//                holder.bgisshow.setVisibility(View.GONE);
//                holder.icon.setVisibility(View.GONE);
                if(TextUtils.isEmpty(document.getTempDocPrompt())){
                    holder.name.setText("");
                }else {
                    holder.name.setText(/*document.getTempDocPrompt()*/document.getFileName());
                }
                holder.rpb_update.setProgress(document.getProgress());
            }else {
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
                holder.bgisshow2.setVisibility(View.GONE);
                holder.rpb_update.setVisibility(View.GONE);
//                holder.bgisshow.setVisibility(View.VISIBLE);
//                holder.icon.setVisibility(View.VISIBLE);
                int syncCount = document.getSyncCount();
                if (syncCount > 0) {
                    holder.mLlyYinXiangCount.setVisibility(View.VISIBLE);
                    holder.mLlyYinXiangCount.getBackground().setAlpha(26);
                    holder.mTvYinXiangCount.setText(String.valueOf(syncCount));
                }
                holder.name.setText(/*(position + 1) + ""*/document.getFileName());
                String url = document.getAttachmentUrl();
                if (!TextUtils.isEmpty(url)) {
                    url = url.substring(0, url.lastIndexOf("<")) + "1" + url.substring(url.lastIndexOf("."), url.length());
                    Uri imageUri = null;
                    if (!TextUtils.isEmpty(url)) {
                        imageUri = Uri.parse(url);
                    }
                    holder.icon.setImageURI(imageUri);
                }
                String date = document.getCreatedDate();
                if (!TextUtils.isEmpty(date)) {
                    long dd = Long.parseLong(date);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd HH:mm:ss");
                    String haha = simpleDateFormat.format(dd);
                    holder.mTvCreateDate.setText(haha);
                }
                if (document.isSelect()) {
                    holder.headll.setSelected(true);
                } else {
                    holder.headll.setSelected(false);
                }
                holder.mIvItemDocMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mDocAndMeetingFileListPopupwindow == null) {
                            mDocAndMeetingFileListPopupwindow = new DocAndMeetingFileListPopupwindow(context);
                        }
                        mDocAndMeetingFileListPopupwindow.show(holder.mIvItemDocMore);
                    }
                });
            }



    }

    public void refreshTempDoc(int progress,String prompt) {
        if(tempDocument != null){
            tempDocument.setProgress(progress);
            tempDocument.setTempDocPrompt(prompt);
        }
        notifyDataSetChanged();
    }

    public void removeTempDoc(){
        if(tempDocument != null){
            mDatas.remove(tempDocument);
            tempDocument = null;
        }
        notifyDataSetChanged();
    }

}
