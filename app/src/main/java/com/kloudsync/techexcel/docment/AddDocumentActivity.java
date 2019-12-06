package com.kloudsync.techexcel.docment;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.BaseActivity;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.CenterToast;
import com.kloudsync.techexcel.dialog.UploadFileDialog;
import com.kloudsync.techexcel.help.AddDocumentTool;
import com.kloudsync.techexcel.help.DocChooseDialog;
import com.kloudsync.techexcel.tool.DocumentUploadTool;
import com.ub.kloudsync.activity.SelectTeamActivity;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.kloudsync.activity.TeamSpaceInterfaceListener;
import com.ub.kloudsync.activity.TeamSpaceInterfaceTools;
import com.ub.techexcel.adapter.SpaceAdapterV2;
import com.ub.techexcel.bean.LineItem;
import com.ub.techexcel.tools.FileUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class AddDocumentActivity extends BaseActivity implements View.OnClickListener, SpaceAdapterV2.OnItemClickListener, DocChooseDialog.SelectedOptionListener {
    private RelativeLayout backLayout;
    private RelativeLayout teamLayout;
    private TextView teamNameText;
    private RecyclerView spaceList;
    String teamName;
    private SpaceAdapterV2 spaceAdapter;
    int teamId;
    private static final int REQUEST_SELECTED_IMAGE = 1;
    private static final int REQUEST_SELECT_TEAM = 2;
    private static final int REQUEST_SELECT_DOC = 3;
    UploadFileDialog uploadFileDialog;
    int spaceId;
    TextView titleText;

    @Override
    protected int setLayout() {
        return R.layout.activity_add_document;
    }

    @Override
    protected void initView() {
        teamName = getIntent().getStringExtra("team_name");
        teamId = getIntent().getIntExtra("team_id", -1);
        titleText = findViewById(R.id.tv_title);
        titleText.setText(R.string.select_a_space);
        backLayout = findViewById(R.id.layout_back);
        teamLayout = findViewById(R.id.layout_team);
        teamNameText = findViewById(R.id.txt_team_name);
        if (!TextUtils.isEmpty(teamName)) {
            teamNameText.setText(teamName);
        }

        backLayout.setOnClickListener(this);
        spaceList = findViewById(R.id.list_space);
        spaceList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        spaceAdapter = new SpaceAdapterV2(this, new ArrayList<TeamSpaceBean>(),false);
        spaceList.setAdapter(spaceAdapter);
        spaceAdapter.setOnItemClickListener(this);
        teamLayout.setOnClickListener(this);
        getSpaceList(teamId);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_back:
                finish();
                break;
            case R.id.layout_team:
                Intent intent = new Intent(this, SelectTeamActivity.class);
                intent.putExtra("team_id", teamId);
                startActivityForResult(intent, REQUEST_SELECT_TEAM);
                break;
            default:
                break;
        }
    }

    private void getSpaceList(final int teamID) {
        TeamSpaceInterfaceTools.getinstance().getTeamSpaceList(AppConfig.URL_PUBLIC + "TeamSpace/List?companyID=" + AppConfig.SchoolID + "&type=2&parentID=" + teamID,
                TeamSpaceInterfaceTools.GETTEAMSPACELIST, new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        spaceAdapter.setSpaces((List<TeamSpaceBean>) object);
                    }
                });
    }

    DocChooseDialog dialog;

    @Override
    public void onItemClick(TeamSpaceBean teamSpaceBean) {
        spaceId = teamSpaceBean.getItemID();
        dialog = new DocChooseDialog(this);
        dialog.setSelectedOptionListener(this);
        dialog.show();
    }

    @Override
    public void selectFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_SELECTED_IMAGE);
    }

    @Override
    public void selectFromDocs() {
        Intent intent = new Intent(this, FavoriteDocumentsActivity.class);
        intent.putExtra("space_id", spaceId);
        startActivityForResult(intent, REQUEST_SELECT_DOC);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_SELECTED_IMAGE:
                    if (data != null) {
                        String path = FileUtils.getPath(this, data.getData());
                        String pathname = path.substring(path.lastIndexOf("/") + 1);
                        LineItem file = new LineItem();
                        file.setUrl(path);
                        file.setFileName(pathname);
//                        uploadFile(file, spaceId);
                        AddDocumentTool.addDocumentToSpace(this, path, spaceId, new DocumentUploadTool.DocUploadDetailLinstener() {
                            @Override
                            public void uploadStart(){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (uploadFileDialog == null) {
                                            uploadFileDialog = new UploadFileDialog(AddDocumentActivity.this);
                                            uploadFileDialog.setTile("uploading");
                                            uploadFileDialog.show();

                                        } else {
                                            if (!uploadFileDialog.isShowing()) {
                                                uploadFileDialog.show();
                                            }
                                        }
                                    }
                                });
                            }

                            @Override
                            public void uploadFile(final int progress) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (uploadFileDialog != null && uploadFileDialog.isShowing()) {
                                            uploadFileDialog.setProgress(progress);
                                        }
                                    }
                                });
                            }

                            @Override
                            public void convertFile(final int progress) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (uploadFileDialog != null && uploadFileDialog.isShowing()) {
                                            uploadFileDialog.setTile("Converting");
                                            uploadFileDialog.setProgress(progress);
                                        }
                                    }
                                });
                            }

                            @Override
                            public void uploadFinished(Object result) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        addDocSucc();
                                    }
                                });

                            }

                            @Override
                            public void uploadError(String message) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "add failed", Toast.LENGTH_SHORT).show();
                                        if (uploadFileDialog != null) {
                                            uploadFileDialog.cancel();
                                        }
                                    }
                                });
                            }
                        });
                    }
                    break;
                case REQUEST_SELECT_TEAM:
                    if (data != null) {
                        refresh(data.getIntExtra("team_id", -1), data.getStringExtra("team_name"));
                    }
                    break;
                case REQUEST_SELECT_DOC:
                    addDocSucc();
                    break;
            }
        }
    }

    private void refresh(int teamId, String teamName) {
        this.teamId = teamId;
        getSpaceList(teamId);
        if (!TextUtils.isEmpty(teamName)) {
            teamNameText.setText(teamName);
        }
    }


    private void addDocSucc() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (uploadFileDialog != null) {
                    uploadFileDialog.cancel();
                }
                new CenterToast.Builder(getApplicationContext()).setSuccess(true).setMessage(getResources().getString(R.string.create_success)).create().show();
                EventBus.getDefault().post(new TeamSpaceBean());
                finish();
            }
        });
    }



}
