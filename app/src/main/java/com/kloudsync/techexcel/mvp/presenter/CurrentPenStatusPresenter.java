package com.kloudsync.techexcel.mvp.presenter;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.EverPen;
import com.kloudsync.techexcel.mvp.view.CurrentPenStatusView;
import com.tqltech.tqlpencomm.BLEException;

public class CurrentPenStatusPresenter extends TQLPenSignalKloudPresenter<CurrentPenStatusView> {
	@Override
	public void onScanResult(EverPen everPen) {

	}

	@Override
	public void onScanFailed(BLEException e) {

	}

	@Override
	public void onConnected() {
		if (getView() != null) {
			getView().setCurrentPenStatus(R.string.the_connected, R.drawable.current_pen_connected_icon, R.string.go_to_my_notes);
			getView().setCurrentPenTips(true);
			getView().getPenPower();
			getView().getPenMemory();
		}
	}

	@Override
	public void onDisconnected() {
		if (getView() != null) {
			getView().setCurrentPenStatus(R.string.not_connected, R.drawable.register_red_tips_icon, R.string.connected);
			getView().setCurrentPenTips(false);
		}
	}

	@Override
	public void onConnectFailed() {
		if (getView() != null) {
			getView().setCurrentPenStatus(R.string.not_connected, R.drawable.register_red_tips_icon, R.string.connected);
			getView().setCurrentPenTips(false);
		}
	}

	@Override
	public void onReceivePenBattery(int penBattery, boolean bIsCharging) {
		if (getView() != null) {
			getView().setCurrentPenBattery(penBattery, bIsCharging);
		}
	}

	@Override
	public void onReceivePenMemory(int penMemory) {
		if (getView() != null) {
			getView().setCurrentPenStorage(penMemory);
		}

	}

	@Override
	public void bleConnectTimeout() {
		if (getView() != null) {
			getView().setCurrentPenStatus(R.string.not_connected, R.drawable.register_red_tips_icon, R.string.connected);
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
		if (getView() != null) {
			getView().getPenMemory();
		}
	}

	@Override
	public void onPenNameSetupResponse(boolean bIsSuccess) {
		if (getView() != null) {
			getView().setPenName(bIsSuccess);
		}
	}

}
