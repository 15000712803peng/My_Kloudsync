package com.ub.techexcel.tools;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.NoteDetail;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.info.Customer;
import com.ub.techexcel.bean.LineItem;
import com.ub.techexcel.bean.Note;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class SyncRoomOtherNoteListPopup {

    public Context mContext;
    public int width;
    public Dialog mPopupWindow;
    private View view;

    private RecyclerView recycleview;
    private SyncRoomNoteListAdapter syncRoomNoteListAdapter;
    private TextView name;
    private LinearLayout back;

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

    public void initPopuptWindow() {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.syncroom_othernotelist_popup, null);
        recycleview = view.findViewById(R.id.recycleview);
        recycleview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        name = view.findViewById(R.id.name);
        back = view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        mPopupWindow = new Dialog(mContext, R.style.my_dialog);
        mPopupWindow.setContentView(view);
        mPopupWindow.getWindow().setGravity(Gravity.RIGHT);
        WindowManager.LayoutParams params = mPopupWindow.getWindow().getAttributes();
        View root = ((Activity) mContext).getWindow().getDecorView();
        params.height = root.getMeasuredHeight();
        mPopupWindow.getWindow().setAttributes(params);
        mPopupWindow.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                webCamPopupListener.back();
            }
        });
        mPopupWindow.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mPopupWindow.getWindow().setWindowAnimations(R.style.anination3);
    }


    public void StartPop(String id, String syncroomid) {
        if (mPopupWindow != null) {
            mPopupWindow.show();
            name.setText("xxx的笔记");
            String url = AppConfig.URL_PUBLIC + "DocumentNote/List?syncRoomID=" + syncroomid + "&documentItemID=0&pageNumber=0&userID=" + id;
            ServiceInterfaceTools.getinstance().getNoteListV2(url, ServiceInterfaceTools.GETSYNCROOMUSERLIST, new ServiceInterfaceListener() {
                @Override
                public void getServiceReturnData(Object object) {
                    List<NoteDetail> items = new ArrayList<NoteDetail>();
                    items.clear();
                    items.addAll((List<NoteDetail>) object);
                    syncRoomNoteListAdapter = new SyncRoomNoteListAdapter(mContext, items);
                    recycleview.setAdapter(syncRoomNoteListAdapter);
                }
            });
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
        void select(NoteDetail noteDetail);

        void back();
    }

    public void setWebCamPopupListener(WebCamPopupListener webCamPopupListener) {
        this.webCamPopupListener = webCamPopupListener;
    }

    private WebCamPopupListener webCamPopupListener;

    public class SyncRoomNoteListAdapter extends RecyclerView.Adapter<SyncRoomNoteListAdapter.RecycleHolder2> {

        private Context context;

        private List<NoteDetail> list = new ArrayList<>();


        public SyncRoomNoteListAdapter(Context context, List<NoteDetail> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public RecycleHolder2 onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.syncroom_othernotelist_popup_item, parent, false);
            RecycleHolder2 holder = new RecycleHolder2(view);
            return holder;
        }


        @Override
        public void onBindViewHolder(SyncRoomNoteListAdapter.RecycleHolder2 holder, final int position) {
            final NoteDetail noteDetail = list.get(position);
            holder.title.setText(noteDetail.getTitle());
            holder.pdfaddress.setText(noteDetail.getFileName() + "-> page" + noteDetail.getPageNumber());
            String date = noteDetail.getCreatedDate();
            if (!TextUtils.isEmpty(date)) {
                long dd = Long.parseLong(date);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd HH:mm");
                String haha = simpleDateFormat.format(dd);
                holder.date.setText(haha);
            }
            holder.ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    webCamPopupListener.select(noteDetail);
                    dismiss();
                }
            });
            String url = noteDetail.getAttachmentUrl();
            if (!TextUtils.isEmpty(url)) {
                url = url.substring(0, url.lastIndexOf("<")) + "1" + url.substring(url.lastIndexOf("."), url.length());
                Uri imageUri = null;
                if (!TextUtils.isEmpty(url)) {
                    imageUri = Uri.parse(url);
                }
                holder.img_url.setImageURI(imageUri);
            }

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class RecycleHolder2 extends RecyclerView.ViewHolder {
            TextView title;
            TextView pdfaddress;
            LinearLayout ll;
            SimpleDraweeView img_url;
            TextView date;

            public RecycleHolder2(View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.title);
                pdfaddress = itemView.findViewById(R.id.pdfaddress);
                date = itemView.findViewById(R.id.date);
                ll = itemView.findViewById(R.id.ll);
                img_url = itemView.findViewById(R.id.img_url);
            }
        }
    }


}
