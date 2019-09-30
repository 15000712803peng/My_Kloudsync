package com.kloudsync.techexcel.start;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.ChangeCountryCodeAdapter;
import com.kloudsync.techexcel.changecode.CountryCodeShow;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.SideBar;
import com.kloudsync.techexcel.help.SideBar.OnTouchingLetterChangedListener;
import com.kloudsync.techexcel.help.SideBarSortHelp;
import com.kloudsync.techexcel.info.CountryCodeInfo;
import com.kloudsync.techexcel.tool.PinyinComparatorCC;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChangeCountryCode extends Activity {

	private TextView tv_back;
	private EditText et_search;
	private ListView lv_group;
	private SideBar sidebar;
	
	private List<CountryCodeInfo> mlist = new ArrayList<CountryCodeInfo>();
	private List<CountryCodeInfo> eList = new ArrayList<CountryCodeInfo>();
	
	private ChangeCountryCodeAdapter cadapter;
	
	private boolean normal;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_countrycode);
		normal = getIntent().getBooleanExtra("normal", false);
		
		initView();		
	}

	private void initView() {
		tv_back = (TextView) findViewById(R.id.tv_back);
		et_search = (EditText) findViewById(R.id.et_search);
		lv_group = (ListView) findViewById(R.id.lv_group);
		sidebar = (SideBar) findViewById(R.id.sidebar);

		getData();
		getSide();
		editGroup();
		
		tv_back.setOnClickListener(new myOnClick());
	}
	
	private void getData() {
		mlist = new CountryCodeShow().ccl;
		Collections.sort(mlist, new PinyinComparatorCC());		

		cadapter = new ChangeCountryCodeAdapter(ChangeCountryCode.this, mlist);
		lv_group.setAdapter(cadapter);
		lv_group.setOnItemClickListener(new myOnItem());
	}
	
	private void getSide() {
		
		sidebar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
			
			@Override
			public void onTouchingLetterChanged(String s) {
				int position;
				position = SideBarSortHelp.getPositionForSectionCC(
						et_search.length() != 0 ? eList : mlist, s.charAt(0));
				if (position != -1) {
					lv_group.setSelection(position);
				} else {
					/*lv_group
							.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);*/
				}	
			}
		});
				
	}
	private void editGroup() {
		et_search.addTextChangedListener(new TextWatcher(){

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub 
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				cadapter.SetSelected(et_search.length() != 0 ? true : false);
				eList = new ArrayList<CountryCodeInfo>();
				for (int i = 0; i < mlist.size(); i++) {
					CountryCodeInfo ci = mlist.get(i);
					String name = et_search.getText().toString();
					String getName = ci.getName().toLowerCase();//转小写
					String getCode = (ci.getCode() + "").toLowerCase();
					String nameb = name.toLowerCase();//转小写
					if ((getName.contains(nameb.toString()) || getCode
							.contains(nameb.toString())) && name.length() > 0) {
						String mName = getName.contains(nameb.toString()) ? ci
								.getName() : getCode;
						int position = getName.contains(nameb.toString()) ? getName
								.indexOf(nameb) : getCode.indexOf(nameb);
						String name1 = mName.substring(0, position);
						String name2 = mName.substring(position, name.length()
								+ position);
						String name3 = mName.substring(
								name1.length() + name2.length(), mName.length());
						String url = name1 + "<font color=\"#55CF6F\">" + name2
								+ "</font>" + name3;
						ci.setShowname(url);						
						ci.setNcshow(getName.contains(nameb.toString()));
						eList.add(ci);
					}
				}
				cadapter.updateListView(et_search.length() != 0 ? eList : mlist);
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
	private class myOnItem implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			CountryCodeInfo ci;			
			if (et_search.length() != 0) {
				ci = eList.get(position);
			} else {
				ci = mlist.get(position);
			}
			AppConfig.COUNTRY_CODE = ci.getCode();
			AppConfig.COUNTRY_NAME = ci.getName();
			CancelActivity();
			
		}
		
	}

	protected class myOnClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_back:
				CancelActivity();
				break;

			default:
				break;
			}
			
		}
		
	}	

	private void CancelActivity() {
		finish();
		if (normal) {
			overridePendingTransition(R.anim.tran_in6, R.anim.tran_out6);
		} else {
			overridePendingTransition(R.anim.tran_in5, R.anim.tran_out5);
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			CancelActivity();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}


	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("ChangeCountryCode"); 
	    MobclickAgent.onResume(this);       //统计时长
	}
	public void onPause() {
	    super.onPause();
        MobclickAgent.onPageEnd("ChangeCountryCode");
	    MobclickAgent.onPause(this);
	}
}
