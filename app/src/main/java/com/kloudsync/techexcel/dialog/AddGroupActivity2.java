package com.kloudsync.techexcel.dialog;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.GroupAdapter;
import com.kloudsync.techexcel.bean.Attendee;
import com.kloudsync.techexcel.help.SideBar;
import com.kloudsync.techexcel.help.SideBar.OnTouchingLetterChangedListener;
import com.kloudsync.techexcel.help.SideBarSortHelp;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.start.LoginGet;
import com.onyx.android.sdk.data.model.Consumer;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AddGroupActivity2 extends Activity {

	private TextView tv_sure;
	private ImageView tv_back;
	private EditText et_search;
	private ListView lv_group;
	private SideBar sidebar;

	private ArrayList<Customer> eList = new ArrayList<Customer>();
	private ArrayList<Customer> mlist = new ArrayList<Customer>();
	private GroupAdapter gadapter;
	List<Customer> mSelectList = new ArrayList<>();

	private List<String> clist = new ArrayList<String>();

	private InputMethodManager inputManager;
	private Button mBtnYes;
	private List<Attendee> attendees;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_groups);
		attendees = (List<Attendee>) getIntent().getSerializableExtra("attendees");
		initView();
	}


	private void initView() {
		tv_back = (ImageView) findViewById(R.id.tv_back);
		tv_sure = (TextView) findViewById(R.id.tv_sure);
		et_search = (EditText) findViewById(R.id.et_search);
		lv_group = (ListView) findViewById(R.id.lv_group);
		sidebar = (SideBar) findViewById(R.id.sidebar);
		mBtnYes = findViewById(R.id.btn_groups_yes);
		mBtnYes.setEnabled(false);
		getData();
		getSide();
		editGroup();

		tv_back.setOnClickListener(new myOnClick());
		tv_sure.setOnClickListener(new myOnClick());
		mBtnYes.setOnClickListener(new myOnClick());

	}

	private void editGroup() {
		inputManager = (InputMethodManager) et_search
				.getContext().getSystemService(getApplication().INPUT_METHOD_SERVICE);
		et_search.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
			                              int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
			                          int count) {
				gadapter.SetSelected(false);
				eList = new ArrayList<Customer>();
				for (int i = 0; i < mlist.size(); i++) {
					Customer cus = mlist.get(i);
					String name = et_search.getText().toString();
					String getName = cus.getName().toLowerCase();//转小写
					String nameb = name.toLowerCase();//转小写
					if (getName.contains(nameb.toString())
							&& name.length() > 0) {
						Customer customer;
						customer = cus;
						eList.add(customer);
					}
				}
				if (et_search.length() != 0) {
					gadapter = new GroupAdapter(getApplicationContext(), eList);
				} else {
					gadapter = new GroupAdapter(getApplicationContext(), mlist);
				}
				lv_group.setAdapter(gadapter);

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}

		});
	}

	private void getData() {
		final LoginGet loginget = new LoginGet();

//		loginget.CustomerRequest(getApplicationContext());

		loginget.setSchoolContactListener(new LoginGet.SchoolContactListener() {
			@Override
			public void getContact(ArrayList<Customer> list) {
				mlist = new ArrayList<Customer>();
				if(attendees==null||attendees.size()==0){
					mlist.addAll(list);
				}else {
					for(int i=0;i<list.size();i++){
						Customer customer=list.get(i);
						for(int j=0;j<attendees.size();j++){
							if(list.get(i).getUrl().equals(attendees.get(j).getAvatarUrl())){
								customer.setHasSelected(true);
								mSelectList.add(customer);
								break;
							}
						}
						mlist.add(customer);
					}
				}
				gadapter = new GroupAdapter(AddGroupActivity2.this, mlist);
				lv_group.setAdapter(gadapter);
				lv_group.setOnItemClickListener(new myOnItem());
			}
		});
		loginget.GetSchoolContact(getApplicationContext());
	}

	private void getSide() {
		sidebar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				int position;
				position = SideBarSortHelp.getPositionForSection(mlist,
						s.charAt(0));
				if (position != -1) {
					lv_group.setSelection(position);
				} else {
                    /*lv_group
                            .setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);*/
				}
			}
		});

	}

	private class myOnItem implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
		                        long id) {
			// TODO Auto-generated method stub
			gadapter.SetSelected(true);
			Customer cus;
			if (et_search.length() != 0) {
				cus = eList.get(position);
				for (int i = 0; i < mlist.size(); i++) {
					if (cus.getUserID().equals(mlist.get(i).getUserID())) {
						if (!cus.isHasSelected()) {
							if (cus.isSelected()) {
								mlist.get(i).setSelected(false);
								mSelectList.remove(cus);
							} else {
								mlist.get(i).setSelected(true);
								mSelectList.add(cus);
							}
						}
						break;
					}
				}
				gadapter.updateListView(eList);
			} else {
				cus = mlist.get(position);
				if (!cus.isHasSelected()) {
					if (cus.isSelected()) {
						mlist.get(position).setSelected(false);
						mSelectList.remove(cus);
					} else {
						mlist.get(position).setSelected(true);
						mSelectList.add(cus);
					}
				}else {
					cus.setHasSelected(false);
					mlist.get(position).setSelected(false);
					mSelectList.remove(cus);
				}
				gadapter.updateListView(mlist);
			}

			if (mSelectList.size() > 0) {
				mBtnYes.setEnabled(true);
			} else {
				mBtnYes.setEnabled(false);
			}

		}

	}

	protected class myOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.tv_back:
					finish();
					break;
				case R.id.tv_sure:
					getChatGroup();
					break;
				case R.id.btn_groups_yes:
					getChatGroup();
					break;
				default:
					break;
			}

		}

	}


	private void getChatGroup() {
		Intent intent = new Intent();
		intent.putExtra("customerList", (Serializable) mSelectList);
		setResult(3, intent);
		finish();

	}


}
