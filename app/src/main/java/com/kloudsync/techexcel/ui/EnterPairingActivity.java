package com.kloudsync.techexcel.ui;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.BluetoothPenAdapter;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.bean.EverPen;
import com.kloudsync.techexcel.help.EverPenManger;
import com.kloudsync.techexcel.mvp.BaseActivity;
import com.kloudsync.techexcel.mvp.presenter.EnterPairingPresenter;
import com.kloudsync.techexcel.mvp.view.EnterPairingView;
import com.tqltech.tqlpencomm.BLEException;
import com.tqltech.tqlpencomm.PenCommAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

import static com.kloudsync.techexcel.help.KloudPerssionManger.REQUEST_PERMISSION_LOCATION_WRITE_READ;

public class EnterPairingActivity extends BaseActivity<EnterPairingPresenter> implements EnterPairingView, BluetoothPenAdapter.OnItemClickListener {


	public static final String SIMILARPENSOURCE = "similarpensource";
	public static final String PENTYPE = "pentype";
	private static final int REQUEST_CURRENT_PEN_STATUS_CODE = 0;
	public static final int RESULT_CURRENT_PEN_STATUS_CODE = 1;
	public static final String PENINFO = "peninfo";
	@Bind(R.id.iv_titlebar_back)
	ImageView mIvBack;
	@Bind(R.id.tv_titlebar_title)
	TextView mTvTitlebarTitle;
	@Bind(R.id.lly_pairing_pen_tips)
	LinearLayout mLlyPairingPenTips;
	@Bind(R.id.tv_pairing_pen_name)
	TextView mTvPairingPenName;
	@Bind(R.id.tv_pairing_source)
	TextView mTvPairingSource;
	@Bind(R.id.rv_pairing_pen)
	RecyclerView mRvPairingPen;
	@Bind(R.id.btn_pairing_scanning)
	Button mBtnPairingScanning;
	private String mSimilaPenSource;
	private String mPenType;
	private List<EverPen> mEverPenList = new ArrayList<>();
	private BluetoothPenAdapter mBluetoothPenAdapter;
	private PenCommAgent mBleManager;
	private EverPenManger mEverPenManger;
	private int mPosition;
	private EverPen mNeedConnectPen;
	private EverPen mNeedDisconnectPen;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}


	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	private void requestPermission() {
		ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
				Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE,
				Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_LOCATION_WRITE_READ);
	}

	@Override
	protected int getLayout() {
		return R.layout.activity_enter_pairing;
	}

	@Override
	protected void initPresenter() {
		mPresenter = new EnterPairingPresenter();
	}

	@Override
	protected void initView() {
		requestPermission();
		mSimilaPenSource = getIntent().getStringExtra(SIMILARPENSOURCE);
		mPenType = getIntent().getStringExtra(PENTYPE);
		mTvTitlebarTitle.setText(R.string.enter_pairing);
		mTvPairingPenName.setText(mPenType);
		mTvPairingSource.setText(mSimilaPenSource);
		App.mApplication.addActivity(this);
	}

	@Override
	protected void initListener() {
		mIvBack.setOnClickListener(this);
		mBtnPairingScanning.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		mEverPenManger = EverPenManger.getInstance(this);
		registerReceiver(mBluetoothEnabledReceiver, makeFilters());
		mEverPenManger.addListener(mPresenter);
		if (mEverPenManger.getCurrentPen() != null && mEverPenManger.getCurrentPen().isConnected()) {
			mEverPenList.add(mEverPenManger.getCurrentPen());
			showPairingPen();
		} else {
			mEverPenManger.startOrStopFindDevice(true);
		}
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
		mRvPairingPen.setLayoutManager(linearLayoutManager);
		mBluetoothPenAdapter = new BluetoothPenAdapter(this, mEverPenList);
		mBluetoothPenAdapter.setOnItemClickListener(this);
		mRvPairingPen.setAdapter(mBluetoothPenAdapter);
	}

	private IntentFilter makeFilters() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		intentFilter.addAction("android.bluetooth.BluetoothAdapter.STATE_OFF");
		intentFilter.addAction("android.bluetooth.BluetoothAdapter.STATE_ON");
		return intentFilter;
	}

	@Override
	public void onItemClick(ProgressBar progressBar, int position, EverPen everPen) {
		setScanningBtnText(true, R.string.start_scanning, 1.0f);
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
		if (everPen.isConnected()) {
			Intent intent = new Intent(this, CurrentPenStatusActivity.class);
			intent.putExtra(CurrentPenStatusActivity.SIMILARPENSOURCE, mSimilaPenSource);
			intent.putExtra(CurrentPenStatusActivity.PENTYPE, mPenType);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
		} else {
			mNeedConnectPen = everPen;
			mNeedDisconnectPen = mEverPenManger.getCurrentPen();
			if (mNeedDisconnectPen != null && mNeedDisconnectPen.isConnected()) {
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
				EverPen currentPen = EverPenManger.getInstance(this).getCurrentPen();
				if (currentPen != null && currentPen.isConnected()) {
					App.mApplication.exitActivity();
				}
				finish();
				break;
			case R.id.btn_pairing_scanning:
				removeNoConnected();
				mEverPenManger.startOrStopFindDevice(true);
				setScanningBtnText(false, R.string.is_scanning, 0.6f);
				notifyDataSetChanged();
				break;
		}
	}

	public void showPairingPen() {
		if (mRvPairingPen.getVisibility() == View.INVISIBLE) {
			mRvPairingPen.setVisibility(View.VISIBLE);
			mLlyPairingPenTips.setVisibility(View.GONE);
		}
	}

	public void hidePairPen() {
		if (mLlyPairingPenTips.getVisibility() == View.GONE) {
			mLlyPairingPenTips.setVisibility(View.VISIBLE);
			mRvPairingPen.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public synchronized void addScanedEverPen(EverPen everPen) {
		if (!mEverPenList.contains(everPen)) {
			mEverPenList.add(everPen);
			notifyDataSetChanged();
			Log.e("EnterPairingActivity", "addScanedEverPen,device:" + everPen);
//			mBluetoothPenAdapter.notifyItemInserted(mBluetoothPenAdapter.getItemCount());
			showPairingPen();
		}
	}

	@Override
	public void onScanFailed(BLEException e) {
		if (mBtnPairingScanning.getAlpha() != 1.0f) {
			setScanningBtnText(true, R.string.start_scanning, 1.0f);
			if (mEverPenList.size() != 0) {
				showPairingPen();
			} else {
				hidePairPen();
			}
		}

	}


	@Override
	public void setConnected(boolean isConnected) {
		/*当前比状态界面点击连接时,未扫描到设备就停止扫描时(已连接),需要添加到列表中*/
		if (mEverPenManger.getCurrentPen() != null && isConnected) {
			addScanedEverPen(mEverPenManger.getCurrentPen());
		}
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
		if (mNeedConnectPen != null) {
			mEverPenManger.connect(mNeedConnectPen);
			mNeedConnectPen = null;
		}
	}

	@Override
	public void setScanningBtnText(boolean enabled, int resId, float alpha) {
		mBtnPairingScanning.setEnabled(enabled);
		mBtnPairingScanning.setText(resId);
		mBtnPairingScanning.setAlpha(alpha);
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
		if (mEverPenList.size() != 0) {
			showPairingPen();
		} else {
			hidePairPen();
		}
	}

	@Override
	public void setPenName(boolean bIsSuccess) {
		if (bIsSuccess && mEverPenList.size() != 0) {
			String name = mEverPenManger.getCurrentPen().getPenName();
			mEverPenList.get(mPosition).setName(name);
			mBluetoothPenAdapter.notifyItemChanged(mPosition);
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
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == REQUEST_PERMISSION_LOCATION_WRITE_READ) {
			if (grantResults.length > 0) {
				for (int i = 0; i < grantResults.length; i++) {
					if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
						if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
							showToast(R.string.please_grant_relevant_permissions);
							Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
							Uri uri = Uri.fromParts("package", getPackageName(), null);
							intent.setData(uri);
							startActivity(intent);
						} else {
							showToast(R.string.please_grant_relevant_permissions);
						}
						finish();
					}
				}

				mBleManager = mEverPenManger.getBleManager();
				if (mBleManager != null) {
					int code = mBleManager.init();
					if (code == 30001) {
						showToast(R.string.the_device_does_not_support_Bluetooth);
						finish();
					} else if (code == 30002) {
						showToast(R.string.device_does_not_support_ble);
						finish();
					}
				}

			}
		}
	}

	private final BroadcastReceiver mBluetoothEnabledReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			switch (action) {
				//上面的两个链接监听，其实也可以BluetoothAdapter实现，修改状态码即可
				case BluetoothAdapter.ACTION_STATE_CHANGED:
					int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
					switch (blueState) {
						case BluetoothAdapter.STATE_ON:
							mEverPenManger.startOrStopFindDevice(true);
							break;
					}
					break;

			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		EverPen currentPen = mEverPenManger.getCurrentPen();
		if (keyCode == KeyEvent.KEYCODE_BACK && currentPen != null && currentPen.isConnected()) {
			App.mApplication.exitActivity();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		mSimilaPenSource = null;
		mPenType = null;
		mEverPenList.clear();
		mBluetoothPenAdapter = null;
		mEverPenManger.removeListener(mPresenter);
		mBleManager = null;
		mEverPenManger = null;
		unregisterReceiver(mBluetoothEnabledReceiver);
		App.mApplication.removeActivity(this);
		super.onDestroy();
	}
}
