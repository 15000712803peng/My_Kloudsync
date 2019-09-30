package com.ub.service.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.help.PopShareKloudSync;
import com.ub.kloudsync.activity.Document;
import com.ub.techexcel.adapter.SharedAdapter;
import com.ub.techexcel.bean.ServiceBean;
import com.ub.techexcel.bean.SoundtrackBean;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import java.util.List;

public class MeetingShareActivity extends Activity implements View.OnClickListener {

    private ImageView mBack;
    private RecyclerView mRecyclerView;
    private ServiceBean bean;
    private SharedAdapter sharedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meetingshareactivity);
        bean = (ServiceBean) getIntent().getSerializableExtra("lesson");
        initView();
        getSoundData();
    }


    private void getSoundData() {
        ServiceInterfaceTools.getinstance().lessonSoundtrack(AppConfig.URL_PUBLIC + "LessonSoundtrack/List?attachmentID=0&lessonID=" + bean.getId(),
                ServiceInterfaceTools.LESSONSOUNDTRACK, new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        List<SoundtrackBean> soundtrackBeanList = (List<SoundtrackBean>) object;
                        sharedAdapter = new SharedAdapter(MeetingShareActivity.this, soundtrackBeanList);
                        sharedAdapter.setOnItemClickListener3(new SharedAdapter.OnItemClickListener3() {
                            @Override
                            public void onClick(SoundtrackBean soundtrackBean) {
                                shareSound(soundtrackBean);
                            }
                        });
                        mRecyclerView.setAdapter(sharedAdapter);
                    }
                });
    }


    private void shareSound(SoundtrackBean soundtrackBean) {
        Document document = new Document();
        document.setTitle(soundtrackBean.getTitle());
        document.setSourceFileUrl(soundtrackBean.getAvatarUrl());

        ShareKloudSync(document, soundtrackBean.getSoundtrackID());

    }

    private void ShareKloudSync(final Document document, final int id) {
        final PopShareKloudSync psk = new PopShareKloudSync();
        psk.getPopwindow(this, document, id);
        psk.setPoPDismissListener(new PopShareKloudSync.PopShareKloudSyncDismissListener() {
            @Override
            public void CopyLink() {

            }

            @Override
            public void Wechat() {

            }

            @Override
            public void Moment() {

            }

            @Override
            public void Scan() {

            }

            @Override
            public void PopBack() {


            }
        });
        psk.startPop();

    }


    private void initView() {

        mBack = (ImageView) findViewById(R.id.back_iv);
        mBack.setOnClickListener(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycleview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                finish();
                break;

        }
    }


}
