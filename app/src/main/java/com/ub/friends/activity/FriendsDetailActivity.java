package com.ub.friends.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;
import com.kloudsync.techexcel.info.AddFriend;
import com.ub.techexcel.database.CustomerDao;
import com.ub.techexcel.service.ConnectService;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

public class FriendsDetailActivity extends Activity implements OnClickListener {

    private TextView name, tel;
    private TextView add;
    private RelativeLayout backLayout;
    public static FriendsDetailActivity instance;
    private String telphone;
    private LinearLayout all;
    private List<AddFriend> list = new ArrayList<AddFriend>();

    //传过来的好友信息
    private AddFriend friends = new AddFriend();
    private static CustomerDao customerDao;
    private TextView friendsinfo;
    private ImageView friendIcon;
    public ImageLoader imageLoader;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0x1101:
                    friends.setType("3"); //等待验证
                    friends.setSourceID(AppConfig.RongUserID);
                    Log.e("好友请求已发出",
                            friends.getSourceID() + "   " + friends.getTargetID());
                    // 将词条记录插入数据库
                    customerDao.insert(friends, false);
                    Dialog(FriendsDetailActivity.this);
                    break;
                default:
                    break;
            }

        }
    };

    private AlertDialog dialog;
    private TextView titleText;
    public void Dialog(Context context) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View windov = inflater.inflate(R.layout.frienddetail_share, null);
        windov.findViewById(R.id.smsinvate).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        dialog.dismiss();
                        if (getIntent().getBooleanExtra("isdetail", false)) {
                            FriendsDetailActivity.instance.finish();
                        }
                        finish();
                    }
                });
        dialog = new AlertDialog.Builder(context).show();
        Window dialogWindow = dialog.getWindow();
        WindowManager m = ((Activity) context).getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.8
        // p.height = (int) (d.getHeight() * 0.5);
        dialogWindow.setAttributes(p);
        dialog.setCanceledOnTouchOutside(true);// 设置点击屏幕Dialog不消失
        dialog.setContentView(windov);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friendsdetail);
        instance = this;
        friends = (AddFriend) getIntent().getSerializableExtra("addfriend");
        customerDao = new CustomerDao(this);
        initView();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("FriendsDetailActivity");
        MobclickAgent.onResume(this); // 统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("FriendsDetailActivity");
        MobclickAgent.onPause(this);
    }

    private void initView() {
        // TODO Auto-generated method stub
        name = (TextView) findViewById(R.id.name);
        tel = (TextView) findViewById(R.id.tel);
        add = (TextView) findViewById(R.id.add);
        add.setOnClickListener(this);
        friendsinfo = (TextView) findViewById(R.id.friendsinfo);
        if (friends.getType().equals("2")) { // 是好友
            add.setText(getResources().getString(R.string.Send_Message));
            friendsinfo.setText(getResources().getString(R.string.nitiate_dialogue));
        }
        titleText = (TextView) findViewById(R.id.tv_title);
        titleText.setText(R.string.details);
        friendIcon = (ImageView) findViewById(R.id.image);
        imageLoader = new ImageLoader(FriendsDetailActivity.this.getApplicationContext());
        String url = friends.getUrl();
        if (null == url || url.length() < 1) {
            friendIcon.setImageResource(R.drawable.hello);
        } else {
            imageLoader.DisplayImage(url, friendIcon);
        }
        name.setText(friends.getName());
        tel.setText(friends.getPhone());
        backLayout = (RelativeLayout) findViewById(R.id.layout_back);
        backLayout.setOnClickListener(this);
        all = (LinearLayout) findViewById(R.id.all);
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.add:
                // Intent intent = new Intent(FriendsDetailActivity.this,
                // AddVerificationActivity.class);
                // intent.putExtra("isdetail", true);
                // intent.putExtra("addfriends", list.get(0));
                // startActivity(intent);
                if (friends.getType().equals("0")) {
                    sendApplyFriend();
                } else if (friends.getType().equals("2")) {
                    AppConfig.Name = friends.getName();
                /*RongContext
						.getInstance()
						.getUserInfoCache()
						.put(friends.getTargetID(),
								new UserInfo(friends.getTargetID(), friends
										.getName(), null));*/
                    RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
                        @Override
                        public UserInfo getUserInfo(String s) {
                            return new UserInfo(
                                    friends.getTargetID(),
                                    friends.getName(),
                                    null);
                        }
                    }, true);
                    RongIM.getInstance().startPrivateChat(
                            FriendsDetailActivity.this, friends.getTargetID(),
                            friends.getName());

                }
                break;
            case R.id.layout_back:
                finish();
                break;
            default:
                break;
        }
    }

    // -------------------------------------发送好友请求-------------------------------

    private void sendApplyFriend() {
        // TODO Auto-generated method stub
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                JSONObject json = format();
                JSONObject jsonObject = ConnectService.submitDataByJson(
                        AppConfig.URL_PUBLIC + "Friend/ApplyFriend", json);
                formatReturnjson(jsonObject);
            }

        }).start(((App) getApplication()).getThreadMgr());
    }

    private void formatReturnjson(JSONObject jsonObject) {
        // TODO Auto-generated method stub
        try {
            int retCode = jsonObject.getInt("RetCode");
            switch (retCode) {
                case AppConfig.RETCODE_SUCCESS:
                    handler.obtainMessage(0x1101).sendToTarget();
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private JSONObject format() {
        // TODO Auto-generated method stub
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("TargetID", friends.getTargetID());
            jsonObject.put("UserID", friends.getUserID());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return jsonObject;

    }

}
