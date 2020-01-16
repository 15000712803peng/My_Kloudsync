package com.kloudsync.techexcel.help;

import android.annotation.SuppressLint;
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
import com.kloudsync.techexcel.bean.DocumentPage;
import com.kloudsync.techexcel.bean.EventCloseNoteView;
import com.kloudsync.techexcel.bean.EventNote;
import com.kloudsync.techexcel.bean.EventNoteErrorShowDocument;
import com.kloudsync.techexcel.bean.EventShowNotePage;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.MeetingDocument;
import com.kloudsync.techexcel.bean.MeetingType;
import com.kloudsync.techexcel.bean.NoteDetail;
import com.kloudsync.techexcel.bean.SupportDevice;
import com.kloudsync.techexcel.bean.UserNotes;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.info.Uploadao;
import com.kloudsync.techexcel.tool.DocumentModel;
import com.kloudsync.techexcel.tool.DocumentPageCache;
import com.kloudsync.techexcel.tool.NoteImageCache;
import com.kloudsync.techexcel.view.spinner.NiceSpinner;
import com.kloudsync.techexcel.view.spinner.OnSpinnerItemSelectedListener;
import com.kloudsync.techexcel.view.spinner.UserNoteTextFormatter;
import com.ub.techexcel.bean.Note;
import com.ub.techexcel.tools.DownloadUtil;
import com.ub.techexcel.tools.FileUtils;
import com.ub.techexcel.tools.MeetingServiceTools;
import com.ub.techexcel.tools.ServiceInterfaceTools;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkView;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import Decoder.BASE64Encoder;
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
    private ImageView backImage;
    private NiceSpinner usersSpinner;
    private RecyclerView noteList;
    private NoteAdapter noteAdapter;
    private Note note;
    private UserNotes user;
    private List<UserNotes> users;
    private MeetingConfig meetingConfig;
    private XWalkView noteWeb;

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

    public synchronized void setContent(Context context, final View view, Note note, XWalkView noteWeb,MeetingConfig meetingConfig) {
        this.meetingConfig = meetingConfig;
        this.context = context;
        noteList = view.findViewById(R.id.list_note);
        backImage = view.findViewById(R.id.image_back);
        this.noteWeb = noteWeb;
        noteWeb.setVisibility(View.VISIBLE);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setVisibility(View.GONE);
                EventBus.getDefault().post(new EventCloseNoteView());
                close();
                instance = null;
            }
        });
        pageCache = DocumentPageCache.getInstance(context);
        noteList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        usersSpinner = view.findViewById(R.id.spinner_users);
        usersSpinner.setOnSpinnerItemSelectedListener(this);
        this.note = note;
        initWeb();
        downLoadNotePageAndShow(note);
        if(meetingConfig.getDocument() == null){
            view.setVisibility(View.GONE);
            return;
        }
        process(AppConfig.UserID, meetingConfig);
        view.setVisibility(View.VISIBLE);
    }

    private void close(){
        if (noteWeb != null) {
            noteWeb.setVisibility(View.GONE);
        }
    }

    private void initWeb() {
//        noteWeb.addJavascriptInterface(this, "AnalyticsWebInterface");
        XWalkPreferences.setValue("enable-javascript", true);
        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
        XWalkPreferences.setValue(XWalkPreferences.JAVASCRIPT_CAN_OPEN_WINDOW, true);
        XWalkPreferences.setValue(XWalkPreferences.SUPPORT_MULTIPLE_WINDOWS, true);
    }

    private void showNote(Note note) {
        this.note = note;
        int index = 0;
        if(user == null || user.getNotes() == null || user.getNotes().size() <= 0){
            return;
        }
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

    public Note getNote(){
        return note;
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
        close();
        Observable.just(user).observeOn(Schedulers.io()).doOnNext(new Consumer<UserNotes>() {
            @Override
            public void accept(UserNotes userNotes) throws Exception {
                String url = "";
                String paramsId = "";
                if (meetingConfig.getType() == MeetingType.DOC || meetingConfig.getType() == MeetingType.MEETING) {
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

    private void downLoadNotePageAndShow(Note note) {
        if(note == null || note.getDocumentPages() == null || note.getDocumentPages().size() <= 0){
            return;
        }
        Observable.just(note).observeOn(Schedulers.io()).doOnNext(new Consumer<Note>() {
            @Override
            public void accept(Note note) throws Exception {
                queryAndDownLoadNoteToShow(note.getDocumentPages().get(0),note,true);
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<Note>() {
            @Override
            public void accept(Note note) throws Exception {
                showNote(note);
            }
        }).subscribe();
    }

    DocumentPageCache pageCache;

    private void queryAndDownLoadNoteToShow(final DocumentPage documentPage, final Note note,final boolean needRedownload) {
        String pageUrl = documentPage.getPageUrl();
        DocumentPage page = pageCache.getPageCache(pageUrl);
        final EventShowNotePage notePage = new EventShowNotePage();
        notePage.setAttachmendId(note.getAttachmentID());
        notePage.setNoteId(note.getNoteID());
        Log.e("queryAndDownLoadNoteTo", "get cach page:" + page + "--> url:" + documentPage.getPageUrl());
        if (page != null && !TextUtils.isEmpty(page.getPageUrl())
                && !TextUtils.isEmpty(page.getSavedLocalPath()) && !TextUtils.isEmpty(page.getShowingPath())) {
            if (new File(page.getSavedLocalPath()).exists()) {
                page.setDocumentId(documentPage.getDocumentId());
                page.setPageNumber(documentPage.getPageNumber());
                page.setLocalFileId(documentPage.getLocalFileId());
                pageCache.cacheFile(page);
                notePage.setNotePage(page);
                EventBus.getDefault().post(notePage);
                return;
            } else {
                pageCache.removeFile(pageUrl);
            }
        }


        String meetingId = meetingConfig.getMeetingId();

        JSONObject queryDocumentResult = DocumentModel.syncQueryDocumentInDoc(AppConfig.URL_LIVEDOC + "queryDocument",
                note.getNewPath());
        if (queryDocumentResult != null) {
            Uploadao uploadao = parseQueryResponse(queryDocumentResult.toString());
            String fileName = pageUrl.substring(pageUrl.lastIndexOf("/") + 1);
            String part = "";
            if (1 == uploadao.getServiceProviderId()) {
                part = "https://s3." + uploadao.getRegionName() + ".amazonaws.com/" + uploadao.getBucketName() + "/" + note.getNewPath()
                        + "/" + fileName;
            } else if (2 == uploadao.getServiceProviderId()) {
                part = "https://" + uploadao.getBucketName() + "." + uploadao.getRegionName() + "." + "aliyuncs.com" + "/" + note.getNewPath() + "/" + fileName;
            }

            String pathLocalPath = FileUtils.getBaseDir() +
                    meetingId + "_" + encoderByMd5(part).replaceAll("/", "_") +
                    "_" + (documentPage.getPageNumber()) +
                    pageUrl.substring(pageUrl.lastIndexOf("."));
            final String showUrl = FileUtils.getBaseDir() +
                    meetingId + "_" + encoderByMd5(part).replaceAll("/", "_") +
                    "_<" + note.getPageCount() + ">" +
                    pageUrl.substring(pageUrl.lastIndexOf("."));


            Log.e("-", "showUrl:" + showUrl);

            documentPage.setSavedLocalPath(pathLocalPath);

            Log.e("-", "page:" + documentPage);
            //保存在本地的地址

            DownloadUtil.get().download(pageUrl, pathLocalPath, new DownloadUtil.OnDownloadListener() {
                @SuppressLint("LongLogTag")
                @Override
                public void onDownloadSuccess(int arg0) {
                    documentPage.setShowingPath(showUrl);
                    Log.e("queryAndDownLoadCurrentPageToShow", "onDownloadSuccess:" + documentPage + ",thread:" + Thread.currentThread());
                    pageCache.cacheFile(documentPage);
                    notePage.setNotePage(documentPage);
                    EventBus.getDefault().post(notePage);
                }

                @Override
                public void onDownloading(final int progress) {

                }

                @Override
                public void onDownloadFailed() {

                    Log.e("-", "onDownloadFailed:" + documentPage);
                    if (needRedownload) {
                        queryAndDownLoadNoteToShow(documentPage, note,false);
                    }
                }
            });
        }
    }

    public String encoderByMd5(String str) {
        try {
            //确定计算方法
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            BASE64Encoder base64en = new BASE64Encoder();
            //加密后的字符串
            String newstr = base64en.encode(md5.digest(str.getBytes("utf-8")));
            return newstr;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return "";
    }

    private Uploadao parseQueryResponse(final String jsonstring) {
        try {
            JSONObject returnjson = new JSONObject(jsonstring);
            if (returnjson.getBoolean("Success")) {
                JSONObject data = returnjson.getJSONObject("Data");

                JSONObject bucket = data.getJSONObject("Bucket");
                Uploadao uploadao = new Uploadao();
                uploadao.setServiceProviderId(bucket.getInt("ServiceProviderId"));
                uploadao.setRegionName(bucket.getString("RegionName"));
                uploadao.setBucketName(bucket.getString("BucketName"));
                return uploadao;
            }
        } catch (JSONException e) {
            return null;
        }
        return null;
    }



    private void changeNote(final int linkId) {

        if (user == null || user.getNotes() == null || user.getNotes().size() <= 0) {
            return;
        }
        if(note.getLinkID() == linkId){
            return;
        }

        Observable.just(linkId).observeOn(Schedulers.io()).map(new Function<Integer, EventNote>() {
            @Override
            public EventNote apply(Integer integer) throws Exception {
                return MeetingServiceTools.getInstance().syncGetNoteByLinkId(linkId);
            }
        }).doOnNext(new Consumer<EventNote>() {
            @Override
            public void accept(EventNote note) throws Exception {
                NoteViewManager.this.note = note.getNote();
                downLoadNotePageAndShow(note.getNote());
            }
        }).subscribe();

    }

    private void requestNoteToShow(final int noteId){
        Observable.just(noteId).observeOn(Schedulers.io()).map(new Function<Integer, EventNote>() {
            @Override
            public EventNote apply(Integer integer) throws Exception {
                return MeetingServiceTools.getInstance().syncGetNoteByNoteId(noteId);
            }
        }).doOnNext(new Consumer<EventNote>() {
            @Override
            public void accept(EventNote note) throws Exception {
//                Log.e("check_note","event_note_local_file_id:" + note.getNote().getLocalFileID());
                if(note.getNote() == null){
                    EventBus.getDefault().post(new EventNoteErrorShowDocument());
                }else {
                    NoteViewManager.this.note = note.getNote();
                    Log.e("requestNoteToShow","note:" + note.getNote());
                    downLoadNotePageAndShow(note.getNote());
                }

            }
        }).subscribe();
    }

    public void followShowNote(Context context, final View view,XWalkView noteWeb,final int noteId,MeetingConfig meetingConfig,ImageView menuIcon){
        this.meetingConfig = meetingConfig;
        this.context = context;
        noteList = view.findViewById(R.id.list_note);
        backImage = view.findViewById(R.id.image_back);
        this.noteWeb = noteWeb;
        noteWeb.setVisibility(View.VISIBLE);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setVisibility(View.GONE);
                close();
                instance = null;
            }
        });
        pageCache = DocumentPageCache.getInstance(context);
        noteList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        usersSpinner = view.findViewById(R.id.spinner_users);
        usersSpinner.setOnSpinnerItemSelectedListener(this);
        initWeb();
        requestNoteToShow(noteId);

        if(meetingConfig.getType() == MeetingType.MEETING){

        }else {
            if(meetingConfig.getDocument() == null){
                view.setVisibility(View.GONE);
                menuIcon.setVisibility(View.VISIBLE);
                return;
            }
            process(AppConfig.UserID, meetingConfig);
        }
        view.setVisibility(View.VISIBLE);
    }

    public void getNotePageActionsToShow(MeetingConfig meetingConfig){
        PageActionsAndNotesMgr.requestActionsForNotePage(meetingConfig,note);
    }


}
