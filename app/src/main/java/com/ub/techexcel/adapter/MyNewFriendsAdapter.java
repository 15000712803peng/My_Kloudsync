package com.ub.techexcel.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;
import com.kloudsync.techexcel.info.AddFriend;

public class MyNewFriendsAdapter extends BaseAdapter {

    private List<AddFriend> list = new ArrayList<AddFriend>();
    private Context mContext;
    private ImageLoader imageLoader;

    public MyNewFriendsAdapter(Context mContext, List<AddFriend> list) {
        this.mContext = mContext;
        this.list = list;
        imageLoader = new ImageLoader(mContext.getApplicationContext());
    }

    public int getCount() {
        return list.size();
    }

    public void update(List<AddFriend> list) {
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
            viewHolder.imageView = (SimpleDraweeView) view.findViewById(R.id.image);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        final AddFriend entity = list.get(position);
        viewHolder.name.setText(entity.getName());
        viewHolder.tel.setText(entity.getPhone());

        String url = entity.getUrl();
        if(!TextUtils.isEmpty(url)){
            Uri imageUri = Uri.parse(url);
            viewHolder.imageView.setImageURI(imageUri);
        }

        if (entity.getType().equals("1")) {
            viewHolder.addfriendbnt.setText(mContext.getResources().getString(R.string.accept));
            viewHolder.addfriendbnt
                    .setBackgroundResource(R.drawable.newfriends_bg_two);
            viewHolder.addfriendbnt.setTextColor(mContext.getResources()
                    .getColor(R.color.c1));
        } else if (entity.getType().equals("2")) {
            viewHolder.addfriendbnt.setText(mContext.getResources().getString(R.string.added));
            viewHolder.addfriendbnt
                    .setBackgroundResource(R.drawable.newfriend_bg);
            viewHolder.addfriendbnt.setTextColor(mContext.getResources()
                    .getColor(R.color.c4));
        } else if (entity.getType().equals("3")) {
            viewHolder.addfriendbnt.setText(mContext.getResources().getString(R.string.waitVerification));
            viewHolder.addfriendbnt
                    .setBackgroundResource(R.drawable.newfriend_bg);
            viewHolder.addfriendbnt.setTextColor(mContext.getResources()
                    .getColor(R.color.c4));
        } else {
            viewHolder.addfriendbnt.setText(mContext.getResources().getString(R.string.add));
            viewHolder.addfriendbnt
                    .setBackgroundResource(R.drawable.newfriends_bg_two);
            viewHolder.addfriendbnt.setTextColor(mContext.getResources()
                    .getColor(R.color.c1));
        }

        viewHolder.addfriendbnt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                onTouchingLetterChangedListener
                        .onTouchingLetterChanged(position);
            }
        });
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
