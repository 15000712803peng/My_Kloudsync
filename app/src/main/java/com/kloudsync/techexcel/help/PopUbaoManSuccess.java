package com.kloudsync.techexcel.help;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import com.kloudsync.techexcel.R;

public class PopUbaoManSuccess {

	public Context mContext;
	public int width;
	public int height;
	public String top;
	
	private static PoPDismissListener popdismissListener;
	
	public interface PoPDismissListener{
		void PopDismiss();
	}
	
	public void setPoPDismissListener(PoPDismissListener popdismissListener){
		PopUbaoManSuccess.popdismissListener = popdismissListener;
	}

	public void getPopwindow(Context context, int width, int height, String top) {
		this.mContext = context;
		this.width = width;
		this.height = height;
		this.top = top;

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

	private ImageView img_close;
	
	@SuppressWarnings("deprecation")
	public void initPopuptWindow() {
		LayoutInflater layoutInflater = LayoutInflater.from(mContext);
		View popupWindow = layoutInflater.inflate(R.layout.pop_ubao_man_success, null);
		
		img_close = (ImageView) popupWindow.findViewById(R.id.img_close);
		
		

		mPopupWindow = new PopupWindow(popupWindow, width, height, false);

		mPopupWindow.getWidth();
		mPopupWindow.getHeight();
		mPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
//				popdismissListener.PopDismiss();
			}
		});
		
		
		img_close.setOnClickListener(new myOnClick());
		

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
			case R.id.img_close:
				mPopupWindow.dismiss();				
				break;

			default:
				break;
			}
			
		}

		
	}

	public void StartPop(View v) {
		mPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY, 0, 0);
	}


}
