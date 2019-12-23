package com.kloudsync.techexcel.ui;


import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebSettings;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.bean.DocumentPage;
import com.kloudsync.techexcel.bean.EventClose;
import com.kloudsync.techexcel.bean.EventCloseShare;
import com.kloudsync.techexcel.bean.EventExit;
import com.kloudsync.techexcel.bean.EventHideMembers;
import com.kloudsync.techexcel.bean.EventHighlightNote;
import com.kloudsync.techexcel.bean.EventInviteUsers;
import com.kloudsync.techexcel.bean.EventMeetingDocuments;
import com.kloudsync.techexcel.bean.EventMute;
import com.kloudsync.techexcel.bean.EventNote;
import com.kloudsync.techexcel.bean.EventPageActions;
import com.kloudsync.techexcel.bean.EventPageNotes;
import com.kloudsync.techexcel.bean.EventRefreshDocs;
import com.kloudsync.techexcel.bean.EventRefreshMembers;
import com.kloudsync.techexcel.bean.EventSetPresenter;
import com.kloudsync.techexcel.bean.EventShareScreen;
import com.kloudsync.techexcel.bean.EventShowMenuIcon;
import com.kloudsync.techexcel.bean.EventSocketMessage;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.MeetingDocument;
import com.kloudsync.techexcel.bean.MeetingMember;
import com.kloudsync.techexcel.bean.MeetingType;
import com.kloudsync.techexcel.bean.NoteDetail;
import com.kloudsync.techexcel.bean.SupportDevice;
import com.kloudsync.techexcel.bean.TvDevice;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.config.RealMeetingSetting;
import com.kloudsync.techexcel.dialog.AddFileFromDocumentDialog;
import com.kloudsync.techexcel.dialog.AddFileFromFavoriteDialog;
import com.kloudsync.techexcel.dialog.CenterToast;
import com.kloudsync.techexcel.dialog.MeetingMembersDialog;
import com.kloudsync.techexcel.dialog.plugin.UserNotesDialog;
import com.kloudsync.techexcel.help.AddDocumentTool;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.DeviceManager;
import com.kloudsync.techexcel.help.DocVedioManager;
import com.kloudsync.techexcel.help.MeetingKit;
import com.kloudsync.techexcel.help.BottomMenuManager;
import com.kloudsync.techexcel.help.NoteViewManager;
import com.kloudsync.techexcel.help.PageActionsAndNotesMgr;
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
import com.kloudsync.techexcel.tool.MeetingSettingCache;
import com.kloudsync.techexcel.tool.SocketMessageManager;
import com.mining.app.zxing.MipcaActivityCapture;
import com.ub.kloudsync.activity.TeamSpaceInterfaceListener;
import com.ub.kloudsync.activity.TeamSpaceInterfaceTools;
import com.ub.techexcel.adapter.AgoraCameraAdapter;
import com.ub.techexcel.adapter.BottomFileAdapter;
import com.ub.techexcel.adapter.FullAgoraCameraAdapter;
import com.ub.techexcel.adapter.MeetingMembersAdapter;
import com.ub.techexcel.bean.AgoraMember;
import com.ub.techexcel.bean.Note;
import com.ub.techexcel.tools.DownloadUtil;
import com.ub.techexcel.tools.ExitDialog;
import com.ub.techexcel.tools.FavoriteVideoPopup;
import com.ub.techexcel.tools.FileUtils;
import com.ub.techexcel.tools.ServiceInterfaceTools;
import com.ub.techexcel.tools.Tools;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xwalk.core.XWalkPreferences;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import Decoder.BASE64Encoder;
import butterknife.Bind;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by tonyan on 2019/11/19.
 */

