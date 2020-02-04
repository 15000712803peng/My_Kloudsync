package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
import com.kloudsync.techexcel.start.LoginGet;
import com.ub.kloudsync.activity.Document;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class SelectAudioForCreatingSyncDialog {

    public Context mContext;
    public int width;
    public Dialog dialog;
    private List<Document> list = new ArrayList<Document>();
    private AudiosAdapter adapter;
    private ListView listView;
    private TextView savevideo;
    private View view;
    private ImageView cancel;
    private TextView uploadfile;
    private TextView cancelText;

    public SelectAudioForCreatingSyncDialog(Context context) {
        this.mContext = context;
        width = mContext.getResources().getDisplayMetrics().widthPixels;
        getPopupWindowInstance();
    }


    public void getPopupWindowInstance() {
        if (null != dialog) {
            dialog.dismiss();
            return;
        } else {
            initPopuptWindow();
        }
    }


    int selectPosition = -1;

    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.dialog_select_audio_for_sync, null);
        listView = (ListView) view.findViewById(R.id.listview);
        cancel = (ImageView) view.findViewById(R.id.image_close);
        savevideo = (TextView) view.findViewById(R.id.savevideo);
        savevideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        uploadfile = (TextView) view.findViewById(R.id.uploadfile);
        cancelText = view.findViewById(R.id.cancel);
        cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
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

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        view.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });
        uploadfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        dialog = new Dialog(mContext, R.style.my_dialog);
        dialog.setContentView(view);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = mContext.getResources().getDisplayMetrics().widthPixels * 3 / 5 + 80;
        View root = ((Activity) mContext).getWindow().getDecorView();
        params.height = mContext.getResources().getDisplayMetrics().heightPixels * 4 / 5 - 30;
        dialog.getWindow().setAttributes(params);

    }

    @SuppressLint("NewApi")
    public void show() {
        if (dialog != null) {
            dialog.show();
            fetchAudiosForSelected();
        }
    }

    private void fetchAudiosForSelected(){
        Observable.just("request").observeOn(Schedulers.io()).map(new Function<String, Object>() {
            @Override
            public Object apply(String s) throws Exception {
                JSONObject jsonObject = ServiceInterfaceTools.getinstance().syncGetFavoriteAttachments(3);
                return jsonObject;
            }
        }).subscribe();
    }


    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
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
                holder.imageview.setImageResource(R.drawable.finish_a);
            } else {
                holder.imageview.setImageResource(R.drawable.finish_d);
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


    public void cancel(){
        if(dialog != null && dialog.isShowing()){
            dialog.cancel();
        }
    }

}
