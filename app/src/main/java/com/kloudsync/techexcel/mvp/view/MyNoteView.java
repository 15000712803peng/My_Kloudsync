package com.kloudsync.techexcel.mvp.view;

import com.tqltech.tqlpencomm.Dot;

public interface MyNoteView extends KloudView {
	void onReceiveDot(Dot dot);

	void onReceiveOfflineStrokes(Dot dot);
}
