package com.kloudsync.techexcel.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.message.SendFileMessage;
import com.umeng.analytics.MobclickAgent;

public class ShowKnowledgeDetail extends Activity {

	
	private WebView wv_kw;
	private TextView tv_back;

	private String content;
	private String title;
	private String knowledgeID;
	private String imageID;
	private String videoInfo;
	
	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_knowledge_detail);

		content = getIntent().getExtras().getString("content", "");
		title = getIntent().getExtras().getString("title", "");
		knowledgeID = getIntent().getExtras().getString("knowledgeID", "");
		imageID = getIntent().getExtras().getString("imageID", "");
		videoInfo = getIntent().getExtras().getString("videoInfo", "");

		SendFileMessage ff = (SendFileMessage) getIntent().getSerializableExtra("sendFileMessage");
		Log.e("SendFileMessage",ff.getAttachmentID() + ":" + ff.getFileDownloadURL() + ":" + ff.getFileName());
		
		initView();

	}

	@SuppressLint("SetJavaScriptEnabled") 
	private void initView() {
		tv_back = (TextView) findViewById(R.id.tv_back);
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
		
	}

	private void showInfo() {
		wv_kw.loadUrl(AppConfig.URL_KNOWLEDGE + knowledgeID);
		
	}
	
	protected class myOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_back:
				finish();
				break;
				
			default:
				break;
			}
		}

	}

	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("ShowKnowledgeDetail"); 
	    MobclickAgent.onResume(this);       //统计时长
	}
	public void onPause() {
	    super.onPause();
        MobclickAgent.onPageEnd("ShowKnowledgeDetail");
	    MobclickAgent.onPause(this);
	}
	
}
