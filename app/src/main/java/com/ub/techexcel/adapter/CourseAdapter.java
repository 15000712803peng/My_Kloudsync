package com.ub.techexcel.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.Course;
import com.kloudsync.techexcel.bean.CourseItemData;
import com.kloudsync.techexcel.bean.LessionInCourse;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;
import com.kloudsync.techexcel.tool.TextTool;
import com.kloudsync.techexcel.ui.MeetingViewActivity;
import com.ub.techexcel.tools.CalListviewHeight;
import com.ub.techexcel.tools.CourseLessionMoreOperationPopup;
import com.ub.techexcel.tools.MeetingMoreOperationPopup;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.feezu.liuli.timeselector.Utils.TextUtil;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wang on 2018/3/23.
 */

public class CourseAdapter extends BaseAdapter {

    private Context context;
    private List<Course> courses;
    public ImageLoader imageLoader;
    private int isShow;
    private int type;

    private CourseLessionMoreOperationPopup.LessionOptionsListener lessionOptionsListener;

    public void setLessionOptionsListener(CourseLessionMoreOperationPopup.LessionOptionsListener lessionOptionsListener) {
        this.lessionOptionsListener = lessionOptionsListener;
    }

    public void setType(int type) {
        this.type = type;
    }

    boolean fromSearch;
    String keyword;

    public CourseAdapter(Context context, List<Course> courses, boolean isPublic, int isShow) {
        this.context = context;
        this.courses = courses;
        this.isShow = isShow;
        imageLoader = new ImageLoader(context.getApplicationContext());
    }

    public OnModifyServiceListener onModifyServiceListener;

    public void setFromSearch(boolean fromSearch, String keyword) {

        this.fromSearch = fromSearch;
        this.keyword = keyword;

    }

    public interface OnModifyServiceListener {
        void select(Course course);
    }

