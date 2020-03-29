package com.kloudsync.techexcel.mvp.view;

import com.tqltech.tqlpencomm.Dot;
import com.tqltech.tqlpencomm.PenCommAgent;

/**
 * Created by tonyan on 2019/10/29.
 */

public interface IMainActivityView extends KloudView{

	void getBleManager(PenCommAgent bleManager);

	void onConnected();

	void onDisconnected();

	void onConnectFailed();

	void onReceiveDot(Dot dot);

	void onReceiveOfflineStrokes(Dot dot);
}
