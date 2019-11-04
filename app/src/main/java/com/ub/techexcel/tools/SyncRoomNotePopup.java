package com.ub.techexcel.tools;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.NoteDetail;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.info.Customer;

import java.util.ArrayList;
import java.util.List;


public class SyncRoomNotePopup {

    public Context mContext;
    public int width;
    public Dialog mPopupWindow;
    private View view;

    private RecyclerView recycleview;
    private SyncRoomUserAdapter syncRoomUserAdapter;
    private TextView presenterusername;
    private TextView presenternoteNumber;
    private TextView myynote;
    private RelativeLayout presenterll;

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

    public void initPopuptWindow() {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.syncroom_note_popup, null);
        recycleview = view.findViewById(R.id.recycleview);
        recycleview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        presenterusername = view.findViewById(R.id.presentername);
        back = view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        presenternoteNumber = view.findViewById(R.id.presenternumber);
        myynote = view.findViewById(R.id.myynote);
        presenterll = view.findViewById(R.id.presenterll);
        presenterll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                webCamPopupListener.enter(presenter);
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

            }
        });
        mPopupWindow.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mPopupWindow.getWindow().setWindowAnimations(R.style.anination3);
    }


    private Customer presenter = new Customer();
    private List<Customer> list = new ArrayList<>();

    public void StartPop(View v, String syncroomid, final String id) {
        if (mPopupWindow != null) {
            mPopupWindow.show();
            String url = AppConfig.URL_PUBLIC + "DocumentNote/SyncRoomUserList?syncRoomID=" + syncroomid;
            ServiceInterfaceTools.getinstance().getSyncRoomUserList(url, ServiceInterfaceTools.GETSYNCROOMUSERLIST, new ServiceInterfaceListener() {
                @Override
                public void getServiceReturnData(Object object) {
                    list.clear();
                    list.addAll((List<Customer>) object);
                    for (int i = 0; i < list.size(); i++) {
                        Customer selectCustomer = list.get(i);
                        if (selectCustomer.getUserID().equals(AppConfig.UserID)) {
                            presenter = list.get(i);
                            list.remove(i);
                            i--;
                        }
                    }

                    if(TextUtils.isEmpty(presenter.getUserID())){
                        presenterll.setVisibility(View.GONE);
                        myynote.setVisibility(View.GONE);
                    }else{
                        presenterll.setVisibility(View.VISIBLE);
                        myynote.setVisibility(View.VISIBLE);
                    }

                    for (int i = 0; i < list.size(); i++) {
                        if (id.equals(list.get(i).getUserID())) {
                            list.get(i).setSelected(true);
                        } else {
                            list.get(i).setSelected(false);
                        }
                    }
                    syncRoomUserAdapter = new SyncRoomUserAdapter(mContext);
                    recycleview.setAdapter(syncRoomUserAdapter);
                    //设置我的笔记
                    presenterusername.setText(presenter.getName() + "(Me)");
                    presenternoteNumber.setText(presenter.getNoteCount() + " Note");
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
        void enter(Customer customer);

        void selectCustomer(Customer customer);
    }

    public void setWebCamPopupListener(WebCamPopupListener webCamPopupListener) {
        this.webCamPopupListener = webCamPopupListener;
    }

    private WebCamPopupListener webCamPopupListener;


    public void notify2(String selectCusterId) {
        for (int i = 0; i < list.size(); i++) {
            if (selectCusterId.equals(list.get(i).getUserID())) {
                list.get(i).setSelected(true);
            } else {
                list.get(i).setSelected(false);
            }
        }
        if (syncRoomUserAdapter != null) {
            syncRoomUserAdapter.notifyDataSetChanged();
        }
    }

    public class SyncRoomUserAdapter extends RecyclerView.Adapter<SyncRoomUserAdapter.RecycleHolder2> {

        private Context context;


        public SyncRoomUserAdapter(Context context) {
            this.context = context;
        }

        @Override
        public RecycleHolder2 onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.syncroom_note_popup_item, parent, false);
            RecycleHolder2 holder = new RecycleHolder2(view);
            return holder;
        }


        @Override
        public void onBindViewHolder(RecycleHolder2 holder, final int position) {
            final Customer customer = list.get(position);
            holder.ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (customer.isSelected()) {
                        dismiss();
                        webCamPopupListener.enter(customer);
                    }
                }
            });
            holder.username.setText(customer.getName());
            holder.noteNumber.setText(customer.getNoteCount() + " Note");
            holder.selectimg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    webCamPopupListener.selectCustomer(customer);

                }
            });

            if (customer.isSelected()) {
                holder.selectimg.setBackground(mContext.getResources().getDrawable(R.drawable.finish_a));
                holder.arrow.setVisibility(View.VISIBLE);
            } else {
                holder.selectimg.setBackground(mContext.getResources().getDrawable(R.drawable.finish_d));
                holder.arrow.setVisibility(View.INVISIBLE);
            }

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class RecycleHolder2 extends RecyclerView.ViewHolder {
            TextView username;
            TextView noteNumber;
            RelativeLayout ll;
            ImageView selectimg;
            ImageView arrow;

            public RecycleHolder2(View itemView) {
                super(itemView);
                username = itemView.findViewById(R.id.username);
                noteNumber = itemView.findViewById(R.id.usernumber);
                ll = itemView.findViewById(R.id.ll);
                selectimg = itemView.findViewById(R.id.selectimg);
                arrow = itemView.findViewById(R.id.arrow);
            }
        }
    }

}
