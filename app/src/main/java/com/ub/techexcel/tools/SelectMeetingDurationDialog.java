package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.EventJoinMeeting;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.service.ConnectService;
import com.ub.service.activity.SocketService;
import com.ub.service.activity.WatchCourseActivity2;
import com.ub.service.activity.WatchCourseActivity3;
import com.ub.techexcel.bean.UpcomingLesson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by wang on 2017/9/18.
 */

public class SelectMeetingDurationDialog implements View.OnClickListener {

    public Context mContext;
    public int width;
    public Dialog mPopupWindow;
    private View view;
    String[] durations;
    int[] times;
    List<DurationData> durationDatas = new ArrayList<>();
    ListView durationList;

    public interface OnDurationSelectedLinstener {
        void onDuratonSelected(DurationData duration);
    }

    private OnDurationSelectedLinstener onDurationSelectedLinstener;

    public void setOnDurationSelectedLinstener(OnDurationSelectedLinstener onDurationSelectedLinstener) {
        this.onDurationSelectedLinstener = onDurationSelectedLinstener;
    }

    public SelectMeetingDurationDialog(Context context) {
        this.mContext = context;
        width = mContext.getResources().getDisplayMetrics().widthPixels;
        getPopupWindowInstance();
        durations = context.getResources().getStringArray(R.array.time_duration);
        durationDatas.clear();
        times = context.getResources().getIntArray(R.array.time_millsecends);
        for(int i = 0 ; i < durations.length; ++i){
            DurationData durationData = new DurationData();
            durationData.duration = durations[i];
            durationData.time = times[i] * 1000 * 60;
            durationDatas.add(durationData);
        }
        mPopupWindow.getWindow().setWindowAnimations(R.style.PopupAnimation5);
    }

    public void getPopupWindowInstance() {
        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }

    @SuppressLint("WrongConstant")
    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.dialog_select_meeting_duration, null);
        mPopupWindow = new Dialog(mContext, R.style.bottom_dialog);
        durationList = view.findViewById(R.id.list_duration);
        durationList.setAdapter(new DurationAdapter());
        durationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (onDurationSelectedLinstener != null) {
                    onDurationSelectedLinstener.onDuratonSelected((DurationData) (parent.getAdapter().getItem(position)));
                    dismiss();
                }
            }
        });
        mPopupWindow.setContentView(view);
        mPopupWindow.getWindow().setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = mPopupWindow.getWindow().getAttributes();
        lp.width = mContext.getResources().getDisplayMetrics().widthPixels;
        mPopupWindow.getWindow().setAttributes(lp);


    }

    @SuppressLint("NewApi")
    public void show() {
        if (mPopupWindow != null) {
            mPopupWindow.show();
        }
    }


    public boolean isShowing() {
        return mPopupWindow.isShowing();
    }

    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    @Override
    public void onClick(View view) {

    }

    class DurationAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return durationDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return durationDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            DurationHolder holder = null;
            if (convertView == null) {
                holder = new DurationHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.txt_duration, null);
                holder.durationText = convertView.findViewById(R.id.txt_duration);
                convertView.setTag(holder);
            } else {
                holder = (DurationHolder) convertView.getTag();
            }
            holder.durationText.setText(durationDatas.get(position).duration);
            return convertView;
        }
    }

    class DurationHolder {
        TextView durationText;
    }

    public class DurationData {
        public String duration;
        public long time;
    }
}
