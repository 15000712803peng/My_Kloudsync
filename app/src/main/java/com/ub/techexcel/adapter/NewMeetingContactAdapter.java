package com.ub.techexcel.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;
import com.kloudsync.techexcel.info.Customer;
import com.ub.techexcel.tools.Tools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 2018/6/5.
 */

public class NewMeetingContactAdapter extends RecyclerView.Adapter<NewMeetingContactAdapter.ViewHolder2> {

    private LayoutInflater inflater;
    private List<Customer> mDatas = new ArrayList<>();
    private Context mContext;
    private Uri defaultImageUri;
    public ImageLoader imageLoader;

    public NewMeetingContactAdapter(Context context, List<Customer> mDatas) {
        this.mDatas = mDatas;
        this.mContext = context;
        inflater = LayoutInflater.from(context);
        imageLoader = new ImageLoader(context.getApplicationContext());
        defaultImageUri = Tools.getUriFromDrawableRes(context, R.drawable.hello);
    }


    @Override
    public ViewHolder2 onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.newmeeting3, parent, false);
        ViewHolder2 viewHolder = new ViewHolder2(view);

        viewHolder.value = (TextView) view.findViewById(R.id.name);
        viewHolder.simpledraweeview = (SimpleDraweeView) view.findViewById(R.id.simpledraweeview);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final ViewHolder2 holder, final int position) {
        holder.value.setText(mDatas.get(position).getName());
        String url = mDatas.get(position).getUrl();
        Uri imageUri;
        if (!TextUtils.isEmpty(url)) {
            imageUri = Uri.parse(url);
        } else {
            imageUri = defaultImageUri;
        }
        holder.simpledraweeview.setImageURI(imageUri);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class ViewHolder2 extends RecyclerView.ViewHolder {
        public ViewHolder2(View a) {
            super(a);
        }
        TextView value;
        SimpleDraweeView simpledraweeview;
    }
}
