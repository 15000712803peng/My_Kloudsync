package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.OutlineChapterItem;
import com.kloudsync.techexcel.bean.OutlineChildSectionItem;
import com.kloudsync.techexcel.bean.OutlineSectionItem;
import com.kloudsync.techexcel.bean.SyncBook;
import com.kloudsync.techexcel.bean.SyncbookInfo;
import com.kloudsync.techexcel.response.NetworkResponse;
import com.kloudsync.techexcel.viewtree.AndroidTreeView;
import com.kloudsync.techexcel.viewtree.OutlineChapterItemHolder;
import com.kloudsync.techexcel.viewtree.OutlineChildChildChildSectionItemHolder;
import com.kloudsync.techexcel.viewtree.OutlineChildChildSectionItemHolder;
import com.kloudsync.techexcel.viewtree.OutlineChildSectionItemHolder;
import com.kloudsync.techexcel.viewtree.OutlineSectionItemHolder;
import com.kloudsync.techexcel.viewtree.mode.TreeNode;
import com.ub.techexcel.bean.LineItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;


public class SyncRoomOutlinePopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public Dialog mPopupWindow;
    private View view;
    AndroidTreeView outlineView;
    SyncbookInfo syncbookInfo;
    LinearLayout outlineLayout;
    TextView docNameText;
    private String syncroomId;

    public interface OutlinePopupEventListener{
        void onOutlinePopOpen();
        void onOutlinePopClose();
    }

    OutlinePopupEventListener outlinePopupEventListener;

    public OutlinePopupEventListener getOutlinePopupEventListener() {
        return outlinePopupEventListener;
    }

    public void setOutlinePopupEventListener(OutlinePopupEventListener outlinePopupEventListener) {
        this.outlinePopupEventListener = outlinePopupEventListener;
    }

    public String getSyncroomId() {
        return syncroomId;
    }

    public void setSyncroomId(String syncroomId) {
        this.syncroomId = syncroomId;
    }

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

    private TreeNode parseSyncbook(SyncbookInfo syncbookInfo, final TreeNode rootNode) {
        TreeNode root = null;
        List<OutlineChapterItem> chapterItems = syncbookInfo.getChapterItems();
        if (chapterItems != null && chapterItems.size() > 0) {

            for (int chapterIndex = 0; chapterIndex < chapterItems.size(); ++chapterIndex) {
                OutlineChapterItem chapterItem = chapterItems.get(chapterIndex);

                if (chapterItem.getSectionItems() == null || chapterItem.getSectionItems().size() <= 0) {
                    continue;
                }

                int cIndex = chapterIndex <= 0 ? 0 : chapterIndex;
                final int currentChapterIndex = cIndex;

                TreeNode chapterNode = new TreeNode(chapterItem).setViewHolder
                        (new OutlineChapterItemHolder(mContext));
                chapterNode.setValueId(chapterItem.getIdeaID() + "-" + chapterItem.getOutLineID());
                chapterItem.setTreeNodeId(chapterItem.getIdeaID() + "-" + chapterItem.getOutLineID());

                chapterNode.setClickListener(new TreeNode.TreeNodeClickListener() {
                    @Override
                    public void onClick(TreeNode node, Object value) {
                        if (onChapterClickedListener != null) {
                            if(outlineView != null){
                                outlineView.justHightOneNode(rootNode,node.getValueId());
                            }
                            onChapterClickedListener.onChapterClicked((OutlineChapterItem) value, currentChapterIndex);
                        }
                    }
                });
                List<OutlineSectionItem> sectionItems = chapterItem.getSectionItems();
                if (sectionItems != null && sectionItems.size() > 0) {

                    for (int i = 0; i < sectionItems.size(); ++i) {

                        TreeNode sectionItemNode = new TreeNode(sectionItems.get(i)).setViewHolder
                                (new OutlineSectionItemHolder(mContext));

                        sectionItemNode.setValueId(sectionItems.get(i).getIdeaID() + "-" + sectionItems.get(i).getOutLineID());
                        sectionItems.get(i).setTreeNodeId(sectionItems.get(i).getIdeaID() + "-" + sectionItems.get(i).getOutLineID());


                        sectionItemNode.setClickListener(new TreeNode.TreeNodeClickListener() {
                            @Override
                            public void onClick(TreeNode node, Object value) {
                                if (onChapterClickedListener != null) {
                                    if(outlineView != null){
                                        outlineView.justHightOneNode(rootNode,node.getValueId());
                                    }
                                    onChapterClickedListener.onSectionItemClicked((OutlineSectionItem) value,currentChapterIndex);
                                }
                            }
                        });


                        List<OutlineChildSectionItem> childSectionItems = sectionItems.get(i).getChildSectionItems();
                        if (childSectionItems != null && childSectionItems.size() > 0) {
                            for (OutlineChildSectionItem childSectionItem : childSectionItems) {
                                TreeNode childSectionNode = new TreeNode(childSectionItem).setViewHolder
                                        (new OutlineChildSectionItemHolder(mContext));

                                childSectionNode.setValueId(childSectionItem.getIdeaID() + "-" + childSectionItem.getOutLineID());
                                childSectionItem.setTreeNodeId(childSectionItem.getIdeaID() + "-" + childSectionItem.getOutLineID());


                                childSectionNode.setClickListener(new TreeNode.TreeNodeClickListener() {
                                    @Override
                                    public void onClick(TreeNode node, Object value) {
                                        if (onChapterClickedListener != null) {
                                            if(outlineView != null){
                                                outlineView.justHightOneNode(rootNode,node.getValueId());
                                            }
                                            onChapterClickedListener.onChildSectionItemClicked((OutlineChildSectionItem) value,currentChapterIndex);
                                        }
                                    }
                                });

                                //---

                                List<OutlineChildSectionItem> childChildSectionItems = childSectionItem.getChildSectionItems();
                                if (childChildSectionItems != null &&
                                        childChildSectionItems.size() > 0) {
                                    for (OutlineChildSectionItem childChildSectionItem : childChildSectionItems) {
                                        TreeNode childChildSectionNode = new TreeNode(childChildSectionItem).setViewHolder
                                                (new OutlineChildChildSectionItemHolder(mContext));
                                        childChildSectionNode.setValueId(childChildSectionItem.getIdeaID() + "-" + childChildSectionItem.getOutLineID());
                                        childChildSectionItem.setTreeNodeId(childChildSectionItem.getIdeaID() + "-" + childChildSectionItem.getOutLineID());
                                        childChildSectionNode.setClickListener(new TreeNode.TreeNodeClickListener() {
                                            @Override
                                            public void onClick(TreeNode node, Object value) {
                                                if (onChapterClickedListener != null) {
                                                    if(outlineView != null){
                                                        outlineView.justHightOneNode(rootNode,node.getValueId());
                                                    }
                                                    onChapterClickedListener.onChildSectionItemClicked((OutlineChildSectionItem) value,currentChapterIndex);
                                                }
                                            }
                                        });
                                        // -----
                                        List<OutlineChildSectionItem> childChildChildSectionItems = childChildSectionItem.getChildSectionItems();
                                        if (childChildChildSectionItems != null && childChildChildSectionItems.size() > 0) {
                                            for (OutlineChildSectionItem childChildChildSectionItem : childChildChildSectionItems) {
                                                TreeNode childChildChildSectionNode = new TreeNode(childChildChildSectionItem).setViewHolder
                                                        (new OutlineChildChildChildSectionItemHolder(mContext));
                                                childChildChildSectionNode.setValueId(childChildChildSectionItem.getIdeaID() + "-"+childChildChildSectionItem.getOutLineID());
                                                childChildChildSectionItem.setTreeNodeId(childChildChildSectionItem.getIdeaID() + "-"+childChildChildSectionItem.getOutLineID());

                                                childChildChildSectionNode.setClickListener(new TreeNode.TreeNodeClickListener() {
                                                    @Override
                                                    public void onClick(TreeNode node, Object value) {
                                                        if (onChapterClickedListener != null) {
                                                            if(outlineView != null){
                                                                outlineView.justHightOneNode(rootNode,node.getValueId());
                                                            }
                                                            onChapterClickedListener.onChildSectionItemClicked((OutlineChildSectionItem) value,currentChapterIndex);
                                                        }
                                                    }
                                                });
                                                childChildSectionNode.addChild(childChildChildSectionNode);
                                            }

                                        }

                                        childSectionNode.addChild(childChildSectionNode);

                                    }
                                }

                                sectionItemNode.addChild(childSectionNode);
                            }
                        }

                        chapterNode.addChild(sectionItemNode);
                    }


                }
                rootNode.addChild(chapterNode);
            }

        }

        return root;

    }


    public void initPopuptWindow() {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.syncroom_outline_popup, null);

        outlineLayout = (LinearLayout) view.findViewById(R.id.layout_outline);
        docNameText = (TextView) view.findViewById(R.id.txt_doc_name);

        mPopupWindow = new Dialog(mContext, R.style.my_dialog);
        mPopupWindow.setContentView(view);
        mPopupWindow.getWindow().setGravity(Gravity.RIGHT);
        WindowManager.LayoutParams params = mPopupWindow.getWindow().getAttributes();
