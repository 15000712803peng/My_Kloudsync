package com.kloudsync.techexcel.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.BluetoothPenAdapter;
import com.kloudsync.techexcel.bean.EverPen;
import com.kloudsync.techexcel.help.EverPenManger;
import com.kloudsync.techexcel.mvp.BaseActivity;
import com.kloudsync.techexcel.mvp.presenter.SwitchPenPresenter;
import com.kloudsync.techexcel.mvp.view.SwitchPenView;
import com.tqltech.tqlpencomm.BLEException;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class SwitchPenActivity extends BaseActivity<SwitchPenPresenter> implements SwitchPenView, BluetoothPenAdapter.OnItemClickListener {

	@Bind(R.id.iv_titlebar_back)
	ImageView mIvTitlebarBack;
	@Bind(R.id.tv_titlebar_title)
	TextView mTvTitlebarTitle;
	@Bind(R.id.rv_switch_pen)
	RecyclerView RvSwitchPen;
	@Bind(R.id.lly_switch_pen_add)
	LinearLayout mLlyAddPen;
	private EverPenManger mEverPenManger;
	private List<EverPen> mEverPenList = new ArrayList<>();
	private BluetoothPenAdapter mBluetoothPenAdapter;
	private int mPosition;
	private EverPen mNeedConnectPen;
	private EverPen mNeedDisconnectPen;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected int getLayout() {
		return R.layout.activity_switch_pen;
	}

	@Override
	protected void initPresenter() {
		mPresenter = new SwitchPenPresenter();
	}

	@Override
	protected void initView() {
		mTvTitlebarTitle.setText(R.string.switch_electronic_pen);

	}

	@Override
	protected void initListener() {
		mIvTitlebarBack.setOnClickListener(this);
		mLlyAddPen.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		mEverPenManger = EverPenManger.getInstance(this);
		mEverPenManger.addListener(mPresenter);
		mEverPenManger.startOrStopFindDevice(true);
		if (mEverPenManger.getCurrentPen() != null && mEverPenManger.getCurrentPen().isConnected()) {
			mEverPenList.add(mEverPenManger.getCurrentPen());
		}
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
		RvSwitchPen.setLayoutManager(linearLayoutManager);
		mBluetoothPenAdapter = new BluetoothPenAdapter(this, mEverPenList);
		mBluetoothPenAdapter.setOnItemClickListener(this);
		RvSwitchPen.setAdapter(mBluetoothPenAdapter);
	}

	@Override
	public void onItemClick(ProgressBar progressBar, int position, EverPen everPen) {
		for (EverPen pen : mEverPenList) {
			if (pen.isClick()) {
				showToast(R.string.connecting);
				return;
			}
		}
		mPosition = position;
		for (int i = 0; i < mEverPenList.size(); i++) {
			if (i == position && !mEverPenList.get(i).isConnected()) {
				if (i == position) {
					mEverPenList.get(i).setClick(true);
				} else {
					mEverPenList.get(i).setClick(false);
				}
			}
			if (i != position) {
				mEverPenList.get(i).setConnected(false);
			}
		}
		notifyDataSetChanged();
		mNeedDisconnectPen = mEverPenManger.getCurrentPen();
		mNeedConnectPen = everPen;
		if (!everPen.isConnected()) {
			if (mNeedDisconnectPen.isConnected()) {
				for (int i = 0; i < mEverPenList.size(); i++) {
					if (mEverPenList.get(i).getMacAddress().equals(mNeedDisconnectPen.getMacAddress())) {
						mNeedDisconnectPen = mEverPenList.get(i);
					}
				}
				mEverPenManger.disconnect(mNeedDisconnectPen);
			} else {
				connect();
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.iv_titlebar_back:
				finish();
				break;
			case R.id.lly_switch_pen_add:
				Intent intent = new Intent(this, EnterPairingActivity.class);
				startActivity(intent);
				break;
		}
	}

	@Override
	public synchronized void addScanedEverPen(EverPen everPen) {
		if (!mEverPenList.contains(everPen)) {
			mEverPenList.add(everPen);
			notifyDataSetChanged();
		}
	}

	@Override
	public void onScanFailed(BLEException e) {

	}

	@Override
	public void setConnected(boolean isConnected) {
		if (mEverPenList.size() != 0) {
			mEverPenList.get(mPosition).setConnected(isConnected);
		}
	}

	@Override
	public void setClick(boolean click) {
		if (mEverPenList.size() != 0) {
			mEverPenList.get(mPosition).setClick(click);
		}
	}

	@Override
	public void connect() {
		mEverPenManger.connect(mNeedConnectPen);
	}

	@Override
	public void startActivity() {
		Intent intent = new Intent(this, CurrentPenStatusActivity.class);
		startActivity(intent);
	}

	@Override
	public void removeNoConnected() {
		for (int i = 0; i < mEverPenList.size(); i++) {
			if (!mEverPenList.get(i).isConnected()) {
				mEverPenList.remove(i);
			}
		}
	}

	@Override
	public void notifyDataSetChanged() {
		mBluetoothPenAdapter.notifyDataSetChanged();
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
		mEverPenManger.removeListener(mPresenter);
		if (mEverPenManger.getCurrentPen().isConnected()) {
			mEverPenManger.startOrStopFindDevice(false);
		}
		mEverPenManger = null;
		mEverPenList.clear();
		mBluetoothPenAdapter = null;
		super.onDestroy();
	}


}
