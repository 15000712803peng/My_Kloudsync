package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.ub.techexcel.adapter.MyRecyclerAdapter2;
import com.ub.techexcel.bean.LineItem;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.info.Customer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 2017/9/18.
 */

public class VideoPopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    private int screenWidth;
    private PopupWindow videoPopupWindow;
    private LinearLayout upload_linearlayout;
    private RelativeLayout auditor;
    private RelativeLayout bottomrl;
    private RecyclerView documentrecycleview;
    private List<LineItem> videoList = new ArrayList<>();
    private MyRecyclerAdapter2 myRecyclerAdapter2;


    public void notifyDataChange(int position) {
        for (int i = 0; i < videoList.size(); i++) {
            LineItem lineitem = videoList.get(i);
            if (i == position) {  //选中的item
                lineitem.setSelect(true);
            } else {
                lineitem.setSelect(false);
            }
        }
        if(myRecyclerAdapter2!=null){
            myRecyclerAdapter2.notifyDataSetChanged();
        }
    }


    public void setVideoList(final List<LineItem> videoList2) {
        this.videoList = videoList2;
        myRecyclerAdapter2 = new MyRecyclerAdapter2(mContext, videoList);
        documentrecycleview.setAdapter(myRecyclerAdapter2);
        myRecyclerAdapter2.setMyItemClickListener(new MyRecyclerAdapter2.MyItemClickListener() {
            @Override
            public void onItemClick(int position) {  // 播放视频按钮 （权限）
                if (identity == 1) { // 学生
                    if (TextUtils.isEmpty(studentCustomer.getUserID())) {
                        return;
                    }
                    if (studentCustomer.getUserID().equals(AppConfig.UserID.replace("-", ""))) {
                        videoListener.selectPlayVideo(position);
                    }
                } else if (identity == 2) { //老师端 绘制
                    if (currentPresenterId.equals(teacherCustomer.getUserID())) {
                        videoListener.selectPlayVideo(position);
                    }
                }
            }
        });
    }


    public void getPopwindow(Context context, int screenWidth, RelativeLayout bottomrl) {
        this.mContext = context;
        this.bottomrl = bottomrl;
        this.screenWidth = screenWidth;
        getPopupWindowInstance();

    }

    public void getPopupWindowInstance() {
        if (videoPopupWindow != null) {
            videoPopupWindow.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }


    public void initPopuptWindow() {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.video_pop, null);

        upload_linearlayout = (LinearLayout) view.findViewById(R.id.video_linearlayout);

        auditor = (RelativeLayout) view.findViewById(R.id.auditor);
        auditor.setOnClickListener(this);

        documentrecycleview = (RecyclerView) view.findViewById(R.id.recycleview2);
        LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(mContext);
        linearLayoutManager3.setOrientation(LinearLayoutManager.HORIZONTAL);
        documentrecycleview.setLayoutManager(linearLayoutManager3);

        ImageView selectfile = (ImageView) view.findViewById(R.id.selectfile);
        selectfile.setOnClickListener(this);

        RelativeLayout file_library = (RelativeLayout) view.findViewById(R.id.video_library);
        file_library.setOnClickListener(this);
        RelativeLayout take_photo = (RelativeLayout) view.findViewById(R.id.take_photo);
        take_photo.setOnClickListener(this);
        RelativeLayout save_video = (RelativeLayout) view.findViewById(R.id.save_video);
        save_video.setOnClickListener(this);

        videoPopupWindow = new PopupWindow(view, screenWidth,
                ViewGroup.LayoutParams.MATCH_PARENT);
        videoPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                bottomrl.setVisibility(View.VISIBLE);
            }
        });
        videoPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        videoPopupWindow.setAnimationStyle(R.style.anination2);
        videoPopupWindow.setFocusable(true);

    }


    @SuppressLint("NewApi")
    public void startVideoPop(View v, LinearLayout menu_linearlayout, ImageView menu) {
        if (videoPopupWindow != null) {
            videoPopupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
            menu_linearlayout.setVisibility(View.GONE);
            menu.setImageResource(R.drawable.icon_menu);
            bottomrl.setVisibility(View.GONE);
        }
    }


    private int identity;
    private String currentPresenterId;
    private Customer studentCustomer, teacherCustomer;

    public void setPresenter(int identity,
                             String currentPresenterId,
                             Customer studentCustomer, Customer teacherCustomer) {
        this.identity = identity;
        this.currentPresenterId = currentPresenterId;
        this.studentCustomer = studentCustomer;
        this.teacherCustomer = teacherCustomer;

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.selectfile: // 弹出上传视频按钮（权限）
                if (identity == 1) { // 学生
                    if (TextUtils.isEmpty(studentCustomer.getUserID())) {
                        return;
                    }
                    if (studentCustomer.getUserID().equals(AppConfig.UserID.replace("-", ""))) {
                        if (upload_linearlayout.getVisibility() == View.GONE) {
                            upload_linearlayout.setVisibility(View.VISIBLE);
                        } else if (upload_linearlayout.getVisibility() == View.VISIBLE) {
                            upload_linearlayout.setVisibility(View.GONE);
                        }
                    }
                } else if (identity == 2) { // 老师端 绘制
                    if (currentPresenterId.equals(teacherCustomer.getUserID())) {
                        if (upload_linearlayout.getVisibility() == View.GONE) {
                            upload_linearlayout.setVisibility(View.VISIBLE);
                        } else if (upload_linearlayout.getVisibility() == View.VISIBLE) {
                            upload_linearlayout.setVisibility(View.GONE);
                        }
                    }
                } else {
                    upload_linearlayout.setVisibility(View.GONE);
                }
                break;
            case R.id.auditor:
                if (upload_linearlayout.getVisibility() == View.VISIBLE) {
                    upload_linearlayout.setVisibility(View.GONE);
                } else {
                    videoPopupWindow.dismiss();
                }
                break;
            case R.id.video_library:  //打开选择视频路径
                upload_linearlayout.setVisibility(View.GONE);
                videoListener.selectVideo();
                break;
            case R.id.take_photo:
                upload_linearlayout.setVisibility(View.GONE);
                videoListener.takePhoto();
                break;
            case R.id.save_video:
                upload_linearlayout.setVisibility(View.GONE);
                videoListener.openSaveVideo();
                break;
            default:
                break;
        }
    }


    public interface VideoListener {

        void selectVideo();

        void takePhoto();

        void selectPlayVideo(int position);

        void openSaveVideo();

    }

    public VideoListener videoListener;

    public void setOnVideoListener(VideoListener videoListener) {
        this.videoListener = videoListener;
    }


}
