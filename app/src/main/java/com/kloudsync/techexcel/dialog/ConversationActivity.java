package com.kloudsync.techexcel.dialog;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.EventRefreshChatList;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.contact.UserDetail;
import com.kloudsync.techexcel.dialog.message.CourseMessage;
import com.kloudsync.techexcel.dialog.message.KnowledgeMessage;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.info.GroupInfo;
import com.kloudsync.techexcel.info.Knowledge;
import com.kloudsync.techexcel.info.RobotInfo;
import com.kloudsync.techexcel.start.LoginGet;
import com.ub.service.activity.WatchCourseActivity2;
import com.ub.techexcel.bean.ServiceBean;
import com.ub.techexcel.service.ConnectService;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import io.rong.imkit.InputBar;
import io.rong.imkit.RongExtension;
import io.rong.imkit.RongIM;
import io.rong.imkit.RongIM.OnSendMessageListener;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imkit.fragment.UriFragment;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient.ErrorCode;
import io.rong.imlib.RongIMClient.ResultCallback;
import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.imlib.model.Discussion;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.LocationMessage;
import io.rong.message.TextMessage;


public class ConversationActivity extends FragmentActivity {


    private String NAME;
    //	private ArrayList<Customer> mlist = new ArrayList<Customer>();
    public static ArrayList<Customer> ulist = new ArrayList<Customer>();
//	public static ArrayList<Customer> memlist = new ArrayList<Customer>();
    /**
     * 目标 Id
     */
    private String mTargetId;

    /**
     * 刚刚创建完讨论组后获得讨论组的id 为targetIds，需要根据 为targetIds 获取 targetId
     */
    private String mTargetIds;

    /**
     * 会话类型
     */
    private ConversationType mConversationType;

    private static RongIM.LocationProvider.LocationCallback mLastLocationCallback;
    private TextView tv_title;
    private ImageView img_add;
    private RelativeLayout backLayout;

    RongExtension mRongExtension;

    private List<String> glist = new ArrayList<String>();

    private GroupInfo groupinfo = new GroupInfo();
    private ArrayList<Customer> gm_list = new ArrayList<Customer>();

    private static ConversationActivity conversationActivity;


    public static ConversationActivity instance;

    public static ConversationActivity getInstance() {

        if (conversationActivity == null) {
            conversationActivity = new ConversationActivity();
        }
        return conversationActivity;
    }

    // 扩展功能自定义
    /*InputProvider.ExtendProvider[] provider = {
            new ImageInputProvider(RongContext.getInstance()),// 图片
			new CameraInputProvider(RongContext.getInstance()),// 相机
			new LocationInputProvider(RongContext.getInstance()),// 地理位置
			new CommonUsedProvider(RongContext.getInstance()), // 自定义
			new SendKnowledgeProvider(RongContext.getInstance()),
			new NewServiceProvider(RongContext.getInstance()),
			new SendServiceProvider(RongContext.getInstance())
	};*/

    private boolean flag_friend = true;



    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case AppConfig.RobotReceive:
                    RobotInfo ri = (RobotInfo) msg.obj;
                    Message.ReceivedStatus ree = new Message.ReceivedStatus(8);
                    ree.setRead();
                    android.os.Message m = new android.os.Message();
                    TextMessage myTextMessage = TextMessage.obtain(ri.getText());
                    RongIM.getInstance().insertIncomingMessage(ConversationType.PRIVATE, AppConfig.DEVICE_ID + AppConfig.RongUserID,
                            AppConfig.RongUserID, ree,
                            myTextMessage, new ResultCallback<Message>() {
                                @Override
                                public void onSuccess(Message message) {
                                    Log.e("haha", message + " onSuccess");
                                    RemoveRBMessage(message);

                                }

                                @Override
                                public void onError(ErrorCode errorCode) {
                                    Log.e("haha", errorCode + " onError");

                                }
                            });
                    break;

