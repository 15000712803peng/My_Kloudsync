package com.kloudsync.techexcel.personal;

import android.os.Bundle;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.mvp.BaseActivity;
import com.kloudsync.techexcel.mvp.presenter.CurrentNotePresenter;
import com.kloudsync.techexcel.mvp.view.CurrentNoteView;

public class CurrentNoteActivity extends BaseActivity<CurrentNotePresenter> implements CurrentNoteView {

	public static final String CURRENTPAGE = "currentpage";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected int getLayout() {
		return R.layout.activity_current_note;
	}

	@Override
	protected void initPresenter() {
		mPresenter = new CurrentNotePresenter();
	}

	@Override
	protected void initView() {

	}

	@Override
	protected void initListener() {

	}

	@Override
	protected void initData() {

	}

	@Override
	public void toast(String msg) {
		super.toast(msg);
	}

	@Override
	public void showLoading() {
		super.showLoading();
	}

	@Override
	public void dismissLoading() {
		super.dismissLoading();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
