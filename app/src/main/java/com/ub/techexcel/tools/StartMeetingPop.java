package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ub.techexcel.bean.UpcomingLesson;
import com.kloudsync.techexcel.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wang on 2017/9/18.
 */

public class StartMeetingPop {

    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private List<UpcomingLesson> list = new ArrayList<>();
    private UpcomeingAdapter mDocumentAdapter;
    private ListView listView;
    private View view;
    private RelativeLayout layout;
    private String classroomid;
    private int identity;
    private TextView prompt;
    private Button meetingbnt, lessonbnt;
    private TextView teachername;

    public void getPopwindow(Context context, List<UpcomingLesson> list, RelativeLayout layout, String classid, int identity) {
        this.mContext = context;
        this.list = list;
        this.layout = layout;
        width = mContext.getResources().getDisplayMetrics().widthPixels;
        this.classroomid = classid;
        this.identity = identity;
        getPopupWindowInstance();

    }

    public void getPopupWindowInstance() {
        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }

    public void updateData(List<UpcomingLesson> upcomingLessonList) {
        this.list = upcomingLessonList;
        mDocumentAdapter.notifyDataSetChanged();
    }

    public void notifyisJoin(int identity, boolean isMeetingStatus) {
        if (identity == 1) {
            if (isMeetingStatus) {
                prompt.setText("The meeting currently is in session");
                prompt.setTextColor(Color.BLACK);
                lessonbnt.setBackgroundColor(mContext.getResources().getColor(R.color.blue));
                lessonbnt.setClickable(true);
                meetingbnt.setBackgroundColor(mContext.getResources().getColor(R.color.blue));
                meetingbnt.setClickable(true);
            } else {
                prompt.setText("Please wait for teacher to start the meeting");
                prompt.setTextColor(Color.RED);
                lessonbnt.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
                lessonbnt.setClickable(false);
                meetingbnt.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
                meetingbnt.setClickable(false);
            }
        }
    }

    public void setTeacherName(int identity, String name) {
        if (identity == 2) {
            teachername.setText("Welcome to My Klassroom");
        } else if (identity == 1) {
            teachername.setText("Welcome to " + name + "'s Klassroom");
        }
    }


    public interface StartMeetingListenering {
        void startMeeting();

        void viewOldCourse(int position);

        void joinSession();
    }

    private StartMeetingListenering startMeetingListenering;

    public void setStartMeetingListenering(StartMeetingListenering startMeetingListenering) {
        this.startMeetingListenering = startMeetingListenering;
    }


    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.selectoldcoursepop, null);
        listView = (ListView) view.findViewById(R.id.listview);
        TextView tv = (TextView) view.findViewById(R.id.classroomid);
        tv.setText(classroomid);
        teachername = (TextView) view.findViewById(R.id.teachername);
        prompt = (TextView) view.findViewById(R.id.prompt);
        if (identity == 2) {
            prompt.setVisibility(View.GONE);
        } else {
            prompt.setVisibility(View.VISIBLE);
        }
        lessonbnt = (Button) view.findViewById(R.id.lesson);
        meetingbnt = (Button) view.findViewById(R.id.meeting);
        if (identity == 2) {
            lessonbnt.setText("Start lesson now");
            meetingbnt.setText("Start meeting now");
            prompt.setVisibility(View.GONE);
        } else {
            prompt.setVisibility(View.VISIBLE);
            prompt.setText("Please wait for teacher to start the meeting");
            prompt.setTextColor(Color.RED);

            lessonbnt.setText("Join as student");
            meetingbnt.setText("Join as auditor");
            lessonbnt.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
            lessonbnt.setClickable(false);
            meetingbnt.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
            meetingbnt.setClickable(false);
        }

        lessonbnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (identity == 2) {
                    startMeetingListenering.startMeeting();
                } else {
                    startMeetingListenering.joinSession();
                }
            }
        });
        meetingbnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (identity == 2) {
                    startMeetingListenering.startMeeting();
                } else {
                    startMeetingListenering.joinSession();
                }
            }
        });
        mDocumentAdapter = new UpcomeingAdapter(mContext, list,
                R.layout.selectcourse_item);
        listView.setAdapter(mDocumentAdapter);
        mPopupWindow = new PopupWindow(view, width * 2 / 3,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        mPopupWindow.getWidth();
        mPopupWindow.getHeight();
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                layout.setAlpha(1.0f);
            }
        });
        mPopupWindow.setFocusable(false);
        mPopupWindow.setOutsideTouchable(false);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }


    @SuppressLint("NewApi")
    public void StartPop(View v) {
        if (mPopupWindow != null) {
            layout.setAlpha(0.5f);
            mPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
        }
    }

    public void dismiss() {
        if (mPopupWindow != null) {
            layout.setAlpha(0.5f);
            mPopupWindow.dismiss();
        }
    }

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
                holder = (ViewHolder) convertView.getTag();
            }
            UpcomingLesson upcomingLesson = list.get(position);
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
