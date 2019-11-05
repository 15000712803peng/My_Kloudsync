package com.kloudsync.techexcel.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.start.LoginGet;
import com.ub.kloudsync.activity.Document;
import com.ub.service.activity.WatchCourseActivity3;
import com.ub.techexcel.adapter.NewMeetingContactAdapter;
import com.ub.techexcel.bean.LineItem;
import com.ub.techexcel.bean.Note;
import com.ub.techexcel.tools.InviteOthersPopup;
import com.ub.techexcel.tools.NoteOthersPopup;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class SelectNoteDialog implements View.OnClickListener {
    public Context mContext;
    public int width;
    public int heigth;
    public Dialog dialog;
    private View view;
    private ImageView closeImage;
    private TextView cancelText;
    private TextView saveText;
    private TextView selectuser;
    private TextView txt_doc_space_name;
    private List<Customer> customerList = new ArrayList<>();

    public interface OnFavoriteDocSelectedListener {
        void onFavoriteDocSelected(Note docId);
    }

    OnFavoriteDocSelectedListener onFavoriteDocSelectedListener;


    public void setOnFavoriteDocSelectedListener(OnFavoriteDocSelectedListener onFavoriteSelectedListener) {
        this.onFavoriteDocSelectedListener = onFavoriteSelectedListener;
    }


    private String syncroomid;

    public SelectNoteDialog(Context context,  String syncroomid) {
        mContext = context;
        this.syncroomid = syncroomid;
        initDialog();
    }

    public void initDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.dialog_add_file_from_note, null);
        dialog = new Dialog(mContext, R.style.my_dialog);
        documentList = view.findViewById(R.id.list_document);
        documentList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        closeImage = view.findViewById(R.id.image_close);
        txt_doc_space_name = view.findViewById(R.id.txt_doc_space_name);
        txt_doc_space_name.setText("My Notes");
        closeImage.setOnClickListener(this);
        cancelText = view.findViewById(R.id.cancel);
        selectuser = view.findViewById(R.id.selectuser);
        saveText = view.findViewById(R.id.save);
        saveText.setOnClickListener(this);
        cancelText.setOnClickListener(this);
        selectuser.setOnClickListener(this);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * (0.85f));
        heigth = (int) (mContext.getResources().getDisplayMetrics().heightPixels * (0.80f));
        dialog.setContentView(view);
        dialog.getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = width;
        lp.height = heigth;
        dialog.getWindow().setAttributes(lp);
    }


    public boolean isShowing() {
        return dialog.isShowing();
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.cancel();
            dialog = null;
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_close:
                dismiss();
                break;
            case R.id.save:
                if (onFavoriteDocSelectedListener != null) {
                    if (documentAdapter != null) {
                        Note document = documentAdapter.getSelectFile();
                        if (document != null) {
                            onFavoriteDocSelectedListener.onFavoriteDocSelected(document);
                            dismiss();
                        } else {
                            Toast.makeText(mContext, "no note selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            case R.id.cancel:
                dismiss();
                break;
            case R.id.selectuser: //  选择用户
                NoteOthersPopup noteOthersPopup = new NoteOthersPopup();
                noteOthersPopup.getPopwindow(mContext);
                noteOthersPopup.setFavoritePoPListener(new NoteOthersPopup.FavoritePoPListener() {
                    @Override
                    public void select(List<Customer> list) {
                        customerList.clear();
                        customerList.addAll(list);
                        String use = "";
                        String useid = "";
                        for (Customer customer : customerList) {
                            use += customer.getName() + " ";
                            useid = customer.getUserID();
                        }
                        selectuser.setText(use);
                        String url = AppConfig.URL_PUBLIC + "DocumentNote/UserNoteList?userID=" + useid;
                        ServiceInterfaceTools.getinstance().getUserNoteList(url, ServiceInterfaceTools.GETSYNCROOMUSERLIST, new ServiceInterfaceListener() {
                            @Override
                            public void getServiceReturnData(Object object) {
                                List<Note> list = new ArrayList<>();
                                list.addAll((List<Note>) object);
                                documentAdapter = new DocumentAdapter(mContext, list);
                                documentList.setAdapter(documentAdapter);
                            }
                        });
                    }
                });
                noteOthersPopup.StartPop(view, syncroomid);
                break;
        }
    }

    public void show(String url) {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
            getDocumentList(url);
        }
    }

    DocumentAdapter documentAdapter;
    RecyclerView documentList;

    private void getDocumentList(String url) {
        selectuser.setText(AppConfig.UserName);
        ServiceInterfaceTools.getinstance().getUserNoteList(url, ServiceInterfaceTools.GETSYNCROOMUSERLIST, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                List<Note> list = new ArrayList<>();
                list.addAll((List<Note>) object);
                documentAdapter = new DocumentAdapter(mContext, list);
                documentList.setAdapter(documentAdapter);
            }
        });
    }

    class DocHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView time;
        TextView size;
        ImageView imageview;

        public DocHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            size = (TextView) itemView
                    .findViewById(R.id.filesize);
            time = (TextView) itemView
                    .findViewById(R.id.totalTime);
            imageview = (ImageView) itemView
                    .findViewById(R.id.imageview);
        }
    }


    public class DocumentAdapter extends RecyclerView.Adapter<DocHolder> {
        private Context mContext;
        private List<Note> mDatas;
        int selectPosition = -1;


        public DocumentAdapter(Context context, List<Note> mDatas) {
            this.mContext = context;
            this.mDatas = mDatas;

        }


        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        public Note getSelectFile() {
            if (selectPosition == -1) {
                return null;
            }
            return mDatas.get(selectPosition);
        }

        @Override
        public DocHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new DocHolder(LayoutInflater.from(mContext).inflate(R.layout.popup_video_item, parent, false));
        }

        @Override
        public void onBindViewHolder(DocHolder holder, final int position) {
            holder.name.setText(mDatas.get(position).getFileName());
            holder.time.setText("123kb");
            String create = mDatas.get(position).getCreatedDate();
            String createdate = "";
            if (!TextUtils.isEmpty(create)) {
                long dd = Long.parseLong(create);
                createdate = new SimpleDateFormat("yyyy.MM.dd").format(dd);
            }
            holder.size.setText(createdate);
            if (selectPosition == position) {
                holder.imageview.setImageResource(R.drawable.finish_a);
            } else {
                holder.imageview.setImageResource(R.drawable.finish_d);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectPosition = position;
                    notifyDataSetChanged();
                }
            });

        }


    }
}
