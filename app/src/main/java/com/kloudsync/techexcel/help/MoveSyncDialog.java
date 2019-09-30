package com.kloudsync.techexcel.help;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.Team;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.CenterToast;
import com.kloudsync.techexcel.response.TeamsResponse;
import com.kloudsync.techexcel.view.spinner.NiceSpinner;
import com.kloudsync.techexcel.view.spinner.OnSpinnerItemSelectedListener;
import com.kloudsync.techexcel.view.spinner.SpaceTextFormatter;
import com.kloudsync.techexcel.view.spinner.TeamTextFormatter;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.kloudsync.activity.TeamSpaceInterfaceListener;
import com.ub.kloudsync.activity.TeamSpaceInterfaceTools;
import com.ub.techexcel.bean.SyncRoomBean;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoveSyncDialog implements OnClickListener {

    private Context mContext;
    private NiceSpinner teamSpinner;
    private NiceSpinner spaceSpinner;
    private SyncRoomBean syncRoom;
    private TextView pathText;
    List<Team> teams;
    List<TeamSpaceBean> spaces;

    public MoveSyncDialog(Context context) {
        this.mContext = context;
        getPopupWindowInstance();
        getTeamList();
    }


    public void setSyncRoom(SyncRoomBean syncRoom) {
        this.syncRoom = syncRoom;
        if (TextUtils.isEmpty(syncRoom.getPath())) {
//            pathText.setText();
        } else {
            pathText.setText(syncRoom.getPath());
        }
    }

    public Dialog dialog;

    public void getPopupWindowInstance() {
        if (null != dialog) {
            dialog.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }

    public void cancel() {
        if (dialog != null) {
            dialog.cancel();
            dialog = null;
        }
    }

    private TextView cancel, ok;
    private View view;

    Team currentSelectedTeam;

    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.dialog_move_sync, null);
        cancel = (TextView) view.findViewById(R.id.cancel);
        ok = (TextView) view.findViewById(R.id.ok);
        pathText = (TextView) view.findViewById(R.id.txt_path);
        dialog = new Dialog(mContext, R.style.my_dialog);
        dialog.setContentView(view);
        spaceSpinner = view.findViewById(R.id.spinner_space);
        teamSpinner = view.findViewById(R.id.spinner_team);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.85f);
        dialog.getWindow().setWindowAnimations(R.style.PopupAnimation3);
        dialog.getWindow().setAttributes(lp);
        cancel.setOnClickListener(this);
        ok.setOnClickListener(this);
        teamSpinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                if (currentSelectedTeam == null) {
                    spaceSpinner.setSelectedIndex(-1);
                    getSpaceList(((Team) teamSpinner.getItemAtPosition(position)).getItemID() + "");
                    return;
                }

                if (currentSelectedTeam != null && teamSpinner.getSelectedItem() != null && currentSelectedTeam == teamSpinner.getSelectedItem()) {
                    return;
                }
                spaces = new ArrayList<>();
                spaceSpinner.attachDataSource(spaces, new SpaceTextFormatter());
                spaceSpinner.setSelectedIndex(-1);
                getSpaceList(((Team) teamSpinner.getItemAtPosition(position)).getItemID() + "");

            }
        });
        teamSpinner.setOnClickListener(this);
        spaceSpinner.setOnClickListener(this);

    }

    private void moveToSpace() {
        if (spaceSpinner.getSelectedIndex() == -1) {
            Toast.makeText(mContext, "请选择空间", Toast.LENGTH_SHORT).show();
        } else {
            TeamSpaceBean space = (TeamSpaceBean) spaceSpinner.getSelectedItem();
            requestMove(syncRoom, space.getItemID() + "");
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.cancel:
                dialog.dismiss();
                break;
            case R.id.ok:
                moveToSpace();
                break;
            case R.id.spinner_team:
                if (teamSpinner.getSelectedIndex() == -1) {
                    currentSelectedTeam = null;
                } else {
                    currentSelectedTeam = (Team) teamSpinner.getSelectedItem();
                }
                break;

            case R.id.spinner_space:
                if (spaces == null || spaces.size() == 0) {
                    Toast.makeText(mContext, "当前团队没有可用的空间", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }

    public void show() {
        if (dialog != null) {
            dialog.show();
        }

    }

    public void getTeamList() {
        ServiceInterfaceTools.getinstance().getCompanyTeams(AppConfig.SchoolID + "").enqueue(new Callback<TeamsResponse>() {
            @Override
            public void onResponse(Call<TeamsResponse> call, Response<TeamsResponse> response) {
                if (response != null && response.isSuccessful() && response.body() != null) {
                    List<Team> list = response.body().getRetData();
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    teams = list;
                }
                teamSpinner.attachDataSource(teams, new TeamTextFormatter());
            }

            @Override
            public void onFailure(Call<TeamsResponse> call, Throwable t) {

            }
        });

    }


    private void getSpaceList(String teamId) {
        String url = AppConfig.URL_PUBLIC + "TeamSpace/List?companyID=" + AppConfig.SchoolID + "&type=2&parentID=" + teamId;
        TeamSpaceInterfaceTools.getinstance().getTeamSpaceList(url,
                TeamSpaceInterfaceTools.GETTEAMSPACELIST, new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        List<TeamSpaceBean> list = (List<TeamSpaceBean>) object;
                        Log.e("MoveSyncDialog", "space_list:" + list);
                        if (list == null) {
                            list = new ArrayList<>();
                        }
                        spaces = list;
                        spaceSpinner.attachDataSource(spaces, new SpaceTextFormatter());
                    }
                });
    }

    private void requestMove(SyncRoomBean syncRoom, String spaceid) {
        TeamSpaceInterfaceTools.getinstance().switchSpace(AppConfig.URL_PUBLIC + "SyncRoom/SwitchSpace?syncRoomID=" + syncRoom.getItemID() + "&spaceID=" + spaceid,
                TeamSpaceInterfaceTools.SWITCHSPACE, new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        EventBus.getDefault().post(new TeamSpaceBean());
                        new CenterToast.Builder(mContext).setSuccess(true).setMessage(mContext.getResources().getString(R.string.operate_success)).create().show();
                        cancel();
                    }
                });
    }


}
