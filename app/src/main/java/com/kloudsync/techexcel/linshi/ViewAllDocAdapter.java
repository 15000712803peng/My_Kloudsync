package com.kloudsync.techexcel.linshi;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.ub.techexcel.bean.LineItem;

import java.util.ArrayList;
import java.util.List;

public class ViewAllDocAdapter extends RecyclerView.Adapter<ViewAllDocAdapter.Holder> {

    private List<LineItem> list = new ArrayList<>();
    private Context mContext;

    public ViewAllDocAdapter(Context context, List<LineItem> list) {
        this.mContext = context;
        this.list = list;
    }

    public List<LineItem> getList() {
        return list;
    }

    public void setList(List<LineItem> list) {
        this.list = new ArrayList<>(list);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.viewalldocumentitem, parent, false);
        ViewAllDocAdapter.Holder holder = new ViewAllDocAdapter.Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

        LineItem lineItem = list.get(position);
        holder.tv_name.setText(lineItem.getFileName());
        holder.tv_synccount.setText("Sync " + lineItem.getSyncRoomCount());

        String url = lineItem.getUrl();
        if (!TextUtils.isEmpty(url)) {
            url = url.substring(0, url.lastIndexOf("<")) + "1" + url.substring(url.lastIndexOf("."), url.length());
            Uri imageUri = null;
            if (!TextUtils.isEmpty(url)) {
                imageUri = Uri.parse(url);
            }
            holder.img_url.setImageURI(imageUri);
        }

    }




    @Override
    public int getItemCount() {
        return list.size();
    }


    class Holder extends RecyclerView.ViewHolder {

        RelativeLayout rl_doc;
        TextView tv_name;
        SimpleDraweeView img_url,img_head;
        TextView tv_synccount;

        public Holder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            rl_doc = (RelativeLayout)  itemView.findViewById(R.id.rl_doc);
            tv_synccount = (TextView) itemView. findViewById(R.id.tv_synccount);
            img_head= (SimpleDraweeView) itemView.findViewById(R.id.img_head);
            img_url= (SimpleDraweeView) itemView.findViewById(R.id.img_url);
        }

    }


}
