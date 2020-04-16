package com.kloudsync.techexcel.mvp.view;

import com.kloudsync.techexcel.bean.EverPen;
import com.tqltech.tqlpencomm.BLEException;

public interface SwitchPenView extends KloudView {
	void addScanedEverPen(EverPen everPen);

	void onScanFailed(BLEException e);

	void setConnected(boolean isConnected);

	void setClick(boolean click);

	void connect();

	void startActivity();

	void removeNoConnected();

	void notifyDataSetChanged();

}
