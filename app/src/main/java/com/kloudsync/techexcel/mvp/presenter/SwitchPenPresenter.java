package com.kloudsync.techexcel.mvp.presenter;

import com.kloudsync.techexcel.bean.EverPen;
import com.kloudsync.techexcel.mvp.view.SwitchPenView;
import com.tqltech.tqlpencomm.BLEException;

public class SwitchPenPresenter extends TQLPenSignalKloudPresenter<SwitchPenView> {
	@Override
	public void onScanResult(EverPen everPen) {
		if (getView() != null) {
			getView().addScanedEverPen(everPen);
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
			getView().startActivity();
			getView().notifyDataSetChanged();
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

	}
}
