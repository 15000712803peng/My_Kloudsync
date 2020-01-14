package com.kloudsync.techexcel.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.ub.kloudsync.activity.Document;
import com.ub.kloudsync.activity.DocumentEditYinXiangPopup;
import com.ub.kloudsync.activity.DocumentYinXiangPopup;
import com.ub.techexcel.bean.SoundtrackBean;
import com.ub.techexcel.tools.CalListviewHeight;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;
import com.ub.techexcel.tools.Tools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FavoriteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnLongClickListener, View.OnClickListener {


    private List<Document> mlist = new ArrayList<>();
    private Context context;

    private DeleteItemClickListener deleteItemClickListener = null;

    public interface OnMoreOptionsClickListener {
        void onMoreOptionsClick(Document document);
    }

    private OnMoreOptionsClickListener onMoreOptionsClickListener;

    public interface OnItemClickListener {
        void onItemClick(Document document);
    }

    private OnItemClickListener onItemClickListener;


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnMoreOptionsClickListener(OnMoreOptionsClickListener onMoreOptionsClickListener) {
        this.onMoreOptionsClickListener = onMoreOptionsClickListener;
    }

    public interface DeleteItemClickListener {
        void AddTempLesson(int position);

        void deleteClick(View view, int position);

        void shareLesson(Document document, int id);
    }

    public void setDeleteItemClickListener(DeleteItemClickListener deleteItemClickListener) {
        this.deleteItemClickListener = deleteItemClickListener;
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public void onClick(final View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v, (Integer) v.getTag());
        }
    }

    private OnRecyclerViewItemLongClickListener mOnItemLongClickListener = null;

    public static interface OnRecyclerViewItemLongClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemLongClickListener(OnRecyclerViewItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }

    @Override
    public boolean onLongClick(View v) {
        if (mOnItemLongClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemLongClickListener.onItemClick(v, (Integer) v.getTag());
        }
        return false;
    }

    public FavoriteAdapter() {

    }

    public FavoriteAdapter(Context context) {
        this.context = context;
    }

    public void UpdateRV(List<Document> mlist) {
        this.mlist.clear();
        this.mlist.addAll(mlist);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favour, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        final Document favorite = mlist.get(position);
        holder.tv_favour.setText(favorite.getTitle());
        holder.tv_synccount.setText(favorite.getSyncCount() + "");
        String createData = new SimpleDateFormat(" hh:mmaa dd/MM/yyyy", Locale.ENGLISH).format(Long.parseLong(favorite.getCreatedDate()));
        holder.tv_time.setText("upload " + createData);
        holder.lin_sync.setVisibility((0 == favorite.getSyncCount()) ? View.GONE : View.VISIBLE);
        holder.img_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onMoreOptionsClickListener != null) {
                    onMoreOptionsClickListener.onMoreOptionsClick(favorite);
                }
            }
        });

        holder.lin_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.listView.setTag(position);
                if (holder.listView.getVisibility() == View.VISIBLE) {
                    holder.listView.setVisibility(View.GONE);
                    holder.imageFolder.setImageResource(R.drawable.arrow_down);
                } else {
                    holder.listView.setVisibility(View.VISIBLE);
                    holder.imageFolder.setImageResource(R.drawable.arrow_up);
                    getSoundtrack(favorite, holder.listView, position);
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(favorite);
                }
            }
        });
    }


    MyBaseAdapter myBaseAdapter;

    private void getSoundtrack(final Document favorite, final ListView listView, final int position) {
        String attachmentid = favorite.getAttachmentID() + "";
        if (TextUtils.isEmpty(attachmentid)) {
            return;
        }
        String url = AppConfig.URL_PUBLIC + "Soundtrack/List?attachmentID=" + attachmentid;
        ServiceInterfaceTools.getinstance().getSoundList(url, ServiceInterfaceTools.GETSOUNDLIST,
                new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        if (listView.getTag() != null) {
                            int p = (Integer) listView.getTag();
                            if (p == position) {
                                List<SoundtrackBean> ll = (List<SoundtrackBean>) object;
                                favorite.setSoundSync(ll);
                                myBaseAdapter = new MyBaseAdapter(ll, favorite);
                                listView.setAdapter(myBaseAdapter);
                                CalListviewHeight.setListViewHeightBasedOnChildren(listView);
                            }
                        }

                    }
                }, false, true);
    }


    private void deleteYinxiang2(SoundtrackBean soundtrackBean, final Document favorite) {
        final int soundtrackID = soundtrackBean.getSoundtrackID();
        String url = AppConfig.URL_PUBLIC + "Soundtrack/Delete?soundtrackID=" + soundtrackID;
        ServiceInterfaceTools.getinstance().deleteSound(url, ServiceInterfaceTools.DELETESOUNDLIST,
                new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
//                        getSoundtrack(favorite,listView);
                    }
                });
    }

    public void SetMyProgress(long total, long current, Document fa) {
        int pb = (int) (current * 100 / total);
        fa.setProgress(pb);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_favour;
        TextView tv_synccount;
        TextView tv_time;
        RelativeLayout lin_favour;
        LinearLayout lin_sync;
        LinearLayout lin_expand;
        ImageView img_more;
        ListView listView;
        ImageView imageFolder;

        ViewHolder(View view) {
            super(view);
            tv_favour = (TextView) view.findViewById(R.id.tv_favour);
            tv_synccount = (TextView) view.findViewById(R.id.tv_synccount);
            tv_time = (TextView) view.findViewById(R.id.tv_time);
            lin_favour = (RelativeLayout) view.findViewById(R.id.lin_favour);
            lin_sync = (LinearLayout) view.findViewById(R.id.lin_sync);
            lin_expand = (LinearLayout) view.findViewById(R.id.lin_expand);
            img_more = (ImageView) view.findViewById(R.id.img_more);
            listView = (ListView) view.findViewById(R.id.listview);
            imageFolder = view.findViewById(R.id.image_folder);
        }
    }

    class MyBaseAdapter extends BaseAdapter {

        class ViewHolder {
            TextView title;
            LinearLayout operation;
            TextView username;
            TextView duration;
            TextView createdate;
            SimpleDraweeView image;
            ImageView morepopup;
        }

        List<SoundtrackBean> list = new ArrayList<>();
        Document favorite;

        public MyBaseAdapter(List<SoundtrackBean> list, Document favorite) {
            this.list = list;
            this.favorite = favorite;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            final ViewHolder viewHolder;
            if (view == null) {
                viewHolder = new ViewHolder();
                view = LayoutInflater.from(context).inflate(R.layout.yinxiang_item3, null);
                viewHolder.title = (TextView) view.findViewById(R.id.title);
                viewHolder.username = (TextView) view.findViewById(R.id.username);
                viewHolder.duration = (TextView) view.findViewById(R.id.duration);
                viewHolder.createdate = (TextView) view.findViewById(R.id.createdate);
                viewHolder.operation = (LinearLayout) view.findViewById(R.id.operation);
                viewHolder.image = (SimpleDraweeView) view.findViewById(R.id.image);
                viewHolder.morepopup = (ImageView) view.findViewById(R.id.morepopup);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.morepopup.setVisibility(View.VISIBLE);
            final SoundtrackBean soundtrackBean = list.get(position);
            viewHolder.title.setText(soundtrackBean.getTitle());
            viewHolder.username.setText(soundtrackBean.getUserName());

            String createData = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(Long.parseLong(soundtrackBean.getCreatedDate()));
            viewHolder.createdate.setText("" + createData);

            viewHolder.duration.setText("Duration: " + soundtrackBean.getDuration());
            viewHolder.operation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            viewHolder.morepopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DocumentYinXiangPopup documentYinXiangPopup = new DocumentYinXiangPopup();
                    documentYinXiangPopup.getPopwindow(context);
                    documentYinXiangPopup.setFavoritePoPListener(new DocumentYinXiangPopup.FavoritePoPListener() {
                        @Override
                        public void share() {
                            onItemLectureListener.share(soundtrackBean.getSoundtrackID(), favorite);
                        }

                        @Override
                        public void edit() {
                            DocumentEditYinXiangPopup documentEditYinXiangPopup = new DocumentEditYinXiangPopup();
                            documentEditYinXiangPopup.getPopwindow(context);
                            documentEditYinXiangPopup.setFavoritePoPListener(new DocumentEditYinXiangPopup.FavoritePoPListener() {

                                @Override
                                public void dismiss() {
                                    onItemLectureListener.dismiss();
                                }

                                @Override
                                public void editSuccess() {
                                    onItemLectureListener.deleteRefresh();
                                }

                                @Override
                                public void open() {
                                    onItemLectureListener.open();
                                }
                            });
                            documentEditYinXiangPopup.StartPop(viewHolder.operation, soundtrackBean);
                        }

                        @Override
                        public void delete() {
                            deleteYinxiang2(soundtrackBean, favorite);
                        }

                        @Override
                        public void open() {

                            onItemLectureListener.open();
                        }

                        @Override
                        public void dismiss() {
                            onItemLectureListener.dismiss();
                        }
                    });
                    documentYinXiangPopup.StartPop(soundtrackBean);

                }
            });
            String url2 = soundtrackBean.getAvatarUrl();
            Uri imageUri2;
            if (!TextUtils.isEmpty(url2)) {
                imageUri2 = Uri.parse(url2);
            } else {
                imageUri2 = Tools.getUriFromDrawableRes(context, R.drawable.hello);
            }
            viewHolder.image.setImageURI(imageUri2);
            return view;
        }
    }

    OnItemLectureListener onItemLectureListener;

    public void setOnItemLectureListener(OnItemLectureListener onItemLectureListener) {
        this.onItemLectureListener = onItemLectureListener;
    }

    public interface OnItemLectureListener {

        void onItem(Document favorite, View view);

        void onRealItem(Document favorite, View view);

        void share(int s, Document favorite);

        void open();

        void dismiss();

        void deleteRefresh();
    }
}
