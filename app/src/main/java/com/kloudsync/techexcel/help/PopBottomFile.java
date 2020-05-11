package com.kloudsync.techexcel.help;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.EventShowMenuIcon;
import com.kloudsync.techexcel.bean.MeetingDocument;
import com.kloudsync.techexcel.view.MyDialog;
import com.ub.techexcel.adapter.BottomFileAdapter;
import com.ub.techexcel.tools.Tools;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class PopBottomFile implements DialogInterface.OnDismissListener, OnClickListener, TextView.OnEditorActionListener {

    private MyDialog bottomFileWindow;
    int width;
    private Context mContext;
    //--
    private RecyclerView fileList;
    private BottomFileAdapter adapter;
    private LinearLayout uploadLayout;
	private RelativeLayout mRllListFile;
    private EditText mEtFileName;
    private InputMethodManager mImm;
    private List<MeetingDocument> mDocuments;
    private int mDocumentId;

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
            case R.id.fileststem_library:
                if(uploadLayout != null){
                    uploadLayout.setVisibility(View.GONE);
                }
                if(bottomFileOperationsListener != null){
                    bottomFileOperationsListener.addFromFileSystem();
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
        void addFromFileSystem();
        void addFromFavorite();
        void addBlankFile();
    }

    private BottomFileOperationsListener bottomFileOperationsListener;

    public PopBottomFile(Context context) {
        this.mContext = context;
        getPopupWindow();
//        bottomFileWindow.setAnimationStyle(R.style.PopupAnimation5);
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
        mImm = (InputMethodManager) mContext.getSystemService(INPUT_METHOD_SERVICE);
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater
                .inflate(R.layout.pop_bottom_file, null);
        RelativeLayout filePop = (RelativeLayout) view.findViewById(R.id.popup_bottom_file);
	    mRllListFile = view.findViewById(R.id.rll_list_file);
        uploadLayout = (LinearLayout) view.findViewById(R.id.upload_linearlayout);
        filePop.setOnClickListener(this);
        mEtFileName = view.findViewById(R.id.et_dialog_bottom_file_file_name);
        fileList = (RecyclerView) view.findViewById(R.id.list_file);
        LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(mContext);
//        linearLayoutManager3.setOrientation(LinearLayoutManager.HORIZONTAL);
        fileList.setLayoutManager(linearLayoutManager3);

        RelativeLayout take_photo = (RelativeLayout) view.findViewById(R.id.take_photo);
        RelativeLayout file_library = (RelativeLayout) view.findViewById(R.id.file_library);
        RelativeLayout fileststem_library = (RelativeLayout) view.findViewById(R.id.fileststem_library);
        RelativeLayout favorite_file = (RelativeLayout) view.findViewById(R.id.save_file);
        RelativeLayout team_file = (RelativeLayout) view.findViewById(R.id.team_document);
        RelativeLayout blank_file = (RelativeLayout) view.findViewById(R.id.blank_file);


        ImageView selectfile = (ImageView) view.findViewById(R.id.img_add);

        mEtFileName.setOnEditorActionListener(this);
        selectfile.setOnClickListener(this);

        take_photo.setOnClickListener(this);

        file_library.setOnClickListener(this);

        fileststem_library.setOnClickListener(this);

        favorite_file.setOnClickListener(this);

        team_file.setOnClickListener(this);

        blank_file.setOnClickListener(this);


//        if (Tools.isOrientationPortrait((Activity) mContext)) {
        bottomFileWindow = new MyDialog(mContext, R.style.my_dialog);
	    bottomFileWindow.setContentView(view);
	    bottomFileWindow.setCanceledOnTouchOutside(true);
      /*  } else {
            bottomFileWindow = new PopupWindow(view, mContext.getResources().getDimensionPixelOffset(R.dimen.dp_360), RelativeLayout.LayoutParams.MATCH_PARENT, false);
        }*/
        bottomFileWindow.setOnDismissListener(this);
//        bottomFileWindow.setBackgroundDrawable(new BitmapDrawable());
//        bottomFileWindow.setAnimationStyle(R.style.anination2);
//        bottomFileWindow.setFocusable(true);
    }


    public void show(View view,PopBottomFile.BottomFileOperationsListener bottomMenuOperationsListener) {
        this.bottomFileOperationsListener = bottomMenuOperationsListener;
        if (bottomFileWindow == null) {
            init();
        }

        /*if (Tools.isOrientationPortrait((Activity) mContext)) {
            mRllListFile.setBackgroundResource(R.drawable.shape_white_top_radius_15);
        } else {
            mRllListFile.setBackgroundResource(R.drawable.shape_white_left_radius_15);
        }*/
//        if (bottomFileWindow.isShowing()) {
	    WindowManager.LayoutParams layoutParams = bottomFileWindow.getWindow().getAttributes();
	    if (Tools.isOrientationPortrait((Activity) mContext)) {
		    mRllListFile.setBackgroundResource(R.drawable.shape_white_top_radius_15);
		    bottomFileWindow.getWindow().setGravity(Gravity.BOTTOM);
		    layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
		    layoutParams.height = mContext.getResources().getDimensionPixelOffset(R.dimen.dp_360);
//                bottomFileWindow.update(RelativeLayout.LayoutParams.MATCH_PARENT, mContext.getResources().getDimensionPixelOffset(R.dimen.dp_360));
	    } else {
		    mRllListFile.setBackgroundResource(R.drawable.shape_white_left_radius_15);
		    bottomFileWindow.getWindow().setGravity(Gravity.RIGHT);
		    layoutParams.width = mContext.getResources().getDimensionPixelOffset(R.dimen.dp_360);
		    layoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
		    bottomFileWindow.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //                bottomFileWindow.update(mContext.getResources().getDimensionPixelOffset(R.dimen.dp_360), RelativeLayout.LayoutParams.MATCH_PARENT);
	    }
        bottomFileWindow.getWindow().setAttributes(layoutParams);
//        }
      /*  if (!bottomFileWindow.isShowing()) {
            if (Tools.isOrientationPortrait((Activity) mContext)) {
                bottomFileWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
            } else {
                bottomFileWindow.showAtLocation(view, Gravity.RIGHT, 0, 0);
            }
        }*/
        if (!bottomFileWindow.isShowing()) {
	        bottomFileWindow.show();
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

    private void hideKeyBoard() {
        mImm.hideSoftInputFromWindow(mEtFileName.getWindowToken(), 0);
    }
    @Override
    public void onDismiss(DialogInterface dialog) {
        mEtFileName.setText("");
        EventBus.getDefault().post(new EventShowMenuIcon());
    }

    List<MeetingDocument> mSearchList = new ArrayList<>();

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_SEARCH && event.getAction() == KeyEvent.ACTION_DOWN)) {
            hideKeyBoard();
            String searchText = mEtFileName.getText().toString();
            if (TextUtils.isEmpty(searchText)) {
//                ToastUtils.show(mContext,R.string.the_content_can_not_be_blank);
                adapter.setDocumentId(mDocuments, mDocumentId);
                adapter.notifyDataSetChanged();
                return true;
            }
            mSearchList.clear();
            for (MeetingDocument meetingDocument : mDocuments) {
                if (meetingDocument.getFileName().contains(searchText)) {
                    mSearchList.add(meetingDocument);
                }
            }
            adapter.setDocumentId(mSearchList, mDocumentId);
            adapter.notifyDataSetChanged();
            return true;
        }
        return false;
    }

    public void setDocuments(List<MeetingDocument> documents, int documentId, BottomFileAdapter.OnDocumentClickListener clickListener) {
        mDocuments = documents;
        mDocumentId = documentId;
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
