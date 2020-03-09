package com.kloudsync.techexcel.mvp.presenter;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.EverPen;
import com.kloudsync.techexcel.help.MyTQLPenSignal;
import com.kloudsync.techexcel.mvp.view.EnterPairingView;
import com.tqltech.tqlpencomm.BLEException;

public class EnterPairingPresenter extends KloudPresenter<EnterPairingView> implements MyTQLPenSignal {

	@Override
	public void onScanResult(EverPen everPen) {
		if (getView() != null) {
			getView().addScanedEverPen(everPen);
			getView().setScanningBtnText(true, R.string.start_scanning, 1.0f);
		}
	}

	@Override
	public void onScanFailed(BLEException e) {
		if (getView() != null) {
			getView().onScanFailed(e);
		}
	}

	@Override
	public void onConnected() {
		if (getView() != null) {
			getView().setConnected(true);
			getView().setClick(false);
			getView().notifyDataSetChanged();
			getView().setScanningBtnText(true, R.string.start_scanning, 1.0f);

		}
	}

	@Override
	public void onDisconnected() {
		if (getView() != null) {
			getView().setConnected(false);
			getView().setClick(false);
			getView().connect();
			getView().notifyDataSetChanged();
		}
	}

	@Override
	public void onConnectFailed() {
		if (getView() != null) {
			getView().setConnected(false);
			getView().setClick(false);
			getView().removeNoConnected();
			getView().notifyDataSetChanged();
		}
	}

	@Override
	public void onReceivePenBattery(int penBattery, boolean bIsCharging) {

	}

	@Override
	public void onReceivePenMemory(int penMemory) {

	}

	@Override
	public void bleConnectTimeout() {
		if (getView() != null) {
			getView().setClick(true);
			getView().notifyDataSetChanged();
		}
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
