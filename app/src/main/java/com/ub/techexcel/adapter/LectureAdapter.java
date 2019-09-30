package com.ub.techexcel.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ub.techexcel.bean.CourseLesson;
import com.kloudsync.techexcel.R;

import java.util.List;

/**
 * Created by wang on 2018/2/8.
 */

public class LectureAdapter extends RecyclerView.Adapter<MyRecycleHolder> {
    private List<CourseLesson> list;
    private Context context;

    public LectureAdapter(Context context, List<CourseLesson> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public MyRecycleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.lecture_recycle_item, parent, false);
        MyRecycleHolder holder = new MyRecycleHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyRecycleHolder holder, int position) {
        final CourseLesson item = list.get(position);
        holder.name.setText(item.getTitle());
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemLectureListener.onItem(item);
            }
        });
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemLectureListener.onenterItem();
            }
        });
    }

    public interface OnItemLectureListener {
        void onItem(CourseLesson lesson);

        void onenterItem();
    }

    public void setOnItemLectureListener(OnItemLectureListener onItemLectureListener) {
        this.onItemLectureListener = onItemLectureListener;
    }

    private OnItemLectureListener onItemLectureListener;

    @Override
    public int getItemCount() {
        return list.size();
    }
}
