package com.kloudsync.techexcel.docment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.help.DialogANContact;
import com.ub.kloudsync.activity.Document;
import com.ub.kloudsync.activity.TeamSpaceBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class InviteNewActivity extends AppCompatActivity{

    private ImageView img_notice;
    private RelativeLayout rl_skc;
    private RelativeLayout rl_anc;
    private RelativeLayout rl_sfm;
    private RelativeLayout rl_wechat;
    private RelativeLayout rl_iyc;
    private TextView tv_skc;
    private TextView tv_sfm;
    private ImageView img_skc;
    private int itemID;
    private boolean flag_c;
    private boolean flag_c2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitenew);
        EventBus.getDefault().register(this);
        itemID = getIntent().getIntExtra("itemID", 0);
        flag_c = getIntent().getBooleanExtra("flag_c",false);
        flag_c2 = getIntent().getBooleanExtra("flag_c2",false);
        initView();
    }

    private void initView() {
        img_notice = (ImageView) findViewById(R.id.img_notice);
        img_skc = (ImageView) findViewById(R.id.img_skc);
        rl_skc= (RelativeLayout) findViewById(R.id.rl_skc);
        rl_anc= (RelativeLayout) findViewById(R.id.rl_anc);
        rl_sfm= (RelativeLayout) findViewById(R.id.rl_sfm);
        rl_wechat= (RelativeLayout) findViewById(R.id.rl_wechat);
        rl_iyc= (RelativeLayout) findViewById(R.id.rl_iyc);
        tv_skc= (TextView) findViewById(R.id.tv_skc);
        tv_sfm= (TextView) findViewById(R.id.tv_sfm);

        img_notice.setOnClickListener(new MyOnClick());
        rl_skc.setOnClickListener(new MyOnClick());
        rl_anc.setOnClickListener(new MyOnClick());
        rl_sfm.setOnClickListener(new MyOnClick());

        ShowCC();

    }

    private void ShowCC() {
        if(flag_c){
            rl_wechat.setVisibility(View.GONE);
            rl_iyc.setVisibility(View.GONE);
            rl_skc.setVisibility(View.GONE);
        }
        if(flag_c2){
            rl_iyc.setVisibility(View.GONE);
        }
    }

    protected class MyOnClick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.img_notice:
                    finish();
                    break;
                case R.id.rl_skc:
                    GotoSKC();
//                    Dialog(InviteNewActivity.this);
                    break;
                case R.id.rl_anc:
                    AddNewC();
                    break;
                case R.id.rl_sfm:
                    GoToSC();
                    break;
                default:
                    break;
            }

        }
    }

    private void GoToSC() {
        Intent intent = new Intent(this, SearchContactActivity.class);
        intent.putExtra("itemID",itemID);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                new Pair(tv_sfm, SearchContactActivity.嘛米嘛米哄));
        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    DialogANContact dac;
    private void AddNewC() {
        dac = new DialogANContact();
        dac.setPoPDismissListener(new DialogANContact.DialogDismissListener() {
            @Override
            public void ChangeOK() {
                EventBus.getDefault().post(new TeamSpaceBean());
                finish();
            }
        });
        dac.EditCancel(InviteNewActivity.this, itemID);
    }

    private void GotoSKC() {
        Intent intent = new Intent(this, InviteNew2Activity.class);
        intent.putExtra("itemID",itemID);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                new Pair(img_skc, InviteNew2Activity.嘛米嘛米哄));
        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventFinish(Document fa) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case com.kloudsync.techexcel.start.RegisterActivity.CHANGE_COUNTRY_CODE:
                dac.ShowCode();
                break;
            default:
                break;
        }
    }
}
