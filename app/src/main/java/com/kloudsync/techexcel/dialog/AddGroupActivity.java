package com.kloudsync.techexcel.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.GroupAdapter;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.message.GroupMessage;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.SideBar;
import com.kloudsync.techexcel.help.SideBar.OnTouchingLetterChangedListener;
import com.kloudsync.techexcel.help.SideBarSortHelp;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.info.GroupInfo;
import com.kloudsync.techexcel.start.LoginGet;
import com.ub.techexcel.service.ConnectService;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

public class AddGroupActivity extends Activity {

	private ImageView tv_back;
	private TextView tv_sure;
	private EditText et_search;
	private ListView lv_group;
	private SideBar sidebar;

	private ArrayList<Customer> eList = new ArrayList<Customer>();
	private ArrayList<Customer> mlist = new ArrayList<Customer>();
	private GroupAdapter gadapter;	

	private List<String> clist = new ArrayList<String>();
	
	private InputMethodManager inputManager;
	
	private boolean isAddGroup;
	
	private GroupInfo gi;
	
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case AppConfig.CREATE_GROUP:
				String result = (String) msg.obj;
				MyCreateJson(result);
				break;
			case AppConfig.ADD_GROUPMEMBER:
				ConversationActivity.instance.finish();
				ChatDetailActivity.instance.finish();
								
				GroupMessage gmsg = new GroupMessage(AppConfig.UserName + "添加了" + Names);
				io.rong.imlib.model.Message myMessage = io.rong.imlib.model.Message.obtain(gi.getGroupID(),Conversation.ConversationType.GROUP, gmsg);
				RongIM.getInstance()
				/*.getRongIMClient()
				.sendMessage(Conversation.ConversationType.GROUP, gi.getGroupID(),
								gmsg, "", "",
								new RongIMClient.SendMessageCallback() {
									@Override
									public void onError(Integer messageId,
														RongIMClient.ErrorCode e) {
										Log.e("lalala", "sendMessage onError");
									}

									@Override
									public void onSuccess(Integer integer) {
										Log.e("lalala", "sendMessage onSuccess");

									}
								});*/
						.sendMessage(myMessage, null, null, new IRongCallback.ISendMessageCallback() {
							@Override
							public void onAttached(io.rong.imlib.model.Message message) {

							}

							@Override
							public void onSuccess(io.rong.imlib.model.Message message) {
								Log.e("lalala", "sendMessage onError");

							}

							@Override
							public void onError(io.rong.imlib.model.Message message, RongIMClient.ErrorCode errorCode) {
								Log.e("lalala", "sendMessage onError");

							}
						});

				RongIM.getInstance().startGroupChat(AddGroupActivity.this, gi.getGroupID(), gi.getGroupName());
				finish();
				break;
			case AppConfig.NO_NETWORK:
				Toast.makeText(
						getApplicationContext(),
						getResources().getString(R.string.No_networking),
						1000).show();
				
