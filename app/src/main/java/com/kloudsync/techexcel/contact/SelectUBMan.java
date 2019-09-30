package com.kloudsync.techexcel.contact;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;

public class SelectUBMan extends Activity {

	private LinearLayout lin_sys, lin_own;
	private ImageView img_selected1, img_selected2;
	private TextView tv_back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selectubman);
		initView();
	}

	private void initView() {
		lin_sys = (LinearLayout) findViewById(R.id.lin_sys);
		lin_own = (LinearLayout) findViewById(R.id.lin_own);
		img_selected1 = (ImageView) findViewById(R.id.img_selected1);
		img_selected2 = (ImageView) findViewById(R.id.img_selected2);
		tv_back = (TextView) findViewById(R.id.tv_back);
		
		GetSysOwn(AppConfig.UbaoMan);
		
		lin_sys.setOnClickListener(new myOnClick());
		lin_own.setOnClickListener(new myOnClick());
		tv_back.setOnClickListener(new myOnClick());
		
	}

	protected class myOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.lin_sys:
				GetSysOwn(0);
				finish();
				break;
			case R.id.lin_own:
				GetSysOwn(1);
				finish();
				break;
			case R.id.tv_back:
				finish();
				break;

			default:
				break;
			}

		}

	}
	

	private void GetSysOwn(int i) {
		AppConfig.UbaoMan = i;
		switch (i) {
		case 0:
			img_selected1.setVisibility(View.VISIBLE);
			img_selected2.setVisibility(View.GONE);
			break;
		case 1:
			img_selected1.setVisibility(View.GONE);
			img_selected2.setVisibility(View.VISIBLE);
			break;

		default:
			break;
		}
		
	}

}
