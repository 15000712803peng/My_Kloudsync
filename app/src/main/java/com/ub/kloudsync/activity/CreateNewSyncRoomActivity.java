package com.ub.kloudsync.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.docment.InviteNew3Activity;
import com.kloudsync.techexcel.info.Customer;
import com.ub.techexcel.bean.SyncRoomBean;
import com.ub.techexcel.tools.NewSyncRoomTypePopup;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class CreateNewSyncRoomActivity extends Activity implements View.OnClickListener {

    private RelativeLayout customerservice, selectmembers;
    private TextView customerservicevalue, selectmembersvalue, createbtn;
    private EditText inputname;
    private ImageView back;
    private int teamId, spaceId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createnewsyncroom);
        teamId = getIntent().getIntExtra("teamid", 0);
        spaceId = getIntent().getIntExtra("spaceid", 0);
        initView();

    }

    private void initView() {
        customerservice = (RelativeLayout) findViewById(R.id.customerservice);
        selectmembers = (RelativeLayout) findViewById(R.id.selectmembers);
        customerservice.setOnClickListener(this);
        selectmembers.setOnClickListener(this);
        customerservicevalue = (TextView) findViewById(R.id.customerservicevalue);
        selectmembersvalue = (TextView) findViewById(R.id.selectmembersvalue);
        createbtn = (TextView) findViewById(R.id.createbtn);

        inputname = (EditText) findViewById(R.id.inputname);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
        createbtn.setOnClickListener(this);

    }

    private void createnewsyncroom() {
        TeamSpaceInterfaceTools.getinstance().createSyncRoom(AppConfig.URL_PUBLIC + "SyncRoom/CreateSyncRoom",
                TeamSpaceInterfaceTools.CREATESYNCROOM, AppConfig.SchoolID, teamId, spaceId, inputname.getText().toString(),
                "", "", returnlist, new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        EventBus.getDefault().post(new SyncRoomBean());
                        finish();
                    }
                });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.customerservice:
                openTeamTypePopup();
                break;
            case R.id.selectmembers:
                selectMembers();
                break;
            case R.id.back:
                finish();
                break;
            case R.id.createbtn:
                createnewsyncroom();
                break;

        }
    }

    private void selectMembers() {
        Intent intent = new Intent(this, InviteNew3Activity.class);
        startActivityForResult(intent, 1);
    }

    List<Customer> returnlist = new ArrayList<>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                returnlist.clear();
                returnlist = (List<Customer>) data.getSerializableExtra("llist");
                String ss = "";
                for (int i = 0; i < returnlist.size(); i++) {
                    Customer customer = returnlist.get(i);
                    ss = ss + customer.getName() + ",";
                }
                selectmembersvalue.setText(ss);
            }

        }
    }

    private int teamType = 0;

    private void openTeamTypePopup() {
        NewSyncRoomTypePopup newSyncRoomTypePopup = new NewSyncRoomTypePopup();
        newSyncRoomTypePopup.getPopwindow(this);
        newSyncRoomTypePopup.setFavoritePoPListener(new NewSyncRoomTypePopup.FavoritePoPListener() {
            @Override
            public void select(int type) {
                teamType = type;
                if (type == 1) {
                    customerservicevalue.setText("Customer Service");
                } else if (type == 2) {
                    customerservicevalue.setText("Customer Requirement Discussion");
                } else if (type == 3) {
                    customerservicevalue.setText("Customer Leads");
                }
            }

            @Override
            public void dismiss() {
                getWindow().getDecorView().setAlpha(1.0f);
            }

            @Override
            public void open() {
                getWindow().getDecorView().setAlpha(0.5f);
            }

        });
        newSyncRoomTypePopup.StartPop(customerservice, teamType,false);
    }
}