//        DisplayMetrics dm = new DisplayMetrics();
//        (((Activity)mContext).getWindowManager()).getDefaultDisplay().getRealMetrics(dm);
        View root = ((Activity) mContext).getWindow().getDecorView();
        params.height = root.getMeasuredHeight();
        mPopupWindow.getWindow().setAttributes(params);
        mPopupWindow.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mPopupWindow.getWindow().setWindowAnimations(R.style.anination3);
        mPopupWindow.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(outlinePopupEventListener != null){
                    outlinePopupEventListener.onOutlinePopClose();
                }
            }
        });
        mPopupWindow.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if(outlinePopupEventListener != null){
                    outlinePopupEventListener.onOutlinePopClose();
                }
            }
        });
        new GetOutlineTask(mContext).execute();

    }


    @SuppressLint("NewApi")
    public void StartPop(View v, List<LineItem> list) {
        if (mPopupWindow != null) {
            mPopupWindow.show();
            if(outlinePopupEventListener != null){
                outlinePopupEventListener.onOutlinePopOpen();
            }


        }
    }

    public boolean isShowing() {
        return mPopupWindow.isShowing();
    }

    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
            mPopupWindow.cancel();
            mPopupWindow = null;
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.closebnt:
                break;
            case R.id.adddocument:

                break;
            default:
                break;
        }
    }


    private class GetOutlineTask extends AsyncTask<Void, Void, SyncbookInfo> {
        private Context context;

        GetOutlineTask(Context context) {
            this.context = context;
        }

        @Override
        protected SyncbookInfo doInBackground(Void... params) {

            try {
              Response<NetworkResponse<SyncBook>> response = ServiceInterfaceTools.getinstance().getSyncbookOutline(syncroomId).execute();
              if(response != null && response.isSuccessful() && response.body() != null){

                  try {
                      JSONObject jsonObject = new JSONObject(response.body().getRetData().getOutlineInfo());
                      return new Gson().fromJson(jsonObject.getJSONObject("BookInfo").toString(),SyncbookInfo.class);
                  } catch (JSONException e) {
                      e.printStackTrace();
                      return null;
                  }

              }else {
                  return null;
              }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(SyncbookInfo syncbookInfo) {
            super.onPostExecute(syncbookInfo);
            rootNode = TreeNode.root();

            Log.e("SyncbookInfo", "" + syncbookInfo);
            if (syncbookInfo != null) {
                parseSyncbook(syncbookInfo, rootNode);
                outlineView = new AndroidTreeView(mContext, rootNode);
                outlineView.setDefaultAnimation(true);
                outlineView.setUse2dScroll(true);
                outlineLayout.addView(outlineView.getView());
                outlineView.setDefaultNodeClickListener(new TreeNode.TreeNodeClickListener() {
                    @Override
                    public void onClick(TreeNode node, Object value) {

                    }
                });
                docNameText.setText(syncbookInfo.getBookTitle());
            }

        }
    }


    public interface OnChapterClickedListener {
        void onChapterClicked(OutlineChapterItem chapterItem, int chapterIndex);

        void onSectionItemClicked(OutlineSectionItem sectionItem,int chapterIndex);

        void onChildSectionItemClicked(OutlineChildSectionItem childSectionItem,int chapterIndex);
    }

    private OnChapterClickedListener onChapterClickedListener;


    public void setOnChapterClickedListener(OnChapterClickedListener onChapterClickedListener) {
        this.onChapterClickedListener = onChapterClickedListener;
    }

    List<TreeNode> allTreeNode;

    public void toggleTreeNode(String valueId,boolean isToggle) {

        if (allTreeNode == null) {
            if(rootNode != null){
                allTreeNode = outlineView.getAll(rootNode);
            }
        }

        if(outlineView != null){
            if(allTreeNode != null && allTreeNode.size() > 0){
                for(TreeNode treeNode : allTreeNode){
                    if(treeNode.getValueId().equals(valueId)){
                        Log.e("toggleTreeNode", "value id:" + valueId);
                        if(isToggle){
                            outlineView.expandNode(treeNode);
                        }else {
                            outlineView.collapseNode(treeNode);
                        }
                        break;
                    }
                }
            }
        }

    }

    TreeNode rootNode;

    public void justHightOneNode(String nodeValueId){
        if(outlineView != null && rootNode != null){
            outlineView.justHightOneNode(rootNode,nodeValueId);
        }
    }


}
