package com.kloudsync.techexcel.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.info.Knowledge;
import com.umeng.analytics.MobclickAgent;

public class KnowledgeDetail extends Activity {

	Knowledge knowledge = new Knowledge();
	
	private WebView wv_kw;
	private TextView tv_back;
	private ImageView img_forward;
	public PopupWindow mPopupWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.knowledge_detail);

		knowledge = (Knowledge) getIntent().getSerializableExtra("Knowledge");
		initView();
		getPopupWindowInstance();

	}

	@SuppressLint("SetJavaScriptEnabled") 
	private void initView() {
		tv_back = (TextView) findViewById(R.id.tv_back);
		img_forward = (ImageView) findViewById(R.id.img_forward);
		wv_kw = (WebView) findViewById(R.id.wv_kw);

		wv_kw.getSettings().setJavaScriptEnabled(true);
		wv_kw.getSettings().setJavaScriptCanOpenWindowsAutomatically(true); 
		wv_kw.setEnabled(true);
		wv_kw.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		
		wv_kw.setWebViewClient(new WebViewClient() {
			// 打开新窗口 覆盖 不是打开新的页面
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		
		showInfo();

		tv_back.setOnClickListener(new myOnClick());
		img_forward.setOnClickListener(new myOnClick());
		
	}

	private void showInfo() {
		wv_kw.loadUrl(AppConfig.URL_KNOWLEDGE + knowledge.getKnowledgeID());
		
	}
	
	protected class myOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_back:
				finish();
				break;
			case R.id.img_forward:
				mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
//				mPopupWindow.showAsDropDown(v);
				mPopupWindow.showAtLocation(wv_kw, Gravity.BOTTOM, 0, 0);
				
				break;
				
			default:
				break;
			}
		}

	}
	
	/*
	 * 获取PopupWindow实例
	 */
	private void getPopupWindowInstance() {
		if (null != mPopupWindow) {
			mPopupWindow.dismiss();
			return;
		} else {
			initPopuptWindow();
		}
	}

	/*
	 * 创建PopupWindow
	 */
	@SuppressWarnings("deprecation")
	private void initPopuptWindow() {
		LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
		// View popupWindow = layoutInflater.inflate(R.layout.popup_window3,
		// null);
		View popupWindow = layoutInflater
				.inflate(R.layout.pop_kwdetail, null);
		// View popupWindow = layoutInflater.inflate(R.layout.popup_window2,
		// null);
		TextView tv_forward = (TextView) popupWindow
				.findViewById(R.id.tv_forward);
		TextView tv_close = (TextView) popupWindow
				.findViewById(R.id.tv_close);

		tv_forward.setOnClickListener(new MypopClick());
		tv_close.setOnClickListener(new MypopClick());
		
		WindowManager window = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		int width = getResources().getDisplayMetrics().widthPixels;
		int height = getResources().getDisplayMetrics().heightPixels;
		// 创建一个PopupWindow
		// 参数1：contentView 指定PopupWindow的内容
		// 参数2：width 指定PopupWindow的width
		// 参数3：height 指定PopupWindow的height
		mPopupWindow = new PopupWindow(popupWindow, width - 200, height / 4,
				false);

		// getWindowManager().getDefaultDisplay().getWidth();
		// getWindowManager().getDefaultDisplay().getHeight();
		mPopupWindow.getWidth();
		mPopupWindow.getHeight();

		// 使其聚焦
		mPopupWindow.setFocusable(true);
		// 设置允许在外点击消失
		mPopupWindow.setOutsideTouchable(true);
		// 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
	}

	private class MypopClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_forward:
				GetForward();
				break;
			case R.id.tv_close:
				mPopupWindow.dismiss();
				break;

			default:

			}

		}

	}
	

	private void GetForward() {
		SendKnowledge.instance.finish();
		mPopupWindow.dismiss();
		AppConfig.KNOWLEDGE =  knowledge;
		AppConfig.isSendKnowledge = true;
		finish();
	}
	
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("KnowledgeDetail"); 
	    MobclickAgent.onResume(this);       //统计时长
	}
	public void onPause() {
	    super.onPause();
        MobclickAgent.onPageEnd("KnowledgeDetail");
	    MobclickAgent.onPause(this);
	}
}
