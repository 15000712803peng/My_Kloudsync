package com.ub.service.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.message.CustomizeMessage;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.start.LoginGet.DetailGetListener;
import com.ub.techexcel.bean.ServiceBean;
import com.ub.techexcel.service.ConnectService;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

public class AddServiceFinish extends Activity implements OnClickListener {

    private TextView lastStep, cancel, sendServicebnt;
    private TextView username, tel, address, description, beizhu;
    private TextView soucontent;
    private TextView title;
    private int conversationtype;
    private String mTargetId;
    private ServiceBean mServiceBean;
    private int newcourseid;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppConfig.LOAD_FINISH:
                    Toast.makeText(getApplicationContext(), getString(R.string.success),
                            Toast.LENGTH_LONG).show();
                    AppConfig.isUpdateDialogue = true;
                    //给学生发送课程单
                    sendServiceToUser();
                    AppConfig.tempServiceBean=new ServiceBean();
                    finish();
                    if (SelectCourseActivity.instance != null) {
                        SelectCourseActivity.instance.finish();
                    }
                    Intent ii = new Intent();
                    ii.setAction("com.ubao.techexcel.frgment");
                    sendBroadcast(ii);
                    break;
                default:
                    break;
            }
        }

        /**
         * 給用戶发送课程
         */
        private void sendServiceToUser() {
            // TODO Auto-generated method stub
            mServiceBean.setId(newcourseid);
            if (conversationtype == 1) {
                Log.e("service1",
                        mServiceBean.getId() + ":" + mServiceBean.getName() + ":" + mServiceBean.getConcernName()
                                + "  " + mServiceBean.getCustomer().getUBAOUserID());
                CustomizeMessage msg = new CustomizeMessage(mServiceBean.getId() + "",
                        mServiceBean.getName(), mServiceBean.getConcernName(), "800");
                io.rong.imlib.model.Message myMessage = io.rong.imlib.model.Message.obtain(mServiceBean.getCustomer().getUBAOUserID(),
                        Conversation.ConversationType.PRIVATE, msg);
                RongIM.getInstance()
                        /*.getRongIMClient()
                        .sendMessage(Conversation.ConversationType.PRIVATE,
                                mServiceBean.getCustomer().getUBAOUserID(), msg, "", "",
                                new RongIMClient.SendMessageCallback() {
                                    @Override
                                    public void onError(Integer messageId,
                                                        RongIMClient.ErrorCode e) {
                                        Log.e("发送失败", "sendMessage onError");
                                    }

                                    @Override
                                    public void onSuccess(Integer integer) {
                                        Log.e("发送成功", "sendMessage onSuccess");
                                    }

                                });*/
                            .sendMessage(myMessage, null, null, new IRongCallback.ISendMessageCallback() {
                                @Override
                                public void onAttached(io.rong.imlib.model.Message message) {

                                }

                                @Override
                                public void onSuccess(io.rong.imlib.model.Message message) {

                                }

                                @Override
                                public void onError(io.rong.imlib.model.Message message, RongIMClient.ErrorCode errorCode) {

                                }
                            });
            } else if (conversationtype == 2) {
                Log.e("service1",
                        mServiceBean.getId() + ":" + mServiceBean.getName() + ":" + mServiceBean.getConcernName()
                                + "ddc" + mServiceBean.getCustomer().getUBAOUserID());
                CustomizeMessage msg = new CustomizeMessage(mServiceBean.getId() + "",
                        mServiceBean.getName(), mServiceBean.getConcernName(), "800");
                RongIM.getInstance()
                        .getRongIMClient()
                        .sendMessage(Conversation.ConversationType.GROUP,
                                mTargetId, msg, "", "",
                                new RongIMClient.SendMessageCallback() {
                                    @Override
                                    public void onError(Integer messageId,
                                                        RongIMClient.ErrorCode e) {
                                        Log.e("发送失败", "sendMessage onError");
                                    }

                                    @Override
                                    public void onSuccess(Integer integer) {
                                        Log.e("发送成功", "sendMessage onSuccess");
                                    }
                                });
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.addservicefinish);
        conversationtype = getIntent().getIntExtra("conversationtype", 1);
        mTargetId = getIntent().getStringExtra("mTargetId");
        mServiceBean = (ServiceBean) getIntent().getSerializableExtra("servicebean");
        initView();
        LoginGet loginGet = new LoginGet();
        loginGet.setDetailGetListener(new DetailGetListener() {

            @Override
            public void getUser(Customer user) {
                // TODO Auto-generated method stub
                mServiceBean.setCustomer(user);
                address.setText(user.getAddress());
            }

            @Override
            public void getMember(Customer member) {
                // TODO Auto-generated method stub
            }

        });
        loginGet.CustomerDetailRequest(getApplicationContext(),
                mServiceBean.getCustomer().getUserID() + "");
    }

    private void initView() {

        lastStep = (TextView) findViewById(R.id.laststep);
        cancel = (TextView) findViewById(R.id.cancel);
        sendServicebnt = (TextView) findViewById(R.id.sendservicebnt);
        lastStep.setOnClickListener(this);
        cancel.setOnClickListener(this);
        sendServicebnt.setOnClickListener(this);

        username = (TextView) findViewById(R.id.username);
        tel = (TextView) findViewById(R.id.tel);
        address = (TextView) findViewById(R.id.address);
        description = (TextView) findViewById(R.id.description);
        beizhu = (TextView) findViewById(R.id.beizhu);
        soucontent = (TextView) findViewById(R.id.soucontent);
        title = (TextView) findViewById(R.id.title);

        username.setText(mServiceBean.getCustomer().getName());
        tel.setText(mServiceBean.getCustomer().getPhone());
        address.setText(mServiceBean.getCustomer()
                .getCurrentPosition());
        description.setText(mServiceBean.getDescription());
        beizhu.setText(mServiceBean.getComment());

        // 符合的方案 关注点的名称
        soucontent.setText(mServiceBean.getConcernName());

    }


    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("AddServiceFinish");
        MobclickAgent.onResume(this); // 统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("AddServiceFinish");
        MobclickAgent.onPause(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.laststep:
                finish();
                break;
            case R.id.cancel: //取消新建
                AppConfig.tempServiceBean=new ServiceBean();
                finish();
                if (SelectCourseActivity.instance != null) {
                    SelectCourseActivity.instance.finish();
                }
                if (AppConfig.ISMODIFY_SERVICE) {
                    AppConfig.ISMODIFY_SERVICE = false;
                }
                AppConfig.isNewService = false;
                break;
            case R.id.sendservicebnt:
                sendService();
                break;
            default:
                break;
        }

    }

    /**
     */
    private void sendService() {
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                JSONObject submitjsonObject = format();
                JSONObject returnjson = ConnectService.submitDataByJson(
                        AppConfig.URL_PUBLIC + "Service/Add", submitjsonObject);
                Log.e("新建服务", submitjsonObject.toString() + "             " + returnjson.toString());
                try {
                    int retCode = returnjson.getInt("RetCode");
                    switch (retCode) {
                        case AppConfig.RETCODE_SUCCESS:
                            newcourseid = returnjson.getInt("RetData");
                            handler.obtainMessage(AppConfig.LOAD_FINISH)
                                    .sendToTarget();
                            break;
                        default:
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start(((App) getApplication()).getThreadMgr());
    }


    private JSONObject format() {
        // TODO Auto-generated method stub
        ServiceBean serviceBean = new ServiceBean();
        serviceBean = mServiceBean;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Name", serviceBean.getCustomer().getName());
            mServiceBean.setName(serviceBean.getCustomer()
                    .getName());
            jsonObject.put("ReferToID", serviceBean.getId());
            jsonObject.put("UserID", serviceBean.getCustomer().getUserID());
            jsonObject.put("CategoryID", serviceBean.getCategoryID());
            jsonObject.put("SubCategoryID", serviceBean.getSubCategoryID());
            jsonObject.put("ConcernID", serviceBean.getConcernID());
            jsonObject.put("Description", serviceBean.getDescription());
            jsonObject.put("Comment", serviceBean.getComment());
            jsonObject.put("ServiceStartTime",
                    serviceBean.getServiceStartTime());

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return jsonObject;
    }
}
