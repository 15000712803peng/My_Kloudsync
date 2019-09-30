package com.ub.techexcel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ub.techexcel.bean.CourseLesson;
import com.kloudsync.techexcel.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 2018/2/8.
 */

public class LectureListAdapter extends BaseAdapter {
    private List<CourseLesson> list = new ArrayList<>();
    private Context mContext;

    public LectureListAdapter(Context context, List<CourseLesson> list) {
        this.list = list;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        CourseLesson c = list.get(i);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.lecturelist_item, null);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.imageview = (ImageView) convertView.findViewById(R.id.imageview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.title.setText(c.getTitle() + "");
        if(c.isSelect()){
            holder.imageview.setImageResource(R.drawable.finish_a);
        }else{
            holder.imageview.setImageResource(R.drawable.finish_d);
        }
        return convertView;
    }

    class ViewHolder {
        TextView title;
        ImageView imageview;
    }
}