public class DocAndMeetingActivity extends BaseDocAndMeetingActivity implements PopBottomMenu.BottomMenuOperationsListener, PopBottomFile.BottomFileOperationsListener, AddFileFromFavoriteDialog.OnFavoriteDocSelectedListener,
        BottomFileAdapter.OnDocumentClickListener, View.OnClickListener, AddFileFromDocumentDialog.OnDocSelectedListener, MeetingMembersAdapter.OnMemberClickedListener, AgoraCameraAdapter.OnCameraOptionsListener {

    public static MeetingConfig meetingConfig;
    private SocketMessageManager messageManager;
    //---
    private BottomMenuManager menuManager;
    private PopBottomFile bottomFilePop;
    private MeetingKit meetingKit;
    //---
    @Bind(R.id.layout_real_meeting)
    RelativeLayout meetingLayout;
    @Bind(R.id.layout_toggle_camera)
    LinearLayout toggleCameraLayout;
    @Bind(R.id.image_toggle_camera)
    ImageView toggleCameraImage;
    @Bind(R.id.member_camera_list)
    RecyclerView cameraList;
    @Bind(R.id.full_camera_list)
    RecyclerView fullCameraList;
    @Bind(R.id.meeting_menu)
    ImageView meetingMenu;
    @Bind(R.id.layout_note)
    RelativeLayout noteLayout;
    @Bind(R.id.layout_full_camera)
    RelativeLayout fullCamereLayout;
    @Bind(R.id.icon_back_full_screen)
    ImageView backFullCameraImage;
    @Bind(R.id.layout_vedio)
    RelativeLayout vedioLayout;
    @Bind(R.id.image_vedio_close)
    ImageView closeVedioImage;

    @Bind(R.id.layout_meeting_default_document)
    RelativeLayout meetingDefaultDocument;

    @Bind(R.id.layout_remote_share)
    RelativeLayout remoteShareLayout;
    @Bind(R.id.frame_remote_share)
    FrameLayout remoteShareFrame;

    AgoraCameraAdapter cameraAdapter;
    FullAgoraCameraAdapter fullCameraAdapter;

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

        initViews();
        //----
        RealMeetingSetting realMeetingSetting = MeetingSettingCache.getInstance(this).getMeetingSetting();
        meetingConfig = getConfig();
        messageManager = SocketMessageManager.getManager(this);
        messageManager.registerMessageReceiver();
        if (meetingConfig.getType() != MeetingType.MEETING) {
            messageManager.sendMessage_JoinMeeting(meetingConfig);
        } else {
            MeetingKit.getInstance().prepareJoin(this, meetingConfig);
        }

        pageCache = DocumentPageCache.getInstance(this);
        //--
        menuManager = BottomMenuManager.getInstance(this, meetingConfig);
        menuManager.setBottomMenuOperationsListener(this);
        menuManager.setMenuIcon(menuIcon);
        initWeb();
        bottomFilePop = new PopBottomFile(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (bottomFilePop != null && bottomFilePop.isShowing()) {
//            bottomFilePop.hide();
//        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }


    @Override
    protected void onResume() {
        if (menuManager != null) {
            menuManager.setMenuIcon(menuIcon);
        }
        if (bottomFilePop != null && !bottomFilePop.isShowing()) {
            menuIcon.setImageResource(R.drawable.icon_menu);
            menuIcon.setEnabled(true);
        }
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
        meetingConfig.setType(data.getIntExtra("meeting_type", MeetingType.DOC));
        meetingConfig.setMeetingId(data.getStringExtra("meeting_id"));
        meetingConfig.setLessionId(data.getIntExtra("lession_id", 0));
        meetingConfig.setDocumentId(data.getStringExtra("document_id"));
        meetingConfig.setRole(data.getIntExtra("meeting_role", MeetingConfig.MeetingRole.HOST));
        meetingConfig.setUserToken(UserData.getUserToken(this));
        meetingConfig.setFromMeeting(data.getBooleanExtra("from_meeting", false));
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


    private synchronized void getMeetingMembers(JSONArray users) {
        List<MeetingMember> allMembers = (List<MeetingMember>) new Gson().fromJson(users.toString(), new TypeToken<List<MeetingMember>>() {
        }.getType());
        List<MeetingMember> auditors = new ArrayList<>();
        List<MeetingMember> members = new ArrayList<>();
        for (MeetingMember member : allMembers) {
            if (member.getRole() == 3) {
                //旁听生
                auditors.add(member);
            } else {
                members.add(member);
            }

            if (member.getRole() == 2) {
                meetingConfig.setMeetingHostId(member.getUserId() + "");
            }

            if (member.getPresenter() == 1) {
                meetingConfig.setPresenterId(member.getUserId() + "");
            }
        }

        Log.e("getMeetingMembers", "members_size:" + members.size());
        meetingConfig.setMeetingAuditor(auditors);
        meetingConfig.setMeetingMembers(members);
//        EventRefreshMembers refreshMembers = new EventRefreshMembers();
//        refreshMembers.setMeetingConfig(meetingConfig);
//        EventBus.getDefault().post(refreshMembers);
    }

    private void requestDocumentsAndShowPage() {
        DocumentModel.asyncGetDocumentsInDocAndShowPage(meetingConfig);
    }

    // ------- @Subscribe
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveDocuments(EventMeetingDocuments documents) {
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
            menuIcon.setVisibility(View.VISIBLE);

            if (meetingConfig.getType() == MeetingType.MEETING) {
                meetingDefaultDocument.setVisibility(View.VISIBLE);
                MeetingKit.getInstance().handleMeetingDefaultDocument(meetingDefaultDocument);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshDocuments(EventRefreshDocs refreshDocs) {
        // 所有文档的data
        Log.e("refreshDocuments", "documents:" + documents);
        this.documents = refreshDocs.getDocuments();
        changeDocument(documents.get(documents.indexOf(new MeetingDocument(refreshDocs.getItemId()))), 1);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showDocumentPage(DocumentPage page) {
        Log.e("showDocumentPage", "page:" + page);
        hideEnterLoading();
        MeetingDocument document = getDocument(page);
        Log.e("showDocumentPage", "current_document:" + document);
        if (document != null) {
            meetingConfig.setDocument(document);
            meetingConfig.setPageNumber(meetingConfig.getDocument().getDocumentPages().indexOf(page) + 1);
        }

        //notify change file
        notifyDocumentChanged();
        Log.e("Show_PDF", "javascript:ShowPDF('" + page.getShowingPath() + "'," + (page.getPageNumber()) + ",''," + meetingConfig.getDocument().getAttachmentID() + "," + false + ")");
        meetingDefaultDocument.setVisibility(View.GONE);
        web.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        web.load("javascript:ShowPDF('" + page.getShowingPath() + "'," + (page.getPageNumber()) + ",''," + meetingConfig.getDocument().getAttachmentID() + "," + false + ")", null);
        web.load("javascript:Record()", null);
        if (bottomFilePop != null && bottomFilePop.isShowing()) {
            bottomFilePop.setDocuments(this.documents, meetingConfig.getDocument().getItemID(), this);
            bottomFilePop.removeTempDoc();
        } else {
            menuIcon.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void showNote(EventNote note) {
        Note _note = note.getNote();
        Log.e("show_note", "note:" + _note);
        if (_note == null || TextUtils.isEmpty(_note.getLocalFilePath())) {
            return;
        }

        NoteViewManager.getInstance().setContent(this, noteLayout, _note, meetingConfig);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showMenuIcon(EventShowMenuIcon showMenuIcon) {
        if (menuIcon != null) {
            Log.e("showMenuIcon", "show");
            menuIcon.setImageResource(R.drawable.icon_menu);
            menuIcon.setEnabled(true);
            Log.e("showMenuIcon", "menu visible:  " + (menuIcon.getVisibility() == View.VISIBLE));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void exit(EventExit exit) {
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
                        if (web != null) {
                            web.load("javascript:PlayActionByTxt('" + _frame + "','" + 1 + "')", null);
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
                MeetingKit.getInstance().requestMeetingMembers(meetingConfig);
                break;
            case SocketMessageManager.MESSAGE_AGORA_STATUS_CHANGE:
//                handleMessageAgoraStatusChange(socketMessage.getData());
                break;
        }
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
                web.load("javascript:PlayActionByArray(" + data + "," + 0 + ")", null);
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
    public void showMemeberCamera(AgoraMember member) {
        Log.e("showMemeberCamera", "member:" + member);
        if ((member.getUserId() + "").equals(AppConfig.UserID) && member.isAdd()) {
            //自己开启会议成功
            if (documents != null && documents.size() > 0) {
                notifyDocumentChanged();
            }
        }

        if (member.isAdd()) {
            meetingConfig.addAgoraMember(member);
        } else {
            //delete user
            meetingConfig.deleteAgoraMember(member);
        }

        checkAgoraMemberName();
        refreshAgoraMember(member);

        if (cameraAdapter != null) {
            cameraAdapter.setOnCameraOptionsListener(this);
        }

        Log.e("check_send_agora_status", "user_id:" + AppConfig.UserID + ",agora_id:" + member.getUserId());
        if ((member.getUserId() + "").equals(AppConfig.UserID)) {
            messageManager.sendMessage_AgoraStatusChange(meetingConfig, member);
        }

    }

    private EventShareScreen shareScreen;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showShareScreen(EventShareScreen shareScreen) {
        Log.e("showShareScreen", "show_screen");
        this.shareScreen = shareScreen;
        remoteShareLayout.setVisibility(View.VISIBLE);
        if (remoteShareFrame.getChildCount() == 0) {
            ViewParent parent = shareScreen.getShareView().getParent();
            if (parent != null) {
                ((FrameLayout) parent).removeView(shareScreen.getShareView());
            }
        }
        MeetingKit.getInstance().setShareScreenStream(shareScreen);
        remoteShareFrame.removeAllViews();
        remoteShareFrame.addView(shareScreen.getShareView(), new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        reloadAgoraMember();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void closeShareScreen(EventCloseShare closeShare) {
        SurfaceView surfaceView = (SurfaceView) remoteShareFrame.getChildAt(0);
        if (surfaceView != null) {
            surfaceView.setVisibility(View.GONE);
        }
        remoteShareFrame.removeAllViews();
        remoteShareLayout.setVisibility(View.GONE);
        shareScreen = null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void inviteUsers(EventInviteUsers inviteUsers) {
        messageManager.sendMessage_InviteToMeeting(meetingConfig, inviteUsers.getUsers());
        MeetingKit.getInstance().requestMeetingMembers(meetingConfig );
    }

    public void refreshAgoraMember(AgoraMember member) {
        if (cameraList.getVisibility() == View.VISIBLE) {
            if (cameraAdapter != null) {
                if (member.isAdd()) {
                    cameraAdapter.addUser(member);
                } else {
                    cameraAdapter.removeUser(member);
                }
            } else {
                List<AgoraMember> copyMembers = new ArrayList<>();
                for (AgoraMember _member : meetingConfig.getAgoraMembers()) {
                    copyMembers.add(_member);
                }
                cameraAdapter = new AgoraCameraAdapter(this);
                cameraAdapter.setMembers(copyMembers);
                cameraAdapter.setOnCameraOptionsListener(this);
//            fitCameraList();
                cameraList.setAdapter(cameraAdapter);
                MeetingKit.getInstance().setCameraAdapter(cameraAdapter);
            }
        }

        if (fullCameraList.getVisibility() == View.VISIBLE) {
            if (fullCameraAdapter != null) {
                if (member.isAdd()) {
                    fullCameraAdapter.addUser(member);
                } else {
                    fullCameraAdapter.removeUser(member);
                }
            } else {
                List<AgoraMember> copyMembers = new ArrayList<>();
                for (AgoraMember _member : meetingConfig.getAgoraMembers()) {
                    copyMembers.add(_member);
                }
                fullCameraAdapter = new FullAgoraCameraAdapter(this);
                fullCameraAdapter.setMembers(copyMembers);
                fitFullCameraList();
                fullCameraList.setAdapter(fullCameraAdapter);
                MeetingKit.getInstance().setFullCameraAdaptero(fullCameraAdapter);
            }
        }
    }


    private void reloadAgoraMember() {
        List<AgoraMember> copyMembers = new ArrayList<>();
        for (AgoraMember member : meetingConfig.getAgoraMembers()) {
            copyMembers.add(member);
        }

        if (cameraList.getVisibility() == View.VISIBLE) {
            if (cameraAdapter != null) {
                cameraAdapter.reset();
                cameraAdapter = null;
            }

            cameraAdapter = new AgoraCameraAdapter(this);
            cameraAdapter.setMembers(copyMembers);
            cameraAdapter.setOnCameraOptionsListener(this);
//            fitCameraList();
            cameraList.setAdapter(cameraAdapter);
            MeetingKit.getInstance().setCameraAdapter(cameraAdapter);
        }

        if (fullCameraList.getVisibility() == View.VISIBLE) {
            if (fullCameraAdapter != null) {
                fullCameraAdapter.reset();
                fullCameraAdapter = null;
            }
            fullCameraAdapter = new FullAgoraCameraAdapter(this);
            fullCameraAdapter.setMembers(copyMembers);
            fitFullCameraList();
            fullCameraList.setAdapter(fullCameraAdapter);
            MeetingKit.getInstance().setFullCameraAdaptero(fullCameraAdapter);
        }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshMeetingMembers(EventRefreshMembers refreshMembers) {

        if (meetingConfig.getMeetingMembers() == null || meetingConfig.getType() != MeetingType.MEETING) {
            return;
        }
        if (meetingMembersDialog != null && meetingMembersDialog.isShowing()) {
            Log.e("refreshMeetingMembers", "dialog_is_show");
            meetingMembersDialog.refresh(refreshMembers);
        }
        checkAgoraMemberName();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void muteAgoraMember(EventMute eventMute) {
        Log.e("muteAgoraMember", "eventMute:" + eventMute);
        if (cameraList.getVisibility() == View.VISIBLE) {
            if (cameraAdapter != null) {
                if (eventMute.getType() == EventMute.TYPE_MUTE_VEDIO) {
                    Log.e("muteAgoraMember", "muteVideo");
                    cameraAdapter.muteVideo(eventMute.getAgoraMember(), eventMute.isMuteVedio());
                } else if (eventMute.getType() == EventMute.TYPE_MUTE_AUDIO) {
                    Log.e("muteAgoraMember", "muteAudio");
                    cameraAdapter.muteAudio(eventMute.getAgoraMember(), eventMute.isMuteAudio());
                }

            }
        }

        if (fullCameraList.getVisibility() == View.VISIBLE) {
            if (fullCameraAdapter != null) {
                if (eventMute.getType() == EventMute.TYPE_MUTE_VEDIO) {
                    fullCameraAdapter.muteVideo(eventMute.getAgoraMember(), eventMute.isMuteVedio());
                } else if (eventMute.getType() == EventMute.TYPE_MUTE_AUDIO) {
                    fullCameraAdapter.muteAudio(eventMute.getAgoraMember(), eventMute.isMuteAudio());

                }

            }
        }

        if (eventMute.getAgoraMember() != null) {
            if ((eventMute.getAgoraMember().getUserId() + "").equals(AppConfig.UserID)) {
                int index = meetingConfig.getAgoraMembers().indexOf(eventMute.getAgoraMember());
                if (index >= 0) {
                    messageManager.sendMessage_AgoraStatusChange(meetingConfig, meetingConfig.getAgoraMembers().get(index));
                }

            }
        }

    }

    private void checkAgoraMemberName() {
        for (MeetingMember member : meetingConfig.getMeetingMembers()) {
            for (AgoraMember agoraMember : meetingConfig.getAgoraMembers()) {
                if ((member.getUserId() + "").equals(agoraMember.getUserId() + "")) {
                    agoraMember.setUserName(member.getUserName());
                    agoraMember.setIconUrl(member.getAvatarUrl());
                    break;
                }
            }
        }

        for (MeetingMember member : meetingConfig.getMeetingAuditor()) {
            for (AgoraMember agoraMember : meetingConfig.getAgoraMembers()) {
                if ((member.getUserId() + "").equals(agoraMember.getUserId() + "")) {
                    agoraMember.setUserName(member.getUserName());
                    agoraMember.setIconUrl(member.getAvatarUrl());
                    break;
                }
            }
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
                DocumentPage page = document.getDocumentPages().get(pageNumber - 1);
                queryAndDownLoadPageToShow(page, true);
                return page;
            }
        }).subscribe();
    }

    private void downLoadDocumentPageAndShow(MeetingDocument document, final int pageNumber) {
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
        Log.e("-", "get cach page:" + page + "--> url:" + documentPage.getPageUrl());
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

            Log.e("-", "showUrl:" + showUrl);

            documentPage.setSavedLocalPath(pathLocalPath);

            Log.e("-", "page:" + documentPage);
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

    private void changeDocument(MeetingDocument document, int pageNumber) {
        Log.e("changeDocument", "document:" + document);
        downLoadDocumentPageAndShow(document, pageNumber);
    }

    private void changeDocument(int itemId, int pageNumber) {
        int index = documents.indexOf(new MeetingDocument(itemId));
        if (index < 0) {
            return;
        }
        MeetingDocument _document = documents.get(index);
        if (meetingConfig.getDocument().equals(_document)) {
            return;
        }
        changeDocument(_document, pageNumber);
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
        Log.e("JavascriptInterface", "afterLoadPageFunction");
    }

    @org.xwalk.core.JavascriptInterface
    public void userSettingChangeFunction(final String option) {
        Log.e("JavascriptInterface", "userSettingChangeFunction,option:  " + option);

    }

    @org.xwalk.core.JavascriptInterface
    public synchronized void preLoadFileFunction(final String url, final int currentpageNum, final boolean showLoading) {
        Log.e("JavascriptInterface", "preLoadFileFunction,url:  " + url + ", currentpageNum:" + currentpageNum + ",showLoading:" + showLoading);
        meetingConfig.setNotifyUrl(url);
        if (currentpageNum - 1 < 0) {
            return;
        }

        final DocumentPage page = meetingConfig.getDocument().getDocumentPages().get(currentpageNum - 1);

        final String pathLocalPath = url.substring(0, url.lastIndexOf("<")) +
                currentpageNum + url.substring(url.lastIndexOf("."));

        if (page != null && !TextUtils.isEmpty(page.getPageUrl())) {
            DocumentPage _page = pageCache.getPageCache(page.getPageUrl());
            Log.e("check_cache_page", "_page:" + _page + "，page:" + page);
            if (_page != null && page.getPageUrl().equals(_page.getPageUrl())) {
                if (!TextUtils.isEmpty(_page.getSavedLocalPath())) {
                    File localFile = new File(_page.getSavedLocalPath());
                    if (localFile.exists()) {
                        if (!pathLocalPath.equals(localFile.getAbsolutePath())) {
                            if (localFile.renameTo(new File(pathLocalPath))) {
                                Log.e("preLoadFileFunction", "uncorrect_file_name,rename");
                                notifyWebFilePrepared(url, currentpageNum);
                                return;
                            } else {
                                Log.e("preLoadFileFunction", "uncorrect_file_name,delete");
                                localFile.delete();
                            }
                        } else {
                            Log.e("preLoadFileFunction", "correct_file_name,notify");
                            notifyWebFilePrepared(url, currentpageNum);
                            return;
                        }

                    } else {
                        //清楚缓存
                        pageCache.removeFile(_page.getPageUrl());
                    }

                }
            }
        }

        Log.e("JavascriptInterface", "preLoadFileFunction,page:  " + page);

        new ApiTask(new Runnable() {
            @Override
            public void run() {
                safeDownloadFile(pathLocalPath, page, url, currentpageNum, true);
            }
        }).start(ThreadManager.getManager());

    }


    @org.xwalk.core.JavascriptInterface
    public void afterLoadFileFunction() {
        Log.e("JavascriptInterface", "afterLoadFileFunction");

    }

    @org.xwalk.core.JavascriptInterface
    public void showErrorFunction(final String error) {
        Log.e("JavascriptInterface", "showErrorFunction,error:  " + error);

    }

    @org.xwalk.core.JavascriptInterface
    public void afterChangePageFunction(final int pageNum, int type) {
        Log.e("JavascriptInterface", "afterChangePageFunction,pageNum:  " + pageNum + ", type:" + type);
        meetingConfig.setPageNumber(pageNum);
        PageActionsAndNotesMgr.requestActionsAndNote(meetingConfig);
    }

    @org.xwalk.core.JavascriptInterface
    public void reflect(String result) {
        Log.e("JavascriptInterface", "reflect,result:  " + result);
        meetingConfig.setDocModifide(checkIfModifyDoc(result));
        notifyMyWebActions(result);
        DocVedioManager.getInstance(this).prepareVedio(result);
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
        if (meetingConfig.getType() != MeetingType.MEETING) {
            if (messageManager == null) {
                messageManager = SocketMessageManager.getManager(this);
                messageManager.registerMessageReceiver();
            }
            messageManager.sendMessage_MyActionFrame(actions, meetingConfig);

        } else {
            Log.e("notifyMyWebActions", "role:" + meetingConfig.getRole());
            if (!AppConfig.UserID.equals(meetingConfig.getPresenterId())) {
                return;
            }
            if (messageManager == null) {
                messageManager = SocketMessageManager.getManager(this);
                messageManager.registerMessageReceiver();
            }
            messageManager.sendMessage_MyActionFrame(actions, meetingConfig);
        }
    }

    private void notifyDocumentChanged() {
        if (meetingConfig.getType() != MeetingType.MEETING) {
            if (messageManager != null) {
                messageManager.sendMessage_DocumentShowed(meetingConfig);
            }
        } else {
            if (!TextUtils.isEmpty(meetingConfig.getPresenterSessionId())) {
                if (AppConfig.UserToken.equals(meetingConfig.getPresenterSessionId())) {
                    if (meetingConfig.isInRealMeeting()) {
                        if (messageManager != null) {
                            messageManager.sendMessage_DocumentShowed(meetingConfig);
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
    public synchronized void callAppFunction(String action, final String data) {
        Log.e("JavascriptInterface", "callAppFunction,action:  " + action + ",data:" + data);
        if (TextUtils.isEmpty(action) || TextUtils.isEmpty(data)) {
            return;
        }

        try {
            PageActionsAndNotesMgr.handleNoteActions(this, action, new JSONObject(data));
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
            menuManager.setMenuIcon(menuIcon);
            menuManager.totalHideMenu();
        }
        menuIcon.setImageResource(R.drawable.shape_transparent);
        menuIcon.setEnabled(false);
        bottomFilePop.show(web, this);
    }

    @Override
    public void menuStartMeetingClicked() {
        meetingKit = MeetingKit.getInstance();
        meetingKit.prepareStart(this, meetingConfig, meetingConfig.getLessionId() + "");
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
        if (meetingConfig.getMeetingMembers() == null || meetingConfig.getMeetingMembers().size() < 0) {
            return;
        }
        showMembersDialog();
    }

    //-----
    @Override
    public void addFromTeam() {
        openTeamDocument();

    }

    @Override
    public void addFromCamera() {
        openCameraForAddDoc();
    }

    @Override
    public void addFromPictures() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICTURE_ADD_DOC);
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

    private void initRealMeeting() {
        Log.e("DocAndMeetigActivity", "initRealMeeting");
        if (meetingConfig.getType() != MeetingType.MEETING) {
            return;
        }

        MeetingKit.getInstance().startMeeting();
        meetingLayout.setVisibility(View.VISIBLE);
        if (messageManager != null && meetingConfig.getRole() == MeetingConfig.MeetingRole.HOST) {
            messageManager.sendMessage_MeetingStatus(meetingConfig);
        }
    }

    private void initViews() {
        toggleCameraLayout.setOnClickListener(this);
        cameraList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        cameraList.setDrawingCacheEnabled(true);
        cameraList.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
        fullCameraList.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
        fullCameraList.setDrawingCacheEnabled(true);
        fullCameraList.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
        meetingMenu.setOnClickListener(this);
        backFullCameraImage.setOnClickListener(this);
        closeVedioImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_toggle_camera:
                meetingConfig.setMembersCameraToggle(!meetingConfig.isMembersCameraToggle());
                toggleMembersCamera(meetingConfig.isMembersCameraToggle());
                break;
            case R.id.meeting_menu:
                if (meetingKit == null) {
                    meetingKit = MeetingKit.getInstance();
                }
                meetingKit.showMeetingMenu(meetingMenu, this, meetingConfig);
                break;
            case R.id.icon_back_full_screen:
                fullCamereLayout.setVisibility(View.GONE);
                fullCameraList.setVisibility(View.GONE);
                cameraList.setVisibility(View.VISIBLE);
                reloadAgoraMember();
                if (shareScreen != null) {
                    showShareScreen(shareScreen);
                }
                break;
            case R.id.image_vedio_close:
                DocVedioManager.getInstance(this).close();
                break;
        }
    }


    private void toggleMembersCamera(boolean isToggle) {
        fullCameraList.setVisibility(View.GONE);
        if (cameraList.getVisibility() != View.VISIBLE) {
            cameraList.setVisibility(View.VISIBLE);
            reloadAgoraMember();
            toggleCameraImage.setImageResource(R.drawable.eyeclose);
        } else {
            if (cameraAdapter != null) {
                cameraAdapter.reset();
            }
            cameraList.setVisibility(View.GONE);
            toggleCameraImage.setImageResource(R.drawable.eyeopen);
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


    MeetingMembersDialog meetingMembersDialog;

    private void showMembersDialog() {

        if (meetingMembersDialog != null) {
            if (meetingMembersDialog.isShowing()) {
                meetingMembersDialog.dismiss();
                meetingMembersDialog = null;
            }
        }
        meetingMembersDialog = new MeetingMembersDialog();
        meetingMembersDialog.init(this, meetingConfig);
        meetingMembersDialog.show(getSupportFragmentManager());
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
                    break;
            }
        }
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
                            new CenterToast.Builder(DocAndMeetingActivity.this).
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
        DocumentModel.asyncGetDocumentsInDocAndRefreshFileList(meetingConfig, newItemid);
        if (bottomFilePop != null && bottomFilePop.isShowing()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new CenterToast.Builder(DocAndMeetingActivity.this).
                            setSuccess(true).setMessage("operate success").create().show();
                }
            });

        }
    }

    private void handleScanTv() {
        Observable.just(meetingConfig).observeOn(Schedulers.io()).map(new Function<MeetingConfig, List<TvDevice>>() {
            @Override
            public List<TvDevice> apply(MeetingConfig meetingConfig) throws Exception {
                Response<DevicesResponse> response = ServiceInterfaceTools.getinstance().getBindTvs().execute();
                List<TvDevice> _devices = new ArrayList<>();
                if (response.isSuccessful() && response.body().getData() != null) {
                    List<TvDevice> devices = response.body().getData().getDeviceList();
                    for (TvDevice device : devices) {
                        if (device.getDeviceType() == 3) {
                            _devices.add(device);
                        }
                    }
                }
                return _devices;
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<List<TvDevice>>() {
            @Override
            public void accept(List<TvDevice> tvDevices) throws Exception {
                if (tvDevices.size() > 0) {
                } else {
                    if (meetingConfig.getType() != MeetingType.MEETING) {
                        gotoScanTv();
                    } else {
                        MeetingKit.getInstance().checkCameraForScan();
                        gotoScanTv();
                    }
                }
            }
        }).subscribe();

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
                    Intent intent = new Intent(DocAndMeetingActivity.this, MipcaActivityCapture.class);
                    intent.putExtra("isHorization", true);
                    intent.putExtra("type", 0);
                    startActivityForResult(intent, REQUEST_SCAN);
                }
            }, 500);

        } else {
            Intent intent = new Intent(DocAndMeetingActivity.this, MipcaActivityCapture.class);
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

    // --set presenter
    @Override
    public void onMemberClicked(MeetingMember meetingMember) {
        if (setPresenterDialog != null) {
            setPresenterDialog.dismiss();
        }
        setPresenterDialog = new SetPresenterDialog(this);
        setPresenterDialog.show(meetingMember, this);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setPresenter(EventSetPresenter setPresenter) {
        messageManager.sendMessage_MakePresenter(meetingConfig, setPresenter.getMeetingMember());

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

    //------Camera vedio options

    @Override
    public void onCameraFrameClick(AgoraMember member) {
        handleFullScreenCamera(cameraAdapter);
    }

    private void handleFullScreenCamera(AgoraCameraAdapter cameraAdapter) {
        if (cameraAdapter == null || cameraAdapter.getUsers().size() == 0) {
            return;
        }
        showFullCameraScreen();
    }

    private void showFullCameraScreen() {
        fullCamereLayout.setVisibility(View.VISIBLE);
        fullCameraList.setVisibility(View.VISIBLE);
        cameraList.setVisibility(View.GONE);
        reloadAgoraMember();

    }

    private void fitFullCameraList() {

        int size = meetingConfig.getAgoraMembers().size();

        if (size == 1) {
            fullCameraList.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));

        } else if (size > 1 && size <= 4) {

            fullCameraList.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));

        } else if (size > 4 && size <= 6) {

            fullCameraList.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));

        } else if (size > 6 && size <= 8) {

            fullCameraList.setLayoutManager(new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false));

        } else {
            fullCameraList.setLayoutManager(new GridLayoutManager(this, 5, GridLayoutManager.VERTICAL, false));

        }
        GridLayoutManager s = (GridLayoutManager) fullCameraList.getLayoutManager();
        int currentSpanCount = s.getSpanCount();

        Log.e("fitFullCameraList", "span:" + currentSpanCount);
    }

//    private void fitCameraList() {
//
//        int size = meetingConfig.getAgoraMembers().size();
//
//        if (size > 0 && size <= 8) {
//            cameraList.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false));
//
//        } else if (size > 8 && size <= 16) {
//
//            cameraList.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false));
//
//        } else if (size > 16 ) {
//
//            cameraList.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.HORIZONTAL, false));
//
//        }
//        GridLayoutManager s = (GridLayoutManager) fullCameraList.getLayoutManager();
//        int currentSpanCount = s.getSpanCount();
//    }


    // ----handle_message

    private void handleMessageSendMessage(JSONObject data) throws JSONException {
        if (!data.has("actionType")) {
            return;
        }

        switch (data.getInt("actionType")) {
            case 8:
                changeDocument(data.getInt("itemId"), Integer.parseInt(data.getString("pageNumber")));
                break;
        }
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
                        getMeetingMembers(usersList);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        MeetingKit.getInstance().requestMeetingMembers(meetingConfig);

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
                DocumentModel.asyncGetDocumentsInDocAndRefreshFileList(meetingConfig, Integer.parseInt(_id));
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
                    String pageData = dataJson.getString("CurrentDocumentPage");
                    String[] datas = pageData.split("-");
                    meetingConfig.setFileId(Integer.parseInt(datas[0]));
                    float page = Float.parseFloat(datas[1]);
                    meetingConfig.setPageNumber((int) page);
                    meetingConfig.setType(dataJson.getInt("type"));
                    if (documents == null || documents.size() <= 0) {
                        requestDocumentsAndShowPage();
                    }

                    if (meetingConfig.getType() == MeetingType.DOC) {
                        meetingLayout.setVisibility(View.GONE);
                    } else if (meetingConfig.getType() == MeetingType.MEETING) {
                        if (dataJson.has("presenterSessionId")) {
                            meetingConfig.setPresenterSessionId(dataJson.getString("presenterSessionId"));
                        }

                        if (dataJson.has("usersList")) {
                            JSONArray users = dataJson.getJSONArray("usersList");
                            if (users.length() >= 0) {
                                getMeetingMembers(users);
                            }
                        }

                        MeetingKit.getInstance().requestMeetingMembers(meetingConfig);

                        if (meetingConfig.isInRealMeeting()) {
                            return;
                        }
                        //
                        initRealMeeting();
                    }
//                    Log.e("MeetingConfig","MeetingConfig:" + meetingConfig);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleMessageAgoraStatusChange(JSONObject data) {
        Log.e("agora_status_change", "data:" + data);
        if (data.has("retData")) {
            try {
                JSONObject _data = data.getJSONObject("retData");
                if (_data.has("usersList")) {
                    JSONArray userList = _data.getJSONArray("usersList");

                    for (int i = 0; i < userList.length(); ++i) {
                        JSONObject user = userList.getJSONObject(i);
                        Log.e("check_list", "user:" + user);
                        int index = meetingConfig.getAgoraMembers().indexOf(new AgoraMember(Integer.parseInt(user.getString("userId"))));
                        if (index >= 0) {
                            AgoraMember member = meetingConfig.getAgoraMembers().get(index);
                            member.setUserName(user.getString("userName"));
                            member.setMuteAudio(!(user.getInt("microphoneStatus") == 2));
                            member.setMuteVideo(!(user.getInt("cameraStatus") == 2));
                        } else {
                            AgoraMember agoraMember = new AgoraMember();
                            agoraMember.setUserId(Integer.parseInt(user.getString("userId")));
                            agoraMember.setUserName(user.getString("userName"));
                            agoraMember.setIconUrl(user.getString("avatarUrl"));
                            agoraMember.setMuteAudio(!(user.getInt("microphoneStatus") == 2));
                            agoraMember.setMuteVideo(!(user.getInt("cameraStatus") == 2));
                            meetingConfig.getAgoraMembers().add(agoraMember);
                        }

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
