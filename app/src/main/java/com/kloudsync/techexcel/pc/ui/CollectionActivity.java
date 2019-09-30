package com.kloudsync.techexcel.pc.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.pc.adapter.CollectionAdapter;
import com.umeng.analytics.MobclickAgent;

public class CollectionActivity extends Activity {

	private ListView lv_pc_published_article;
	private LinearLayout img_back;
	private TextView tv_name;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pc_search_list);
		initView();
		List<String> list=new ArrayList<String>();
		list.add("");
		list.add("");
		list.add("");
		list.add("");
		list.add("");
		list.add("");
		CollectionAdapter adapter=new CollectionAdapter(this,list);
		lv_pc_published_article.setAdapter(adapter);
	}

	private void initView() {
		lv_pc_published_article = (ListView) findViewById(R.id.lv_pc_published_article);
		tv_name = (TextView) findViewById(R.id.tv_name);
		img_back = (LinearLayout) findViewById(R.id.img_back);
		
		tv_name.setText(R.string.fauorites_title);
		
		img_back.setOnClickListener(new myOnClick());
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
			default:
				break;
			}

		}
		
	}
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("CollectionActivity"); 
	    MobclickAgent.onResume(this);       //统计时长
	}
	public void onPause() {
	    super.onPause();
        MobclickAgent.onPageEnd("CollectionActivity");
	    MobclickAgent.onPause(this);
	}
}
