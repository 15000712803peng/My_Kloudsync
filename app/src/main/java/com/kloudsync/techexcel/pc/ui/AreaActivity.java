package com.kloudsync.techexcel.pc.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.pc.help.SendPostAndGet;

import org.json.JSONException;
import org.json.JSONObject;

public class AreaActivity extends Activity implements OnClickListener {
	private EditText editText;
	private ImageView img;
	private LinearLayout layout;
	private Button button;
	private String mContent = null;
	private String topname;
	private TextView tv_topname,tv_save;
	private String settvname;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {

			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		topname = getIntent().getStringExtra("topname");
		setContentView(R.layout.pc_area);
		initview();
	}

	private void initview() {
		editText = (EditText) findViewById(R.id.editarea);
		tv_topname = (TextView) findViewById(R.id.topname);
		if (topname.equals("name")) {
			settvname = getString(R.string.name) + "";
		} else if (topname.equals("description")) {
			settvname = getString(R.string.description) + "";
		} else if (topname.equals("signature")) {
			settvname = getString(R.string.signature) + "";
		} else if (topname.equals("area")) {
			settvname = getString(R.string.area) + "";
		}
		tv_topname.setText(settvname);
		editText.setText(mContent);
		if (mContent != null) {
			editText.setSelection(mContent.length());
		}
		tv_save = (TextView) findViewById(R.id.tv_save);
		tv_save.setOnClickListener(this);
		img = (ImageView) findViewById(R.id.imgback);
		img.setOnClickListener(this);
		layout = (LinearLayout) findViewById(R.id.area);
		layout.setOnClickListener(this);
		button = (Button) findViewById(R.id.areasendpost);
		button.setOnClickListener(this);
	}

	@SuppressLint("ShowToast")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id._shareicontext:
			// 弹出软键盘
			// 键盘
			@SuppressWarnings("static-access")
			final InputMethodManager inputManager = (InputMethodManager) editText
					.getContext().getSystemService(this.INPUT_METHOD_SERVICE);
			inputManager.showSoftInput(editText, 0);
			break;
		case R.id.imgback:
			finish();
			break;
		case R.id.areasendpost:
			save();
			break;
		case R.id.tv_save:
			finish();
			break;
		default:
			break;
		}
	}

	public void save() {
		final Message message = new Message();
//		final JSONObject jsonObject = format();
		new ApiTask(new Runnable() {
			@Override
			public void run() {
				try {
					// JSONObject responsedata =
					// SendPostAndGet.submitDataByJson(
					// "http://192.168.22.5/UBAODebug/V1/User/Choices4Register",
					// jsonObject);
					Log.e("zhang", "hello");
					JSONObject responsedata = SendPostAndGet
							.getIncidentbyHttpGet("http://192.168.22.5/UBAODebug/V1/User/Choices4Register?ChoiceTypeID=2&ParentChoiceID=1");
					Log.e("zhang", "RetData:" + responsedata.get("RetData"));
					int retcode = (Integer) responsedata.get("RetCode");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}).start(ThreadManager.getManager());
	}

	private JSONObject format() {
		final JSONObject jsonObject = new JSONObject();
		try {
			// 接口没出来 模拟一个地址
			jsonObject.put("ChoiceTypeID", "1");
			jsonObject.put("ParentChoiceID", "1");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
