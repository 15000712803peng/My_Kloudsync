package com.kloudsync.techexcel.pc.ui;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.info.CustomerYu;
import com.kloudsync.techexcel.view.ClearEditText;
public class ChangeNameActivity extends Activity {

	private ImageView tv_back;
	private TextView tv_name,tv_cancel;
	private TextView tv_pc_done;
	private ClearEditText pc_fist_name;

	private SharedPreferences sharedPreferences;
	private  int language;
	private String FirstName;

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
		tv_cancel = (TextView) findViewById(R.id.tv_cancel);

		pc_fist_name = (ClearEditText) findViewById(R.id.pc_fist_name);

		pc_fist_name.setText(customerYu.getFullName());

		tv_pc_done = (TextView) findViewById(R.id.tv_pc_done);

		tv_name.setText(getResources().getString(R.string.change_name));

		//当前语言
		sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
				MODE_PRIVATE);
		language = sharedPreferences.getInt("language",1);

		tv_back.setOnClickListener(new myOnClick());
		tv_pc_done.setOnClickListener(new myOnClick());
		tv_cancel.setOnClickListener(new myOnClick());
		pc_fist_name.addTextChangedListener(new MyTextWatch());

	}

	protected class MyTextWatch implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
									  int after) {
			// TODO Auto-generated method stub

		}

		@SuppressLint("NewApi")
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
								  int count) {
			if (pc_fist_name.getText().length() > 0
					&& !pc_fist_name.getText().toString().equals(FullName)) {
				tv_pc_done.setAlpha(1.0f);
				tv_pc_done.setEnabled(true);
			} else {
				tv_pc_done.setAlpha(0.6f);
				tv_pc_done.setEnabled(false);
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

		}
	}
	private class myOnClick implements OnClickListener {
		Intent intent = new Intent();

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
				case R.id.tv_cancel:
					finish();
					break;

				case R.id.tv_pc_done:
					FirstName = pc_fist_name.getText().toString();
					FullName = FirstName;

					Log.e("老余/Choices",FullName + "");


					if(FirstName == null){
						customerYu.setFirstName("");
					}else{
						customerYu.setFirstName(FirstName);
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
