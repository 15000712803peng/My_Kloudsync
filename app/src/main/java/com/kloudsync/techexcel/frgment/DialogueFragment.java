package com.kloudsync.techexcel.frgment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.EventRefreshChatList;
import com.kloudsync.techexcel.bean.EventSearchChat;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.AddGroupActivity;
import com.kloudsync.techexcel.dialog.message.CourseMessage;
import com.kloudsync.techexcel.dialog.message.FriendMessage;
import com.kloudsync.techexcel.dialog.message.SpectatorMessage;
import com.kloudsync.techexcel.info.AddFriend;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.info.Friend;
import com.kloudsync.techexcel.info.GroupInfo;
import com.kloudsync.techexcel.school.SelectSchoolActivity;
import com.kloudsync.techexcel.search.ui.ChatSearchActivity;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.start.LoginGet.FriendGetListener;
import com.kloudsync.techexcel.start.LoginGet.GroupChatsGetListener;
import com.kloudsync.techexcel.start.LoginGet.GroupGetListener;
import com.kloudsync.techexcel.start.LoginGet.LoginGetListener;
import com.kloudsync.techexcel.tool.EditHelp;
import com.kloudsync.techexcel.ui.MainActivity;
import com.kloudsync.techexcel.view.ClearEditText;
import com.mining.app.zxing.MipcaActivityCapture;
import com.ub.friends.activity.AddFriendsActivity;
import com.ub.service.activity.MyKlassroomActivity;
import com.ub.service.activity.NotifyActivity;
import com.ub.service.activity.SelectCourseActivity;
import com.ub.service.activity.SelectUserActivity;
import com.ub.service.activity.WatchCourseActivity2;
import com.ub.techexcel.database.CustomerDao;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ErrorCode;
import io.rong.imlib.RongIMClient.ResultCallback;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;

public class DialogueFragment extends Fragment {

    private View view;
    private ImageView img_add;
    private ImageView img_notice;
    private ViewPager main_viewpager;
    //    private LinearLayout lin_search, lin_edit;
    private LinearLayout lin_myroom, lin_join, lin_schedule;

    private LinearLayout lin_none, lin_dialogue;
    private TextView tv_startdialogue;
    //    private TextView tv_cancel;
    private TextView tv_diatitle, tv_top_session, tv_remove;
    private TextView tv_ns;
    private TextView tv_title;

    private ClearEditText et_search;
    BroadcastReceiver broadcastReceiver;
    private DemoFragmentPagerAdapter mDemoFragmentPagerAdapter;
    private Fragment mConversationFragment = null;
    private String token = "LCuYg9Cds4RQB7yS+b8nJwHY8MlAjQf10nKLvC5zk6pMO6dfl9RoZ25B3oq+lbkyozCkup6m611eJcXgtBhBdg==";
    private String SelectId;
    private ConversationType Select_c;
    private List<Friend> userIdList;
    private boolean isFirst = true;
    private boolean isFragmentVisible = true;
    private boolean isToTop = true;
    private InputMethodManager inputManager;

    List<Conversation> list;

    public PopupWindow mPopupWindow;

    private SharedPreferences sharedPreferences;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case AppConfig.AddFriend:
                    ((MainActivity) getActivity()).DisplayRed(true);
                    break;
                default:
                    break;
            }
        }

        ;
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.conversationlist, container, false);
            initView();
            GetDMInfo();
            GetCourseBroad();
        }
