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
import com.ub.techexcel.bean.Note;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class SyncRoomNoteListAdapter extends RecyclerView.Adapter<SyncRoomNoteListAdapter.RecycleHolder2> {

    private Context context;

    private List<Note> list = new ArrayList<>();

    public SyncRoomNoteListAdapter(Context context, List<Note> list) {
        this.context = context;
        this.list = list;
    }


    public interface WebCamPopupListener {

        void select(Note noteDetail);

        void notifychangeUserid(String userId);
    }

    public void setWebCamPopupListener(WebCamPopupListener webCamPopupListener) {
        this.webCamPopupListener = webCamPopupListener;
    }

    private WebCamPopupListener webCamPopupListener;


    @Override
    public RecycleHolder2 onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.syncroom_othernotelist_popup_item, parent, false);
        RecycleHolder2 holder = new RecycleHolder2(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(final RecycleHolder2 holder, final int position) {
        final Note noteDetail = list.get(position);
        holder.title.setText(noteDetail.getTitle());
        String date = noteDetail.getCreatedDate();
        if (!TextUtils.isEmpty(date)) {
            long dd = Long.parseLong(date);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd  HH:mm:ss");
            String haha = simpleDateFormat.format(dd);
            holder.date.setText(haha);
        }
        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webCamPopupListener.select(noteDetail);
            }
        });
        holder.pagenumber.setText("Page " + noteDetail.getPageNumber());
        String url = noteDetail.getAttachmentUrl();
        if (!TextUtils.isEmpty(url)) {
            url = url.substring(0, url.lastIndexOf("<")) + "1" + url.substring(url.lastIndexOf("."), url.length());
            Uri imageUri = null;
            if (!TextUtils.isEmpty(url)) {
                imageUri = Uri.parse(url);
            }
            holder.img_url.setImageURI(imageUri);
        }
        holder.operationmore.setVisibility(View.GONE);
        holder.operationmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                NoteOperatorPopup noteOperatorPopup = new NoteOperatorPopup();
//                noteOperatorPopup.getPopwindow(mContext);
//                noteOperatorPopup.setFavoritePoPListener(new NoteOperatorPopup.FavoritePoPListener() {
//                    @Override
//                    public void delete() {
//                        getNoteData(selectId, syncroomid);
//                    }
//                });
//                noteOperatorPopup.StartPop(holder.operationmore, noteDetail);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class RecycleHolder2 extends RecyclerView.ViewHolder {
        TextView title;
        LinearLayout ll;
        SimpleDraweeView img_url;
        TextView date;
        TextView pagenumber;
        ImageView operationmore;

        public RecycleHolder2(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            date = itemView.findViewById(R.id.date);
            ll = itemView.findViewById(R.id.ll);
            img_url = itemView.findViewById(R.id.img_url);
            pagenumber = itemView.findViewById(R.id.pagenumber);
            operationmore = itemView.findViewById(R.id.operationmore);
        }
    }
}


