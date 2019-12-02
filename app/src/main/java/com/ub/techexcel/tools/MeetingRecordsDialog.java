package com.ub.techexcel.tools;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.kloudsync.techexcel.R;
import com.ub.techexcel.bean.Record;

import java.util.ArrayList;
import java.util.List;


public class MeetingRecordsDialog {

    public Context mContext;
    public int width;
    public Dialog mPopupWindow;
    private View view;
    private RecyclerView recycleview;
    private RecordAdapter adapter;
    private ImageView back;
    public MeetingRecordsDialog(Context context) {
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

    private void getRecords(String lessionId){
        String url = "https://wss.peertime.cn/MeetingServer/recording/recording_list?lessonId=" + lessionId;
        ServiceInterfaceTools.getinstance().getRecordingList(url, ServiceInterfaceTools.GETRECORDINGLIST, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                List<Record> records = new ArrayList<>();
                records.addAll((List<Record>) object);
                if (records.size() > 0) {
                    adapter = new RecordAdapter(mContext,records);
                    recycleview.setAdapter(adapter);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.dialog_meeting_records, null);
        recycleview = view.findViewById(R.id.list_record);
        recycleview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        back = view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
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


    public void StartPop(String lessionId) {
        if (mPopupWindow != null) {
            mPopupWindow.show();
        }
        getRecords(lessionId);
    }




    public boolean isShowing() {
        return mPopupWindow.isShowing();
    }

    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }


    public interface OnPlayRecordListener {
        void play(Record record);

    }

    public void setOnPlayRecordListener(OnPlayRecordListener onPlayRecordListener) {
        this.onPlayRecordListener = onPlayRecordListener;
    }

    private OnPlayRecordListener onPlayRecordListener;

    public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecycleHolder> {

        private Context context;

        private List<Record> list = new ArrayList<>();

        public RecordAdapter(Context context, List<Record> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public RecycleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.meeting_record, parent, false);
            RecycleHolder holder = new RecycleHolder(view);
            return holder;
        }


        @Override
        public void onBindViewHolder(final RecordAdapter.RecycleHolder holder, final int position) {
            final Record recordData = list.get(position);
            holder.record.setText(recordData.getTitle());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onPlayRecordListener != null){
                        onPlayRecordListener.play(recordData);
                    }
                    dismiss();
                }
            });


        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class RecycleHolder extends RecyclerView.ViewHolder {
            TextView record;
            public RecycleHolder(View itemView) {
                super(itemView);
                record = itemView.findViewById(R.id.txt_record_name);

            }
        }
    }


}
