package com.kloudsync.techexcel.frgment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshWebView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;

public class CommunityFragment extends MyFragment implements OnClickListener {

	private boolean isLoadDataFinish = false, isPrepared = false;
	protected WebView mWebView;
	private PullToRefreshWebView refreshWebView;
	private LinearLayout backll;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.advancedfragment, container,
				false);
		initView(view);
		isPrepared = true;
		lazyLoad();
		return view;
	}

	@Override
	protected void lazyLoad() {
		// TODO Auto-generated method stub
		if (isPrepared && isVisible) {
			if (!isLoadDataFinish) {
				isLoadDataFinish = true;
				mWebView.loadUrl(AppConfig.targetUrl);
			}
		}
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initView(View view) {

		backll = (LinearLayout) view.findViewById(R.id.backll);
		backll.setVisibility(View.INVISIBLE);
		backll.setOnClickListener(this);
		refreshWebView = (PullToRefreshWebView) view.findViewById(R.id.scrollview);
		refreshWebView.setMode(Mode.PULL_FROM_START);
		refreshWebView.getLoadingLayoutProxy().setPullLabel(getString(R.string.pulldownre));
		mWebView = refreshWebView.getRefreshableView();
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true); // 支持通过JS打开新窗口
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		// mWebView.getSettings().setUseWideViewPort(true); // 设置此属性，可任意比例缩放
		mWebView.getSettings().setAllowFileAccess(true);
		mWebView.getSettings().setAllowFileAccessFromFileURLs(false);
		mWebView.getSettings().setLoadWithOverviewMode(true);
		refreshWebView.getLoadingLayoutProxy().setReleaseLabel(
				getString(R.string.pullrefresh));
		refreshWebView.getLoadingLayoutProxy().setRefreshingLabel(
				getString(R.string.pull));

		refreshWebView.setOnRefreshListener(new OnRefreshListener<WebView>() {
			@Override
			public void onRefresh(PullToRefreshBase<WebView> refreshView) {
				// TODO Auto-generated method stub
				refreshWebView.getLoadingLayoutProxy().setReleaseLabel(
						getString(R.string.pullrefresh));
				refreshWebView.getLoadingLayoutProxy().setRefreshingLabel(
						getString(R.string.pull));
				if (refreshView.isShownHeader()) {
					mWebView.loadUrl(mWebView.getUrl());
				}
			}
		});
		mWebView.setWebViewClient(new WebViewClient() {
			// 打开新窗口 覆盖 不是打开新的页面
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.equals(AppConfig.targetUrl)) {
					backll.setVisibility(View.INVISIBLE);
				} else {
					backll.setVisibility(View.VISIBLE);
				}
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
				refreshWebView.onRefreshComplete();
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// TODO Auto-generated method stub
				super.onPageStarted(view, url, favicon);
			}

		});
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int id = arg0.getId();
		switch (id) {
		case R.id.backll:
			mWebView.goBack(); // goBack()表示返回WebView的上一页面
			break;
		default:
			break;
		}
	}

}
