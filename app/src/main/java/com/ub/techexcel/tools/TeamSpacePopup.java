package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.view.RoundProgressBar;
import com.ub.kloudsync.activity.Document;
import com.ub.kloudsync.activity.TeamSpaceBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 2017/9/18.
 */

public class TeamSpacePopup {

    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private List<TeamSpaceBean> list = new ArrayList<TeamSpaceBean>();
    private DocumentAdapter mDocumentAdapter;
    private ListView listView;
    private View view;

    private ImageView addsavefile;


    public void getPopwindow(Context context, List<TeamSpaceBean> list) {
        this.mContext = context;
        this.list = list;

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

    private static FavoritePoPListener mFavoritePoPListener;


    public interface FavoritePoPListener {

        void selectFavorite(int position);

    }

    public void setFavoritePoPListener(FavoritePoPListener documentPoPListener) {
        this.mFavoritePoPListener = documentPoPListener;
    }

    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.popup_document, null);
        listView = (ListView) view.findViewById(R.id.listview);
        addsavefile = (ImageView) view.findViewById(R.id.addsavefile);
        addsavefile.setVisibility(View.GONE);

        mDocumentAdapter = new DocumentAdapter(mContext, list,
                R.layout.popup_document_item);
        listView.setAdapter(mDocumentAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                mFavoritePoPListener.selectFavorite(position);
            }
        });
        mPopupWindow = new PopupWindow(view, width / 2,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        mPopupWindow.getWidth();
        mPopupWindow.getHeight();

        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }


    @SuppressLint("NewApi")
    public void StartPop(View v) {
        if (mPopupWindow != null) {
            mPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
        }
    }


    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
            mPopupWindow=null;
        }
    }

    public class DocumentAdapter extends BaseAdapter {
        private Context context;
        private List<TeamSpaceBean> mDatas;
        private int itemLayoutId;

        public DocumentAdapter(Context context, List<TeamSpaceBean> mDatas,
                               int itemLayoutId) {
            this.context = context;
            this.mDatas = mDatas;
            this.itemLayoutId = itemLayoutId;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mDatas.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            ViewHolder holder = null;
            TeamSpaceBean teamSpaceBean = mDatas.get(position);

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(
                        itemLayoutId, null);
                holder.values = (TextView) convertView
                        .findViewById(R.id.document_name);
                holder.uploadstatus = (TextView) convertView
                        .findViewById(R.id.uploadstatus);
                holder.rpb_update = (RoundProgressBar) convertView
                        .findViewById(R.id.rpb_update);
                holder.bgisshow2 = (LinearLayout) convertView
                        .findViewById(R.id.bgisshow2);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.values.setText(teamSpaceBean.getName());

            holder.rpb_update.setVisibility(View.GONE);
            holder.bgisshow2.setVisibility(View.GONE);
            holder.uploadstatus.setVisibility(View.GONE);
            return convertView;
        }

        class ViewHolder {
            TextView values,uploadstatus;
            RoundProgressBar rpb_update;
            LinearLayout bgisshow2;
        }
    }

    public void setProgress(long total, long current, Document att) {
        int pb = (int) (current * 100 / total);
        att.setProgress(pb);
        mDocumentAdapter.notifyDataSetChanged();
    }


    public void notifyDataSetChanged() {
        mDocumentAdapter.notifyDataSetChanged();
    }

}
