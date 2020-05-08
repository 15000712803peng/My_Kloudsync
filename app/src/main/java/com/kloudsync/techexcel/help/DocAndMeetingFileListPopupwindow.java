package com.kloudsync.techexcel.help;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.kloudsync.techexcel.R;

public class DocAndMeetingFileListPopupwindow implements View.OnClickListener {

	private Context mContext;
	private PopupWindow mPpw;

	public DocAndMeetingFileListPopupwindow(Context context) {
		mContext = context;
		initPopupwindow();
	}

	private void initPopupwindow() {
		View view = LayoutInflater.from(mContext).inflate(R.layout.ppw_doc_file_more, null);
		RelativeLayout docMoreShare = view.findViewById(R.id.moreshare);
		RelativeLayout docMoreEdit = view.findViewById(R.id.moreedit);
		RelativeLayout docMoreDelete = view.findViewById(R.id.moredelete);
		docMoreShare.setOnClickListener(this);
		docMoreEdit.setOnClickListener(this);
		docMoreDelete.setOnClickListener(this);
		mPpw = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		mPpw.setBackgroundDrawable(new ColorDrawable());
		mPpw.setOutsideTouchable(true);
		mPpw.setFocusable(true);
	}

	public void show(final View view) {
		if (mPpw != null) {
			mPpw.showAtLocation(view, Gravity.LEFT, 0, 0);
		}
	}

	private void dismiss() {
		if (mPpw == null) return;
		mPpw.dismiss();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.moreshare:
				break;
			case R.id.moreedit:
				break;
			case R.id.moredelete:
				break;
		}
//		dismiss();
	}
}