//        if (null != view) {
//            ViewGroup parent = (ViewGroup) view.getParent();
//            if (null != parent) {
//                parent.removeView(view);
//            }
//        } else {
//
//        }
        isFirst = false;

        return view;
    }

    private ArrayList<Customer> clist = new ArrayList<Customer>();
    public Customer customer = null;
    private ArrayList<Customer> customers = new ArrayList<Customer>();
    private ArrayList<GroupInfo> gi_list = new ArrayList<GroupInfo>();

    private void initView() {
        lin_dialogue = (LinearLayout) view.findViewById(R.id.lin_dialogue);
        lin_none = (LinearLayout) view.findViewById(R.id.lin_none);
//        lin_search = (LinearLayout) view.findViewById(R.id.lin_search);
//        lin_edit = (LinearLayout) view.findViewById(R.id.lin_edit);
        lin_myroom = (LinearLayout) view.findViewById(R.id.lin_myroom);
        lin_join = (LinearLayout) view.findViewById(R.id.lin_join);
        lin_schedule = (LinearLayout) view.findViewById(R.id.lin_schedule);
        tv_startdialogue = (TextView) view.findViewById(R.id.tv_startdialogue);
//        tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        tv_ns = (TextView) view.findViewById(R.id.tv_ns);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        et_search = (ClearEditText) view.findViewById(R.id.et_search);
//        img_notice = (ImageView) view.findViewById(R.id.img_notice);

        tv_startdialogue.setOnClickListener(new myOnClick());
//        img_notice.setOnClickListener(new myOnClick());
//        tv_cancel.setOnClickListener(new myOnClick());
        lin_myroom.setOnClickListener(new myOnClick());
        lin_join.setOnClickListener(new myOnClick());
        lin_schedule.setOnClickListener(new myOnClick());
//		lin_search.setOnClickListener(new myOnClick());

//        editCustomers();

//        CheckMessage();
    }

    private void GetCourseBroad() {
        RefreshNotify();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                RefreshNotify();
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(getString(R.string.Receive_Course));
        getActivity().registerReceiver(broadcastReceiver, filter);

    }

    private void RefreshNotify() {
        int sum = 0;
        for (int i = 0; i < AppConfig.progressCourse.size(); i++) {
            if (!AppConfig.progressCourse.get(i).isStatus()) {
                sum++;
            }
        }
        tv_ns.setText(sum + "");
        tv_ns.setVisibility(sum == 0 ? View.GONE : View.VISIBLE);
    }

    LoginGet loginget;

    private void GetDMInfo() {
        Log.e("DialogueFragment", "get dm info");
        loginget = new LoginGet();
        loginget.setLoginGetListener(new LoginGetListener() {

            @Override
            public void getMember(ArrayList<Customer> list) {
                // TODO Auto-generated method stub
//                clist.addAll(list);
//                for (Customer c : clist) {
//                    if (c.getUserID().equals(AppConfig.UserID)) {
//                        customer = c;
//                        break;
//                    }
//                }
//                loginget.CustomerDetailRequest(getActivity(), AppConfig.UserID);
            }

            @Override
            public void getCustomer(ArrayList<Customer> list) {
                clist = new ArrayList<>();
                clist.addAll(list);
                for (Customer c : clist) {
                    if (c.getUserID().equals(AppConfig.UserID)) {
                        customer = c;
                        RongIM.getInstance().refreshUserInfoCache(
                                new UserInfo(customer.getUBAOUserID(), customer.getName(), Uri
                                        .parse(customer.getUrl())));
                        break;
                    }
                }
//				loginget.MemberRequest(getActivity(), 0);
                loginget.GetGroupsRequest(getActivity());
            }
        });

        loginget.setGroupsGetListener(new GroupChatsGetListener() {

            @Override
            public void getGroups(ArrayList<GroupInfo> list) {
                gi_list = new ArrayList<GroupInfo>();
                gi_list.addAll(list);

                for (int i = 0; i < gi_list.size(); i++) {
                    GroupInfo gi = gi_list.get(i);
                    if (gi.getGroupID().contains(getResources().getString(R.string.Classroom))) {
                        RemoveMessage(gi.getGroupID());
                    }
                }
                initName();
            }
        });
        loginget.CustomerRequest(getActivity());
    }

    private void CheckMessage() {
        if (AppConfig.isCourse) {
            StartWatchCourse(AppConfig.COURSE);
            AppConfig.isCourse = false;
        }
        if (AppConfig.isSpectator) {
            SpectatorJoin(AppConfig.SPECTATOR);
            AppConfig.isSpectator = false;
        }
    }


    private void editCustomers() {
//        inputManager = (InputMethodManager) et_search
//                .getContext().getSystemService(getActivity().INPUT_METHOD_SERVICE);
//        et_search.setHint(getActivity().getResources().getString(R.string.dialogue));
        EditHelp.hideSoftInputMethod(et_search, getActivity());
//        et_search.setCursorVisible(false);
//        et_search.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count,
//                                          int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before,
//                                      int count) {
//                if (!TextUtils.isEmpty(s)) {
//                    list = RongIMClient.getInstance().getConversationList();
//                    RongIM.getInstance().getHistoryMessages(ConversationType.PRIVATE, "123", 1231312, 10, new ResultCallback<List<Message>>() {
//                        @Override
//                        public void onSuccess(List<Message> messages) {
//
//                        }
//
//                        @Override
//                        public void onError(ErrorCode errorCode) {
//
//                        }
//                    });
//                } else {
//
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                // TODO Auto-generated method stub
//            }
//        });

    }

    private void GetGroupInfo(String id) {
        LoginGet loginget = new LoginGet();
        loginget.setGroupGetListener(new GroupGetListener() {

            @Override
            public void getGroupDetail(GroupInfo gi) {
                for (int i = 0; i < gi_list.size(); i++) {
                    GroupInfo g = gi_list.get(i);
                    if (g.getGroupID().equals(gi.getGroupID())) {
//						gi_list.remove(i);
                        gi_list.add(gi);
                        break;
                    }
                }
                String name = gi.getGroupName();
                if (null == name || name.length() < 1
                        || name.equals("null")) {
                    name = gi.getGroupTempName();
                }

                Log.e("bbbbbb",
                        gi.getGroupID() + ":" + gi.getGroupName() + ":"
                                + gi.getGroupTempName() + ":" + name);
//				RongIM.getInstance().refreshGroupInfoCache(new Group(gi.getGroupID(), name + "", null));
            }

            @Override
            public void getGDMember(ArrayList<Customer> list) {
//                clist.addAll(list);
                for (int i = 0; i < clist.size(); i++) {
                    Customer cus = clist.get(i);
                    RongIM.getInstance().refreshUserInfoCache(
                            new UserInfo(cus.getUBAOUserID(), cus.getName(), Uri
                                    .parse(cus.getUrl())));
                }
            }

        });
        loginget.GroupDetailRequest(getActivity(), id);
    }

    private void initName() {

        userIdList = new ArrayList<Friend>();
        userIdList.add(new Friend("-1", "UBAO小博士",
                "http://ub.servicewise.net.cn/SWiseWeb/UBAODoctor.png?" + new Random().nextInt(100)));

        GetDialogueList();
        SetMyDialogList();
        getPopupWindowInstance();

        /**
         *  设置接收消息的监听器。
         */
        RongIM.setOnReceiveMessageListener(new MyReceiveMessageListener());
        DeleteGroupMessage();//验证Classroom的聊天记录,有的话删除
        initFunction();
    }

    private void SetMyDialogList() {
        SetUserProvider();
        main_viewpager = (ViewPager) view.findViewById(R.id.main_viewpager);
        if (mDemoFragmentPagerAdapter == null) {
            mDemoFragmentPagerAdapter = new DemoFragmentPagerAdapter(getActivity().getSupportFragmentManager());
            main_viewpager.setAdapter(mDemoFragmentPagerAdapter);
        } else {
            mDemoFragmentPagerAdapter.notifyDataSetChanged();
        }

    }


    private void SetUserProvider() {
        RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
            @Override
            public UserInfo getUserInfo(String userId) {
                Log.e("DialogueFragment", "getUserInfo called");
                for (Friend i : userIdList) {
                    if (i.getUserId().equals(userId)) {
                        return new UserInfo(i.getUserId(), i.getUserName(), Uri.parse(i
                                .getPortraitUri()));
                    }
                }
                for (int j = 0; j < clist.size(); j++) {
                    Customer cus = clist.get(j);
                    if (cus.getUBAOUserID().equals(userId)) {
                        Uri uri = null;
                        if (cus.getUrl() != null) {
                            uri = Uri.parse(cus.getUrl());
                        }
                        return new UserInfo(cus.getUBAOUserID(), cus
                                .getName(), uri);
                    }
                }
//                if (userId.equals(customer.get())) {
//                    Uri uri = null;
//                    if (customer.getUrl() != null) {
//                        uri = Uri.parse(customer.getUrl());
//                    }
//                    return new UserInfo(customer.getUBAOUserID(), customer
//                            .getName(), uri);
//                }
                if (userId.equals(AppConfig.DEVICE_ID + AppConfig.RongUserID)) {
                    Uri uri = null;
                    return new UserInfo(AppConfig.DEVICE_ID + AppConfig.RongUserID, AppConfig.RobotName, uri);
                }

                for (int j = 0; j < customers.size(); j++) {
                    Customer cus = customers.get(j);
                    if (cus.getUBAOUserID().equals(userId)) {
                        if (cus.getUBAOUserID() != null) {
                            Uri uri = null;
                            if (cus.getUrl() != null) {
                                uri = Uri.parse(cus.getUrl());
                            }

                            return new UserInfo(cus.getUBAOUserID(), cus
                                    .getName(), uri);
                        }
                    }
                }
                return null;
            }

        }, true);

        RongIM.setGroupInfoProvider(new RongIM.GroupInfoProvider() {

            @Override
            public Group getGroupInfo(String groupId) {
                for (int i = 0; i < gi_list.size(); i++) {
                    GroupInfo gi = gi_list.get(i);
                    if (gi.getGroupID().equals(groupId)) {
                        String name = gi.getGroupName();
                        if (null == name || name.length() < 1
                                || name.equals("null")) {
                            name = gi.getGroupTempName();
                        }
                        Log.e("aaaaaa",
                                gi.getGroupID() + ":" + gi.getGroupName() + ":"
                                        + gi.getGroupTempName() + ":" + name);
                        return new Group(gi.getGroupID(), name + "", null);
                    }
                }
                return null;
            }
        }, true);


    }


    private void DeleteGroupMessage() {
        // TODO Auto-generated method stub
        list = RongIMClient.getInstance().getConversationList();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Conversation cc = list.get(i);
                if (cc.getConversationType() == ConversationType.GROUP && (cc.getTargetId().equals(AppConfig.DELETEGROUP_ID) ||
                        cc.getTargetId().contains(getResources().getString(R.string.Classroom)))) {
                    RemoveMessage(cc.getTargetId());
                }
            }
        }
    }


    private void GetDialogueList() {
//        VisibleGoneDialogue();
        list = RongIMClient.getInstance().getConversationList();

        if (list != null && list.size() > 0) {
            customers = new ArrayList<Customer>();
            for (int i = 0; i < list.size(); i++) {
                Conversation cc = list.get(i);
                for (Customer c : clist) {
                    if (cc.getTargetId().equals(c.getUBAOUserID())) {
                        RongIM.getInstance().refreshUserInfoCache(
                                new UserInfo(c.getUBAOUserID(), c.getName(), Uri
                                        .parse(c.getUrl())));
                        cc.setSenderUserName(c.getName());
                        cc.setPortraitUrl(c.getUrl());
                        break;
                    }
                }
                if (cc.getTargetId().equals("-1")) {
                    RongIMClient.getInstance().setConversationToTop(
                            cc.getConversationType(), cc.getTargetId(), true,
                            null);
                }
                if (cc.getObjectName().equals("UB:FriendMsg")) {
                    FriendMessage fm = (FriendMessage) cc.getLatestMessage();
                    String ID = !fm.getTargetID().equals(AppConfig.RongUserID) ? fm
                            .getTargetID() : fm.getSourceID();
                    Customer cus = new Customer();
                    cus.setUBAOUserID(ID);
                    customers.add(cus);

                    boolean isMyFriend = isMyfriend(fm, clist);

                    getFriendInfo(ID);
                    CustomerDao cd = new CustomerDao(getActivity());
                    AddFriend af = new AddFriend();
                    af.setSourceID(fm.getSourceID());
                    af.setType(fm.getType());
                    af.setTime(fm.getTime());
                    af.setTargetID(fm.getTargetID());
                    cd.insert(af, isMyFriend);
                } else if (cc.getConversationType() == ConversationType.PRIVATE) {
                    String ID = cc.getTargetId();
                    Customer cus = new Customer();
                    cus.setUBAOUserID(ID);
                    customers.add(cus);
                } else if (cc.getConversationType() == ConversationType.GROUP) {
                    String ID = cc.getTargetId();
                    GetGroupInfo(ID);
                }
            }
            if (customers != null && customers.size() > 0) {
                String IDS = "";
                for (int i = 0; i < customers.size(); i++) {
                    if (0 == i) {
                        IDS += customers.get(i).getUBAOUserID();
                    } else {
                        IDS += "," + customers.get(i).getUBAOUserID();
                    }
                }
                getFriendInfo(IDS);
            }
//			customers = new ArrayList<Customer>();
            /*Conversation cc = list.get(0);
            Log.e("conversation",
					cc.getConversationTitle() + ":" + cc.getDraft() + ":"
							+ cc.getLatestMessageId() + ":"
							+ cc.getObjectName() + ":" + cc.getReceivedTime()
							+ ":" + cc.getSenderUserId() + ":"
							+ cc.getSenderUserName() + ":" + cc.getSentTime()
							+ ":" + cc.getTargetId() + ":"
							+ cc.getUnreadMessageCount() + ":"
							+ cc.getConversationType() + ":"
							+ cc.getLatestMessage() + ":"
							+ cc.getSentStatus().toString() + ":"
							+ cc.getReceivedStatus().toString());*/
            // ::141:RC:TxtMsg:1451294138250:27:null:1451294138668:28:0:PRIVATE:io.rong.message.TextMessage@4278b938
//			TextMessage textMessage = (TextMessage) cc.getLatestMessage();
//			Log.e("textmessage", textMessage.getContent() + ":" + (cc.getLatestMessage() instanceof TextMessage));

            String content;
            /*if (cc.getLatestMessage() instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) cc.getLatestMessage();
				content = textMessage.getContent();
				Log.e("json", textMessage.getUserInfo() + "");
            } else if (cc.getLatestMessage() instanceof ImageMessage) {
            	content = "[图片]";
            } else if (cc.getLatestMessage() instanceof VoiceMessage) {
            	content = "[语音消息]";
            } else if (cc.getLatestMessage() instanceof CustomizeMessage) {
            	CustomizeMessage customizemessage = (CustomizeMessage) cc.getLatestMessage();
            	content = "[服务单]" ;
            } else if (cc.getLatestMessage() instanceof ContactNotificationMessage) {
                ContactNotificationMessage mContactNotificationMessage = (ContactNotificationMessage) cc.getLatestMessage();
                content = "（好友）操作通知消息" ;
            } else if (cc.getLatestMessage() instanceof ProfileNotificationMessage) {
                ProfileNotificationMessage mProfileNotificationMessage = (ProfileNotificationMessage) cc.getLatestMessage();
                Log.d("onReceived", "GroupNotificationMessage--收收收收--接收到一条【资料变更通知消息】-----" + mProfileNotificationMessage.getData() + ",getExtra:" + mProfileNotificationMessage.getExtra());
            } else if (cc.getLatestMessage() instanceof CommandNotificationMessage) {
                CommandNotificationMessage mCommandNotificationMessage = (CommandNotificationMessage) cc.getLatestMessage();
                Log.d("onReceived", "GroupNotificationMessage--收收收收--接收到一条【命令通知消息】-----" + mCommandNotificationMessage.getData() + ",getName:" + mCommandNotificationMessage.getName());
            } else if (cc.getLatestMessage() instanceof InformationNotificationMessage) {
                InformationNotificationMessage mInformationNotificationMessage = (InformationNotificationMessage) cc.getLatestMessage();
                Log.d("onReceived", "InformationNotificationMessage--收收收收--接收到一条【小灰条消息】-----" + mInformationNotificationMessage.getMessage() + ",getName:" + mInformationNotificationMessage.getExtra());

            } else if (cc.getLatestMessage() instanceof KnowledgeMessage) {
            	KnowledgeMessage aa = (KnowledgeMessage) cc.getLatestMessage();
            	content = aa.getTitle();
            }	else if (cc.getLatestMessage() instanceof SystemMessage) {
            	SystemMessage aa = (SystemMessage) cc.getLatestMessage();
            	content = "优葆养身推荐";
            }	else if (cc.getLatestMessage() instanceof FriendMessage) {
            	FriendMessage aa = (FriendMessage) cc.getLatestMessage();
                if(aa.getType().equals("1")){
                	content = "您有一条好友添加信息";
                }else if(aa.getType().equals("2")){
                	content = "恭喜你，好友申请已通过";
                }
            }*/
            /*RongIM.getInstance().startConversation(getActivity(),
              Conversation.ConversationType.PRIVATE, cc.getTargetId(), "聊天标题");*/

        }

    }

    private void VisibleGoneDialogue() {
        list = RongIMClient.getInstance().getConversationList();
        if (list != null && list.size() > 0) {
            lin_none.setVisibility(View.GONE);
            lin_dialogue.setVisibility(View.VISIBLE);
        } else {
            lin_none.setVisibility(View.VISIBLE);
            lin_dialogue.setVisibility(View.GONE);
        }

    }


    private class MyReceiveMessageListener implements RongIMClient.OnReceiveMessageListener {

        /**
         * 收到消息的处理。
         *
         * @param message 收到的消息实体。
         * @param left    剩余未拉取消息数目。
         * @return 收到消息是否处理完成，true 表示走自已的处理方式，false 走融云默认处理方式。
         */
        @Override
        public boolean onReceived(Message message, int left) {
            VisibleGoneDialogue();
            /*list = RongIMClient.getInstance().getConversationList();
            if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Conversation cc = list.get(i);
					if(cc.getTargetId().equals("-1")){
						RongIMClient.getInstance().setConversationToTop(cc.getConversationType(), cc.getTargetId(), true,null);
					}
				}
				Conversation cc = list.get(0);
				Log.e("conversation",
						cc.getConversationTitle() + ":" + cc.getDraft() + ":"
								+ cc.getLatestMessageId() + ":"
								+ cc.getObjectName() + ":" + cc.getReceivedTime()
								+ ":" + cc.getSenderUserId() + ":"
								+ cc.getSenderUserName() + ":" + cc.getSentTime()
								+ ":" + cc.getTargetId() + ":"
								+ cc.getUnreadMessageCount() + ":"
								+ cc.getConversationType() + ":"
								+ cc.getLatestMessage() + ":" + cc.toString());
			}*/
            //开发者根据自己需求自行处理
            if (message.getObjectName().equals("UB:FriendMsg")) {
                FriendMessage cc = (FriendMessage) message.getContent();
                getFriendInfo(!cc.getTargetID().equals(AppConfig.RongUserID) ? cc.getTargetID() : cc.getSourceID());
                CustomerDao cd = new CustomerDao(getActivity());
                AddFriend af = new AddFriend();

                boolean isMyFriend = isMyfriend(cc, clist);

                af.setSourceID(cc.getSourceID());
                af.setType(cc.getType());
                af.setTime(cc.getTime());
                af.setTargetID(cc.getTargetID());
                cd.insert(af, isMyFriend);
                if (cc.getType().equals("1")) {
                    android.os.Message msg = new android.os.Message();
                    msg.what = AppConfig.AddFriend;
                    msg.obj = "连接错误";
                    handler.sendMessage(msg);
                }
            }
            /*if (message.getObjectName().equals("UB:MeetingMsg")) {
                CourseMessage cc = (CourseMessage) message.getContent();
                Log.e("onReceived", "CourseMessage " + cc.getMeetingId() + "  " + cc.getAttachmentUrl() +
                        "  " + cc.getRongCloudUserID() + " : " + getActivity());
                if(getActivity() != null) {
                    StartWatchCourse(cc);
                }else{
                    AppConfig.isCourse = true;
                    AppConfig.COURSE = cc;
                }
                int[] msg = new int[1];
                msg[0] = message.getMessageId();
                DeleteMessage(msg);
            }
            if (message.getObjectName().equals("UB:AuditorMsg")) {
                SpectatorMessage sm = (SpectatorMessage) message.getContent();
                for (int i = 0; i < sm.getCollection_dataArray().size(); i++) {
                    Spectator sp = sm.getCollection_dataArray().get(i);
                    Log.e("SpectatorMessage", sp.getName() + ":" + sp.getAvatarUrl()
                            + ":" + sp.getIdentity() + ":" + sp.getIdentityType());
                }
                if(getActivity() != null) {
                    SpectatorJoin(sm);
                }else{
                    AppConfig.isSpectator = true;
                    AppConfig.SPECTATOR = sm;
                }

                int[] msg = new int[1];
                msg[0] = message.getMessageId();
                DeleteMessage(msg);

            }*/
            /*if (message.getObjectName().equals("UB:ItemIDMsg")) {
                ChangeItemMessage cc = (ChangeItemMessage) message.getContent();
                Log.e("onReceived", "ChangeItemMessage " + cc.getItemId());
                int[] msg = new int[1];
                msg[0] = message.getMessageId();
                DeleteMessage(msg);

            }*/

            Log.e("message", message.getObjectName() + ":" + message.getTargetId());
            if (message.getConversationType() == ConversationType.GROUP) {
                if (message.getTargetId().contains(getResources().getString(R.string.Classroom))) {
                    int[] msg = new int[1];
                    msg[0] = message.getMessageId();
//                    DeleteMessage(msg);
                    RemoveMessage(message.getTargetId());
                    Intent intent = new Intent();
                    intent.setAction("com.cn.getGroupbroadcastReceiver");
                    getActivity().sendBroadcast(intent);
                }

                if (message.getObjectName().equals("UB:GroupMsg")) {
                    GetGroupInfo(message.getTargetId());
                }
            }

            //收到消息时静音
            /*if (message.getObjectName().equals("UB:ItemIDMsg")) {

                RongIMClient.getInstance().setConversationNotificationStatus(ConversationType.PRIVATE,
                        message.getTargetId(),
                        Conversation.ConversationNotificationStatus.DO_NOT_DISTURB,
                        new ResultCallback<Conversation.ConversationNotificationStatus>() {
                    @Override
                    public void onSuccess(Conversation.ConversationNotificationStatus conversationNotificationStatus) {

                    }

                    @Override
                    public void onError(ErrorCode errorCode) {

                    }
                });
                return true;
            }*/

            return false;
        }
    }

    private void SpectatorJoin(SpectatorMessage sm) {
        Intent intent = new Intent(getActivity(), WatchCourseActivity2.class);
        intent.putExtra("isPresenter", sm.getCurrent_presenter().equals("1") ? true : false);
        intent.putExtra("dataArray", (Serializable) sm.getCollection_dataArray());
        intent.putExtra("url", sm.getAuditor_attachmentUrl());
        intent.putExtra("meetingId", sm.getAuditor_meetingID());
        intent.putExtra("identity", 3);
        startActivity(intent);
    }

    private void StartWatchCourse(CourseMessage cc) {
        Intent intent = new Intent(getActivity(), WatchCourseActivity2.class);
        intent.putExtra("url", cc.getAttachmentUrl());
        intent.putExtra("meetingId", cc.getMeetingId());
        intent.putExtra("CustomerRongCloudID", cc.getRongCloudUserID());
        intent.putExtra("identity", 2);
        startActivity(intent);
    }

    private void DeleteMessage(int[] msg) {
        RongIM.getInstance().deleteMessages(msg, new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                Log.e("deleteMessages", "onSuccess");

            }

            @Override
            public void onError(ErrorCode errorCode) {
                Log.e("deleteMessages", "onError");

            }
        });
    }

    private void RemoveMessage(String targetid) {
        RongIM.getInstance().removeConversation(ConversationType.GROUP, targetid, new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                Log.e("removeMessages", "onSuccess");

            }

            @Override
            public void onError(ErrorCode errorCode) {
                Log.e("removeMessages", "onError");

            }
        });
        /*RongIM.getInstance().getLatestMessages(ConversationType.GROUP, targetid, 212, new ResultCallback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> messages) {
                List<Message> List = messages;
                for (Message message : List) {
                    MessageContent m=  message.getContent();
                    if(m instanceof TextMessage){
                        TextMessage textMessage=(TextMessage) m;//文本消息
                    }
                }
            }

            @Override
            public void onError(ErrorCode errorCode) {

            }
        });*/
    }

    private void getFriendInfo(String targetID) {
        loginget = new LoginGet();
        loginget.setFriendGetListener(new FriendGetListener() {

            @Override
            public void getFriends(ArrayList<Customer> cus_list) {
                // TODO Auto-generated method stub
                customers.addAll(cus_list);
                SetMyDialogList();
            }
        });
        loginget.FriendsRequest(getActivity(), targetID);

    }


    private void GetSchoolInfo() {
        sharedPreferences = getActivity().getSharedPreferences(AppConfig.LOGININFO,
                getActivity().MODE_PRIVATE);
        int SchoolId = sharedPreferences.getInt("SchoolID", -1);
        String schoolName = sharedPreferences.getString("SchoolName", null);
        Log.e("duang", SchoolId + ":" + AppConfig.SchoolID);
        /*if (-1 == SchoolId || SchoolId == AppConfig.SchoolID) {
            tv_title.setText(getResources().getString(R.string.My_School));
        } else {
            tv_title.setText(schoolName);
        }*/
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        // TODO Auto-generated method stub
        super.setUserVisibleHint(isVisibleToUser);
        isFragmentVisible = isVisibleToUser;
        if (isVisibleToUser && !isFirst) {
            //解决验证切换语言时列表没刷新
            if (lin_dialogue != null && null == customer) {
                initView();
            } else if (lin_dialogue == null) {
                initView();
            }
        }
        RefreshInfo();

    }

    private void initFunction() {
//		SetMyDialogList();

//        lin_search = (LinearLayout) view.findViewById(R.id.lin_search);
        img_add = (ImageView) view.findViewById(R.id.img_add);

//        lin_search.setOnClickListener(new myOnClick());
        img_add.setOnClickListener(new myOnClick());
    }

    protected class myOnClick implements OnClickListener {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.img_add:
                    ShowPP(v);
                    break;
                /*case R.id.lin_search:
//                    GetSearch();
                    break;*/
                /*case R.id.tv_cancel:
                    CancelSearch();
                    break;*/
                case R.id.img_notice:
                    GoToNotice();
                    break;
                case R.id.tv_startdialogue:
                    Intent i = new Intent(getActivity(), SelectUserActivity.class);
                    i.putExtra("isDialogue", true);
                    startActivity(i);
                    break;
                case R.id.lin_myroom:
                    GoToMyRoom();
                    break;
                case R.id.lin_join:
                    GoToJoin();
                    break;
                case R.id.lin_schedule:
                    GoToSchedule();
                    break;
                default:
                    break;
            }
        }


    }

    private void ShowPP(View v) {
        sharedPreferences = getActivity().getSharedPreferences(AppConfig.LOGININFO,
                getActivity().MODE_PRIVATE);
        mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
        mPopupWindow.showAsDropDown(v);
        int SchoolId = sharedPreferences.getInt("SchoolID", -1);
        String schoolName = sharedPreferences.getString("SchoolName", null);
        if (-1 == SchoolId || SchoolId == AppConfig.SchoolID) {
            tv_seschool.setText(getResources().getString(R.string.My_School));
        } else {
            tv_seschool.setText(schoolName);
        }
    }

    private void GoToSchedule() {
        startActivity(new Intent(getActivity(), SelectCourseActivity.class));
        getActivity().overridePendingTransition(R.anim.tran_in5, R.anim.tran_out5);
    }

    private void GoToJoin() {
        GoToNotice();
    }

    private void GoToMyRoom() {
        startActivity(new Intent(getActivity(), MyKlassroomActivity.class));
        getActivity().overridePendingTransition(R.anim.tran_in5, R.anim.tran_out5);
    }


    private void GoToNotice() {
        startActivity(new Intent(getActivity(), NotifyActivity.class));
        getActivity().overridePendingTransition(R.anim.tran_in5, R.anim.tran_out5);
    }

    /*private void GetSearch() {
        et_search.setFocusable(true);
        et_search.setFocusableInTouchMode(true);
        et_search.requestFocus();
        inputManager.showSoftInput(et_search, 0);
        lin_search.setVisibility(View.GONE);
        lin_edit.setVisibility(View.VISIBLE);
    }*/

    /*private void CancelSearch() {
        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus()
                        .getApplicationWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        et_search.setText("");
        lin_search.setVisibility(View.VISIBLE);
        lin_edit.setVisibility(View.GONE);
    }*/

    @SuppressWarnings("deprecation")
    private void sendMessage(final MessageContent msg) {
        if (MainActivity.mRongIMClient != null) {
            MainActivity.mRongIMClient.sendMessage(ConversationType.SYSTEM, "", msg, null, null, new RongIMClient.SendMessageCallback() {
                @Override
                public void onError(Integer integer, ErrorCode errorCode) {
                    Log.d("sendMessage", "----发发发发发--发送消息失败----ErrorCode----" + errorCode.getValue());
                }

                @Override
                public void onSuccess(Integer integer) {
                    RongIM.getInstance()
                            .getRongIMClient()
                            .sendMessage(ConversationType.PRIVATE, "8",
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
                                    });
                }
            });


        } else {
            Toast.makeText(getActivity(), "请先连接。。。", Toast.LENGTH_LONG).show();
        }

    }


    /*
     * 获取PopupWindow实例
     */
    private void getPopupWindowInstance() {
        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }


    private RelativeLayout rl_school;
    private TextView tv_seschool;

    /*
     * 创建PopupWindow
     */
    @SuppressWarnings("deprecation")
    private void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        // View popupWindow = layoutInflater.inflate(R.layout.popup_window3,
        // null);
        View popupWindow = layoutInflater
                .inflate(R.layout.popup_dialog, null);
        // View popupWindow = layoutInflater.inflate(R.layout.popup_window2,
        // null);
        LinearLayout lin_todialog = (LinearLayout) popupWindow
                .findViewById(R.id.lin_todialog);
        LinearLayout lin_groupchat = (LinearLayout) popupWindow
                .findViewById(R.id.lin_groupchat);
        LinearLayout lin_todialogs = (LinearLayout) popupWindow
                .findViewById(R.id.lin_todialogs);
        LinearLayout lin_scan = (LinearLayout) popupWindow
                .findViewById(R.id.lin_scan);
        rl_school = (RelativeLayout) popupWindow
                .findViewById(R.id.rl_school);
        tv_seschool = (TextView) popupWindow
                .findViewById(R.id.tv_seschool);
        /*LinearLayout lin_addfriend = (LinearLayout) popupWindow
                .findViewById(R.id.lin_addfriend);*/

        lin_todialog.setOnClickListener(new MypopClick());
        lin_groupchat.setOnClickListener(new MypopClick());
        lin_todialogs.setOnClickListener(new MypopClick());
        lin_scan.setOnClickListener(new MypopClick());
        rl_school.setOnClickListener(new MypopClick());
