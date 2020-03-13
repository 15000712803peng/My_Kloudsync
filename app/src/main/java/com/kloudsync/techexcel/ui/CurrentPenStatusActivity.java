package com.kloudsync.techexcel.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.bean.EverPen;
import com.kloudsync.techexcel.help.EverPenManger;
import com.kloudsync.techexcel.mvp.BaseActivity;
import com.kloudsync.techexcel.mvp.presenter.CurrentPenStatusPresenter;
import com.kloudsync.techexcel.mvp.view.CurrentPenStatusView;
import com.kloudsync.techexcel.personal.MyNoteActivity;

import butterknife.Bind;

public class CurrentPenStatusActivity extends BaseActivity<CurrentPenStatusPresenter> implements CurrentPenStatusView {

	private String TAG = CurrentPenStatusActivity.class.getSimpleName();
	public static final String SIMILARPENSOURCE = "similarpensource";
	public static final String PENTYPE = "pentype";
	@Bind(R.id.iv_titlebar_back)
	ImageView mIvTitleBarBack;
	@Bind(R.id.tv_titlebar_title)
	TextView mTvTitlebarTitle;
	@Bind(R.id.iv_titlebar_setting)
	ImageView mIvTitlebarSetting;
	@Bind(R.id.tv_current_connected_status)
	TextView mTvCurrentConnectedStatus;
	@Bind(R.id.iv_current_status_icon)
	ImageView mIvCurrentStatusIcon;
	@Bind(R.id.tv_current_pen_name)
	TextView mTvCurrentPenName;
	@Bind(R.id.tv_current_pen_source)
	TextView mTvCurrentPenSource;
	@Bind(R.id.pb_current_pen_info)
	ProgressBar mPbCurrentPenInfo;
	@Bind(R.id.tv_current_pen_info_number)
	TextView mTvCurrentPenInfoNumber;
	@Bind(R.id.pb_current_pen_info2)
	ProgressBar mPbCurrentPenInfo2;
	@Bind(R.id.tv_current_pen_info_number2)
	TextView mTvCurrentPenInfoNumber2;
	@Bind(R.id.pb_current_pen_info3)
	ProgressBar mPbCurrentPenInfo3;
	@Bind(R.id.tv_current_pen_info_number3)
	TextView mTvCurrentPenInfoNumber3;
	@Bind(R.id.tv_current_pen_tips)
	TextView mCurrentPenTips;
	@Bind(R.id.tv_current_status_go_my_note)
	TextView mTvCurrentStatusGoMyNote;
	@Bind(R.id.pb_current_status_loading)
	ProgressBar mPbCurrentStatusLoading;
	private String mSimlarPenSource;
	private String mPenType;
	private EverPenManger mEverPenManger;
	private EverPen mCurrentPen;
	private String mPenTypeAndSource;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		mCurrentPen = mEverPenManger.getCurrentPen();
		mTvCurrentPenName.setText(mCurrentPen.getPenName());
	}

	@Override
	protected void onResume() {
		super.onResume();
		getPenMemory();
	}

	@Override
	protected int getLayout() {
//		App.setCustomDensity(this, 1);
		return R.layout.activity_current_pen_status;
	}

	@Override
	protected void initPresenter() {
		mPresenter = new CurrentPenStatusPresenter();
	}

	@Override
	protected void initView() {
		mEverPenManger = EverPenManger.getInstance(this);
		mEverPenManger.addListener(mPresenter);
		mSimlarPenSource = getIntent().getStringExtra(SIMILARPENSOURCE);
		mPenType = getIntent().getStringExtra(PENTYPE);
		mTvTitlebarTitle.setText(R.string.current_pen_status);
		mIvTitlebarSetting.setVisibility(View.VISIBLE);
		mPenTypeAndSource = mPenType + mSimlarPenSource;
		mCurrentPen = mEverPenManger.getCurrentPen();
		mTvCurrentPenName.setText(mCurrentPen.getPenName());
		mTvCurrentPenSource.setText(mPenTypeAndSource);
		boolean connected = mCurrentPen.isConnected();
		if (connected) {
			setPenStatusTextColor(R.color.color3d78fb);
			setCurrentPenStatus(R.string.the_connected, R.drawable.current_pen_connected_icon, R.string.go_to_my_notes);
			mCurrentPenTips.setVisibility(View.GONE);
			getPenPower();
		} else {
			setPenStatusTextColor(R.color.red2);
			setCurrentPenStatus(R.string.not_connected, R.drawable.register_red_tips_icon, R.string.connected);
			mCurrentPenTips.setVisibility(View.VISIBLE);
		}
		App.mApplication.addActivity(this);
	}

	@Override
	protected void initListener() {
		mIvTitleBarBack.setOnClickListener(this);
		mIvTitlebarSetting.setOnClickListener(this);
		mTvCurrentStatusGoMyNote.setOnClickListener(this);
	}

	@Override
	protected void initData() {

	}


	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
			case R.id.iv_titlebar_back:
				EverPen currentPen = EverPenManger.getInstance(this).getCurrentPen();
				if (currentPen != null && currentPen.isConnected()) {
					App.mApplication.exitActivity();
				}
				finish();
				break;
			case R.id.iv_titlebar_setting:
				intent = new Intent(this, PenSeetingActivity.class);
				intent.putExtra(PenSeetingActivity.SIMILARPENSOURCE, mSimlarPenSource);
				intent.putExtra(PenSeetingActivity.PENTYPE, mPenType);
				startActivity(intent);
				break;
			case R.id.tv_current_status_go_my_note:
				if (!mCurrentPen.isConnected()) {
					mTvCurrentStatusGoMyNote.setEnabled(false);
					mTvCurrentStatusGoMyNote.setText(R.string.connecting);
					mTvCurrentStatusGoMyNote.setAlpha(0.6f);
//					if (!mCurrentPen.isClick()) {
					mCurrentPen.setClick(true);
					mEverPenManger.connect(mCurrentPen);
//					}
				} else {
					intent = new Intent(this, MyNoteActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
				break;
		}
	}

	@Override
	public void setCurrentPenStatus(int resStringId, int resImgId, int resBtnStringId) {
		mTvCurrentConnectedStatus.setText(resStringId);
		mIvCurrentStatusIcon.setImageResource(resImgId);
		mTvCurrentStatusGoMyNote.setText(resBtnStringId);
		mTvCurrentStatusGoMyNote.setEnabled(true);
		mTvCurrentStatusGoMyNote.setAlpha(1.0f);
	}

	@Override
	public void setPenStatusTextColor(int resColorId) {
		mTvCurrentConnectedStatus.setTextColor(resColorId);
	}

	@Override
	public void getPenPower() {
		mEverPenManger.getBleManager().getPenPowerStatus();
	}

	@Override
	public void getPenMemory() {
		mEverPenManger.getBleManager().getPenUsedMemory();
	}

	@Override
	public void setCurrentPenTips(boolean isShow) {
		if (isShow) {
			mCurrentPenTips.setVisibility(View.GONE);
		} else {
			mTvCurrentPenInfoNumber.setVisibility(View.GONE);
			mTvCurrentPenInfoNumber2.setVisibility(View.GONE);
			mCurrentPenTips.setVisibility(View.VISIBLE);
			mPbCurrentPenInfo.setProgress(0);
			mPbCurrentPenInfo2.setProgress(0);
			mTvCurrentPenInfoNumber.setText("- -");
			mTvCurrentPenInfoNumber2.setText("- -");
		}
	}

	@Override
	public void setCurrentPenBattery(int batteryLength, boolean bIsCharging) {
		mPbCurrentPenInfo.setProgress(batteryLength);
		mTvCurrentPenInfoNumber.setText(batteryLength + "%");
		mTvCurrentPenInfoNumber.setVisibility(View.VISIBLE);
		Log.e(TAG, "batteryLength=" + batteryLength);
	}

	@Override
	public void setCurrentPenStorage(int storageLength) {
		storageLength = 100 - storageLength;
		mPbCurrentPenInfo2.setProgress(storageLength);
		mTvCurrentPenInfoNumber2.setText(storageLength + "%");
		mTvCurrentPenInfoNumber2.setVisibility(View.VISIBLE);
		Log.e(TAG, "storageLength=" + storageLength);
	}

	@Override
	public void setPenName(boolean bIsSuccess) {
		if (bIsSuccess) {
			String name = mCurrentPen.getPenName();
			mTvCurrentPenName.setText(name);
		}
	}

	@Override
	public void toast(String msg) {
		super.toast(msg);
	}

	@Override
	public void showLoading() {
		super.showLoading();
	}

	@Override
	public void dismissLoading() {
		super.dismissLoading();
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		EverPen currentPen = EverPenManger.getInstance(this).getCurrentPen();
		if (keyCode == KeyEvent.KEYCODE_BACK && currentPen != null && currentPen.isConnected()) {
			App.mApplication.exitActivity();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		mSimlarPenSource = null;
		mPenType = null;
		mEverPenManger.removeListener(mPresenter);
		mEverPenManger = null;
		mCurrentPen = null;
		mPenTypeAndSource = null;
		App.mApplication.mList.clear();
		super.onDestroy();
	}
}
