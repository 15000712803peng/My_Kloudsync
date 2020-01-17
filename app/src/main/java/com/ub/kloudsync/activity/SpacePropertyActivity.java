package com.ub.kloudsync.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.kloudsync.techexcel.help.AddSpaceMemberDialog;
import com.kloudsync.techexcel.help.InviteNewDialog;
import com.kloudsync.techexcel.help.SpaceMemberOperationDialog;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.response.TeamMembersResponse;
import com.kloudsync.techexcel.tool.DensityUtil;
import com.kloudsync.techexcel.tool.KloudCache;
import com.kloudsync.techexcel.ui.InviteFromCompanyActivity;
import com.kloudsync.techexcel.ui.InviteFromPhoneActivity;
import com.kloudsync.techexcel.ui.InviteFromSpaceActivity;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpacePropertyActivity extends Activity implements SpaceMembersAdapter.OnItemClickListener, View.OnClickListener, SpaceMembersAdapter.MoreOptionsClickListener {
    private RecyclerView mTeamRecyclerView;
    private int spaceId;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_property);
        initView();
        spaceId = getIntent().getIntExtra("ItemID", 0);
        getSpaceMemebers(spaceId);
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


    private int getRoleInSchool() {
        return KloudCache.getInstance(this).getTeamRole().getTeamRole();
    }


    private void fillByRole() {
        int role = KloudCache.getInstance(this).getUserRole();
        int teamRole = KloudCache.getInstance(this).getTeamRole().getTeamRole(); //登录用户的身份
        Log.e("ddddd", teamRole + "");
        if (teamRole == RoleInTeam.ROLE_OWENER || teamRole == RoleInTeam.ROLE_ADMIN) {
            View view = getLayoutInflater().inflate(R.layout.add_space_member_header, mTeamRecyclerView, false);
            LinearLayout addAdminLayout = view.findViewById(R.id.layout_add_admin);
            LinearLayout addMemberLayout = view.findViewById(R.id.layout_add_member);
            if (teamRole == RoleInTeam.ROLE_OWENER) {
                addAdminLayout.setVisibility(View.VISIBLE);
                addMemberLayout.setVisibility(View.VISIBLE);
            } else if (teamRole == RoleInTeam.ROLE_ADMIN) {
                addAdminLayout.setVisibility(View.VISIBLE);
                addMemberLayout.setVisibility(View.VISIBLE);
            }
            addAdminLayout.setOnClickListener(this);
            addMemberLayout.setOnClickListener(this);
            madapter.setHeaderView(view);
        }
    }


    private void initView() {
        titleText = findViewById(R.id.tv_title);
        titleText.setText(getString(R.string.space_memers));
        mTeamRecyclerView = (RecyclerView) findViewById(R.id.recycleview);
        mTeamRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        madapter = new SpaceMembersAdapter(this, getRoleInSchool());
        madapter.setOnItemClickListener(this);
        madapter.setMoreOptionsClickListener(this);
        mTeamRecyclerView.setAdapter(madapter);
        backLayout = (RelativeLayout) findViewById(R.id.layout_back);
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        fillByRole();
        Drawable d = getResources().getDrawable(R.drawable.documentadd2);
        d.setBounds(0, 0, DensityUtil.dp2px(getApplicationContext(), 41),
                DensityUtil.dp2px(getApplicationContext(), 41));
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onItemClick(int position, Object data) {

    }


    private AddSpaceMemberDialog addSpaceMemberDialog;
    private static final int REQUEST_ADD_ADMIN = 1;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_add_admin:
                addSpaceMemberDialog = new AddSpaceMemberDialog(this);
                addSpaceMemberDialog.setOptionsLinstener(new AddSpaceMemberDialog.InviteOptionsLinstener() {
                    @Override
                    public void fromCompany() {
                        Intent intent = new Intent(SpacePropertyActivity.this, InviteFromSpaceActivity.class);
                        intent.putExtra("team_id", spaceId);
                        intent.putExtra("isAddAdmin", true);
                        startActivityForResult(intent, REQUEST_ADD_ADMIN);
                    }

                    @Override
                    public void formInvite() {
                        Intent intent = new Intent(SpacePropertyActivity.this, InviteFromPhoneActivity.class);
                        intent.putExtra("invite_type", 3);
                        intent.putExtra("team_id", spaceId);
                        startActivity(intent);
                    }
                });
                addSpaceMemberDialog.show(0);

                break;
            case R.id.layout_add_member:
                addSpaceMemberDialog = new AddSpaceMemberDialog(this);
                addSpaceMemberDialog.setOptionsLinstener(new AddSpaceMemberDialog.InviteOptionsLinstener() {
                    @Override
                    public void fromCompany() {
                        Intent intent = new Intent(SpacePropertyActivity.this, InviteFromSpaceActivity.class);
                        intent.putExtra("team_id", spaceId);
                        intent.putExtra("isAddAdmin", false);
                        startActivityForResult(intent, REQUEST_ADD_ADMIN);
                    }

                    @Override
                    public void formInvite() {
                        Intent intent = new Intent(SpacePropertyActivity.this, InviteFromPhoneActivity.class);
                        intent.putExtra("invite_type", 3);
                        intent.putExtra("team_id", spaceId);
                        startActivity(intent);
                    }
                });
                addSpaceMemberDialog.show(1);
                break;
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_ADD_ADMIN) {
                getSpaceMemebers(spaceId);
            }
        }
    }

    private SpaceMemberOperationDialog spaceMemberOperationDialog;

    @Override
    public void moreOptionsClick(final TeamMember member) {

        spaceMemberOperationDialog = new SpaceMemberOperationDialog(this, member);

        spaceMemberOperationDialog.setOptionsLinstener(new SpaceMemberOperationDialog.InviteOptionsLinstener() {
            @Override
            public void setAdmin() {
                String url = AppConfig.URL_PUBLIC + "TeamSpace/ChangeMemberType?ItemID=" + spaceId + "&MemberID=" + member.getMemberID() + "&memberType=" + 1; //设置Admin = 1
                ServiceInterfaceTools.getinstance().changeMemberType(url, ServiceInterfaceTools.CHANGEMEMBERTYPE, new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        getSpaceMemebers(spaceId);
                    }
                });
            }

            @Override
            public void clean() {
                String url = AppConfig.URL_PUBLIC + "TeamSpace/RemoveMember?ItemID=" + spaceId + "&MemberID=" + member.getMemberID();
                ServiceInterfaceTools.getinstance().removeMember(url, ServiceInterfaceTools.REMOVEMEMBER, new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        getSpaceMemebers(spaceId);
                    }
                });

            }
        });
        spaceMemberOperationDialog.show();

    }
}
