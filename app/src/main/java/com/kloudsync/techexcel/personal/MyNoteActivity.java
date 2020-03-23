package com.kloudsync.techexcel.personal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.ui.DrawView;
import com.kloudsync.techexcel.ui.NoteViewActivity;
import com.kloudsync.techexcel.ui.NoteViewActivityV2;
import com.ub.service.activity.SocketService;
import com.ub.techexcel.bean.Note;
import com.ub.techexcel.tools.MeetingServiceTools;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MyNoteActivity extends Activity implements View.OnClickListener {

    private TextView tv_title;
    private RelativeLayout layout_back;
    private RecyclerView rv_pc;
    private UserNotesAdapter notesAdapter;
    private List<Note> notes = new ArrayList<>();
    private static final int TYPE_LIVE_NOTE = 1;
    private static final int TYPE_NOTE = 2;

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
                List<Note> list = (List<Note>) object;
                for (Note note : list) {
                    note.setNoteType(TYPE_NOTE);
                }
                notes.addAll(list);
                rv_pc.setAdapter(notesAdapter);
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
    }


    public class UserNotesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context context;
        private DrawView canvasImage;

        private RelativeLayout noteLayout;
        private List<Note> list;

        private OnNotesOperationsListener notesOperationsListener;

        public OnNotesOperationsListener getNotesOperationsListener() {
            return notesOperationsListener;
        }

        public void setNotesOperationsListener(OnNotesOperationsListener notesOperationsListener) {
            this.notesOperationsListener = notesOperationsListener;
        }

        public UserNotesAdapter(Context context, List<Note> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case TYPE_LIVE_NOTE:
                    view = LayoutInflater.from(context).inflate(R.layout.live_note_item, parent, false);
                    canvasImage = new DrawView(MyNoteActivity.this);
                    noteLayout = view.findViewById(R.id.layout_note);
                    canvasImage.setBackgroundColor(getResources().getColor(R.color.tab_blue));
                    RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    noteLayout.addView(canvasImage, param);
                    Log.e("check_canvasImage", "canvasImage1:" + canvasImage);
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
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            int type = getItemViewType(position);
            switch (type) {
                case TYPE_LIVE_NOTE:
                    if (holder instanceof LiveNoteHolder) {
                        final LiveNoteHolder noteHolder = (LiveNoteHolder) holder;
                        Log.e("check_canvasImage", "canvasImage2:" + canvasImage);
                        noteLayout = noteHolder.noteLayout;
                        noteHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String currentPage = noteHolder.mTvCurrentPage.getText().toString();
                                Intent intent = new Intent(context, CurrentNoteActivity.class);
                                intent.putExtra(CurrentNoteActivity.CURRENTPAGE, currentPage);
                                context.startActivity(intent);
                            }
                        });
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
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.e("check_click", "item_view,clicked");
                                handleBluetoothNote(noteDetail);
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
            }
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

            public RelativeLayout noteLayout;
            private final TextView mTvCurrentPage;
            private final TextView mTvPageUpdateDate;

            public LiveNoteHolder(View itemView) {
                super(itemView);
                noteLayout = itemView.findViewById(R.id.layout_note);
                mTvCurrentPage = itemView.findViewById(R.id.item_tv_current_page);
                mTvPageUpdateDate = itemView.findViewById(R.id.item_tv_page_update_date);
            }
        }
    }

    public  interface OnNotesOperationsListener {

        void viewNote(Note note);

        void deleteNote(Note note);

        void moveNote(Note note);

        void renameNote(Note note);
    }

    private void goToViewNote(String lessonId, String itemId, Note note) {
        updateSocket();
        Intent intent = new Intent(this, NoteViewActivityV2.class);
//        Intent intent = new Intent(getActivity(), WatchCourseActivity3.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("userid", AppConfig.UserID);
        //-----
        intent.putExtra("meeting_id", Integer.parseInt(lessonId) + "," + AppConfig.UserID);
//        intent.putExtra("meeting_id", "Doc-" + AppConfig.UserID);
        intent.putExtra("document_id", itemId);
        intent.putExtra("meeting_type", 2);
        intent.putExtra("lession_id", Integer.parseInt(lessonId));
        intent.putExtra("url", note.getAttachmentUrl());
        intent.putExtra("note_id", note.getNoteID());
        intent.putExtra("local_file_id", note.getLocalFileID());
        startActivity(intent);
    }

    private void updateSocket() {
        Intent service = new Intent(this, SocketService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                context.startForegroundService(service);
            startService(service);
        } else {
            startService(service);
        }
    }

    public void handleBluetoothNote(Note note) {

        if (note.getNoteID() > 0) {
            Observable.just(note.getNoteID() + "").observeOn(Schedulers.io()).map(new Function<String, Note>() {
                @Override
                public Note apply(String noteId) throws Exception {
                    return MeetingServiceTools.getInstance().syncGetNoteByNoteId(noteId);
                }

            }).doOnNext(new Consumer<Note>() {
                @Override
                public void accept(final Note note) throws Exception {
                    if (note.getAttachmentID() > 0) {
                        String title = note.getTitle();
                        if (TextUtils.isEmpty(title)) {
                            title = "";
                        }
                        String url = AppConfig.URL_PUBLIC
                                + "Lesson/AddTempLessonWithOriginalDocument?attachmentID=" + note.getAttachmentID()
                                + "&Title=" + URLEncoder.encode(LoginGet.getBase64Password(""), "UTF-8");
                        JSONObject jsonObject = ServiceInterfaceTools.getinstance().syncGetTempLessonWithOriginalDocument(url);
                        if (jsonObject.has("RetCode")) {
                            if (jsonObject.getInt("RetCode") == 0) {
                                JSONObject data = jsonObject.getJSONObject("RetData");
                                final String lessionId = data.optLong("LessonID") + "";
                                final String itemId = data.optLong("ItemID") + "";
                                if (!TextUtils.isEmpty(lessionId)) {
                                    Observable.just("view_note").observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                                        @Override
                                        public void accept(String s) throws Exception {
                                            goToViewNote(lessionId, itemId, note);
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            }).subscribe();
        }


    }


}