				break;
			case AppConfig.NETERROR:
				Toast.makeText(
						getApplicationContext(),
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

	protected void MyCreateJson(String result) {

		JSONObject obj;
		try {
			obj = new JSONObject(result);
			JSONObject RetData = obj.getJSONObject("RetData");
			String GroupID = RetData.getString("GroupID");

			AppConfig.isUpdateDialogue = true;
			
			GroupMessage msg = new GroupMessage(AppConfig.UserName + getResources().getString(R.string.create_group));
			io.rong.imlib.model.Message myMessage = io.rong.imlib.model.Message.obtain(GroupID,Conversation.ConversationType.GROUP, msg);

			RongIM.getInstance()
			/*.getRongIMClient()
			.sendMessage(Conversation.ConversationType.GROUP, GroupID,
					msg, "", "",
					new RongIMClient.SendMessageCallback() {
						@Override
						public void onError(Integer messageId,
								RongIMClient.ErrorCode e) {
							Log.e("lalala", "sendMessage onError");
						}

						@Override
						public void onSuccess(Integer integer) {
							Log.e("lalala", "sendMessage onSuccess");

						}
					});*/
					.sendMessage(myMessage, null, null, new IRongCallback.ISendMessageCallback() {
						@Override
						public void onAttached(io.rong.imlib.model.Message message) {

						}

						@Override
						public void onSuccess(io.rong.imlib.model.Message message) {
							Log.e("lalala", "sendMessage onError");

						}

						@Override
						public void onError(io.rong.imlib.model.Message message, RongIMClient.ErrorCode errorCode) {
							Log.e("lalala", "sendMessage onError");

						}
					});
			
			
			RongIM.getInstance().startGroupChat(AddGroupActivity.this, GroupID, AppConfig.Name);

			finish();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_groups);
		isAddGroup = getIntent().getBooleanExtra("isAddGroup", false);
	
		initView();
	}


	private void initView() {
		tv_back = (ImageView) findViewById(R.id.tv_back);
		tv_sure = (TextView) findViewById(R.id.tv_sure);
		et_search = (EditText) findViewById(R.id.et_search);
		lv_group = (ListView) findViewById(R.id.lv_group);
		sidebar = (SideBar) findViewById(R.id.sidebar);
		
		getData();
		getSide();
		editGroup();
		
		tv_back.setOnClickListener(new myOnClick());
		tv_sure.setOnClickListener(new myOnClick());
		
	}
	
	private void editGroup() {
		inputManager = (InputMethodManager) et_search
				.getContext().getSystemService(getApplication().INPUT_METHOD_SERVICE);
		et_search.addTextChangedListener(new TextWatcher(){

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
				}else {
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
		if(isAddGroup){
			clist = getIntent().getStringArrayListExtra("chatlist");
		}
		final LoginGet loginget = new LoginGet();
		loginget.setLoginGetListener(new LoginGet.LoginGetListener() {
			
			@Override
			public void getMember(ArrayList<Customer> list) {
				mlist.addAll(list);
				if(isAddGroup){
					for (int i = 0; i < mlist.size(); i++) {
						String uid = mlist.get(i).getUBAOUserID();
						Log.e("uid", uid);
						for (int j = 0; j < clist.size(); j++) {
							if(uid.equals(clist.get(j))){
								mlist.get(i).setHasSelected(true);
								break;
							}
						}
					}
				}
				gadapter = new GroupAdapter(AddGroupActivity.this, mlist);
				lv_group.setAdapter(gadapter);
				lv_group.setOnItemClickListener(new myOnItem());				
			}
			
			@Override
			public void getCustomer(ArrayList<Customer> list) {
				mlist = new ArrayList<Customer>();
				mlist.addAll(list);
//				loginget.MemberRequest(getApplicationContext(), 0);
				if(isAddGroup){
					for (int i = 0; i < mlist.size(); i++) {
						String uid = mlist.get(i).getUBAOUserID();
						Log.e("uid", uid);
						for (int j = 0; j < clist.size(); j++) {
							if(uid.equals(clist.get(j))){
								mlist.get(i).setHasSelected(true);
								break;
							}
						}
					}
				}
				gadapter = new GroupAdapter(AddGroupActivity.this, mlist);
				lv_group.setAdapter(gadapter);
				lv_group.setOnItemClickListener(new myOnItem());
				
			}
		});
		loginget.CustomerRequest(getApplicationContext());
		
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
					lv_group
							.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
				}
			}
		});
		
	}
	
	private class myOnItem implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			gadapter.SetSelected(true);
			Customer cus;			
			if(et_search.length() != 0){
				cus = eList.get(position);
				for (int i = 0; i < mlist.size(); i++) {
					if(cus.getUserID().equals(mlist.get(i).getUserID())){
						if (!cus.isHasSelected()) {
							if (cus.isSelected()) {
								mlist.get(i).setSelected(false);
							} else {
								mlist.get(i).setSelected(true);
							}
						}
						break;
					}
				}
				gadapter.updateListView(eList);
			}else {
				cus = mlist.get(position);
				if (!cus.isHasSelected()) {
					if (cus.isSelected()) {
						mlist.get(position).setSelected(false);
					} else {
						mlist.get(position).setSelected(true);
					}
				}
				gadapter.updateListView(mlist);
			}
			
		}
		
	}
	
	protected class myOnClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_back:
				finish();
				break;
			case R.id.tv_sure:
