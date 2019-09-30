package com.ub.techexcel.tools;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.start.LoginGet;
import com.ub.kloudsync.activity.Document;
import com.ub.techexcel.adapter.AudioPlayAdapter;
import com.ub.techexcel.adapter.AudioPlayAdapter2;

import java.util.ArrayList;

public class AudioPlayDialog extends Dialog {

    private Context context;
    private RecyclerView secondRecyclerview;
    private String confirmButtonText;
    private String cacelButtonText;
    private ClickListenerInterface clickListenerInterface;
    private AudioPlayAdapter audioPlayAdapter;


    private RecyclerView audiorecyclerview;
    private AudioPlayAdapter2 audioPlayAdapter2;


    public interface ClickListenerInterface {

        void doConfirm();

        void doCancel();

        void select(int value);

        void select2(Document value);
    }

    public AudioPlayDialog(Context context, String confirmButtonText, String cacelButtonText) {
        super(context, R.style.confirmDialog);
        this.context = context;
        this.confirmButtonText = confirmButtonText;
        this.cacelButtonText = cacelButtonText;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.audioplaylayout, null);
        setContentView(view);

        secondRecyclerview = (RecyclerView) view.findViewById(R.id.secondrecyclerview);
        secondRecyclerview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        audioPlayAdapter = new AudioPlayAdapter(context);
        audioPlayAdapter.setAudioPlayListener(new AudioPlayAdapter.AudioPlayListener() {
            @Override
            public void select(int value) {
                clickListenerInterface.select(value);
            }
        });
        secondRecyclerview.setAdapter(audioPlayAdapter);

        audiorecyclerview = (RecyclerView) view.findViewById(R.id.audiorecyclerview);
        audiorecyclerview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        LoginGet loginGet = new LoginGet();
        loginGet.setMyFavoritesGetListener(new LoginGet.MyFavoritesGetListener() {
            @Override
            public void getFavorite(ArrayList<Document> list) {

                audioPlayAdapter2 = new AudioPlayAdapter2(context, list);
                audioPlayAdapter2.setAudioPlay2Listener(new AudioPlayAdapter2.AudioPlayListener2() {
                    @Override
                    public void select(Document value) {
                        clickListenerInterface.select2(value);
                    }
                });
                audiorecyclerview.setAdapter(audioPlayAdapter2);

            }
        });
        loginGet.MyFavoriteRequest(context, 3);


        TextView tvConfirm = (TextView) view.findViewById(R.id.confirm);
        TextView tvCancel = (TextView) view.findViewById(R.id.cancel);
        tvConfirm.setText(confirmButtonText);
        tvCancel.setText(cacelButtonText);
        tvConfirm.setOnClickListener(new ClickListener());
        tvCancel.setOnClickListener(new ClickListener());
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics();
        lp.width = (int) (d.widthPixels * 0.7);
        dialogWindow.setAttributes(lp);
    }

    public void setClicklistener(ClickListenerInterface clickListenerInterface) {
        this.clickListenerInterface = clickListenerInterface;
    }

    private class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.confirm:
                    clickListenerInterface.doConfirm();
                    break;
                case R.id.cancel:
                    clickListenerInterface.doCancel();
                    break;
            }
        }
    }

}