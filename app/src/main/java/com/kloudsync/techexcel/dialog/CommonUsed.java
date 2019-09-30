package com.kloudsync.techexcel.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.CommonUsedAdapter;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.info.CommonUse;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.start.LoginGet.DialogGetListener;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

public class CommonUsed extends Activity {

	private List<TextView> tvs = new ArrayList<TextView>();
	private TextView tv_back, tv_set;
	private ListView lv_mused, lv_pop;
	private LinearLayout sv_useful;
	private FrameLayout fl_pop;

	private int flag_se = -1;
	private int largeId = -1;
	private int smallId = -1;
	private int focusId = -1;
	private int tvIDs[] = { R.id.tv_all_l, R.id.tv_all_s, R.id.tv_all_f };
	private ArrayList<CommonUse> main = new ArrayList<CommonUse>();
	private ArrayList<CommonUse> l_list = new ArrayList<CommonUse>();
	private ArrayList<CommonUse> s_list = new ArrayList<CommonUse>();
	private ArrayList<CommonUse> f_list = new ArrayList<CommonUse>();
	private ArrayList<CommonUse> show_list = new ArrayList<CommonUse>();

	private ArrayList<CommonUse> default_list = new ArrayList<CommonUse>();
	
	private CommonUsedAdapter cAdapter;
	private CommonUsedAdapter ShowAdapter;
	
	float density;
	
	LoginGet loginget = new LoginGet();

