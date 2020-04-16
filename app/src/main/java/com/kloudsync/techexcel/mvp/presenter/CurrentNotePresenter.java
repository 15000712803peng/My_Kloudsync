package com.kloudsync.techexcel.mvp.presenter;

import com.kloudsync.techexcel.mvp.view.CurrentNoteView;
import com.tqltech.tqlpencomm.Dot;

public class CurrentNotePresenter extends TQLPenSignalKloudPresenter<CurrentNoteView> {

	@Override
	public void onReceiveDot(Dot dot) {
		if (getView() != null) {
			getView().onReceiveDot(dot);
		}
	}

	@Override
	public void onReceiveOfflineStrokes(Dot dot) {
		if (getView() != null) {
			getView().onReceiveOfflineStrokes(dot);
		}
	}
}
