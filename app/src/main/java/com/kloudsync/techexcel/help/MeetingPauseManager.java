package com.kloudsync.techexcel.help;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.MeetingPauseOrResumBean;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.tool.ToastUtils;
import com.ub.techexcel.tools.MeetingServiceTools;

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


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
	private RelativeLayout mRlyMeetingPauseEdit;
	private TextView mTipsEditOk;
	private TextView mTipsEditCancle;
	private InputMethodManager mImm;

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
		mImm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
		mMeetingPauseLayout = activity.findViewById(R.id.rly_meeting_pause_layout);
		mMeetingPauseBigLayout = activity.findViewById(R.id.rly_meeting_pause_big);
		mMeetingPauseTitle = activity.findViewById(R.id.tv_meeting_pause_title);
		mMeetingPauseMinimize = activity.findViewById(R.id.iv_meeting_pause_minimize);
		mMeetingPauseTips = activity.findViewById(R.id.tv_meeting_pause_tips_text);
		mRlyMeetingPauseEdit = activity.findViewById(R.id.rly_meeting_pause_edit);
		mTipsEditOk = activity.findViewById(R.id.tv_edit_pause_tips_ok);
		mTipsEditCancle = activity.findViewById(R.id.tv_edit_pause_tips_cancle);
		mMeetingPauseEditTips = activity.findViewById(R.id.et_meeting_pause_tips_text);
		mMeetingPauseTime = activity.findViewById(R.id.tv_meeting_pause_time);
		mMeetingResumeBtn = activity.findViewById(R.id.btn_meeting_resume);

		mMeetingPauseMinLayout = activity.findViewById(R.id.rly_meeting_pause_minimize);
		mMeetingPauseMinTitle = activity.findViewById(R.id.tv_meeting_pause_minimize_title);
		mMeetingPauseMinTime = activity.findViewById(R.id.tv_meeting_pause_min_time);
		mMeetingPausebig = activity.findViewById(R.id.iv_meeting_pause_big);
		mTipsEditOk.setOnClickListener(this);
		mTipsEditCancle.setOnClickListener(this);
		mMeetingPauseMinimize.setOnClickListener(this);
		mMeetingPausebig.setOnClickListener(this);
		mMeetingResumeBtn.setOnClickListener(this);
		mStringBuilder = new SpannableStringBuilder();
		Drawable drawable = activity.getResources().getDrawable(R.drawable.ic_launcher);
		int widthiAndHeight = activity.getResources().getDimensionPixelOffset(R.dimen.dp_20);
		drawable.setBounds(0, 0, widthiAndHeight, widthiAndHeight);
		mImageSpan = new ImageSpan(drawable);
		mClickableSpan = new ClickableSpan() {
			@Override
			public void onClick(@NonNull View widget) {
				String tips = mMeetingPauseTips.getText().toString().replaceAll("\t", "");
				mMeetingPauseEditTips.setText(tips);
				mMeetingPauseEditTips.setFocusable(true);
				mMeetingPauseEditTips.setFocusableInTouchMode(true);
				mMeetingPauseEditTips.requestFocus();
//				mMeetingPauseEditTips.requestFocusFromTouch();
				mImm.showSoftInput(mMeetingPauseEditTips, InputMethodManager.SHOW_FORCED);
				mMeetingPauseTips.setVisibility(View.GONE);
				mRlyMeetingPauseEdit.setVisibility(View.VISIBLE);
			}
		};
		if (mMeetingConfig.getSystemType() == AppConfig.COMPANY_MODEL) {//如果是会议模式
			setTipInfo(mActivity.getString(R.string.meeting_pause_tips));
		} else {//如果是课堂模式
			setTipInfo(mActivity.getString(R.string.class_pause_tips));
		}
	}


	/*public void setViewTipAndVisibility(String tipsText) {
		mMeetingPauseEditTips.setText("");
		mRlyMeetingPauseEdit.setVisibility(View.GONE);
		mMeetingPauseTips.setVisibility(View.VISIBLE);
		if (mMeetingConfig.getSystemType() == AppConfig.COMPANY_MODEL) {//如果是会议模式
			mMeetingPauseTitle.setText(R.string.the_meeting_is_suspended);
			mMeetingPauseMinTitle.setText(R.string.meeting_suspended);
			setTipInfo(tipsText);
			if (isHost()) {
				mMeetingResumeBtn.setText(R.string.resume_meeting);
				mMeetingResumeBtn.setVisibility(View.VISIBLE);
			} else {
				mMeetingResumeBtn.setVisibility(View.GONE);
			}
		} else {//如果是课堂模式
			mMeetingPauseTitle.setText(R.string.practice_time);
			mMeetingPauseMinTitle.setText(R.string.practice_in_class);
			setTipInfo(tipsText);
			if (isHost()) {//如果是老师
				mMeetingResumeBtn.setText(R.string.continue_class);
				mMeetingResumeBtn.setVisibility(View.VISIBLE);
			} else {
				mMeetingResumeBtn.setVisibility(View.GONE);
			}
		}
	}*/

	public void setTipInfo(String tips) {
		mStringBuilder.clear();
		mStringBuilder.clearSpans();
		if (isHost()) {
			tips += "\t\t\t";
			mStringBuilder.append(tips);
			mStringBuilder.setSpan(mImageSpan, tips.length() - 2, tips.length() - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			mStringBuilder.setSpan(mClickableSpan, tips.length() - 2, tips.length() - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		} else {
			mStringBuilder.append(tips);
		}
		mMeetingPauseTips.setText(mStringBuilder);
		mMeetingPauseTips.setMovementMethod(LinkMovementMethod.getInstance());
	}

	public String getPauseTipsText() {
		String tipsText = mMeetingPauseTips.getText().toString();
		return tipsText;
	}

	/**
	 * 是否是主持人或是否是老师
	 *
	 * @return
	 */
	private boolean isHost() {
		if (mMeetingConfig.getMeetingHostId().equals(AppConfig.UserID) /*||mMeetingConfig.getPresenterId().equals(AppConfig.UserID)*/) {
			return true;
		}
		return false;
	}

	/**
	 * 显示暂停布局
	 */
	public void showBigLayout() {
		mMeetingPauseEditTips.setText("");
		mRlyMeetingPauseEdit.setVisibility(View.GONE);
		mMeetingPauseTips.setVisibility(View.VISIBLE);
		if (mMeetingConfig.getSystemType() == AppConfig.COMPANY_MODEL) {//如果是会议模式
			mMeetingPauseTitle.setText(R.string.the_meeting_is_suspended);
			mMeetingPauseMinTitle.setText(R.string.meeting_suspended);
			if (isHost()) {
				mMeetingResumeBtn.setText(R.string.resume_meeting);
				mMeetingResumeBtn.setVisibility(View.VISIBLE);
			} else {
				mMeetingResumeBtn.setVisibility(View.GONE);
			}
		} else {//如果是课堂模式
			mMeetingPauseTitle.setText(R.string.practice_time);
			mMeetingPauseMinTitle.setText(R.string.practice_in_class);
			if (isHost()) {//如果是老师
				mMeetingResumeBtn.setText(R.string.continue_class);
				mMeetingResumeBtn.setVisibility(View.VISIBLE);
			} else {
				mMeetingResumeBtn.setVisibility(View.GONE);
			}
		}

		startMeetingPauseTime();
		mMeetingPauseLayout.setVisibility(View.VISIBLE);
		mMeetingPauseBigLayout.setVisibility(View.VISIBLE);
		mMeetingPauseMinLayout.setVisibility(View.GONE);
	}

	/**
	 * 暂停布局最小化
	 */
	private void showMinLayout() {
		mMeetingPauseBigLayout.setVisibility(View.GONE);
		mMeetingPauseMinLayout.setVisibility(View.VISIBLE);
	}

	/**
	 * 隐藏暂停布局
	 */
	public void hide() {
		if (mImm.isActive()) {
			mMeetingPauseEditTips.setFocusable(false);
			mMeetingPauseEditTips.setFocusableInTouchMode(false);
			mMeetingPauseEditTips.requestFocus();
//					mMeetingPauseEditTips.requestFocusFromTouch();
			mImm.hideSoftInputFromWindow(mMeetingPauseLayout.getWindowToken(), 0);
		}
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
				if (mImm.isActive()) {
					mMeetingPauseEditTips.setFocusable(false);
					mMeetingPauseEditTips.setFocusableInTouchMode(false);
					mMeetingPauseEditTips.requestFocus();
//					mMeetingPauseEditTips.requestFocusFromTouch();
					mImm.hideSoftInputFromWindow(mMeetingPauseLayout.getWindowToken(), 0);
				}
				requestMeetingResume();
				break;
			case R.id.iv_meeting_pause_big:
				showBigLayout();
				break;
			case R.id.tv_edit_pause_tips_ok:
				requestMeetingPauseMessage();
				break;
			case R.id.tv_edit_pause_tips_cancle:
				if (mImm.isActive()) {
					mMeetingPauseEditTips.setFocusable(false);
					mMeetingPauseEditTips.setFocusableInTouchMode(false);
					mMeetingPauseEditTips.requestFocus();
//					mMeetingPauseEditTips.requestFocusFromTouch();
					mImm.hideSoftInputFromWindow(mMeetingPauseLayout.getWindowToken(), 0);
				}
				mMeetingPauseEditTips.setText("");
				mMeetingPauseTips.setVisibility(View.VISIBLE);
				mRlyMeetingPauseEdit.setVisibility(View.GONE);
				break;
		}
	}

	/**
	 * 继续会议
	 */
	private void requestMeetingResume() {
		Observable.just("meeting_pause").observeOn(Schedulers.io()).map(new Function<String, MeetingPauseOrResumBean>() {
			@Override
			public MeetingPauseOrResumBean apply(String s) throws Exception {
				MeetingPauseOrResumBean meetingPauseOrResumBean = MeetingServiceTools.getInstance().requestMeetingResume();
				return meetingPauseOrResumBean;
			}
		}).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<MeetingPauseOrResumBean>() {
			@Override
			public void accept(MeetingPauseOrResumBean bean) throws Exception {
				if (bean.getMsg() != null && bean.getMsg().equals("success")) {
					bean.setPause(false);
					EventBus.getDefault().post(bean);
				} else {
					ToastUtils.show(mActivity, bean.getMsg());
					return;
				}
			}
		}).subscribe();
	}

	/**
	 * 修改提示文字
	 */
	private void requestMeetingPauseMessage() {
		final String tips = mMeetingPauseEditTips.getText().toString();
		if (TextUtils.isEmpty(tips)) {
			ToastUtils.show(mActivity, R.string.the_content_can_not_be_blank);
			return;
		}
		Observable.just("meeting_pause").observeOn(Schedulers.io()).map(new Function<String, MeetingPauseOrResumBean>() {
			@Override
			public MeetingPauseOrResumBean apply(String s) throws Exception {
				MeetingPauseOrResumBean meetingPauseOrResumBean = MeetingServiceTools.getInstance().requestMeetingPauseMessage(tips);
				return meetingPauseOrResumBean;
			}
		}).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<MeetingPauseOrResumBean>() {
			@Override
			public void accept(MeetingPauseOrResumBean bean) throws Exception {
				if (bean.getMsg() != null && bean.getMsg().equals("success")) {
					if (mImm.isActive()) {
						mMeetingPauseEditTips.setFocusable(false);
						mMeetingPauseEditTips.setFocusableInTouchMode(false);
						mMeetingPauseEditTips.requestFocus();
//						mMeetingPauseEditTips.requestFocusFromTouch();
						mImm.hideSoftInputFromWindow(mMeetingPauseLayout.getWindowToken(), 0);
					}
					setTipInfo(tips);
					mMeetingPauseEditTips.setText("");
					mMeetingPauseTips.setVisibility(View.VISIBLE);
					mRlyMeetingPauseEdit.setVisibility(View.GONE);
				} else {
					ToastUtils.show(mActivity, bean.getMsg());
				}
			}
		}).subscribe();
	}

	/**
	 * 开始计时
	 */
	public void startMeetingPauseTime() {
		if (mPauseTimer == null && mPauseTimerTask == null) {
			mPauseTimer = new Timer();
			mPauseTimerTask = new TimerTask() {
				@Override
				public void run() {
					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (mStopPauseTimerTask) return;
							updatePauseTime(mPauseTime++);
						}
					});
				}
			};
			mStopPauseTimerTask = false;
			mPauseTimer.schedule(mPauseTimerTask, 0, 1000);
		}
	}

	/**
	 * 设置暂停时间
	 * @param time
	 */
	public void setPauseTime(long time) {
		mPauseTime = time;
	}

	/**
	 * 更新暂停时间文本显示
	 * @param time
	 */
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

	/**
	 * 停止计时
	 */
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
