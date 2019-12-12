package com.ub.techexcel.tools;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
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
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.NoteDetail;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.info.Customer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;


public class SyncRoomOtherNoteListPopup {

    public Context mContext;
    public int width;
    public Dialog mPopupWindow;
    private View view;

    private RecyclerView recycleview;
    private SyncRoomNoteListAdapter syncRoomNoteListAdapter;
    private TextView name;
    private TextView swichuser;
    private ImageView back;

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

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public void initPopuptWindow() {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.syncroom_othernotelist_popup, null);
        recycleview = view.findViewById(R.id.recycleview);
        recycleview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        name = view.findViewById(R.id.name);
        back = view.findViewById(R.id.back);
        swichuser = view.findViewById(R.id.swichuser);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        swichuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotosyncRoomNote(selectId);
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
                dismiss();
                webCamPopupListener.close();
            }
        });
        mPopupWindow.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mPopupWindow.getWindow().setWindowAnimations(R.style.anination3);
    }


    /**
     * 进入选择用户列表
     *
     * @param
     */
    private SyncRoomNotePopup syncRoomNotePopup;

    private void gotosyncRoomNote(String userid) {
        syncRoomNotePopup = new SyncRoomNotePopup();
        syncRoomNotePopup.getPopwindow(mContext);
        syncRoomNotePopup.setWebCamPopupListener(new SyncRoomNotePopup.WebCamPopupListener() {
            @Override
            public void selectCustomer(Customer customer) {
                String userid = customer.getUserID();
                webCamPopupListener.notifychangeUserid(userid);
//                reLoadUser(userid);
            }
        });
        syncRoomNotePopup.StartPop(syncroomid, userid);
    }


    public void reLoadUser(String userid) {
        selectId = userid;
        setName(userid, syncroomid);
        getNoteData(userid, syncroomid);
    }


    private String selectId;
    private String syncroomid;

    public void StartPop(String userid, String syncroomid) {
        if (mPopupWindow != null) {
            selectId = userid;
            this.syncroomid = syncroomid;
            mPopupWindow.show();
            name.setText("XXX的笔记");
            setName(userid, syncroomid);
            getNoteData(userid, syncroomid);
        }
    }


    private void setName(final String id, final String syncroomid) {
        String url2 = AppConfig.URL_PUBLIC + "DocumentNote/SyncRoomUserList?syncRoomID=" + syncroomid;
        ServiceInterfaceTools.getinstance().getSyncRoomUserList(url2, ServiceInterfaceTools.GETSYNCROOMUSERLIST, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                List<Customer> list = new ArrayList<>();
                list.clear();
                list.addAll((List<Customer>) object);
                for (int i = 0; i < list.size(); i++) {
                    Customer ss = list.get(i);
                    if (ss.getUserID().equals(id)) {
                        name.setText(ss.getName() + "的笔记");
                        break;
                    }
                }
            }
        });
    }


    private List<NoteDetail> useNoteList = new ArrayList<>();

    private void getNoteData(String id, String syncroomid) {
        String url = AppConfig.URL_PUBLIC + "DocumentNote/List?syncRoomID=" + syncroomid + "&documentItemID=0&pageNumber=0&userID=" + id;
        ServiceInterfaceTools.getinstance().getNoteListV2(url, ServiceInterfaceTools.GETNOTELISTV2, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                List<NoteDetail> items = new ArrayList<NoteDetail>();
                items.clear();
                items.addAll((List<NoteDetail>) object);
                useNoteList.clear();
                useNoteList.addAll((List<NoteDetail>) object);
                syncRoomNoteListAdapter = new SyncRoomNoteListAdapter(mContext, items);
                recycleview.setAdapter(syncRoomNoteListAdapter);
            }
        });
    }

    public List<NoteDetail> getUseNoteList() {
        return useNoteList;
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

        void viewNote(NoteDetail noteDetail);

        void notifychangeUserid(String userId);

        void close();
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
        public void onBindViewHolder(final RecycleHolder2 holder, final int position) {
            final NoteDetail noteDetail = list.get(position);
            holder.title.setText(noteDetail.getTitle());
            String date = noteDetail.getCreatedDate();
            if (!TextUtils.isEmpty(date)) {
                long dd = Long.parseLong(date);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd  HH:mm:ss");
                String haha = simpleDateFormat.format(dd);
                holder.date.setText(haha);
            }
            holder.ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    webCamPopupListener.select(noteDetail);
                }
            });
            holder.pagenumber.setText("Page " + noteDetail.getPageNumber());
            String url = noteDetail.getAttachmentUrl();
            if (!TextUtils.isEmpty(url)) {
                url = url.substring(0, url.lastIndexOf("<")) + "1" + url.substring(url.lastIndexOf("."), url.length());
                Uri imageUri = null;
                if (!TextUtils.isEmpty(url)) {
                    imageUri = Uri.parse(url);
                }
                holder.img_url.setImageURI(imageUri);
            }

            holder.operationmore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NoteOperatorPopup noteOperatorPopup = new NoteOperatorPopup();
                    noteOperatorPopup.getPopwindow(mContext);
                    noteOperatorPopup.setFavoritePoPListener(new NoteOperatorPopup.FavoritePoPListener() {
                        @Override
                        public void delete() {
                            getNoteData(selectId, syncroomid);
                        }
                    });

                    noteOperatorPopup.StartPop(holder.operationmore, noteDetail);
                }
            });
            holder.viewnote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    webCamPopupListener.viewNote(noteDetail);
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class RecycleHolder2 extends RecyclerView.ViewHolder {
            TextView title;
            LinearLayout ll;
            SimpleDraweeView img_url;
            TextView date;
            TextView pagenumber;
            ImageView operationmore;
            ImageView viewnote;

            public RecycleHolder2(View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.title);
                date = itemView.findViewById(R.id.date);
                ll = itemView.findViewById(R.id.ll);
                img_url = itemView.findViewById(R.id.img_url);
                pagenumber = itemView.findViewById(R.id.pagenumber);
                operationmore = itemView.findViewById(R.id.operationmore);
                viewnote = itemView.findViewById(R.id.viewnote);
            }
        }
    }


}
