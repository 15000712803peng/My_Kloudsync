package com.kloudsync.techexcel.personal;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.transition.Fade;
import android.transition.Transition;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.SearchSelectAdapter;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.start.StartUbao;
import com.kloudsync.techexcel.ui.MainActivity;

import java.util.ArrayList;
import java.util.Locale;


public class LanguageActivity extends Activity {

	private TextView tv_name;
    private RelativeLayout backLayout;
	private ListView lst_language;	

//	private Class ActivityClass;
	
	private SearchSelectAdapter mAdapter;
	private ArrayList<String> mList = new ArrayList<String>();

	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor editor;
	
	private int lan_select = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.language);
//		ActivityClass = ActivitySlideMenu.instance.getClass();
		initView();
		initSlideTrans();
	}

	private void initView() {
        tv_name = (TextView) findViewById(R.id.tv_title);
        backLayout = (RelativeLayout) findViewById(R.id.layout_back);
		lst_language = (ListView) findViewById(R.id.lst_language);
		tv_name.setText(getResources().getString(R.string.language2));
		sharedPreferences = getSharedPreferences(AppConfig.LOGININFO,
				MODE_PRIVATE);

		editor = sharedPreferences.edit();
		getLanguage();
        backLayout.setOnClickListener(new myOnClick());
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private void initSlideTrans() {
		Transition transition = buildEnterTransition();
		getWindow().setEnterTransition(transition);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private Transition buildEnterTransition() {
		Fade enterTransition = new Fade();
		enterTransition.setDuration(500);
		return enterTransition;
	}

	private void getLanguage() {
		switch (AppConfig.LANGUAGEID) {
		case 1:
			lan_select = 0;
			break;
		case 2:
			lan_select = 1;
			break;	
		default:
			break;
		}
		mList.add(getResources().getString(R.string.English));
		mList.add(getResources().getString(R.string.Chinese));
//		DisplayMetrics dm = new DisplayMetrics();
//		getWindowManager().getDefaultDisplay().getMetrics(dm);
//		final float density = dm.density;
//
//		LayoutParams params = (LayoutParams) lst_language.getLayoutParams();
//		params.height = (int) (mList.size() * 51 * density);
		
		mAdapter = new SearchSelectAdapter(getApplicationContext(), mList, lan_select);
		lst_language.setAdapter(mAdapter);
		lst_language.setOnItemClickListener(new MyOnitem());
		
	}
	
	private void RefreshLanguage(){
		tv_name.setText(getResources().getString(R.string.language));
	}
	
	private class MyOnitem implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			switch (position) {
			case 0:
				updateLange(Locale.ENGLISH, 1);
				break;
			case 1:
				updateLange(Locale.SIMPLIFIED_CHINESE, 2);
				break;	
			default:
				break;
			}
			mAdapter.getPosition(position);
			
		}
		
	}
	
	private void updateLange(Locale locale, int language) {
		editor.putInt("language", language);
		Log.e("老余language", language + "");
		editor.commit();
		if(AppConfig.LANGUAGEID != language){
			AppConfig.LANGUAGEID = language;
			StartUbao.updateLange(this,locale);
			MainActivity.RESUME = true;
		}
		RefreshLanguage();
//		Intent intent = new Intent();
//		intent.setAction("com.kloudsync.techexcel.appname");
//		sendBroadcast(intent);
		FinishActivity();

	}

	protected class myOnClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
                case R.id.layout_back:
				FinishActivity();
				break;

			default:
				break;
			}
			
		}
		
	}

	private void FinishActivity() {
		ActivityCompat.finishAfterTransition(LanguageActivity.this);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			FinishActivity();
			return true;

		}
		return super.onKeyDown(keyCode, event);
	}
}
