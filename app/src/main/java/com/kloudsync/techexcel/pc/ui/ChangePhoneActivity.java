package com.kloudsync.techexcel.pc.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;

public class ChangePhoneActivity extends Activity {

	private ImageView tv_back;
	private TextView tv_name;
	private TextView tv_pc_done;
	private EditText pc_fist_name, pc_middle_name, pc_last_name;
	private SharedPreferences sharedPreferences;
	private  int language;
	private String new_password;
	private String new_confirm;
	private String old_password;
	private String old_pd;
	private String new_pd;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pc_changephone);
		Toast.makeText(ChangePhoneActivity.this, "此模块还在开发中", Toast.LENGTH_LONG).show();
		initView();

	}

	private void initView() {
		tv_back = (ImageView) findViewById(R.id.tv_back);
		tv_name = (TextView) findViewById(R.id.tv_name);

		pc_fist_name = (EditText) findViewById(R.id.pc_ord_password);
		pc_middle_name = (EditText) findViewById(R.id.pc_new_password);
		pc_last_name = (EditText) findViewById(R.id.pc_confirm_password);

		tv_pc_done = (TextView) findViewById(R.id.tv_pc_done);

		tv_name.setText(getResources().getString(R.string.change_phone));

		sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
				MODE_PRIVATE);
		language = sharedPreferences.getInt("language",1);

		ChangeNameLanguage();

		tv_back.setOnClickListener(new ChangePhoneActivity.myOnClick());
		tv_pc_done.setOnClickListener(new ChangePhoneActivity.myOnClick());

	}

	private void ChangeNameLanguage() {
		if(language == 1){
			//英文

		}else {
			// 中文

		}

		// startActivity(new Intent(Intent.ACTION_VIEW,uri));
	}

	private class myOnClick implements OnClickListener {
		Intent intent = new Intent();

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
				case R.id.tv_back:
					finish();
					break;

				case R.id.tv_pc_done:
					break;

				default:
					break;
			}

		}

	}
}
