package com.kloudsync.techexcel.pc.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.start.LoginActivity;
import com.kloudsync.techexcel.ui.MainActivity;
import com.ub.techexcel.service.ConnectService;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

public class SettingActivity extends Activity {

	private LinearLayout img_back;
	private TextView tv_name;
	private RelativeLayout rl_pc_account, rl_pc_protocol, rl_pc_about,
			rl_pc_help_feedback, rl_pc_go_evaluation, rl_pc_clear_buffer,
			rl_pc_log_off;
	private SharedPreferences sharedPreferences;
	private Intent intent;
	public static SettingActivity settingActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		settingActivity = this;
		setContentView(R.layout.pc_setting);
		initView();

	}

	private void initView() {
		img_back = (LinearLayout) findViewById(R.id.img_back);
		tv_name = (TextView) findViewById(R.id.tv_name);

		rl_pc_account = (RelativeLayout) findViewById(R.id.rl_pc_account);
		rl_pc_protocol = (RelativeLayout) findViewById(R.id.rl_pc_protocol);
		rl_pc_about = (RelativeLayout) findViewById(R.id.rl_pc_about);
		rl_pc_help_feedback = (RelativeLayout) findViewById(R.id.rl_pc_help_feedback);
		rl_pc_go_evaluation = (RelativeLayout) findViewById(R.id.rl_pc_go_evaluation);
		rl_pc_clear_buffer = (RelativeLayout) findViewById(R.id.rl_pc_clear_buffer);
		rl_pc_log_off = (RelativeLayout) findViewById(R.id.rl_pc_log_off);

		tv_name.setText(R.string.setting_title);
		
		rl_pc_go_evaluation.setVisibility(View.GONE);
		rl_pc_protocol.setVisibility(View.GONE);
		rl_pc_help_feedback.setVisibility(View.GONE);
		rl_pc_clear_buffer.setVisibility(View.GONE);

		img_back.setOnClickListener(new myOnClick());
		rl_pc_account.setOnClickListener(new myOnClick());
		rl_pc_protocol.setOnClickListener(new myOnClick());
		rl_pc_about.setOnClickListener(new myOnClick());
		rl_pc_help_feedback.setOnClickListener(new myOnClick());
		rl_pc_go_evaluation.setOnClickListener(new myOnClick());
		rl_pc_clear_buffer.setOnClickListener(new myOnClick());
		rl_pc_log_off.setOnClickListener(new myOnClick());
	}

	private class myOnClick implements OnClickListener {
		Intent intent = new Intent();

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.img_back:
				finish();
				break;
			case R.id.rl_pc_account:
				intent = new Intent(SettingActivity.this, AccountActivity.class);
				startActivity(intent);
				break;
			case R.id.rl_pc_protocol:

				break;
			case R.id.rl_pc_about:
				intent = new Intent(SettingActivity.this, AboutActivity.class);
				startActivity(intent);
				break;
			case R.id.rl_pc_help_feedback:
					
				break;
			case R.id.rl_pc_go_evaluation:

				break;
			case R.id.rl_pc_clear_buffer:

				break;
			case R.id.rl_pc_log_off:
				logout();
				break;
			default:
				break;
			}

		}
	}

	private void logout() {
		// TODO Auto-generated method stub
		new ApiTask(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				JSONObject jsonObject = ConnectService
						.getIncidentbyHttpGet(AppConfig.URL_PUBLIC + "Logout");
				Log.e("dk", jsonObject.toString());
				formatlogout(jsonObject);
			}

		}).start(((App) getApplication()).getThreadMgr());
	}

	private void formatlogout(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		try {
			int retCode = jsonObject.getInt("RetCode");
			String error=jsonObject.getString("ErrorMessage");
			switch (retCode) {
			case 0:
				sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
					      MODE_PRIVATE);
					    SharedPreferences.Editor editor = sharedPreferences.edit();
					    editor.putBoolean("isLogIn", false);
					    editor.commit();
					    intent = new Intent(SettingActivity.this, LoginActivity.class);
					    startActivity(intent);
					    finish();
					    MainActivity.instance.finish();
				break;
			case -1500:
				Toast.makeText(getApplicationContext(), error, 100).show();
				break;
			default:
				break;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("SettingActivity"); 
	    MobclickAgent.onResume(this);       //统计时长
	}
	public void onPause() {
	    super.onPause();
        MobclickAgent.onPageEnd("SettingActivity");
	    MobclickAgent.onPause(this);
	}
}
