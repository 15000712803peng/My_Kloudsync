package com.kloudsync.techexcel.help;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.EventShowMenuIcon;
import com.kloudsync.techexcel.bean.MeetingDocument;
import com.ub.techexcel.adapter.BottomFileAdapter;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PopBottomFile implements PopupWindow.OnDismissListener, OnClickListener {

    private PopupWindow bottomFileWindow;
    int width;
    private Context mContext;
    //--
    private RecyclerView fileList;
    private BottomFileAdapter adapter;
    private LinearLayout uploadLayout;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_add:
                if (uploadLayout.getVisibility() != View.VISIBLE) {
                    uploadLayout.setVisibility(View.VISIBLE);
                } else {
                    uploadLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.popup_bottom_file:
                if (uploadLayout.getVisibility() == View.VISIBLE) {
                    uploadLayout.setVisibility(View.GONE);
                } else {
                    if (bottomFileWindow != null) {
                        bottomFileWindow.dismiss();
                    }

                }
                break;
            case R.id.take_photo:
                if(uploadLayout != null){
                    uploadLayout.setVisibility(View.GONE);
                }
                if(bottomFileOperationsListener != null){
                    bottomFileOperationsListener.addFromCamera();
                }
                break;
            case R.id.file_library:
                if(uploadLayout != null){
                    uploadLayout.setVisibility(View.GONE);
                }
                if(bottomFileOperationsListener != null){
                    bottomFileOperationsListener.addFromPictures();
                }
                break;
            case R.id.save_file:
                if(uploadLayout != null){
                    uploadLayout.setVisibility(View.GONE);
                }
                if(bottomFileOperationsListener != null){
                    bottomFileOperationsListener.addFromFavorite();
                }
                break;
            case R.id.team_document:
                if(uploadLayout != null){
                    uploadLayout.setVisibility(View.GONE);
                }
                if(bottomFileOperationsListener != null){
                    bottomFileOperationsListener.addFromTeam();
                }
                break;

            case R.id.blank_file:
                if(uploadLayout != null){
                    uploadLayout.setVisibility(View.GONE);
                }
                if(bottomFileOperationsListener != null){
                    bottomFileOperationsListener.addBlankFile();
                }
                break;
        }
    }

    public interface BottomFileOperationsListener {
        void addFromTeam();
        void addFromCamera();
        void addFromPictures();
        void addFromFavorite();
        void addBlankFile();
    }

    private BottomFileOperationsListener bottomFileOperationsListener;

    public PopBottomFile(Context context) {
        this.mContext = context;
        getPopupWindow();
        bottomFileWindow.setAnimationStyle(R.style.PopupAnimation5);
    }


    public void getPopupWindow() {
        if (null != bottomFileWindow) {
            bottomFileWindow.dismiss();
            return;
        } else {
            init();
        }
    }


    public void init() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater
                .inflate(R.layout.pop_bottom_file, null);
        RelativeLayout filePop = (RelativeLayout) view.findViewById(R.id.popup_bottom_file);
        uploadLayout = (LinearLayout) view.findViewById(R.id.upload_linearlayout);
        filePop.setOnClickListener(this);

        fileList = (RecyclerView) view.findViewById(R.id.list_file);
        LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(mContext);
        linearLayoutManager3.setOrientation(LinearLayoutManager.HORIZONTAL);
        fileList.setLayoutManager(linearLayoutManager3);

        RelativeLayout take_photo = (RelativeLayout) view.findViewById(R.id.take_photo);
        RelativeLayout file_library = (RelativeLayout) view.findViewById(R.id.file_library);
        RelativeLayout favorite_file = (RelativeLayout) view.findViewById(R.id.save_file);
        RelativeLayout team_file = (RelativeLayout) view.findViewById(R.id.team_document);
        RelativeLayout blank_file = (RelativeLayout) view.findViewById(R.id.blank_file);


        ImageView selectfile = (ImageView) view.findViewById(R.id.img_add);

        selectfile.setOnClickListener(this);

        take_photo.setOnClickListener(this);

        file_library.setOnClickListener(this);

        favorite_file.setOnClickListener(this);

        team_file.setOnClickListener(this);

        blank_file.setOnClickListener(this);

        bottomFileWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, false);
        bottomFileWindow.setOnDismissListener(this);
        bottomFileWindow.setBackgroundDrawable(new BitmapDrawable());
        bottomFileWindow.setAnimationStyle(R.style.anination2);
        bottomFileWindow.setFocusable(true);

    }


    public void show(View view,PopBottomFile.BottomFileOperationsListener bottomMenuOperationsListener) {
        this.bottomFileOperationsListener = bottomMenuOperationsListener;
        if (bottomFileWindow == null) {
            init();
        }

        bottomFileWindow.setOnDismissListener(this);
        if (!bottomFileWindow.isShowing()) {
            bottomFileWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        }
    }


    public boolean isShowing() {
        if (bottomFileWindow != null) {
            return bottomFileWindow.isShowing();
        }
        return false;
    }

    public void hide() {
        if (bottomFileWindow != null) {
            bottomFileWindow.dismiss();
        }

    }

    @Override
    public void onDismiss() {
        EventBus.getDefault().post(new EventShowMenuIcon());
    }

    public void setDocuments(List<MeetingDocument> documents, int documentId, BottomFileAdapter.OnDocumentClickListener clickListener) {
        if (adapter == null) {
            adapter = new BottomFileAdapter(mContext, documents);
            adapter.setOnDocumentClickListener(clickListener);
            adapter.setDocumentId(documents,documentId);
            fileList.setAdapter(adapter);
        } else {
            adapter.setOnDocumentClickListener(clickListener);
            adapter.setDocumentId(documents,documentId);
            adapter.notifyDataSetChanged();
        }
    }

    public void addTempDoc(MeetingDocument tempDoc){
        if(adapter != null){
            adapter.addTempDocument(tempDoc);
        }
    }

    public void refreshTempDoc(String prompt,int progress){
        if(adapter != null){
            adapter.refreshTempDoc(progress,prompt);
        }
    }

    public void removeTempDoc(){
        if(adapter != null){
            adapter.removeTempDoc();
        }
    }

    public void openAndShowAdd(View view,BottomFileOperationsListener bottomFileOperationsListener){
        show(view,bottomFileOperationsListener);
        uploadLayout.setVisibility(View.VISIBLE);
    }

}
