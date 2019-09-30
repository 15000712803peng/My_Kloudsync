package com.ub.techexcel.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.ub.techexcel.bean.LineItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class SyncRoomDocumentPopup implements View.OnClickListener {

    public Context mContext;
    public int width;
    public Dialog mPopupWindow;
    private View view;
    private ImageView adddocument;
    private RecyclerView recycleview;
    private SyncRoomTeamAdapter syncRoomTeamAdapter;

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

    LinearLayout upload_linearlayout;
    LinearLayout morell;

    public void initPopuptWindow() {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.syncroom_document_popup, null);

        recycleview = (RecyclerView) view.findViewById(R.id.recycleview);
        adddocument = (ImageView) view.findViewById(R.id.adddocument);
        adddocument.setOnClickListener(this);
        recycleview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        upload_linearlayout = (LinearLayout) view.findViewById(R.id.upload_linearlayout);
        morell = (LinearLayout) view.findViewById(R.id.morell);

        RelativeLayout fromTeamDocument = (RelativeLayout) view.findViewById(R.id.fromteamdocument);
        RelativeLayout take_photo = (RelativeLayout) view.findViewById(R.id.take_photo);
        RelativeLayout file_library = (RelativeLayout) view.findViewById(R.id.file_library);
        RelativeLayout save_file = (RelativeLayout) view.findViewById(R.id.save_file);

        fromTeamDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload_linearlayout.setVisibility(View.GONE);
                webCamPopupListener.teamDocument();
            }
        });
        take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload_linearlayout.setVisibility(View.GONE);
                webCamPopupListener.takePhoto();
            }
        });
        file_library.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload_linearlayout.setVisibility(View.GONE);
                webCamPopupListener.importFromLibrary();
            }
        });
        save_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload_linearlayout.setVisibility(View.GONE);
                webCamPopupListener.savedFile();
            }
        });


        RelativeLayout moreshare = (RelativeLayout) view.findViewById(R.id.moreshare);
        RelativeLayout moreedit = (RelativeLayout) view.findViewById(R.id.moreedit);
        RelativeLayout moredelete = (RelativeLayout) view.findViewById(R.id.moredelete);
        moreshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                morell.setVisibility(View.GONE);

            }
        });
        moreedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                morell.setVisibility(View.GONE);
                webCamPopupListener.edit(selectLineItem);

            }
        });
        moredelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                morell.setVisibility(View.GONE);
                webCamPopupListener.delete(selectLineItem);

            }
        });
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


    }


    @SuppressLint("NewApi")
    public void StartPop(View v, List<LineItem> list) {
        if (mPopupWindow != null) {
            webCamPopupListener.open();
            mPopupWindow.show();
            syncRoomTeamAdapter = new SyncRoomTeamAdapter(mContext, list);
            recycleview.setAdapter(syncRoomTeamAdapter);

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
                if (upload_linearlayout.getVisibility() == View.VISIBLE) {
                    upload_linearlayout.setVisibility(View.GONE);
                } else {
                    upload_linearlayout.setVisibility(View.VISIBLE);
                }
                morell.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }


    private LineItem selectLineItem = new LineItem();

    public class SyncRoomTeamAdapter extends RecyclerView.Adapter<SyncRoomTeamAdapter.RecycleHolder2> {

        private Context context;

        private List<LineItem> list = new ArrayList<>();

        public SyncRoomTeamAdapter(Context context, List<LineItem> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public RecycleHolder2 onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.syncroom_document_popup_item, parent, false);
            RecycleHolder2 holder = new RecycleHolder2(view);
            return holder;
        }


        @Override
        public void onBindViewHolder(RecycleHolder2 holder, int position) {
            final LineItem lineItem = list.get(position);
            holder.title.setText(lineItem.getFileName());
            holder.synccount.setText("Sync:" + lineItem.getSyncRoomCount());
            String date = lineItem.getCreatedDate();
            if (!TextUtils.isEmpty(date)) {
                long dd = Long.parseLong(date);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd HH:mm:ss");
                String haha = simpleDateFormat.format(dd);
                holder.date.setText("Create Date: " + haha);
            }
            holder.img_url.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    webCamPopupListener.changeOptions(lineItem);
                }
            });
            holder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectLineItem = lineItem;
                    if (morell.getVisibility() == View.VISIBLE) {
                        morell.setVisibility(View.GONE);
                    } else {
                        morell.setVisibility(View.VISIBLE);
                    }
                    upload_linearlayout.setVisibility(View.GONE);
                }
            });
            holder.ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    morell.setVisibility(View.GONE);
                    upload_linearlayout.setVisibility(View.GONE);
                }
            });
            String url = lineItem.getUrl();
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
            TextView synccount;
            ImageView more;
            LinearLayout ll;
            SimpleDraweeView img_url;
            TextView date;

            public RecycleHolder2(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.title);
                synccount = (TextView) itemView.findViewById(R.id.synccount);
                date = (TextView) itemView.findViewById(R.id.date);
                more = (ImageView) itemView.findViewById(R.id.more);
                ll = (LinearLayout) itemView.findViewById(R.id.ll);
                img_url = (SimpleDraweeView) itemView.findViewById(R.id.img_url);
            }
        }
    }


}
