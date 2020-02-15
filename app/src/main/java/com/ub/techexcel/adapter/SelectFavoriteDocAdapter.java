package com.ub.techexcel.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.MeetingDocument;
import java.text.SimpleDateFormat;
import java.util.List;


public class SelectFavoriteDocAdapter extends BaseAdapter{
    private List<MeetingDocument> documents;
    private Context context;
    public SelectFavoriteDocAdapter(Context context, List<MeetingDocument> documents) {
        this.context = context;
        this.documents = documents;
    }

    private void setDocumentIcon(String name, ImageView documentIcon) {
        Log.e("check_name", "name:" + name);
        if (name.endsWith(".jpg") || name.endsWith(".JPG")) {
            documentIcon.setImageResource(R.drawable.icon_jpg);
        } else if (name.endsWith(".ppt") || name.endsWith(".PPT") || name.endsWith(".pptx") || name.endsWith(".PPTX")) {
            documentIcon.setImageResource(R.drawable.icon_ppt);
        } else if (name.endsWith(".pdf") || name.endsWith(".pdf")) {
            documentIcon.setImageResource(R.drawable.icon_pdf);
        } else if (name.endsWith(".doc") || name.endsWith(".DOC") || name.endsWith(".docx") || name.endsWith(".DOCX")) {
            documentIcon.setImageResource(R.drawable.icon_doc);
        } else {
            documentIcon.setImageResource(R.drawable.file);
        }
    }

    @Override
    public int getCount() {
        return documents.size();
    }

    @Override
    public Object getItem(int position) {
        return documents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        MeetingDocument document = (MeetingDocument) getItem(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.select_favorite_doc_item, null);
            viewHolder.docNameText = (TextView) view.findViewById(R.id.txt_name);
            viewHolder.timeText = (TextView) view.findViewById(R.id.txt_time);
            viewHolder.docIcon = view.findViewById(R.id.image_icon_document);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.docNameText.setText(document.getTitle());
        String createData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.parseLong(document.getCreatedDate()));
        viewHolder.timeText.setText(createData);
        setDocumentIcon(document.getTitle(), viewHolder.docIcon);
        return view;
    }

    class ViewHolder{
        public TextView docNameText;
        public TextView timeText;
        public ImageView docIcon;

    }


}


