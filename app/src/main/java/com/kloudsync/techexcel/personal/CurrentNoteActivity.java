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
}
