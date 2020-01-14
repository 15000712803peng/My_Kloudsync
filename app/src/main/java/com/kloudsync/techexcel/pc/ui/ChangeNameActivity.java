package com.kloudsync.techexcel.pc.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.info.CustomerYu;
import com.ub.techexcel.service.ConnectService;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangeNameActivity extends Activity {

	private ImageView tv_back;
	private TextView tv_name;
	private TextView tv_pc_done;
	private EditText pc_fist_name, pc_middle_name, pc_last_name;
    private LinearLayout pi_ll_mname;

	private SharedPreferences sharedPreferences;
	private  int language;
	private String FirstName;
	private String MiddleName;
	private String LastName;
	private String FullName;

	private CustomerYu customerYu;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pc_changename);
		//通过intent获取customerYu
		customerYu = (CustomerYu) getIntent().getSerializableExtra("customerYu");
		initView();


	}

	private void initView() {
		tv_back = (ImageView) findViewById(R.id.tv_back);
		tv_name = (TextView) findViewById(R.id.tv_name);

		pc_fist_name = (EditText) findViewById(R.id.pc_fist_name);
		pc_middle_name = (EditText) findViewById(R.id.pc_middle_name);
		pc_last_name = (EditText) findViewById(R.id.pc_last_name);
        pi_ll_mname = (LinearLayout) findViewById(R.id.pi_ll_mname);

		pc_fist_name.setText(customerYu.getFirstName());
		pc_middle_name.setText(customerYu.getMiddleName());
		pc_last_name.setText(customerYu.getLastName());

		tv_pc_done = (TextView) findViewById(R.id.tv_pc_done);

		tv_name.setText(getResources().getString(R.string.change_name));

		//当前语言
		sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
				MODE_PRIVATE);
		language = sharedPreferences.getInt("language",1);

		ChangeNameLanguage();

		tv_back.setOnClickListener(new myOnClick());
		tv_pc_done.setOnClickListener(new myOnClick());

	}

	private void ChangeNameLanguage() {
		if(language == 1){
			//英文

		}else {
			// 中文
            pi_ll_mname.setVisibility(View.GONE);
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
					FirstName = pc_fist_name.getText().toString();
					MiddleName = pc_middle_name.getText().toString();
					LastName = pc_last_name.getText().toString();

					FullName = FirstName+MiddleName+LastName+"";

					Log.e("老余/Choices",FullName + "");


					if(FirstName == null){
						customerYu.setFirstName("");
					}else{
						customerYu.setFirstName(FirstName);
					}
					if(MiddleName == null){
						customerYu.setMiddleName("");
					}else{
						customerYu.setMiddleName(MiddleName);
					}
					if(LastName == null){
						customerYu.setLastName("");
					}else{
						customerYu.setLastName(LastName);
					}
					customerYu.setFullName(FullName);


					//创建Intent对象
					Intent intent = new Intent();
					//将求和的结果放进intent中
					intent.putExtra("result", customerYu);
					//返回结果
					setResult(0x001,intent);
					//关闭当前界面
					finish();

					break;

				default:
					break;
			}

		}

	}
}
