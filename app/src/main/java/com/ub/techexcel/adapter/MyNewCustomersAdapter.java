package com.ub.techexcel.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;
import com.kloudsync.techexcel.info.Customer;

public class MyNewCustomersAdapter extends BaseAdapter {

    private List<Customer> list = new ArrayList<Customer>();
    private Context mContext;
    private ImageLoader imageLoader;

    public MyNewCustomersAdapter(Context mContext, List<Customer> list) {
        this.mContext = mContext;
        this.list = list;
        imageLoader = new ImageLoader(mContext.getApplicationContext());
    }

    public int getCount() {
        return list.size();
    }

    public void update(List<Customer> list) {
        this.list = list;
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup arg2) {
        final ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(
                    R.layout.newfriends_item, null);
            viewHolder.name = (TextView) view.findViewById(R.id.name);
            viewHolder.tvLetter = (TextView) view
                    .findViewById(R.id.item1_catalog);
            viewHolder.tel = (TextView) view.findViewById(R.id.tel);
            viewHolder.addfriendbnt = (TextView) view
                    .findViewById(R.id.addfriendbnt);
            viewHolder.imageView = (SimpleDraweeView) view
                    .findViewById(R.id.image);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        final Customer entity = list.get(position);
        viewHolder.name.setText(entity.getName());
//		viewHolder.tel.setText(entity.getPhone());
        String url = entity.getUrl();
        Uri imageUri = Uri.parse(url);
        viewHolder.imageView.setImageURI(imageUri);
        viewHolder.addfriendbnt.setVisibility(View.GONE);
        return view;
    }

    final static class ViewHolder {
        TextView tvLetter; // 首字母
        TextView name;
        TextView tel;
        TextView addfriendbnt;
        SimpleDraweeView imageView;
    }

    private OnHealthChangedListener onTouchingLetterChangedListener;

    public void setOnHealthChangedListener(
            OnHealthChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    public interface OnHealthChangedListener {
        public void onTouchingLetterChanged(int position);
    }

}
