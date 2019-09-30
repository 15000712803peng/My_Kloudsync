package com.kloudsync.techexcel.help;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.ub.service.activity.AlertDialogActivity;
import com.ub.service.activity.NotifyActivity;
import com.kloudsync.techexcel.R;

public class PopNotify {

	public Context mContext;

	public void getPopwindow(Context context) {
		this.mContext = context;

		getPopupWindowInstance();
		mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
	}
	

	public PopupWindow mPopupWindow;

	public void getPopupWindowInstance() {
		if (null != mPopupWindow) {
			mPopupWindow.dismiss();
			return;
		} else {
			initPopuptWindow();
		}
	}

	private LinearLayout lin1, lin2;
	private ImageView img_close;
	
	@SuppressWarnings("deprecation")
	public void initPopuptWindow() {
		LayoutInflater layoutInflater = LayoutInflater.from(mContext);
		View popupWindow = layoutInflater.inflate(R.layout.popup_notify, null);

		lin1 = (LinearLayout) popupWindow.findViewById(R.id.lin1);
		lin2 = (LinearLayout) popupWindow.findViewById(R.id.lin2);
		
		

		mPopupWindow = new PopupWindow(popupWindow, LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT, false);

		mPopupWindow.getWidth();
		mPopupWindow.getHeight();

		lin1.setOnClickListener(new myOnClick());
		lin2.setOnClickListener(new myOnClick());
		

		// 使其聚焦
		mPopupWindow.setFocusable(true);
		// 设置允许在外点击消失
		mPopupWindow.setOutsideTouchable(true);
		// 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
	}

	
	private class myOnClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.lin1:
				((Activity) mContext).startActivity(new Intent(mContext,AlertDialogActivity.class));
				mPopupWindow.dismiss();	
				break;
			case R.id.lin2:
				((Activity) mContext).startActivity(new Intent(mContext,NotifyActivity.class));
				mPopupWindow.dismiss();		
				break;

			default:
				break;
			}
			
		}

		
	}

	private View view;
	public void StartPop(View v) {
		view = v;
		mPopupWindow.showAsDropDown(v);
	}


}
