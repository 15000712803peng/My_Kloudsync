package com.kloudsync.techexcel.personal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.HelpCenterAdapter;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.search.HelpDocumentSearchActivity;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.ui.DocAndMeetingActivity;
import com.ub.techexcel.bean.LineItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class HelpCenterActivity extends Activity implements View.OnClickListener {
    private RelativeLayout backLayout;
    private TextView tv_title;
    private RecyclerView rv_hc;
    private HelpCenterAdapter hadapter;
    private LinearLayout searchLayout;
    ArrayList<LineItem> mlist = new ArrayList();

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("NewApi")
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case AppConfig.FAILED:
                    String result = (String) msg.obj;
                    Toast.makeText(getApplicationContext(),
                            result,
                            Toast.LENGTH_LONG).show();
                    break;

                case AppConfig.AddTempLesson:
                    result = (String) msg.obj;
                    GoToVIew(result);
                    break;
                case AppConfig.LOAD_FINISH:
//                    GoToVIew();
                    break;

                default:
                    break;
            }
        }
    };

    private void GoToVIew(String result) {
        Intent intent = new Intent(HelpCenterActivity.this, DocAndMeetingActivity.class);
        intent.putExtra("userid", AppConfig.UserID);
        intent.putExtra("meetingId", result);
        intent.putExtra("teacherid", AppConfig.UserID);
        intent.putExtra("isTeamspace", true);
        intent.putExtra("lessionId", result);
        intent.putExtra("identity", 2);
        intent.putExtra("isStartCourse", true);
        intent.putExtra("isPrepare", true);
        intent.putExtra("isInstantMeeting", 0);
        intent.putExtra("yinxiangmode", 0);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_center);
        //initSlideTrans();
        findView();
        initView();
        getInfo();
    }

    LineItem item;

    private void findView() {
        backLayout = (RelativeLayout) findViewById(R.id.layout_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(R.string.HelpCenter);
        rv_hc = (RecyclerView) findViewById(R.id.rv_hc);
        searchLayout = findViewById(R.id.search_layout);
        searchLayout.setOnClickListener(this);
        final LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_hc.setLayoutManager(manager);
        hadapter = new HelpCenterAdapter(mlist);
        hadapter.setOnItemClickListener(new HelpCenterAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                item = mlist.get(position);
                GetTempLesson(item);
            }
        });
        rv_hc.setAdapter(hadapter);

    }

    private void GetTempLesson(final  LineItem item) {
        final JSONObject jsonObject = null;
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject responsedata = null;
                        responsedata = com.ub.techexcel.service.ConnectService.submitDataByJson(
                                AppConfig.URL_PUBLIC
                                        + "Lesson/AddTempLessonWithOriginalDocument?attachmentID=" + item.getAttachmentID()
                                        + "&Title=" + URLEncoder.encode(LoginGet.getBase64Password(item.getFileName()), "UTF-8"), jsonObject);


                    Log.e("返回的jsonObject", jsonObject + "");
                    Log.e("返回的responsedata", responsedata.toString() + "");
                    String retcode = responsedata.getString("RetCode");
                    Message msg = new Message();
                    if (retcode.equals(AppConfig.RIGHT_RETCODE)) {
                        msg.what = AppConfig.AddTempLesson;
                        JSONObject RetData = responsedata.getJSONObject("RetData");
                        msg.obj = RetData.getString("LessonID");
                    } else {
                        msg.what = AppConfig.FAILED;
                        String ErrorMessage = responsedata.getString("ErrorMessage");
                        msg.obj = ErrorMessage;
                    }

                    handler.sendMessage(msg);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());

    }

    private void getInfo() {
        LoginGet lg = new LoginGet();
        lg.setSpaceAttachmentGetListener(new LoginGet.SpaceAttachmentGetListener() {
            @Override
            public void getSA(ArrayList<LineItem> items) {
                mlist = items;
                hadapter.UpdateRV(mlist);
            }
        });
        lg.GetSpaceAttachment(this, 280);
    }

    private void initView() {
        backLayout.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_back:
                finish();
                break;
            case R.id.search_layout:
                startActivity(new Intent(this, HelpDocumentSearchActivity.class));
                break;
            default:
                break;
        }
    }

}
