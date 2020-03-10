package com.kloudsync.techexcel.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.bean.EverPen;
import com.kloudsync.techexcel.dialog.CreateFolderDialog;
import com.kloudsync.techexcel.help.EverPenManger;
import com.kloudsync.techexcel.mvp.BaseActivity;
import com.kloudsync.techexcel.mvp.presenter.PenSeetingPresenter;
import com.kloudsync.techexcel.mvp.view.PenSeetingView;

import butterknife.Bind;

public class PenSeetingActivity extends BaseActivity<PenSeetingPresenter> implements PenSeetingView {
	public static final String SIMILARPENSOURCE = "similarpensource";
	public static final String PENTYPE = "pentype";
	@Bind(R.id.iv_titlebar_back)
	ImageView mIvTitlebarBack;
	@Bind(R.id.tv_titlebar_title)
	TextView mTvTitlebarTitle;
	@Bind(R.id.tv_seeting_pen_name)
	TextView mTvPenName;
	@Bind(R.id.iv_setting_pen_edit_name)
	ImageView mEditPenName;
	@Bind(R.id.tv_pen_seeting_firmware_upgrade)
	TextView mTvPenFirmwareUpgrade;
	@Bind(R.id.tv_pen_seeting_use_guide)
	TextView mTvPenUseGuide;
	@Bind(R.id.rly_pen_seeting_switch_source)
	RelativeLayout mRlySwitchPenSource;
	@Bind(R.id.tv_seeting_pen_source)
	TextView mTvPenSource;
	@Bind(R.id.tv_pen_seeting_switch)
	TextView mTvPenSwitch;
	@Bind(R.id.tv_pen_seeting_delete)
	TextView mTvPenDelete;
	private String mSimlaPenSource;
	private String mPenType;
	private EverPenManger mEverPenManger;
	private EverPen mCurrentPen;
	private String mOldPenName;
	private String mNewPenName;
	private String mName;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected int getLayout() {
		return R.layout.activity_pen_seeting;
	}

	@Override
	protected void initPresenter() {
		mPresenter = new PenSeetingPresenter();
	}

	@Override
	protected void initView() {
		mTvTitlebarTitle.setText(R.string.seeting);
		mEverPenManger = EverPenManger.getInstance(this);
		mEverPenManger.addListener(mPresenter);
		mSimlaPenSource = getIntent().getStringExtra(SIMILARPENSOURCE);
		mPenType = getIntent().getStringExtra(PENTYPE);
		mTvPenSource.setText(mPenType + mSimlaPenSource);
		mCurrentPen = mEverPenManger.getCurrentPen();
		mTvPenName.setText(mCurrentPen.getPenName());
		App.mApplication.addActivity(this);
	}

	@Override
	protected void initListener() {
		mIvTitlebarBack.setOnClickListener(this);
		mEditPenName.setOnClickListener(this);
		mTvPenFirmwareUpgrade.setOnClickListener(this);
		mTvPenUseGuide.setOnClickListener(this);
		mRlySwitchPenSource.setOnClickListener(this);
		mTvPenSwitch.setOnClickListener(this);
		mTvPenDelete.setOnClickListener(this);
	}

	@Override
	protected void initData() {

	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
			case R.id.iv_titlebar_back:
				finish();
				break;
			case R.id.iv_setting_pen_edit_name:
				mOldPenName = mTvPenName.getText().toString();
				mName = mCurrentPen.getName();
				CreateFolderDialog.instance(this).showDialog(R.string.edit_pen_name, R.string.please_enter_a_pen_name, 20);
				CreateFolderDialog.instance(this).setCreateFolderCallback(new CreateFolderDialog.CreateFolderCallback() {
					@Override
					public void createFolder(String folderName) {
						mNewPenName = mCurrentPen.getMacAddress() + folderName;
						mCurrentPen.setName(folderName);
						mCurrentPen.setPenName(mNewPenName);
						mTvPenName.setText(mNewPenName);
						mEverPenManger.getBleManager().setPenName(folderName);
					}
				});
				break;
			case R.id.tv_pen_seeting_firmware_upgrade:
				break;
			case R.id.rly_pen_seeting_switch_source:
				intent = new Intent(this, DigitalPensActivity.class);
				startActivity(intent);
				break;
			case R.id.tv_pen_seeting_switch:
				intent = new Intent(this, SwitchPenActivity.class);
				intent.putExtra(PenSeetingActivity.SIMILARPENSOURCE, mSimlaPenSource);
				intent.putExtra(PenSeetingActivity.PENTYPE, mPenType);
				startActivity(intent);
				break;
			case R.id.tv_pen_seeting_delete:
				mEverPenManger.disconnect(mCurrentPen);
				break;
		}
	}

	@Override
	public void onDisconnected() {
		mEverPenManger.deleteAutoConnectPen();
		finish();
	}

	@Override
	public void setPenName(boolean bIsSuccess) {
		if (!bIsSuccess) {
			mTvPenName.setText(mOldPenName);
			showToast(R.string.setup_failed);
			mCurrentPen.setName(mName);
			mCurrentPen.setPenName(mOldPenName);
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
	protected void onDestroy() {
		mSimlaPenSource = null;
		mPenType = null;
		mEverPenManger.removeListener(mPresenter);
		mEverPenManger = null;
		CreateFolderDialog.instance(this).destory();
		mCurrentPen = null;
		mOldPenName = null;
		mNewPenName = null;
		App.mApplication.removeActivity(this);
		super.onDestroy();
	}
}
