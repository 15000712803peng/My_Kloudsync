package com.kloudsync.techexcel.pc.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.pc.adapter.PublishedArticleAdapter;
import com.kloudsync.techexcel.pc.bean.PublishedArticleDetails;
import com.ub.techexcel.service.ConnectService;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PublishedArticleActivity extends Activity {

	private ListView lv_pc_published_article;
	private TextView tv_back;
	private TextView tv_name;

	private ViewPager viewPager;// 页卡内容
	private ImageView imageView;// 动画图片
	private TextView textView1, textView2;
	private List<View> views;// Tab页面列表
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度
	private View view1, view2, view3;// 各个页卡
	private List<PublishedArticleDetails> 	pArticleList;
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AppConfig.PASSWORDSUCCESS:
				//Log.e("size", pArticleList.size()+"yy");
				PublishedArticleAdapter adapter = new PublishedArticleAdapter(getApplicationContext(),
						pArticleList);
				lv_pc_published_article.setAdapter(adapter);
				break;
			default:
				break;
			}
		};
	};

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pc_published_article);
		initView();
		InitImageView();
		InitViewPager();
		getPublishedArticle();
		
/*		List<String> list = new ArrayList<String>();
		list.add("");
		list.add("");
		list.add("");
		list.add("");
		list.add("");
		list.add("");
		
		PublishedArticleAdapter adapter = new PublishedArticleAdapter(this,
				list);
		lv_pc_published_article.setAdapter(adapter);*/
	}

	private void getPublishedArticle() {
		// TODO Auto-generated method stub
        new ApiTask(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				JSONObject jsonObject = ConnectService
						.getIncidentbyHttpGet(AppConfig.URL_PUBLIC + "User/UserArticleList?PageSize=20&PageIndex=0");
				Log.e("dk", jsonObject.toString());
				formatIntegral(jsonObject);
			}

        }).start(ThreadManager.getManager());
	}
	
	private void formatIntegral(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		try {
			int retCode = jsonObject.getInt("RetCode");
			String error=jsonObject.getString("ErrorMessage");
			JSONArray  dIntegral = jsonObject.getJSONObject("RetData").getJSONArray("Articles");
			switch (retCode) {
			case 0:
				pArticleList = new ArrayList<PublishedArticleDetails>();
				for (int i = 0; i < dIntegral.length(); i++) {
					PublishedArticleDetails pArticleDetails = new PublishedArticleDetails();
					String issueTitle = dIntegral.getJSONObject(i).getString("IssueTitle");
					String description = dIntegral.getJSONObject(i).getString("Description");
					
					pArticleDetails.setIssueTitle(issueTitle);
					pArticleDetails.setDescription(description);
					
					
					pArticleList.add(pArticleDetails);
				}
				Message message1 = handler
						.obtainMessage(AppConfig.PASSWORDSUCCESS);
				message1.sendToTarget();
				break;
			case -1500:
				Toast.makeText(getApplicationContext(), error, 100).show();
				break;
			case -1401:
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
	
	private void InitViewPager() {
		viewPager = (ViewPager) findViewById(R.id.vPager);
		views = new ArrayList<View>();
		LayoutInflater inflater = getLayoutInflater();
		view1 = inflater.inflate(R.layout.pc_published_article_one, null);
		view2 = inflater.inflate(R.layout.pc_published_article_two, null);
		lv_pc_published_article = (ListView) view1.findViewById(R.id.lv_pc_published_article);
		
		
		views.add(view1);
		views.add(view2);
		viewPager.setAdapter(new MyViewPagerAdapter(views));
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	private void initView() {
		
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_back = (TextView) findViewById(R.id.tv_back);

		textView1 = (TextView) findViewById(R.id.text1);
		textView2 = (TextView) findViewById(R.id.text2);

		tv_name.setText(R.string.published_article_title);

		tv_back.setOnClickListener(new myOnClick());

		textView1.setOnClickListener(new MyOnClickListener(0));
		textView2.setOnClickListener(new MyOnClickListener(1));
	}

	/**
	 * 2 * 初始化动画，这个就是页卡滑动时，下面的横线也滑动的效果，在这里需要计算一些数据 3
	 */

	private void InitImageView() {
		imageView = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(),
				R.drawable.pc_article).getWidth();// 获取图片宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		offset = (screenW / 2 - bmpW) / 2;// 计算偏移量
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		imageView.setImageMatrix(matrix);// 设置动画初始位置
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

	/**
	 * 
	 * 头标点击监听 3
	 */
	private class MyOnClickListener implements OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		public void onClick(View v) {
			viewPager.setCurrentItem(index);
		}

	}

	public class MyViewPagerAdapter extends PagerAdapter {
		private List<View> mListViews;

		public MyViewPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mListViews.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(mListViews.get(position), 0);
			return mListViews.get(position);
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
		int two = one * 2;// 页卡1 -> 页卡3 偏移量

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		public void onPageSelected(int arg0) {
			Animation animation = new TranslateAnimation(one * currIndex, one
					* arg0, 0, 0);// 显然这个比较简洁，只有一行代码。
			currIndex = arg0;
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(300);
			imageView.startAnimation(animation);
			/*Toast.makeText(PublishedArticleActivity.this,
					"您选择了" + viewPager.getCurrentItem() + "页卡",
					Toast.LENGTH_SHORT).show();*/
		}

	}
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("PublishedArticleActivity"); 
	    MobclickAgent.onResume(this);       //统计时长
	}
	public void onPause() {
	    super.onPause();
        MobclickAgent.onPageEnd("PublishedArticleActivity");
	    MobclickAgent.onPause(this);
	}
}
