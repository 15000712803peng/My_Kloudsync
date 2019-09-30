package com.kloudsync.techexcel.pc.ui;

import com.kloudsync.techexcel.R;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SelfDescriptionActivity extends Activity implements OnClickListener{
	private EditText editText;
	private ImageView img;
	private LinearLayout layout;
	private String mContent = null;
	private TextView tv_topname, tv_save;
	private String settvname;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pc_description);
		initview();
	}

	private void initview() {
		editText = (EditText) findViewById(R.id.description);
		tv_topname = (TextView) findViewById(R.id.topname);
		settvname = getString(R.string.description) + "";
		tv_topname.setText(settvname);
		mContent = getIntent().getStringExtra("name");
		editText.setText(mContent);
		if (mContent != null) {
			editText.setSelection(mContent.length());
		}
		tv_save = (TextView) findViewById(R.id.tv_save);
		tv_save.setOnClickListener(this);
		img = (ImageView) findViewById(R.id.imgback);
		img.setOnClickListener(this);
		layout = (LinearLayout) findViewById(R.id.editdescription);
		layout.setOnClickListener(this);
	}

	@SuppressLint("ShowToast")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.editdescription:
			// 弹出软键盘
			@SuppressWarnings("static-access")
			final InputMethodManager inputManager = (InputMethodManager) editText
					.getContext().getSystemService(this.INPUT_METHOD_SERVICE);
			inputManager.showSoftInput(editText, 0);
			break;
		case R.id.imgback:
			finish();
			break;
		case R.id.tv_save:
			finish();
			break;
		default:
			break;
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("SelfDescriptionActivity");
		MobclickAgent.onResume(this); // 统计时长
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("SelfDescriptionActivity");
		MobclickAgent.onPause(this);
	}
}
