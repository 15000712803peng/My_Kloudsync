package com.kloudsync.techexcel.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.widget.Toast;

import com.facebook.network.connectionclass.ConnectionClassManager;
import com.facebook.network.connectionclass.ConnectionQuality;
import com.google.gson.Gson;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.BookNote;
import com.kloudsync.techexcel.bean.DocumentPage;
import com.kloudsync.techexcel.bean.EventClose;
import com.kloudsync.techexcel.bean.EventCloseNoteView;
import com.kloudsync.techexcel.bean.EventExit;
import com.kloudsync.techexcel.bean.EventHighlightNote;
import com.kloudsync.techexcel.bean.EventMeetingDocuments;
import com.kloudsync.techexcel.bean.EventNote;
import com.kloudsync.techexcel.bean.EventNoteErrorShowDocument;
import com.kloudsync.techexcel.bean.EventNotePageActions;
import com.kloudsync.techexcel.bean.EventOpenNote;
import com.kloudsync.techexcel.bean.EventOpenOrCloseBluethoothNote;
import com.kloudsync.techexcel.bean.EventPageActions;
import com.kloudsync.techexcel.bean.EventPageNotes;
import com.kloudsync.techexcel.bean.EventPlaySoundtrack;
import com.kloudsync.techexcel.bean.EventPresnterChanged;
import com.kloudsync.techexcel.bean.EventRefreshDocs;
import com.kloudsync.techexcel.bean.EventRefreshMembers;
import com.kloudsync.techexcel.bean.EventSelectNote;
import com.kloudsync.techexcel.bean.EventSetPresenter;
import com.kloudsync.techexcel.bean.EventShowMenuIcon;
import com.kloudsync.techexcel.bean.EventShowNotePage;
import com.kloudsync.techexcel.bean.EventSocketMessage;
import com.kloudsync.techexcel.bean.HelloMessage;
import com.kloudsync.techexcel.bean.JoinMeetingMessage;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.MeetingDocument;
import com.kloudsync.techexcel.bean.MeetingMember;
import com.kloudsync.techexcel.bean.MeetingType;
import com.kloudsync.techexcel.bean.NoteDetail;
import com.kloudsync.techexcel.bean.NoteId;
import com.kloudsync.techexcel.bean.SoundTrack;
import com.kloudsync.techexcel.bean.SoundtrackDetail;
import com.kloudsync.techexcel.bean.SoundtrackDetailData;
import com.kloudsync.techexcel.bean.SupportDevice;
import com.kloudsync.techexcel.bean.TvDevice;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.config.RealMeetingSetting;
import com.kloudsync.techexcel.dialog.AddFileFromDocumentDialog;
import com.kloudsync.techexcel.dialog.AddFileFromFavoriteDialog;
import com.kloudsync.techexcel.dialog.CenterToast;
import com.kloudsync.techexcel.dialog.RecordNoteActionManager;
import com.kloudsync.techexcel.dialog.RecordPlayDialog;
import com.kloudsync.techexcel.dialog.SoundtrackPlayDialog;
import com.kloudsync.techexcel.dialog.SoundtrackRecordManager;
import com.kloudsync.techexcel.dialog.plugin.UserNotesDialog;
import com.kloudsync.techexcel.help.AddDocumentTool;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.BottomMenuManager;
import com.kloudsync.techexcel.help.ChatManager;
import com.kloudsync.techexcel.help.DeviceManager;
import com.kloudsync.techexcel.help.DocVedioManager;
import com.kloudsync.techexcel.help.MeetingKit;
import com.kloudsync.techexcel.help.NoteViewManager;
import com.kloudsync.techexcel.help.PageActionsAndNotesMgr;
import com.kloudsync.techexcel.help.PopBottomChat;
import com.kloudsync.techexcel.help.PopBottomFile;
import com.kloudsync.techexcel.help.PopBottomMenu;
import com.kloudsync.techexcel.help.SetPresenterDialog;
import com.kloudsync.techexcel.help.ShareDocumentDialog;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.help.UserData;
import com.kloudsync.techexcel.info.Uploadao;
import com.kloudsync.techexcel.response.DevicesResponse;
import com.kloudsync.techexcel.service.ConnectService;
import com.kloudsync.techexcel.tool.DocumentModel;
import com.kloudsync.techexcel.tool.DocumentPageCache;
import com.kloudsync.techexcel.tool.DocumentUploadTool;
import com.kloudsync.techexcel.tool.LocalNoteManager;
import com.kloudsync.techexcel.tool.MeetingSettingCache;
import com.kloudsync.techexcel.tool.QueryLocalNoteTool;
import com.kloudsync.techexcel.tool.SocketMessageManager;
import com.mining.app.zxing.MipcaActivityCapture;
import com.ub.kloudsync.activity.TeamSpaceInterfaceListener;
import com.ub.kloudsync.activity.TeamSpaceInterfaceTools;
import com.ub.service.activity.SocketService;
import com.ub.techexcel.adapter.BottomFileAdapter;
import com.ub.techexcel.bean.EventMuteAll;
import com.ub.techexcel.bean.EventUnmuteAll;
import com.ub.techexcel.bean.Note;
import com.ub.techexcel.bean.Record;
import com.ub.techexcel.tools.DevicesListDialog;
import com.ub.techexcel.tools.DownloadUtil;
import com.ub.techexcel.tools.ExitDialog;
import com.ub.techexcel.tools.FavoriteVideoPopup;
import com.ub.techexcel.tools.FileUtils;
import com.ub.techexcel.tools.MeetingRecordsDialog;
import com.ub.techexcel.tools.MeetingServiceTools;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;
import com.ub.techexcel.tools.Tools;
import com.ub.techexcel.tools.UserSoundtrackDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xwalk.core.XWalkPreferences;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import Decoder.BASE64Encoder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by tonyan on 2019/11/19.
 */
