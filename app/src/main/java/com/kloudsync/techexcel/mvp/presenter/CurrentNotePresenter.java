package com.kloudsync.techexcel.mvp.presenter;

import com.kloudsync.techexcel.mvp.view.CurrentNoteView;
import com.tqltech.tqlpencomm.Dot;

public class CurrentNotePresenter extends TQLPenSignalKloudPresenter<CurrentNoteView> {

	@Override
	public void onReceiveDot(Dot dot) {
		super.onReceiveDot(dot);
	}

	@Override
	public void onReceiveOfflineStrokes(Dot dot) {
		super.onReceiveOfflineStrokes(dot);
	}
}
