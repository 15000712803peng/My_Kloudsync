package com.kloudsync.techexcel.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.kloudsync.techexcel.R;
import com.ub.kloudsync.activity.Document;

import java.util.ArrayList;
import java.util.List;

public class HelpDocumentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {


    private List<Document> documents = new ArrayList<>();

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, Document document);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public void onClick(final View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v, (Document) v.getTag());
        }
    }

    public HelpDocumentAdapter() {
    }

    public void setDocuments(List<Document> documents) {
        if (this.documents != null) {
            this.documents.clear();
        }
        this.documents.addAll(documents);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hc_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        final Document document = documents.get(position);
        holder.tv_title.setText(document.getTitle());
        String url = document.getAttachmentUrl();
        url = url.substring(0, url.lastIndexOf("<")) + "1" + url.substring(url.lastIndexOf("."), url.length());
        Uri imageUri = null;
        if (!TextUtils.isEmpty(url)) {
            imageUri = Uri.parse(url);
        }
        adjust(holder.img_url, imageUri);
        holder.itemView.setTag(document);
        holder.itemView.setOnClickListener(this);

    }

    private void adjust(SimpleDraweeView image, Uri uri) {
        int width = 50, height = 100;
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(width, height))
                .build();
        AbstractDraweeController controller = Fresco.newDraweeControllerBuilder()
                .setOldController(image.getController())
                .setImageRequest(request)
                .build();
        image.setController(controller);
    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView img_url;
        TextView tv_title;

        ViewHolder(View view) {
            super(view);
            img_url = (SimpleDraweeView) view.findViewById(R.id.img_url);
            tv_title = (TextView) view.findViewById(R.id.tv_title);
        }
    }
}
