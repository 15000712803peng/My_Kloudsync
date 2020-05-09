package com.kloudsync.techexcel.help;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.kloudsync.techexcel.R;

public class DocAndMeetingFileListPopupwindow implements View.OnClickListener {

	private Context mContext;
	private PopupWindow mPpw;
	private View mView;

	public DocAndMeetingFileListPopupwindow(Context context) {
		mContext = context;
		initPopupwindow();
	}

	private void initPopupwindow() {
		mView = LayoutInflater.from(mContext).inflate(R.layout.ppw_doc_file_more, null);
		RelativeLayout docMoreShare = mView.findViewById(R.id.moreshare);
		RelativeLayout docMoreEdit = mView.findViewById(R.id.moreedit);
		RelativeLayout docMoreDelete = mView.findViewById(R.id.moredelete);
		docMoreShare.setOnClickListener(this);
		docMoreEdit.setOnClickListener(this);
		docMoreDelete.setOnClickListener(this);
		mPpw = new PopupWindow(mView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		mPpw.setBackgroundDrawable(new ColorDrawable());
		mPpw.setOutsideTouchable(true);
		mPpw.setFocusable(true);
	}

	public void show(final View view) {
		if (mPpw != null) {
			mView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
			view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
			int popupWidth = mView.getMeasuredWidth();    //  获取测量后的宽度
			int popupHeight = mView.getMeasuredHeight();  //获取测量后的高度
			int viewWidth = view.getMeasuredWidth();    //  获取测量后的宽度
			int viewHeight = view.getMeasuredHeight();  //获取测量后的高度
			mPpw.showAsDropDown(view, -popupWidth, -popupHeight + viewHeight / 2);
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
		dismiss();
	}
}
