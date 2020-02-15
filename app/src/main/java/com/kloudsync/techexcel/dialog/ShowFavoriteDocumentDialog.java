package com.kloudsync.techexcel.dialog;

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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 2017/9/18.
 */

public class ShowFavoriteDocumentDialog {

    public Context mContext;
    public int width;
    public PopupWindow mPopupWindow;
    private List<Document> list = new ArrayList<Document>();
    private DocumentAdapter mDocumentAdapter;
    private ListView listView;
    private View view;
    private ImageView addsavefile;
    private int type;

    public void getPopwindow(Context context, List<Document> list, int type) {
        this.mContext = context;
        this.list = list;
        this.type = type;
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

        void uploadFile();

        void dismiss();

        void open();
    }

    public void setFavoritePoPListener(FavoritePoPListener documentPoPListener) {
        this.mFavoritePoPListener = documentPoPListener;
    }

    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.popup_document, null);
        listView = (ListView) view.findViewById(R.id.listview);
        addsavefile = (ImageView) view.findViewById(R.id.addsavefile);
        if (type == 2) {
            addsavefile.setVisibility(View.VISIBLE);
        } else if (type == 1) {
            addsavefile.setVisibility(View.GONE);
        }

        mDocumentAdapter = new DocumentAdapter(mContext, list,
                R.layout.popup_document_item);
        listView.setAdapter(mDocumentAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (list.get(position).getFlag() == 0) {
                    mFavoritePoPListener.selectFavorite(position);
                }
            }
        });
        addsavefile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFavoritePoPListener.uploadFile();
            }
        });
        mPopupWindow = new PopupWindow(view,ViewGroup.LayoutParams.WRAP_CONTENT ,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        mPopupWindow.getWidth();
        mPopupWindow.getHeight();
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mFavoritePoPListener.dismiss();
            }
        });
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }


    @SuppressLint("NewApi")
    public void StartPop(View v) {
        if (mPopupWindow != null) {
            mFavoritePoPListener.open();
            mPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
        }
    }


    public void dismiss() {
        if (mPopupWindow != null) {
            mFavoritePoPListener.open();
            mPopupWindow.dismiss();
        }
    }

    public class DocumentAdapter extends BaseAdapter {
        private Context context;
        private List<Document> mDatas;
        private int itemLayoutId;

        public DocumentAdapter(Context context, List<Document> mDatas,
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
            Document favorite = mDatas.get(position);

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
            holder.values.setText(favorite.getTitle());

            if (favorite.getFlag() == 0) {
                holder.rpb_update.setVisibility(View.GONE);
                holder.bgisshow2.setVisibility(View.GONE);
                holder.uploadstatus.setVisibility(View.GONE);
            } else if (favorite.getFlag() == 1) {
                holder.rpb_update.setVisibility(View.VISIBLE);
                holder.bgisshow2.setVisibility(View.VISIBLE);
                holder.rpb_update.setCricleProgressColor(context.getResources().getColor(R.color.white));
                holder.rpb_update.setProgress(favorite.getProgress());
                holder.uploadstatus.setVisibility(View.VISIBLE);
                holder.uploadstatus.setText("Uploading");
            } else {
                holder.rpb_update.setVisibility(View.VISIBLE);
                holder.bgisshow2.setVisibility(View.VISIBLE);
                holder.rpb_update.setCricleProgressColor(context.getResources().getColor(R.color.back_main));
                holder.rpb_update.setProgress(favorite.getProgress());
                holder.uploadstatus.setVisibility(View.VISIBLE);
                holder.uploadstatus.setText("Converting");
            }
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
