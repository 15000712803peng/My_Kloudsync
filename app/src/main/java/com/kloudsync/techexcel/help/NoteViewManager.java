package com.kloudsync.techexcel.help;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.EventHighlightNote;
import com.kloudsync.techexcel.bean.EventNote;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.MeetingType;
import com.kloudsync.techexcel.bean.NoteDetail;
import com.kloudsync.techexcel.bean.UserNotes;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.tool.NoteImageCache;
import com.kloudsync.techexcel.view.spinner.NiceSpinner;
import com.kloudsync.techexcel.view.spinner.OnSpinnerItemSelectedListener;
import com.kloudsync.techexcel.view.spinner.UserNoteTextFormatter;
import com.ub.techexcel.bean.Note;
import com.ub.techexcel.tools.DownloadUtil;
import com.ub.techexcel.tools.FileUtils;
import com.ub.techexcel.tools.ServiceInterfaceTools;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.kloudsync.techexcel.help.PageActionsAndNotesMgr.parseNote;

/**
 * Created by tonyan on 2019/12/5.
 */

public class NoteViewManager implements OnSpinnerItemSelectedListener {

    private static NoteViewManager instance;
    private Context context;
    //------
    private ImageView noteImage;
    private ImageView backImage;
    private NiceSpinner usersSpinner;
    private RecyclerView noteList;
    private NoteAdapter noteAdapter;

    private Note note;
    private UserNotes user;
    private List<UserNotes> users;
    private MeetingConfig meetingConfig;


    public void setMeetingConfig(MeetingConfig meetingConfig) {
        this.meetingConfig = meetingConfig;
    }

