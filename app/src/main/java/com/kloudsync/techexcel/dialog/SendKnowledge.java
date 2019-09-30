package com.kloudsync.techexcel.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.kloudsync.techexcel.adapter.KnowledgeAdapter;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.info.CommonUse;
import com.kloudsync.techexcel.info.Knowledge;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.start.LoginGet.DialogGetListener;
import com.ub.techexcel.service.ConnectService;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SendKnowledge extends Activity {

	private List<TextView> tvs = new ArrayList<TextView>();
	private TextView tv_back;
	private ListView lv_knowledge, lv_pop;
	private LinearLayout lin_show;
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
	private ArrayList<Knowledge> kw_list = new ArrayList<Knowledge>();
	
	private CommonUsedAdapter cAdapter;
	private KnowledgeAdapter kAdapter;
	
	float density;
	
	public static SendKnowledge instance = null;
	
	LoginGet loginget = new LoginGet();

	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor editor;
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case AppConfig.GETKNOWLEDGE:
				String result = (String) msg.obj;
				if(result != null){
					GetKnowledge(result);
				}else{
					Toast.makeText(
							getApplicationContext(),
							getResources().getString(R.string.No_Data),
							1000).show();
				}
				
				break;
			case AppConfig.NO_NETWORK:
				Toast.makeText(
						SendKnowledge.this,
						getResources().getString(R.string.No_networking),
						1000).show();
				
				break;
			case AppConfig.NETERROR:
				Toast.makeText(
						SendKnowledge.this,
						getResources().getString(R.string.NETWORK_ERROR),
						1000).show();
				
				break;
			case AppConfig.FAILED:
				result = (String) msg.obj;
				Toast.makeText(getApplicationContext(), result,
						Toast.LENGTH_LONG).show();
				break;

			default:
				break;
			}
		}

	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sendknowledge);

		instance= this;

		getId();
		initView();
		
	}
	
	private void getId() {
		// TODO Auto-generated method stub
		sharedPreferences = getSharedPreferences(AppConfig.COMMONUSEDINFO,
				MODE_PRIVATE);
		editor = sharedPreferences.edit();

		largeId = sharedPreferences.getInt("largeId2", -1);
		smallId = sharedPreferences.getInt("smallId2", -1);
		focusId = sharedPreferences.getInt("focusId2", -1);
	}

	private void initView() {
		// TODO Auto-generated method stub
		tv_back = (TextView) findViewById(R.id.tv_back);
		lv_knowledge = (ListView) findViewById(R.id.lv_knowledge);
		lv_pop = (ListView) findViewById(R.id.lv_pop);
		lin_show = (LinearLayout) findViewById(R.id.lin_show);
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

		
		GetConcernHierarchy();
	}
	
	private void GetConcernHierarchy() {
		loginget = new LoginGet();
		loginget.setDialogGetListener(new DialogGetListener() {
			
			@Override
			public void getUseful(ArrayList<CommonUse> list) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void getCH(ArrayList<CommonUse> list) {
				main = new ArrayList<CommonUse>();
				main.addAll(list);
				FilterList();
				getKnowledgeList();
			}
		});
		loginget.ConcernHierarchyRequest(SendKnowledge.this);
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
				AppConfig.isSend = true;
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
				getKnowledgeList();
				break;

			default:
				break;
			}
			
		}

	}
	
	private class KnowledgeItem implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Knowledge kl = kw_list.get(position);
			Intent intent = new Intent(getApplication(), KnowledgeDetail.class);
			intent.putExtra("Knowledge", kl);
			startActivity(intent);
		}

	}
	

	private void getKnowledgeList() {
		final JSONObject jsonObject = format();
        new ApiTask(new Runnable() {
			@Override
			public void run() {
				try {
					JSONObject responsedata = ConnectService.submitDataByJson(
							AppConfig.URL_PUBLIC
									+ "KnowledgeList", jsonObject);
					Log.e("返回的jsonObject", jsonObject.toString() + "");
					Log.e("返回的responsedata", responsedata.toString() + "");
					String retcode = responsedata.getString("RetCode");
					Message msg = new Message();
					if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
						msg.what = AppConfig.GETKNOWLEDGE;
						msg.obj = responsedata.toString();
					}else{
						msg.what = AppConfig.FAILED;
						String ErrorMessage = responsedata.getString("ErrorMessage");
						msg.obj = ErrorMessage;
					}
					handler.sendMessage(msg);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
        }).start(ThreadManager.getManager());
	}

	private JSONObject format() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("PageIndex", "0");
			jsonObject.put("PageSize", "10");
			if(largeId >= 0){
				jsonObject.put("CusType1IDs", "" + largeId);
			}
			if(smallId >= 0){
				jsonObject.put("CusType2IDs", "" + smallId);
			}
			if(focusId >= 0){
				jsonObject.put("CusType3IDs", "" + focusId);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jsonObject;
	}

	private void GetKnowledge(String result) {
		try {
			JSONObject obj = new JSONObject(result);
			String RetCode = obj.getString("RetCode");
			if(RetCode.equals(AppConfig.RIGHT_RETCODE)){
				kw_list = new ArrayList<Knowledge>();
				JSONObject RetData = obj.getJSONObject("RetData");
				JSONArray Knowledges = RetData.getJSONArray("Knowledges");
				
				for (int i = 0; i < Knowledges.length(); i++) {
					JSONObject Knowledge = Knowledges.getJSONObject(i);
					String IssueTitle = Knowledge.getString("IssueTitle");
					String Description = Knowledge.getString("Description");
//					String CrntOwnerName = Knowledge.getString("CrntOwnerName");
//					String DateCreated = Knowledge.getString("DateCreated");
					/*String KWLinkURL = Knowledge.getString("KWLinkURL");
					if(KWLinkURL == null || KWLinkURL.equals("null")){
						KWLinkURL = "";
					};*/
					String VideoInfo = Knowledge.getString("VideoInfo");
					int ProjectID = Knowledge.getInt("ProjectID");
					int KnowledgeID = Knowledge.getInt("KnowledgeID");
//					int CreatedByID = Knowledge.getInt("CreatedByID");
//					int CrntOwnerID = Knowledge.getInt("CrntOwnerID");
					int ItemTypeID = Knowledge.getInt("ItemTypeID");
					int TypeID1 = Knowledge.getInt("TypeID1");
					int TypeID2 = Knowledge.getInt("TypeID2");
					int TypeID3 = Knowledge.getInt("TypeID3");
					int LinkOption = Knowledge.getInt("LinkOption");
					int ImageID = Knowledge.getInt("ImageID");
					
					Knowledge kl = new Knowledge(IssueTitle, ProjectID,
							KnowledgeID, ItemTypeID, TypeID1, TypeID2, TypeID3);

					kl.setDescription(Description);
//					kl.setKWLinkURL(KWLinkURL);
					kl.setVideoInfo(VideoInfo);
					kl.setLinkOption(LinkOption);
					kl.setImageID(ImageID);
					
					kw_list.add(kl);
				}
				kAdapter = new KnowledgeAdapter(SendKnowledge.this, kw_list);
				lv_knowledge.setAdapter(kAdapter);
				lv_knowledge.setOnItemClickListener(new KnowledgeItem());
				
			}else{
				
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressLint("NewApi") 
	private void ClosePop() {
		fl_pop.setVisibility(View.GONE);
		lin_show.setAlpha(1.0f);
	}

	@SuppressLint("NewApi") 
	private void ShowPop() {
		lin_show.setAlpha(0.5f);
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
	    MobclickAgent.onPageStart("SendKnowledge"); 
	    MobclickAgent.onResume(this);       //统计时长
	}
	public void onPause() {
	    super.onPause();
        MobclickAgent.onPageEnd("SendKnowledge");
	    MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		editor.putInt("largeId2", largeId);
		editor.putInt("smallId2", smallId);
		editor.putInt("focusId2", focusId);
		editor.commit();
	}
}
