package com.ub.friends.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.info.AddFriend;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.mm.sdk.platformtools.Util;
import com.ub.techexcel.service.ConnectService;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddFriendsActivity extends Activity implements OnClickListener,
        IWXAPIEventHandler {
    private LinearLayout backll;
    private TextView inviteFriends;
    private EditText editText;
    private List<AddFriend> list = new ArrayList<AddFriend>();
    private IWXAPI api;
    private LinearLayout ll1, ll2, all, searchfriends;
    private TextView searchphone;
    private InputMethodManager inputManager;
    // 1是搜学生，2是搜老师
    private int type = 2;
    private TextView tvTitle;
    private RelativeLayout backLayout;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppConfig.LOAD_FINISH:
                    if (list.size() > 0) {
                        Intent intent = new Intent(AddFriendsActivity.this,
                                FriendsDetailActivity.class);
                        intent.putExtra("addfriend", list.get(0));
                        startActivity(intent);
                    } else {
                        // 显示没有此好友信息
                        Dialog2(AddFriendsActivity.this);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public void Dialog2(Context context) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View windov = inflater.inflate(R.layout.isfriend_share, null);
        windov.findViewById(R.id.smsinvate).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        dialog.dismiss();
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
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.addfriends);
        initView();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("AddFriendsActivity");
        MobclickAgent.onResume(this); // 统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("AddFriendsActivity");
        MobclickAgent.onPause(this);
    }

    private void initView() {
        // TODO Auto-generated method stub
        editText = (EditText) findViewById(R.id.searchfriends);
        inviteFriends = (TextView) findViewById(R.id.invatefriends);
        inviteFriends.setOnClickListener(this);
        type = getIntent().getIntExtra("type", 2);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText(getString(R.string.addcontact));
        if (type == 2) {
            editText.setHint(getString(R.string.addteacher1));
        } else if (type == 1) {
            editText.setHint(getString(R.string.addstudent1));
        }

        backLayout = findViewById(R.id.layout_back);
        backLayout.setOnClickListener(this);
        api = WXAPIFactory.createWXAPI(AddFriendsActivity.this,
                AppConfig.WX_APP_ID, true);
        api.registerApp(AppConfig.WX_APP_ID);
        api.handleIntent(getIntent(), this);

        ll1 = (LinearLayout) findViewById(R.id.ll1);
        ll2 = (LinearLayout) findViewById(R.id.ll2);
        all = (LinearLayout) findViewById(R.id.all);
        searchfriends = (LinearLayout) findViewById(R.id.searchfriendsll);
        searchfriends.setOnClickListener(this);
        ll2.setVisibility(View.GONE);
        ll1.setVisibility(View.GONE);
        all.setVisibility(View.VISIBLE);
        searchphone = (TextView) findViewById(R.id.searchphone);

        editText = (EditText) findViewById(R.id.searchfriends);
        inputManager = (InputMethodManager) editText.getContext()
                .getSystemService(this.INPUT_METHOD_SERVICE);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
                if (arg0.toString().length() > 0) {
                    showMycontact(arg0.toString());
                } else {
                    showInvateFriends();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    /**
     * 显示邀请联系人
     *
     * @param
     */
    private void showInvateFriends() {
        // TODO Auto-generated method stub
        ll2.setVisibility(View.GONE);
        ll1.setVisibility(View.GONE);
    }

    /**
     * 搜索好友
     */
    private void showMycontact(String string) {
        ll2.setVisibility(View.GONE);
        ll1.setVisibility(View.VISIBLE);
        searchphone.setText(string);

        // list.clear();
        // for (AddFriend addFriend : sourcelist) {
        // if (addFriend.getPhone().contains(string)) {
        // list.add(addFriend);
        // }
        // }
        // myAddFriendsAdapter = new
        // MyAddFriendsAdapter(getApplicationContext(),
        // list);
        // myAddFriendsAdapter
        // .setOnHealthChangedListener(new OnHealthChangedListener() { // 添加
        // @Override
        // public void onTouchingLetterChanged(int position) {
        // Intent intent = new Intent(AddFriendsActivity.this,
        // AddVerificationActivity.class);
        // startActivity(intent);
        // }
        // });
        // listView.setAdapter(myAddFriendsAdapter);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.backll:
                inputManager.hideSoftInputFromWindow(getCurrentFocus()
                                .getApplicationWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                finish();
                break;
            case R.id.invatefriends: // 邀请好友
                inputManager.hideSoftInputFromWindow(getCurrentFocus()
                                .getApplicationWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                Dialog(AddFriendsActivity.this);
                break;
            case R.id.searchfriendsll:
                inputManager.hideSoftInputFromWindow(getCurrentFocus()
                                .getApplicationWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                searchFriends(searchphone.getText().toString());
                break;
            case R.id.layout_back:
                finish();
                break;
            default:
                break;
        }
    }

    private boolean isMobileNO(String mobiles) {
        Pattern p = Pattern
                .compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    // -----------------搜索好友------------------
    private void searchFriends(final String tel) {
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = ConnectService
                        .getIncidentbyHttpGet(AppConfig.URL_PUBLIC
                                + "Friend/SearchFriend?Phone=" + tel + "&type=" + type);
                List<AddFriend> list2 = formatFrinds(jsonObject);
                list.clear();
                list.addAll(list2);
                handler.obtainMessage(AppConfig.LOAD_FINISH).sendToTarget();
            }
        }).start(((App) getApplication()).getThreadMgr());
    }

    private List<AddFriend> formatFrinds(JSONObject jsonObject) {
        Log.e("搜索结果", jsonObject.toString());
        List<AddFriend> list = new ArrayList<AddFriend>();
        try {
            int retCode = jsonObject.getInt("RetCode");
            switch (retCode) {
                case AppConfig.RETCODE_SUCCESS:
                    JSONArray retData = jsonObject.getJSONArray("RetData");
                    list = new ArrayList<AddFriend>();
                    for (int i = 0; i < retData.length(); i++) {
                        JSONObject object = retData.getJSONObject(i);
                        AddFriend addfriend = new AddFriend();
                        addfriend.setName(object.getString("Name"));
                        addfriend.setUserID(object.getInt("UserID") + "");
                        String telephone = object.getString("Phone");
                        addfriend.setPhone(telephone);
                        addfriend.setTargetID(object.getString("RongCloudUserID"));
                        boolean IsFriend = object.getBoolean("IsFriend");
                        addfriend.setUrl(object.getString("AvatarUrl"));// 搜索到好友的头像
                        if (IsFriend) { // 是好友
                            addfriend.setType("2");
                        } else { // 不是好友
                            addfriend.setType("0");
                        }
                        list.add(addfriend);
                    }
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }


    // -----------------------微信分享----------------------
    private AlertDialog dialog;
    public void Dialog(Context context) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View windov = inflater.inflate(R.layout.addfriend_share, null);
        windov.findViewById(R.id.wxinvate).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub
                        weixinshare();
                        dialog.dismiss();
                    }
                });
        windov.findViewById(R.id.smsinvate).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub
                        sendSMS();
                        dialog.dismiss();
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

    /**
     *
     */
    private void weixinshare() {
        if (isWXAppInstalledAndSupported(api)) {
            WXWebpageObject webpage = new WXWebpageObject();
            webpage.webpageUrl = "http://ub.servicewise.net.cn/app/app.htm";
            WXMediaMessage msg = new WXMediaMessage(webpage);
            msg.title = "【优葆】来自xxx的邀请";
            msg.description = "1,请点击此框跳转优葆健康app下载界面。2，登入注册领取优葆积分。";
            Bitmap thumb = BitmapFactory.decodeResource(getResources(),
                    R.drawable.appshare);
            msg.thumbData = Util.bmpToByteArray(thumb, true);
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = buildTransaction("webpage");
            req.message = msg;
            req.scene = SendMessageToWX.Req.WXSceneSession;
            api.sendReq(req);
        } else {
            Toast.makeText(getApplicationContext(), "微信客户端未安装，请确认",
                    Toast.LENGTH_LONG).show();
        }
    }

    private boolean isWXAppInstalledAndSupported(IWXAPI api) {
        return api.isWXAppInstalled() && api.isWXAppSupportAPI();
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis())
                : type + System.currentTimeMillis();
    }

    private void sendSMS() {
        String smsBody = "你的朋友邀请您一起加入优葆健康平台"
                + "（http://ub.servicewise.net.cn/app/app.htm）"
                + "现在注册并完成认证，可与朋友共同领取积分币奖励！优葆健康期待您的加入，我们共同成长!";
        Uri smsToUri = Uri.parse("smsto:");
        Intent sendIntent = new Intent(Intent.ACTION_VIEW, smsToUri);
        // sendIntent.putExtra("address", "123456"); //分享到指定联系人
        sendIntent.putExtra("sms_body", smsBody);
        sendIntent.setType("vnd.android-dir/mms-sms");
        startActivityForResult(sendIntent, 1002);
    }

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq arg0) {
        // TODO Auto-generated method stub
    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    @Override
    public void onResp(BaseResp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                Toast.makeText(AddFriendsActivity.this, "分享成功", Toast.LENGTH_LONG)
                        .show();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                Toast.makeText(AddFriendsActivity.this, "分享被取消", Toast.LENGTH_LONG)
                        .show();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                Toast.makeText(AddFriendsActivity.this, "分享失败", Toast.LENGTH_LONG)
                        .show();
                break;
            default:
                Toast.makeText(AddFriendsActivity.this, "分享失败", Toast.LENGTH_LONG)
                        .show();
                break;
        }
    }

}
