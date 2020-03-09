package com.kloudsync.techexcel.mvp.presenter;

import com.kloudsync.techexcel.bean.EverPen;
import com.kloudsync.techexcel.help.MyTQLPenSignal;
import com.kloudsync.techexcel.mvp.view.PenSeetingView;
import com.tqltech.tqlpencomm.BLEException;

public class PenSeetingPresenter extends KloudPresenter<PenSeetingView> implements MyTQLPenSignal {
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
		if (getView() != null) {
			getView().onDisconnected();
		}
	}

	@Override
	public void onConnectFailed() {

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
		if (getView() != null) {
			getView().setPenName(bIsSuccess);
		}
	}
}
