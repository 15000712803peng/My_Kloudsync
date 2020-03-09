package com.kloudsync.techexcel.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.mvp.BaseActivity;
import com.kloudsync.techexcel.mvp.presenter.DigitalPensPresenter;
import com.kloudsync.techexcel.mvp.view.DigitalPensView;

import butterknife.Bind;

/**
 * Created by tonyan on 2020/1/16.
 */

public class DigitalPensActivity extends BaseActivity<DigitalPensPresenter> implements DigitalPensView {

	@Bind(R.id.layout_back)
	RelativeLayout mBack;
    @Bind(R.id.tv_title)
    TextView mTitleText;
	@Bind(R.id.lly_digital_impression)
	LinearLayout mLlyDigitalImpression;
	@Bind(R.id.tv_digital_pen_impression)
	TextView mTvPenImpression;
	@Bind(R.id.tv_digital_similar_pen_source)
	TextView mTvSimilarPenSource;
	@Bind(R.id.lly_digital_you_dao_yun)
	LinearLayout mLlyDigitalYDY;
	@Bind(R.id.tv_digital_pen_you_dao_yun)
	TextView mTvPenYDY;
	@Bind(R.id.tv_digital_similar_pen_source2)
	TextView mTvSimilarPenSource2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	@Override
	protected int getLayout() {
		return R.layout.activity_digital_pen;
	}

	@Override
	protected void initPresenter() {

	}

	@Override
	protected void initView() {
		mTitleText.setText(R.string.title_select_digital_pen);
	}

	@Override
	protected void initListener() {
		mBack.setOnClickListener(this);
		mLlyDigitalImpression.setOnClickListener(this);
		mLlyDigitalYDY.setOnClickListener(this);
	}

	@Override
	protected void initData() {

	}


	@Override
	public void onClick(View v) {
		Intent intent;
		String similarPenSource;
		String penType;
		switch (v.getId()) {
			case R.id.layout_back:
				finish();
				break;
			case R.id.lly_digital_impression:
				similarPenSource = mTvSimilarPenSource.getText().toString().trim();
				penType = mTvPenImpression.getText().toString().trim();
				intent = new Intent(this, EnterPairingActivity.class);
				intent.putExtra(EnterPairingActivity.SIMILARPENSOURCE, similarPenSource);
				intent.putExtra(EnterPairingActivity.PENTYPE, penType);
				startActivity(intent);
				break;
			case R.id.lly_digital_you_dao_yun:
				similarPenSource = mTvSimilarPenSource2.getText().toString().trim();
				penType = mTvPenYDY.getText().toString().trim();
				intent = new Intent(this, EnterPairingActivity.class);
				intent.putExtra(EnterPairingActivity.SIMILARPENSOURCE, similarPenSource);
				intent.putExtra(EnterPairingActivity.PENTYPE, penType);
				startActivity(intent);
				break;
		}
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