public class NoteViewActivity extends BaseMeetingViewActivity implements PopBottomMenu.BottomMenuOperationsListener, PopBottomFile.BottomFileOperationsListener, AddFileFromFavoriteDialog.OnFavoriteDocSelectedListener,
        BottomFileAdapter.OnDocumentClickListener, View.OnClickListener, AddFileFromDocumentDialog.OnDocSelectedListener {

    public static MeetingConfig meetingConfig;
    private SocketMessageManager messageManager;
    //---
    private BottomMenuManager menuManager;
    private PopBottomFile bottomFilePop;
    Gson gson;
    private String attachmentUrl;

    private String localFileId;

    @Override
    public void showErrorPage() {

    }

    @Override
    public void initData() {

        boolean createSuccess = FileUtils.createFileSaveDir(this);
        if (!createSuccess) {
            Toast.makeText(getApplicationContext(), "文件系统异常，打开失败", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        writeNoteBlankPageImage();
        initViews();
        //----
        RealMeetingSetting realMeetingSetting = MeetingSettingCache.getInstance(this).getMeetingSetting();
        meetingConfig = getConfig();
        messageManager = SocketMessageManager.getManager(this);
        messageManager.registerMessageReceiver();
        messageManager.sendMessage_JoinMeeting(meetingConfig);
        pageCache = DocumentPageCache.getInstance(this);
        //--
        initWeb();
        menuManager = BottomMenuManager.getInstance(this, meetingConfig);
        menuManager.setShowMeetingRecordPlay(true);
        menuManager.setBottomMenuOperationsListener(this);
        menuManager.setMenuIcon(menu);
        bottomFilePop = new PopBottomFile(this);
        gson = new Gson();
    }


    private void writeNoteBlankPageImage() {
        File localNoteFile = new File(FileUtils.getBaseDir() + "note" + File.separator + "blank_note_1.jpg");
        if (localNoteFile.exists()) {
            return;
        }
        new File(FileUtils.getBaseDir() + "note").mkdirs();
        Observable.just(localNoteFile).observeOn(Schedulers.io()).doOnNext(new Consumer<File>() {
            @Override
            public void accept(File file) throws Exception {
                copyAssetsToDst("blank_note_1.jpg", file);
            }
        }).subscribe();

    }

    private void copyAssetsToDst(String srcPath, File dstPath) {
        try {
            InputStream is = getAssets().open(srcPath);
            Log.e("copy_file", "is:" + is);
            FileOutputStream fos = new FileOutputStream(dstPath);
            Log.e("copy_file", "fos:" + fos);
            byte[] buffer = new byte[1024];
            int byteCount;
            while ((byteCount = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();
            is.close();
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("copy_file", "Exception:" + e.getMessage());

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private ConnectionChangedListener connectionChangedListener = new ConnectionChangedListener();


    private class ConnectionChangedListener implements ConnectionClassManager.ConnectionClassStateChangeListener {
        @Override
        public void onBandwidthStateChange(ConnectionQuality connectionQuality) {
            if (connectionQuality == ConnectionQuality.POOR || connectionQuality == ConnectionQuality.UNKNOWN) {
                MeetingKit.getInstance().retSetConfigurationBaseonNetwork(true);
            } else {
                MeetingKit.getInstance().retSetConfigurationBaseonNetwork(false);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ConnectionClassManager.getInstance().remove(connectionChangedListener);
    }

    @Override
    protected void onResume() {
        ConnectionClassManager.getInstance().register(connectionChangedListener);
        if (menuManager != null) {
            menuManager.setMenuIcon(menu);
        }
        if (bottomFilePop != null && !bottomFilePop.isShowing()) {
            menu.setImageResource(R.drawable.icon_menu);
            menu.setEnabled(true);
        }
        Tools.keepSocketServiceOn(this);
        super.onResume();
    }

    private void initWeb() {
        web.setZOrderOnTop(false);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        web.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        web.addJavascriptInterface(this, "AnalyticsWebInterface");

        XWalkPreferences.setValue("enable-javascript", true);
        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
        XWalkPreferences.setValue(XWalkPreferences.JAVASCRIPT_CAN_OPEN_WINDOW, true);
        XWalkPreferences.setValue(XWalkPreferences.SUPPORT_MULTIPLE_WINDOWS, true);
        loadWebIndex();

    }

    private void loadWebIndex() {
        int deviceType = DeviceManager.getDeviceType(this);
        String indexUrl = "file:///android_asset/index.html";
        if (deviceType == SupportDevice.BOOK) {
            indexUrl += "?devicetype=4";
        }
        final String url = indexUrl;
        web.load(url, null);
        web.load("javascript:ShowToolbar(" + false + ")", null);
        web.load("javascript:Record()", null);

    }

    private MeetingConfig getConfig() {
        Intent data = getIntent();
        if (meetingConfig == null) {
            meetingConfig = new MeetingConfig();
        }
        meetingConfig.setType(data.getIntExtra("meeting_type", MeetingType.MEETING));
        meetingConfig.setMeetingId(data.getStringExtra("meeting_id"));
        meetingConfig.setLessionId(data.getIntExtra("lession_id", 0));
        meetingConfig.setDocumentId(data.getStringExtra("document_id"));
        meetingConfig.setRole(data.getIntExtra("meeting_role", MeetingConfig.MeetingRole.HOST));
        meetingConfig.setUserToken(UserData.getUserToken(this));
        meetingConfig.setFromMeeting(data.getBooleanExtra("from_meeting", false));
        meetingConfig.setSpaceId(getIntent().getIntExtra("spaceId", 0));
        attachmentUrl = data.getStringExtra("url");
        localFileId = data.getStringExtra("local_file_id");
        currentNoteId = data.getIntExtra("note_id", 0);

        return meetingConfig;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (messageManager != null) {
            messageManager.sendMessage_LeaveMeeting(meetingConfig);
            messageManager.release();
        }

        if (menuManager != null) {
            menuManager.release();
        }

        if (wakeLock != null) {
            wakeLock.release();
        }

        MeetingKit.getInstance().release();
        if (web != null) {
            web.removeAllViews();
            web.onDestroy();
            web = null;
        }

        if (meetingConfig != null) {
            meetingConfig.reset();
        }
        meetingConfig = null;

    }


    private void requestDocumentsAndShowPage() {
        DocumentModel.asyncGetDocumentsInDocAndShowPage(meetingConfig, true);
    }

    private void requestDocuments() {
        DocumentModel.asyncGetDocumentsInDocAndShowPage(meetingConfig, false);
    }

    // ------- @Subscribe
    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void receiveDocuments(EventMeetingDocuments documents) {
        // 所有文档的data
        Log.e("receiverDocuemnts", "documents:" + documents);
        this.documents.clear();
        this.documents.addAll(documents.getDocuments());
        if (this.documents != null && this.documents.size() > 0) {
            int index = this.documents.indexOf(new MeetingDocument(meetingConfig.getFileId()));
            if (index < 0) {
                index = 0;
            }
            meetingConfig.setDocument(this.documents.get(index));
            downLoadDocumentPageAndShow();
        } else {
            hideEnterLoading();
            menu.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshDocuments(EventRefreshDocs refreshDocs) {
        // 所有文档的data
        Log.e("refreshDocuments", "documents:" + documents);
        this.documents = refreshDocs.getDocuments();
        changeDocument(documents.get(documents.indexOf(new MeetingDocument(refreshDocs.getItemId()))), refreshDocs.getPageNumber());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showDocumentPage(DocumentPage page) {
        Log.e("showDocumentPage", "page:" + page);
        hideEnterLoading();
        MeetingDocument document = getDocument(page);
        Log.e("showDocumentPage", "current_document:" + document);
        if (document != null) {
            meetingConfig.setDocument(document);
            meetingConfig.setCurrentDocumentPage(page);
            meetingConfig.setPageNumber(meetingConfig.getDocument().getDocumentPages().indexOf(page) + 1);
        }

        //notify change file
        notifyDocumentChanged();
        web.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        web.load("javascript:ShowPDF('" + page.getShowingPath() + "'," + (page.getPageNumber()) + ",''," + meetingConfig.getDocument().getAttachmentID() + "," + false + ")", null);
        web.load("javascript:Record()", null);
        if (bottomFilePop != null && bottomFilePop.isShowing()) {
            bottomFilePop.setDocuments(this.documents, meetingConfig.getDocument().getItemID(), this);
            bottomFilePop.removeTempDoc();
        } else {
            menu.setVisibility(View.VISIBLE);
        }
    }


    private long currentNoteId;

    class TempNoteData {
        private String data;
        private long noteId;

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public long getNoteId() {
            return noteId;
        }

        public void setNoteId(long noteId) {
            this.noteId = noteId;
        }
    }

    private CopyOnWriteArrayList<TempNoteData> newNoteDatas = new CopyOnWriteArrayList<>();

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showNotePage(final EventShowNotePage page) {

        Log.e("showNotePage", "page:" + page);
        if (!TextUtils.isEmpty(page.getNotePage().getLocalFileId())) {
            if (page.getNotePage().getLocalFileId().contains(".")) {
                currentNoteId = page.getNoteId();
                noteWeb.setVisibility(View.VISIBLE);
                String localNoteBlankPage = FileUtils.getBaseDir() + "note" + File.separator + "blank_note_1.jpg";
                Log.e("show_PDF", "javascript:ShowPDF('" + localNoteBlankPage + "'," + (page.getNotePage().getPageNumber()) + ",''," + page.getAttachmendId() + "," + false + ")");
                noteWeb.load("javascript:ShowPDF('" + localNoteBlankPage + "'," + (page.getNotePage().getPageNumber()) + ",''," + page.getAttachmendId() + "," + false + ")", null);
                noteWeb.load("javascript:Record()", null);
                handleBluetoothNote(page.getNotePage().getPageUrl());
                return;
            }
        }

        noteWeb.setVisibility(View.VISIBLE);
        noteWeb.load("javascript:ShowPDF('" + page.getNotePage().getShowingPath() + "'," + (page.getNotePage().getPageNumber()) + ",''," + page.getAttachmendId() + "," + false + ")", null);
        noteWeb.load("javascript:Record()", null);

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void showNote(EventNote note) {
        Note _note = note.getNote();
        Log.e("show_note", "note:" + _note);
        if (meetingConfig.getType() == MeetingType.MEETING) {
            noteUsersLayout.setVisibility(View.GONE);
        } else {
            noteUsersLayout.setVisibility(View.VISIBLE);
        }
//        NoteViewManager.getInstance().setContent(this, noteLayout, _note, noteWeb, meetingConfig);
        notifyViewNote(note.getNote());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void showSelectedNote(EventSelectNote selectNote) {
        Observable.just(selectNote).observeOn(Schedulers.io()).doOnNext(new Consumer<EventSelectNote>() {
            @Override
            public void accept(EventSelectNote selectNote) throws Exception {
                JSONObject response = ServiceInterfaceTools.getinstance().syncImportNote(meetingConfig, selectNote);
                if (response != null && response.has("RetCode")) {
                    if (response.getInt("RetCode") == 0) {
                        selectNote.setNewLinkId(response.getInt("RetData"));
                    }
                }
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<EventSelectNote>() {
            @Override
            public void accept(EventSelectNote selectNote) throws Exception {
                if (selectNote.getLinkId() > 0) {
                    deleteNote(selectNote.getLinkId());
                }
                if (selectNote.getNewLinkId() > 0) {
                    drawNote(selectNote.getNewLinkId(), selectNote.getLinkProperty(), 0);
                }
            }
        }).subscribe();
    }


    public synchronized void followShowNote(int noteId) {

        if (meetingConfig.getType() == MeetingType.MEETING) {
            noteUsersLayout.setVisibility(View.GONE);
        } else {
            noteUsersLayout.setVisibility(View.VISIBLE);
        }
        Log.e("followShowNote", "noteid:" + noteId);
        hideEnterLoading();
//        NoteViewManager.getInstance().followShowNote(this, noteLayout, noteWeb, noteId, meetingConfig, menu);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showDocumentIfRequestNoteError(EventNoteErrorShowDocument showDocument) {
        requestDocumentsAndShowPage();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showMenuIcon(EventShowMenuIcon showMenuIcon) {
        if (menu != null) {
            Log.e("showMenuIcon", "show");
            menu.setVisibility(View.VISIBLE);
            menu.setImageResource(R.drawable.icon_menu);
            menu.setEnabled(true);
            Log.e("showMenuIcon", "menu visible:  " + (menu.getVisibility() == View.VISIBLE));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void exit(EventExit exit) {
        Log.e("event_bus", "exit");
        handleExit(exit.isEnd());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void receiveSocketMessage(EventSocketMessage socketMessage) {
        Log.e("DocAndMeetingActivity", "socket_message:" + socketMessage);
        String action = socketMessage.getAction();
        if (TextUtils.isEmpty(action)) {
            return;
        }

        switch (action) {
            case SocketMessageManager.MESSAGE_LEAVE_MEETING:
                handleMessageLeaveMeeting(socketMessage.getData());
                break;

            case SocketMessageManager.MESSAGE_JOIN_MEETING:
                handleMessageJoinMeeting(socketMessage.getData());
                break;

            case SocketMessageManager.MESSAGE_BROADCAST_FRAME:

                if (socketMessage.getData() == null) {
                    return;
                }
                if (socketMessage.getData().has("data")) {
                    try {
                        String _frame = Tools.getFromBase64(socketMessage.getData().getString("data"));
                        if (noteLayout.getVisibility() == View.VISIBLE) {
                            if (noteWeb != null) {
                                noteWeb.load("javascript:PlayActionByTxt('" + _frame + "','" + 1 + "')", null);
                            }
                        } else {
                            if (web != null) {
                                web.load("javascript:PlayActionByTxt('" + _frame + "','" + 1 + "')", null);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case SocketMessageManager.MESSAGE_SEND_MESSAGE:
                if (socketMessage.getData() == null) {
                    return;
                }

                if (socketMessage.getData().has("data")) {
                    try {
                        handleMessageSendMessage(new JSONObject(Tools.getFromBase64(socketMessage.getData().getString("data"))));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case SocketMessageManager.MESSAGE_MAKE_PRESENTER:
                if (socketMessage.getData() == null) {
                    return;
                }

                try {
                    if (socketMessage.getData().has("presenterId")) {
                        meetingConfig.setPresenterId(socketMessage.getData().getString("presenterId"));
                    }
                    if (socketMessage.getData().has("presenterSessionId")) {
                        meetingConfig.setPresenterSessionId(socketMessage.getData().getString("presenterSessionId"));
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (MeetingMember member : meetingConfig.getMeetingMembers()) {
                    if ((member.getUserId() + "").equals(meetingConfig.getPresenterId())) {
                        member.setPresenter(1);
                    } else {
                        member.setPresenter(0);
                    }
                }
                EventRefreshMembers refreshMembers = new EventRefreshMembers();
                refreshMembers.setMeetingConfig(meetingConfig);
                EventBus.getDefault().post(refreshMembers);
                break;
            case SocketMessageManager.MESSAGE_ATTACHMENT_UPLOADED:
                if (socketMessage.getData() == null) {
                    return;
                }
                handleMessageAttchmentUploadedAndShow(socketMessage.getData());
                break;
            case SocketMessageManager.MESSAGE_END_MEETING:
                finish();
                break;
            case SocketMessageManager.MESSAGE_MEMBER_LIST_CHANGE:
                MeetingKit.getInstance().requestMeetingMembers(meetingConfig, false);
                break;
            case SocketMessageManager.MESSAGE_AGORA_STATUS_CHANGE:
//                handleMessageAgoraStatusChange(socketMessage.getData());
                break;

            case SocketMessageManager.MESSAGE_NOTE_DATA:
                if (socketMessage.getData().has("retData")) {
                    try {
                        JSONObject retData = socketMessage.getData().getJSONObject("retData");
                        String noteData = retData.getString("data");
                        long noteId = retData.getInt("noteId");
                        if (currentNoteId != noteId) {
                            newNoteDatas.clear();
                            TempNoteData _noteData = new TempNoteData();
                            _noteData.setData(Tools.getFromBase64(noteData));
                            _noteData.setNoteId(noteId);
                            handleBluetoothNote(noteId);
                            newNoteDatas.add(_noteData);
                            return;
                        }
                        Log.e("check_note_id", "current_note_id:" + currentNoteId + ",note_id:" + noteId);
                        String key = "ShowDotPanData";

                        if (web != null) {
                            JSONObject _data = new JSONObject();
                            _data.put("LinesData", Tools.getFromBase64(noteData));
                            _data.put("ShowInCenter", true);
                            _data.put("TriggerEvent", true);
                            web.load("javascript:FromApp('" + key + "'," + _data + ")", null);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case SocketMessageManager.MESSAGE_NOTE_CHANGE:
                if (socketMessage.getData().has("retData")) {
                    try {
                        int noteId = socketMessage.getData().getJSONObject("retData").getInt("noteId");
                        followShowNote(noteId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case SocketMessageManager.MESSAGE_NOTE_P1_CREATEAD:
                if (socketMessage.getData().has("retData")) {
                    try {
                        JSONObject p1Created = socketMessage.getData().getJSONObject("retData");
                        addLinkBorderForDTNew(p1Created);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case SocketMessageManager.MESSAGE_HELLO:
                if (socketMessage.getData().has("data")) {
                    try {
                        HelloMessage helloMessage = gson.fromJson(Tools.getFromBase64(socketMessage.getData().getString("data")),
                                HelloMessage.class);
                        handleMessageHelloMessage(helloMessage);
                        Log.e("check_hello_message", "hello_message:" + helloMessage);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void handleMessageHelloMessage(HelloMessage helloMessage) {
        if (helloMessage == null) {
            return;
        }

    }

    private void addLinkBorderForDTNew(JSONObject p1Created) throws JSONException {
        if (p1Created.has("noteId")) {
            if (currentNoteId == p1Created.getInt("noteId")) {
//                noteWeb.load("javascript:whiteboard");
                JSONArray positionArray = new JSONArray(p1Created.getString("position"));
                Log.e("addLinkBorderForDTNew", "positionArray:" + positionArray);
                JSONObject info = new JSONObject();
                info.put("ProjectID", p1Created.getInt("projectId"));
                info.put("TaskID", p1Created.getInt("itemId"));
                for (int i = 0; i < positionArray.length(); ++i) {
                    JSONObject position = positionArray.getJSONObject(i);
                    doDrawDTNewBorder(position.getInt("left"), position.getInt("top"), position.getInt("width"), position.getInt("height"), info);
                }
            }
        }
    }

    private void doDrawDTNewBorder(int x, int y, int width, int height, JSONObject info) throws JSONException {
        JSONObject message = new JSONObject();
        message.put("type", 40);
        message.put("CW", 678);
        message.put("x", x);
        message.put("y", y);
        message.put("width", width);
        message.put("height", height);
        message.put("info", info);

        JSONObject clearLastMessage = new JSONObject();
        clearLastMessage.put("type", 40);

        Log.e("doDrawDTNewBorder", "border_PlayActionByTxt:" + message);
        noteWeb.load("javascript:PlayActionByTxt('" + message + "')", null);
    }

    @Subscribe
    public void receiveEventClose(EventClose close) {
        Log.e("receiveEventClose", "close");
        finish();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receivePageActions(EventPageActions pageActions) {
        String data = pageActions.getData();
        if (!TextUtils.isEmpty(data)) {
            if (pageActions.getPageNumber() == meetingConfig.getPageNumber()) {
                Log.e("check_play_txt", "PlayActionByArray:" + data);
                web.load("javascript:PlayActionByArray(" + data + "," + 0 + ")", null);
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveNotePageActions(EventNotePageActions pageActions) {
        String data = pageActions.getData();
        if (!TextUtils.isEmpty(data)) {
            if (noteLayout.getVisibility() == View.VISIBLE) {
                noteWeb.load("javascript:PlayActionByArray(" + data + "," + 0 + ")", null);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receivePageNotes(EventPageNotes pageNotes) {
        Log.e("receivePageNotes", "page_notes:" + pageNotes);
        List<NoteDetail> notes = pageNotes.getNotes();
        if (notes != null && notes.size() > 0) {
            if (pageNotes.getPageNumber() == meetingConfig.getPageNumber()) {
                if (messageManager != null) {
                    for (NoteDetail note : notes) {

                        try {
                            JSONObject message = new JSONObject();
                            message.put("type", 38);
                            message.put("LinkID", note.getLinkID());
                            message.put("IsOther", false);
                            if (!TextUtils.isEmpty(note.getLinkProperty())) {
                                message.put("LinkProperty", new JSONObject(note.getLinkProperty()));
                            }
                            Log.e("check_play_txt", "notes_PlayActionByTxt:" + message);
                            web.load("javascript:PlayActionByTxt('" + message + "')", null);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void uploadNodeSuccess(NoteId noteId) {
        Log.e("event_bus", "draw_note_by_id:" + noteId);
        if (noteId.getLinkID() == 0) {
            return;
        }
        deleteTempNote();
        drawNote(noteId.getLinkID(), meetingConfig.getCurrentLinkProperty(), 0);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventMuteAllMembersAudio(EventMuteAll muteAll) {
        MeetingKit.getInstance().menuMicroClicked(false);
        SocketMessageManager.getManager(this).sendMessage_MuteStatus(0);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventUnmuteAllMembersAudio(EventUnmuteAll unmuteAll) {
        MeetingKit.getInstance().menuMicroClicked(true);
        SocketMessageManager.getManager(this).sendMessage_MuteStatus(1);
    }

    private void drawNote(int linkId, JSONObject linkProperty, int isOther) {
        JSONObject noteData = new JSONObject();
        try {
            noteData.put("type", 38);
            noteData.put("LinkID", linkId);
            noteData.put("IsOther", isOther);
            noteData.put("LinkProperty", linkProperty);
            Log.e("drawNote", "note:" + noteData.toString());
            if (web != null) {
                web.load("javascript:PlayActionByTxt('" + noteData + "')", null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void drawTempNote() {
        drawNote(-1, meetingConfig.getCurrentLinkProperty(), 0);
    }

    private void deleteNote(int linkId) {
        String url = AppConfig.URL_PUBLIC + "DocumentNote/RemoveNote?linkIDs=" + linkId;
        JSONObject noteData = new JSONObject();
        try {
            noteData.put("type", 102);
            noteData.put("id", "BooXNote_" + linkId);
            Log.e("deleteTempNote", "note:" + noteData.toString());
            if (web != null) {
                web.load("javascript:PlayActionByTxt('" + noteData + "')", null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ServiceInterfaceTools.getinstance().removeNote(url, ServiceInterfaceTools.REMOVENOTE, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {

            }
        });
    }

    private void deleteTempNote() {
        deleteNote(-1);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void highLightNote(final EventHighlightNote note) {

        if (note.getPageNumber() == meetingConfig.getPageNumber()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("LinkID", note.getNote().getLinkID());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String key = "TwinkleBookNote";
            web.load("javascript:FromApp('" + key + "'," + jsonObject + ")", null);

        } else {

//            if (note.getNote().getAttachmentID() != meetingConfig.getDocument().getAttachmentID()) {
//                Log.e("highLightNote", "in_different_document");
//                final int index = documents.indexOf(new MeetingDocument(note.getNote().getDocumentItemID()));
//                changeDocument(note.getNote().getDocumentItemID(), note.getPageNumber());
//
//            } else

            {
                Log.e("highLightNote", "in_same_document");
                final String notifyUrl = meetingConfig.getNotifyUrl();
                if (TextUtils.isEmpty(notifyUrl)) {
                    return;
                }
                if (note.getPageNumber() - 1 < 0 || note.getPageNumber() > meetingConfig.getDocument().getPageCount()) {
                    return;
                }
                final DocumentPage page = meetingConfig.getDocument().getDocumentPages().get(note.getPageNumber() - 1);
                if (page == null) {
                    return;
                }
                final String pathLocalPath = notifyUrl.substring(0, notifyUrl.lastIndexOf("<")) +
                        note.getPageNumber() + notifyUrl.substring(notifyUrl.lastIndexOf("."));
                if (!TextUtils.isEmpty(page.getPageUrl())) {
                    DocumentPage _page = pageCache.getPageCache(page.getPageUrl());
                    Log.e("check_cache_page", "_page:" + _page + "，page:" + page);

                    if (_page != null && page.getPageUrl().equals(_page.getPageUrl())) {
                        if (!TextUtils.isEmpty(_page.getSavedLocalPath())) {
                            File localFile = new File(_page.getSavedLocalPath());
                            if (localFile.exists()) {
                                if (!pathLocalPath.equals(localFile.getAbsolutePath())) {
                                    if (localFile.renameTo(new File(pathLocalPath))) {
                                        Log.e("highLightNote", "uncorrect_file_name,rename");
                                        notifyWebFilePrepared(notifyUrl, note.getPageNumber());
                                        page.setSavedLocalPath(pathLocalPath);
                                        page.setShowingPath(notifyUrl);
                                        pageCache.cacheFile(page);
                                        highLightNoteInDifferentPage(page, note);
                                        return;
                                    } else {
                                        Log.e("highLightNote", "uncorrect_file_name,delete");
                                        localFile.delete();
                                    }
                                } else {
                                    Log.e("highLightNote", "correct_file_name,notify");
                                    page.setSavedLocalPath(pathLocalPath);
                                    page.setShowingPath(notifyUrl);
                                    pageCache.cacheFile(page);
                                    notifyWebFilePrepared(notifyUrl, note.getPageNumber());
                                    highLightNoteInDifferentPage(page, note);
                                    return;
                                }

                            } else {
                                //清楚缓存
                                pageCache.removeFile(_page.getPageUrl());
                            }

                        }
                    }
                }

                Observable.just(meetingConfig.getDocument()).observeOn(Schedulers.io()).map(new Function<MeetingDocument, Object>() {
                    @Override
                    public Object apply(MeetingDocument document) throws Exception {
                        safeDownloadFile(page, pathLocalPath, note.getPageNumber(), true);
                        return document;
                    }
                }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        highLightNoteInDifferentPage(page, note);

                    }
                }).subscribe();
            }


        }
    }


    private void handleWebUISetting() {
        if (meetingConfig.getType() != MeetingType.MEETING) {
            return;
        }


    }


    private void highLightNoteInDifferentPage(DocumentPage page, EventHighlightNote note) {
        showDocumentPage(page);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("LinkID", note.getNote().getLinkID());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Observable.just(page).delay(500, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<DocumentPage>() {
            @Override
            public void accept(DocumentPage page) throws Exception {
                String key = "TwinkleBookNote";
                Log.e("TwinkleBookNote", "delay");
                web.load("javascript:FromApp('" + key + "'," + jsonObject + ")", null);
            }
        }).subscribe();

    }

    private MeetingDocument getDocument(DocumentPage page) {
        Log.e("check_page", "current_page:" + page);
        for (MeetingDocument document : documents) {
//            if(document.getDocumentPages().contains(page)){
//                return document;
//            }
            for (DocumentPage _page : document.getDocumentPages()) {
                Log.e("check_page", "page:" + _page);
                if (_page.equals(page)) {
                    return document;
                }
            }
        }
        return null;
    }

    private void downLoadDocumentPageAndShow() {
        Observable.just(meetingConfig.getDocument()).observeOn(Schedulers.io()).map(new Function<MeetingDocument, Object>() {
            @Override
            public Object apply(MeetingDocument document) throws Exception {
                int pageNumber = 1;
                if (meetingConfig.getPageNumber() == 0) {
                    pageNumber = 1;
                } else if (meetingConfig.getPageNumber() > 0) {
                    pageNumber = meetingConfig.getPageNumber();
                }

                if (pageNumber > document.getDocumentPages().size()) {
                    pageNumber = document.getDocumentPages().size();
                }
                DocumentPage page = document.getDocumentPages().get(pageNumber - 1);
                queryAndDownLoadPageToShow(page, true);
                return page;
            }
        }).subscribe();
    }

    private synchronized void downLoadDocumentPageAndShow(MeetingDocument document, final int pageNumber) {
        Observable.just(document).observeOn(Schedulers.io()).map(new Function<MeetingDocument, Object>() {
            @Override
            public Object apply(MeetingDocument document) throws Exception {
                queryAndDownLoadPageToShow(document, pageNumber, true);
                return document;
            }
        }).subscribe();
    }

    private List<MeetingDocument> documents = new ArrayList<>();
    private DocumentPageCache pageCache;

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

    private void queryAndDownLoadPageToShow(final DocumentPage documentPage, final boolean needRedownload) {
        String pageUrl = documentPage.getPageUrl();
        DocumentPage page = pageCache.getPageCache(pageUrl);
        Log.e("check_download_page", "get_cach_page:" + page + "--> url:" + documentPage.getPageUrl());
        if (page != null && !TextUtils.isEmpty(page.getPageUrl())
                && !TextUtils.isEmpty(page.getSavedLocalPath()) && !TextUtils.isEmpty(page.getShowingPath())) {
            if (new File(page.getSavedLocalPath()).exists()) {
                page.setDocumentId(documentPage.getDocumentId());
                page.setPageNumber(documentPage.getPageNumber());
                pageCache.cacheFile(page);
                EventBus.getDefault().post(page);
                return;
            } else {
                pageCache.removeFile(pageUrl);
            }
        }

        MeetingDocument document = meetingConfig.getDocument();
        String meetingId = meetingConfig.getMeetingId();

        JSONObject queryDocumentResult = DocumentModel.syncQueryDocumentInDoc(AppConfig.URL_LIVEDOC + "queryDocument",
                document.getNewPath());
        if (queryDocumentResult != null) {
            Uploadao uploadao = parseQueryResponse(queryDocumentResult.toString());
            String fileName = pageUrl.substring(pageUrl.lastIndexOf("/") + 1);
            String part = "";
            if (1 == uploadao.getServiceProviderId()) {
                part = "https://s3." + uploadao.getRegionName() + ".amazonaws.com/" + uploadao.getBucketName() + "/" + document.getNewPath()
                        + "/" + fileName;
            } else if (2 == uploadao.getServiceProviderId()) {
                part = "https://" + uploadao.getBucketName() + "." + uploadao.getRegionName() + "." + "aliyuncs.com" + "/" + document.getNewPath() + "/" + fileName;
            }

            String pathLocalPath = FileUtils.getBaseDir() +
                    meetingId + "_" + encoderByMd5(part).replaceAll("/", "_") +
                    "_" + (documentPage.getPageNumber()) +
                    pageUrl.substring(pageUrl.lastIndexOf("."));
            final String showUrl = FileUtils.getBaseDir() +
                    meetingId + "_" + encoderByMd5(part).replaceAll("/", "_") +
                    "_<" + document.getPageCount() + ">" +
                    pageUrl.substring(pageUrl.lastIndexOf("."));
            int pageIndex = 1;
            if (meetingConfig.getPageNumber() == 0) {
                pageIndex = 1;
            } else if (meetingConfig.getPageNumber() > 0) {
                pageIndex = meetingConfig.getPageNumber();
            }


            documentPage.setSavedLocalPath(pathLocalPath);

            Log.e("check_download_page", "download_page:" + documentPage);
            //保存在本地的地址

            DownloadUtil.get().download(pageUrl, pathLocalPath, new DownloadUtil.OnDownloadListener() {
                @SuppressLint("LongLogTag")
                @Override
                public void onDownloadSuccess(int arg0) {
                    documentPage.setShowingPath(showUrl);
                    Log.e("queryAndDownLoadCurrentPageToShow", "onDownloadSuccess:" + documentPage);
                    pageCache.cacheFile(documentPage);
                    EventBus.getDefault().post(documentPage);
                }

                @Override
                public void onDownloading(final int progress) {

                }

                @Override
                public void onDownloadFailed() {

                    Log.e("-", "onDownloadFailed:" + documentPage);
                    if (needRedownload) {
                        queryAndDownLoadPageToShow(documentPage, false);
                    }
                }
            });
        }
    }

    private synchronized void queryAndDownLoadPageToShow(final MeetingDocument document, final int pageNumber, final boolean needRedownload) {
        final DocumentPage _page = document.getDocumentPages().get(pageNumber - 1);
        String pageUrl = _page.getPageUrl();
        final DocumentPage page = pageCache.getPageCache(pageUrl);
        Log.e("-", "get cach page:" + page + "--> url:" + pageUrl);
        if (page != null && !TextUtils.isEmpty(page.getPageUrl())
                && !TextUtils.isEmpty(page.getSavedLocalPath()) && !TextUtils.isEmpty(page.getShowingPath())) {
            if (new File(page.getSavedLocalPath()).exists()) {
                page.setDocumentId(_page.getDocumentId());
                page.setPageNumber(_page.getPageNumber());
                pageCache.cacheFile(page);
                EventBus.getDefault().post(page);
                return;
            } else {
                pageCache.removeFile(pageUrl);
            }
        }

        String meetingId = meetingConfig.getMeetingId();
        JSONObject queryDocumentResult = DocumentModel.syncQueryDocumentInDoc(AppConfig.URL_LIVEDOC + "queryDocument",
                document.getNewPath());
        if (queryDocumentResult != null) {
            Uploadao uploadao = parseQueryResponse(queryDocumentResult.toString());
            String fileName = pageUrl.substring(pageUrl.lastIndexOf("/") + 1);
            String part = "";
            if (1 == uploadao.getServiceProviderId()) {
                part = "https://s3." + uploadao.getRegionName() + ".amazonaws.com/" + uploadao.getBucketName() + "/" + document.getNewPath()
                        + "/" + fileName;
            } else if (2 == uploadao.getServiceProviderId()) {
                part = "https://" + uploadao.getBucketName() + "." + uploadao.getRegionName() + "." + "aliyuncs.com" + "/" + document.getNewPath() + "/" + fileName;
            }

            String pathLocalPath = FileUtils.getBaseDir() +
                    meetingId + "_" + encoderByMd5(part).replaceAll("/", "_") +
                    "_" + (_page.getPageNumber()) +
                    pageUrl.substring(pageUrl.lastIndexOf("."));
            final String showUrl = FileUtils.getBaseDir() +
                    meetingId + "_" + encoderByMd5(part).replaceAll("/", "_") +
                    "_<" + document.getPageCount() + ">" +
                    pageUrl.substring(pageUrl.lastIndexOf("."));

            Log.e("-", "showUrl:" + showUrl);

            _page.setSavedLocalPath(pathLocalPath);

            Log.e("-", "page:" + _page);
            //保存在本地的地址

            DownloadUtil.get().download(pageUrl, pathLocalPath, new DownloadUtil.OnDownloadListener() {
                @SuppressLint("LongLogTag")
                @Override
                public void onDownloadSuccess(int arg0) {
                    _page.setShowingPath(showUrl);
                    Log.e("queryAndDownLoadCurrentPageToShow", "onDownloadSuccess:" + page);
                    pageCache.cacheFile(_page);
                    EventBus.getDefault().post(_page);
                }

                @Override
                public void onDownloading(final int progress) {

                }

                @Override
                public void onDownloadFailed() {

                    Log.e("-", "onDownloadFailed:" + _page);
                    if (needRedownload) {
                        queryAndDownLoadPageToShow(document, pageNumber, false);
                    }
                }
            });
        }
    }

    private synchronized void changeDocument(MeetingDocument document, int pageNumber) {
        Log.e("changeDocument", "document:" + document);
        downLoadDocumentPageAndShow(document, pageNumber);
    }

    private synchronized void changeDocument(int itemId, int pageNumber) {
        if (hasLoadedFile) {
            int index = documents.indexOf(new MeetingDocument(itemId));
            if (index < 0) {
                return;
            }
            MeetingDocument _document = documents.get(index);
            if (meetingConfig.getDocument().equals(_document)) {
                return;
            }
            changeDocument(_document, pageNumber);

        } else {
            DocumentModel.asyncGetDocumentsInDocAndRefreshFileList(meetingConfig, itemId, pageNumber);
        }

    }


    private synchronized void safeDownloadFile(final DocumentPage page, final String localPath, final int pageNumber, final boolean needRedownload) {

        Log.e("safeDownloadFile", "start down load:" + page);
        final String url = meetingConfig.getNotifyUrl();

        page.setSavedLocalPath(localPath);
        final ThreadLocal<DocumentPage> localPage = new ThreadLocal<>();
        localPage.set(page);
//      DownloadUtil.get().cancelAll();
        DownloadUtil.get().syncDownload(localPage.get(), new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(int code) {
                localPage.get().setShowingPath(url);
                Log.e("safeDownloadFile", "onDownloadSuccess:" + localPage.get());
                pageCache.cacheFile(localPage.get());
                notifyWebFilePrepared(url, pageNumber);
            }

            @Override
            public void onDownloading(int progress) {

            }

            @Override
            public void onDownloadFailed() {
                Log.e("safeDownloadFile", "onDownloadFailed:" + localPage.get());
                if (needRedownload) {
                    safeDownloadFile(page, localPath, pageNumber, false);
                }
            }
        });
    }


    private synchronized void safeDownloadFile(final String pathLocalPath, final DocumentPage page, final String notifyUrl, final int index, final boolean needRedownload) {

        Log.e("safeDownloadFile", "start down load:" + page);
        page.setSavedLocalPath(pathLocalPath);
        final ThreadLocal<DocumentPage> localPage = new ThreadLocal<>();
        localPage.set(page);
//      DownloadUtil.get().cancelAll();
        DownloadUtil.get().syncDownload(localPage.get(), new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(int code) {
                localPage.get().setShowingPath(notifyUrl);
                Log.e("safeDownloadFile", "onDownloadSuccess:" + localPage.get());
                pageCache.cacheFile(localPage.get());
                notifyWebFilePrepared(notifyUrl, index);
            }

            @Override
            public void onDownloading(int progress) {

            }

            @Override
            public void onDownloadFailed() {
                Log.e("safeDownloadFile", "onDownloadFailed:" + localPage.get());
                if (needRedownload) {
                    safeDownloadFile(pathLocalPath, page, notifyUrl, index, false);
                }
            }
        });
    }

    private void notifyWebFilePrepared(final String url, final int index) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e("WebView_Load", "javascript:AfterDownloadFile('" + url + "', " + index + ")");
                web.load("javascript:AfterDownloadFile('" + url + "', " + index + ")", null);

            }
        });
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


    //-----  JavascriptInterface ----
    @org.xwalk.core.JavascriptInterface
    public void afterLoadPageFunction() {

        Log.e("local_file_id", "localFileId:" + localFileId);
        Log.e("JavascriptInterface", "afterLoadPageFunction");
        if (!TextUtils.isEmpty(localFileId) && localFileId.contains(".")) {
            Observable.just("load_note_page").observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                @Override
                public void accept(String s) throws Exception {
                    hideEnterLoading();
                    String localNoteBlankPage = FileUtils.getBaseDir() + "note" + File.separator + "blank_note_1.jpg";
                    Log.e("show_PDF", "javascript:ShowPDF('" + localNoteBlankPage + "'," + 1 + ",''," + meetingConfig.getDocumentId() + "," + true + ")");
                    web.load("javascript:ShowPDF('" + localNoteBlankPage + "'," + (1) + ",''," + meetingConfig.getDocumentId() + "," + true + ")", null);
                    web.load("javascript:Record()", null);
                }
            });
        }


    }

    @org.xwalk.core.JavascriptInterface
    public void userSettingChangeFunction(final String option) {
        Log.e("JavascriptInterface", "userSettingChangeFunction,option:  " + option);

    }

    @org.xwalk.core.JavascriptInterface
    public synchronized void preLoadFileFunction(final String url, final int currentpageNum, final boolean showLoading) {
        Log.e("JavascriptInterface", "preLoadFileFunction,url:  " + url + ", currentpageNum:" + currentpageNum + ",showLoading:" + showLoading);

    }


    private boolean hasLoadedFile = false;

    @org.xwalk.core.JavascriptInterface
    public void afterLoadFileFunction() {
        Log.e("JavascriptInterface", "afterLoadFileFunction");
        hasLoadedFile = true;


    }

    @org.xwalk.core.JavascriptInterface
    public void showErrorFunction(final String error) {
        Log.e("JavascriptInterface", "showErrorFunction,error:  " + error);

    }

    @org.xwalk.core.JavascriptInterface
    public void afterChangePageFunction(final int pageNum, int type) {
        Log.e("JavascriptInterface", "afterChangePageFunction,pageNum:  " + pageNum + ", type:" + type);
        if (!TextUtils.isEmpty(attachmentUrl)) {
            handleBluetoothNote(attachmentUrl);
        }

    }

    @org.xwalk.core.JavascriptInterface
    public void reflect(String result) {
        Log.e("JavascriptInterface", "reflect,result:  " + result);
    }

    private boolean checkIfModifyDoc(String result) {
        if (meetingConfig.isDocModifide()) {
            return true;
        }
        if (!TextUtils.isEmpty(result)) {
            try {
                JSONObject _result = new JSONObject(result);
                if (_result.has("type")) {

                    int type = _result.getInt("type");
                    if (type == 22 || type == 24 || type == 25 || type == 103) {
                        return true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void notifyMyWebActions(String actions) {
        if (messageManager == null) {
            messageManager = SocketMessageManager.getManager(this);
            messageManager.registerMessageReceiver();
        }

        if (meetingConfig.getType() != MeetingType.MEETING) {
            if (isSyncing) {
                if (!TextUtils.isEmpty(actions)) {
                    Log.e("syncing---", SoundtrackRecordManager.getManager(this).getCurrentTime() + "");
                    try {
                        JSONObject jsonObject = new JSONObject(actions);
                        jsonObject.put("time", SoundtrackRecordManager.getManager(this).getCurrentTime());
                        actions = jsonObject.toString();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            messageManager.sendMessage_MyActionFrame(actions, meetingConfig);
        } else {
            Log.e("notifyMyWebActions", "role:" + meetingConfig.getRole());
            if (!AppConfig.UserID.equals(meetingConfig.getPresenterId())) {
                return;
            }
            messageManager.sendMessage_MyActionFrame(actions, meetingConfig);
        }
    }

    private void notifyMyNoteWebActions(String actions, Note note) {
        if (meetingConfig.getType() != MeetingType.MEETING) {
            if (messageManager == null) {
                messageManager = SocketMessageManager.getManager(this);
                messageManager.registerMessageReceiver();
            }
            messageManager.sendMessage_MyNoteActionFrame(actions, meetingConfig, note);

        } else {
            Log.e("notifyMyWebActions", "role:" + meetingConfig.getRole());
            if (!AppConfig.UserID.equals(meetingConfig.getPresenterId())) {
                return;
            }
            if (messageManager == null) {
                messageManager = SocketMessageManager.getManager(this);
                messageManager.registerMessageReceiver();
            }
            messageManager.sendMessage_MyNoteActionFrame(actions, meetingConfig, note);
        }
    }

    private void notifyDocumentChanged() {
        if (meetingConfig.getType() != MeetingType.MEETING) {
            if (messageManager != null) {
                messageManager.sendMessage_DocumentShowed(meetingConfig);
            }
        } else {
            if (!TextUtils.isEmpty(meetingConfig.getPresenterSessionId())) {
                if (AppConfig.UserID.equals(meetingConfig.getPresenterId())) {
                    if (meetingConfig.isInRealMeeting()) {
                        if (messageManager != null) {
                            messageManager.sendMessage_DocumentShowed(meetingConfig);
                        }

                    }
                }
            }
        }

    }

    private void notifyViewNote(Note note) {
        if (meetingConfig.getType() != MeetingType.MEETING) {
            if (messageManager != null) {
                messageManager.sendMessage_ViewNote(meetingConfig, note);
            }
        } else {
            if (!TextUtils.isEmpty(meetingConfig.getPresenterSessionId())) {
                if (AppConfig.UserToken.equals(meetingConfig.getPresenterSessionId())) {
                    if (meetingConfig.isInRealMeeting()) {
                        if (messageManager != null) {
                            messageManager.sendMessage_ViewNote(meetingConfig, note);
                        }

                    }
                }
            }
        }

    }

    @org.xwalk.core.JavascriptInterface
    public synchronized void autoChangeFileFunction(int diff) {
        Log.e("JavascriptInterface", "autoChangeFileFunction,diff:  " + diff);
        if (documents.size() <= 1) {
            return;
        }
        if (diff == 1) {
            _changeToNextDocument();
        } else if (diff == -1) {
            _changeToPreDocument();
        }
    }

    private void _changeToNextDocument() {
        MeetingDocument document = meetingConfig.getDocument();
        int index = documents.indexOf(document);
        Log.e("check_file_index", "index:" + index + ",documents size:" + documents.size());
        if (index + 1 < documents.size()) {
            document = documents.get(index + 1);
            changeDocument(document, 1);
        }
    }

    private void _changeToPreDocument() {
        MeetingDocument document = meetingConfig.getDocument();
        int index = documents.indexOf(document);
        if (index - 1 < documents.size() && (index - 1 >= 0)) {
            document = documents.get(index - 1);
            changeDocument(document, document.getPageCount());
        }
    }

    // 播放视频
    @org.xwalk.core.JavascriptInterface
    public void videoPlayFunction(final int vid) {
        Log.e("JavascriptInterface", "videoPlayFunction,vid:  " + vid);
        if (meetingConfig.getType() == MeetingType.MEETING) {
            if (!meetingConfig.getPresenterId().equals(AppConfig.UserID)) {
                Toast.makeText(this, "不是presenter，不能操作", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        DocVedioManager.getInstance(this).play(this, vedioLayout, meetingConfig, vid);

    }

    private FavoriteVideoPopup selectVideoDialog;

    //打开
    @org.xwalk.core.JavascriptInterface
    public void videoSelectFunction(String video) {
        Log.e("JavascriptInterface", "videoSelectFunction,id:  " + video);
        if (selectVideoDialog != null) {
            selectVideoDialog.dismiss();
            selectVideoDialog = null;
        }
        selectVideoDialog = new FavoriteVideoPopup(this);
    }

    // 录制
    @org.xwalk.core.JavascriptInterface
    public void audioSyncFunction(final int id, final int isRecording) {
        Log.e("JavascriptInterface", "audioSyncFunction,id:  " + id + ",isRecording:" + isRecording);

    }

    @org.xwalk.core.JavascriptInterface
    public synchronized void callAppFunction(final String action, final String data) {
        Log.e("JavascriptInterface", "callAppFunction,action:  " + action + ",data:" + data);
        if (TextUtils.isEmpty(action) || TextUtils.isEmpty(data)) {
            return;
        }

        Observable.just(data).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                PageActionsAndNotesMgr.handleNoteActions(NoteViewActivity.this, action, new JSONObject(data), meetingConfig);
            }
        }).subscribe();

    }

    // ---- Bottom Menu
    ExitDialog exitDialog;

    @Override
    public void menuClosedClicked() {
        handleExit(false);
    }

    private void handleExit(boolean isEnd) {
        if (exitDialog != null) {
            if (exitDialog.isShowing()) {
                exitDialog.dismiss();
            }
            exitDialog = null;
        }
        exitDialog = new ExitDialog(this, meetingConfig);
        exitDialog.setEndMeeting(isEnd);
        exitDialog.setDialogClickListener(new ExitDialog.ExitDialogClickListener() {
            @Override
            public void onSaveAndLeaveClick() {

                if (exitDialog.isEndMeeting() && meetingConfig.isInRealMeeting()) {
                    messageManager.sendMessage_EndMeeting(meetingConfig);
                }
                if (messageManager != null) {
                    messageManager.sendMessage_UpdateAttchment(meetingConfig);
                    messageManager.sendMessage_LeaveMeeting(meetingConfig);
                }
                PageActionsAndNotesMgr.requestActionsSaved(meetingConfig);
                finish();
            }

            @Override
            public void onLeaveClick() {
                if (exitDialog.isEndMeeting() && meetingConfig.isInRealMeeting()) {
                    messageManager.sendMessage_EndMeeting(meetingConfig);
                }
                finish();
            }
        });
        exitDialog.show();
    }

    @Override
    public void menuFileClicked() {
        if (bottomFilePop == null) {
            bottomFilePop = new PopBottomFile(this);
        }
        if (documents != null && documents.size() > 0) {
            bottomFilePop.setDocuments(documents, meetingConfig.getDocument().getItemID(), this);
        }
        if (menuManager != null) {
            menuManager.setMenuIcon(menu);
            menuManager.totalHideMenu();
        }
        menu.setImageResource(R.drawable.shape_transparent);
        menu.setEnabled(false);
        bottomFilePop.show(web, this);
    }

    @Override
    public void menuStartMeetingClicked() {


    }

    @Override
    public void menuShareDocClicked() {
        shareDocument();
    }

    @Override
    public void menuNoteClicked() {
        showNotesDialog();
    }

    @Override
    public void menuScanTvClicked() {
        handleScanTv();
    }

    @Override
    public void menuMeetingMembersClicked() {

    }

    @Override
    public void menuSyncClicked() {

    }

    @Override
    public void menuPlayMeetingRecordClicked() {

    }

    @Override
    public void menuChatClicked() {

    }


    //-----
    @Override
    public void addFromTeam() {

        openTeamDocument();

    }

    @Override
    public void addFromCamera() {
        String[] permissions = new String[]{
                Manifest.permission.CAMERA};
        startRequestPermission(permissions, 322);
    }

    @Override
    public void addFromPictures() {
        String[] permissions = new String[]{
                Manifest.permission.CAMERA};
        startRequestPermission(permissions, 323);
    }

    @Override
    public void addBlankFile() {
        reqeustNewBlankPage();
    }

    AddFileFromDocumentDialog addFileFromDocumentDialog;

    private void openTeamDocument() {

        if (addFileFromDocumentDialog != null) {
            addFileFromDocumentDialog.dismiss();
        }
        addFileFromDocumentDialog = new AddFileFromDocumentDialog(this);
        addFileFromDocumentDialog.setOnSpaceSelectedListener(this);
        addFileFromDocumentDialog.show();

    }

    @Override
    public void onDocSelected(String docId) {
        TeamSpaceInterfaceTools.getinstance().uploadFromSpace(AppConfig.URL_PUBLIC + "EventAttachment/UploadFromSpace?lessonID=" + meetingConfig.getLessionId() + "&itemIDs=" + docId, TeamSpaceInterfaceTools.UPLOADFROMSPACE, new TeamSpaceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                Log.e("add_success", "response:" + object);
                try {
                    JSONObject data = new JSONObject(object.toString());
                    if (data.getInt("RetCode") == 0) {
                        JSONObject document = data.getJSONArray("RetData").getJSONObject(0);
                        if (document != null && document.has("ItemID")) {
                            addDocSucc(document.getInt("ItemID"));
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    //------

    private AddFileFromFavoriteDialog addFileFromFavoriteDialog;

    @Override
    public void addFromFavorite() {
        if (addFileFromFavoriteDialog != null) {
            if (addFileFromFavoriteDialog.isShowing()) {
                addFileFromFavoriteDialog.dismiss();
            }
            addFileFromFavoriteDialog = null;
        }
        addFileFromFavoriteDialog = new AddFileFromFavoriteDialog(this);
        addFileFromFavoriteDialog.setOnFavoriteDocSelectedListener(this);
        addFileFromFavoriteDialog.show();
    }


    @Override
    public void onFavoriteDocSelected(String docId) {
        TeamSpaceInterfaceTools.getinstance().uploadFromSpace(AppConfig.URL_PUBLIC + "EventAttachment/UploadFromFavorite?lessonID=" +
                        meetingConfig.getLessionId() + "&itemIDs=" + docId, TeamSpaceInterfaceTools.UPLOADFROMSPACE,
                new TeamSpaceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        Log.e("add_success", "response:" + object);
                        try {
                            JSONObject data = new JSONObject(object.toString());
                            if (data.getInt("RetCode") == 0) {
                                JSONObject document = data.getJSONArray("RetData").getJSONObject(0);
                                if (document != null && document.has("ItemID")) {
                                    addDocSucc(document.getInt("ItemID"));
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    //-----
    @Override
    public void onDocumentClick(MeetingDocument document) {
        changeDocument(document, 1);
    }


    private void initViews() {
        closeVedioImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }


    ShareDocumentDialog shareDocumentDialog;

    private void shareDocument() {
        if (shareDocumentDialog != null) {
            shareDocumentDialog.dismiss();
            shareDocumentDialog = null;
        }
        shareDocumentDialog = new ShareDocumentDialog();
        shareDocumentDialog.getPopwindow(this, meetingConfig.getDocument());
        shareDocumentDialog.show();
    }

    UserNotesDialog notesDialog;

    private void showNotesDialog() {
        Log.e("showNotesDialog", "meeting_config:" + meetingConfig);
        if (notesDialog != null) {
            if (notesDialog.isShowing()) {
                notesDialog.dismiss();
                notesDialog = null;
            }
        }
        notesDialog = new UserNotesDialog(this);
        notesDialog.show(AppConfig.UserID, meetingConfig);
    }


    //--------
    private boolean isCameraCanUse() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)
                && !getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            return false;
        } else {
            return true;
        }
    }

    private File cameraFile;

    private void openCameraForAddDoc() {
        if (!isCameraCanUse()) {
            Toast.makeText(getApplicationContext(), "相机不可用", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String mFilePath = FileUtils.getBaseDir();
        // 文件名
        String fileName = "Kloud_" + DateFormat.format("yyyyMMdd_hhmmss",
                Calendar.getInstance(Locale.CHINA))
                + ".jpg";
        cameraFile = new File(mFilePath, fileName);
        //Android7.0文件保存方式改变了
        if (Build.VERSION.SDK_INT < 24) {
            Uri uri = Uri.fromFile(cameraFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        } else {
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, cameraFile.getAbsolutePath());
            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        startActivityForResult(intent, REQUEST_CAMEIA_ADD_DOC);
    }

    private static final int REQUEST_CAMEIA_ADD_DOC = 1;
    private static final int REQUEST_PICTURE_ADD_DOC = 2;
    private static final int REQUEST_SCAN = 3;
    private static final int REQUEST_CODE_ADD_NOTE = 100;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CAMEIA_ADD_DOC:
                    if (cameraFile != null && cameraFile.exists()) {
                        Log.e("onActivityResult", "camera_file:" + cameraFile);
                        uploadFileWhenAddDoc(cameraFile);
                    }
                    break;
                case REQUEST_PICTURE_ADD_DOC:
                    if (data.getData() != null) {
                        File picture = new File(FileUtils.getPath(this, data.getData()));
                        if (picture != null && picture.exists()) {
                            uploadFileWhenAddDoc(picture);
                        }
                    }
                    break;
                case REQUEST_SCAN:
                    if (meetingConfig.getType() == MeetingType.MEETING) {
                        MeetingKit.getInstance().restoreLocalVedeo();
                    }
                    String url = "https://wss.peertime.cn/MeetingServer/tv/change_bind_tv_status?status=1";
                    ServiceInterfaceTools.getinstance().changeBindTvStatus(url, ServiceInterfaceTools.CHANGEBINDTVSTATUS,
                            true, new ServiceInterfaceListener() {
                                @Override
                                public void getServiceReturnData(Object object) {

                                }
                            });
                    break;
                case REQUEST_CODE_ADD_NOTE:
                    String json = data.getStringExtra("OPEN_NOTE_BEAN_JSON");
                    BookNote note = new Gson().fromJson(json, BookNote.class);
                    if (web != null) {
                        uploadNote(note);
                    }
                    drawTempNote();
                    break;
            }
        }
    }

    LocalNoteManager noteManager;

    private void uploadNote(BookNote note) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("ID", note.documentId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("AfterEditBookNote", "jsonObject:" + jsonObject);
        web.load("javascript:AfterEditBookNote(" + jsonObject + ")", null);
        noteManager = LocalNoteManager.getMgr(this);
        String exportPath = Environment.getExternalStorageDirectory().getPath() + File.separator + "Kloudsyn" + File.separator + "Kloud_" + note.documentId + ".pdf";
        Log.e("upload_note", "link_property:" + meetingConfig.getCurrentLinkProperty());
        noteManager.exportPdfAndUpload(this, note, exportPath, meetingConfig.getDocument().getAttachmentID() + "", meetingConfig.getPageNumber() + "", meetingConfig.getSpaceId(), "0", meetingConfig.getCurrentLinkProperty().toString());
    }

    private void uploadFileWhenAddDoc(File file) {

        AddDocumentTool.addDocumentInDoc(this, file, meetingConfig.getLessionId() + "", new DocumentUploadTool.DocUploadDetailLinstener() {
            @Override
            public void uploadStart() {
//                                Log.e("addDocumentInDoc","uploadStart");
                if (bottomFilePop != null && bottomFilePop.isShowing()) {
                    final MeetingDocument tempDoc = new MeetingDocument();
                    tempDoc.setProgress(0);
                    tempDoc.setTemp(true);
                    tempDoc.setTempDocPrompt("loading");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bottomFilePop.addTempDoc(tempDoc);
                        }
                    });

                }
            }

            @Override
            public void uploadFile(final int progress) {
                Log.e("addDocumentInDoc", "uploadFile:" + progress);
                if (bottomFilePop != null && bottomFilePop.isShowing()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bottomFilePop.refreshTempDoc("uploading", progress);

                        }
                    });
                }

            }

            @Override
            public void convertFile(final int progress) {
                Log.e("addDocumentInDoc", "convertFile:" + progress);
                if (bottomFilePop != null && bottomFilePop.isShowing()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bottomFilePop.refreshTempDoc("converting", progress);
                        }
                    });
                }

            }

            @Override
            public void uploadFinished(Object result) {
                Log.e("addDocumentInDoc", "uploadFinished:" + result);
                try {
                    JSONObject data = new JSONObject(result.toString());
                    if (data.getInt("RetCode") == 0) {
                        JSONObject document = data.getJSONObject("RetData");
                        if (document != null && document.has("ItemID")) {
                            addDocSucc(document.getInt("ItemID"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void uploadError(String message) {
                Log.e("addDocumentInDoc", "uploadError");
                if (bottomFilePop != null && bottomFilePop.isShowing()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new CenterToast.Builder(NoteViewActivity.this).
                                    setSuccess(false).setMessage("operate failed").create().show();
                            bottomFilePop.removeTempDoc();
                        }
                    });

                }

            }
        });
    }

    private void reqeustNewBlankPage() {
        Observable.just(meetingConfig).observeOn(Schedulers.io()).doOnNext(new Consumer<MeetingConfig>() {
            @Override
            public void accept(MeetingConfig meetingConfig) throws Exception {
                final JSONObject data = ConnectService.submitDataByJson(AppConfig.URL_PUBLIC + "EventAttachment/AddBlankPage?lessonID=" +
                        meetingConfig.getLessionId(), null);
//                Log.e("blank_page","result:" + jsonObject);
                if (data.getInt("RetCode") == 0) {
                    JSONObject document = data.getJSONObject("RetData");
                    if (document != null && document.has("ItemID")) {
                        addDocSucc(document.getInt("ItemID"));
                    }
                }
            }
        }).subscribe();
    }

    private void addDocSucc(int newItemid) {
        DocumentModel.asyncGetDocumentsInDocAndRefreshFileList(meetingConfig, newItemid, 1);
        if (bottomFilePop != null && bottomFilePop.isShowing()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new CenterToast.Builder(NoteViewActivity.this).
                            setSuccess(true).setMessage("operate success").create().show();
                }
            });

        }
    }


    private DevicesListDialog devicesListDialog;

    private void handleScanTv() {
        ServiceInterfaceTools.getinstance().getBindTvs().enqueue(new Callback<DevicesResponse>() {
            @Override
            public void onResponse(Call<DevicesResponse> call, Response<DevicesResponse> response) {
                if (response != null && response.isSuccessful() && response.body() != null) {
                    if (response.body().getData() != null) {
                        List<TvDevice> devices = response.body().getData().getDeviceList();
                        boolean enable = response.body().getData().isEnableBind();
                        if (devices != null && devices.size() > 0) {
                            devicesListDialog = new DevicesListDialog();
                            devicesListDialog.getPopwindow(NoteViewActivity.this);
                            devicesListDialog.setWebCamPopupListener(new DevicesListDialog.WebCamPopupListener() {
                                @Override
                                public void scanTv() {
                                    gotoScanTv();
                                }

                                @Override
                                public void changeBindStatus(boolean isCheck) {
                                    String url = "https://wss.peertime.cn/MeetingServer/tv/change_bind_tv_status?status=" + (isCheck ? 1 : 0);
                                    ServiceInterfaceTools.getinstance().changeBindTvStatus(url, ServiceInterfaceTools.CHANGEBINDTVSTATUS,
                                            isCheck, new ServiceInterfaceListener() {
                                                @Override
                                                public void getServiceReturnData(Object object) {

                                                }
                                            });
                                }

                                @Override
                                public void openTransfer(TvDevice tvDevice) {
//                                    tvVoice(1, tvDevice);
                                }

                                @Override
                                public void closeTransfer(TvDevice tvDevice) {
//                                    tvVoice(0, tvDevice);
                                }

                                @Override
                                public void logout(TvDevice tvDevice) {
//                                    tvlogout(0, tvDevice);
                                }
                            });
                            devicesListDialog.show(devices, enable);
                        } else {
                            gotoScanTv();
                        }
                    } else {
                        gotoScanTv();
                    }
                } else {
                    gotoScanTv();
                }
            }

            @Override
            public void onFailure(Call<DevicesResponse> call, Throwable t) {
                gotoScanTv();
            }
        });


    }

    private void gotoScanTv() {

        if (!isCameraCanUse()) {
            Toast.makeText(getApplicationContext(), "相机不可用", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e("gotoScanTv", "meeting_type:" + meetingConfig.getType());
        if (meetingConfig.getType() == MeetingType.MEETING) {
            MeetingKit.getInstance().templeDisableLocalVideo();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(NoteViewActivity.this, MipcaActivityCapture.class);
                    intent.putExtra("isHorization", true);
                    intent.putExtra("type", 0);
                    startActivityForResult(intent, REQUEST_SCAN);
                }
            }, 500);

        } else {
            Intent intent = new Intent(NoteViewActivity.this, MipcaActivityCapture.class);
            intent.putExtra("isHorization", true);
            intent.putExtra("type", 0);
            startActivityForResult(intent, REQUEST_SCAN);
        }
//        String url = AppConfig.wssServer + "/tv/change_bind_tv_status?status=1";
//        ServiceInterfaceTools.getinstance().changeBindTvStatus(url, ServiceInterfaceTools.CHANGEBINDTVSTATUS,
//                true, new ServiceInterfaceListener() {
//                    @Override
//                    public void getServiceReturnData(Object object) {
//
//                    }
//                });

    }

    SetPresenterDialog setPresenterDialog;


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setPresenter(EventSetPresenter setPresenter) {
//        messageManager.sendMessage_MakePresenter(meetingConfig, setPresenter.getMeetingMember());
        Observable.just(setPresenter).observeOn(Schedulers.io()).doOnNext(new Consumer<EventSetPresenter>() {
            @Override
            public void accept(EventSetPresenter eventSetPresenter) throws Exception {
                JSONObject result = ServiceInterfaceTools.getinstance().syncMakePresenter(eventSetPresenter.getMeetingMember().getUserId() + "");
                if (result.has("code")) {
                    if (result.getInt("code") == 0) {
                        MeetingKit.getInstance().requestMeetingMembers(meetingConfig, false);
                    }
                }
            }
        }).subscribe();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void playSoundtrack(EventPlaySoundtrack soundtrack) {
        Log.e("check_play", "playSoundtrack");
        showSoundtrackPlayDialog(soundtrack.getSoundtrackDetail());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void closeViewNote(EventCloseNoteView closeNoteView) {
        currentNoteId = 0;
        noteWeb.setVisibility(View.GONE);
        noteWeb.load("javascript:ClearPath()", null);
        newNoteDatas.clear();
        menu.setVisibility(View.VISIBLE);
        notifyDocumentChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void openNoteByEvent(EventOpenNote openNote) {
        openNote(openNote.getNoteId());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void presenterChanged(EventPresnterChanged presnterChanged) {
        handleWebUISetting();
    }


//    private CreateSyncDialog createSyncDialog;
//
//    private void showCreateSyncDialog() {
//        if (createSyncDialog != null) {
//            if (createSyncDialog.isShowing()) {
//                createSyncDialog.dismiss();
//                createSyncDialog = null;
//            }
//        }
//        createSyncDialog = new CreateSyncDialog(this);
//        createSyncDialog.show(meetingConfig.getDocument().getAttachmentID() + "");
//    }


    private void startRequestPermission(String[] permissions, int requestcode) {
        ActivityCompat.requestPermissions(this, permissions, requestcode);
    }

    // 用户权限 申请 的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                    boolean i = shouldShowRequestPermissionRationale(permissions[0]);
                    boolean j = shouldShowRequestPermissionRationale(permissions[1]);
                    if (!i || !j) {
                        // 提示用户去应用设置界面手动开启权限
//                       showDialogTipUserGoToAppSettting();
                    } else {
                        Toast.makeText(this, "必要权限未开启", Toast.LENGTH_SHORT).show();
                    }
                } else {

                }
            }
        } else if (requestCode == 322) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "必要权限未开启", Toast.LENGTH_SHORT).show();
                } else {
                    openCameraForAddDoc();
                }
            }
        } else if (requestCode == 323) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "必要权限未开启", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, REQUEST_PICTURE_ADD_DOC);
                }
            }
        }
    }

    private boolean isSyncing = false;


    private void getJspPagenumber() {
        web.evaluateJavascript("javascript:GetCurrentPageNumber()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
                int id = 1;
                if (!TextUtils.isEmpty(s)) {
                    Log.e("syncing---", s + "");
                    id = Integer.parseInt(s);
                }
                JSONObject js = new JSONObject();
                try {
                    js.put("type", 2);
                    js.put("page", id);
                    js.put("time", 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                messageManager.sendMessage_MyActionFrame(js.toString(), meetingConfig);
            }
        });
    }


    private int currentPosition = -1;
    private FavoriteVideoPopup favoritePopup;

    private void openNote(String noteId) {
        BookNote bookNote = null;
        if (TextUtils.isEmpty(noteId)) {
            bookNote = new BookNote().setTitle("new note").setJumpBackToNote(false);
        } else {
            bookNote = new BookNote().setDocumentId(noteId).setJumpBackToNote(false);
            if (!QueryLocalNoteTool.noteIsExist(this, noteId)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(NoteViewActivity.this, "笔记在本地设备不存在", Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
        }

        Intent intent = new Intent();
        intent.putExtra("OPEN_NOTE_BEAN", new Gson().toJson(bookNote));
        ComponentName comp = new ComponentName("com.onyx.android.note", "com.onyx.android.note.note.ui.ScribbleActivity");
        intent.setComponent(comp);
        startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);
    }

    private SoundtrackPlayDialog soundtrackPlayDialog;

    private void showSoundtrackPlayDialog(SoundtrackDetail soundtrackDetail) {
        if (soundtrackPlayDialog != null) {
            if (soundtrackPlayDialog.isShowing()) {
                soundtrackPlayDialog.dismiss();
                soundtrackPlayDialog = null;
            }
        }
        soundtrackPlayDialog = new SoundtrackPlayDialog(this, soundtrackDetail, meetingConfig);
        soundtrackPlayDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handleExit(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            handleExit(false);
        }
        return true;

    }

    // ----handle_message

    private void handleMessageSendMessage(JSONObject data) throws JSONException {
        if (!data.has("actionType")) {
            return;
        }

        switch (data.getInt("actionType")) {
            case 8:
                if (data.has("docType")) {
                    int docType = data.getInt("docType");
                    switch (docType) {
                        case 1:
                            // 切换笔记
                            followShowNote(data.getInt("itemId"));
                            break;
                        case 2:
                            // 切换文档
                        default:
                            if (noteLayout.getVisibility() == View.VISIBLE) {
                                noteWeb.setVisibility(View.GONE);
                                noteLayout.setVisibility(View.GONE);
                            }
                            changeDocument(data.getInt("itemId"), Integer.parseInt(data.getString("pageNumber")));
                            break;
                    }

                } else {
                    if (noteLayout.getVisibility() == View.VISIBLE) {
                        noteWeb.load("javascript:ClearPath()", null);
                        noteWeb.setVisibility(View.GONE);
                        noteLayout.setVisibility(View.GONE);
                    }
                    changeDocument(data.getInt("itemId"), Integer.parseInt(data.getString("pageNumber")));
                }
                break;


            case 23:
                //播放音想
                if (data.has("stat")) {
                    final int stat = data.getInt("stat");
                    final String soundtrackID = data.getString("soundtrackId");
                    int audioTime = 0;
                    if (stat == 4) {
                        audioTime = data.getInt("time");
                    }
                    Log.e("mediaplayer-----", stat + ":   " + soundtrackID);
                    if (stat == 1) {  //开始播放
                        int vid2 = 0;
                        if (!TextUtils.isEmpty(soundtrackID)) {
                            vid2 = Integer.parseInt(soundtrackID);
                        }
                        SoundTrack soundTrack = new SoundTrack();
                        soundTrack.setSoundtrackID(vid2);
                        requestSyncDetailAndPlay(soundTrack);
                    } else if (stat == 0) { //停止播放
                        if (soundtrackPlayDialog != null) {
                            soundtrackPlayDialog.followClose();
                        }
                    } else if (stat == 2) {  //暂停播放
                        if (soundtrackPlayDialog != null) {
                            soundtrackPlayDialog.followPause();
                        }
                    } else if (stat == 3) { // 继续播放
                        if (soundtrackPlayDialog != null) {
                            soundtrackPlayDialog.followRestart();
                        }
                    } else if (stat == 4) {  // 追上进度
//
                    } else if (stat == 5) {  // 拖动进度条
//                    seekToTime(audioTime);
                        if (soundtrackPlayDialog != null) {
                            soundtrackPlayDialog.followSeekTo(audioTime);
                        }
                    }
                }


                break;
        }
    }

    private void requestSyncDetailAndPlay(final SoundTrack soundTrack) {
        Observable.just(soundTrack).observeOn(Schedulers.io()).map(new Function<SoundTrack, SoundtrackDetailData>() {
            @Override
            public SoundtrackDetailData apply(SoundTrack soundtrack) throws Exception {
                SoundtrackDetailData soundtrackDetailData = new SoundtrackDetailData();
                JSONObject response = ServiceInterfaceTools.getinstance().syncGetSoundtrackDetail(soundTrack);
                if (response.has("RetCode")) {
                    if (response.getInt("RetCode") == 0) {
                        SoundtrackDetail soundtrackDetail = new Gson().fromJson(response.getJSONObject("RetData").toString(), SoundtrackDetail.class);
                        soundtrackDetailData.setSoundtrackDetail(soundtrackDetail);
                    }
                }
                return soundtrackDetailData;
            }

        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<SoundtrackDetailData>() {
            @Override
            public void accept(SoundtrackDetailData soundtrackDetailData) throws Exception {
                if (soundtrackDetailData.getSoundtrackDetail() != null) {
                    EventPlaySoundtrack soundtrack = new EventPlaySoundtrack();
                    soundtrack.setSoundtrackDetail(soundtrackDetailData.getSoundtrackDetail());
                    playSoundtrack(soundtrack);
                }
            }
        }).subscribe();
    }


    private void handleMessageLeaveMeeting(JSONObject data) {
        Log.e("handle_leave_meeting", "data:" + data);
        if (data == null) {
            return;
        }

        if (data.has("retCode")) {
            try {
                if (data.getInt("retCode") == 0) {
                    JSONArray usersList = data.getJSONObject("retData").getJSONArray("usersList");
                    if (usersList != null) {
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        MeetingKit.getInstance().requestMeetingMembers(meetingConfig, false);

    }

    private void handleMessageAttchmentUploadedAndShow(JSONObject data) {
        Log.e("handle_attchment_upload", "data;" + data);
        String newDocumentId = "";
        try {
            newDocumentId = data.getString("itemId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(newDocumentId)) {
            return;
        }
        final String _id = newDocumentId;
        Observable.just(meetingConfig).observeOn(Schedulers.io()).doOnNext(new Consumer<MeetingConfig>() {
            @Override
            public void accept(MeetingConfig meetingConfig) throws Exception {
                DocumentModel.asyncGetDocumentsInDocAndRefreshFileList(meetingConfig, Integer.parseInt(_id), 1);
            }
        }).subscribe();
    }

    public void handleMessageJoinMeeting(JSONObject data) {
        if (data == null) {
            return;
        }

        if (data.has("retCode")) {
            try {
                if (data.getInt("retCode") == 0) {
                    // 成功收到JOIN_MEETING的返回
                    JSONObject dataJson = data.getJSONObject("retData");
//                    if (!dataJson.has("CurrentDocumentPage")) {
//                        Toast.makeText(this, "join meeting failed", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
                    JoinMeetingMessage joinMeetingMessage = gson.fromJson(dataJson.toString(), JoinMeetingMessage.class);
                    Log.e("JOIN_MEETING", "join_meeting_message:" + joinMeetingMessage);
                    String[] datas = joinMeetingMessage.getCurrentDocumentPage().split("-");
                    meetingConfig.setFileId(Integer.parseInt(datas[0]));
                    float page = Float.parseFloat(datas[1]);
                    meetingConfig.setPageNumber((int) page);
                    meetingConfig.setType(joinMeetingMessage.getType());
                    if (dataJson.has("currentMode")) {
                        meetingConfig.setMode(joinMeetingMessage.getCurrentMode());
                    }
                    if (dataJson.has("currentMaxVideoUserId")) {
                        meetingConfig.setCurrentMaxVideoUserId(joinMeetingMessage.getCurrentMaxVideoUserId());
                    }
                    if (documents == null || documents.size() <= 0) {
                        if (!TextUtils.isEmpty(localFileId) && localFileId.contains(".")) {
                            return;
                        }
                        requestDocumentsAndShowPage();
                    } else {
//                        requestDocuments();
                    }
                    if (joinMeetingMessage.getNoteId() > 0 && !TextUtils.isEmpty(joinMeetingMessage.getNotePageId())) {
                        followShowNote((int) joinMeetingMessage.getNoteId());
                    }

                    if (meetingConfig.getType() == MeetingType.DOC) {

                    }
//                    Log.e("MeetingConfig","MeetingConfig:" + meetingConfig);
                }
            } catch (JSONException e) {
                Log.e("JOIN_MEETING", "JOIN_MEETING,JSONException:" + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    public class NoteJavascriptInterface {
        @org.xwalk.core.JavascriptInterface
        public void afterChangePageFunction(final int pageNum, int type) {
//            Log.e("JavascriptInterface", "note_afterChangePageFunction,pageNum:  " + pageNum + ", type:" + type);
            NoteViewManager.getInstance().getNotePageActionsToShow(meetingConfig);
        }

        @org.xwalk.core.JavascriptInterface
        public void reflect(String result) {
            Log.e("JavascriptInterface", "reflect,result:  " + result);
            Note note = NoteViewManager.getInstance().getNote();
            if (note != null) {
                notifyMyNoteWebActions(result, note);
            }
        }

        @org.xwalk.core.JavascriptInterface
        public synchronized void callAppFunction(final String action, final String data) {
            Log.e("JavascriptInterface", "callAppFunction,action:  " + action + ",data:" + data);
            if (TextUtils.isEmpty(action) || TextUtils.isEmpty(data)) {
                return;
            }
        }
    }

    private PowerManager.WakeLock wakeLock;

    private void keepScreenWake() {
        wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK, "TEST");
        wakeLock.acquire();
    }

    private void notifyAgoraVedioScreenStatus(int viewMode, String userId) {
        if (meetingConfig.getType() != MeetingType.MEETING) {
            return;
        }
        if (!AppConfig.UserID.equals(meetingConfig.getPresenterId())) {
            return;
        }
        SocketMessageManager.getManager(this).sendMessage_ViewModeStatus(viewMode, userId);

        meetingConfig.setMode(viewMode);
        if (viewMode == 2) {
            meetingConfig.setCurrentMaxVideoUserId(userId);
        }
        MeetingKit.getInstance().setEncoderConfigurationBaseMode();
//        SocketMessageManager.getManager(this).sendMessage_MyNoteActionFrame(actions, meetingConfig, note);
    }

    JSONObject lastjsonObject;

    private void handleBluetoothNote(final String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        Observable.just(url).observeOn(Schedulers.io()).map(new Function<String, String>() {
            @Override
            public String apply(String url) throws Exception {
                String newUrl = "";
                URL _url = new URL(url);
                Log.e("check_url_path", _url.getPath());
                String path = _url.getPath();
                if (!TextUtils.isEmpty(path)) {
                    if (path.startsWith("/")) {
                        path = path.substring(1);
                    }
                    int index = path.lastIndexOf("/");
                    if (index >= 0 && index < path.length()) {
                        String centerPart = path.substring(0, index);
                        String fileName = path.substring(index + 1, path.length());
                        Log.e("check_transform_url", "centerPart:" + centerPart + ",fileName:" + fileName);
                        if (!TextUtils.isEmpty(centerPart)) {
                            JSONObject queryDocumentResult = DocumentModel.syncQueryDocumentInDoc(AppConfig.URL_LIVEDOC + "queryDocument",
                                    centerPart);
                            if (queryDocumentResult != null) {
                                Uploadao uploadao = parseQueryResponse(queryDocumentResult.toString());
                                String part = "";
                                if (uploadao != null) {
                                    if (1 == uploadao.getServiceProviderId()) {
                                        part = "https://s3." + uploadao.getRegionName() + ".amazonaws.com/" + uploadao.getBucketName() + "/" + centerPart
                                                + "/" + fileName;
                                    } else if (2 == uploadao.getServiceProviderId()) {
                                        part = "https://" + uploadao.getBucketName() + "." + uploadao.getRegionName() + "." + "aliyuncs.com" + "/" + centerPart + "/" + fileName;
                                    }
                                    url = part;
                                    Log.e("check_transform_url", "url:" + url);
                                }

                            }
                        }
                    }
                }

                int checkIndex = url.lastIndexOf("/");
                if (checkIndex > 0 && checkIndex < url.length() - 2) {
                    newUrl = url.substring(0, checkIndex + 1) + "book_page_data.json";
                }
                return newUrl;
            }
        }).map(new Function<String, JSONObject>() {
            @Override
            public JSONObject apply(String url) throws Exception {
                JSONObject jsonObject = new JSONObject();
                if (!TextUtils.isEmpty(url)) {
                    Log.e("check_url", "url:" + url);
                    jsonObject = ServiceInterfaceTools.getinstance().syncGetNotePageJson(url);
                    try {
                        lastjsonObject = jsonObject.getJSONObject("PaintData");
                    } catch (Exception e) {

                    }
                }
                return jsonObject;
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject jsonObject) throws Exception {
                if (web == null) {
                    return;
                }
                String key = "ShowDotPanData";
                JSONObject _data = new JSONObject();
                _data.put("LinesData", jsonObject);
                _data.put("ShowInCenter", false);
                _data.put("TriggerEvent", false);
                Log.e("ShowDotPanData", "ShowDotPanData");
                web.load("javascript:FromApp('" + key + "'," + _data + ")", null);
                RecordNoteActionManager.getManager(NoteViewActivity.this).sendDisplayHomePageActions(currentNoteId, lastjsonObject);
            }
        }).doOnNext(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject jsonObject) throws Exception {
                if (!newNoteDatas.isEmpty()) {
                    Observable.fromIterable(newNoteDatas).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<TempNoteData>() {
                        @Override
                        public void accept(TempNoteData tempNoteData) throws Exception {
                            Log.e("draw_new_note", "temp_note_note");
                            if (tempNoteData.getNoteId() == currentNoteId) {
                                String key = "ShowDotPanData";
                                JSONObject _data = new JSONObject();
                                _data.put("LinesData", tempNoteData.getData());
                                _data.put("ShowInCenter", true);
                                _data.put("TriggerEvent", true);
                                web.load("javascript:FromApp('" + key + "'," + _data + ")", null);
                            }
                            newNoteDatas.remove(tempNoteData);
                        }
                    }).subscribe();
                }
            }
        }).observeOn(Schedulers.io()).doOnNext(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject jsonObject) throws Exception {
                JSONObject result = ServiceInterfaceTools.getinstance().syncGetNoteP1Item(currentNoteId);
                if (result.has("code")) {
                    if (result.getInt("code") == 0) {
                        JSONArray dataArray = result.getJSONArray("data");
                        Observable.just(dataArray).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<JSONArray>() {
                            @Override
                            public void accept(JSONArray _jsonArray) throws Exception {
                                for (int i = 0; i < _jsonArray.length(); ++i) {
                                    JSONObject data = _jsonArray.getJSONObject(i);
                                    addLinkBorderForDTNew(data);
                                }

                            }
                        }).subscribe();


                    }
                }
            }
        }).subscribe();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleBluetoothNote(EventOpenOrCloseBluethoothNote note) {
        if (note != null) {
            if (note.getStatus() == 0) {
                finish();
            }
        }
    }

    private void goToViewNote(String lessonId, String itemId, Note note) {
        updateSocket();
        Intent intent = new Intent(this, NoteViewActivity.class);
//        Intent intent = new Intent(getActivity(), WatchCourseActivity3.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("userid", AppConfig.UserID);
        //-----
        intent.putExtra("meeting_id", Integer.parseInt(lessonId) + "," + AppConfig.UserID);
//        intent.putExtra("meeting_id", "Doc-" + AppConfig.UserID);
        intent.putExtra("document_id", itemId);
        intent.putExtra("meeting_type", 2);
        intent.putExtra("lession_id", Integer.parseInt(itemId));
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

    private void handleBluetoothNote(final long noteId) {
        if (noteId <= 0) {
            return;
        }

        Observable.just(noteId + "").observeOn(Schedulers.io()).map(new Function<String, Note>() {
            @Override
            public Note apply(String noteId) throws Exception {
                return MeetingServiceTools.getInstance().syncGetNoteByNoteId(noteId);
            }

        }).map(new Function<Note, String>() {
            @Override
            public String apply(Note note) throws Exception {
                String newUrl = "";
                String url = note.getAttachmentUrl();

                if (!TextUtils.isEmpty(url)) {
                    URL _url = new URL(url);
                    Log.e("check_url_path", _url.getPath());
                    String path = _url.getPath();
                    if (!TextUtils.isEmpty(path)) {
                        if (path.startsWith("/")) {
                            path = path.substring(1);
                        }
                        int index = path.lastIndexOf("/");
                        if (index >= 0 && index < path.length()) {
                            String centerPart = path.substring(0, index);
                            String fileName = path.substring(index + 1, path.length());
                            Log.e("check_transform_url", "centerPart:" + centerPart + ",fileName:" + fileName);
                            if (!TextUtils.isEmpty(centerPart)) {
                                JSONObject queryDocumentResult = DocumentModel.syncQueryDocumentInDoc(AppConfig.URL_LIVEDOC + "queryDocument",
                                        centerPart);
                                if (queryDocumentResult != null) {
                                    Uploadao uploadao = parseQueryResponse(queryDocumentResult.toString());
                                    String part = "";
                                    if (uploadao != null) {
                                        if (1 == uploadao.getServiceProviderId()) {
                                            part = "https://s3." + uploadao.getRegionName() + ".amazonaws.com/" + uploadao.getBucketName() + "/" + centerPart
                                                    + "/" + fileName;
                                        } else if (2 == uploadao.getServiceProviderId()) {
                                            part = "https://" + uploadao.getBucketName() + "." + uploadao.getRegionName() + "." + "aliyuncs.com" + "/" + centerPart + "/" + fileName;
                                        }
                                        url = part;
                                        Log.e("check_transform_url", "url:" + url);
                                    }

                                }
                                currentNoteId = note.getNoteID();

                            }
                        }
                    }

                    int checkIndex = url.lastIndexOf("/");
                    if (checkIndex > 0 && checkIndex < url.length() - 2) {
                        newUrl = url.substring(0, checkIndex + 1) + "book_page_data.json";
                    }
                }


                return newUrl;
            }
        }).map(new Function<String, JSONObject>() {
            @Override
            public JSONObject apply(String url) throws Exception {
                JSONObject jsonObject = new JSONObject();
                if (!TextUtils.isEmpty(url)) {
                    Log.e("check_url", "url:" + url);
                    jsonObject = ServiceInterfaceTools.getinstance().syncGetNotePageJson(url);
                    lastjsonObject = jsonObject.getJSONObject("PaintData");
                }
                return jsonObject;
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject jsonObject) throws Exception {
                if (web == null) {
                    return;
                }
                web.load("javascript:ClearPageAndAction()", null);
                String key = "ShowDotPanData";
                JSONObject _data = new JSONObject();
                _data.put("LinesData", jsonObject);
                _data.put("ShowInCenter", false);
                _data.put("TriggerEvent", false);
                Log.e("ShowDotPanData", "ShowDotPanData");
                web.load("javascript:FromApp('" + key + "'," + _data + ")", null);
                RecordNoteActionManager.getManager(NoteViewActivity.this).sendDisplayHomePageActions(currentNoteId, lastjsonObject);
            }
        }).doOnNext(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject jsonObject) throws Exception {
                if (!newNoteDatas.isEmpty()) {
                    Observable.fromIterable(newNoteDatas).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<TempNoteData>() {
                        @Override
                        public void accept(TempNoteData tempNoteData) throws Exception {
                            Log.e("draw_new_note", "temp_note_note");
                            if (tempNoteData.getNoteId() == currentNoteId) {
                                String key = "ShowDotPanData";
                                JSONObject _data = new JSONObject();
                                _data.put("LinesData", tempNoteData.getData());
                                _data.put("ShowInCenter", true);
                                _data.put("TriggerEvent", true);
                                web.load("javascript:FromApp('" + key + "'," + _data + ")", null);
                            }
                            newNoteDatas.remove(tempNoteData);
                        }
                    }).subscribe();
                }
            }
        }).observeOn(Schedulers.io()).doOnNext(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject jsonObject) throws Exception {
                JSONObject result = ServiceInterfaceTools.getinstance().syncGetNoteP1Item(currentNoteId);
                if (result.has("code")) {
                    if (result.getInt("code") == 0) {
                        JSONArray dataArray = result.getJSONArray("data");
                        Observable.just(dataArray).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<JSONArray>() {
                            @Override
                            public void accept(JSONArray _jsonArray) throws Exception {
                                for (int i = 0; i < _jsonArray.length(); ++i) {
                                    JSONObject data = _jsonArray.getJSONObject(i);
                                    addLinkBorderForDTNew(data);
                                }

                            }
                        }).subscribe();


                    }
                }
            }
        }).subscribe();
    }


}
