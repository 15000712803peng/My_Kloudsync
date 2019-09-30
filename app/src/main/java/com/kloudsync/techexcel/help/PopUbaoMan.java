package com.kloudsync.techexcel.help;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.ub.techexcel.service.ConnectService;

import org.json.JSONException;
import org.json.JSONObject;

public class PopUbaoMan {

	public Context mContext;
	public int width;
	public int height;
	public String top;
	
	private static PoPDismissListener popdismissListener;
	
	public interface PoPDismissListener{
		void PopDismiss();
	}
	
	public void setPoPDismissListener(PoPDismissListener popdismissListener){
		PopUbaoMan.popdismissListener = popdismissListener;
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

	private TextView tv_apply, tv_top;
	private ImageView img_close;
	
	@SuppressWarnings("deprecation")
	public void initPopuptWindow() {
		LayoutInflater layoutInflater = LayoutInflater.from(mContext);
		View popupWindow = layoutInflater.inflate(R.layout.pop_ubao_man, null);
		
		tv_apply = (TextView) popupWindow.findViewById(R.id.tv_apply);
		tv_top = (TextView) popupWindow.findViewById(R.id.tv_top);
		img_close = (ImageView) popupWindow.findViewById(R.id.img_close);
		
		

		mPopupWindow = new PopupWindow(popupWindow, width, height, false);

		mPopupWindow.getWidth();
		mPopupWindow.getHeight();
		mPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				popdismissListener.PopDismiss();
			}
		});
		
		tv_top.setText(top);
		
		tv_apply.setOnClickListener(new myOnClick());
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
			case R.id.tv_apply:
//				BecomeUBMan();
				mPopupWindow.dismiss();	
				break;
			case R.id.img_close:
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
		mPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY, 0, 0);
	}

	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case AppConfig.BECOME_UBMAN:
				AppConfig.UserType = 1;
				mPopupWindow.dismiss();
				PopUbaoManSuccess pum = new PopUbaoManSuccess();
				pum.getPopwindow(mContext, width, height,
						mContext.getString(R.string.Become_Success));
				pum.StartPop(view);
				break;
			case AppConfig.FAILED:
				String result = (String) msg.obj;
				Toast.makeText(mContext, result,
						Toast.LENGTH_LONG).show();
				break;

			default:
				break;
			}
		}

	};
	
	
	public void BecomeUBMan() {
        new ApiTask(new Runnable() {
			@Override
			public void run() {
				try {
					JSONObject responsedata = ConnectService.submitDataByJson(
							AppConfig.URL_PUBLIC
									+ "User/Apply4UpLevel", null);
					Log.e("返回的responsedata", responsedata.toString() + "");
					String retcode = responsedata.getString("RetCode");
					Message msg = new Message();
					if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
						msg.what = AppConfig.BECOME_UBMAN;		
						msg.obj = responsedata.toString();
					}else{
						msg.what = AppConfig.FAILED;
						String ErrorMessage = responsedata.getString("ErrorMessage");
						msg.obj = ErrorMessage;
					}
					handler.sendMessage(msg);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
        }).start(ThreadManager.getManager());
		
	}

}