//				GetAddGroup();
				GetAddChatGroup();
				break;

			default:
				break;
			}
			
		}
		
	}

	/**
	 * 创建讨论组
	 */
	public void getGroup() {
		for (int i = 0; i < mlist.size(); i++) {
			Customer cus = mlist.get(i);
			if(cus.isSelected()){
				mlist.get(i).setHasSelected(true);
			}
		}
		
		gadapter.updateListView(mlist);
		
		ArrayList<String> userIds = new ArrayList<String>();
		userIds.add(AppConfig.RongUserID);
		for (int i = 0; i < mlist.size(); i++) {
			Customer cus = mlist.get(i);
			if (cus.isHasSelected()) {
				userIds.add(cus.getUBAOUserID());
			}
		}

		AppConfig.Name = "讨论组";
		AppConfig.UserIDs = userIds;
		/**
		 * 创建讨论组会话并进入会话界面。
		 * 
		 * @param context
		 *            应用上下文。
		 * @param targetUserIds
		 *            要与之聊天的讨论组用户 Id 列表。
		 * @param title
		 *            聊天的标题，如果传入空值，则默认显示与之聊天的用户名称。
		 */
		if (userIds != null && userIds.size() > 0) {
			RongIM.getInstance().createDiscussionChat(AddGroupActivity.this,
					userIds, "讨论组");

			finish();
		}

	}


	/**
	 * 给讨论组添加成员
	 */
	private void AddGroup() {
		ArrayList<String> userIds = new ArrayList<String>();
		for (int i = 0; i < mlist.size(); i++) {
			Customer cus = mlist.get(i);
			if (cus.isSelected()) {
				userIds.add(cus.getUBAOUserID());
			}
		}
		for (int i = 0; i < clist.size(); i++) {
			for (int j = 0; j < userIds.size(); j++) {
				if(userIds.get(j).equals(clist.get(i))){
					userIds.remove(j--);
				}
			}
		}

		final String mTargetId = getIntent().getStringExtra("mTargetId");
		 /**
		 * 添加一名或者一组用户加入讨论组。
		 *
		 * @param discussionId 讨论组 Id。
		 * @param userIdList   邀请的用户 Id 列表。
		 * @param callback     执行操作的回调。
		 */
		if (userIds != null && userIds.size() > 0) {
			RongIM.getInstance()
					.getRongIMClient()
					.addMemberToDiscussion(mTargetId, userIds,
							new RongIMClient.OperationCallback() {

								@Override
								public void onSuccess() {
									ConversationActivity.instance.finish();
									ChatDetailActivity.instance.finish();
									finish();
									AppConfig.isChangeGroupName = false;
									RongIM.getInstance().startDiscussionChat(
											AddGroupActivity.this, mTargetId,
											AppConfig.GROUP_NAME);
								}

								@Override
								public void onError(
										RongIMClient.ErrorCode errorCode) {

								}
							});
		}
		
	}
	
	/**
	 * 讨论组的创建或添加
	 */
	public void GetAddGroup() {
		if (isAddGroup) {
			AddGroup();
		}else {
			getGroup();
		}
		
	}
	
	/**
	 * 创建群聊
	 */
	private void getChatGroup() {
		int size = 0;
		for (int i = 0; i < mlist.size(); i++) {
			Customer cus = mlist.get(i);
			if(cus.isSelected()){
				size++;
//				mlist.get(i).setHasSelected(true);
			}
		}
//		gadapter.updateListView(mlist);
		
		final JSONObject jsonObject = formatCreate();
		if(size < 1){
			Toast.makeText(getApplicationContext(), "请先选人再点确定", Toast.LENGTH_SHORT).show();
			return;
		}
		new ApiTask(new Runnable() {
			@Override
			public void run() {
				try {
					JSONObject responsedata = ConnectService.submitDataByJson(
							AppConfig.URL_PUBLIC
									+ "ChatGroup/CreateGroup", jsonObject);
					Log.e("返回的jsonObject", jsonObject.toString() + "");
					Log.e("返回的responsedata", responsedata.toString() + "");
					String retcode = responsedata.getString("RetCode");
					Message msg = new Message();
					if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
						msg.what = AppConfig.CREATE_GROUP;	
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
	
	private JSONObject formatCreate() {
		JSONObject jsonObject = new JSONObject();

		ArrayList<Customer> chats = new ArrayList<Customer>();
		Customer customer = new Customer();
		customer.setName(AppConfig.UserName);
		customer.setUBAOUserID(AppConfig.RongUserID);
		chats.add(customer);
		for (int i = 0; i < mlist.size(); i++) {
			Customer cus = mlist.get(i);
			/*if (cus.isHasSelected()) {
				chats.add(cus);
			}*/
			if (cus.isSelected()) {
				chats.add(cus);
			}
		}

		String GroupName = "";
		String UserIDs = "";
		for (int i = 0; i < chats.size(); i++) {
			Customer cus = chats.get(i);
			if(0 == i){
				GroupName += cus.getName();
				UserIDs += cus.getUBAOUserID();
			}else{
				GroupName += "," + cus.getName();
				UserIDs += "," + cus.getUBAOUserID();
			}
		}
		
		try {
			jsonObject.put("GroupName", GroupName);
			jsonObject.put("UserIDs", UserIDs);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jsonObject;
	}

	/**
	 * 往群里拉人
	 */
	private void AddChatGroup() {
		gi = (GroupInfo) getIntent().getSerializableExtra("groupinfo");
		
		final JSONObject jsonObject = formatAdd();
		new ApiTask(new Runnable() {
			@Override
			public void run() {
				try {
					JSONObject responsedata = ConnectService.submitDataByJson(
							AppConfig.URL_PUBLIC
									+ "ChatGroup/InviteFriend", jsonObject);
					Log.e("返回的jsonObject", jsonObject.toString() + "");
					Log.e("返回的responsedata", responsedata.toString() + "");
					String retcode = responsedata.getString("RetCode");
					Message msg = new Message();
					if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
						msg.what = AppConfig.ADD_GROUPMEMBER;	
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
	
	
	private String Names;
	private JSONObject formatAdd() {
		JSONObject jsonObject = new JSONObject();

		ArrayList<String> userIds = new ArrayList<String>();
		ArrayList<String> userNames = new ArrayList<String>();
		for (int i = 0; i < mlist.size(); i++) {
			Customer cus = mlist.get(i);
			if (cus.isSelected()) {
				userIds.add(cus.getUBAOUserID());
				userNames.add(cus.getName());
			}
		}
		for (int i = 0; i < clist.size(); i++) {
			for (int j = 0; j < userIds.size(); j++) {
				if(userIds.get(j).equals(clist.get(i))){
					userIds.remove(j);
					userNames.remove(j--);
				}
			}
		}

		String UserIDs = "";	
		Names = "";
		for (int i = 0; i < userIds.size(); i++) {
			String id = userIds.get(i);
			String name = userNames.get(i);
			if(0 == i){
				UserIDs += id;
				Names += name;
			}else{
				UserIDs += "," + id;
				Names += "," + name;
			}
		}
		try {
			jsonObject.put("GroupID", gi.getGroupID());
			jsonObject.put("UserIDs", UserIDs);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jsonObject;
	}

	/**
	 * 群聊的创建或添加
	 */
	public void GetAddChatGroup() {
		if (isAddGroup) {
			AddChatGroup();
		}else {
			getChatGroup();
		}
	}


	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("AddGroupActivity"); 
	    MobclickAgent.onResume(this);       //统计时长
	}
	public void onPause() {
	    super.onPause();
        MobclickAgent.onPageEnd("AddGroupActivity");
	    MobclickAgent.onPause(this);
	}
}
