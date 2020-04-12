package com.kloudsync.techexcel.help;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.tool.ToastUtils;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Faction:会议暂停管理类
 */
public class MeetingPauseManager implements View.OnClickListener {
	private static MeetingPauseManager mInstance;
	private Activity mActivity;
	private RelativeLayout mMeetingPauseLayout;
	private RelativeLayout mMeetingPauseBigLayout;
	private TextView mMeetingPauseTitle;
	private ImageView mMeetingPauseMinimize;
	private TextView mMeetingPauseTips;
	private EditText mMeetingPauseEditTips;
	private TextView mMeetingPauseTime;
	private Button mMeetingResumeBtn;
	private MeetingConfig mMeetingConfig;
	private SpannableStringBuilder mStringBuilder;
	private ImageSpan mImageSpan;
	private ClickableSpan mClickableSpan;
	private RelativeLayout mMeetingPauseMinLayout;
	private TextView mMeetingPauseMinTitle;
	private TextView mMeetingPauseMinTime;
	private ImageView mMeetingPausebig;
	private Timer mPauseTimer;
	private TimerTask mPauseTimerTask;
	private boolean mStopPauseTimerTask;
	private long mPauseTime = 0;

	public MeetingPauseManager(Activity activity, MeetingConfig config) {
		mMeetingConfig = config;
		mActivity = activity;
		initView(activity);
	}

	public static MeetingPauseManager getInstance(Activity activity, MeetingConfig config) {
		if (mInstance == null) {
			synchronized (MeetingPauseManager.class) {
				if (mInstance == null) {
					mInstance = new MeetingPauseManager(activity, config);
				}
			}
		}
		return mInstance;
	}