//		lin_addfriend.setOnClickListener(new MypopClick());

        WindowManager window = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        // 创建一个PopupWindow
        // 参数1：contentView 指定PopupWindow的内容
        // 参数2：width 指定PopupWindow的width
        // 参数3：height 指定PopupWindow的height
        mPopupWindow = new PopupWindow(popupWindow, width * 2 / 5, height / 3,
                false);

        // getWindowManager().getDefaultDisplay().getWidth();
        // getWindowManager().getDefaultDisplay().getHeight();
        mPopupWindow.getWidth();
        mPopupWindow.getHeight();

        // 使其聚焦
        mPopupWindow.setFocusable(true);
        // 设置允许在外点击消失
        mPopupWindow.setOutsideTouchable(true);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }

    private class MypopClick implements OnClickListener {

        @SuppressWarnings("deprecation")
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.lin_todialog:
                    Intent i = new Intent(getActivity(), SelectUserActivity.class);
                    i.putExtra("isDialogue", true);
                    startActivity(i);
                    mPopupWindow.dismiss();
                    break;
                case R.id.lin_groupchat:
                    i = new Intent(getActivity(), AddGroupActivity.class);
                    startActivity(i);
                    mPopupWindow.dismiss();
                    break;
                case R.id.lin_todialogs:
                    CreateNewUM(1);
                    mPopupWindow.dismiss();
                    break;
                case R.id.lin_scan:
                    i = new Intent(getActivity(), MipcaActivityCapture.class);
                    startActivity(i);
                    mPopupWindow.dismiss();
                    break;
                case R.id.rl_school:
                    i = new Intent(getActivity(), SelectSchoolActivity.class);
                    startActivity(i);
                    mPopupWindow.dismiss();
                    break;
			/*case lin_addfriend:
				CreateNewUM(2);
				mPopupWindow.dismiss();
				break;*/

                default:

            }

        }

    }

    public void CreateNewUM(int type) {
        Intent i = new Intent(getActivity(), AddFriendsActivity.class);
        i.putExtra("type", type);
        startActivity(i);
    }


    private class DemoFragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

        public DemoFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = null;
            Log.e("pager adapter","get item");
            switch (i) {
                case 0:
                    //TODO
                    if (mConversationFragment == null) {
//	                        ConversationListFragment listFragment = ConversationListFragment.getInstance();
                        ConversationListFragment listFragment = new ConversationListFragment();
	                        /*listFragment.setAdapter(new ConversationListAdapterEx(RongContext
	                				.getInstance()));*/
                        Uri uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
                                .appendPath("conversationlist")
                                .appendQueryParameter(ConversationType.PRIVATE.getName(), "false") //设置私聊会话是否聚合显示
                                .appendQueryParameter(ConversationType.GROUP.getName(), "false")//群组
                                .appendQueryParameter(ConversationType.DISCUSSION.getName(), "false")//讨论组
                                .appendQueryParameter(ConversationType.APP_PUBLIC_SERVICE.getName(), "false")//应用公众服务。
                                .appendQueryParameter(ConversationType.PUBLIC_SERVICE.getName(), "false")//公共服务号
                                .appendQueryParameter(ConversationType.SYSTEM.getName(), "false")//系统
                                .build();
                        listFragment.setUri(uri);
                        fragment = listFragment;
                    } else {
                        fragment = mConversationFragment;

//	                        fragment = new TestFragment();
                    }

                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 1;
        }
    }

    /**
     * 判断是否为好友
     *
     * @param fm
     * @param clist2
     * @return
     */
    private boolean isMyfriend(FriendMessage fm, ArrayList<Customer> clist2) {
        String targetId = fm.getSourceID().equals(AppConfig.RongUserID) ? fm
                .getTargetID() : fm.getSourceID();
        if (clist2 != null && clist2.size() > 0) {
            for (Customer customer : clist2) {
                if (customer.getUBAOUserID().equals(targetId)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void RefreshInfo() {
//        if (AppConfig.isUpdateDialogue) {
//            initView();
//        }
        if (AppConfig.isDeleteGroup) {
            DeleteGroupMessage();
        }
        if (AppConfig.isChangeGroupName) {
            RongIM.getInstance().refreshGroupInfoCache(AppConfig.UPDATEGROUP);
            initView();
            GetGroupInfo(AppConfig.UPDATEGROUP.getId());
        }
			/*if(AppConfig.isDeletFRIEND){
				RongIMClient.getInstance().removeConversation(ConversationType.PRIVATE, AppConfig.DELETEFRIEND_ID, new ResultCallback<Boolean>() {

					@Override
					public void onError(ErrorCode arg0) {
						// TODO Auto-generated method stub
						
					``}

					@Override
					public void onSuccess(Boolean arg0) {
						initView();
					}
				});
			}*/
        AppConfig.isUpdateDialogue = false;
        AppConfig.isDeleteGroup = false;
        AppConfig.isChangeGroupName = false;
//			AppConfig.isDeletFRIEND = false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshList(EventRefreshChatList eventRefreshChatList) {
        refresh();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        if (isFragmentVisible) {
            RefreshInfo();
        }

//        if (tv_title != null) {
//            GetSchoolInfo();
//        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void goToSearch(EventSearchChat eventSearchChat) {
        Intent intent = new Intent(getActivity(), ChatSearchActivity.class);
        Bundle bundle = new Bundle();
        if (clist != null && clist.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Conversation cc = list.get(i);
                for (Customer c : clist) {
                    if (cc.getTargetId().equals(c.getUBAOUserID())) {
                        RongIM.getInstance().refreshUserInfoCache(
                                new UserInfo(c.getUBAOUserID(), c.getName(), Uri
                                        .parse(c.getUrl())));
                        cc.setSenderUserName(c.getName());
                        cc.setPortraitUrl(c.getUrl());
                        break;
                    }
                }
            }
        }
        bundle.putSerializable("conversation_list", (Serializable) list);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    public void refresh() {
//        if(null != view){
//            mDemoFragmentPagerAdapter = new DemoFragmentPagerAdapter(getActivity().getSupportFragmentManager());
//            main_viewpager.setAdapter(mDemoFragmentPagerAdapter);
//        }
        GetDMInfo();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (broadcastReceiver != null && getActivity() != null) {
            getActivity().unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }
}
