package com.ub.techexcel.adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
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
import com.kloudsync.techexcel.tool.TextTool;
import com.ub.kloudsync.activity.Document;
import com.ub.kloudsync.activity.DocumentEditYinXiangPopup;
import com.ub.kloudsync.activity.DocumentYinXiangPopup;
import com.ub.techexcel.bean.SoundtrackBean;
import com.ub.techexcel.tools.CalListviewHeight;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;
import com.ub.techexcel.tools.Tools;

import org.feezu.liuli.timeselector.Utils.TextUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class HomeDocumentAdapter extends RecyclerView.Adapter<HomeDocumentAdapter.RecycleHolder> {

    private List<Document> documents;
    private Context context;
    boolean fromSearch;
    String keyword;
    List<String> expandedDocumentIds = new ArrayList<>();

    public void setFromSearch(boolean fromSearch, String keyword) {
        this.fromSearch = fromSearch;
        this.keyword = keyword;
    }

    public HomeDocumentAdapter(Context context, List<Document> documents) {
        this.context = context;
        this.documents = documents;
    }

    public void setDocuments(List<Document> documents) {

        if (documents == null) {
            documents = new ArrayList<>();
        }
        for (String id : expandedDocumentIds) {
            for (Document document : documents) {
                if (id.equals(document.getItemID())) {
                    document.setSyncExpanded(true);
                    break;
                }
            }
        }
        this.documents.clear();
        this.documents.addAll(documents);
        notifyDataSetChanged();
    }

    public interface OnItemLectureListener {

        void onItem(Document document, View view);

        void onRealItem(Document document, View view);

        void share(int s, Document document);

        void open();

        void dismiss();

        void deleteRefresh();
    }

    public void setOnItemLectureListener(OnItemLectureListener onItemLectureListener) {
        this.onItemLectureListener = onItemLectureListener;
    }

    private OnItemLectureListener onItemLectureListener;

    @Override
    public RecycleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.document_item, parent, false);
        RecycleHolder holder = new RecycleHolder(view);
        return holder;
    }

    private void setDocumentIcon(String name,ImageView documentIcon){
        Log.e("check_name","name:" + name);
        if(name.endsWith(".jpg") || name.endsWith(".JPG")){
            documentIcon.setImageResource(R.drawable.icon_jpg);
        }else if(name.endsWith(".ppt") || name.endsWith(".PPT") || name.endsWith(".pptx") || name.endsWith(".PPTX")){
            documentIcon.setImageResource(R.drawable.icon_ppt);
        }else if(name.endsWith(".pdf") || name.endsWith(".pdf")){
            documentIcon.setImageResource(R.drawable.icon_pdf);
        }else if(name.endsWith(".doc") || name.endsWith(".DOC") || name.endsWith(".docx") || name.endsWith(".DOCX")){
            documentIcon.setImageResource(R.drawable.icon_doc);
        }else{
            documentIcon.setImageResource(R.drawable.file);
        }
    }

    @Override
    public void onBindViewHolder(final RecycleHolder holder, final int position) {
        final Document item = documents.get(position);
        String title = "";
        if (fromSearch) {
            if (!TextUtil.isEmpty(keyword)) {
                holder.documetname.setText(TextTool.setSearchColor(Color.parseColor("#72AEFF"), item.getTitle(), keyword));
            }
        } else {
            holder.documetname.setText(item.getTitle());
        }

        holder.lin_favour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemLectureListener != null) {
                    onItemLectureListener.onRealItem(item, holder.tv_num);
                }
            }
        });
        holder.morepopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemLectureListener != null) {
                    onItemLectureListener.onItem(item, holder.tv_num);
                }
            }
        });
        String createData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.parseLong(item.getCreatedDate()));
        holder.createdata.setText("" + createData);
        holder.listView.setVisibility(View.GONE);
