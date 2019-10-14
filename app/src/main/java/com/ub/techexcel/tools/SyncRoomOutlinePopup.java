package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.OutlineChapterItem;
import com.kloudsync.techexcel.bean.OutlineChildSectionItem;
import com.kloudsync.techexcel.bean.OutlineSectionItem;
import com.kloudsync.techexcel.bean.SyncbookInfo;
import com.kloudsync.techexcel.viewtree.AndroidTreeView;
import com.kloudsync.techexcel.viewtree.OutlineChildSectionItemHolder;
import com.kloudsync.techexcel.viewtree.mode.TreeNode;
import com.ub.techexcel.bean.LineItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class SyncRoomOutlinePopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public Dialog mPopupWindow;
    private View view;
    AndroidTreeView outlineView;
    SyncbookInfo syncbookInfo;

    public void getPopwindow(Context context) {
        this.mContext = context;
        width = mContext.getResources().getDisplayMetrics().widthPixels;
        getPopupWindowInstance();
    }

    public void getPopupWindowInstance() {
        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }

    private void parseSyncbook(SyncbookInfo syncbookInfo, AndroidTreeView outlineView) {
        List<OutlineChapterItem> chapterItems = syncbookInfo.getChapterItems();
        if (chapterItems != null && chapterItems.size() > 0) {
            for (OutlineChapterItem chapterItem : syncbookInfo.getChapterItems()) {
                TreeNode chapterNode = new TreeNode("chapterNode").setViewHolder
                        (new OutlineChildSectionItemHolder(mContext));
                List<OutlineSectionItem> sectionItems = chapterItem.getSectionItems();
                if (sectionItems != null && sectionItems.size() > 0) {

                    for (OutlineSectionItem sectionItem : sectionItems) {
                        TreeNode sectionItemNode = new TreeNode("sectionItem").setViewHolder
                                (new OutlineChildSectionItemHolder(mContext));

                        List<OutlineChildSectionItem> childSectionItems = sectionItem.getChildSectionItems();
                        if (childSectionItems != null && childSectionItems.size() > 0) {
                            for (OutlineChildSectionItem childSectionItem : childSectionItems) {
                                TreeNode childSectionNode = new TreeNode("childSetionItem").setViewHolder
                                        (new OutlineChildSectionItemHolder(mContext));

                                OutlineChildSectionItem tempSectionItme = childSectionItem;
                                while (tempSectionItme.getChildSectionItems() != null &&
                                        tempSectionItme.getChildSectionItems().size() > 0) {
                                    for (OutlineChildSectionItem childSectionItem2 : tempSectionItme.getChildSectionItems()) {
                                        TreeNode childSectionNode2 = new TreeNode("childSetionItem").setViewHolder
                                                (new OutlineChildSectionItemHolder(mContext));
                                    }
                                }


                            }
                        }

                    }


                }
            }
        }

    }

    LinearLayout upload_linearlayout;
    LinearLayout morell;

    public void initPopuptWindow() {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.syncroom_document_popup, null);

        TreeNode rootNode = TreeNode.root();


        outlineView = new AndroidTreeView(mContext, rootNode);
        outlineView.setDefaultAnimation(true);
        outlineView.setUse2dScroll(true);


        mPopupWindow = new Dialog(mContext, R.style.my_dialog);
        mPopupWindow.setContentView(view);
        mPopupWindow.getWindow().setGravity(Gravity.LEFT);
        WindowManager.LayoutParams params = mPopupWindow.getWindow().getAttributes();
//        DisplayMetrics dm = new DisplayMetrics();
//        (((Activity)mContext).getWindowManager()).getDefaultDisplay().getRealMetrics(dm);
        View root = ((Activity) mContext).getWindow().getDecorView();
        params.height = root.getMeasuredHeight();
        mPopupWindow.getWindow().setAttributes(params);
        mPopupWindow.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mPopupWindow.getWindow().setWindowAnimations(R.style.anination3);


    }


    @SuppressLint("NewApi")
    public void StartPop(View v, List<LineItem> list) {
        if (mPopupWindow != null) {
            webCamPopupListener.open();
            mPopupWindow.show();


        }
    }

    public boolean isShowing() {
        return mPopupWindow.isShowing();
    }

    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }


    public interface WebCamPopupListener {

        void changeOptions(LineItem syncRoomBean);

        void teamDocument();

        void takePhoto();

        void importFromLibrary();

        void savedFile();

        void dismiss();

        void open();

        void delete(LineItem selectLineItem);

        void edit(LineItem selectLineItem);


    }

    public void setWebCamPopupListener(WebCamPopupListener webCamPopupListener) {
        this.webCamPopupListener = webCamPopupListener;
    }

    private WebCamPopupListener webCamPopupListener;


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.closebnt:
                mPopupWindow.dismiss();
                break;
            case R.id.adddocument:

                break;
            default:
                break;
        }
    }


}