	private void initView(Activity activity) {
		mMeetingPauseLayout = activity.findViewById(R.id.rly_meeting_pause_layout);
		mMeetingPauseBigLayout = activity.findViewById(R.id.rly_meeting_pause_big);
		mMeetingPauseTitle = activity.findViewById(R.id.tv_meeting_pause_title);
		mMeetingPauseMinimize = activity.findViewById(R.id.iv_meeting_pause_minimize);
		mMeetingPauseTips = activity.findViewById(R.id.tv_meeting_pause_tips_text);
		mMeetingPauseTips.setMovementMethod(ScrollingMovementMethod.getInstance());
		mMeetingPauseEditTips = activity.findViewById(R.id.et_meeting_pause_tips_text);
		mMeetingPauseTime = activity.findViewById(R.id.tv_meeting_pause_time);
		mMeetingResumeBtn = activity.findViewById(R.id.btn_meeting_resume);

		mMeetingPauseMinLayout = activity.findViewById(R.id.rly_meeting_pause_minimize);
		mMeetingPauseMinTitle = activity.findViewById(R.id.tv_meeting_pause_minimize_title);
		mMeetingPauseMinTime = activity.findViewById(R.id.tv_meeting_pause_min_time);
		mMeetingPausebig = activity.findViewById(R.id.iv_meeting_pause_big);
		mMeetingPauseMinimize.setOnClickListener(this);
		mMeetingPausebig.setOnClickListener(this);
		mMeetingResumeBtn.setOnClickListener(this);
		mMeetingPauseEditTips.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE ||
						(event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
								event.getAction() == KeyEvent.ACTION_DOWN)) {
					String tips = v.getText().toString();
					if (TextUtils.isEmpty(tips)) {
						ToastUtils.show(mActivity, R.string.the_content_can_not_be_blank);
						return true;
					}
					setViewTipAndVisibility(tips);
					mMeetingPauseEditTips.setText("");
					mMeetingPauseTips.setVisibility(View.VISIBLE);
					mMeetingPauseEditTips.setVisibility(View.GONE);
				}
				return false;
			}
		});
		mStringBuilder = new SpannableStringBuilder();
		Drawable drawable = activity.getResources().getDrawable(R.drawable.ic_launcher);
		int widthiAndHeight = activity.getResources().getDimensionPixelOffset(R.dimen.dp_12);
		drawable.setBounds(0, 0, widthiAndHeight, widthiAndHeight);
		mImageSpan = new ImageSpan(drawable);
		mClickableSpan = new ClickableSpan() {
			@Override
			public void onClick(@NonNull View widget) {
				String tips = mMeetingPauseTips.getText().toString();
				mMeetingPauseEditTips.setText(tips);
				mMeetingPauseTips.setVisibility(View.GONE);
				mMeetingPauseEditTips.setVisibility(View.VISIBLE);
			}
		};
		if (true) {//如果是会议模式
			setViewTipAndVisibility(mActivity.getString(R.string.meeting_pause_tips));
		} else {//如果是课堂模式
			setViewTipAndVisibility(mActivity.getString(R.string.class_pause_tips));
		}
	}


	public void setViewTipAndVisibility(String tipsText) {
		if (true) {//如果是会议模式
			mMeetingPauseTitle.setText(R.string.the_meeting_is_suspended);
			mMeetingPauseMinTitle.setText(R.string.meeting_suspended);
			setTipInfo(tipsText);
			if (isPresenter()) {
				mMeetingResumeBtn.setText(R.string.resume_meeting);
				mMeetingResumeBtn.setVisibility(View.VISIBLE);
			} else {
				mMeetingResumeBtn.setVisibility(View.GONE);
			}
		} else {//如果是课堂模式
			mMeetingPauseTitle.setText(R.string.practice_time);
			mMeetingPauseMinTitle.setText(R.string.practice_in_class);
			setTipInfo(tipsText);
			if (isPresenter()) {//如果是老师
				mMeetingResumeBtn.setText(R.string.continue_class);
				mMeetingResumeBtn.setVisibility(View.VISIBLE);
			} else {
				mMeetingResumeBtn.setVisibility(View.GONE);
			}
		}
	}

	public void setTipInfo(String tips) {
		if (isPresenter()) {
			tips += "\t\t";
			mStringBuilder.append(tips);
			mStringBuilder.setSpan(mImageSpan, tips.length() - 2, tips.length() + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			mStringBuilder.setSpan(mClickableSpan, tips.length() - 2, tips.length() + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		} else {
			mStringBuilder.clear();
			mStringBuilder.clearSpans();
			mStringBuilder.append(tips);
		}
		mMeetingPauseTips.setText(mStringBuilder);
	}

	private boolean isPresenter() {
		if (mMeetingConfig.getPresenterId().equals(AppConfig.UserID)) {
			return true;
		}
		return false;
	}

	public void showBigLayout() {
		startMeetingPauseTime();
		mMeetingPauseLayout.setVisibility(View.VISIBLE);
		mMeetingPauseBigLayout.setVisibility(View.VISIBLE);
		mMeetingPauseMinLayout.setVisibility(View.GONE);
	}

	private void showMinLayout() {
		mMeetingPauseBigLayout.setVisibility(View.GONE);
		mMeetingPauseMinLayout.setVisibility(View.VISIBLE);
	}

	public void hide() {
		mMeetingPauseLayout.setVisibility(View.GONE);
		stopMeetingPauseTime();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.iv_meeting_pause_minimize:
				showMinLayout();
				break;
			case R.id.btn_meeting_resume:
				break;
			case R.id.iv_meeting_pause_big:
				showBigLayout();
				break;
		}
	}

	private void startMeetingPauseTime() {
		if (mPauseTimer == null && mPauseTimerTask == null) {
			mPauseTimer = new Timer();
			mPauseTimerTask = new TimerTask() {
				@Override
				public void run() {
					if (mStopPauseTimerTask) return;
					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							updatePauseTime(mPauseTime++);
						}
					});
				}
			};
			mStopPauseTimerTask = false;
			mPauseTimer.schedule(mPauseTimerTask, 0, 1000);
		}
	}

	public void setPauseTime(long time) {
		mPauseTime = time;
	}

	private void updatePauseTime(long time) {
		long h = time / 3600;
		long m = time % 3600 / 60;
		long s = time % 60;
		String hours;
		String minute;
		String second;
		if (!(h > 9)) {
			hours = "0" + h;
		} else {
			hours = String.valueOf(h);
		}

		if (!(m > 9)) {
			minute = "0" + m;
		} else {
			minute = String.valueOf(m);
		}
		if (!(s > 9)) {
			second = "0" + s;
		} else {
			second = String.valueOf(s);
		}
		String pauseTime = hours + ":" + minute + ":" + second;
		mMeetingPauseTime.setText(pauseTime);
		mMeetingPauseMinTime.setText(pauseTime);
	}

	private void stopMeetingPauseTime() {
		mStopPauseTimerTask = true;
		mPauseTime = 0;
		if (mPauseTimer != null && mPauseTimerTask != null) {
			mPauseTimer.cancel();
			mPauseTimerTask.cancel();
			mPauseTimer = null;
			mPauseTimerTask = null;
		}
	}

	public void destory() {
		hide();
		mMeetingPauseLayout = null;
		mMeetingPauseBigLayout = null;
		mMeetingPauseTitle = null;
		mMeetingPauseMinimize = null;
		mMeetingPauseTips = null;
		mMeetingPauseEditTips = null;
		mMeetingPauseTime = null;
		mMeetingResumeBtn = null;
		mMeetingConfig = null;
		mStringBuilder = null;
		mImageSpan = null;
		mClickableSpan = null;
		mMeetingPauseMinLayout = null;
		mMeetingPauseMinTitle = null;
		mMeetingPauseMinTime = null;
		mMeetingPausebig = null;
		mActivity = null;
		mInstance = null;
	}
}
