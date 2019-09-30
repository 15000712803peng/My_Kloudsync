package com.kloudsync.techexcel.pc.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.ub.techexcel.service.ConnectService;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangePasswordActivity extends Activity {

    private RelativeLayout backLayout;
	private TextView tv_pc_done;
	private EditText pc_ord_password, pc_new_password, pc_confirm_password;
	private String new_password;
	private String new_confirm;
	private String old_password;
	private String old_pd;
	private String new_pd;
    private TextView titleText;
	

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AppConfig.PASSWORDSUCCESS:
				String result = (String) msg.obj;
				
				Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
				finish();
				break;
			case AppConfig.PASSWORDERROR:
				String result1 = (String) msg.obj;
				Toast.makeText(getApplicationContext(), result1, Toast.LENGTH_LONG).show();
				break;
			case AppConfig.PASSWORDERROR2:
				String result2 = (String) msg.obj;
				Toast.makeText(getApplicationContext(), result2, Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pc_changepassword);
		initView();

	}

	private void initView() {
        backLayout = (RelativeLayout) findViewById(R.id.layout_back);
		pc_ord_password = (EditText) findViewById(R.id.pc_ord_password);
		pc_new_password = (EditText) findViewById(R.id.pc_new_password);
		pc_confirm_password = (EditText) findViewById(R.id.pc_confirm_password);
		tv_pc_done = (TextView) findViewById(R.id.tv_pc_done);
        backLayout.setOnClickListener(new myOnClick());
		tv_pc_done.setOnClickListener(new myOnClick());
        titleText = findViewById(R.id.tv_title);
        titleText.setText(R.string.Reset_Password);
		// rl_pc_phonenumber.setOnClickListener(new myOnClick());
		// rl_pc_change_password.setOnClickListener(new myOnClick());

	}

	private void equalJudgment() {
		old_password = pc_ord_password.getText().toString();
		new_password = pc_new_password.getText().toString();
		new_confirm = pc_confirm_password.getText().toString();
		
		if(old_password.length()<6 || new_password.length()<6){
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.password_bigthan6), Toast.LENGTH_SHORT).show();
		}else if (new_password == null || new_password.equals("")) {
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.password_notbe_null), Toast.LENGTH_SHORT).show();
		} else if (new_password.equals(new_confirm)) {
			Log.e("e", new_password+"---------"+new_confirm);
			submitPassword();
		} else {
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.password_noequal), Toast.LENGTH_SHORT).show();
		}
	}

	private void submitPassword() {
		// TODO Auto-generated method stub
		if (new_password != null && 0 < new_password.length()) {
			new_pd = jiami(new_password);
			new_pd = new_pd.replaceAll("(\r\n|\r|\n|\n\r)", "");
		}
		if (old_password != null && 0 < old_password.length()) {
			old_pd = jiami(old_password);
			old_pd = old_pd.replaceAll("(\r\n|\r|\n|\n\r)", "");
		}
		final JSONObject jsonObject = format();
		new ApiTask(new Runnable() {

			@Override
			public void run() {
				try {
					JSONObject responsedata = ConnectService
							.submitDataByJson(AppConfig.URL_PUBLIC
									+ "User/ChangePwd", jsonObject);
					Log.e("jsonObject___save", jsonObject.toString());

					Log.e("responsedata___save", responsedata.toString());

					int retcode = responsedata.getInt("RetCode");
					String errorMessage = responsedata
							.getString("ErrorMessage");
					String retData = responsedata.getString("RetData");
					switch (retcode) {
					case 0:
						Message message1 = handler
								.obtainMessage(AppConfig.PASSWORDSUCCESS);
						retData = getResources().getString(R.string.CP_Success);
						message1.obj = retData;
						message1.sendToTarget();
						break;
					case -2001:
						Message message2 = handler
								.obtainMessage(AppConfig.PASSWORDERROR);
						message2.obj = errorMessage;
						message2.sendToTarget();
						break;
					case -2002:
						Message message3 = handler
								.obtainMessage(AppConfig.PASSWORDERROR2);
						message3.obj = errorMessage;
						message3.sendToTarget();
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

	private JSONObject format() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("OldPassword", old_pd);
			jsonObject.put("Password", new_pd);
			jsonObject.put("Role", 1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonObject;
	}

	private class myOnClick implements OnClickListener {
		Intent intent = new Intent();

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
                case R.id.layout_back:
				finish();
				break;

			case R.id.tv_pc_done:
				equalJudgment();
				break;

			default:
				break;
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
		MobclickAgent.onPageStart("ChangePasswordActivity");
		MobclickAgent.onResume(this); // 统计时长
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("ChangePasswordActivity");
		MobclickAgent.onPause(this);
	}
}
