package com.kloudsync.techexcel.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.ChatDetailAdapter;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.info.GroupInfo;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.start.LoginGet.FriendGetListener;
import com.kloudsync.techexcel.start.LoginGet.GroupGetListener;

import java.util.ArrayList;
import java.util.List;

public class ChatDetailActivity extends Activity {

	private LinearLayout lin_chatname, lin_empty, lin_report;
	private TextView tv_back, tv_delete, tv_name;
	private GridView gv_chat;
	
	private ArrayList<Customer> chatlist = new ArrayList<Customer>();
	private List<String> clist = new ArrayList<String>();
//	private String chatname;
	
	private ChatDetailAdapter cadapter;
	
	float density;
	
	boolean isGroup;
	
//	private String mTargetId;
	
	private GroupInfo gi;
	

    public static ChatDetailActivity instance;    

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chatdetail);
		
		instance = this;
		
		getDialogueInfo();
		
		initView();
	}

	private void getDialogueInfo() {
		//		clist = getIntent().getStringArrayListExtra("chatlist");
		//		chatname = getIntent().getStringExtra("chatname");
				isGroup = getIntent().getBooleanExtra("isGroup", false);
				gi = (GroupInfo) getIntent().getSerializableExtra("groupinfo");
				/*for (int i = 0; i < clist.size(); i++) {
					if(clist.get(i).equals(AppConfig.RongUserID)){
						clist.remove(i);
						break;
					}
				}*/
	}

	private void initView() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		density = dm.density;
		
		lin_chatname = (LinearLayout) findViewById(R.id.lin_chatname);
		lin_empty = (LinearLayout) findViewById(R.id.lin_empty);
		lin_report = (LinearLayout) findViewById(R.id.lin_report);
		tv_back = (TextView) findViewById(R.id.tv_back);
		tv_delete = (TextView) findViewById(R.id.tv_delete);
		tv_name = (TextView) findViewById(R.id.tv_name);
		gv_chat = (GridView) findViewById(R.id.gv_chat);
		
		GetMyName();
//		GetChat();
		if(gi.getGroupAdminID().equals(AppConfig.RongUserID)){
			tv_delete.setText(getResources().getString(R.string.Disbanded_group));
		}
		
		lin_chatname.setOnClickListener(new MyOnClick());
		lin_empty.setOnClickListener(new MyOnClick());
		lin_report.setOnClickListener(new MyOnClick());
		tv_back.setOnClickListener(new MyOnClick());
		tv_delete.setOnClickListener(new MyOnClick());
	}
	
	private void GetChat() {
		String ID = "";
		for (int i = 0; i < clist.size(); i++) {
			if(i != 0){
				ID += "," + clist.get(i); 
			}else{
				ID += clist.get(i);
			}
		}
		getFriendInfo(ID);
		
	}

	private void getFriendInfo(String targetID) {
		LoginGet loginget = new LoginGet();
		loginget.setFriendGetListener(new FriendGetListener() {
			
			@Override
			public void getFriends(ArrayList<Customer> cus_list) {
				// TODO Auto-generated method stub
				chatlist.addAll(cus_list);
				Customer cus = new Customer();
				chatlist.add(cus);
				cadapter = new ChatDetailAdapter(ChatDetailActivity.this, chatlist);
				LayoutParams params = (LayoutParams) gv_chat.getLayoutParams();
				int size = chatlist.size() / 4 + 1;
				params.height = (int) (size * 120 * density);
				gv_chat.setAdapter(cadapter);
				gv_chat.setOnItemClickListener(new myOnItem());	
			}
		});
		loginget.FriendsRequest(getApplicationContext(), targetID);
	}
	
	private class myOnItem implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub			
			if(position == (chatlist.size() - 1)){
				Intent intent = new Intent(ChatDetailActivity.this , AddGroupActivity.class);
				intent.putStringArrayListExtra("chatlist", (ArrayList<String>) clist);
				intent.putExtra("isAddGroup", true);
				intent.putExtra("groupinfo", gi);
				startActivity(intent);
			}
			
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void GetMyName() {

		if(isGroup){
			
			LoginGet loginget = new LoginGet();
			loginget.setGroupGetListener(new GroupGetListener() {
				
				@Override
				public void getGroupDetail(GroupInfo groupinfo) {
					
					gi = groupinfo;

					if(null == gi.getGroupName() || gi.getGroupName().length() < 1 || gi.getGroupName().equals("null")){
						tv_name.setText(getResources().getString(R.string.unnamed));
					}else{
						tv_name.setText(gi.getGroupName());
					}
				}
				
				@Override
				public void getGDMember(ArrayList<Customer> list) {
					chatlist = new ArrayList<Customer>();
					chatlist.addAll(list);
					clist = new ArrayList<String>();
					for (int i = 0; i < chatlist.size(); i++) {
						clist.add(chatlist.get(i).getUBAOUserID());
					}
								
					Customer cus = new Customer();
					chatlist.add(cus);
					cadapter = new ChatDetailAdapter(ChatDetailActivity.this, chatlist);
					LayoutParams params = (LayoutParams) gv_chat.getLayoutParams();
					int size = (chatlist.size() - 1) / 4 + 1;
					params.height = (int) (size * 120 * density);
					gv_chat.setAdapter(cadapter);
					gv_chat.setOnItemClickListener(new myOnItem());	
					
				}

			});
			loginget.GroupDetailRequest(ChatDetailActivity.this, gi.getGroupID());
			
			
			
			
		}
		
	}

	protected class MyOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_back:
				finish();
				break;
			case R.id.tv_delete:
				DeleteGroup();
				break;
			case R.id.lin_chatname:
				GoToCN();
				break;
			case R.id.lin_empty:
				Toast.makeText(getApplicationContext(), "待完成", Toast.LENGTH_SHORT).show();
				break;	
			case R.id.lin_report:
				Toast.makeText(getApplicationContext(), "待完成", Toast.LENGTH_SHORT).show();
				break;	

			default:
				break;
			}
			
		}
		
	}
	
	private void DeleteGroup() {
		if(gi.getGroupAdminID().equals(AppConfig.RongUserID)){
			DissmissGroup();
		}else{
			QuitChats();
		}
	}

	public void QuitChats() {
		AppConfig.DELETEGROUP_ID = gi.getGroupID();
		LoginGet l = new LoginGet();
		l.QuitGroupRequest(ChatDetailActivity.this, gi.getGroupID());
		
	}
	
	public void DissmissGroup() {
		AppConfig.DELETEGROUP_ID = gi.getGroupID();
		LoginGet l = new LoginGet();
		l.DismissGroupRequest(ChatDetailActivity.this, gi.getGroupID());
		
	}

	public void GoToCN() {
		Intent i = new Intent(getApplicationContext(), ChangeGroupNameActivity.class);
		i.putExtra("groupinfo", gi);
		startActivity(i);
		
	}

}