                case AppConfig.LOAD_FINISH:
//                    GoToVIew();
                    break;
                default:
                    break;
            }
        }

    };

    private void GoToVIew() {
        Intent intent = new Intent(ConversationActivity.this, WatchCourseActivity2.class);
        intent.putExtra("userid", bean.getUserId());
        intent.putExtra("meetingId", bean.getId() + "");
        intent.putExtra("teacherid", bean.getTeacherId());
        intent.putExtra("identity", bean.getRoleinlesson());
        intent.putExtra("isStartCourse", true);
        intent.putExtra("isPrepare", true);
        intent.putExtra("isInstantMeeting", 0);
        intent.putExtra("yinxiangmode", 0);
        startActivity(intent);
    }


    private void RemoveRBMessage(Message message) {
        RongIM.getInstance().removeConversation(ConversationType.PRIVATE,
                message.getTargetId(), new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                Log.e("removeMessages", "onSuccess");

            }

            @Override
            public void onError(ErrorCode errorCode) {
                Log.e("removeMessages", "onError");

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        EventBus.getDefault().post(new EventRefreshChatList());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        conversationActivity = this;
        instance = this;
        RongIM.getInstance();
        Log.e("ConversationActivity","on_create");

		/*RongIM.resetInputExtensionProvider(
                ConversationType.PRIVATE, provider);
		RongIM.setLocationProvider(new LocationProvider());
		
		InputProvider.ExtendProvider[] provider2 = {
		};

		RongIM.resetInputExtensionProvider(
				ConversationType.SYSTEM, provider2);*/


//		RongIM.setPrimaryInputProvider(mp);
        setContentView(R.layout.conversation);
        Intent intent = getIntent();
        getIntentDate(intent);
        initView();
        RongIM.setConversationBehaviorListener(new MyConversationBehaviorListener());

    }


    private void initView() {
        backLayout = (RelativeLayout) findViewById(R.id.layout_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        img_add = (ImageView) findViewById(R.id.img_add);
        tv_title.setText(AppConfig.Name);
        AppConfig.Name = "";

        //讨论组监听
        if (ConversationType.DISCUSSION == mConversationType) {
            img_add.setImageDrawable(getResources().getDrawable(R.drawable.selected_groupchat));
            if (mTargetId != null) {
                GetDiscussionInfo();
            }
        } else if (ConversationType.GROUP == mConversationType) {
            tv_title.setText(getResources().getString(R.string.group_chat));
            img_add.setImageDrawable(getResources().getDrawable(R.drawable.selected_groupchat));
            if (mTargetId != null) {
                GetGroupReceiver();
                GetGroupInfo();
            }
            if (mTargetId.contains(getResources().getString(R.string.Classroom))) {
                img_add.setVisibility(View.GONE);
            }
        } else {
            img_add.setVisibility(View.GONE);
        }
        if (ConversationType.PRIVATE == mConversationType && mTargetId.contains(AppConfig.DEVICE_ID)) {
            GetMyTimer();
        } else {
            GetDialogName();
        }
        //设置自己发出的消息监听器。
        RongIM.getInstance().setSendMessageListener(new MySendMessageListener());
        backLayout.setOnClickListener(new myOnClick());
        img_add.setOnClickListener(new myOnClick());

    }

    private Timer timer;
    private String watermark = "0";
    private String robotID = "";

    private void GetMyTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                try {
                    JSONObject responsedata = ConnectService
                            .getRobotHttpGet(
                                    "https://directline.botframework.com/v3/directline/conversations/" +
                                            AppConfig.conversationId +
                                            "/activities?watermark=" + watermark);
                    Log.e("返回的responsedata", responsedata.toString() + "");
                    if (watermark.equals("0")) {
                        watermark = responsedata.getString("watermark");
                        return;
                    }
                    watermark = responsedata.getString("watermark");
                    JSONArray activities = responsedata.getJSONArray("activities");
                    RobotInfo ri = new RobotInfo();
                    for (int i = 0; i < activities.length(); i++) {
                        JSONObject RetData = activities.getJSONObject(i);
                        String replyToId = RetData.getString("replyToId");
                        if(replyToId.equals(robotID)){
                            String text = RetData.getString("text");
                            ri.setText(text);

                            android.os.Message msg = new android.os.Message();
                            msg.what = AppConfig.RobotReceive;
                            msg.obj = ri;
                            handler.sendMessage(msg);
                            break;
                        }else{
                            continue;
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        timer.schedule(task, 1000, 1000);
    }

    private void GetGroupInfo() {

        LoginGet loginget = new LoginGet();
        loginget.setGroupGetListener(new LoginGet.GroupGetListener() {

            @Override
            public void getGroupDetail(GroupInfo gi) {
                groupinfo = gi;
                if (null != gi.getGroupName() && gi.getGroupName().length() > 0) {
                    RongIM.getInstance()
                            .refreshGroupInfoCache(
                                    new Group(gi.getGroupID(), gi
                                            .getGroupName(), null));
                }
            }

            @Override
            public void getGDMember(ArrayList<Customer> list) {
                gm_list = list;
                String name = groupinfo.getGroupName();
                if (TextUtils.isEmpty(name)
                        || name.equals("null")) {
                    tv_title.setText(getResources().getString(R.string.group_chat) + "（" + gm_list.size() + "人）");
                } else {
                    tv_title.setText(name);
                }
                for (int i = 0; i < gm_list.size(); i++) {
                    Customer cus = gm_list.get(i);
                    Log.e("haha", cus.getUserID() + cus.getName() + Uri
                            .parse(cus.getUrl()));
                    RongIM.getInstance().refreshUserInfoCache(
                            new UserInfo(cus.getUBAOUserID(), cus.getName(), Uri
                                    .parse(cus.getUrl())));
                }
            }

        });
        loginget.GroupDetailRequest(ConversationActivity.this, mTargetId);
    }

    private void GetGroupReceiver() {
        /*RongIM.setOnReceiveMessageListener(new OnReceiveMessageListener() {

			@Override
			public boolean onReceived(Message message, int arg1) {
				// TODO Auto-generated method stub
				if(message.getObjectName().equals("UB:FriendMsg")){
		    		FriendMessage cc = (FriendMessage) message.getContent();
		    		getFriendInfo(!cc.getTargetID().equals(AppConfig.RongUserID) ? cc.getTargetID() : cc.getSourceID());
	                CustomerDao cd = new CustomerDao(ConversationActivity.this);
	    	        AddFriend af = new AddFriend();
	    	        af.setSourceID(cc.getSourceID());
	    	        af.setType(cc.getType());
	    	        af.setTime(cc.getTime());
	    	        af.setTargetID(cc.getTargetID());
	    	        cd.insert(af);
		    	}
				return false;
			}
		});*/

    }

    private void GetDiscussionInfo() {

        RongIM.getInstance().getRongIMClient()
                .getDiscussion(mTargetId, new ResultCallback<Discussion>() {

                    @Override
                    public void onError(ErrorCode arg0) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onSuccess(Discussion discussion) {
                        // TODO Auto-generated method stub
                        tv_title.setText(discussion.getName());

                        glist = discussion.getMemberIdList();
                        Log.e("hehe",
                                discussion.getId() + ":" + discussion.getName()
                                        + ":" + discussion.getCreatorId());
                        for (int i = 0; i < glist.size(); i++) {
                            Log.e("haha", i + ":" + glist.get(i));
                        }

                    }

                });


    }

    private void GetDialogName() {
        final LoginGet loginget = new LoginGet();
        loginget.setLoginGetListener(new LoginGet.LoginGetListener() {

            @Override
            public void getMember(ArrayList<Customer> list) {
                // TODO Auto-generated method stub
//				memlist = new ArrayList<Customer>();
//				mlist.addAll(list);
//				memlist.addAll(list);
            }

            @Override
            public void getCustomer(ArrayList<Customer> list) {
//				mlist = new ArrayList<Customer>();
                ulist = new ArrayList<Customer>();

//				mlist.addAll(list);
                ulist.addAll(list);
                for (int i = 0; i < ulist.size(); i++) {
                    Customer cus = ulist.get(i);
                    if (cus.getUBAOUserID().equals(mTargetId)) {
                        NAME = cus.getName();
                        tv_title.setText(NAME);
                        break;
                    }
                }
                if ((null == NAME || NAME.length() < 1)
                        && (mTargetId != null && !mTargetId.equals("-1"))) {
                    getFriendInfo(mTargetId);
                }
                getFriendSure();
//				loginget.MemberRequest(getApplicationContext(), 0);
            }
        });
        loginget.CustomerRequest(getApplicationContext());

    }

    protected void getFriendSure() {

        if (ConversationType.PRIVATE == mConversationType) {
            flag_friend = true;
            for (int i = 0; i < ulist.size(); i++) {
                String id = ulist.get(i).getUBAOUserID();
                if (mTargetId.equals(id)) {
                    flag_friend = false;
                    break;
                }
            }
        }

    }

    private void getFriendInfo(String targetID) {
        LoginGet loginget = new LoginGet();
        loginget.setFriendGetListener(new LoginGet.FriendGetListener() {

            @Override
            public void getFriends(ArrayList<Customer> cus_list) {
                // TODO Auto-generated method stub
                if (mConversationType != ConversationType.GROUP) {

                    Customer cus = cus_list.get(0);
                    tv_title.setText(cus.getName());
                }
            }
        });
        loginget.FriendsRequest(getApplicationContext(), targetID);


    }

    protected class myOnClick implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.layout_back:
                    EventBus.getDefault().post(new EventRefreshChatList());
                    finish();
                    break;
                case R.id.img_add:
                    ChatDetail();
                    break;

                default:
                    break;
            }

        }

    }

    public void ChatDetail() {
        if (ConversationType.DISCUSSION == mConversationType) {
            Intent i = new Intent(ConversationActivity.this, ChatDetailActivity.class);
            String targetId = GetGroupID();
            i.putStringArrayListExtra("chatlist", (ArrayList<String>) (mTargetId != null ? glist : AppConfig.UserIDs));
            i.putExtra("chatname", tv_title.getText().toString());
            i.putExtra("mTargetId", targetId);
            i.putExtra("isGroup", true);
            startActivity(i);

        } else if (ConversationType.GROUP == mConversationType) {
            if (groupinfo != null && groupinfo.getGroupID() != null) {
                Intent i = new Intent(ConversationActivity.this,
                        ChatDetailActivity.class);
                i.putExtra("groupinfo", groupinfo);
                i.putExtra("isGroup", true);
                startActivity(i);
            } else {
                Toast.makeText(getApplicationContext(), "该群已解散或网络错误", Toast.LENGTH_SHORT).show();
            }
        }

    }

    /**
     * 展示如何从 Intent 中得到 融云会话页面传递的 Uri
     */
    private void getIntentDate(Intent intent) {

        mTargetId = intent.getData().getQueryParameter("targetId");
        mTargetIds = intent.getData().getQueryParameter("targetIds");
        // intent.getData().getLastPathSegment();//获得当前会话类型
        mConversationType = ConversationType.valueOf(intent
                .getData().getLastPathSegment()
                .toUpperCase(Locale.getDefault()));


		/*if(ConversationType.GROUP == mConversationType){
            RongIM.resetInputExtensionProvider(
				ConversationType.GROUP, provider);
		}*/

        enterFragment(mConversationType, mTargetId);

        Log.e("mTargetId", mTargetId + ":" + mTargetIds + ":" + mConversationType);
        if (mTargetId.equals("-1") || ConversationType.SYSTEM == mConversationType) {
            /*InputProvider.ExtendProvider[] provider = {
                    new ImageInputProvider(RongContext.getInstance()),// 图片
			};
//			RongIM.getInstance();

			RongIM.resetInputExtensionProvider(
					Conversation.ConversationType.SYSTEM, provider);*/
        }

        //切换输入显示形式
        if (ConversationType.GROUP == mConversationType && mTargetId.contains(getResources().getString(R.string.Classroom))) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mRongExtension.setInputBarStyle(InputBar.Style.STYLE_CONTAINER);
        } else if (ConversationType.PRIVATE == mConversationType && mTargetId.contains(AppConfig.DEVICE_ID)) {
            mRongExtension.setInputBarStyle(InputBar.Style.STYLE_CONTAINER);
        } else {
            mRongExtension.setInputBarStyle(InputBar.Style.STYLE_SWITCH_CONTAINER_EXTENSION);
        }
    }


    /**
     * 加载会话页面 ConversationFragment
     *
     * @param mConversationType 会话类型
     * @param mTargetId         目标 Id
     */
    private void enterFragment(ConversationType mConversationType,
                               String mTargetId) {

        ConversationFragment fragment = (ConversationFragment) getSupportFragmentManager()
                .findFragmentById(R.id.conversation);
        mRongExtension = (RongExtension) fragment.getView().findViewById(R.id.rc_extension);

        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName)
                .buildUpon().appendPath("conversation")
                .appendPath(mConversationType.getName().toLowerCase())
                .appendQueryParameter("targetId", mTargetId).build();


        fragment.setUri(uri);
    }


    private class MyConversationBehaviorListener implements
            RongIM.ConversationBehaviorListener {

        /**
         * 当点击用户头像后执行。
         *
         * @param context          上下文。
         * @param conversationType 会话类型。
         * @param userInfo         被点击的用户的信息。
         * @return 如果用户自己处理了点击后的逻辑，则返回 true，否则返回 false，false 走融云默认处理方式。
         */
        @Override
        public boolean onUserPortraitClick(Context context,
                                           ConversationType conversationType,
                                           UserInfo userInfo) {
            if (mTargetId.contains(getResources().getString(R.string.Classroom)) ||
                    mTargetId.contains(AppConfig.DEVICE_ID)) {
                return false;
            }
            if (userInfo.getUserId().equals(AppConfig.RongUserID)) {
                AppConfig.isToPersonalCenter = true;
                finish();
            } else if (!mTargetId.equals("-1")) {
                boolean flag_um = false;
                String ID = "-1";
                for (int i = 0; i < ulist.size(); i++) {
                    Customer cus = ulist.get(i);
                    if (cus.getUBAOUserID().equals(userInfo.getUserId())) {
                        flag_um = true;
                        ID = cus.getUserID();
                        break;
                    }
                }
                Intent i;
                i = new Intent(getApplicationContext(), UserDetail.class);
                i.putExtra("UserID", ID);
                if (!userInfo.getUserId().equals(AppConfig.RongUserID)) {
                    startActivity(i);
                }
                /*if(flag_um){
					i = new Intent(getApplicationContext(),UserDetail.class);
					i.putExtra("UserID", ID);
					if(!userInfo.getUserId().equals(AppConfig.RongUserID)){
						startActivity(i);				
					}
				}else{
					for (int j = 0; j < memlist.size(); j++) {
						Customer cus = memlist.get(j);
						if(cus.getUBAOUserID().equals(userInfo.getUserId())){
							flag_um = true;
							ID = cus.getUserID();
							break;
						}
					}
					if(flag_um){
						i = new Intent(getApplicationContext(),MemberDetail.class);
						i.putExtra("UserID", ID);
						if(!userInfo.getUserId().equals(AppConfig.RongUserID)){
							startActivity(i);				
						}				
					}
				}*/

            }
            return false;
        }

        /**
         * 当长按用户头像后执行。
         *
         * @param context          上下文。
         * @param conversationType 会话类型。
         * @param userInfo         被点击的用户的信息。
         * @return 如果用户自己处理了点击后的逻辑，则返回 true，否则返回 false，false 走融云默认处理方式。
         */
        @Override
        public boolean onUserPortraitLongClick(Context context,
                                               ConversationType conversationType,
                                               UserInfo userInfo) {
            return false;
        }

        /**
         * 当点击消息时执行。
         *
         * @param context 上下文。
         * @param view    触发点击的 View。
         * @param message 被点击的消息的实体信息。
         * @return 如果用户自己处理了点击后的逻辑，则返回 true， 否则返回 false, false 走融云默认处理方式。
         */
        @Override
        public boolean onMessageClick(Context context, View view,
                                      Message message) {
			/*if (message.getContent() instanceof LocationMessage) {
				LocationMessage msg = (LocationMessage) message.getContent();
				Intent i = new Intent(getApplicationContext(), ContactMap.class);
				i.putExtra("isLocation", true);
				i.putExtra("isDialog", true);
				String ID = null;
				if(message.getMessageDirection() == Message.MessageDirection.SEND){
					ID = AppConfig.UserID;
				}else if(message.getMessageDirection() == Message.MessageDirection.RECEIVE){
					ID = mTargetId;
				}					
				Customer customer = new Customer();
				boolean isCustomer = false;
				*//*for (int j = 0; j < ulist.size(); j++) {
					Customer cus = ulist.get(j);
					if(cus.getUBAOUserID().equals(ID)){
						customer = cus;
						isCustomer = true;
						break;
					}
				}
				for (int j = 0; j < memlist.size(); j++) {
					Customer cus = memlist.get(j);
					if(cus.getUBAOUserID().equals(ID)){
						customer = cus;
						isCustomer = false;
						break;
					}
				}*//*
				customer.setLatitude(msg.getLat());
				customer.setLongitude(msg.getLng());
				i.putExtra("Customer", customer);
				i.putExtra("isCustomer", isCustomer);
				
				startActivity(i);
			}*/
            return false;
        }

        /**
         * 当长按消息时执行。
         *
         * @param context 上下文。
         * @param view    触发点击的 View。
         * @param message 被长按的消息的实体信息。
         * @return 如果用户自己处理了长按后的逻辑，则返回 true，否则返回 false，false 走融云默认处理方式。
         */
        @Override
        public boolean onMessageLongClick(Context context, View view,
                                          Message message) {
            return false;
        }

        /**
         * 当点击链接消息时执行。
         *
         * @param context 上下文。
         * @param link    被点击的链接。
         * @return 如果用户自己处理了点击后的逻辑处理，则返回 true， 否则返回 false, false 走融云默认处理方式。
         */
        @Override
        public boolean onMessageLinkClick(Context context, String link) {

            String shareKey = link.replace(AppConfig.Sharelive,"");
            GetCourseID(shareKey);

            return true;
        }
    }

    private void GetCourseID(final String shareKey) {

        new ApiTask(new Runnable() {
            @Override
            public void run() {
                JSONObject returnjson = ConnectService
                        .getIncidentbyHttpGet(AppConfig.URL_PUBLIC
                                + "ShareDocument/SharedTempLesson?shareKey=" + shareKey);
                Log.e("SharedTempLesson", returnjson.toString() + "");
                try {
                    int retCode = returnjson.getInt("RetCode");
                    switch (retCode) {
                        case AppConfig.RETCODE_SUCCESS:
                            String RetData = returnjson.getString("RetData");
                            ViewdoHaha(RetData);
                            break;
                        default:
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }

    private void ViewdoHaha(final String meetingID) {
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                JSONObject returnjson = ConnectService
                        .getIncidentbyHttpGet(AppConfig.URL_PUBLIC
                                + "Lesson/Item?lessonID=" + meetingID);
                formatServiceData(returnjson);
            }
        }).start(ThreadManager.getManager());
    }

    private ServiceBean bean = new ServiceBean();
    private void formatServiceData(JSONObject returnJson) {
        Log.e("returnJson", returnJson.toString());
        try {
            int retCode = returnJson.getInt("RetCode");
            switch (retCode) {
                case AppConfig.RETCODE_SUCCESS:
                    JSONObject service = returnJson.getJSONObject("RetData");
                    bean = new ServiceBean();
                    bean.setId(service.getInt("LessonID"));
                    String des = service.getString("Description");
                    bean.setDescription(des);
                    int statusID = service.getInt("StatusID");
                    bean.setStatusID(statusID);
                    bean.setRoleinlesson(service.getInt("RoleInLesson"));
                    JSONArray memberlist = service.getJSONArray("MemberInfoList");
                    for (int i = 0; i < memberlist.length(); i++) {
                        JSONObject jsonObject = memberlist.getJSONObject(i);
                        int role = jsonObject.getInt("Role");
                        if (role == 2) { //teacher
                            bean.setTeacherName(jsonObject.getString("MemberName"));
                            bean.setTeacherId(jsonObject.getString("MemberID"));
                        } else if (role == 1) {
                            bean.setUserName(jsonObject.getString("MemberName"));
                            bean.setUserId(jsonObject.getString("MemberID"));
                        }
                    }
                    if(bean.getTeacherId().equals(AppConfig.UserID)){
                        bean.setRoleinlesson(2);
                    }else{
                        bean.setRoleinlesson(1);
                    }
                    handler.obtainMessage(AppConfig.LOAD_FINISH).sendToTarget();
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private class MySendMessageListener implements OnSendMessageListener {

        /**
         * 消息发送前监听器处理接口（是否发送成功可以从 SentStatus 属性获取）。
         *
         * @param message 发送的消息实例。
         * @return 处理后的消息实例。
         */
        @Override
        public Message onSend(Message message) {
            // 开发者根据自己需求自行处理逻辑
            if (mTargetId.contains(AppConfig.DEVICE_ID) && (ConversationType.PRIVATE == mConversationType)) {
                TextMessage myTextMessage = (TextMessage) message.getContent();
                SendRobotMessage(myTextMessage.getContent());
                return message;
            }
            if (flag_friend && (ConversationType.PRIVATE == mConversationType)) {
                Toast.makeText(getApplicationContext(), "对方不是你的好友",
                        Toast.LENGTH_LONG).show();
                return null;
            }
            if (ConversationType.SYSTEM == mConversationType) {
                Toast.makeText(getApplicationContext(), "暂不支持与小博士聊天",
                        Toast.LENGTH_LONG).show();
                return null;
            }
            if (ConversationType.GROUP == mConversationType && mTargetId.contains(getResources().getString(R.string.Classroom))) {
                TextMessage myTextMessage = (TextMessage) message.getContent();
                myTextMessage.setExtra(AppConfig.UserID);
                return Message.obtain(message.getTargetId(), ConversationType.GROUP, myTextMessage);
            }
            return message;
        }

        /**
         * 消息在 UI 展示后执行/自己的消息发出后执行,无论成功或失败。
         *
         * @param message              消息实例。
         * @param sentMessageErrorCode 发送消息失败的状态码，消息发送成功 SentMessageErrorCode 为 null。
         * @return true 表示走自已的处理方式，false 走融云默认处理方式。
         */
        @Override
        public boolean onSent(Message message,
                              RongIM.SentMessageErrorCode sentMessageErrorCode) {
            if (mTargetId.contains(AppConfig.DEVICE_ID) && (ConversationType.PRIVATE == mConversationType)) {
                return true;
            }
            if (flag_friend && (ConversationType.PRIVATE == mConversationType)) {
                return true;
            }
            if (ConversationType.SYSTEM == mConversationType) {
                return true;
            }

            return false;
        }
    }

    private void SendRobotMessage(String content) {
        final JSONObject jsonobject = format(content);
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject responsedata = ConnectService
                            .submitDataByJsonRobot("https://directline.botframework.com/v3/directline/conversations/" +
                                    AppConfig.conversationId + "/activities", jsonobject);
                    Log.e("jsonobject", jsonobject.toString() + "");
                    Log.e("responsedata", responsedata.toString() + "");
                    robotID = responsedata.getString("id");
                    /*android.os.Message msg = new android.os.Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        msg.what = AppConfig.SAVESUCCESS;
                        msg.obj = UserID;
                    } else {
                        msg.what = AppConfig.FAILED;
                        String ErrorMessage = responsedata
                                .getString("ErrorMessage");
                        msg.obj = ErrorMessage;
                    }
                    handler.sendMessage(msg);*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }

    private JSONObject format(String content) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", "message");
            JSONObject js = new JSONObject();
            js.put("id", "user1");
            jsonObject.put("from", js);
            jsonObject.put("text", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }


    /**
     * 常用语扩展功能
     * @author pingfan
     *
     *//*
	public class CommonUsedProvider extends InputProvider.ExtendProvider {

		HandlerThread mWorkThread;
		Handler mUploadHandler;

		public CommonUsedProvider(RongContext context) {
			super(context);
			mWorkThread = new HandlerThread("RongDemo");
			mWorkThread.start();
			mUploadHandler = new Handler(mWorkThread.getLooper());
		}

		*//**
     * 设置展示的图标
     *
     * @param context
     * @return
     *//*
		@Override
		public Drawable obtainPluginDrawable(Context context) {
			// R.drawable.de_contacts 通讯录图标
			return context.getResources().getDrawable(R.drawable.commonphrase);
		}

		*//**
     * 设置图标下的title
     *
     * @param context
     * @return
     *//*
		@Override
		public CharSequence obtainPluginTitle(Context context) {
			// R.string.add_contacts 通讯录
			return context.getString(R.string.Common_used);
		}

		*//**
     * click 事件
     *
     * @param view
     *//*
		@Override
		public void onPluginClick(View view) {
			*//*Intent intent = new Intent();
			intent.setAction(Intent.ACTION_PICK);
			intent.setData(ContactsContract.Contacts.CONTENT_URI);
			startActivityForResult(intent, REQUEST_CONTACT);*//*
			Intent intent = new Intent(ConversationActivity.this, CommonUsed.class);
			startActivity(intent);
			
		}
		

		
	}
	
	*//**
     * 发送知识扩展功能
     * @author pingfan
     *
     *//*
	public class SendKnowledgeProvider extends InputProvider.ExtendProvider {

		HandlerThread mWorkThread;
		Handler mUploadHandler;

		public SendKnowledgeProvider(RongContext context) {
			super(context);
			mWorkThread = new HandlerThread("RongDemo");
			mWorkThread.start();
			mUploadHandler = new Handler(mWorkThread.getLooper());
		}

		*//**
     * 设置展示的图标
     *
     * @param context
     * @return
     *//*
		@Override
		public Drawable obtainPluginDrawable(Context context) {
			// R.drawable.de_contacts 通讯录图标
			return context.getResources().getDrawable(R.drawable.knowledge);
		}

		*//**
     * 设置图标下的title
     *
     * @param context
     * @return
     *//*
		@Override
		public CharSequence obtainPluginTitle(Context context) {
			// R.string.add_contacts 通讯录
			return context.getString(R.string.Send_knowledge);
		}

		*//**
     * click 事件
     *
     * @param view
     *//*
		@Override
		public void onPluginClick(View view) {
			*//*Intent intent = new Intent();
			intent.setAction(Intent.ACTION_PICK);
			intent.setData(ContactsContract.Contacts.CONTENT_URI);
			startActivityForResult(intent, REQUEST_CONTACT);*//*
			Intent intent = new Intent(ConversationActivity.this, SendKnowledge.class);
			startActivity(intent);
		}
		

		
	}
	
	*//**
     * 新建服务扩展功能
     * @author pingfan
     *
     *//*
	public class NewServiceProvider extends InputProvider.ExtendProvider {

		HandlerThread mWorkThread;
		Handler mUploadHandler;

		public NewServiceProvider(RongContext context) {
			super(context);
			mWorkThread = new HandlerThread("RongDemo");
			mWorkThread.start();
			mUploadHandler = new Handler(mWorkThread.getLooper());
		}

		*//**
     * 设置展示的图标
     *
     * @param context
     * @return
     *//*
		@Override
		public Drawable obtainPluginDrawable(Context context) {
			// R.drawable.de_contacts 通讯录图标
			return context.getResources().getDrawable(R.drawable.service);
		}

		*//**
     * 设置图标下的title
     *
     * @param context
     * @return
     *//*
		@Override
		public CharSequence obtainPluginTitle(Context context) {
			// R.string.add_contacts 通讯录
			return context.getString(R.string.New_Service);
		}

		*//**
     * click 事件
     *
     * @param view
     *//*
		@Override
		public void onPluginClick(View view) {
			*//*Intent intent = new Intent();
			intent.setAction(Intent.ACTION_PICK);
			intent.setData(ContactsContract.Contacts.CONTENT_URI);
			startActivityForResult(intent, REQUEST_CONTACT);*//*
			if (1 == AppConfig.UserType) {
				boolean ismember = false;
				for (int i = 0; i < memlist.size(); i++) {
					Customer cus = memlist.get(i);
					if (cus.getUBAOUserID().equals(mTargetId)) {
						ismember = true;
						break;
					}
				}
				Intent intent = new Intent(ConversationActivity.this,
						AddServiceFirst.class);
				intent.putExtra("mTargetId", mTargetId);
				int ctype = ConversationType.GROUP == mConversationType? 2 : 1;
				intent.putExtra("conversationtype", ctype);
				// intent.putExtra("isDialogue", true);
				AppConfig.isNewService = true;
				if (ismember) {
					Toast.makeText(getApplicationContext(), "会员不可新建服务",
							Toast.LENGTH_LONG).show();
				} else {
					startActivity(intent);
				}
			}else {
				PopUbaoMan pum = new PopUbaoMan();
				int width = getResources().getDisplayMetrics().widthPixels;
				int height = getResources().getDisplayMetrics().heightPixels;
				pum.getPopwindow(getApplicationContext(), width, height, getString(R.string.Add_Service_Pop));
				pum.StartPop(view);
				pum.setPoPDismissListener(new PoPDismissListener() {
					
					@Override
					public void PopDismiss() {
						// TODO Auto-generated method stub
						
					}
				});
				
				
			}
		}
		

		
	}
	
	*//**
     * 发送服务单扩展功能
     * @author pingfan
     *
     *//*
	public class SendServiceProvider extends InputProvider.ExtendProvider {

		HandlerThread mWorkThread;
		Handler mUploadHandler;

		public SendServiceProvider(RongContext context) {
			super(context);
			mWorkThread = new HandlerThread("RongDemo");
			mWorkThread.start();
			mUploadHandler = new Handler(mWorkThread.getLooper());
		}

		*//**
     * 设置展示的图标
     *
     * @param context
     * @return
     *//*
		@Override
		public Drawable obtainPluginDrawable(Context context) {
			// R.drawable.de_contacts 通讯录图标
			return context.getResources().getDrawable(R.drawable.sendservice);
		}

		*//**
     * 设置图标下的title
     *
     * @param context
     * @return
     *//*
		@Override
		public CharSequence obtainPluginTitle(Context context) {
			// R.string.add_contacts 通讯录
			return context.getString(R.string.Send_Service);
		}

		*/

    /**
     * click 事件
     *//*
		@Override
		public void onPluginClick(View view) {
			*//*Intent intent = new Intent();
			intent.setAction(Intent.ACTION_PICK);
			intent.setData(ContactsContract.Contacts.CONTENT_URI);
			startActivityForResult(intent, REQUEST_CONTACT);*//*
			if (1 == AppConfig.UserType) {
				Intent intent = new Intent(ConversationActivity.this,
						SendServiceActivity.class);
				intent.putExtra("mTargetId", mTargetId);
				int ctype = ConversationType.GROUP == mConversationType? 2 : 1;
				intent.putExtra("conversationtype", ctype);
				// intent.putExtra("isDialogue", true);
				AppConfig.isNewService = true;
				startActivity(intent);
			}else {
				平UbaoMan pum = new PopUbaoMan();
				WindowManager window = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
				int width = getResources().getDisplayMetrics().widthPixels;
				int height = getResources().getDisplayMetrics().heightPixels;
				pum.getPopwindow(getApplicationContext(), width, height, getString(R.string.Add_Service_Pop));
				pum.StartPop(view);
				
				
			}
		}
		

		
	}*/

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (AppConfig.isSend) {
			/*TextInputProvider textInputProvider = (TextInputProvider) RongContext
					.getInstance().getPrimaryInputProvider();
			// 重置文本框数据
			textInputProvider.setEditTextContent(AppConfig.SEND_SENTENCE);*/

            EditText et = mRongExtension.getInputEditText();
            et.setText(AppConfig.SEND_SENTENCE);
        }
        if (AppConfig.isSendKnowledge) {
            Knowledge kl = AppConfig.KNOWLEDGE;
            KnowledgeMessage msg = new KnowledgeMessage(kl.getDescription(),
                    kl.getIssueTitle(), kl.getKnowledgeID() + "",
                    kl.getImageID() + "", kl.getVideoInfo());
            CourseMessage msg2 = new CourseMessage("123", "123", "123");

            SelectSend(msg);
        }
		/*if (AppConfig.ISONRESUME) {//服务单
			ServiceBean sb = AppConfig.tempServiceBean;
			Log.e("sssbb", sb.getId() + ":" + sb.getName() + ":" + sb.getConcernName());
			CustomizeMessage msg = new CustomizeMessage(sb.getId()
					+ "", sb.getName(), sb.getConcernName(), "800");
			SelectSend(msg);
		}*/
        if (AppConfig.ISLOCATIONS) {
            LocationMessage msg = AppConfig.LOCATIONMESSAGE;
            Log.e("LocationMessage", msg.getPoi() + ":" + msg.getLat() + ":" + msg.getLng() + ":" + msg.getImgUri());
            SelectSend(msg);
        }
        AppConfig.isSend = false;
        AppConfig.isSendKnowledge = false;
        AppConfig.ISONRESUME = false;
        AppConfig.ISLOCATIONS = false;
		
		/*if(ConversationType.DISCUSSION == mConversationType){
			if(AppConfig.isChangeGroupName){
				String targetId = GetGroupID();				
				
				RongIM.getInstance()
				.getRongIMClient()
				.setDiscussionName(targetId, AppConfig.GROUP_NAME, new OperationCallback() {
					
					@Override
					public void onSuccess() {
						tv_title.setText(AppConfig.GROUP_NAME);
						
					}
					
					@Override
					public void onError(ErrorCode arg0) {
					}
				});
				
			}
		}
		AppConfig.isChangeGroupName = false;*/
    }

    private void SelectSend(MessageContent msg) {
        if (ConversationType.GROUP == mConversationType) {
            sendGroupMessage(msg);
        } else {
            sendMessage(msg);
        }
    }

    /**
     * 获取第一次进入群聊或讨论组时，群组的ID
     *
     * @return
     */
    private String GetGroupID() {
        String targetId = "";
        if (null != mTargetId) {
            targetId = mTargetId;
        } else {
            UriFragment fragment = (UriFragment) getSupportFragmentManager().getFragments().get(0);
            fragment.getUri();
            //得到讨论组的 targetId
            targetId = fragment.getUri().getQueryParameter("targetId");
        }
        return targetId;
    }

    @SuppressWarnings("deprecation")
    private void sendMessage(final MessageContent msg) {
        Message myMessage = Message.obtain(mTargetId, ConversationType.PRIVATE, msg);
        RongIM.getInstance()
		/*.getRongIMClient()
		.sendMessage(ConversationType.PRIVATE, mTargetId,
				msg, "", "",
				new RongIMClient.SendMessageCallback() {
					@Override
					public void onError(Integer messageId,
							ErrorCode e) {
						Log.e("lalala", "sendMessage onError");
					}

					@Override
					public void onSuccess(Integer integer) {
						Log.e("lalala", "sendMessage onSuccess");

					}
				});*/
                .sendMessage(myMessage, null, null, new IRongCallback.ISendMessageCallback() {
                    @Override
                    public void onAttached(Message message) {

                    }

                    @Override
                    public void onSuccess(Message message) {
                        Log.e("lalala", "sendMessage onError");

                    }

                    @Override
                    public void onError(Message message, ErrorCode errorCode) {
                        Log.e("lalala", "sendMessage onError");

                    }
                });
    }

    @SuppressWarnings("deprecation")
    private void sendGroupMessage(final MessageContent msg) {

        Message myMessage = Message.obtain(mTargetId, ConversationType.GROUP, msg);
        RongIM.getInstance()
		/*.getRongIMClient()
		.sendMessage(ConversationType.GROUP, mTargetId,
				msg, "", "",
				new RongIMClient.SendMessageCallback() {
					@Override
					public void onError(Integer messageId,
							ErrorCode e) {
						Log.e("lalala", "sendMessage onError");
					}

					@Override
					public void onSuccess(Integer integer) {
						Log.e("lalala", "sendMessage onSuccess");

					}
				});*/
                .sendMessage(myMessage, null, null, new IRongCallback.ISendMessageCallback() {
                    @Override
                    public void onAttached(Message message) {

                    }

                    @Override
                    public void onSuccess(Message message) {
                        Log.e("lalala", "sendMessage onError");

                    }

                    @Override
                    public void onError(Message message, ErrorCode errorCode) {
                        Log.e("lalala", "sendMessage onError");

                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
