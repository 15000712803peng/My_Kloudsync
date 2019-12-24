package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.EventSoundtrackList;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.help.SoundtrackManager;
import com.ub.techexcel.adapter.SoundtrackAdapter;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;


public class UserSoundtrackDialog implements View.OnClickListener,DialogInterface.OnDismissListener,SoundtrackManager.OnSoundtrackResponse{

    public Activity host;
    public int width;
    public Dialog dialog;
    private View view;
    private ImageView close;
    private MeetingConfig meetingConfig;
    private RecyclerView soundtrackListView;
    private SoundtrackAdapter soundtrackAdapter;

    private void init() {
        if (null != dialog) {
            dialog.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }

    public UserSoundtrackDialog(Activity host){
        this.host = host;
        init();
    }

    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(host);
        view = layoutInflater.inflate(R.layout.dialog_soundtrack, null);
        close = (ImageView) view.findViewById(R.id.close);
        close.setOnClickListener(this);
        soundtrackListView = view.findViewById(R.id.list_soundtrack);
        soundtrackListView.setLayoutManager(new LinearLayoutManager(host, RecyclerView.VERTICAL, false));
        dialog = new Dialog(host, R.style.my_dialog);
        dialog.setContentView(view);
        dialog.getWindow().setGravity(Gravity.RIGHT);
        dialog.setOnDismissListener(this);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        View root = host.getWindow().getDecorView();
        params.height = root.getMeasuredHeight();
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.getWindow().setWindowAnimations(R.style.anination3);
    }

    @SuppressLint("NewApi")
    public void show(MeetingConfig meetingConfig) {
        this.meetingConfig = meetingConfig;
        SoundtrackManager.getInstance().requestSoundtrackList(meetingConfig,this);
        if(dialog != null){
            dialog.show();
        }
    }

    public boolean isShowing() {
        if(dialog != null){
            return dialog.isShowing();
        }
        return false;
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.close:
                dismiss();
                break;

            default:
                break;
        }
    }


    @Override
    public void onDismiss(DialogInterface dialog) {

    }

    @Override
    public void soundtrackList(EventSoundtrackList soundtrackList) {
        Observable.just(soundtrackList).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<EventSoundtrackList>() {
            @Override
            public void accept(EventSoundtrackList soundtrackList) throws Exception {
                if(soundtrackList.getSoundTracks() != null && soundtrackList.getSoundTracks().size() > 0){
                    if(soundtrackAdapter == null){
                        soundtrackAdapter = new SoundtrackAdapter(host);
                        soundtrackAdapter.setSoundTracks(soundtrackList.getSoundTracks());
                        soundtrackListView.setAdapter(soundtrackAdapter);
                    }else {
                        soundtrackAdapter.setSoundTracks(soundtrackList.getSoundTracks());
                    }
                }
            }
        }).subscribe();

    }
}
