package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.info.ConvertingResult;
import com.kloudsync.techexcel.start.LoginGet;
import com.ub.kloudsync.activity.Document;

import java.util.ArrayList;
import java.util.List;

public class MyFavoriteVideoPopup {

    public Context mContext;
    public int width;
    public Dialog mPopupWindow;
    private List<Document> list = new ArrayList<Document>();
    private AudiosAdapter adapter;
    private ListView listView;
    private View view;
    private int type = 0;
    private boolean isYinxiang=false;
    private TextView cancelText;

    public void getPopwindow(Context context) {
        this.mContext = context;
        width = mContext.getResources().getDisplayMetrics().widthPixels;
        getPopupWindowInstance();
    }

    public MyFavoriteVideoPopup(Context context) {
        this.mContext = context;
        initPopuptWindow();
    }

    public void getPopupWindowInstance() {
        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }


    public void setData() {  // 2   video  3   audio
        selectPosition=-1;
        LoginGet loginGet = new LoginGet();
        loginGet.setMyFavoritesGetListener(new LoginGet.MyFavoritesGetListener() {
            @Override
            public void getFavorite(ArrayList<Document> list2) {
                list.clear();
                list.addAll(list2);
                adapter.notifyDataSetChanged();
            }
        });
        loginGet.MyFavoriteRequest(mContext, 3);
    }



    private static FavoriteVideoPoPListener mFavoritePoPListener;

    public interface FavoriteVideoPoPListener {
        void save(Document document);

    }

    public void setFavoritePoPListener(FavoriteVideoPoPListener documentPoPListener) {
        this.mFavoritePoPListener = documentPoPListener;
    }

    int selectPosition = -1;

    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.my_save_audio_popup, null);
        listView = (ListView) view.findViewById(R.id.listview);
        cancelText = view.findViewById(R.id.cancel);
        cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.cancel();
            }
        });
        adapter = new AudiosAdapter(mContext, list,
                R.layout.popup_video_item);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                selectPosition = position;
                adapter.notifyDataSetChanged();
            }
        });
        view.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectPosition>0&&selectPosition<list.size()){
                    mFavoritePoPListener.save(list.get(selectPosition));


//                    ConvertingResult convertingResult=new ConvertingResult();
//                    convertingResult.setFileName(fileNamecon);
//                    convertingResult.setCount(1);
//                    ServiceInterfaceTools.getinstance().uploadFavoriteNewFile(AppConfig.URL_PUBLIC + "FavoriteAttachment/UploadNewFile",
//                            ServiceInterfaceTools.UPLOADFAVORITENEWFILE,
//                            fileName, "", fileHash,
//                            convertingResult, field, new ServiceInterfaceListener() {
//                                @Override
//                                public void getServiceReturnData(Object object) {
//                                    Log.e("UploadMp3File", "FavoriteAttachment/UploadNewFile");
//                                    if (uploadDetailLinstener != null) {
//                                        uploadDetailLinstener.uploadFinished(object);
//                                    }
//                                }
//                            }
//                    );


                }
                mPopupWindow.dismiss();
            }
        });


        mPopupWindow = new Dialog(mContext, R.style.my_dialog);
        mPopupWindow.setContentView(view);
        mPopupWindow.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    @SuppressLint("NewApi")
    public void StartPop(View v) {
        if (mPopupWindow != null) {
            WindowManager.LayoutParams params = mPopupWindow.getWindow().getAttributes();
            if (Tools.isOrientationPortrait((Activity) mContext)) {
                View root = ((Activity) mContext).getWindow().getDecorView();
                params.width = root.getMeasuredWidth() * 9 / 10;
                params.height = mContext.getResources().getDisplayMetrics().heightPixels * 1 / 2;
            } else {
                params.width = mContext.getResources().getDisplayMetrics().widthPixels * 3 / 5;
                View root = ((Activity) mContext).getWindow().getDecorView();
                params.height = root.getMeasuredHeight() * 4 / 5 + 30;
            }
            mPopupWindow.getWindow().setAttributes(params);
            mPopupWindow.show();
            setData();
        }
    }


    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    public class AudiosAdapter extends BaseAdapter {
        private Context context;
        private List<Document> mDatas;
        private int itemLayoutId;

        public AudiosAdapter(Context context, List<Document> mDatas,
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
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(
                        itemLayoutId, null);
                holder.name = (TextView) convertView
                        .findViewById(R.id.name);
                holder.size = (TextView) convertView
                        .findViewById(R.id.filesize);
                holder.time = (TextView) convertView
                        .findViewById(R.id.totalTime);
                holder.imageview = (ImageView) convertView
                        .findViewById(R.id.imageview);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.name.setText(mDatas.get(position).getTitle());
            holder.time.setText(mDatas.get(position).getDuration());
            holder.size.setText(mDatas.get(position).getSize());
            if (selectPosition == position) {
                holder.imageview.setImageResource(R.drawable.accompany_select);
            } else {
                holder.imageview.setImageResource(R.drawable.accompany_unselect);
            }
            return convertView;
        }

        class ViewHolder {
            TextView name;
            TextView time;
            TextView size;
            ImageView imageview;
        }
    }

}
