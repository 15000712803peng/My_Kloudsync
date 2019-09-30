package com.kloudsync.techexcel.pc.ui;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.umeng.analytics.MobclickAgent;

public class EffectiveActivity extends Activity {

	private TextView tv_back;
	private TextView tv_name,tv_pc_expirationDate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pc_effective);
		initView();
		
	}

	private void initView() {
		tv_back = (TextView) findViewById(R.id.tv_back);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_pc_expirationDate = (TextView) findViewById(R.id.tv_pc_expirationDate);
		tv_name.setText(R.string.effective_title);
		tv_pc_expirationDate.setText(getIntent().getStringExtra("effective"));

		tv_back.setOnClickListener(new myOnClick());
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
			default:
				break;
			}

		}
		
	}
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("EffectiveActivity"); 
	    MobclickAgent.onResume(this);       //统计时长
	}
	public void onPause() {
	    super.onPause();
        MobclickAgent.onPageEnd("EffectiveActivity");
	    MobclickAgent.onPause(this);
	}
}
