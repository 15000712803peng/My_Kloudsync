package com.kloudsync.techexcel.pc.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.start.LoginActivity;
import com.kloudsync.techexcel.ui.MainActivity;
import com.ub.techexcel.service.ConnectService;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangePhoneNumberActivity extends Activity {

	private TextView tv_cphone, tv_reset, tv_sendcheckcode,tv_name;
	private EditText et_telephone, et_checkcode;
	private ImageView img_back;

	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor editor;
	private String telephone;
	private String checkcode;
	private String errorMessage;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case AppConfig.GETCHECKCODE:
				// 短信发送成功后，下次发验证码倒计时
				new ApiTask(new CheckCodeEnable()).start(ThreadManager.getManager());
				break;
			case AppConfig.CHECKCODE:
				// 判断是否可以再次发送验证码
				int time = (Integer) msg.obj;
				ChangeSendEnable(time);
				break;
			case AppConfig.FAILED:
				// 短信发送失败
				String result = (String) msg.obj;
				Toast.makeText(getApplicationContext(), result,
						Toast.LENGTH_LONG).show();
				tv_sendcheckcode.setEnabled(true);
				break;
			case AppConfig.PASSWORDSUCCESS:
				// 账号修改成功跳回登陆界面
				Toast.makeText(getApplicationContext(), "修改成功",
						Toast.LENGTH_LONG).show();
				if (MainActivity.instance != null) {
					MainActivity.instance.finish();
				}
				AccountActivity.accountActivity.finish();

				startActivity(new Intent(ChangePhoneNumberActivity.this,
						LoginActivity.class));
				finish();

				break;
			case AppConfig.PASSWORDERROR:
				// 验证码不正确或已失效
				String result1 = (String) msg.obj;
				Toast.makeText(getApplicationContext(), result1,
						Toast.LENGTH_LONG).show();
				tv_sendcheckcode.setEnabled(true);
				break;
			case AppConfig.PASSWORDERROR2:
				// 手机号已被使用
				String result2 = (String) msg.obj;
				Toast.makeText(getApplicationContext(), result2,
						Toast.LENGTH_LONG).show();
				tv_sendcheckcode.setEnabled(true);
				break;
			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pc_changephonenumber);

		initView();
	}

	private void initView() {
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_cphone = (TextView) findViewById(R.id.tv_cphone);
		tv_reset = (TextView) findViewById(R.id.tv_reset);
		tv_sendcheckcode = (TextView) findViewById(R.id.tv_sendcheckcode);
		et_telephone = (EditText) findViewById(R.id.et_telephone);
		et_checkcode = (EditText) findViewById(R.id.et_checkcode);
		img_back = findViewById(R.id.img_back);
		
		tv_name.setText("修改密码");
		tv_reset.setEnabled(false);
		setEditChangeInput();

		tv_reset.setOnClickListener(new myOnClick());
		tv_sendcheckcode.setOnClickListener(new myOnClick());
		img_back.setOnClickListener(new myOnClick());
	}

	private void setEditChangeInput() {
		et_telephone.addTextChangedListener(new myTextWatch());
		et_checkcode.addTextChangedListener(new myTextWatch());

	}

	protected class myTextWatch implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@SuppressLint("NewApi")
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (et_telephone.getText().length() > 0
					&& et_checkcode.getText().length() > 0) {
				tv_reset.setAlpha(1.0f);
				tv_reset.setEnabled(true);
			} else {
				tv_reset.setAlpha(0.6f);
				tv_reset.setEnabled(false);
			}

		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

		}

	}

	protected class myOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_reset:
				GetAccessCode();
				break;
			case R.id.tv_sendcheckcode:
				GetCheckCode();
				break;
			case R.id.img_back:
				finish();
				break;

			default:
				break;
			}

		}

	}

	/**
	 * 判断是否可发送验证码
	 * 
	 * @param time
	 */
	private void ChangeSendEnable(int time) {
		if (time < 0) {
			tv_sendcheckcode.setEnabled(true);
			tv_sendcheckcode.setText(getResources().getString(
					R.string.Resend_CheckCode));
			tv_sendcheckcode.setTextColor(getResources().getColor(
					R.color.skyblue));
		} else {
			tv_sendcheckcode.setEnabled(false);
			tv_sendcheckcode.setText(time + "s");
			tv_sendcheckcode.setTextColor(getResources().getColor(
					R.color.newgrey));
		}
	}

	private JSONObject format() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("CheckCode", checkcode);
			jsonObject.put("Role", "2");
			jsonObject.put("Mobile", et_telephone.getText().toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonObject;
	}

	public void GetAccessCode() {
		checkcode = jiami(et_checkcode.getText().toString());

		checkcode = checkcode.replaceAll("(\r\n|\r|\n|\n\r)", "");
		final JSONObject jsonObject = format();

		new ApiTask(new Runnable() {
			@Override
			public void run() {
				try {
					JSONObject responsedata = ConnectService.submitDataByJson(
							AppConfig.URL_PUBLIC + "User/ChangeMobile",
							jsonObject);

					Log.e("jsonObject___save", jsonObject.toString());
					Log.e("responsedata___save", responsedata.toString());
					int retcode = responsedata.getInt("RetCode");
					errorMessage = responsedata.getString("ErrorMessage");
					switch (retcode) {
					case 0:
						handler.sendEmptyMessage(AppConfig.PASSWORDSUCCESS);
						break;
					case -3001:
						Message message = handler.obtainMessage(AppConfig.PASSWORDERROR);
						message.obj = errorMessage;
						message.sendToTarget();
						break;
					case -2004:
						Message message1 = handler.obtainMessage(AppConfig.PASSWORDERROR2);
						message1.obj = errorMessage;
						message1.sendToTarget();
						break;
					default:
						break;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start(ThreadManager.getManager());
	}

	/*
	 * 发送验证码
	 */
	private void GetCheckCode() {
		telephone = et_telephone.getText().toString();
		if (telephone.length() < 1) {
			Toast.makeText(getApplicationContext(), "手机不能为空", Toast.LENGTH_LONG)
					.show();
			return;
		}
		tv_sendcheckcode.setEnabled(false);
		new ApiTask(new Runnable() {
			@Override
			public void run() {
				try {
					JSONObject responsedata = ConnectService
							.submitDataByJsonNoToken(AppConfig.URL_PUBLIC
									+ "CheckCode/Send?mobile=" + telephone,
									null);
					Log.e("CheckCode", responsedata.toString() + "");
					String retcode = responsedata.getString("RetCode");
					String RetData = responsedata.getString("RetData");
					Message msg = new Message();
					if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
						msg.what = AppConfig.GETCHECKCODE;
						// msg.obj = RetData;
					} else {
						msg.what = AppConfig.FAILED;
						String ErrorMessage = responsedata
								.getString("ErrorMessage");
						msg.obj = ErrorMessage;
					}
					handler.sendMessage(msg);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					tv_sendcheckcode.setEnabled(true);
				}
			}
		}).start(ThreadManager.getManager());
	}

	public class CheckCodeEnable implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int time = 59;
			while (time >= -1) {
				try {
					Message message = new Message();
					message.what = AppConfig.CHECKCODE;
					message.obj = time;
					handler.sendMessage(message);
					Thread.sleep(1000);
					time--;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public String jiami(String str) {
		// Log.e("enToStr", str+"加密前");
		// String strBase64 = new String(Base64.encode(str.getBytes(),
		// Base64.DEFAULT));
		String enToStr = Base64.encodeToString(str.getBytes(), Base64.DEFAULT);
		// passwordtrue=enToStr;
		// Log.e("enToStr", enToStr+"加密后");
		return enToStr;
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("ForgetPasswordActivity");
		MobclickAgent.onResume(this); // 统计时长
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("ForgetPasswordActivity");
		MobclickAgent.onPause(this);
	}

}
