package com.kloudsync.techexcel.dialog;

import android.app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.bean.EventRefreshMembers;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.frgment.MeetingMembersFragment;
import com.ub.service.activity.AddMeetingMemberActivity;

import static android.content.Context.MODE_PRIVATE;

public class MeetingMembersDialog extends DialogFragment implements View.OnClickListener,ViewPager.OnPageChangeListener {
    public Activity host;
    public int width;
    public int heigth;
    private MeetingConfig meetingConfig;
    private RelativeLayout indicatorLayout;
    private RelativeLayout membersTab,auditorsTab,invitorsTab;
    private ViewPager membersPager;
    private TextView tab1,tab2,tab3;
    private TextView indicator1,indicator2,indicator3;
    private ImageView backImage;
    private ImageView addImage;
    private SharedPreferences sharedPreferences;

    public MeetingMembersDialog(){

    }

    public void init(Activity host,MeetingConfig meetingConfig){
        this.host = host;
        this.meetingConfig = meetingConfig;
        sharedPreferences = host.getSharedPreferences(AppConfig.LOGININFO,MODE_PRIVATE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.my_dialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        if(view == null){
            view = inflater.inflate(R.layout.dialog_meeting_members, container,false);
        }
        initView(view);
        return view;
    }


    private void initView(View view) {
        heigth = (int) (host.getResources().getDisplayMetrics().heightPixels);
        membersTab = view.findViewById(R.id.tab_members);
        auditorsTab = view.findViewById(R.id.tab_auditor);
        invitorsTab = view.findViewById(R.id.tab_invitors);
        membersPager = view.findViewById(R.id.pager_members);
        addImage = view.findViewById(R.id.image_add);
        addImage.setOnClickListener(this);
        backImage = view.findViewById(R.id.back);
        backImage.setOnClickListener(this);
        tab1 = view.findViewById(R.id.tab1);
        tab2 = view.findViewById(R.id.tab2);
        tab3 = view.findViewById(R.id.tab3);
        indicator1 = view.findViewById(R.id.indicator1);
        indicator2 = view.findViewById(R.id.indicator2);
        indicator3 = view.findViewById(R.id.indicator3);
        membersTab.setOnClickListener(this);
        auditorsTab.setOnClickListener(this);
        invitorsTab.setOnClickListener(this);
        Log.e("check_members","members:" + meetingConfig.getMeetingMembers().size() + ",auditors:" + meetingConfig.getMeetingAuditor() +
                ",invitors:" + meetingConfig.getMeetingInvitors().size());
        membersAdapter = new MeetingMembersAdapter(getChildFragmentManager(),this.meetingConfig);
        membersPager.setAdapter(membersAdapter);
        getDialog().getWindow().setGravity(Gravity.RIGHT);
        getDialog().getWindow().setWindowAnimations(R.style.anination3);
        init();

    }

    private  MeetingMembersAdapter membersAdapter;

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(lp);

    }

    public boolean isShowing() {
        if(getDialog() != null){
            return getDialog().isShowing();
        }
        return false;

    }

    public void dismiss() {
        if (getDialog() != null) {
            getDialog().cancel();
        }
        super.dismiss();
    }

