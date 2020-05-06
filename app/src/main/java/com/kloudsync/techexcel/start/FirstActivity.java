package com.kloudsync.techexcel.start;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

public class FirstActivity extends Activity {

	private ImageView img_dot_one,img_dot_two,img_dot_three;
	private ViewPager vp_start;
	private ArrayList<View> views;// Tab页面列表

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_startfirst);

		initView();
		initViewPager();
		GetMyPermission();
	}

	private void GetMyPermission() {
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED
				|| ActivityCompat.checkSelfPermission(this, Manifest.permission.LOCATION_HARDWARE)
				!= PackageManager.PERMISSION_GRANTED
				|| ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED
				|| ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
				!= PackageManager.PERMISSION_GRANTED
				|| ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
				!= PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
					Manifest.permission.LOCATION_HARDWARE, Manifest.permission.READ_EXTERNAL_STORAGE,
					Manifest.permission.ACCESS_FINE_LOCATION,
					Manifest.permission.READ_PHONE_STATE,
					Manifest.permission.CALL_PHONE,
					Manifest.permission.CAMERA,
					Manifest.permission.RECORD_AUDIO}, 0x0010);
		}

	}


	private void initViewPager() {
		vp_start = (ViewPager) findViewById(R.id.vp_start);
		views = new ArrayList<View>();
		LayoutInflater inflater = getLayoutInflater();

		views.add(inflater.inflate(R.layout.start_page1, null));
		views.add(inflater.inflate(R.layout.start_page2, null));
		views.add(inflater.inflate(R.layout.start_page3, null));
		
		vp_start.setAdapter(new MyViewPager(views));
		vp_start.setCurrentItem(0);
		vp_start.addOnPageChangeListener(new MyOnPagerListener());
	}
	
	
	class MyViewPager extends PagerAdapter {
		public ArrayList<View> list;

		public MyViewPager(ArrayList<View> list) {
			this.list = list;
		}
		
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(list.get(position), 0);
			
			TextView tv_jump = (TextView) list.get(position).findViewById(R.id.tv_jump);
			tv_jump.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(getApplicationContext(), StartKloudActivity.class);
					startActivity(intent);
					finish();
				}
			});
			
			return list.get(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(list.get(position));
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}
	
	class MyOnPagerListener implements OnPageChangeListener {
		
		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int arg0) {
			img_dot_one.setImageDrawable(getResources().
					getDrawable(R.drawable.icon_noselected));
			img_dot_two.setImageDrawable(getResources().
					getDrawable(R.drawable.icon_noselected));
			img_dot_three.setImageDrawable(getResources().
					getDrawable(R.drawable.icon_noselected));
			
			switch (arg0) {
			case 0:
				img_dot_one.setImageDrawable(getResources().
						getDrawable(R.drawable.icon_seleced));
				break;
			case 1:
				img_dot_two.setImageDrawable(getResources().
						getDrawable(R.drawable.icon_seleced));
				break;
			case 2:
				img_dot_three.setImageDrawable(getResources().
						getDrawable(R.drawable.icon_seleced));
				break;

			default:
				break;
			}
			
		}
	}

	private void initView() {
		img_dot_one = (ImageView) findViewById(R.id.img_dot_one);
		img_dot_two = (ImageView) findViewById(R.id.img_dot_two);
		img_dot_three = (ImageView) findViewById(R.id.img_dot_three);
	}
	
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("FirstActivity"); 
	    MobclickAgent.onResume(this);       //统计时长
	}
	public void onPause() {
	    super.onPause();
        MobclickAgent.onPageEnd("FirstActivity");
	    MobclickAgent.onPause(this);
	}
}
