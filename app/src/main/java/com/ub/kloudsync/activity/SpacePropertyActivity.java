package com.ub.kloudsync.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.SpaceMembersAdapter;
import com.kloudsync.techexcel.bean.RoleInTeam;
import com.kloudsync.techexcel.bean.TeamMember;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.InviteNewDialog;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.response.TeamMembersResponse;
import com.kloudsync.techexcel.tool.DensityUtil;
import com.kloudsync.techexcel.tool.KloudCache;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpacePropertyActivity extends Activity implements SpaceMembersAdapter.OnItemClickListener, View.OnClickListener {
    private RecyclerView mTeamRecyclerView;
    private int itemID;
    private RelativeLayout backLayout;
    private TextView titleText;
    private SpaceMembersAdapter madapter;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("NewApi")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppConfig.DELETESUCCESS:
                    EventBus.getDefault().post(new TeamSpaceBean());
                    EventBus.getDefault().post(new Customer());
                    finish();
                    break;
                case AppConfig.FAILED:
                    String result = (String) msg.obj;
                    Toast.makeText(getApplicationContext(),
                            result,
                            Toast.LENGTH_LONG).show();
                    break;
                case AppConfig.NO_NETWORK:
                    Toast.makeText(
                            getApplicationContext(),
                            getResources().getString(R.string.No_networking),
                            Toast.LENGTH_SHORT).show();

                    break;
                case AppConfig.NETERROR:
                    Toast.makeText(
                            getApplicationContext(),
                            getResources().getString(R.string.NETWORK_ERROR),
                            Toast.LENGTH_SHORT).show();

                    break;

                default:
                    break;
            }
        }
    };

    int myTeamRole;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_property);
        EventBus.getDefault().register(this);
        initView();
        itemID = getIntent().getIntExtra("ItemID", 0);
        getSpaceMemebers(itemID);
    }

    private void getSpaceMemebers(int spaceId) {
        ServiceInterfaceTools.getinstance().getSpaceMembers(spaceId + "").enqueue(new Callback<TeamMembersResponse>() {
            @Override
            public void onResponse(Call<TeamMembersResponse> call, Response<TeamMembersResponse> response) {
                if (response != null && response.isSuccessful()) {
                    List<TeamMember> members = response.body().getRetData();
                    if (members == null) {
                        members = new ArrayList<>();
                    }
                    madapter.setDatas(members);
                }
            }

            @Override
            public void onFailure(Call<TeamMembersResponse> call, Throwable t) {

            }
        });
    }

    private void fillByRole() {
        int role = KloudCache.getInstance(this).getUserRole();
        int teamRole = KloudCache.getInstance(this).getTeamRole().getTeamRole();
        if (teamRole == RoleInTeam.ROLE_OWENER || teamRole == RoleInTeam.ROLE_ADMIN) {
            View view = getLayoutInflater().inflate(R.layout.add_space_member_header, mTeamRecyclerView, false);
            LinearLayout addAdminLayout = view.findViewById(R.id.layout_add_admin);
            LinearLayout addMemberLayout = view.findViewById(R.id.layout_add_member);
            if (teamRole == RoleInTeam.ROLE_OWENER) {
                addAdminLayout.setVisibility(View.VISIBLE);
                addMemberLayout.setVisibility(View.VISIBLE);
            } else if (teamRole == RoleInTeam.ROLE_ADMIN) {
                addAdminLayout.setVisibility(View.GONE);
                addMemberLayout.setVisibility(View.VISIBLE);
            }
            addAdminLayout.setOnClickListener(this);
            addMemberLayout.setOnClickListener(this);
            madapter.setHeaderView(view);

        }
    }


    private void initView() {
        titleText = findViewById(R.id.tv_title);
        titleText.setText("Space members");
        mTeamRecyclerView = (RecyclerView) findViewById(R.id.recycleview);
        mTeamRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        madapter = new SpaceMembersAdapter(this);
        madapter.setOnItemClickListener(this);
        mTeamRecyclerView.setAdapter(madapter);
        fillByRole();
        backLayout = (RelativeLayout) findViewById(R.id.layout_back);
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Drawable d = getResources().getDrawable(R.drawable.documentadd2);
        d.setBounds(0, 0, DensityUtil.dp2px(getApplicationContext(), 41),
                DensityUtil.dp2px(getApplicationContext(), 41));

    }

    InviteNewDialog inviteDialog;



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventGroupInfo(TeamSpaceBean teamSpaceBean) {
        flagr = true;
    }



    private boolean flagr;

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }


    @Override
    public void onItemClick(int position, Object data) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_add_admin:
                break;
            case R.id.layout_add_member:
                break;
        }
    }
}
