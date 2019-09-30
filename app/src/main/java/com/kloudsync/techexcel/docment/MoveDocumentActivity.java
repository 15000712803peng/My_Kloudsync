package com.kloudsync.techexcel.docment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.SpaceSelAdapter;
import com.kloudsync.techexcel.app.BaseActivity;
import com.kloudsync.techexcel.config.AppConfig;
import com.ub.kloudsync.activity.SelectTeamActivity;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.kloudsync.activity.TeamSpaceInterfaceListener;
import com.ub.kloudsync.activity.TeamSpaceInterfaceTools;
import com.ub.techexcel.service.ConnectService;

import org.feezu.liuli.timeselector.Utils.TextUtil;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MoveDocumentActivity extends BaseActivity implements View.OnClickListener, SpaceSelAdapter.OnItemClickListener {

    private RelativeLayout backLayout;
    private TextView teamNameText;
    private int teamID;
    private int newTeamID;
    private int spaceID;
    private int docID;
    private String teamName;
    SpaceSelAdapter adapter;
    RecyclerView spaceList;
    Button moveBtn;
    RelativeLayout switchTeamLayout;
    private static final int REQUEST_SELECT_TEAM = 1;
    private TextView titleText;

    @Override
    protected int setLayout() {
        return R.layout.activity_move_document;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadSpaceList(teamID);
    }

    @Override
    protected void initView() {
        teamID = getIntent().getIntExtra("team_id", -1);
        spaceID = getIntent().getIntExtra("space_id", -1);
        docID = getIntent().getIntExtra("doc_id", -1);
        teamName = getIntent().getStringExtra("team_name");
        backLayout = findViewById(R.id.layout_back);
        teamNameText = findViewById(R.id.txt_team_name);
        moveBtn = findViewById(R.id.btn_move);
        backLayout.setOnClickListener(this);
        moveBtn.setOnClickListener(this);
        spaceList = findViewById(R.id.list_space);
        titleText = findViewById(R.id.tv_title);
        titleText.setText(R.string.move_to);
        spaceList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        if (!TextUtil.isEmpty(teamName)) {
            teamNameText.setText(teamName);
        }
        adapter = new SpaceSelAdapter();
        adapter.setSpaces(new ArrayList<TeamSpaceBean>());
        adapter.setCurrentSpaceId(spaceID);
        spaceList.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        switchTeamLayout = findViewById(R.id.layout_switch_team);
        switchTeamLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_back:
                finish();
                break;
            case R.id.btn_move:
                move();
                break;
            case R.id.layout_switch_team:
                selectTeam();
                break;
        }
    }

    private void loadSpaceList(int teamID) {
        TeamSpaceInterfaceTools.getinstance().getTeamSpaceList(AppConfig.URL_PUBLIC + "TeamSpace/List?companyID=" + AppConfig.SchoolID + "&type=2&parentID=" + teamID,
                TeamSpaceInterfaceTools.GETTEAMSPACELIST, new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        refreshList((List<TeamSpaceBean>) object);
                    }
                });
    }

    private void refreshList(List<TeamSpaceBean> spaces) {
        if (spaces == null) {
            spaces = new ArrayList<>();
        }
        adapter.setCurrentSpaceId(spaceID);
        adapter.setSpaces(spaces);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(int spaceID) {
        adapter.setCurrentSpaceId(spaceID);
        adapter.notifyDataSetChanged();
    }

    private void move() {
        if (adapter == null || adapter.getSpaces() == null || adapter.getSpaces().size() == 0) {
            Toast.makeText(this, "please create space first", Toast.LENGTH_SHORT).show();
            return;
        }
        if (adapter.getCurrentSpaceId() == -1) {
            Toast.makeText(this, "Please select space first", Toast.LENGTH_SHORT).show();
            return;
        }

        if (adapter.getCurrentSpaceId() == spaceID) {
            return;
        }
        moveRequst(adapter.getCurrentSpaceId());
    }

    private void moveRequst(int spaceID) {
        String requestUrl = AppConfig.URL_PUBLIC
                + "SpaceAttachment/SwitchSpace?itemIDs=" + docID
                + "&spaceID=" + spaceID;
        Observable.just(requestUrl).observeOn(Schedulers.io()).map(new Function<String, JSONObject>() {
            @Override
            public JSONObject apply(String url) throws Exception {
                return ConnectService.submitDataByJson(url, null);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject response) throws Exception {
                handleMoveResponse(response);
            }
        });
    }

    private void handleMoveResponse(JSONObject response) {
        if (response == null) {
            Toast.makeText(this, R.string.operate_failure, Toast.LENGTH_SHORT).show();
            return;
        }
        String retCode = "";
        try {

            retCode = response.getString("RetCode");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (TextUtil.isEmpty(retCode) || !retCode.equals(AppConfig.RIGHT_RETCODE)) {
            Toast.makeText(this, R.string.operate_failure, Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, R.string.operate_success, Toast.LENGTH_SHORT).show();
        EventBus.getDefault().post(new TeamSpaceBean());
        setResult(RESULT_OK);
        finish();

    }

    private void selectTeam() {
        Intent intent = new Intent(this, SelectTeamActivity.class);
        intent.putExtra("team_id", teamID);
        startActivityForResult(intent, REQUEST_SELECT_TEAM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_TEAM && resultCode == RESULT_OK) {
            newTeamID = data.getIntExtra("team_id", -1);
            teamName = data.getStringExtra("team_name");
            loadSpaceList(newTeamID);
            teamNameText.setText(teamName);
        }
    }
}
