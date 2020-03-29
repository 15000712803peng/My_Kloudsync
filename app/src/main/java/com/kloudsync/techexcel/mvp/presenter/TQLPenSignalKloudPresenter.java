package com.kloudsync.techexcel.mvp.presenter;

import com.kloudsync.techexcel.bean.EverPen;
import com.kloudsync.techexcel.help.MyTQLPenSignal;
import com.kloudsync.techexcel.mvp.view.KloudView;
import com.tqltech.tqlpencomm.BLEException;
import com.tqltech.tqlpencomm.Dot;
import com.tqltech.tqlpencomm.PenCommAgent;

public class TQLPenSignalKloudPresenter<V extends KloudView> extends KloudPresenter<V> implements MyTQLPenSignal {
	@Override
	public void getBleManager(PenCommAgent bleManager) {

	}

	@Override
	public void onScanResult(EverPen everPen) {
	}

	@Override
	public void onScanFailed(BLEException e) {

	}

	@Override
	public void onConnected() {

	}

	@Override
	public void onDisconnected() {

	}

	@Override
	public void onConnectFailed() {

	}

	@Override
	public void onReceiveDot(Dot dot) {

	}

	@Override
	public void onReceiveOfflineStrokes(Dot dot) {

	}

	@Override
	public void onReceivePenBattery(int penBattery, boolean bIsCharging) {

	}

	@Override
	public void onReceivePenMemory(int penMemory) {

	}

	@Override
	public void bleConnectTimeout() {

	}

	@Override
	public void setNameTimeout() {

	}

	@Override
	public void requestBattaryTimeout() {

	}

	@Override
	public void requestMemoryTimeout() {

	}

	@Override
	public void onPenNameSetupResponse(boolean bIsSuccess) {

	}
}
