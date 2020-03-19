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
import com.kloudsync.techexcel.tool.SyncWebNoteActionsCache;
import com.tqltech.tqlpencomm.BLEException;
import com.tqltech.tqlpencomm.BLEScanner;
import com.tqltech.tqlpencomm.Dot;
import com.tqltech.tqlpencomm.ElementCode;
import com.tqltech.tqlpencomm.ErrorStatus;
import com.tqltech.tqlpencomm.PenCommAgent;
import com.tqltech.tqlpencomm.PenStatus;
import com.tqltech.tqlpencomm.listener.TQLPenSignal;
import com.ub.techexcel.bean.NoteDotBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

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
	private CopyOnWriteArrayList<PenDotsReceiver> dotsReceivers = new CopyOnWriteArrayList<>();
	private List<NoteDotBean> mDotOnlineList = new ArrayList<>();
	private List<NoteDotBean> mDotOfflineList;
	private List<NoteDotBean> mWebSocketOfflineList;
	private long mGetPenRtcTimeCurrentTime;//获取笔的rtc时间时的当前时间戳
	private long mDifferenceTime;//当前时间与笔时间的时间戳差值

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
//		agent.getPenOfflineDataList();
		host.runOnUiThread(new Runnable() {
			@Override
			public void run() {
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
		EverPenDataManger.getInstace(EverPenManger.this, host).removeHandlerMessage();
		host.runOnUiThread(new Runnable() {
			@Override
			public void run() {
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
		EverPenDataManger.getInstace(EverPenManger.this, host).removeHandlerMessage();
		host.runOnUiThread(new Runnable() {
			@Override
			public void run() {
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

	private final double B5_WIDTH = 119.44;
	private final double B5_HEIGHT = 168;
	private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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

		for (PenDotsReceiver receiver : dotsReceivers) {
			receiver.onDotReceive(dot);
		}
		String uuid = UUID.randomUUID().toString()/* + System.currentTimeMillis()*/;
		NoteDotBean noteDotBean = new NoteDotBean();
		noteDotBean.setDotId(uuid);
		noteDotBean.setDot(dot);
		mDotOnlineList.add(noteDotBean);
		switch (dot.type) {
			case PEN_UP:
				EverPenDataManger.getInstace(this, host).sendNoteDataToWebSocket(dot, mDotOnlineList);
				EverPenDataManger.getInstace(this, host).cacheDotListData(mDotOnlineList);
				mDotOnlineList.clear();
				break;
		}
	}

	long mOneDayTime = 24 * 60 * 60 * 1000;

	@Override
	public void onReceiveOfflineStrokes(final Dot dot) {
		Log.e("EverPenManager", "onReceiveOfflineStrokes:" + dot);
		long l = mGetPenRtcTimeCurrentTime - dot.timelong;
		long l1 = dot.timelong - mGetPenRtcTimeCurrentTime;
		if (l > mOneDayTime) {
			dot.timelong = dot.timelong + mDifferenceTime;
		} else if (l1 > mOneDayTime) {
			dot.timelong = dot.timelong - mDifferenceTime;
		}
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
		String uuid = UUID.randomUUID().toString() + System.currentTimeMillis();
		NoteDotBean noteDotBean = new NoteDotBean();
		noteDotBean.setDotId(uuid);
		noteDotBean.setDot(dot);
		mWebSocketOfflineList.add(noteDotBean);
		mDotOfflineList.add(noteDotBean);
		switch (dot.type) {
			case PEN_UP:
				EverPenDataManger.getInstace(this, host).sendNoteDataToWebSocket(dot, mWebSocketOfflineList);
				mWebSocketOfflineList.clear();
				break;
		}
	}

	@Override
	public void onOfflineDataList(int i) {
		Log.e("EverPenManager", "onOfflineDataList:" + i);
		if (i == 0) {
			agent.ReqAdjustRTC();
			EverPenDataManger.getInstace(this, host).sendHandlerMessage();
		} else {
			mWebSocketOfflineList = new ArrayList<>();
			mDotOfflineList = new ArrayList<>();
			agent.ReqOfflineDataTransfer(true);
		}
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
		if (b) {
			agent.ReqAdjustRTC();
			EverPenDataManger.getInstace(this, host).cacheDotListData(mDotOfflineList);
			EverPenDataManger.getInstace(this, host).sendHandlerMessage();
			mDotOfflineList.clear();
			agent.RemoveOfflineData();//离线数据获取完成后删除
		}
	}

	@Override
	public void onReceiveOfflineProgress(int i) {
		Log.e("EverPenManager", "onReceiveOfflineProgress:" + i);
	}

	@Override
	public void onPenDeleteOfflineDataResponse(boolean b) {
		Log.e("EverPenManager", "onPenDeleteOfflineDataResponse:" + b);
		if (!b) {
			List<String> uuidList = new ArrayList<>();
			for (int i = 0; i < mDotOfflineList.size(); i++) {
				String dotId = mDotOfflineList.get(i).getDotId();
				uuidList.add(dotId);
			}
			SyncWebNoteActionsCache.getInstance(host).removeListActions(uuidList);
			mDotOfflineList.clear();
		}
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
		if (b) {
			agent.getPenRtc();
		}
	}

	@Override
	public void onReceivePenTime(long l) {
		Log.e("EverPenManager", "onReceivePenTime:" + l);
		try {
			l *= 1000;
			Date date = mSimpleDateFormat.parse(" 2010-01-01 00:00:00");
			long time = date.getTime();
			mGetPenRtcTimeCurrentTime = System.currentTimeMillis() - time;
			mDifferenceTime = mGetPenRtcTimeCurrentTime - l;
		} catch (ParseException e) {
			e.printStackTrace();
		}

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
//        if (mAutoPenInfo != null) {
//            startOrStopFindDevice(true);
//        }
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

	public interface PenDotsReceiver {
		void onDotReceive(Dot dot);
	}

	public void addDotsReceiver(PenDotsReceiver receiver) {
		if (!dotsReceivers.contains(receiver)) {
			dotsReceivers.add(receiver);
		}
	}

	public void removeDotsReceiver(PenDotsReceiver receiver) {
		if (dotsReceivers.contains(receiver)) {
			dotsReceivers.remove(receiver);
		}
	}
}
