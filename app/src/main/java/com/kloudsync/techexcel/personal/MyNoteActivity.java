package com.kloudsync.techexcel.personal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.NoteDetail;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.SelectNoteDialog;
import com.ub.techexcel.adapter.SyncRoomNoteListAdapter;
import com.ub.techexcel.bean.Note;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;
import com.ub.techexcel.tools.SyncRoomOtherNoteListPopup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MyNoteActivity extends Activity implements View.OnClickListener {

    private TextView tv_title;
    private RelativeLayout layout_back;
    private LinearLayout search_layout;
    private RecyclerView rv_pc;
    private SyncRoomNoteListAdapter syncRoomNoteListAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_note);
        EventBus.getDefault().register(this);
        initView();
        getNoteData(AppConfig.UserID);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventNote(Note note) {

    }


    private void initView() {
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText(R.string.mynote);
        search_layout = findViewById(R.id.search_layout);
        search_layout.setOnClickListener(this);
        layout_back = findViewById(R.id.layout_back);
        layout_back.setOnClickListener(this);
        rv_pc = findViewById(R.id.rv_pc);
        rv_pc.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }


    private void getNoteData(String useid) {
        String url = AppConfig.URL_PUBLIC + "DocumentNote/UserNoteList?userID=" + useid;
        ServiceInterfaceTools.getinstance().getUserNoteList(url, ServiceInterfaceTools.GETSYNCROOMUSERLIST, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                List<Note> list = new ArrayList<>();
                list.addAll((List<Note>) object);
                syncRoomNoteListAdapter = new SyncRoomNoteListAdapter(MyNoteActivity.this, list);
                rv_pc.setAdapter(syncRoomNoteListAdapter);
                syncRoomNoteListAdapter.setWebCamPopupListener(new SyncRoomNoteListAdapter.WebCamPopupListener() {
                    @Override
                    public void select(Note noteDetail) {

                    }

                    @Override
                    public void notifychangeUserid(String userId) {

                    }

                });
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_back:
                finish();
                break;
            case R.id.search_layout:
                break;
            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1 && data != null) {

            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