    public void setOnModifyServiceListener(OnModifyServiceListener onModifyServiceListener) {
        this.onModifyServiceListener = onModifyServiceListener;
    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return courses.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return courses.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.course_item, null);
            holder.day = (TextView) convertView.findViewById(R.id.day);
            holder.month = (TextView) convertView.findViewById(R.id.month);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.host = (TextView) convertView.findViewById(R.id.host);
            holder.attendee = (TextView) convertView.findViewById(R.id.attendee);
            holder.moreoperation = (ImageView) convertView.findViewById(R.id.moreoperation);
            holder.lessionList = (ListView) convertView.findViewById(R.id.list_lession);
            holder.lessionLayout = (LinearLayout) convertView.findViewById(R.id.course_lessions);
            holder.arrowImage = (ImageView) convertView.findViewById(R.id.image_arrow);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Course course = courses.get(position);
        final ViewHolder finalHolder = holder;
        holder.moreoperation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onModifyServiceListener.select(course);
            }
        });

        String start = "0:00", end = "0:00";
        String month = "", day = "";
        long number = 0;

        final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        final SimpleDateFormat formatter2 = new SimpleDateFormat("MM");
        final SimpleDateFormat formatter3 = new SimpleDateFormat("dd");

        if (!TextUtil.isEmpty(course.getStartDate())) {
            Date curDate = new Date(Long.parseLong(course.getStartDate()));
            start = formatter.format(curDate);
            month = formatter2.format(curDate);
            day = formatter3.format(curDate);
        }
        if (!TextUtil.isEmpty(course.getEndDate())) {
            Date curDate = new Date(Long.parseLong(course.getEndDate()));
            end = formatter.format(curDate);
        }
        number = Long.parseLong(course.getEndDate()) - Long.parseLong(course.getStartDate());
        long hour = number / 3600000;               //以小时为单位取整
        long min = number % 86400000 % 3600000 / 60000;       //以分钟为单位取整
        long seconds = number % 86400000 % 3600000 % 60000 / 1000;   //以秒为单位取整
        Log.e("laoyu", "day" + day);
        String dd = hour + ":" + min + ":" + seconds;

        holder.title.setText(course.getTitle());

        if (fromSearch) {
            if (!TextUtil.isEmpty(keyword)) {
                holder.title.setText(TextTool.setSearchColor(Color.parseColor("#72AEFF"), course.getTitle(), keyword));
            }
        } else {
            holder.title.setText(course.getTitle());
        }

        holder.day.setText(day);
        holder.month.setText(getMonth(month));
        holder.date.setText("Course time: " + start + " - " + end + "(" + dd + ")");
        holder.host.setText("Teachers: " + course.getTeacherNames());
        holder.attendee.setText("Students: " + course.getStudentNames());
        //--------

        final ViewHolder _holder = holder;
        holder.lessionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_holder.lessionList.getVisibility() == View.VISIBLE) {
                    _holder.lessionList.setVisibility(View.GONE);
                    _holder.arrowImage.setImageResource(R.drawable.arrow_down);
                } else {
                    _holder.lessionList.setVisibility(View.VISIBLE);
                    _holder.arrowImage.setImageResource(R.drawable.arrow_up);
                    getLessions(course, _holder.lessionList);
                }
            }
        });
        return convertView;

    }


    public String getMonth(String monthnumber) {
        if (monthnumber.equals("01")) {
            return context.getResources().getString(R.string.mtJan);
        } else if (monthnumber.equals("02")) {
            return context.getResources().getString(R.string.mtFeb);
        } else if (monthnumber.equals("03")) {
            return context.getResources().getString(R.string.mtMar);
        } else if (monthnumber.equals("04")) {
            return context.getResources().getString(R.string.mtApr);
        } else if (monthnumber.equals("05")) {
            return context.getResources().getString(R.string.mtMay);
        } else if (monthnumber.equals("06")) {
            return context.getResources().getString(R.string.mtJun);
        } else if (monthnumber.equals("07")) {
            return context.getResources().getString(R.string.mtJul);
        } else if (monthnumber.equals("08")) {
            return context.getResources().getString(R.string.mtAug);
        } else if (monthnumber.equals("09")) {
            return context.getResources().getString(R.string.mtSept);
        } else if (monthnumber.equals("10")) {
            return context.getResources().getString(R.string.mtOct);
        } else if (monthnumber.equals("11")) {
            return context.getResources().getString(R.string.mtNov);
        } else if (monthnumber.equals("12")) {
            return context.getResources().getString(R.string.mtDec);
        }
        return "";
    }


    class ViewHolder {

        TextView day, month, title, date, host, attendee;
        ImageView moreoperation;
        ListView lessionList;
        LinearLayout lessionLayout;
        ImageView arrowImage;
    }

    class LessionsAdapter extends BaseAdapter {

        List<LessionInCourse> lessions = new ArrayList<>();

        public LessionsAdapter(List<LessionInCourse> lessions) {
            this.lessions = lessions;

        }

        @Override
        public int getCount() {
            return lessions.size();
        }

        @Override
        public Object getItem(int position) {
            return lessions.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            final ViewHolder viewHolder;
            if (view == null) {
                viewHolder = new ViewHolder();
                view = LayoutInflater.from(context).inflate(R.layout.course_lession_item, null);
                viewHolder.title = (TextView) view.findViewById(R.id.title);
                viewHolder.teachers = (TextView) view.findViewById(R.id.txt_teachers);
                viewHolder.duration = (TextView) view.findViewById(R.id.txt_duration);
                viewHolder.operation = (LinearLayout) view.findViewById(R.id.operation);
                viewHolder.morepopup = (ImageView) view.findViewById(R.id.morepopup);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            final LessionInCourse lession = lessions.get(position);
            viewHolder.morepopup.setVisibility(View.VISIBLE);
            viewHolder.morepopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLessionMoreDialog(lession);
                }
            });
            viewHolder.title.setText(lession.getTitle());
            viewHolder.teachers.setText(context.getString(R.string.Teacher) + ":" + lession.getTeacherNames());
            return view;
        }

        class ViewHolder {
            TextView title;
            LinearLayout operation;
            TextView teachers;
            TextView duration;
            ImageView morepopup;
        }
    }


    LessionsAdapter lessionsAdapter;

    public void getLessions(final Course course, final ListView listView) {
//        String attachmentid = document.getAttachmentID() + "";
        if (course.getMeetingID() <= 0) {
            return;
        }

        Observable.just("Request_Lessions").observeOn(Schedulers.io()).map(new Function<String, JSONObject>() {
            @Override
            public JSONObject apply(String s) throws Exception {
                return ServiceInterfaceTools.getinstance().syncGetRecurringMeetingItemList(course.getMeetingID() + "", type);
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject jsonObject) throws Exception {
                if (jsonObject.has("RetCode")) {
                    if (jsonObject.getInt("RetCode") == 0) {
                        CourseItemData itemData = new Gson().fromJson(jsonObject.toString(), CourseItemData.class);
                        if (itemData.getRetData() != null && itemData.getRetData().size() > 0) {
                            lessionsAdapter = new LessionsAdapter(itemData.getRetData());
                            listView.setAdapter(lessionsAdapter);
                            CalListviewHeight.setListViewHeightBasedOnChildren(listView);
                        }
                    }
                }
            }
        }).subscribe();
//        String url = AppConfig.URL_PUBLIC + "Soundtrack/List?attachmentID=" + attachmentid;
//        ServiceInterfaceTools.getinstance().getSoundList(url, ServiceInterfaceTools.GETSOUNDLIST,
//                new ServiceInterfaceListener() {
//                    @Override
//                    public void getServiceReturnData(Object object) {
//                        List<SoundtrackBean> ll = (List<SoundtrackBean>) object;
//                        myBaseAdapter = new HomeDocumentAdapter.MyBaseAdapter(ll, document);
//                        listView.setAdapter(myBaseAdapter);
//                        CalListviewHeight.setListViewHeightBasedOnChildren(listView);
//                    }
//                }, false, true);
    }

    private void showLessionMoreDialog(LessionInCourse lessionInCourse) {
        CourseLessionMoreOperationPopup lessionMoreOperationPopup = new CourseLessionMoreOperationPopup();
        lessionMoreOperationPopup.getPopwindow(context);
        lessionMoreOperationPopup.setLessionOptionsListener(lessionOptionsListener);
        lessionMoreOperationPopup.show(lessionInCourse);
    }

}

