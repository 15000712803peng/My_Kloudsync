package com.ub.techexcel.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ub.techexcel.bean.CourseLesson;
import com.ub.techexcel.bean.Lesson;
import com.kloudsync.techexcel.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wang on 2018/2/8.
 */

public class LessonAdapter extends BaseAdapter {
    private List<Lesson> list = new ArrayList<>();
    private Context mContext;

    public LessonAdapter(Context context, List<Lesson> list) {
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
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        final Lesson lesson = list.get(i);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.lesson_item, null);
            holder.lessonname = (TextView) convertView.findViewById(R.id.lessonname);
            holder.recycleview = (RecyclerView) convertView.findViewById(R.id.recycleview);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            holder.recycleview.setLayoutManager(linearLayoutManager);
            holder.selectlectures = (ImageView) convertView.findViewById(R.id.selectlectures);
            holder.selectTime = (TextView) convertView.findViewById(R.id.selectTime);
            holder.delete = (ImageView) convertView.findViewById(R.id.delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.lessonname.setText(lesson.getTitle());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lessonListenering.delete(i);
            }
        });
        holder.selectlectures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lessonListenering.selectLectures(lesson);
            }
        });
        holder.selectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lessonListenering.selectTime(lesson);
            }
        });

        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (lesson.getStartData() > 0) {
            Date curDate = new Date(lesson.getStartData());
            String str = formatter.format(curDate);
            holder.selectTime.setText(str);
        }

        final List<CourseLesson> courseLessons = lesson.getLectures();
        final LectureAdapter lectureAdapter = new LectureAdapter(mContext, courseLessons);
        lectureAdapter.setOnItemLectureListener(new LectureAdapter.OnItemLectureListener() {
            @Override
            public void onItem(CourseLesson lesson) {
                for (int j = 0; j < courseLessons.size(); j++) {
                    if (lesson.getLectureID() == courseLessons.get(j).getLectureID()) {
                        courseLessons.remove(j);
                        break;
                    }
                }
                lectureAdapter.notifyDataSetChanged();
            }

            @Override
            public void onenterItem() {
                lessonListenering.selectLectures(lesson);
            }
        });
        holder.recycleview.setAdapter(lectureAdapter);

        return convertView;
    }

    public interface LessonListenering {
        void delete(int position);

        void selectLectures(Lesson lesson);

        void selectTime(Lesson lesson);
    }

    public void setOnLessonListenering(LessonListenering lessonListenering) {
        this.lessonListenering = lessonListenering;
    }

    private LessonListenering lessonListenering;

    class ViewHolder {
        TextView lessonname;
        RecyclerView recycleview;
        TextView selectTime;
        ImageView selectlectures;
        ImageView delete;

    }
}


