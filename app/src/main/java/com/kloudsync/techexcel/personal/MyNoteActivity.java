package com.kloudsync.techexcel.personal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.adapter.MyNotesAdapter;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.mvp.BaseActivity;
import com.kloudsync.techexcel.mvp.presenter.MyNotePresenter;
import com.kloudsync.techexcel.mvp.view.MyNoteView;
import com.tqltech.tqlpencomm.Dot;
import com.ub.techexcel.bean.Note;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class MyNoteActivity extends BaseActivity<MyNotePresenter> implements MyNoteView {

	@Bind(R.id.tv_title)
	private TextView tv_title;
	@Bind(R.id.layout_back)
	private RelativeLayout layout_back;
	@Bind(R.id.rv_pc)
	private RecyclerView rv_pc;
	private MyNotesAdapter mMyNotesAdapter;
    private List<Note> notes = new ArrayList<>();
	public static final int TYPE_LIVE_NOTE = 1;
	public static final int TYPE_NOTE = 2;
	private Note mLiveNote;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

	@Override
	protected int getLayout() {
		return R.layout.activity_personal_note;
	}

	@Override
	protected void initPresenter() {
		mPresenter = new MyNotePresenter();
	}

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventNote(Note note) {

    }

	@Override
	protected void initView() {
        tv_title.setText(R.string.mynote);
        rv_pc.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
		mLiveNote = new Note();
		mLiveNote.setNoteID(-1);
		mLiveNote.setTitle(getString(R.string.no_notes_currently));
		mLiveNote.setNoteType(TYPE_LIVE_NOTE);
		notes.add(mLiveNote);
		mMyNotesAdapter = new MyNotesAdapter(this, notes);
		rv_pc.setAdapter(mMyNotesAdapter);
    }

	@Override
	protected void initListener() {
		layout_back.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		String url = AppConfig.URL_PUBLIC + "DocumentNote/UserNoteList?userID=" + AppConfig.UserID;
        ServiceInterfaceTools.getinstance().getUserNoteList(url, ServiceInterfaceTools.GETSYNCROOMUSERLIST, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                List<Note> list = (List<Note>) object;
                for (Note note : list) {
                    note.setNoteType(TYPE_NOTE);
                }
                notes.addAll(list);
	            mMyNotesAdapter.notifyDataSetChanged();
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
	public void onReceiveDot(Dot dot) {
		mMyNotesAdapter.onReceiveDot(dot);
	}

	@Override
	public void onReceiveOfflineStrokes(Dot dot) {
		mMyNotesAdapter.onReceiveOfflineStrokes(dot);
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
    }
}