    @Override
    public void onClick(View view) {
        Log.e("onClick","clicked");
        switch (view.getId()) {
            case R.id.tab_members:
                if(membersPager != null){
                    membersPager.setCurrentItem(0);
                }
                break;
            case R.id.tab_auditor:
                if(membersPager != null){
                    membersPager.setCurrentItem(1);
                }
                break;
            case R.id.tab_invitors:
                if(membersPager != null && invitorsTab.getVisibility() == View.VISIBLE){
                    membersPager.setCurrentItem(2);
                }
                break;
            case R.id.back:
                dismiss();
                break;
            case R.id.image_add:
                Intent intent = new Intent(host, AddMeetingMemberActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
    }


    public void show(FragmentManager fragmentManager) {
        show(fragmentManager,"dialog");
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        initIndicators();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class MeetingMembersAdapter extends FragmentPagerAdapter {
        MeetingConfig meetingConfig;
        public MeetingMembersAdapter(FragmentManager fm,MeetingConfig meetingConfig) {
            super(fm);
            this.meetingConfig = meetingConfig;
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putInt("type",position + 1);

            MeetingMembersFragment fragment = new MeetingMembersFragment();
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            if(meetingConfig.getMeetingInvitors().size() > 0){
                return 3;
            }
            return 2;
        }
    }

    public void refresh(EventRefreshMembers eventRefreshMembers){
        this.meetingConfig = eventRefreshMembers.getMeetingConfig();
        init();
    }

    private void init(){
        try {
            membersPager.setSaveEnabled(false);
            membersPager.addOnPageChangeListener(this);
//            tab1.setText("主讲人" +"(" + meetingConfig.getMeetingMembers().size() + ")");
//            tab2.setText("参会者" +"(" + meetingConfig.getMeetingAuditor().size() + ")");
//            tab3.setText("被邀请人" +"(" + meetingConfig.getMeetingInvitors().size() + ")");
            if(meetingConfig.getMeetingInvitors().size() > 0){
                invitorsTab.setVisibility(View.VISIBLE);
            }else {
                invitorsTab.setVisibility(View.GONE);
            }
            if(membersAdapter != null){
                membersAdapter.notifyDataSetChanged();
            }
            initIndicators();
        }catch (Exception e){

        }
        setBindViewText();

    }

    private void initIndicators(){
        if(membersPager != null){
            int index = membersPager.getCurrentItem();
            Log.e("check_page_index","page_index:" + index);
            if(index == 0){
                indicator1.setVisibility(View.VISIBLE);
                indicator2.setVisibility(View.INVISIBLE);
                indicator3.setVisibility(View.INVISIBLE);
                tab1.setTextColor(host.getResources().getColor(R.color.skyblue));
                tab2.setTextColor(host.getResources().getColor(R.color.c5));
                tab3.setTextColor(host.getResources().getColor(R.color.c5));
            }else if(index == 1){
                indicator1.setVisibility(View.INVISIBLE);
                indicator2.setVisibility(View.VISIBLE);
                indicator3.setVisibility(View.INVISIBLE);
                tab1.setTextColor(host.getResources().getColor(R.color.c5));
                tab2.setTextColor(host.getResources().getColor(R.color.skyblue));
                tab3.setTextColor(host.getResources().getColor(R.color.c5));
            }else if(index == 2){
                indicator1.setVisibility(View.INVISIBLE);
                indicator2.setVisibility(View.INVISIBLE);
                indicator3.setVisibility(View.VISIBLE);
                tab1.setTextColor(host.getResources().getColor(R.color.c5));
                tab2.setTextColor(host.getResources().getColor(R.color.c5));
                tab3.setTextColor(host.getResources().getColor(R.color.skyblue));

            }
        }
    }

    private String getBindViewText(int fileId){
        String appBindName="";
        int language = sharedPreferences.getInt("language",1);
        if(language==1&&App.appENNames!=null){
            for(int i=0;i<App.appENNames.size();i++){
                if(fileId==App.appENNames.get(i).getFieldId()){
                    System.out.println("Name->"+App.appENNames.get(i).getFieldName());
                    appBindName=App.appENNames.get(i).getFieldName();
                    break;
                }
            }
        }else if(language==2&&App.appCNNames!=null){
            for(int i=0;i<App.appCNNames.size();i++){
                if(fileId==App.appCNNames.get(i).getFieldId()){
                    System.out.println("Name->"+App.appCNNames.get(i).getFieldName());
                    appBindName=App.appCNNames.get(i).getFieldName();
                    break;
                }
            }
        }
        return appBindName;
    }
    private void setBindViewText(){
        String member=getBindViewText(1024);
        tab1.setText(TextUtils.isEmpty(member)? getString(R.string.wxf_speakers)+"(" + meetingConfig.getMeetingMembers().size() + ")":member+"(" + meetingConfig.getMeetingMembers().size() + ")");
        String auditor=getBindViewText(1025);
        tab2.setText(TextUtils.isEmpty(auditor)? getString(R.string.wxf_attendee) +"(" + meetingConfig.getMeetingAuditor().size() + ")":auditor +"(" + meetingConfig.getMeetingAuditor().size() + ")");
        String invitors=getBindViewText(1026);
        tab3.setText(TextUtils.isEmpty(auditor)? getString(R.string.wxf_invite) +"(" + meetingConfig.getMeetingInvitors().size() + ")":invitors +"(" + meetingConfig.getMeetingInvitors().size() + ")");

    }
}
