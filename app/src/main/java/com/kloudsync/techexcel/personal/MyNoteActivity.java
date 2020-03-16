package com.kloudsync.techexcel.personal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.EverPen;
import com.kloudsync.techexcel.bean.NoteDetail;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.SelectNoteDialog;
import com.kloudsync.techexcel.help.EverPenManger;
import com.kloudsync.techexcel.help.MyTQLPenSignal;
import com.kloudsync.techexcel.help.PenDotTool;
import com.kloudsync.techexcel.ui.DrawView;
import com.tqltech.tqlpencomm.BLEException;
import com.tqltech.tqlpencomm.Dot;
import com.tqltech.tqlpencomm.PenCommAgent;
import com.ub.techexcel.adapter.SyncRoomNoteListAdapter;
import com.ub.techexcel.adapter.UserNotesAdapter;
import com.ub.techexcel.bean.Note;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;
import com.ub.techexcel.tools.SyncRoomOtherNoteListPopup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MyNoteActivity extends Activity implements View.OnClickListener,EverPenManger.PenDotsReceiver{

    private TextView tv_title;
    private RelativeLayout layout_back;
    private LinearLayout search_layout;
    private RecyclerView rv_pc;
    private UserNotesAdapter notesAdapter;

    private List<Note> notes = new ArrayList<>();

    private static final int TYPE_LIVE_NOTE = 1;
    private static final int TYPE_NOTE = 2;
    private DrawView[] bDrawl = new DrawView[2];  //add 2016-06-15 for draw



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_note);
        EventBus.getDefault().register(this);
        initView();
        Note liveNote = new Note();
        liveNote.setNoteID(-1);
        liveNote.setNoteType(TYPE_LIVE_NOTE);
        notes.add(liveNote);
        notesAdapter = new UserNotesAdapter(this, notes);
        rv_pc.setAdapter(notesAdapter);
        EverPenManger.getInstance(this).addDotsReceiver(this);
        getNoteData(AppConfig.UserID);
    }




    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventNote(Note note) {

    }

    private void initView() {
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText(R.string.mynote);
        layout_back = findViewById(R.id.layout_back);
        layout_back.setOnClickListener(this);
        rv_pc = findViewById(R.id.rv_pc);
        rv_pc.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private void getNoteData(String useid) {
        String url = AppConfig.URL_PUBLIC + "DocumentNote/UserNoteList?userID=" + useid;
        ServiceInterfaceTools.getinstance().getUserNoteList(url, ServiceInterfaceTools.GETSYNCROOMUSERLIST, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
//                notes.addAll((List<Note>) object);
//                rv_pc.setAdapter(notesAdapter);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_back:
                finish();
                break;
            case R.id.search_layout:
                break;
            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1 && data != null) {

            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        EverPenManger.getInstance(this).removeDotsReceiver(this);
    }

    @Override
    public void onDotReceive(Dot dot) {
        Log.e("onDotReceive","dot:" + dot);
        if(notesAdapter != null){
            notesAdapter.drawDot(dot);
        }
    }

    public class UserNotesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context context;

        private DrawView canvasImage;

        private RelativeLayout noteLayout;


        private List<Note> list = new ArrayList<>();

        private com.ub.techexcel.adapter.UserNotesAdapter.OnNotesOperationsListener notesOperationsListener;

        public com.ub.techexcel.adapter.UserNotesAdapter.OnNotesOperationsListener getNotesOperationsListener() {
            return notesOperationsListener;
        }

        public void setNotesOperationsListener(com.ub.techexcel.adapter.UserNotesAdapter.OnNotesOperationsListener notesOperationsListener) {
            this.notesOperationsListener = notesOperationsListener;
        }

        public UserNotesAdapter(Context context, List<Note> list) {
            this.context = context;
            this.list = list;
        }

        public void drawDot(Dot dot){
            if(canvasImage != null){
                PenDotTool.processEachDot(dot,canvasImage);
            }
//            notifyItemChanged(0);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case TYPE_LIVE_NOTE:
                    view = LayoutInflater.from(context).inflate(R.layout.live_note_item, parent, false);
                    canvasImage = view.findViewById(R.id.image_live);
                    noteLayout = view.findViewById(R.id.layout_note);

                    Log.e("onBindViewHolder","set_bdrawl");
                    //TODO new draw
                    //newDrawPenView = new NewDrawPenView(this);
                    return new LiveNoteHolder(view);
                case TYPE_NOTE:
                    view = LayoutInflater.from(context).inflate(R.layout.user_note_item, parent, false);
                    return new RecycleHolder2(view);
            }
            return null;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return TYPE_LIVE_NOTE;
            } else {
                return TYPE_NOTE;
            }

        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            int type = getItemViewType(position);
            switch (type) {
                case TYPE_LIVE_NOTE:
                    if(holder instanceof LiveNoteHolder){
                        LiveNoteHolder noteHolder = (LiveNoteHolder) holder;
                        canvasImage = noteHolder.noteImage;
                        noteLayout = noteHolder.noteLayout;
                    }
                    break;

                case TYPE_NOTE:
                    if (list.get(position) instanceof Note) {
                        final Note noteDetail = list.get(position);
                        RecycleHolder2 holder2 = (RecycleHolder2) holder;
                        holder2.title.setText(noteDetail.getTitle());
                        String date = noteDetail.getCreatedDate();
                        if (!TextUtils.isEmpty(date)) {
                            long dd = Long.parseLong(date);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd  HH:mm:ss");
                            String haha = simpleDateFormat.format(dd);
                            holder2.date.setText(haha);
                        }
                        holder2.ll.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                        String url = noteDetail.getAttachmentUrl();
                        if (!TextUtils.isEmpty(url)) {
                            url = url.substring(0, url.lastIndexOf("<")) + "1" + url.substring(url.lastIndexOf("."), url.length());
                            Uri imageUri = null;
                            if (!TextUtils.isEmpty(url)) {
                                imageUri = Uri.parse(url);
                            }
                            holder2.img_url.setImageURI(imageUri);
                        }
//        holder.operationmore.setVisibility(View.GONE);
                        holder2.operationmore.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                    }

                    break;
                default:
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }


    }

    public interface OnNotesOperationsListener {

        void viewNote(Note note);

        void deleteNote(Note note);

        void moveNote(Note note);

        void renameNote(Note note);
    }

    public class RecycleHolder2 extends RecyclerView.ViewHolder {
        TextView title;
        LinearLayout ll;
        SimpleDraweeView img_url;
        TextView date;
        ImageView operationmore;

        public RecycleHolder2(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            date = itemView.findViewById(R.id.date);
            ll = itemView.findViewById(R.id.ll);
            img_url = itemView.findViewById(R.id.img_url);
            operationmore = itemView.findViewById(R.id.operationmore);
        }
    }

    public class LiveNoteHolder extends RecyclerView.ViewHolder {

        public DrawView noteImage;
        public RelativeLayout noteLayout;

        public LiveNoteHolder(View itemView) {
            super(itemView);
            noteImage = itemView.findViewById(R.id.image_live);
            noteLayout = itemView.findViewById(R.id.layout_note);
        }
    }

}
