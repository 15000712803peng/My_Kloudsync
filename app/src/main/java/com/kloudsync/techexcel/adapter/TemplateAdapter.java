package com.kloudsync.techexcel.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ub.techexcel.bean.TempletedCourse;
import com.kloudsync.techexcel.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pingfan on 2017/12/11.
 */

public class TemplateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<TempletedCourse> mlist = new ArrayList<>();

    public TemplateAdapter(List<TempletedCourse> mlist) {
        this.mlist = mlist;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_template, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        TempletedCourse templetedCourse = mlist.get(position);
        holder.title.setText(templetedCourse.getTitle());
        holder.teacherName.setText("By " + templetedCourse.getTeacherName());
        holder.authorLessonCount.setText(templetedCourse.getAuthorLessonCount() + " Lectures");
        holder.authorLessonCost.setText("Suggested price:$" + templetedCourse.getAuthorLessonCost() + "");
        holder.authorCost.setText("$" + templetedCourse.getAuthorCost() + "");
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView teacherName;
        TextView authorLessonCount;
        TextView authorLessonCost;
        TextView authorCost;

        ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.tv1);
            teacherName = (TextView) view.findViewById(R.id.tv2);
            authorLessonCount = (TextView) view.findViewById(R.id.tv3);
            authorLessonCost = (TextView) view.findViewById(R.id.tv4);
            authorCost = (TextView) view.findViewById(R.id.tv5);
        }
    }
}
