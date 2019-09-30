package com.kloudsync.techexcel.linshi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.SyncDocumentAdapter;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.RecyclerViewDivider;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.tool.DensityUtil;
import com.ub.kloudsync.activity.TeamSpaceInterfaceListener;
import com.ub.kloudsync.activity.TeamSpaceInterfaceTools;
import com.ub.service.activity.SyncRoomActivity;
import com.ub.techexcel.bean.LineItem;
import com.ub.techexcel.bean.SyncRoomBean;
import com.ub.techexcel.bean.SyncRoomMember;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LinshiActivity extends AppCompatActivity {

    TextView tv_synccount;
    TextView tv_contact;
    TextView tv_va;
    LinearLayout lin_avtar;
    private ImageView img_notice;
    private RecyclerView rv_document;

    private SyncDocumentAdapter sadapter;

    SyncRoomBean syncRoomBean;

    LineItem item1;
    List<LineItem> items = new ArrayList<>();

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 0x1123:
                    sadapter.UpdateRV(items);
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linshi);
        syncRoomBean = (SyncRoomBean) getIntent().getSerializableExtra("syncRoomBean");
        findView();
        initView();
    }

    private void findView() {
        img_notice = (ImageView) findViewById(R.id.img_notice);
        tv_synccount = (TextView) findViewById(R.id.tv_synccount);
        tv_contact = (TextView) findViewById(R.id.tv_contact);
        tv_va = (TextView) findViewById(R.id.tv_va);
        lin_avtar = (LinearLayout) findViewById(R.id.lin_avtar);
        rv_document = (RecyclerView) findViewById(R.id.rv_document);

    }

    private void initView() {
        img_notice.setOnClickListener(new MyOnClick());
        tv_va.setOnClickListener(new MyOnClick());

        sadapter = new SyncDocumentAdapter(LinshiActivity.this, items);
        sadapter.setItemClickListener(new SyncDocumentAdapter.OnItemClickListener() {
            @Override
            public void Onitems(int position) {


                Intent intent = new Intent(LinshiActivity.this, SyncRoomActivity.class);
                intent.putExtra("userid", AppConfig.UserID);
                intent.putExtra("meetingId", syncRoomBean.getItemID() + "," + AppConfig.UserID);
                intent.putExtra("isTeamspace", true);
                intent.putExtra("yinxiangmode", 0);
                intent.putExtra("identity", 2);
                intent.putExtra("lessionId", syncRoomBean.getItemID() + "");
                intent.putExtra("syncRoomname", syncRoomBean.getName() + "");
                intent.putExtra("isInstantMeeting", 0);
                intent.putExtra("teacherid", AppConfig.UserID.replace("-", ""));
                intent.putExtra("isStartCourse", true);
                startActivity(intent);
            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_document.setLayoutManager(manager);
        rv_document.addItemDecoration(new RecyclerViewDivider(
                LinshiActivity.this, LinearLayout.HORIZONTAL,
                DensityUtil.dp2px(LinshiActivity.this,15),getResources().getColor(R.color.detail_top)));
        rv_document.setAdapter(sadapter);

        GetData();
    }

    private void GetData() {
        getServiceDetail();
        getAllMembers();
    }

    private void getServiceDetail() {

        new ApiTask(new Runnable() {
            @Override
            public void run() {
                JSONObject returnjson = com.ub.techexcel.service.ConnectService
                        .getIncidentbyHttpGet(AppConfig.URL_PUBLIC
                                + "TopicAttachment/List?topicID=" + syncRoomBean.getItemID());
                formatServiceData(returnjson);
            }
        }).start(ThreadManager.getManager());
    }


    private void formatServiceData(JSONObject returnJson) {
        Log.e("PDF文档列表", returnJson.toString());
        items = new ArrayList<LineItem>();
        try {
            int retCode = returnJson.getInt("RetCode");
            switch (retCode) {
                case AppConfig.RETCODE_SUCCESS:
                    JSONArray lineitems = returnJson.getJSONArray("RetData");
                    for (int j = 0; j < lineitems.length() && j<3; j++) {
                        JSONObject lineitem = lineitems.getJSONObject(j);
                        LineItem item = new LineItem();

                        item.setCreatedBy(lineitem.getString("CreatedBy"));
                        item.setCreatedByAvatar(lineitem.getString("CreatedByAvatar"));
                        item.setTopicId(lineitem.getInt("TopicID"));
                        item.setSyncRoomCount(lineitem.getInt("SyncCount"));
                        item.setFileName(lineitem.getString("Title"));
                        item.setUrl(lineitem.getString("AttachmentUrl"));
                        item.setHtml5(false);
                        item.setItemId(lineitem.getString("ItemID"));
                        item.setAttachmentID(lineitem.getString("AttachmentID"));
                        item.setCreatedDate(lineitem.getString("CreatedDate"));
                        item.setFlag(0);
                        if (lineitem.getInt("Status") == 0) {
                            items.add(item);
                        }
                    }
                    Message msg = Message.obtain();
                    msg.what = 0x1123;
                    handler.sendMessage(msg);
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private final void getAllMembers() {
        TeamSpaceInterfaceTools.getinstance().getMemberList(AppConfig.URL_PUBLIC + "Topic/MemberList?TeamTopicID=" + syncRoomBean.getItemID(),
                TeamSpaceInterfaceTools.GETMEMBERLIST, new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        List<SyncRoomMember> list = new ArrayList<>();
                        list = (List<SyncRoomMember>) object;
                        int x = 0;
                        lin_avtar.removeAllViews();
                        tv_contact.setText(list.size() + " Contact");
                        for (int i = 0; i < list.size(); i++) {
                            SyncRoomMember syncRoomMember = list.get(i);
                            if (syncRoomMember.getMemberType() == 2) {
//                                img_head.setImageURI(syncRoomMember.getMemberAvatar());
//                                tv_name.setText(syncRoomMember.getMemberName());
                            }

                            if (x++ < 3) {
                                Log.e("duang", x + "");
                                SimpleDraweeView sv = new SimpleDraweeView(LinshiActivity.this);
                                RoundingParams parames = RoundingParams.asCircle();
                                GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(getResources());
                                GenericDraweeHierarchy hierarchy = builder
                                        .setFailureImage(R.drawable.hello)
                                        .setRoundingParams(parames).build();
                                sv.setHierarchy(hierarchy);
                                sv.setImageURI(syncRoomMember.getMemberAvatar());
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT);
                                lp.width = DensityUtil.dp2px(LinshiActivity.this, 20);
                                lp.height = DensityUtil.dp2px(LinshiActivity.this, 20);
                                lp.setMargins((1 == x) ? 0 : DensityUtil.dp2px(LinshiActivity.this, -5), 0, 0, 0);

                                sv.setLayoutParams(lp);
                                lin_avtar.addView(sv);
                            }
                        }
                    }
                });
    }

    protected class MyOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.img_notice:
                    finish();
                    break;
                case R.id.tv_va:
                    Intent intent1 = new Intent(LinshiActivity.this, ViewAllDocumentActivity.class);
                    intent1.putExtra("syncRoomBean",syncRoomBean);
                    startActivity(intent1);
                    break;
                default:
                    break;
            }
        }
    }

}