	private String[] deShow = { "您好，请问有什么可以帮您？", "您最近身体好点了么吗？",
			"今天天气不错，有没有出去运动一下呢？", "每个人的身体情况是不一样的，要做具体分析。",
			"请您稍等，稍后我会给您制定一份健康方案。", "谢谢您对我的肯定，这是应该做的！", "请按时服药，定期检查。" };

	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor editor;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_commonused);

		getId();
		initView();
		
	}

	private void getId() {
		// TODO Auto-generated method stub
		sharedPreferences = getSharedPreferences(AppConfig.COMMONUSEDINFO,
				MODE_PRIVATE);
		editor = sharedPreferences.edit();

		largeId = sharedPreferences.getInt("largeId", -1);
		smallId = sharedPreferences.getInt("smallId", -1);
		focusId = sharedPreferences.getInt("focusId", -1);
	}

	private void initView() {
		tv_back = (TextView) findViewById(R.id.tv_back);
		tv_set = (TextView) findViewById(R.id.tv_set);
		lv_mused = (ListView) findViewById(R.id.lv_mused);
//		lv_greet = (ListView) findViewById(R.id.lv_greet);
		lv_pop = (ListView) findViewById(R.id.lv_pop);
		sv_useful = (LinearLayout) findViewById(R.id.sv_useful);
		fl_pop = (FrameLayout) findViewById(R.id.fl_pop);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		density = dm.density;
		for (int i = 0; i < tvIDs.length; i++) {
			TextView tv = (TextView) findViewById(tvIDs[i]);
			
			Drawable d;
			tv.setTextColor(getResources().getColor(R.color.darkgrey));
			d = getResources().getDrawable(R.drawable.select_d2);
			d.setBounds(0, 0, (int) (6 * density), (int) (6 * density));
			tv.setCompoundDrawables(null, null, d, null);
			tv.setOnClickListener(new myOnClick());
			tvs.add(tv);
		}
		tv_back.setOnClickListener(new myOnClick());
		tv_set.setOnClickListener(new myOnClick());

		
		showDefault();
		
		GetConcernHierarchy();
		Log.e("haha1", default_list.size() + "");
	}

	private void showDefault() {
		
		default_list = new ArrayList<CommonUse>();
		for (int i = 0; i < deShow.length; i++) {
			CommonUse cu = new CommonUse();
			cu.setName(deShow[i]);
			cu.setID(i);
			default_list.add(cu);
		}
		showMused();
	}

	private void showMused() {
		/*LayoutParams params = (LayoutParams) lv_mused.getLayoutParams();
		params.height = (int) (default_list.size() * 36 * density);*/
		
		ShowAdapter = new CommonUsedAdapter(getApplicationContext(), default_list);
		lv_mused.setAdapter(ShowAdapter);
		lv_mused.setOnItemClickListener(new MyShowItem());
	}

	private void GetConcernHierarchy() {
		loginget = new LoginGet();
		loginget.setDialogGetListener(new DialogGetListener() {
			
			@Override
			public void getUseful(ArrayList<CommonUse> list) {
				// TODO Auto-generated method stub

				if(-1 != largeId && -1 != smallId && -1 != smallId){
					default_list = new ArrayList<CommonUse>();
					default_list.addAll(list);
					showMused();
				}
			}
			
			@Override
			public void getCH(ArrayList<CommonUse> list) {
				main = new ArrayList<CommonUse>();
				main.addAll(list);
				FilterList();
				
				getUsefuls(); 
			}
		});
		loginget.ConcernHierarchyRequest(CommonUsed.this);
	}

	protected void FilterList() {
		l_list = new ArrayList<CommonUse>();
		s_list = new ArrayList<CommonUse>();
		f_list = new ArrayList<CommonUse>();
		for (int i = 0; i < main.size(); i++) {
			CommonUse cu = main.get(i);
			switch (cu.getNodeType()) {
			case 0:
				l_list.add(cu);
				break;
			case 1:
				s_list.add(cu);
				break;
			case 2:
				f_list.add(cu);
				break;

			default:
				break;
			}
		}
		
	}

	protected class myOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_all_l:
				GoTOSentence(0);
				break;
			case R.id.tv_all_s:
				GoTOSentence(1);
				break;
			case R.id.tv_all_f:
				GoTOSentence(2);
				break;
			case R.id.tv_back:
				finish();
				break;
			case R.id.tv_set:
				Toast.makeText(getApplicationContext(), "设置", Toast.LENGTH_SHORT).show();
				break;
				
			default:
				break;
			}
		}

	}

	@SuppressLint("NewApi") 
	public void GoTOSentence(int s) {
		for (int i = 0; i < tvs.size(); i++) {
			Drawable d;
			if (i == s && s != flag_se) {
				tvs.get(s).setTextColor(getResources().getColor(R.color.green));
				d = getResources().getDrawable(R.drawable.select_c2);
			} else {
				tvs.get(i).setTextColor(
						getResources().getColor(R.color.darkgrey));
				d = getResources().getDrawable(R.drawable.select_d2);
			}
			d.setBounds(0, 0, (int) (6 * density), (int) (6 * density)); // 必须设置图片大小，否则不显示
			tvs.get(i).setCompoundDrawables(null, null, d, null);
		}
		
		if(s == flag_se){
			ClosePop();
			flag_se = -1;
		}else{
			flag_se = s;
			ShowPop();			
		}
		showTopList(s);		

	}

	private void showTopList(int s) {
		CommonUse cu = new CommonUse();
		show_list = new ArrayList<CommonUse>();
		
		switch (s) {
		case 0:
			show_list = l_list;
			cAdapter = new CommonUsedAdapter(getApplicationContext(), show_list);
			cAdapter.SelectedItem(largeId);
			break;
		case 1:
			GetsmallList(cu);
			cAdapter = new CommonUsedAdapter(getApplicationContext(), show_list);
			cAdapter.SelectedItem(smallId);
			
			break;
		case 2:
			GetFocusList(cu);
			cAdapter = new CommonUsedAdapter(getApplicationContext(), show_list);
			cAdapter.SelectedItem(focusId);
			
			break;

		default:
			break;
		}
		lv_pop.setAdapter(cAdapter);
		lv_pop.setOnItemClickListener(new MyItem());
	}
	
	private class MyShowItem implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Log.e("haha2", default_list.size() + "");
			CommonUse cu = default_list.get(position);
			ShowAdapter.updateListView(default_list, position);
			AppConfig.SEND_SENTENCE = cu.getName();
			AppConfig.isSend = true;
			finish();
			
			
		}

		
	}
	
	private class MyItem implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			CommonUse cu = show_list.get(position);
			int ID = cu.getID();
			cAdapter.updateListView(show_list, ID);
			switch (flag_se) {
			case 0:
				largeId = ID;
				GoTOSentence(1);
				break;
			case 1:
				smallId = ID;
				GoTOSentence(2);
				break;
			case 2:
				focusId = ID;
				ClosePop();
				getUsefuls();
				break;

			default:
				break;
			}
			
		}

		
	}
	

	private void getUsefuls() {
		// TODO Auto-generated method stub
		loginget.UsefulExpressionRequest(CommonUsed.this, largeId, smallId, focusId);
	}

	@SuppressLint("NewApi") 
	private void ClosePop() {
		fl_pop.setVisibility(View.GONE);
		sv_useful.setAlpha(1.0f);
	}

	@SuppressLint("NewApi") 
	private void ShowPop() {
		sv_useful.setAlpha(0.5f);
		fl_pop.setVisibility(View.VISIBLE);
	}

	private void GetFocusList(CommonUse cu) {
		for (int i = 0; i < s_list.size(); i++) {
			if (s_list.get(i).getID() == smallId) {
				cu = s_list.get(i);
				break;
			}
		}
		for (int i = 0; i < f_list.size(); i++) {
			if (smallId < 0) {
				show_list = f_list;
				break;
			}
			int id = f_list.get(i).getID();
			int length = (cu.getChildSelections() != null ? cu.getChildSelections().length : 0);
			if(0 == length){
				ClosePop();
				break;
			}
			for (int j = 0; j < length; j++) {
				if(id == cu.getChildSelections()[j]){
					show_list.add(f_list.get(i));
				}
			}
		}
	}

	private void GetsmallList(CommonUse cu) {
		for (int i = 0; i < l_list.size(); i++) {
			if (l_list.get(i).getID() == largeId) {
				cu = l_list.get(i);
				break;
			}
		}
		for (int i = 0; i < s_list.size(); i++) {
			if (largeId < 0) {
				show_list = s_list;
				break;
			}
			int id = s_list.get(i).getID();
			int length = (cu.getChildSelections() != null ? cu.getChildSelections().length : 0);
			if(null == cu.getChildSelections()){
				ClosePop();
				break;
			}
			for (int j = 0; j < length; j++) {
				if(id == cu.getChildSelections()[j]){
					show_list.add(s_list.get(i));
				}
			}
		}
	}
	
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("CommonUsed"); 
	    MobclickAgent.onResume(this);       //统计时长
	}
	public void onPause() {
	    super.onPause();
        MobclickAgent.onPageEnd("CommonUsed");
	    MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		editor.putInt("largeId", largeId);
		editor.putInt("smallId", smallId);
		editor.putInt("focusId", focusId);
		editor.commit();
	}

}