//        holder.documentOwner.setText(item.getCreatedByName());
        int syncCount = item.getSyncCount();
        holder.tv_num_value.setText(item.getSyncCount() + "");
        setDocumentIcon(item.getTitle(),holder.documentIcon);
        if (syncCount == 0) {
            holder.syncll.setVisibility(View.GONE);
        } else {
            holder.syncll.setVisibility(View.VISIBLE);
        }
        holder.syncll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.listView.getVisibility() == View.VISIBLE) {
                    holder.listView.setVisibility(View.GONE);
                    holder.imageFolder.setImageResource(R.drawable.arrow_down);
                } else {
                    holder.listView.setVisibility(View.VISIBLE);
                    holder.imageFolder.setImageResource(R.drawable.arrow_up);
                    getSoundtrack(item, holder.listView);
                }
            }
        });

    }


    public void getSoundtrack(final Document document, final ListView listView) {
        String attachmentid = document.getAttachmentID() + "";
        if (TextUtils.isEmpty(attachmentid)) {
            return;
        }
        String url = AppConfig.URL_PUBLIC + "Soundtrack/List?attachmentID=" + attachmentid;
        ServiceInterfaceTools.getinstance().getSoundList(url, ServiceInterfaceTools.GETSOUNDLIST,
                new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        List<SoundtrackBean> ll = (List<SoundtrackBean>) object;
                        myBaseAdapter = new MyBaseAdapter(ll, document);
                        listView.setAdapter(myBaseAdapter);
                        CalListviewHeight.setListViewHeightBasedOnChildren(listView);
                    }
                }, false, true);
    }


    @Override
    public int getItemCount() {
        return documents.size();
    }

    private MyBaseAdapter myBaseAdapter;

    class RecycleHolder extends RecyclerView.ViewHolder {

        TextView documetname, createdata;
        ImageView tv_num;
        TextView tv_num_value;
        LinearLayout syncll;
        RelativeLayout lin_favour;
        ListView listView;
        ImageView morepopup;
        ImageView imageFolder;
        ImageView documentIcon;
        TextView documentOwner;

        public RecycleHolder(View itemView) {
            super(itemView);
            documetname = (TextView) itemView.findViewById(R.id.documetname);
            tv_num_value = (TextView) itemView.findViewById(R.id.tv_num_value);
            createdata = (TextView) itemView.findViewById(R.id.createdata);
            tv_num = (ImageView) itemView.findViewById(R.id.tv_num);
            morepopup = (ImageView) itemView.findViewById(R.id.morepopup);
            listView = (ListView) itemView.findViewById(R.id.listview);
            lin_favour = (RelativeLayout) itemView.findViewById(R.id.lin_favour);
            syncll = (LinearLayout) itemView.findViewById(R.id.syncll);
            imageFolder = (ImageView) itemView.findViewById(R.id.image_folder);
            documentIcon = (ImageView) itemView.findViewById(R.id.icon_document);
            documentOwner = (TextView)itemView.findViewById(R.id.txt_document_owner);
        }
    }


    class MyBaseAdapter extends BaseAdapter {

        List<SoundtrackBean> list = new ArrayList<>();
        Document document;

        public MyBaseAdapter(List<SoundtrackBean> list, Document document) {
            this.list = list;
            this.document = document;
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
                            onItemLectureListener.share(soundtrackBean.getSoundtrackID(), document);
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
                            deleteYinxiang2(soundtrackBean);
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

        class ViewHolder {
            TextView title;
            LinearLayout operation;
            TextView username;
            TextView duration;
            TextView createdate;
            SimpleDraweeView image;
            ImageView morepopup;
        }
    }


    private void deleteYinxiang2(SoundtrackBean soundtrackBean) {
        final int soundtrackID = soundtrackBean.getSoundtrackID();
        String url = AppConfig.URL_PUBLIC + "Soundtrack/Delete?soundtrackID=" + soundtrackID;
        ServiceInterfaceTools.getinstance().deleteSound(url, ServiceInterfaceTools.DELETESOUNDLIST,
                new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        onItemLectureListener.deleteRefresh();
                    }
                });
    }


}