    @Override
    public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
        if (users == null || users.size() <= 0) {
            return;
        }
        if(this.user == null){
            return;
        }
        UserNotes user = users.get(position);
        Log.e("onItemSelected","position:" + position + ",user:" + user);
        if (user.getUserId().equals(this.user.getUserId())) {
            Log.e("onItemSelected", "the same");
            return;
        }
        changeUser(user);
    }


    public synchronized void setContent(Context context, final View view, Note note, MeetingConfig meetingConfig) {
        this.meetingConfig = meetingConfig;
        this.context = context;
        noteImage = view.findViewById(R.id.image_note);
        noteList = view.findViewById(R.id.list_note);
        backImage = view.findViewById(R.id.image_back);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setVisibility(View.GONE);
                instance = null;
            }
        });
        noteList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        usersSpinner = view.findViewById(R.id.spinner_users);
        usersSpinner.setOnSpinnerItemSelectedListener(this);
        this.note = note;
        view.setVisibility(View.VISIBLE);
        noteImage.setImageURI(Uri.parse(note.getLocalFilePath()));
        process(AppConfig.UserID, meetingConfig);
    }

    private void showNote(EventNote note) {

        this.note = note.getNote();
        int index = 0;
        for(int i = 0 ; i < user.getNotes().size(); ++i){
           if(this.note.getLinkID() == user.getNotes().get(i).getLinkID()){
               index = i;
               break;
           }
        }
        final int scrollIndex =index;
        Observable.just(this.note).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<Note>() {
            @Override
            public void accept(Note note) throws Exception {
                noteImage.setImageURI(Uri.parse(note.getLocalFilePath()));
                if(noteAdapter != null){
                    noteAdapter.notifyDataSetChanged();
                    Log.e("NoteViewManager","scroll_to_position:" + scrollIndex);
                    noteList.scrollToPosition(scrollIndex);
                }
            }
        }).subscribe();

    }

    public static NoteViewManager getInstance() {
        if (instance == null) {
            synchronized (NoteViewManager.class) {
                if (instance == null) {
                    instance = new NoteViewManager();
                }
            }
        }
        return instance;
    }

    private void changeUser(UserNotes user) {

        if (user.getNotes() != null && user.getNotes().size() >= 0) {
            showUserNotes(user);
        } else {
            Observable.just(user).observeOn(Schedulers.io()).doOnNext(new Consumer<UserNotes>() {
                @Override
                public void accept(UserNotes userNotes) throws Exception {

                    if (users == null || users.size() <= 0 || !users.contains(userNotes)) {
                        return;
                    }

                    String url = AppConfig.URL_PUBLIC + "DocumentNote/List?syncRoomID=" + 0 + "&documentItemID=" +
                            userNotes.getParamsId() + "&pageNumber=0&userID=" + userNotes.getUserId();
                    userNotes.setNotes(ServiceInterfaceTools.getinstance().syncGetUserNotes(url));
                    refreshUserList(userNotes);

                }
            }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<UserNotes>() {
                @Override
                public void accept(UserNotes userNotes) throws Exception {

                    if (userNotes.getNotes() == null) {
                        return;
                    }
                    showUserNotes(userNotes);
                }
            }).subscribe();
        }
    }


    private void process(final String userId, final MeetingConfig meetingConfig) {

        user = new UserNotes();
        user.setUserId(userId);
        Observable.just(user).observeOn(Schedulers.io()).doOnNext(new Consumer<UserNotes>() {
            @Override
            public void accept(UserNotes userNotes) throws Exception {
                String url = "";
                String paramsId = "";
                if (meetingConfig.getType() == MeetingType.DOC) {
                    url = AppConfig.URL_PUBLIC + "DocumentNote/DocViewUserList?attachmentID=" + meetingConfig.getDocument().getAttachmentID();
                    paramsId = meetingConfig.getDocument().getAttachmentID() + "";
                }
                userNotes.setParamsId(paramsId);
                List<Customer> members = ServiceInterfaceTools.getinstance().syncGetDocUsers(url);
                Log.e("process", "one:" + members.size());
                if (members != null && members.size() > 0) {
                    users = new ArrayList<>();
                    for (int i = 0; i < members.size(); ++i) {
                        Customer member = members.get(i);
                        if (user.getUserId().equals(member.getUserID())) {
                            user.setNoteCount(member.getNoteCount());
                            user.setUserName(member.getName());
                            refreshUserList(user);
                        } else {
                            UserNotes _user = new UserNotes();
                            _user.setParamsId(paramsId);
                            _user.setNoteCount(member.getNoteCount());
                            _user.setUserName(member.getName());
                            _user.setUserId(member.getUserID());
                            refreshUserList(_user);
                        }

                    }
                }

            }
        }).doOnNext(new Consumer<UserNotes>() {
            @Override
            public void accept(UserNotes userNotes) throws Exception {
                Log.e("process", "two");
                if (users == null || users.size() <= 0 || !users.contains(userNotes)) {
                    return;
                }

                String url = AppConfig.URL_PUBLIC + "DocumentNote/List?syncRoomID=" + 0 + "&documentItemID=" +
                        userNotes.getParamsId() + "&pageNumber=0&userID=" + userNotes.getUserId();
                userNotes.setNotes(ServiceInterfaceTools.getinstance().syncGetUserNotes(url));
                refreshUserList(userNotes);

            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<UserNotes>() {
            @Override
            public void accept(UserNotes userNotes) throws Exception {
                Log.e("process", "three:" + userNotes);
                if (userNotes.getNotes() == null) {
                    return;
                }
                showUserNotes(userNotes);

            }
        }).subscribe();
    }

    private void refreshUserList(UserNotes user) {
        if (users != null) {
            if (!users.contains(user)) {
                users.add(user);
            } else {
                UserNotes _user = users.get(users.indexOf(user));
                if (user.getNotes() != null) {
                    _user.setUserId(user.getUserId());
                    _user.setNotes(user.getNotes());
                }
            }
        }
    }

    private void showUserNotes(UserNotes user) {
        this.user = user;
        usersSpinner.setVisibility(View.VISIBLE);
        noteAdapter = new NoteAdapter(context, user.getNotes());
        noteList.setAdapter(noteAdapter);
        usersSpinner.attachDataSource(users, new UserNoteTextFormatter());
        usersSpinner.setTextInternal(user);
        int index = 0;
        for(int i = 0 ; i < user.getNotes().size(); ++i){
            if(this.note.getLinkID() == user.getNotes().get(i).getLinkID()){
                index = i;
                break;
            }
        }
        noteList.scrollToPosition(index);

    }

    public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.Holder> {

        private Context context;

        private List<NoteDetail> list = new ArrayList<>();

        public NoteAdapter(Context context, List<NoteDetail> list) {
            this.context = context;
            this.list.clear();
            this.list.addAll(list);
        }

        @Override
        public NoteAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.note_item_v2, parent, false);
            NoteAdapter.Holder holder = new NoteAdapter.Holder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final NoteAdapter.Holder holder, final int position) {
            final NoteDetail noteDetail = list.get(position);
            holder.title.setText(noteDetail.getTitle());
            String date = noteDetail.getCreatedDate();
            if (!TextUtils.isEmpty(date)) {
                long dd = Long.parseLong(date);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd  HH:mm:ss");
                String haha = simpleDateFormat.format(dd);
                holder.date.setText(haha);
            }


            String url = noteDetail.getAttachmentUrl();
            if (!TextUtils.isEmpty(url)) {
                url = url.substring(0, url.lastIndexOf("<")) + "1" + url.substring(url.lastIndexOf("."), url.length());
                Uri imageUri = null;
                if (!TextUtils.isEmpty(url)) {
                    imageUri = Uri.parse(url);
                }
                holder.img_url.setImageURI(imageUri);
            }

            if(note.getLinkID() == noteDetail.getLinkID()){
                holder.container.setBackgroundResource(R.drawable.note_selected_bg);
            }else {
                holder.container.setBackgroundResource(R.drawable.corners_white);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (note.getLinkID() == noteDetail.getLinkID()) {
                        return;
                    }

                    changeNote(noteDetail.getLinkID());

                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class Holder extends RecyclerView.ViewHolder {
            TextView title;
            LinearLayout ll;
            SimpleDraweeView img_url;
            TextView date;
            LinearLayout container;


            public Holder(View itemView) {
                super(itemView);
                container = itemView.findViewById(R.id.container);
                title = itemView.findViewById(R.id.title);
                date = itemView.findViewById(R.id.date);
                ll = itemView.findViewById(R.id.ll);
                img_url = itemView.findViewById(R.id.img_url);
            }
        }
    }


    private void changeNote(int linkId) {

        if (user == null || user.getNotes() == null || user.getNotes().size() <= 0) {
            return;
        }

        final EventNote _note = new EventNote();
        NoteDetail noteDetail = null;
        for (NoteDetail note : user.getNotes()) {
            if (note.getLinkID() == linkId) {
                noteDetail = note;
                _note.setLinkId(noteDetail.getLinkID());
                break;
            }
        }
        if(noteDetail == null){
            return;
        }

        final NoteImageCache noteCache = NoteImageCache.getInstance(context);
        Observable.just(noteDetail).observeOn(Schedulers.io()).map(new Function<NoteDetail, EventNote>() {
            @Override
            public EventNote apply(NoteDetail noteDetail) throws Exception {
                _note.setNote(parseNote(noteDetail));
                return _note;
            }

        }).doOnNext(new Consumer<EventNote>() {
            @Override
            public void accept(EventNote eventNote) throws Exception {
                Note note = eventNote.getNote();
                Log.e("check_note", "one_event_note:" + eventNote);
                if (note != null && !TextUtils.isEmpty(note.getUrl())) {
//                    Log.e("getNoteDetail","note url:" + note.getAttachmentUrl());
                    if (noteCache.containFile(note.getUrl())) {
                        if (new File(noteCache.getNoteImage(note.getUrl())).exists()) {
                            note.setLocalFilePath(noteCache.getNoteImage(note.getUrl()));
                            Log.e("check_note", "post note:" + note);
                            showNote(eventNote);
                            return;
                        } else {
                            noteCache.removeNoteImage(note.getUrl());
                        }
                    }
                }
            }
        }).doOnNext(new Consumer<EventNote>() {
            @Override
            public void accept(EventNote eventNote) throws Exception {
                Log.e("check_note", "two_event_note:" + eventNote);
                Note note = eventNote.getNote();
                if (note != null && !TextUtils.isEmpty(note.getLocalFilePath())) {
                    return;
                }
                note.setLocalFilePath(FileUtils.getBaseDir() + note.getUrl().substring(note.getUrl().lastIndexOf("/")));
                safeDownloadNote(eventNote, noteCache, true);

            }
        }).subscribe();

    }

    private synchronized void safeDownloadNote(final EventNote note, final NoteImageCache imageCache, final boolean needRedownload) {


        final ThreadLocal<EventNote> localNote = new ThreadLocal<>();
        localNote.set(note);
//      DownloadUtil.get().cancelAll();
        DownloadUtil.get().syncDownload(localNote.get(), new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(int code) {
                localNote.get().getNote().setLocalFilePath(note.getNote().getLocalFilePath());
                Log.e("safeDownloadFile", "onDownloadSuccess:" + localNote.get());
                imageCache.cacheNoteImage(localNote.get().getNote().getUrl(), localNote.get().getNote().getLocalFilePath());
                showNote(localNote.get());

            }

            @Override
            public void onDownloading(int progress) {

            }

            @Override
            public void onDownloadFailed() {
                Log.e("safeDownloadFile", "onDownloadFailed:" + localNote.get());
                if (needRedownload) {
                    safeDownloadNote(note, imageCache, false);
                }
            }
        });
    }


}
