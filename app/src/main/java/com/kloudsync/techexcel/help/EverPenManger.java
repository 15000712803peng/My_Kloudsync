package com.kloudsync.techexcel.help;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.EverPen;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.service.BluetoothLEService;
import com.kloudsync.techexcel.tool.SharedPreferencesUtils;
import com.tqltech.tqlpencomm.BLEException;
import com.tqltech.tqlpencomm.BLEScanner;
import com.tqltech.tqlpencomm.Dot;
import com.tqltech.tqlpencomm.ElementCode;
import com.tqltech.tqlpencomm.ErrorStatus;
import com.tqltech.tqlpencomm.PenCommAgent;
import com.tqltech.tqlpencomm.PenStatus;
import com.tqltech.tqlpencomm.listener.TQLPenSignal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static android.content.Context.BIND_AUTO_CREATE;


public class EverPenManger implements BluetoothLEService.OnDataReceiveListener, BLEScanner.OnBLEScanListener, TQLPenSignal {
	private static EverPenManger manger;
	private BluetoothLEService mService = null;
	PenCommAgent agent;
	Activity host;
	boolean serviceConnected;
	List<EverPen> everPens = new ArrayList<>();
	private final ConcurrentHashMap<MyTQLPenSignal, Integer> mTQLPenSignalMap = new ConcurrentHashMap<>();
	private EverPen mCurrentPen;
	private EverPen mAutoPenInfo;
	private static final int AUTOCONNECT = 100;
	private boolean mIsAutoConnected = false;

	private EverPenManger(Activity host) {
		this.host = host;
	}

	public static EverPenManger getInstance(Activity host) {
		if (manger == null) {
			synchronized (EverPenManger.class) {
				if (manger == null) {
					manger = new EverPenManger(host);
				}
			}
		}
		return manger;
	}

	public void addListener(MyTQLPenSignal listener) {
		this.mTQLPenSignalMap.put(listener, 0);
	}

	public void removeListener(MyTQLPenSignal listener) {
		this.mTQLPenSignalMap.remove(listener);
	}

	public void init() {
		Intent gattServiceIntent = new Intent(host, BluetoothLEService.class);
		host.bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
	}

	public void unBindService() {
		if (mCurrentPen != null) {
			disconnect(mCurrentPen);
		}
		host.unbindService(mServiceConnection);
		mService.stopSelf();
		mService = null;
		EverPenDataManger.getInstace(this, host).removeCallbacksAndMessages(null);
		manger = null;
	}

	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		public void onServiceConnected(final ComponentName className, IBinder rawBinder) {
			mService = ((BluetoothLEService.LocalBinder) rawBinder).getService();
			if (mService.initialize(getInstance(host))) {
				mService.setOnDataReceiveListener(getInstance(host));
				agent = mService.getBleManager();
				if (getBleManager() != null) {
					Iterator<MyTQLPenSignal> it = mTQLPenSignalMap.keySet().iterator();
					while (it.hasNext()) {
						MyTQLPenSignal myTQLPenSignal = it.next();
						myTQLPenSignal.getBleManager(getBleManager());
					}
				}
				serviceConnected = true;
				Log.e("EverPenManager", "onServiceConnected:" + agent);
				mAutoPenInfo = SharedPreferencesUtils.getString(AppConfig.EVERPENINFO, AppConfig.EVERPENINFO, EverPen.class);
				mCurrentPen = mAutoPenInfo;
				startOrStopFindDevice(true);

			}

		}

