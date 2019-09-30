package com.ub.techexcel.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ub.techexcel.bean.UpcomingLesson;
import com.kloudsync.techexcel.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by wang on 2018/1/11.
 */

public class UpcomeingAdapter extends BaseAdapter {
    private Context context;
    private List<UpcomingLesson> mDatas;
    private int itemLayoutId;

    public UpcomeingAdapter(Context context, List<UpcomingLesson> mDatas,
                            int itemLayoutId) {
        this.context = context;
        this.mDatas = mDatas;
        this.itemLayoutId = itemLayoutId;
    }


    public interface StartMeetingListenering {

        void viewOldCourse(int position);

    }

    private StartMeetingListenering startMeetingListenering;

    public void setStartMeetingListenering(StartMeetingListenering startMeetingListenering) {
        this.startMeetingListenering = startMeetingListenering;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView,
                        ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                    itemLayoutId, null);
            holder.view = (TextView) convertView
                    .findViewById(R.id.view);
            holder.date = (TextView) convertView
                    .findViewById(R.id.date);
            holder.surplustime = (TextView) convertView
                    .findViewById(R.id.surplustime);
            holder.title = (TextView) convertView
                    .findViewById(R.id.title);
            convertView.setTag(holder);
        } else {
            holder =( ViewHolder) convertView.getTag();
        }
        UpcomingLesson upcomingLesson = mDatas.get(position);
        holder.title.setText(upcomingLesson.getTitle());
        String date = upcomingLesson.getStartDate();

        int min = 0;
        if (!TextUtils.isEmpty(date)) {
            long time = Long.parseLong(date);
            holder.date.setText(convert2String(time, TIME_FORMAT));
            long currentTime = curTimeMillis();

            if (currentTime > time) {
                long tt = currentTime - time;
                min = (int) tt / 60000;
            }
        }

        holder.surplustime.setText("   Starting in " + min + " minutes");
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMeetingListenering.viewOldCourse(position);
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView date;
        TextView view;
        TextView title;
        TextView surplustime;
    }



    public static String TIME_FORMAT = "yyyy-MM-dd HH:mm";

    public static String convert2String(long time, String format) {
        if (time > 0l) {
            SimpleDateFormat sf = new SimpleDateFormat(format);
            Date date = new Date(time);
            return sf.format(date);
        }
        return "";
    }

    /**
     * 获取当前系统的日期
     *
     * @return
     */
    public static long curTimeMillis() {
        return System.currentTimeMillis();
    }
}

