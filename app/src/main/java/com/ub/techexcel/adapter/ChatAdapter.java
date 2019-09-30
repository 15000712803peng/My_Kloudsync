package com.ub.techexcel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kloudsync.techexcel.R;

import java.util.ArrayList;
import java.util.List;

import io.rong.message.TextMessage;

/**
 * Created by wang on 2017/10/16.
 */

public class ChatAdapter extends BaseAdapter {

    private List<TextMessage> mList = new ArrayList<>();
    private Context mContext;


    @Override
    public int getCount() {
        return mList.size();
    }

    public ChatAdapter(Context context, List<TextMessage> list) {
        this.mContext = context;
        mList = list;
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item, null);
            viewHolder.tv = (TextView) view.findViewById(R.id.context);
            viewHolder.user_name = (TextView) view.findViewById(R.id.user_name);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        TextMessage textMessage = mList.get(i);//文本消息
        viewHolder.user_name.setText(textMessage.getExtra() + " :");
        viewHolder.tv.setText(textMessage.getContent());

//        if(m instanceof ImageMessage){
//            ImageMessage imageMessage=(ImageMessage)m;
//            Uri imageUrl=imageMessage.getThumUri();
//            String url=imageUrl.toString();
//
//        }

        return view;
    }


    class ViewHolder {
        TextView tv, user_name;
    }

}