		public void onServiceDisconnected(ComponentName classname) {
			Log.e("EverPenManager", "onServiceDisconnected");
			if (mCurrentPen != null) {
				disconnect(mCurrentPen);
			}
			mService = null;
		}
	};


	@Override
	public void onDataReceive(Dot dot) {

	}

	@Override
	public void onOfflineDataReceive(Dot dot) {

	}

	@Override
	public void onFinishedOfflineDown(boolean success) {

	}

	@Override
	public void onOfflineDataNum(int num) {

	}

	/**
	 * TQLPenSignal start
	 */
	@Override
	public void onConnected() {
		agent.ReqOfflineDataTransfer(true);
		host.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				EverPenDataManger.getInstace(EverPenManger.this, host).sendHandlerMessage();
				if (mCurrentPen != null) {
					mCurrentPen.setConnected(true);
					mCurrentPen.setClick(false);
				}
				Iterator<MyTQLPenSignal> it = mTQLPenSignalMap.keySet().iterator();
				while (it.hasNext()) {
					MyTQLPenSignal myTQLPenSignal = it.next();
					myTQLPenSignal.onConnected();
				}
			}
		});
		mIsAutoConnected = false;
		startOrStopFindDevice(false);
		/*if (!everPens.contains(mCurrentPen)){
			everPens.add(mCurrentPen);
		}*/
		Log.e("EverPenManager", "setConnected");
	}

	@Override
	public void onDisconnected() {
		Log.e("EverPenManager", "onDisconnected");
		host.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				EverPenDataManger.getInstace(EverPenManger.this, host).removeHandlerMessage();
				if (mCurrentPen != null) {
					mCurrentPen.setConnected(false);
					mCurrentPen.setClick(false);
				}
				Iterator<MyTQLPenSignal> it = mTQLPenSignalMap.keySet().iterator();
				while (it.hasNext()) {
					MyTQLPenSignal myTQLPenSignal = it.next();
					myTQLPenSignal.onDisconnected();
				}
			}
		});
		mIsAutoConnected = false;
	}

	@Override
	public void onConnectFailed() {
		Log.e("EverPenManager", "notifyDataSetChanged");
		host.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				EverPenDataManger.getInstace(EverPenManger.this, host).removeHandlerMessage();
				if (mCurrentPen != null) {
					mCurrentPen.setConnected(false);
					mCurrentPen.setClick(false);
				}
				Iterator<MyTQLPenSignal> it = mTQLPenSignalMap.keySet().iterator();
				while (it.hasNext()) {
					MyTQLPenSignal myTQLPenSignal = it.next();
					myTQLPenSignal.onConnectFailed();
				}
			}
		});
		mIsAutoConnected = false;
		startOrStopFindDevice(true);
		/*if (mAutoPenInfo!=null) {
			connect(mAutoPenInfo);
		}*/
	}

	@Override
	public void onReceiveDot(final Dot dot) {
		Log.e("EverPenManager", "onReceiveDot:" + dot);
		host.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Iterator<MyTQLPenSignal> it = mTQLPenSignalMap.keySet().iterator();
				while (it.hasNext()) {
					MyTQLPenSignal myTQLPenSignal = it.next();
					myTQLPenSignal.onReceiveDot(dot);
				}
			}
		});
		EverPenDataManger.getInstace(this, host).cacheDotData(dot);

	}

	@Override
	public void onReceiveOfflineStrokes(final Dot dot) {
		Log.e("EverPenManager", "onReceiveOfflineStrokes:" + dot);
		host.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Iterator<MyTQLPenSignal> it = mTQLPenSignalMap.keySet().iterator();
				while (it.hasNext()) {
					MyTQLPenSignal myTQLPenSignal = it.next();
					myTQLPenSignal.onReceiveOfflineStrokes(dot);
				}
			}
		});
		EverPenDataManger.getInstace(this, host).cacheDotData(dot);
	}

	@Override
	public void onOfflineDataList(int i) {
		Log.e("EverPenManager", "onOfflineDataList:" + i);
	}

	@Override
	public void onStartOfflineDownload(boolean b) {
		Log.e("EverPenManager", "onStartOfflineDownload:" + b);

	}

	@Override
	public void onStopOfflineDownload(boolean b) {
		Log.e("EverPenManager", "onStopOfflineDownload:" + b);
	}

	@Override
	public void onPenPauseOfflineDataTransferResponse(boolean b) {
		Log.e("EverPenManager", "onPenPauseOfflineDataTransferResponse:" + b);
	}

	@Override
	public void onPenContinueOfflineDataTransferResponse(boolean b) {
		Log.e("EverPenManager", "onPenContinueOfflineDataTransferResponse:" + b);
	}

	@Override
	public void onFinishedOfflineDownload(boolean b) {
		Log.e("EverPenManager", "onFinishedOfflineDownload:" + b);
	}

	@Override
	public void onReceiveOfflineProgress(int i) {
		Log.e("EverPenManager", "onReceiveOfflineProgress:" + i);

	}

	@Override
	public void onPenDeleteOfflineDataResponse(boolean b) {
		Log.e("EverPenManager", "onPenDeleteOfflineDataResponse:" + b);
	}

	@Override
	public void onPenNameSetupResponse(final boolean b) {
		Log.e("EverPenManager", "onPenNameSetupResponse:" + b);
		host.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Iterator<MyTQLPenSignal> it = mTQLPenSignalMap.keySet().iterator();
				while (it.hasNext()) {
					MyTQLPenSignal myTQLPenSignal = it.next();
					myTQLPenSignal.onPenNameSetupResponse(b);
				}
			}
		});
	}

	@Override
	public void onReceivePenName(String s) {
		Log.e("EverPenManager", "onReceivePenName:" + s);

	}

	@Override
	public void onReceivePenMac(String s) {
		Log.e("EverPenManager", "onReceivePenMac:" + s);
	}

	@Override
	public void onReceivePenBtFirmware(String s) {
		Log.e("EverPenManager", "onReceivePenBtFirmware:" + s);

	}

	@Override
	public void onReceivePenBattery(final int i, final boolean b) {
		Log.e("EverPenManager", "onReceivePenBattery:" + i + ",b:" + b);
		host.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Iterator<MyTQLPenSignal> it = mTQLPenSignalMap.keySet().iterator();
				while (it.hasNext()) {
					MyTQLPenSignal myTQLPenSignal = it.next();
					myTQLPenSignal.onReceivePenBattery(i, b);
				}
			}
		});
	}

	@Override
	public void onPenTimetickSetupResponse(boolean b) {
		Log.e("EverPenManager", "onPenTimetickSetupResponse:" + b);
	}

	@Override
	public void onReceivePenTime(long l) {
		Log.e("EverPenManager", "onReceivePenTime:" + l);
	}

	@Override
	public void onPenAutoShutdownSetUpResponse(boolean b) {
		Log.e("EverPenManager", "onPenAutoShutdownSetUpResponse:" + b);
	}

	@Override
	public void onReceivePenAutoOffTime(int i) {
		Log.e("EverPenManager", "onReceivePenAutoOffTime:" + i);
	}

	@Override
	public void onPenFactoryResetSetUpResponse(boolean b) {
		Log.e("EverPenManager", "onPenFactoryResetSetUpResponse:" + b);
	}

	@Override
	public void onReceivePenMemory(final int i) {
		Log.e("EverPenManager", "onReceivePenMemory:" + i);
		host.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Iterator<MyTQLPenSignal> it = mTQLPenSignalMap.keySet().iterator();
				while (it.hasNext()) {
					MyTQLPenSignal myTQLPenSignal = it.next();
					myTQLPenSignal.onReceivePenMemory(i);
				}
			}
		});
	}

	@Override
	public void onPenAutoPowerOnSetUpResponse(boolean b) {
		Log.e("EverPenManager", "onPenAutoPowerOnSetUpResponse:" + b);
	}

	@Override
	public void onReceivePenAutoPowerOnModel(boolean b) {
		Log.e("EverPenManager", "onReceivePenAutoPowerOnModel:" + b);
	}

	@Override
	public void onPenBeepSetUpResponse(boolean b) {
		Log.e("EverPenManager", "onPenBeepSetUpResponse:" + b);
	}

	@Override
	public void onReceivePenBeepModel(boolean b) {
		Log.e("EverPenManager", "onReceivePenBeepModel:" + b);
	}

	@Override
	public void onPenSensitivitySetUpResponse(boolean b) {
		Log.e("EverPenManager", "onPenSensitivitySetUpResponse:" + b);
	}

	@Override
	public void onReceivePenSensitivity(int i) {
		Log.e("EverPenManager", "onReceivePenSensitivity:" + i);
	}

	@Override
	public void onPenLedConfigResponse(boolean b) {
		Log.e("EverPenManager", "onPenLedConfigResponse:" + b);
	}

	@Override
	public void onReceivePenLedConfig(int i) {
		Log.e("EverPenManager", "onReceivePenLedConfig:" + i);
	}

	@Override
	public void onPenDotTypeResponse(boolean b) {
		Log.e("EverPenManager", "onPenDotTypeResponse:" + b);
	}

	@Override
	public void onPenChangeLedColorResponse(boolean b) {
		Log.e("EverPenManager", "onPenChangeLedColorResponse:" + b);
	}

	@Override
	public void onReceivePresssureValue(int i, int i1) {
		Log.e("EverPenManager", "onReceivePresssureValue:" + i);
	}

	@Override
	public void onReceivePenMcuVersion(String s) {
		Log.e("EverPenManager", "onReceivePresssureValue:" + s);
	}

	@Override
	public void onReceivePenDotType(int i) {
		Log.e("EverPenManager", "onReceivePenDotType:" + i);
	}

	@Override
	public void onReceivePenAllStatus(PenStatus penStatus) {
		Log.e("EverPenManager", "onReceivePenAllStatus:" + penStatus);
	}

	@Override
	public void onReceivePenLED(int color) {
		Log.e("EverPenManager", "onReceivePenLED:" + color);
	}

	@Override
	public void onWriteCmdResult(final int code) {
		Log.e("EverPenManager", "onWriteCmdResult:" + code);
		host.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Iterator<MyTQLPenSignal> it = mTQLPenSignalMap.keySet().iterator();
				while (it.hasNext()) {
					MyTQLPenSignal myTQLPenSignal = it.next();
					switch (code) {
						case ErrorStatus.BLE_CONNECT_TIMEOUT://蓝牙连接超时
							if (mAutoPenInfo != null) {
								mIsAutoConnected = false;
								startOrStopFindDevice(true);
//								connect(mAutoPenInfo);
							}
							myTQLPenSignal.bleConnectTimeout();
							break;
						case ErrorStatus.SETUP_NAME_TIMEOUT://设置笔名超时
							myTQLPenSignal.setNameTimeout();
							break;
						case ErrorStatus.REQUEST_BATTARY_TIMEOUT://获取电量超时
							myTQLPenSignal.requestBattaryTimeout();
							break;
						case ErrorStatus.REQUEST_USEDMEM_TIMEOUT://获取已使用内存超时
							myTQLPenSignal.requestMemoryTimeout();
							break;
						case ErrorStatus.ERR_SET_PENNAME:   //设置笔名，命令下发失败
							myTQLPenSignal.onPenNameSetupResponse(false);
							break;
					}
				}
			}
		});
	}

	@Override
	public void onException(BLEException e) {
		Log.e("EverPenManager", "onException:" + e);
	}

	@Override
	public void onReceivePenType(String type) {
		Log.e("EverPenManager", "onReceivePenType:" + type);
	}

	@Override
	public void onReceivePenEnableLed(Boolean aBoolean) {
		Log.e("EverPenManager", "onReceivePenEnableLed:" + aBoolean);
	}

	@Override
	public void onReceivePenHandwritingColor(int i) {
		Log.e("EverPenManager", "onReceivePenHandwritingColor:" + i);
	}

	@Override
	public void onReceiveElementCode(ElementCode elementCode) {
		Log.e("EverPenManager", "onReceiveElementCode:" + elementCode);
	}

	/**
	 * TQLPenSignal end
	 */

	public void startOrStopFindDevice(boolean enable) {
		Log.e("EverPenManager", "startOrStopFindDevice:" + enable);
		if (agent != null) {
			if (enable) {
				removeNoConnected();
				Log.e("EverPenManager", "FindAllDevices");
				agent.FindAllDevices(this);
			} else {
				Log.e("EverPenManager", "stopFindDevice");
				agent.stopFindAllDevices();
			}
		}
	}

	@Override
	public void onScanResult(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
		Log.e("EverPenManager", "onScanResult,device:" + bluetoothDevice);
		EverPen everPen = new EverPen(bluetoothDevice.getAddress());
		everPen.setName(bluetoothDevice.getName());
		everPen.setSimilaPenSource(host.getResources().getString(R.string.similar_pen_source));
		everPen.setPenType(host.getResources().getString(R.string.impression_electronic_pen));
		if (mAutoPenInfo != null && mAutoPenInfo.getMacAddress().equals(everPen.getMacAddress())) {
			mAutoPenInfo = everPen;
			mAutoPenInfo.setClick(true);
			if (!mIsAutoConnected) {
				mIsAutoConnected = true;
				connect(mAutoPenInfo);
			}
		}
		Iterator<MyTQLPenSignal> it = mTQLPenSignalMap.keySet().iterator();
		while (it.hasNext()) {
			MyTQLPenSignal myTQLPenSignal = it.next();
			myTQLPenSignal.onScanResult(everPen);
		}

		addScanedEverPen(everPen);
	}

	@Override
	public void onScanFailed(BLEException e) {
		Log.e("EverPenManager", "onScanFailed,exception:" + e.getMessage());
		Iterator<MyTQLPenSignal> it = mTQLPenSignalMap.keySet().iterator();
		while (it.hasNext()) {
			MyTQLPenSignal myTQLPenSignal = it.next();
			myTQLPenSignal.onScanFailed(e);
		}
		if (mAutoPenInfo != null) {
			startOrStopFindDevice(true);
		}
	}

	public PenCommAgent getBleManager() {
		return agent;
	}

	private synchronized void addScanedEverPen(EverPen everPen) {
        /*EverPen pen = new EverPen(everPen.getMacAddress());
        pen.setName(everPen.getName());*/
		if (!everPens.contains(everPen)) {
			everPens.add(everPen);
			Log.e("EverPenManager", "addScanedEverPen,device:" + everPen);
			/*if (mCurrentPen != null) {
				if (mCurrentPen.getMacAddress().equals(everPen.getMacAddress())) {
					if (!mCurrentPen.isConnected()) {
						everPen.setClick(true);
						mCurrentPen = everPen;
//						mHandler.sendEmptyMessage(AUTOCONNECT);
					}
				}
			}*/
		}
	}

	private void removeNoConnected() {
		for (EverPen everPen : everPens) {
			if (!everPen.isConnected()) {
				everPens.remove(everPen);
			}
		}
	}

	public EverPen getCurrentPen() {
		return mCurrentPen;
	}

	public EverPen getAutoPen() {
		return mAutoPenInfo;
	}

	public void deleteAutoConnectPen() {
		mAutoPenInfo = null;
		SharedPreferencesUtils.putString(AppConfig.EVERPENINFO, AppConfig.EVERPENINFO, null);
		startOrStopFindDevice(true);
	}

	public void connect(EverPen everPen) {
		mCurrentPen = everPen;
		mAutoPenInfo = everPen;
		mAutoPenInfo.setClick(true);
		SharedPreferencesUtils.putString(AppConfig.EVERPENINFO, AppConfig.EVERPENINFO, mAutoPenInfo);
		agent.connect(everPen.getMacAddress());
	}

	public void disconnect(EverPen everPen) {
		String macAddress = everPen.getMacAddress();
		agent.disconnect(macAddress);

	}
}
