package com.kloudsync.techexcel.pc.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.umeng.analytics.MobclickAgent;

public class AccountActivity extends Activity {

	private LinearLayout img_back;
	private TextView tv_name;
	private RelativeLayout rl_pc_phonenumber, rl_pc_change_password;
	public static AccountActivity accountActivity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		accountActivity = this;
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pc_account);
		initView();
	}

	private void initView() {
		img_back = (LinearLayout) findViewById(R.id.img_back);
		tv_name = (TextView) findViewById(R.id.tv_name);

		rl_pc_phonenumber = (RelativeLayout) findViewById(R.id.rl_pc_phonenumber);
		rl_pc_change_password = (RelativeLayout) findViewById(R.id.rl_pc_change_password);

		tv_name.setText(R.string.account_title);

		img_back.setOnClickListener(new myOnClick());
		rl_pc_phonenumber.setOnClickListener(new myOnClick());
		rl_pc_change_password.setOnClickListener(new myOnClick());

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
			case R.id.rl_pc_phonenumber:
				intent = new Intent(AccountActivity.this,
						ChangePhoneNumberActivity.class);
				startActivity(intent);
				/*finish();
				SettingActivity.settingActivity.finish();*/
				break;
			case R.id.rl_pc_change_password:
				intent = new Intent(AccountActivity.this,
						ChangePasswordActivity.class);
				startActivity(intent);
				/*finish();
				SettingActivity.settingActivity.finish();*/
				break;
			default:
				break;
			}

		}

		
	}
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("AccountActivity"); 
	    MobclickAgent.onResume(this);       //统计时长
	}
	public void onPause() {
	    super.onPause();
        MobclickAgent.onPageEnd("AccountActivity");
	    MobclickAgent.onPause(this);
	}
}
