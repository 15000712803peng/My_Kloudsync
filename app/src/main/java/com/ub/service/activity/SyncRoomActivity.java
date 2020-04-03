package com.ub.service.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.model.ResumableUploadRequest;
import com.alibaba.sdk.android.oss.model.ResumableUploadResult;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.mobileconnectors.s3.transfermanager.Upload;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.facebook.network.connectionclass.ConnectionClassManager;
import com.facebook.network.connectionclass.ConnectionQuality;
import com.google.gson.Gson;
import com.kloudsync.techexcel.R;
import com.kloudsync.techexcel.app.App;
import com.kloudsync.techexcel.bean.BookNote;
import com.kloudsync.techexcel.bean.EventSyncRoom;
import com.kloudsync.techexcel.bean.EventSyncSucc;
import com.kloudsync.techexcel.bean.MeetingType;
import com.kloudsync.techexcel.bean.NoteDetail;
import com.kloudsync.techexcel.bean.NoteId;
import com.kloudsync.techexcel.bean.SupportDevice;
import com.kloudsync.techexcel.bean.TvDevice;
import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.AddFileFromDocumentDialog;
import com.kloudsync.techexcel.dialog.AddFileFromFavoriteDialog;
import com.kloudsync.techexcel.dialog.CenterToast;
import com.kloudsync.techexcel.dialog.SelectNoteDialog;
import com.kloudsync.techexcel.dialog.ShareSyncDialog;
import com.kloudsync.techexcel.help.ApiTask;
import com.kloudsync.techexcel.help.DeviceManager;
import com.kloudsync.techexcel.help.PopAlbums;
import com.kloudsync.techexcel.help.Popupdate;
import com.kloudsync.techexcel.help.Popupdate2;
import com.kloudsync.techexcel.help.ShareKloudSyncDialog;
import com.kloudsync.techexcel.help.ThreadManager;
import com.kloudsync.techexcel.httpgetimage.ImageLoader;
import com.kloudsync.techexcel.info.ConvertingResult;
import com.kloudsync.techexcel.info.Customer;
import com.kloudsync.techexcel.info.Uploadao;
import com.kloudsync.techexcel.response.DevicesResponse;
import com.kloudsync.techexcel.service.ConnectService;
import com.kloudsync.techexcel.start.LoginGet;
import com.kloudsync.techexcel.start.StartUbao;
import com.kloudsync.techexcel.tool.DocumentUploadUtil;
import com.kloudsync.techexcel.tool.FileGetTool;
import com.kloudsync.techexcel.tool.LocalNoteManager;
import com.kloudsync.techexcel.tool.Md5Tool;
import com.kloudsync.techexcel.ui.DocAndMeetingActivity;
import com.kloudsync.techexcel.ui.MeetingViewActivity;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.util.PreferencesCookieStore;
import com.mining.app.zxing.MipcaActivityCapture;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.pen.RawInputCallback;
import com.onyx.android.sdk.pen.TouchHelper;
import com.onyx.android.sdk.pen.data.TouchPoint;
import com.onyx.android.sdk.pen.data.TouchPointList;
import com.ub.kloudsync.activity.Document;
import com.ub.kloudsync.activity.TeamSpaceBean;
import com.ub.kloudsync.activity.TeamSpaceInterfaceListener;
import com.ub.kloudsync.activity.TeamSpaceInterfaceTools;
import com.ub.service.audiorecord.AudioRecorder;
import com.ub.service.audiorecord.RecordEndListener;
import com.ub.teacher.gesture.BrightnessHelper;
import com.ub.teacher.gesture.ShowChangeLayout;
import com.ub.teacher.gesture.VideoGestureRelativeLayout;
import com.ub.techexcel.adapter.ChatAdapter;
import com.ub.techexcel.bean.AudioActionBean;
import com.ub.techexcel.bean.LineItem;
import com.ub.techexcel.bean.Note;
import com.ub.techexcel.bean.PageActionBean;
import com.ub.techexcel.bean.ServiceBean;
import com.ub.techexcel.bean.SoundtrackBean;
import com.ub.techexcel.bean.SyncRoomBean;
import com.ub.techexcel.bean.TelePhoneCall;
import com.ub.techexcel.tools.AudienceMemberPopup;
import com.ub.techexcel.tools.ConfirmDialog;
import com.ub.techexcel.tools.CreateSyncRoomPopup;
import com.ub.techexcel.tools.DetchedPopup;
import com.ub.techexcel.tools.DownloadUtil;
import com.ub.techexcel.tools.FavoritePopup;
import com.ub.techexcel.tools.FavoriteVideoPopup;
import com.ub.techexcel.tools.FileUtils;
import com.ub.techexcel.tools.InviteUserPopup;
import com.ub.techexcel.tools.MeetingServiceTools;
import com.ub.techexcel.tools.ServiceInterfaceListener;
import com.ub.techexcel.tools.ServiceInterfaceTools;
import com.ub.techexcel.tools.SpliteSocket;
import com.ub.techexcel.tools.SyncRoomDocumentPopup;
import com.ub.techexcel.tools.SyncRoomMeetingPopup;
import com.ub.techexcel.tools.SyncRoomMemberPopup;
import com.ub.techexcel.tools.SyncRoomOtherNoteListPopup;
import com.ub.techexcel.tools.SyncRoomPopup;
import com.ub.techexcel.tools.SyncRoomPropertyPopup;
import com.ub.techexcel.tools.Tools;
import com.ub.techexcel.tools.TvDevicesListPopup;
import com.ub.techexcel.tools.VideoPopup;
import com.ub.techexcel.tools.YinxiangCreatePopup;
import com.ub.techexcel.tools.YinxiangEditPopup;
import com.ub.techexcel.tools.YinxiangPopup;
import com.ub.techexcel.view.CustomVideoView;

import org.feezu.liuli.timeselector.Utils.TextUtil;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import Decoder.BASE64Encoder;
import io.agora.openlive.ui.BaseActivity;
import io.agora.rtc.internal.RtcEngineImpl;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wang on 2017/6/16.
 */
public class SyncRoomActivity extends BaseActivity implements View.OnClickListener,
        VideoGestureRelativeLayout.VideoGestureListener, YinxiangPopup.ShareDocumentToFriendListener, AddFileFromDocumentDialog.OnDocSelectedListener, AddFileFromFavoriteDialog.OnFavoriteDocSelectedListener, SyncRoomDocumentPopup.DocumentPopupEventListener {

    private String targetUrl;
    private String newPath;
    public PopupWindow chatPopupWindow;
    public PopupWindow mPopupWindow2;
    public PopupWindow mPopupWindow4;
    private WebView wv_show;
    private CustomVideoView videoView;
    private VideoGestureRelativeLayout videoGestureRelativeLayout;
    private ShowChangeLayout showChangeLayout;
    private AudioManager mAudioManager;
    private int maxVolume = 0;
    private int oldVolume = 0;
    private int newProgress = 0, oldProgress = 0;
    private BrightnessHelper mBrightnessHelper;
    private float brightness = 1;
    private Window mWindow;
    private WindowManager.LayoutParams mLayoutParams;

    private ImageView closeVideo;
    private ProgressBar mProgressBar;
    private String studentid;
    private String teacherid;
    public static String meetingId;
    private String lessonId;
    private int isInstantMeeting; // 1 新的课程   旧的课程
    private int identity = 0;
    private WebSocketClient mWebSocketClient;
    public ImageLoader imageLoader;
    private Customer teacherCustomer = new Customer();
    private PowerManager pm;
    private PowerManager.WakeLock wl;
    private ShareSyncDialog shareSyncDialog;
    int meetingType = MeetingType.SYNCROOM;

    @Override
    public void shareDocumentToFriend(SoundtrackBean soundtrackBean) {
        shareSyncDialog = new ShareSyncDialog(this);
        shareSyncDialog.setDocument(currentShowPdf);
        shareSyncDialog.setSync(soundtrackBean);
        shareSyncDialog.show();
    }

    @Override
    public void onDocSelected(String docId) {
        TeamSpaceInterfaceTools.getinstance().uploadFromSpace(AppConfig.URL_PUBLIC + "TopicAttachment/UploadFromSpace?TopicID=" + lessonId + "&itemIDs=" + docId, TeamSpaceInterfaceTools.UPLOADFROMSPACE, new TeamSpaceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                new CenterToast.Builder(SyncRoomActivity.this).setSuccess(true).setMessage("operate success").create().show();
            }
        });
    }

    @Override
    public void onFavoriteDocSelected(String docId) {
        TeamSpaceInterfaceTools.getinstance().uploadFromSpace(AppConfig.URL_PUBLIC + "TopicAttachment/UploadFromFavorite?TopicID=" + lessonId + "&itemIDs=" + docId, TeamSpaceInterfaceTools.UPLOADFROMSPACE, new TeamSpaceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                new CenterToast.Builder(SyncRoomActivity.this).setSuccess(true).setMessage("operate success").create().show();
            }
        });
    }

    @Override
    public void onDocumentPopOpen() {
        notifyDocumentPopupOpenOrClose(1);
    }

    @Override
    public void onDocumentPopClose() {
        notifyDocumentPopupOpenOrClose(0);
    }

    public enum MenberRole {
        DEFULT(-1), AUDIENCE(3);
        private int role;

        MenberRole(int role) {
            this.role = role;
        }

        public static MenberRole match(int role) {
            for (MenberRole myRole : MenberRole.values()) {
                if (myRole.role == role) {
                    return myRole;
                }
            }
            return MenberRole.DEFULT;
        }
    }

    private Customer studentCustomer = new Customer();
    private String currentPresenterId = "";
    private List<Customer> auditorList = new ArrayList<>();
    private List<Customer> teacorstudentList = new ArrayList<>(); //老师学生的集合
    private List<String> invateList = new ArrayList<>(); // 其余在meeting中的获得新加入invatelist的旁听者
    private List<String> invitedUserIds = new ArrayList<>();// join meeting  返回的未加入meeting的invate
    private String leaveUserid;
    private RelativeLayout refresh_notify_2;
    private RelativeLayout startll;
    private RelativeLayout joinvideo;
    private RelativeLayout scanll;
    private RelativeLayout testdebug;
    private RelativeLayout inviteUser;
    private LineItem currentShowPdf = new LineItem();  // 当前展示的pdf
    private List<LineItem> documentList = new ArrayList<>();
    private String currentAttachmentId;
    private String currentItemId;
    private String currentAttachmentPage;
    private File cache;
    private List<Document> myFavoriteList = new ArrayList<>();
    private List<Document> myFavoriteVideoList = new ArrayList<>();
    private ImageView menu;
    private ImageView command_active;
    private LinearLayout activte_linearlayout;
    private LinearLayout syncroomll;
    private RelativeLayout prepareStart, syncdisplaydocument, syncdisplaynote, syncdisplaymembers, syncdisplaymeeting, syncdisplaychat, syncdisplayproperty, syncdisplayshare, syncyinxiang, syncdisplayquit, prepareScanTV;
    public static boolean syncroomInstance = false;
    private TextView endtextview;
    private TextView prompt;
    private String changeNumber = "0";
    private boolean isInClassroom = false;
    private MyHandler handler;
    private int screenWidth;
    private LinearLayout llpre;
    private TextView leavepre;
    private boolean isTeamspace;
    private int currentLine = 0;
    private String zoomInfo;
    private List<LineItem> videoList = new ArrayList<>();
    private boolean isLoadPdfAgain = true;
    private int yinxiangmode = 2;
    private boolean isSyncRoom;
    private boolean ishavedefaultpage;
    private RelativeLayout filedownprogress;
    private ProgressBar fileprogress;
    private TextView progressbartv;
    private LinearLayout h5close, h5open;

    private class MyHandler extends Handler {

        private WeakReference<SyncRoomActivity> watchCourseActivity3WeakReference;

        public MyHandler(SyncRoomActivity watchCourseActivity3) {
            watchCourseActivity3WeakReference = new WeakReference<>(watchCourseActivity3);
        }

        @Override
        public void handleMessage(Message msg) {

            final SyncRoomActivity activity3 = watchCourseActivity3WeakReference.get();
            if (activity3 != null) {
                switch (msg.what) {
                    case 0x1109:
                        if (activity3.isWebViewLoadFinish) {
                            final String m = (String) msg.obj;
                            activity3.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONObject jsonObject = new JSONObject(m);
                                        String actionType;
                                        try {
                                            actionType = jsonObject.getString("actionType");
                                        } catch (JSONException e) {
                                            actionType = "";
                                            e.printStackTrace();
                                        }
                                        if (actionType.equals("8")) {
                                            String currentitemid = jsonObject.getString("itemId");
                                            String pagenumber = jsonObject.getString("pageNumber");
                                            activity3.currentAttachmentPage = pagenumber;
                                            if (activity3.currentItemId.equals(currentitemid)) {  //同一文档
                                                if (pagenumber.equals(activity3.currentAttachmentPage)) {
                                                } else {
                                                    activity3.currentAttachmentPage = pagenumber;
                                                    String changpage = "{\"type\":2,\"page\":" + activity3.currentAttachmentPage + "}";
                                                    activity3.wv_show.loadUrl("javascript:PlayActionByTxt('" + changpage + "','" + 1 + "')", null);
                                                }
                                            } else {
                                                for (int i = 0; i < activity3.documentList.size(); i++) {
                                                    LineItem lineItem2 = activity3.documentList.get(i);
                                                    if ((currentitemid).equals(lineItem2.getItemId())) {
                                                        activity3.currentShowPdf = lineItem2;
                                                        activity3.currentAttachmentPage = pagenumber;
                                                        break;
                                                    }
                                                }
                                                Log.e("PlayActionByTxt", "不同文档");
                                                activity3.changedocumentlabel(activity3.currentShowPdf);
                                            }
                                        } else {
                                            Log.e("PlayActionByTxt", "正常  " + m);
                                            int type = 34;  //鼠标移动接受的消息
                                            try {
                                                type = jsonObject.getInt("type");
                                            } catch (JSONException e) {
                                                type = 34;
                                                e.printStackTrace();
                                            }
                                            if (type != 34) {
                                                activity3.wv_show.loadUrl("javascript:PlayActionByTxt('" + m + "','" + 1 + "')", null);
                                                activity3.zoomInfo = null;
                                            }
                                        }
                                        if (jsonObject.getString("type").equals("2")) {
                                            int page = jsonObject.getInt("page");
                                            if (!(page + "").equals(activity3.currentAttachmentPage)) {
//                                                activity3.reflectPage(page+"");
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                        break;
                    case 0x5001:
                        final String m3 = (String) msg.obj;
                        activity3.wv_show.loadUrl("javascript:PlayActionByTxt('" + m3 + "','" + 1 + "')", null);
                        activity3.zoomInfo = null;
                        break;
                    case AppConfig.SUCCESS:
                        activity3.mProgressBar.setVisibility(View.GONE);
                        activity3.getSyncRoomDetail();
                        break;
                    case 0x3102:
                        activity3.mProgressBar.setVisibility(View.GONE);
                        activity3.getAllData((List<Customer>) msg.obj);
                        break;
                    case AppConfig.SUCCESS2:
                        activity3.mProgressBar.setVisibility(View.GONE);
                        activity3.getAllData((List<Customer>) msg.obj);
                        break;
                    case 0x1110:  //收到改变presenter的socket
                        for (int i = 0; i < activity3.teacorstudentList.size(); i++) {
                            Customer customer = activity3.teacorstudentList.get(i);
                            //判断当前的presenter
                            if (customer.getUserID().equals(activity3.currentPresenterId)) {
                                customer.setPresenter(true);
                                activity3.studentCustomer = customer;
                            } else {
                                customer.setPresenter(false);
                            }
                        }
                        activity3.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (activity3.currentPresenterId.equals(AppConfig.UserID)) {
                                    activity3.wv_show.loadUrl("javascript:ShowToolbar(" + true + ")", null);
                                    activity3.wv_show.loadUrl("javascript:Record()", null);
                                } else {
                                    activity3.wv_show.loadUrl("javascript:ShowToolbar(" + false + ")", null);
                                    activity3.wv_show.loadUrl("javascript:StopRecord()", null);
                                }
                            }
                        });
                        activity3.videoPopuP.setPresenter(activity3.identity,
                                activity3.currentPresenterId,
                                activity3.studentCustomer, activity3.teacherCustomer);
                        if (activity3.isHavePresenter()) {
                            activity3.findViewById(R.id.videoline).setVisibility(View.VISIBLE);
                        } else {
                            activity3.findViewById(R.id.videoline).setVisibility(View.GONE);
                        }
                        break;
                    case 0x1121:  //invate  to  meeting
                        List<String> ll = (List<String>) msg.obj;
                        activity3.getDetailInfo(ll, 1);
                        break;
                    case 0x4010:
                        final String ddd = (String) msg.obj;
                        activity3.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (activity3.wv_show != null) {
                                    if (!TextUtil.isEmpty(ddd)) {
                                        activity3.wv_show.loadUrl("javascript:PlayActionByArray(" + ddd + "," + 0 + ")", null);
                                    }
                                }
                            }
                        });
                        break;
                    case 0x4001:
                        String prepareMode = (String) msg.obj;
                        int pre = Integer.parseInt(prepareMode);
                        if (pre == 1) {
                            activity3.llpre.setVisibility(View.VISIBLE);
                        } else if (pre == 0) {
                            activity3.llpre.setVisibility(View.GONE);
                        }
                        break;

                    case 0x1190:
                        activity3.detectPopwindow((int) msg.obj);
                        break;
                    case 0x2105:  //join meeting 走进来的
                        activity3.videoPopuP.setVideoList(activity3.videoList);
                        break;
                    case 0x6115: //copy file finish
                        String s = (String) msg.obj;
                        if (activity3.wv_show != null) {
                            activity3.wv_show.loadUrl(s, null);
                            activity3.wv_show.loadUrl("javascript:Record()", null);
                            activity3.userSetting();
                        }
                        break;

                }
                super.handleMessage(msg);
            }
        }
    }

    /**
     * @param lineItem
     * @param pagenumber
     * @param docType    切换文档 增加 docType//0:doc 1: note
     */
    private void notifySwitchDocumentSocket(LineItem lineItem, String pagenumber, int docType) {
        JSONObject json = new JSONObject();
        try {
            json.put("actionType", 8);
            json.put("eventID", lineItem.getEventID());
            json.put("attachmentUrl", lineItem.getUrl());
            json.put("eventName", lineItem.getEventName());
            json.put("meetingID", meetingId);
            json.put("itemId", lineItem.getItemId());
            json.put("incidentID", meetingId);
            json.put("pageNumber", pagenumber);
            json.put("docType", docType);
            json.put("isH5", lineItem.isHtml5());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("dsasa 切换文档", " " + lineItem.getItemId() + json.toString());
        send_message("SEND_MESSAGE", AppConfig.UserToken, 0, "", Tools.getBase64(json.toString()).replaceAll("[\\s*\t\n\r]", ""));
        updatecurrentdocumentid(lineItem);
    }

    /**
     * 老师第一次join时，调一下这个方法，将当前文档ID发给鹏飞
     */
    private void updatecurrentdocumentid(LineItem lineItem) {
        if (isTeamspace) {
            try {
                JSONObject loginjson = new JSONObject();
                loginjson.put("action", "UPDATE_CURRENT_ATTACHMENT_ID");
                loginjson.put("sessionId", AppConfig.UserToken);
                loginjson.put("documentId", lineItem.getAttachmentID());
                String ss = loginjson.toString();
                SpliteSocket.sendMesageBySocket(ss);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                JSONObject loginjson = new JSONObject();
                loginjson.put("action", "UPDATE_CURRENT_DOCUMENT_ID");
                loginjson.put("sessionId", AppConfig.UserToken);
                loginjson.put("documentId", lineItem.getItemId());
                String ss = loginjson.toString();
                SpliteSocket.sendMesageBySocket(ss);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void changedocumentlabel(LineItem lineItem) {
        if (documentList.size() > 0) {
            if (lineItem == null || TextUtils.isEmpty(lineItem.getItemId()) || lineItem.getItemId().equals("0")) {
                lineItem = documentList.get(0);
                lineItem.setSelect(true);
            } else {
                for (int i = 0; i < documentList.size(); i++) {
                    LineItem lineItem1 = documentList.get(i);
                    if (lineItem.getItemId().equals(lineItem1.getItemId())) {
                        lineItem1.setSelect(true);
                        lineItem = lineItem1;
                    } else {
                        lineItem1.setSelect(false);
                    }
                }
            }
            currentShowPdf = lineItem;
            currentAttachmentId = lineItem.getAttachmentID();
            localFileID = "";
            currentItemId = lineItem.getItemId();
            targetUrl = lineItem.getUrl();
            newPath = lineItem.getNewPath();
            Log.e("dddddd", currentAttachmentId + "  " + currentItemId + "  " + targetUrl + "  " + newPath);
            closenote.setVisibility(View.GONE);
            loadWebIndex();
            if (isHavePresenter()) {
                notifySwitchDocumentSocket(lineItem, currentAttachmentPage, lineItem.getDocType());
            }
        }
    }

    private void followChangeFile(LineItem lineItem, String pagenumber) {
        if (lineItem.getDocType() == 1) {
            String noteid = lineItem.getItemId();
            String url = AppConfig.URL_PUBLIC + "DocumentNote/Item?noteID=" + noteid;
            ServiceInterfaceTools.getinstance().getNoteByNoteId(url, ServiceInterfaceTools.GETNOTEBYNOTEID, new ServiceInterfaceListener() {
                @Override
                public void getServiceReturnData(Object object) {
                    Note note = (Note) object;
                    displayNoteTv(note);
                }
            });
        } else {
            if (documentList.size() > 0) {
                if (lineItem == null || TextUtils.isEmpty(lineItem.getItemId()) || lineItem.getItemId().equals("0")) {
                    lineItem = documentList.get(0);
                    lineItem.setSelect(true);
                } else {
                    for (int i = 0; i < documentList.size(); i++) {
                        LineItem lineItem1 = documentList.get(i);
                        if (lineItem.getItemId().equals(lineItem1.getItemId())) {
                            lineItem1.setSelect(true);
                            lineItem = lineItem1;
                        } else {
                            lineItem1.setSelect(false);
                        }
                    }
                }


                prevItemId = null;
                currentShowPdf = lineItem;
                currentShowPdf.setPageNumber(pagenumber);
                currentAttachmentId = lineItem.getAttachmentID();
                localFileID = "";
                currentItemId = lineItem.getItemId();
                targetUrl = lineItem.getUrl();
                newPath = lineItem.getNewPath();
                Log.e("dddddd", currentAttachmentId + "  " + currentItemId + "  " + targetUrl + "  " + newPath);
                closenote.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(targetUrl)) {
                    loadWebIndex();
                }
            }

        }

    }


    public void CheckLanguage() {
        if (AppConfig.LANGUAGEID > 0) {
            switch (AppConfig.LANGUAGEID) {
                case 1:
                    StartUbao.updateLange(this, Locale.ENGLISH);
                    break;
                case 2:
                    StartUbao.updateLange(this, Locale.SIMPLIFIED_CHINESE);
                    break;
                default:
                    break;
            }
        }
    }


    private TouchHelper touchHelper;

    @SuppressLint("ClickableViewAccessibility")
    private void initWeb() {

        if (DeviceManager.getDeviceType(this) == SupportDevice.BOOK) {
//            Toast.makeText(this, "book", Toast.LENGTH_LONG).show();
            wv_show = (WebView) findViewById(R.id.wv_show);
//            EpdController.setWebViewContrastOptimize(wv_show, true);
            EpdController.setScreenHandWritingPenState(wv_show, 1);
            touchHelper = TouchHelper.create(wv_show, callback);
//            wv_show.getSettings().setDomStorageEnabled(true);
            wv_show.addJavascriptInterface(SyncRoomActivity.this, "AnalyticsWebInterface");
            wv_show.getSettings().setJavaScriptEnabled(true);
            wv_show.post(new Runnable() {
                @Override
                public void run() {
                    getRectList();
                    initTouchHelper();
                }
            });

        } else {
//            Toast.makeText(this, "app", Toast.LENGTH_LONG).show();
            wv_show = (WebView) findViewById(R.id.wv_show);
//            wv_show.setZOrderOnTop(false);
//            wv_show.getSettings().setDomStorageEnabled(true);
            wv_show.addJavascriptInterface(SyncRoomActivity.this, "AnalyticsWebInterface");
//            XWalkPreferences.setValue("enable-javascript", true);
//            XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
//            XWalkPreferences.setValue(XWalkPreferences.JAVASCRIPT_CAN_OPEN_WINDOW, true);
//            XWalkPreferences.setValue(XWalkPreferences.SUPPORT_MULTIPLE_WINDOWS, true);
        }


    }


    private List<Rect> exclude = new ArrayList<>();

    private void getRectList() {
        exclude = new ArrayList<>();
        exclude.add(getRelativeRect(wv_show, command_active));
        exclude.add(getRelativeRect(wv_show, menu));
        exclude.add(getRelativeRect(wv_show, h5close));
        exclude.add(getRelativeRect(wv_show, h5open));
    }

    private void initTouchHelper() {
        Rect limit = new Rect();
        wv_show.getLocalVisibleRect(limit);
        touchHelper.setStrokeWidth(3.0f)
                .setLimitRect(limit, exclude)
                .openRawDrawing();
        touchHelper.setStrokeStyle(TouchHelper.STROKE_STYLE_PENCIL);
    }


    /**
     * 当前正在映射状态
     */
    private void refreshTouchHelper() {
        if (DeviceManager.getDeviceType(this) == SupportDevice.BOOK) {
            if (touchHelper.isRawDrawingRenderEnabled()) {
                touchHelper.closeRawDrawing();
                initTouchHelper();
                touchHelper.setRawDrawingEnabled(false);
                Log.e("refreshTouchHelper", "refreshTouchHelper");
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        touchHelper.setRawDrawingEnabled(true);
                    }
                }, 500);
            }
        }
    }
    private void  closeTouchHelper(){
        if (DeviceManager.getDeviceType(this) == SupportDevice.BOOK) {
            if (touchHelper.isRawDrawingRenderEnabled()) {
                touchHelper.closeRawDrawing();
                touchHelper.setRawDrawingEnabled(false);
                Log.e("refreshTouchHelper", "refreshTouchHelper");
            }
        }
    }

    public Rect getRelativeRect(final View parentView, final View childView) {
        int[] parent = new int[2];
        int[] child = new int[2];
        parentView.getLocationOnScreen(parent);
        childView.getLocationOnScreen(child);
        Rect rect = new Rect();
        childView.getLocalVisibleRect(rect);
        rect.offset(child[0] - parent[0], child[1] - parent[1]);
        return rect;
    }

    private final String TAG = "hhahhahah";

    private RawInputCallback callback = new RawInputCallback() {
        @Override
        public void onBeginRawDrawing(boolean b, TouchPoint touchPoint) {
            Log.d(TAG, "onBeginRawDrawing");
            Log.d(TAG, touchPoint.getX() + ", " + touchPoint.getY());
//            TouchUtils.disableFingerTouch(getApplicationContext());
        }

        @Override
        public void onRawDrawingTouchPointMoveReceived(TouchPoint touchPoint) {
            Log.d(TAG, "onRawDrawingTouchPointMoveReceived");
            Log.d(TAG, touchPoint.getX() + ", " + touchPoint.getY());
        }

        @Override
        public void onRawDrawingTouchPointListReceived(TouchPointList touchPointList) {
            Log.d(TAG, "onRawDrawingTouchPointListReceived");
        }

        @Override
        public void onEndRawDrawing(boolean b, TouchPoint touchPoint) {
            Log.d(TAG, "onEndRawDrawing");
//            TouchUtils.enableFingerTouch(getApplicationContext());
        }

        //察除
        @Override
        public void onBeginRawErasing(boolean b, TouchPoint touchPoint) {
            Log.d(TAG, "onBeginRawErasing");
        }

        @Override
        public void onEndRawErasing(boolean b, TouchPoint touchPoint) {
            Log.d(TAG, "onEndRawErasing");
        }

        @Override
        public void onRawErasingTouchPointMoveReceived(TouchPoint touchPoint) {
            Log.d(TAG, "onRawErasingTouchPointMoveReceived");
        }

        @Override
        public void onRawErasingTouchPointListReceived(TouchPointList touchPointList) {
            Log.d(TAG, "onRawErasingTouchPointListReceived");
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CheckLanguage();
//        MainActivity.IsInSyncRoom  = true;
        setContentView(R.layout.syncroomactivity2);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }
        WindowManager wm = (WindowManager)
                getSystemService(WINDOW_SERVICE);
        EventBus.getDefault().register(this);
        EventBus.getDefault().post(new EventSyncRoom());
        screenWidth = wm.getDefaultDisplay().getWidth();
        handler = new MyHandler(this);
        syncroomInstance = true;
        AppConfig.IsInMeeting = false;


        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK, "TEST");
        wl.acquire();
        teacherid = getIntent().getStringExtra("teacherid");
        studentid = getIntent().getStringExtra("userid");
        identity = getIntent().getIntExtra("identity", 0);
        meetingId = getIntent().getStringExtra("meetingId").toUpperCase();

        isSyncRoom = getIntent().getBooleanExtra("isPrepare", false);
        isInstantMeeting = getIntent().getIntExtra("isInstantMeeting", 1);
        lessonId = getIntent().getStringExtra("lessionId");
        yinxiangmode = getIntent().getIntExtra("yinxiangmode", 2);
        isInClassroom = getIntent().getBooleanExtra("isInClassroom", false);
        ishavedefaultpage = getIntent().getBooleanExtra("ishavedefaultpage", false);
        isTeamspace = getIntent().getBooleanExtra("isTeamspace", false);
        spaceId = getIntent().getIntExtra("spaceId", 0);
        Log.e("-------", targetUrl + "  " + "  meetingId: " + meetingId + "  identity : " + identity + "    isInstantMeeting :" + isInstantMeeting + "   lession: " + lessonId);
        if (meetingId.contains(",")) {
            isTeamspace = true;
            lessonId = meetingId.substring(0, meetingId.lastIndexOf(","));
        }
        imageLoader = new ImageLoader(this);
        cache = new File(Environment.getExternalStorageDirectory(), "Image");
        if (!cache.exists()) {
            cache.mkdirs();
        }
        SpliteSocket.init(getApplicationContext());
        initView();

        initWeb();
        //初始化会话弹窗
        initChatAudienceList();

        initVideoPopup();
        openSaveVideoPopup();
        ((App) getApplication()).initWorkerThread();
        initUIandEvent();

        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("com.cn.getGroupbroadcastReceiver");
        registerReceiver(getGroupbroadcastReceiver, intentFilter2);

        //注册广播 接受socket信息
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.cn.socket");
        registerReceiver(broadcastReceiver, intentFilter);
        initdefault();
        initBroadcastReceiver();

    }

    private boolean isRegistered = false;

    private NetWorkChangReceiver netWorkChangReceiver;

    private void initBroadcastReceiver() {
        //注册网络状态监听广播
        netWorkChangReceiver = new NetWorkChangReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkChangReceiver, filter);
        isRegistered = true;
    }

    private ConnectionChangedListener connectionChangedListener = new ConnectionChangedListener();

    private class ConnectionChangedListener implements ConnectionClassManager.ConnectionClassStateChangeListener {
        @Override
        public void onBandwidthStateChange(ConnectionQuality connectionQuality) {
            if (connectionQuality == ConnectionQuality.POOR || connectionQuality == ConnectionQuality.UNKNOWN) {
                poornetworkll.setVisibility(View.VISIBLE);
                if (isHavePresenter()) {
                    poornetworktv.setVisibility(View.VISIBLE);
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        poornetworktv.setVisibility(View.GONE);
                    }
                }, 2000);
            } else {
                poornetworkll.setVisibility(View.GONE);
            }
        }
    }


    private void initdefault() {
        mWebSocketClient = AppConfig.webSocketClient;
        if (mWebSocketClient == null) {
            return;
        }
        if (mWebSocketClient.getReadyState() == WebSocket.READYSTATE.OPEN) {
            if (identity == 2) {
                sendStringBySocket2("JOIN_MEETING", AppConfig.UserToken, "", meetingId, "", true, "v20140605.0", false, identity, isInstantMeeting);
            } else {
                getOnstageMemberCount(meetingId);
            }
        }
    }


    /**
     * 获取选中的旁听者
     */
    @Override
    protected void onResume() {
        syncroomInstance = true;
        ConnectionClassManager.getInstance().register(connectionChangedListener);

        if (AppConfig.isUpdateAuditor) {
            for (Customer c : AppConfig.auditorList) {
                c.setEnterMeeting(false);
            }
            for (Customer d : AppConfig.auditorList) {
                boolean isex = false;
                for (Customer c : teacorstudentList) {
                    if (d.getUserID().equals(c.getUserID())) {
                        isex = true;
                    }
                }
                if (!isex) {
                    teacorstudentList.add(d);
                }
            }
            if (isTeamspace) {
                inviteToSyncRoom(AppConfig.auditorList);
            }
            AppConfig.isUpdateAuditor = false;
            invite_meeting(AppConfig.auditorList);
            AppConfig.auditorList.clear();
        }
        if (AppConfig.ISSUCCESS) {
            AppConfig.ISSUCCESS = false;
            LineItem attachmentBean = new LineItem();
            attachmentBean.setUrl(AppConfig.CURRENT_VALUES); //文件的路径
            attachmentBean.setFileName(AppConfig.CURRENT_VALUES
                    .substring(AppConfig.CURRENT_VALUES.lastIndexOf("/") + 1)); // 文件名
            AppConfig.CURRENT_VALUES = null;
            uploadFile(attachmentBean);
        }
        if (wv_show != null) {
            wv_show.resumeTimers();
//            wv_show.onShow();
        }

        super.onResume();
    }

    private void inviteToSyncRoom(final List<Customer> teacorstudentList2) {
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("LessonID", lessonId);
                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < teacorstudentList2.size(); i++) {
                        Customer customer = teacorstudentList2.get(i);
                        JSONObject jsonObject1 = new JSONObject();
                        jsonObject1.put("MemberID", customer.getUserID());
                        jsonObject1.put("Role", 3);
                        jsonArray.put(jsonObject1);
                    }
                    jsonObject.put("MemberList", jsonArray);
                    JSONObject rtjsonObject = ConnectService.submitDataByJson(AppConfig.URL_PUBLIC + "LessonMember/InviteToSyncRoom", jsonObject);
                    Log.e("ddd", jsonObject.toString() + "    " + rtjsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start(((App) getApplication()).getThreadMgr());
    }


    private List<Document> uploadFavorList = new ArrayList<>();


    /**
     * 邀请旁听者
     *
     * @param list
     */
    private void invite_meeting(List<Customer> list) {
        String meetings = "";
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                meetings += list.get(i).getUserID();
            } else {
                meetings += "," + list.get(i).getUserID();
            }
        }
        try {
            JSONObject loginjson = new JSONObject();
            loginjson.put("action", "INVITE_TO_MEETING");
            loginjson.put("sessionId", AppConfig.UserToken);
            loginjson.put("meetingId", meetingId);
            loginjson.put("userIds", meetings);
            String ss = loginjson.toString();
            SpliteSocket.sendMesageBySocket(ss);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject json = new JSONObject();
        try {
            json.put("meetingID", meetingId);
            json.put("sourceID", teacherid);
            json.put("targetID", studentid);
            json.put("incidentID", "");
            json.put("roleType", "1");
            json.put("attachmentUrl", targetUrl);
            json.put("actionType", 10);
            json.put("isH5", false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("send_mesage", meetings + "   " + json.toString());
        send_message("SEND_MESSAGE", AppConfig.UserToken, 1, meetings, Tools.getBase64(json.toString()).replaceAll("[\\s*\t\n\r]", ""));
    }


    private void changeVideo1(int i, String currentSessionID) {
        JSONObject json = new JSONObject();
        try {
            json.put("videoMode", i);
            json.put("actionType", 9);
            if (i == 2) {
                json.put("currentSessionID", currentSessionID);
            } else {
                json.put("currentSessionID", "null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        send_message("SEND_MESSAGE", AppConfig.UserToken, 0, "", Tools.getBase64(json.toString()).replaceAll("[\\s*\t\n\r]", ""));
        send_message("SEND_MESSAGE", AppConfig.UserToken, 1, AppConfig.UserID, Tools.getBase64(json.toString()).replaceAll("[\\s*\t\n\r]", ""));
    }


    private int startYinxiangTime = 0;
    private String prevItemId;
    private String prevAttachmentPage;
    private boolean isJoinNote = false;

    private void doJOIN_MEETING(String msg) {
        try {
            JSONObject jsonObject = new JSONObject(msg);
            changeNumber = jsonObject.getString("changeNumber");
            JSONObject retdata = jsonObject.getJSONObject("retData");

//            JSONArray jsonArray = retdata.getJSONArray("usersList");
//            List<Customer> joinlist = Tools.getUserListByJoinMeeting(jsonArray);

            if (retdata.has("type")) {
                meetingType = retdata.getInt("type");
            }

            msg = retdata.toString();
            String currentLine2 = getRetCodeByReturnData2("currentLine", msg);
            currentLine = Integer.parseInt(currentLine2);
            if (isTeamspace) {
                command_active.setVisibility(View.GONE);
            } else {
            }
            invitedUserIds.clear();
            String ids = getRetCodeByReturnData2("invitedUserIds", msg);
            if (!TextUtils.isEmpty(ids)) {
                String idss[] = ids.split(",");
                for (String s : idss) {
                    invitedUserIds.add(s);
                }
            }
            try {
                String videoStatus2 = getRetCodeByReturnData2("videoStatus", msg);
                zoomInfo = getRetCodeByReturnData2("zoomInfo", msg);
                zoomInfo = zoomInfo.replaceAll("'", "\"");
                Log.e("miao1", zoomInfo);
                zoomInfo = zoomInfo.replaceAll("'", "\"");
                Log.e("miao2", zoomInfo);
                changeNumber2();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            String currentpage = getRetCodeByReturnData2("CurrentDocumentPage", msg);
            String[] page = currentpage.split("-");
            currentItemId = page[0];
            currentAttachmentPage = page[1];

            String prevpage = getRetCodeByReturnData2("prevDocInfo", msg);
            if (TextUtils.isEmpty(prevpage)) {
                isJoinNote = false;
            } else {
                String[] prevpages = prevpage.split("-");
                prevItemId = prevpages[0];
                prevAttachmentPage = prevpages[1];
                prevAttachmentPage = (int) Float.parseFloat(prevAttachmentPage) + "";
                if (currentItemId.equals(prevItemId)) {
                    isJoinNote = false;
                } else {
                    isJoinNote = true;
                }
            }

            Message message1 = Message.obtain();
//            message1.obj = joinlist;
            message1.what = AppConfig.SUCCESS;
            handler.sendMessage(message1);

            String prepareMode = getRetCodeByReturnData2("prepareMode", msg);
            Message msg2 = Message.obtain();
            msg2.obj = prepareMode;
            msg2.what = 0x4001;
            handler.sendMessage(msg2);
            //播放音频
            String playAudioData = retdata.getString("playAudioData");
            String msg3 = Tools.getFromBase64(playAudioData);
            if (!TextUtils.isEmpty(msg3)) {
                JSONObject ms3 = new JSONObject(msg3);
                int actionType = ms3.getInt("actionType");
                if (actionType == 23) {
                    int soundtrackId = ms3.getInt("soundtrackId");
                    int stat = ms3.getInt("stat");
                    if (stat == 1 || stat == 4) {
                        getAudioAction(soundtrackId, 0);
                        getyinxiangdetail(soundtrackId);
                        startYinxiangTime = ms3.getInt("time");
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void doLEAVE_MEETING(String msg) {
        String retdate = getRetCodeByReturnData2("retData", msg);
        try {
            leaveUserid = new JSONObject(retdate).getString("userId");
            Message msg2 = Message.obtain();
            msg2.what = 0x1111;
            handler.sendMessage(msg2);
            if (identity == 2) { // 老师将权限拿过来
                if (!leaveUserid.equals(teacherCustomer.getUserID())) {
                    if (currentPresenterId.equals(leaveUserid)) {  // 离开的学生是presenter
                        //设置老师为presenter
                        sendStringBySocket3("MAKE_PRESENTER", teacherCustomer.getUsertoken(), meetingId, teacherCustomer.getUsertoken());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private LinearLayout poornetworkll;
    private TextView poornetworktv;
    private int audioTime = 0;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String message = intent.getStringExtra("message");

            if (TextUtils.isEmpty(message)) {
                return;
            }

            if (message.equals("disconnect")) {
                poornetworkll.setVisibility(View.VISIBLE);
                if (isHavePresenter()) {
                    poornetworktv.setVisibility(View.VISIBLE);
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        poornetworktv.setVisibility(View.GONE);
                    }
                }, 2000);
                return;
            } else if (message.equals("connect")) {
                poornetworkll.setVisibility(View.GONE);
                return;
            }

            if (message.equals("LEAVE_MEETING")) {
                followLeaveMeeting();
                return;
            }
            String msg = Tools.getFromBase64(message);
            String msg_action = getRetCodeByReturnData2("action", msg);
            if (msg_action.equals("JOIN_MEETING")) {
                if (getRetCodeByReturnData2("retCode", msg).equals("0")) {
                    doJOIN_MEETING(msg);
                } else if (getRetCodeByReturnData2("retCode", msg).equals("-1")) {
//                    initdefault();  // 重新 JOIN_MEETING
                }
            }
            if (msg_action.equals("USER_JOIN_MEETING_ON_OTHER_DEVICE")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.deviceprompt), Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            }
            if (msg_action.equals("OFFLINE_MODE") || msg_action.equals("ONLINE_MODE")) {
                try {
                    List<Customer> joinlist = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(msg);
                    JSONObject retdata = jsonObject.getJSONObject("retData");
                    JSONArray jsonArray = retdata.getJSONArray("usersList");
                    joinlist = Tools.getUserListByJoinMeeting(jsonArray);
                    Message message1 = Message.obtain();
                    message1.obj = joinlist;
                    message1.what = 0x3102;
                    handler.sendMessage(message1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            /**
             * 邀请学生加入声网 成功回调
             */
            if (msg_action.equals("MEETING_STATUS")) {
                if (getRetCodeByReturnData2("status", msg).equals("1")) { //打开直播
                    prompt.setVisibility(View.GONE);
                    startll.setVisibility(View.GONE);
                    prompt.setVisibility(View.GONE);
                }
            }
            /**
             * 收到升级旁听者的通知
             */
            if (msg_action.equals("PROMOTE_TO_STUDENT")) {
                String userid = getRetCodeByReturnData2("userId", msg);
                Message ms = Message.obtain();
                ms.obj = userid;
                ms.what = 0x1301;
                handler.sendMessage(ms);
            }

            /**
             * 邀请旁听者 其余在线收到的信息
             */
            if (msg_action.equals("INVITE_TO_MEETING")) {
                invateList.clear();
                String userIDS = getRetCodeByReturnData2("userIds", msg);
                if (!TextUtils.isEmpty(userIDS)) {
                    String userid[] = userIDS.split(",");
                    for (int i = 0; i < userid.length; i++) {
                        invateList.add(userid[i]);
                    }
                    Message invatemsg = Message.obtain();
                    invatemsg.obj = invateList;
                    invatemsg.what = 0x1121;
                    handler.sendMessage(invatemsg);
                }
            }
            if (msg_action.equals("NOTE_DATA")) {
                String pageIdentifier = getRetCodeByReturnData2("pageIdentifier", msg);
                String d = getRetCodeByReturnData2("data", msg);
                try {
                    final JSONObject jsonObject = new JSONObject(Tools.getFromBase64(d));
                    showDotPanData(pageIdentifier, jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            /**
             *
             * 切换文档  直播视频的切换
             */
            if (msg_action.equals("SEND_MESSAGE")) {
                String d = getRetCodeByReturnData2("data", msg);
                try {
                    final JSONObject jsonObject = new JSONObject(Tools.getFromBase64(d));
                    if (jsonObject.getInt("actionType") == 2) {  // 横竖屏切换

                    } else if (jsonObject.getInt("actionType") == 8) {
                        // 切换文档
                        Log.e("switch_file", "message:" + d);
                        LineItem lineitem = new LineItem();
                        lineitem.setUrl(jsonObject.getString("attachmentUrl"));
                        lineitem.setHtml5(jsonObject.getBoolean("isH5"));
                        lineitem.setItemId(jsonObject.getString("itemId"));
                        lineitem.setDocType(jsonObject.getInt("docType"));
                        lineitem.setPageNumber(jsonObject.getString("pageNumber"));
                        currentAttachmentPage = jsonObject.getString("pageNumber");
                        AppConfig.currentPageNumber = jsonObject.getString("pageNumber");
                        followChangeFile(lineitem, currentAttachmentPage);

                    } else if (jsonObject.getInt("actionType") == 19) {
                        final int stat = jsonObject.getInt("stat");
                        final float time = jsonObject.getInt("time");
                        final String attachmentId = jsonObject.getString("vid");
                        final String url = jsonObject.getString("url");
                        final int videotype = jsonObject.getInt("type");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startOrPauseVideo(stat, time, attachmentId, url, videotype);
                            }
                        });
                    } else if (jsonObject.getInt("actionType") == 23) {  //学生接收是否播放音频
                        Log.e("mediaplayer-----", jsonObject.toString());
                        final int stat = jsonObject.getInt("stat");
                        final String soundtrackID = jsonObject.getString("soundtrackId");
                        if (stat == 4) {
                            audioTime = jsonObject.getInt("time");
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("mediaplayer-----", stat + ":   " + (mediaPlayer == null));
                                if (stat == 1) {
                                    int vid2 = 0;
                                    if (!TextUtils.isEmpty(soundtrackID)) {
                                        vid2 = Integer.parseInt(soundtrackID);
                                    }
                                    getAudioAction(vid2, 0);
                                    getyinxiangdetail(vid2);
                                } else if (stat == 0) {
                                    StopMedia();
                                    StopMedia2();
                                    closeAudioSync();
                                } else if (stat == 2) {
                                    pauseMedia();
                                    pauseMedia2();
                                } else if (stat == 3) {
                                    resumeMedia();
                                    resumeMedia2();
                                } else if (stat == 4) {
//
                                } else if (stat == 5) {  // 拖动进度条  学生端处理
                                    seekToTime(audioTime);
                                }
                            }
                        });
                    } else if (jsonObject.getInt("actionType") == 14) { // 收到关闭自己的Audio

                    } else if (jsonObject.getInt("actionType") == 15) { //收到关闭自己的Video

                    } else if (jsonObject.getInt("actionType") == 1820) {
                        String userid = jsonObject.getString("useId");
                        if (jsonObject.getInt("stat") == 0) {
                            if (syncRoomOtherNoteListPopup != null) {
                                syncRoomOtherNoteListPopup.dismiss();
                            }
                        } else if (jsonObject.getInt("stat") == 1) {
                            if (syncRoomOtherNoteListPopup == null || !syncRoomOtherNoteListPopup.isShowing()) {
                                selectCusterId = userid;
                                openNotePopup();
                            }
                        }
                    } else if (jsonObject.getInt("actionType") == 21) {
                    } else if (jsonObject.getInt("actionType") == 24) {
                        String newMeetingid = jsonObject.getString("meetingID");
                        enterTeacherOnGoingMeeting(newMeetingid);
                    } else if (jsonObject.getInt("actionType") == 26) {
                        int deleteAttachmentId = jsonObject.getInt("attachmentId");
                        for (int i1 = 0; i1 < documentList.size(); i1++) {
                            LineItem lineItem = documentList.get(i1);
                            if (lineItem.getAttachmentID().equals(deleteAttachmentId + "")) {
                                documentList.remove(i1);
                                break;
                            }
                        }
                    } else if (jsonObject.getInt("actionType") == 27) {
                        final String userid = jsonObject.getString("userId");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadNoteWhenChangeUser(userid);
                            }
                        });

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (msg_action.equals("SEND_MESSAGE")) {
                String d = getRetCodeByReturnData2("data", msg);
                try {
                    JSONObject jsonObject = new JSONObject(Tools.getFromBase64(d));
                    if (jsonObject.getInt("actionType") == 11) {
                        lessonId = jsonObject.getString("incidentID");
                        getSyncRoomDetail();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (msg_action.equals("KLOUD_CALL_STARTED")) {
                if (getRetCodeByReturnData2("retCode", msg).equals("1")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Message invatemsg = Message.obtain();
                            invatemsg.obj = 2;
                            invatemsg.what = 0x3121;
                            handler.sendMessage(invatemsg);
                        }
                    });
                }
            }
            if (msg_action.equals("CALL_ME")) {
                if (getRetCodeByReturnData2("retCode", msg).equals("-2403")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SyncRoomActivity.this, "user exist in conference", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
            //通知上传文档了
            if (msg_action.equals("ATTACHMENT_UPLOADED")) {
                getSyncRoomDetail();
            }
            //获得课程Action
            if (msg_action.equals("BROADCAST_FRAME")) {
                String msg2 = Tools.getFromBase64(getRetCodeByReturnData2("data", msg));
                Message msg3 = Message.obtain();
                msg3.obj = msg2;
                msg3.what = 0x5001;
                handler.sendMessage(msg3);
            }
            // 1@MAKE_PRESENTER
            if (msg_action.equals("MAKE_PRESENTER")) {
                if (getRetCodeByReturnData2("retCode", msg).equals("1")) {  // 此时设置student or teacher presenter success
                    currentPresenterId = getRetCodeByReturnData2("presenterId", msg);
                    Message msg4 = Message.obtain();
                    msg4.what = 0x1110;
                    handler.sendMessage(msg4);
                    if (currentPresenterId.equals(AppConfig.UserID)) {
                        changeVideo1(0, "");
                    }
                }
            }
            //离开课程  返回的socket
            if (msg_action.equals("LEAVE_MEETING")) {  // 别人收到xx离开
                doLEAVE_MEETING(msg);
//                closeCourse(0);
            }
            //老师 结束课程  所有人离开
            if (msg_action.equals("END_MEETING")) { //所有人都收到
                finish();
            }
            if (msg_action.equals("HELLO")) {
                AppConfig.isPresenter = isHavePresenter();
                AppConfig.status = 1 + "";
                AppConfig.currentLine = currentLine;
                AppConfig.currentPageNumber = currentAttachmentPage;
                AppConfig.currentDocId = currentItemId;
                try {
                    String data = getRetCodeByReturnData2("data", msg);
                    if (!TextUtils.isEmpty(data)) {
                        JSONObject jsonObject = new JSONObject(Tools.getFromBase64(data));
                        String currentItemId2 = jsonObject.getString("currentItemId");
                        currentPresenterId = jsonObject.getString("currentPresenter");
                        String currentMode2 = jsonObject.getString("currentMode");
                        Log.e("document", "current:" + currentItemId + ",change:" + currentItemId2);
                        if (!currentPresenterId.equals(AppConfig.UserID)) {
                            String currentAttachmentPage2 = jsonObject.getString("currentPageNumber");
                            if (currentItemId2.equals("0")) {
                                return;
                            }
                            if (currentItemId2.equals(currentItemId)) {  //当前是同一个文档
                                String changpage = "{\"type\":2,\"page\":" + currentAttachmentPage2 + "}";
                                Message msg3 = Message.obtain();
                                msg3.obj = changpage;
                                msg3.what = 0x1109;
                                if (!isPlaying2) {
                                    if (currentAttachmentPage2.equals(currentAttachmentPage)) {
                                    } else {
                                        handler.sendMessage(msg3);
                                    }
                                }
                            } else {   // 切换到当前文档
                                currentItemId = currentItemId2;
                                for (int i = 0; i < documentList.size(); i++) {
                                    if (documentList.get(i).getItemId().equals(currentItemId)) {
                                        followChangeFile(documentList.get(i), "1");
                                    }
                                }
                            }
                            Message msg4 = Message.obtain();
                            msg4.obj = currentMode2;
                            msg4.what = 0x1120;
                            handler.sendMessage(msg4);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (msg_action.equals("NO_ACTIVITY_DETECTED")) {
                if (teacherCustomer.getUserID().equals(AppConfig.UserID)) {
                    Message msg3 = Message.obtain();
                    String countDown = getRetCodeByReturnData2("countDown", msg);
                    int countDown2 = TextUtils.isEmpty(countDown) ? 0 : Integer.parseInt(countDown);
                    msg3.obj = countDown2;
                    msg3.what = 0x1190;
                    handler.sendMessage(msg3);
                }
            }
        }
    };


    private void enterTeacherOnGoingMeeting(final String newMeetingid) {

        String url = AppConfig.URL_PUBLIC + "Lesson/Item?lessonID=" + newMeetingid;
        MeetingServiceTools.getInstance().enterTeacherOnGoingMeeting(url, MeetingServiceTools.ENTERTEACHERONGOINGMEETING, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                ServiceBean bean = (ServiceBean) object;
                Intent intent = new Intent(SyncRoomActivity.this, MeetingViewActivity.class);
                intent.putExtra("userid", bean.getUserId());
                intent.putExtra("meetingId", bean.getId() + "");
                intent.putExtra("teacherid", bean.getTeacherId());
                intent.putExtra("identity", bean.getRoleinlesson());
                intent.putExtra("isStartCourse", true);
                intent.putExtra("isInstantMeeting", 0);
                startActivity(intent);
                finish();
            }
        });
    }


    /**
     * 改变HELLO changenumber的值
     */
    private void changeNumber2() {
        Intent intent1 = new Intent();
        intent1.setAction("com.cn.changenumber");
        intent1.putExtra("changeNumber", changeNumber);
        sendBroadcast(intent1);
    }

    private void sendvoicenet(String action, String sessionId, String meetingId, int status, String lessionid) {
        try {
            JSONObject loginjson = new JSONObject();
            loginjson.put("action", action);
            loginjson.put("sessionId", sessionId);
            loginjson.put("meetingId", meetingId);
            loginjson.put("status", status);
            loginjson.put("lessonid", lessionid);
            String ss = loginjson.toString();
            SpliteSocket.sendMesageBySocket(ss);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 老师给学生发送上课消息
     */
    private void send_message(String action, String sessionId, int type, String userlist, String data) {
        try {
            JSONObject loginjson = new JSONObject();
            loginjson.put("action", action);
            loginjson.put("sessionId", sessionId);
            loginjson.put("type", type);
            loginjson.put("userList", userlist);
            loginjson.put("data", data);
            String ss = loginjson.toString();
            SpliteSocket.sendMesageBySocket(ss);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendStringBySocket2(String action, String sessionId, String username, String meetingId2,
                                     String itemId, boolean isPresenter, String clientVersion, boolean isLogin, int role, int isInstantMeeting) {
        try {
            JSONObject loginjson = new JSONObject();
            loginjson.put("action", action);
            loginjson.put("sessionId", sessionId);
            loginjson.put("meetingId", meetingId2);
            loginjson.put("meetingPassword", "");
            loginjson.put("role", role);
            loginjson.put("type", meetingType);
            loginjson.put("mode", 1);
            loginjson.put("isInstantMeeting", 0);
            if (isTeamspace) {
                loginjson.put("lessonId", Integer.parseInt(lessonId));
            } else {
                loginjson.put("lessonId", Integer.parseInt(lessonId));
            }
            if (isInClassroom) {
                loginjson.put("isInClassroom", 1);
                isInClassroom = false;
            }
            String ss = loginjson.toString();
            SpliteSocket.sendMesageBySocket(ss);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * MAKE_PRESENTER
     */
    private void sendStringBySocket3(String action, String sessionId, String meetingId, String newPresenterSessionId) {
        try {
            JSONObject loginjson = new JSONObject();
            loginjson.put("action", action);
            loginjson.put("sessionId", sessionId);
            loginjson.put("meetingId", meetingId);
            loginjson.put("newPresenterSessionId", newPresenterSessionId);
            String ss = loginjson.toString();
            SpliteSocket.sendMesageBySocket(ss);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获得socket返回的action
     */
    private String getRetCodeByReturnData2(String str, String returnData) {
        if (!TextUtils.isEmpty(returnData)) {
            try {
                JSONObject jsonObject = new JSONObject(returnData);
                if (jsonObject.has(str)) {
                    return jsonObject.getString(str) + "";
                } else {
                    return "000000000";
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return "000000000";
            } catch (Exception e) {
                e.printStackTrace();
                return "000000000";
            }
        } else {
            return "000000000";
        }
    }

    private LinearLayout createblabkpage, inviteattendee, sharedocument;

    private void initView() {
        puo2 = new Popupdate2();
        puo2.getPopwindow(SyncRoomActivity.this);
        prompt = (TextView) findViewById(R.id.prompt);
        closenote = findViewById(R.id.closenote);
        endtextview = (TextView) findViewById(R.id.endtextview);
        llpre = (LinearLayout) findViewById(R.id.llpre);
        leavepre = (TextView) findViewById(R.id.leavepre);
        leavepre.setOnClickListener(this);
        llpre.setOnClickListener(this);
        poornetworkll = (LinearLayout) findViewById(R.id.poornetworkll);
        poornetworktv = (TextView) findViewById(R.id.poornetworktv);
        createblabkpage = (LinearLayout) findViewById(R.id.createblabkpage);
        inviteattendee = (LinearLayout) findViewById(R.id.inviteattendee);
        sharedocument = (LinearLayout) findViewById(R.id.sharedocument);

        filedownprogress = findViewById(R.id.filedownprogress);
        fileprogress = findViewById(R.id.fileprogress);
        progressbartv = findViewById(R.id.progressbartv);
        h5open = findViewById(R.id.h5open);
        h5close = findViewById(R.id.h5close);

        findViewById(R.id.hiddenwalkview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("haha", "onclick");
                activte_linearlayout.setVisibility(View.GONE);
                findViewById(R.id.hiddenwalkview).setVisibility(View.GONE);
                menu.setImageResource(R.drawable.icon_menu);
                command_active.setImageResource(R.drawable.icon_command);
            }
        });
        //设置视频控制器
        videoView = (CustomVideoView) findViewById(R.id.videoView);
        videoView.setMediaController(new MediaController(this));
        videoView.setZOrderOnTop(true);
        videoView.setZOrderMediaOverlay(true);
        closeVideo = (ImageView) findViewById(R.id.closeVideo);
        closeVideo.setOnClickListener(this);
        videoGestureRelativeLayout = (VideoGestureRelativeLayout) findViewById(R.id.videoll);
        videoGestureRelativeLayout.setVideoGestureListener(this);
        showChangeLayout = (ShowChangeLayout) findViewById(R.id.scl);
        //初始化获取音量属性
        mAudioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //初始化亮度调节
        mBrightnessHelper = new BrightnessHelper(this);
        //下面这是设置当前APP亮度的方法配置
        mWindow = getWindow();
        mLayoutParams = mWindow.getAttributes();
        brightness = mLayoutParams.screenBrightness;

        audiosyncll = (LinearLayout) findViewById(R.id.audiosyncll);
        preparedownprogress = (RelativeLayout) findViewById(R.id.preparedownprogress);
        timeShow = (TextView) findViewById(R.id.timeshow);
        timeShow.setOnClickListener(this);
        timeHidden = (ImageView) findViewById(R.id.timehidden);
        timeHidden.setOnClickListener(this);

        menu = (ImageView) findViewById(R.id.menu);
        menu.setOnClickListener(this);
        command_active = (ImageView) findViewById(R.id.command_active);
        command_active.setOnClickListener(this);

        activte_linearlayout = (LinearLayout) findViewById(R.id.activte_linearlayout);


        syncroomll = (LinearLayout) findViewById(R.id.syncroomll);
        syncdisplaydocument = (RelativeLayout) findViewById(R.id.syncdisplaydocument);
        syncdisplaydocument.setOnClickListener(this);
        syncdisplaynote = (RelativeLayout) findViewById(R.id.syncdisplaynote);
        syncdisplaynote.setOnClickListener(this);
        syncdisplayshare = (RelativeLayout) findViewById(R.id.syncdisplayshare);
        syncdisplayshare.setOnClickListener(this);
        syncyinxiang = (RelativeLayout) findViewById(R.id.syncyinxiang);
        syncyinxiang.setOnClickListener(this);
        syncdisplaymembers = (RelativeLayout) findViewById(R.id.syncdisplaymembers);
        syncdisplaymembers.setOnClickListener(this);
        syncdisplaymeeting = (RelativeLayout) findViewById(R.id.syncdisplaymeeting);
        syncdisplaymeeting.setOnClickListener(this);
        syncdisplaychat = (RelativeLayout) findViewById(R.id.syncdisplaychat);
        syncdisplaychat.setOnClickListener(this);
        syncdisplayproperty = (RelativeLayout) findViewById(R.id.syncdisplayproperty);
        syncdisplayproperty.setOnClickListener(this);
        syncdisplayquit = (RelativeLayout) findViewById(R.id.syncdisplayquit);
        syncdisplayquit.setOnClickListener(this);


        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        mProgressBar.setVisibility(View.GONE);
        startll = (RelativeLayout) findViewById(R.id.startll);
        scanll = (RelativeLayout) findViewById(R.id.scanll);
        inviteUser = (RelativeLayout) findViewById(R.id.inviteuser);
        joinvideo = (RelativeLayout) findViewById(R.id.joinvideo);
        refresh_notify_2 = (RelativeLayout) findViewById(R.id.refresh_notify_2);
        testdebug = (RelativeLayout) findViewById(R.id.testdebug);
        refresh_notify_2.setOnClickListener(this);
        startll.setOnClickListener(this);
        joinvideo.setOnClickListener(this);
        scanll.setOnClickListener(this);
        inviteUser.setOnClickListener(this);
        testdebug.setOnClickListener(this);
        startll.setVisibility(View.GONE);
        joinvideo.setVisibility(View.GONE);

        startll.setVisibility(View.VISIBLE);
        endtextview.setText(getString(R.string.mtClose));

        prepareStart = (RelativeLayout) findViewById(R.id.prepareStart);
        prepareStart.setOnClickListener(this);

        prepareScanTV = (RelativeLayout) findViewById(R.id.prepareScanTV);
        prepareScanTV.setOnClickListener(this);

        if (identity != 2) {
//            findViewById(R.id.videoline).setVisibility(View.GONE);
        }


    }

    private Document favoriteAudio = new Document();
    private MediaPlayer mediaPlayer;
    private Timer audiotimer;  // 每个4秒 通知播放进度
    private Timer audioplaytimer;  // 进度条

    /**
     * @param url         播放背景音乐
     * @param isrecording 是否录音
     */
    private void GetMediaPlay(String url, final boolean isrecording) {
        Log.e("GetMediaPlay", url + "  " + isrecording);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (TextUtils.isEmpty(url)) {
            if (!isTeamspace) {

            }
            if (isrecording) {  // 同步录音
                startAudioRecord();  //开始录音
            }
        } else {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(url);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    if (!isTeamspace) {

                    }
                    if (isrecording) {  // 同步录音
                        startAudioRecord();  // 开始录音
                    }
                }
            });
        }
    }

    private boolean issetting = false;

    private void StopMedia() {
//        getLineAction(currentAttachmentPage, false);
        if (!isTeamspace) {

        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (audiotimer != null) {
            audiotimer.cancel();
            audiotimer = null;
        }
        if (audioplaytimer != null) {
            audioplaytimer.cancel();
            audioplaytimer = null;
        }
    }

    private void pauseMedia() {
//        getLineAction(currentAttachmentPage, false);
        isPause = true;
        playstop.setImageResource(R.drawable.video_play);
        if (!isTeamspace) {

        }
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }

    }

    private void resumeMedia() {
//        getLineAction(currentAttachmentPage, true);
        isPause = false;
        playstop.setImageResource(R.drawable.video_stop);

        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }


    private MediaPlayer mediaPlayer2;

    private void GetMediaPlay2(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (mediaPlayer2 != null) {
            mediaPlayer2.stop();
            mediaPlayer2.reset();
            mediaPlayer2.release();
            mediaPlayer2 = null;
        }
        mediaPlayer2 = new MediaPlayer();
        try {
            mediaPlayer2.setDataSource(url);
            mediaPlayer2.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer2.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer2.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });

        mediaPlayer2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (isPlaying2 && isHavePresenter()) {
                    closeAudioSync();
                    StopMedia();
                    StopMedia2();
                    sendAudioSocket(0, soundtrackID);
                }
            }
        });
    }

    private void StopMedia2() {
        if (mediaPlayer2 != null) {
            mediaPlayer2.stop();
            mediaPlayer2.reset();
            mediaPlayer2.release();
            mediaPlayer2 = null;
        }
    }

    private void pauseMedia2() {
        if (mediaPlayer2 != null) {
            mediaPlayer2.pause();
        }
    }

    private void resumeMedia2() {
        if (mediaPlayer2 != null) {
            mediaPlayer2.start();
        }
    }


    private LinearLayout audiosyncll;
    private ImageView playstop;
    private ImageView close;
    private TextView isStatus;
    private TextView audiotime;
    private ImageView syncicon;
    private SeekBar mSeekBar;

    private boolean isPause = false;
    private boolean isPlaying2;
    private boolean isChangePageNumber = false;
    private String actions = "";

    /**
     * @param isrecording 是否录制音频
     * @param isPlaying   是否播放录音
     * @param duration    录音时长
     */
    private void openAudioSync(final boolean isrecording, final boolean isPlaying, long duration) {
        Log.e("openAudioSync ", isrecording + "   " + isPlaying + "     " + duration + "");
        isPlaying2 = isPlaying;
        isPause = false;
        playstop = (ImageView) findViewById(R.id.playstop);
        playstop.setImageResource(R.drawable.video_stop);
        syncicon = (ImageView) findViewById(R.id.syncicon);
        close = (ImageView) findViewById(R.id.close);
        isStatus = (TextView) findViewById(R.id.isStatus);
        audiotime = (TextView) findViewById(R.id.audiotime);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        if (isPlaying) {
            mSeekBar.setVisibility(View.VISIBLE);
            int ss = Integer.parseInt(duration + "");
            mSeekBar.setMax(ss);
            isStatus.setText("Playing");
            syncicon.setVisibility(View.GONE);
        } else {
            mSeekBar.setVisibility(View.GONE);
            if (isrecording) {
                isStatus.setText("Recording");
                syncicon.setVisibility(View.VISIBLE);
            } else {
                isStatus.setText("Syncing");
                syncicon.setVisibility(View.GONE);
            }
        }

        if (!isHavePresenter()) {
            playstop.setEnabled(false);
            close.setEnabled(false);
            mSeekBar.setEnabled(false);
        } else {
            playstop.setEnabled(true);
            close.setEnabled(true);
            mSeekBar.setEnabled(true);
        }


        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tttime = progress;
                String time = new SimpleDateFormat("mm:ss").format(tttime);
                audiotime.setText(time);
                timeShow.setText(time);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sendAudioSocket(5, soundtrackID);  // 通知学生拖动进度条
                seekToTime(tttime);
            }
        });

        playstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPause) {
                    resumeMedia();
                    resumeMedia2();
                    if (!isrecording && isHavePresenter()) {
                        if (isPlaying) {
                            sendAudioSocket(3, soundtrackID);
                        }
                    }
                } else {
                    pauseMedia();
                    pauseMedia2();
                    if (!isrecording && isHavePresenter()) {
                        if (isPlaying) {
                            sendAudioSocket(2, soundtrackID);
                        }
                    }
                }
                if (isrecording) {
                    pauseOrStartAudioRecord();
                }
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeAudioSync();
                StopMedia();
                StopMedia2();
                if (isSync) {  // 录制音响
                    sendSync("AUDIO_SYNC", AppConfig.UserToken, 0, soundtrackID);
                }
                if (isrecording) {    // 完成录音
                    stopAudioRecord(soundtrackID);
                } else if (isHavePresenter()) { // 通知結束播放
                    if (isPlaying) {
                        sendAudioSocket(0, soundtrackID);
                    }
                }
            }
        });
        if (!isrecording && isHavePresenter()) {
            if (!isSync) {
                if (audiotimer != null) {
                    audiotimer.cancel();
                    audiotimer = null;
                }
                audiotimer = new Timer();
                audiotimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (isPlaying) {
                            sendAudioSocket(4, soundtrackID);
                        }
                    }
                }, 0, 3000);
            }
        }
        if (isSync) { // 录音想
            refreshRecord();
        } else { // 播放音想
            refreshPlayTime();
        }
        audiosyncll.setVisibility(View.VISIBLE);

        if (startYinxiangTime > 0 && isPlaying) {
            startYinxiangTime = 0;
            seekToTime(startYinxiangTime);
        }

    }

    private void seekToTime(int time) {
        tttime = time;
        getAudioAction(soundtrackID, time);
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(time);
        }
        if (mediaPlayer2 != null) {
            mediaPlayer2.seekTo(time);
        }
        if (mSeekBar != null) {
            mSeekBar.setProgress(time);
        }
        String url = AppConfig.URL_PUBLIC + "Soundtrack/PageActions?soundtrackID=" + soundtrackID + "&time=" + time;
        ServiceInterfaceTools.getinstance().getPageActions(url, ServiceInterfaceTools.GETPAGEACTIONS, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                PageActionBean pageActionBean = (PageActionBean) object;
                final String pagenumber = pageActionBean.getPageNumber();
                actions = pageActionBean.getActions();
                Log.e("pagenumber", currentAttachmentPage + "  " + pagenumber);
                if (currentAttachmentPage.equals(pagenumber)) {  //后退到当前页
                    // 3 displayDrawingLine =0 展示所有的线
                    getLineAction(currentAttachmentPage, !isPause);
                    Message msg = Message.obtain();
                    msg.what = 0x4010;
                    msg.obj = actions;
                    handler.sendMessage(msg);
                } else {  // 不同页 先进行翻頁
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            isChangePageNumber = true;
                            currentAttachmentPage = pagenumber;
                            String changpage = "{\"type\":2,\"page\":" + currentAttachmentPage + "}";
                            wv_show.loadUrl("javascript:PlayActionByTxt('" + changpage + "','" + 0 + "')", null);
                        }
                    });
                }
            }
        });
    }


    int tttime = 0;
    private TextView timeShow;
    private ImageView timeHidden;

    /**
     * 每隔100毫秒拿播放进度
     */
    private void refreshPlayTime() {
        mSeekBar.setProgress(tttime);
        tttime = 0;
        if (audioplaytimer != null) {
            audioplaytimer.cancel();
            audioplaytimer = null;
        }
        audioplaytimer = new Timer();
        audioplaytimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isPause) {
                            if (mediaPlayer2 != null) {
                                if ((mediaPlayer2.getCurrentPosition() - tttime) >= 0) {
                                    tttime = mediaPlayer2.getCurrentPosition();
                                    mSeekBar.setProgress(tttime);
                                }
                                //录音文件长度大于音想长度

                                //处理action的展示
                                newAudioActionTime(tttime);
                            }
                        }
                    }
                });
            }
        }, 0, 100);
    }

    /**
     * 每隔100毫秒拿录制进度
     */
    private void refreshRecord() {
        Log.e("refreshTime", "ddd");
        tttime = 0;
        audiotime.setText("00:00");
        if (audioplaytimer != null) {
            audioplaytimer.cancel();
            audioplaytimer = null;
        }
        audioplaytimer = new Timer();
        audioplaytimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.e("fffff", isPause + "");
                if (!isPause) {
                    tttime = tttime + 100;
                    Log.e("refreshTime", " " + tttime);
                    if (audiotime != null) {
                        final String time = new SimpleDateFormat("mm:ss").format(tttime);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                audiotime.setText(time);
                                timeShow.setText(time);
                            }
                        });
                    }
                }
            }
        }, 0, 100);

    }

    private void closeAudioSync() {
        isPlaying2 = false;
        audiosyncll.setVisibility(View.GONE);
        timeShow.setVisibility(View.GONE);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                wv_show.loadUrl("javascript:ClearPageAndAction()", null);
            }
        });
    }


    private void getAllData(List<Customer> mAttendesList) {
        auditorList.clear();
        teacorstudentList.clear();
        for (int i = 0; i < mAttendesList.size(); i++) {
            Customer a = mAttendesList.get(i);
            a.setEnterMeeting(true);
            Log.e("在meeting中的人", a.getRole() + "  " + a.getName());
            if (a.getRole() == 2) {   // 设置老师
                teacherCustomer = a;
                teacherid = teacherCustomer.getUserID();
                if (a.isPresenter()) {
                    currentPresenterId = a.getUserID();
                }
                teacorstudentList.add(a);
            } else if (a.getRole() == 1) { //设置学生信息
                if (a.isPresenter()) {
                    studentCustomer = a;
                    studentid = a.getUserID();
                    currentPresenterId = a.getUserID();
                }
                teacorstudentList.add(a);
            } else if (a.getRole() == 3) { //设置旁听者信息
                a.setEnterMeeting(true);
                auditorList.add(a);
            }
        }
    }


    public int i = 0;
    /**
     * 老师端与学生端
     * 得到学生与老师的详细信息
     * isinvite
     * 0  解析老师  学生  信息
     * 1  invate to  meeting  返回的旁听者信息
     * 2  invitedUserIds|  invitedUserIds的信息
     */
    private String userid2 = "";

    private void getDetailInfo(List<String> useridList, final int isinvite) {
        userid2 = "";
        for (int i = 0; i < useridList.size(); i++) {
            if (i == 0) {
                userid2 += useridList.get(i);
            } else {
                userid2 += "," + useridList.get(i);
            }
        }
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = ConnectService.getIncidentbyHttpGet(AppConfig.URL_PUBLIC + "User/UserListBasicInfo?userIds=" + userid2);
                formatjson(jsonObject, isinvite);
            }
        }).start(((App) getApplication()).getThreadMgr());
    }


    private void formatjson(JSONObject jsonObject, int isinvate) {
        try {
            int retCode = jsonObject.getInt("RetCode");
            switch (retCode) {
                case 0:
                    JSONArray jsarray = jsonObject.getJSONArray("RetData");
                    List<Customer> mylist = new ArrayList<>();
                    for (int j = 0; j < jsarray.length(); j++) {
                        JSONObject js = jsarray.getJSONObject(j);
                        Customer s = new Customer();
                        s.setUserID(js.getString("UserID"));
                        s.setName(js.getString("UserName"));
                        s.setUBAOUserID(js.getString("RongCloudID"));
                        s.setUrl(js.getString("AvatarUrl"));
                        mylist.add(s);
                    }
                    Message msg = Message.obtain();
                    msg.obj = mylist;
                    if (isinvate == 1) { // invate-meeting 返回的
                        msg.what = 0x1203;
                    } else if (isinvate == 2) { // join meeting  invate
                        msg.what = 0x1204;
                    }
                    handler.sendMessage(msg);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Popupdate2 puo2;

    /**
     * 加载PDF
     */
    @JavascriptInterface
    public void afterLoadPageFunction() {
        crpage = (int) Float.parseFloat(currentAttachmentPage);
        Log.e("当前文档信息", "url  " + targetUrl + "        " + crpage + "      newpath  " + newPath);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                JsonDown();
            }
        });
    }

    @JavascriptInterface
    public void userSettingChangeFunction(final String opt) {
        Log.e("userSettingChan", opt.toString() + "   ");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createOrUpdateUserSetting(opt);
            }
        });
    }


    private void createOrUpdateUserSetting(final String opt) {
        try {
            JSONObject jsonObject = new JSONObject(opt);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(jsonObject);
            ServiceInterfaceTools.getinstance().createOrUpdateUserSetting(AppConfig.URL_PUBLIC + "User/CreateOrUpdateUserSetting",
                    ServiceInterfaceTools.CREATEORUPDATEUSERSETTING,
                    jsonArray.toString(), new ServiceInterfaceListener() {
                        @Override
                        public void getServiceReturnData(Object object) {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void userSetting() {
        String url = AppConfig.URL_PUBLIC + "User/UserSettings?settingIDs=5001,5002,5003,5004,5005,5006,5007,5008,5009,5010,5011,5012";
        ServiceInterfaceTools.getinstance().userSettings(url, ServiceInterfaceTools.USERSETTINGS, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                if (object != null) {
                    Log.e("userSettingChan", object.toString() + "   ");
                    final JSONArray jsonArray = (JSONArray) object;
                    if (wv_show != null) {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                wv_show.loadUrl("javascript:SetUserSeting(" + jsonArray + ")", null);
                            }
                        }, 200);
                    }

                }
            }
        });
    }

    private void JsonDown() {
        if (TextUtils.isEmpty(newPath)) {
            newPath = targetUrl.substring(targetUrl.indexOf(".com") + 5, targetUrl.lastIndexOf("/"));
        }
        //
        ServiceInterfaceTools.getinstance().queryDocument(AppConfig.URL_LIVEDOC + "queryDocument", ServiceInterfaceTools.QUERYDOCUMENT,
                newPath, new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        String jsonstring = (String) object;
                        Log.e("当前文档信息", jsonstring);
                        uploadao = transfering2(jsonstring);
                        if (uploadao == null) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    JsonDown();
                                }
                            }, 1000);
                        } else {
                            String filename = targetUrl.substring(targetUrl.lastIndexOf("/") + 1);
                            if (1 == uploadao.getServiceProviderId()) {
                                targetUrl = "https://s3." + uploadao.getRegionName() + ".amazonaws.com/" + uploadao.getBucketName() + "/" + newPath + "/" + filename;
                            } else if (2 == uploadao.getServiceProviderId()) {
                                targetUrl = "https://" + uploadao.getBucketName() + "." + uploadao.getRegionName() + "." + "aliyuncs.com" + "/" + newPath + "/" + filename;
                            }
                            //https://peertime.oss-cn-shanghai.aliyuncs.com/P49/Attachment/D24893/3fffe932-5e52-4dbb-8376-9436a2de4dbe_1_2K.jpg
                            Log.e("当前文档信息", "url  " + targetUrl);
                            if (crpage == 0) {
                                downloadPdf(targetUrl, 1);
                            } else {
                                downloadPdf(targetUrl, crpage);
                                crpage = 0;
                            }
                        }
                    }
                });

    }


    private boolean isTemporary = false;

    private Uploadao transfering2(final String jsonstring) {
        try {
            JSONObject returnjson = new JSONObject(jsonstring);
            if (returnjson.getBoolean("Success")) {
                JSONObject data = returnjson.getJSONObject("Data");

                JSONObject bucket = data.getJSONObject("Bucket");
                Uploadao uploadao = new Uploadao();
                uploadao.setServiceProviderId(bucket.getInt("ServiceProviderId"));
                uploadao.setRegionName(bucket.getString("RegionName"));
                uploadao.setBucketName(bucket.getString("BucketName"));

                isTemporary = data.getBoolean("IsTemporary");

                return uploadao;
            }
        } catch (JSONException e) {
            return null;
        }
        return null;
    }


    private int transfering(final String jsonstring) {
        filedownprogress.setVisibility(View.VISIBLE);
        try {
            JSONObject returnjson = new JSONObject(jsonstring);
            if (returnjson.getBoolean("Success")) {
                JSONObject dataJsonArray = returnjson.getJSONObject("Data");
                JSONObject js = dataJsonArray.getJSONObject(newPath);
                int Status = js.getInt("Status");
                if (Status == 2) {  //正在转换
                    JSONObject syncDetail = js.getJSONObject("SyncDetail");
                    final int finishPercent = syncDetail.getInt("FinishPercent");
                    fileprogress.setProgress(finishPercent);
                    progressbartv.setText(finishPercent + "%   File Converting,pls wait");
                    return 2;
                } else if (Status == 1) {  //下载
                    filedownprogress.setVisibility(View.GONE);
                    return 1;
                } else {  //文件不存在
                    filedownprogress.setVisibility(View.GONE);
                    return 3;
                }
            }
        } catch (JSONException e) {
            filedownprogress.setVisibility(View.GONE);
            return 1;
        }
        return 1;
    }

    int crpage = 0;
    private String prefixPdf;
    private int pageCount = 0; //每个PDF的总页数
    private String showpdfurl;

    private int getPdfCount(String pdfurl) {
        int first = pdfurl.lastIndexOf("<");
        int last = pdfurl.lastIndexOf(">");
        Log.e("hahh------------", first + "   " + last);
        if (first == 0 || last == 0) {  // 格式错误
            return 0;
        }
        if (last > first) {
            return Integer.parseInt(pdfurl.substring(first + 1, last));
        }
        return 0;
    }


    private boolean isDisplayInCenter = false;

    private void downloadPdf(final String pdfurl, final int page) {
        // https://peertime.cn/CWDocs/P49/Attachment/D3191/test_<10>.pdf
        if (TextUtils.isEmpty(pdfurl)) {
            return;
        }
        FileUtils fileUtils = new FileUtils(SyncRoomActivity.this);
        File fileUtils1 = new File(fileUtils.getStorageDirectory());
        if (!fileUtils1.exists()) {
            fileUtils1.mkdir();
        }
        File fileUtils2 = new File(fileUtils.getStorageDirectory2());
        if (!fileUtils2.exists()) {
            fileUtils2.mkdir();
        }
        pageCount = getPdfCount(pdfurl);
        if (pageCount == 0) {
            return;
        }
        if (pageCount == 1) {
            isDisplayInCenter = true;
        } else {
            isDisplayInCenter = false;
        }

        String sourceName = pdfurl.substring(pdfurl.lastIndexOf("/") + 1); // 得到test_<10>.pdf
        String houzui = sourceName.substring(sourceName.lastIndexOf("."));
        // 得到 https://peertime.cn/CWDocs/P49/Attachment/D3191/test_
        prefixPdf = pdfurl.substring(0, pdfurl.lastIndexOf("<"));
        // 得到下载地址  https:peertime.cn/CWDocs/P49/Attachment/D3191/test_1.pdf
        String downloadurl;
        Log.e("DDDDD", screenWidth + "  ");
//        if (screenWidth > 1920) {
//            downloadurl = prefixPdf + page + "_2K" + houzui;
//        } else {
//            downloadurl = prefixPdf + page + houzui;
//        }

        downloadurl = prefixPdf + page + houzui;
        String xxx = meetingId + "_" + EncoderByMd5(targetUrl).replaceAll("/", "_") + "_";
        //保存在本地的地址
        final String fileLocalUrl = fileUtils.getStorageDirectory() + File.separator
                + xxx + page + houzui;   // --->  /ubao/19999_xxxxx_1.pdf
        final String fileLocalUrl2 = fileUtils.getStorageDirectory2() + File.separator
                + xxx + page + houzui;   // --->  /ubao2/19999_xxxxx_1.pdf
        // show pdf 地址
        showpdfurl = fileUtils.getStorageDirectory2() + File.separator + xxx + sourceName.substring(sourceName.lastIndexOf("<"));  //--->  /ubao/xxxx_<10>.pdf
        Log.e("webview-downloadPdf", "保存在本地的地址  " + fileLocalUrl2 + "   show pdf 地址  " + showpdfurl + "  downloadurl " + downloadurl);
        filedownprogress.setVisibility(View.GONE);
        if (fileUtils.isFileExists(fileLocalUrl2)) {
            Log.e("webview-downloadPdf", " 本地文件存在  show pdf 地址  " + showpdfurl + "  " + page + "  ");
            currentAttachmentPage = page + "";
            AppConfig.currentPageNumber = currentAttachmentPage;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (wv_show == null) {
                        return;
                    }
                    wv_show.loadUrl("javascript:ShowPDF('" + showpdfurl + "', " + page + ",0,'" + currentAttachmentId + "'," + isDisplayInCenter + ")", null);
                    wv_show.loadUrl("javascript:Record()", null);
                    userSetting();
                }
            });
        } else {
            if (fileUtils.isFileExists(fileLocalUrl)) {
                new File(fileLocalUrl).delete();
            }
            filedownprogress.setVisibility(View.VISIBLE);
            DownloadUtil.get().download(downloadurl, fileLocalUrl, new DownloadUtil.OnDownloadListener() {
                @Override
                public void onDownloadSuccess(int arg0) {
                    if (arg0 == 200) {
                        currentAttachmentPage = page + "";
                        AppConfig.currentPageNumber = currentAttachmentPage;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                filedownprogress.setVisibility(View.GONE);
                            }
                        });
                        new ApiTask(new Runnable() {
                            @Override
                            public void run() {
                                int retcode = Tools.copyFileSdCard(fileLocalUrl, fileLocalUrl2);
                                if (retcode == 0) {
                                    Message message = Message.obtain();
                                    message.obj = "javascript:ShowPDF('" + showpdfurl + "', " + page + ",0,'" + currentAttachmentId + "'," + isDisplayInCenter + ")";
                                    message.what = 0x6115;
                                    handler.sendMessage(message);
                                }
                            }
                        }).start(((App) getApplication()).getThreadMgr());
                    } else if (arg0 == 404) {
//                        downloadPdf(pdfurl, page);
                    }
                }

                @Override
                public void onDownloading(final int progress) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fileprogress.setProgress(progress);
                            progressbartv.setText(progress + "%   File downloading,pls wait");
                        }
                    });
                }

                @Override
                public void onDownloadFailed() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            filedownprogress.setVisibility(View.GONE);
                            Toast.makeText(SyncRoomActivity.this, " download failed", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        }
    }


    private RelativeLayout preparedownprogress;

    @JavascriptInterface
    public synchronized void preLoadFileFunction(final String url, final int currentpageNum, final boolean showLoading) {
        Log.e("webview-preLoadFile", url + "     currentpageNum   " + currentpageNum + "   showLoading    " + showLoading);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (showLoading) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            preparedownprogress.setVisibility(View.VISIBLE);
                        }
                    }, 500);
                    if (isTemporary) {  //  isTemporary=true
                        ServiceInterfaceTools.getinstance().queryDocument(AppConfig.URL_LIVEDOC + "queryDocument", ServiceInterfaceTools.QUERYDOCUMENT,
                                newPath, new ServiceInterfaceListener() {
                                    @Override
                                    public void getServiceReturnData(Object object) {
                                        String jsonstring = (String) object;
                                        Uploadao ud = transfering2(jsonstring);
                                        if (ud != null) {
                                            uploadao = ud;
                                            String filename = targetUrl.substring(targetUrl.lastIndexOf("/") + 1);
                                            if (1 == uploadao.getServiceProviderId()) {
                                                targetUrl = "https://s3." + uploadao.getRegionName() + ".amazonaws.com/" + uploadao.getBucketName() + "/" + newPath + "/" + filename;
                                            } else if (2 == uploadao.getServiceProviderId()) {
                                                targetUrl = "https://" + uploadao.getBucketName() + "." + uploadao.getRegionName() + "." + "aliyuncs.com" + "/" + newPath + "/" + filename;
                                            }
                                            prefixPdf = targetUrl.substring(0, targetUrl.lastIndexOf("<"));
                                        }
                                    }
                                });
                    }

                    DownloadUtil.get().cancelAll();
                    downEveryOnePdf(url, currentpageNum);
                } else {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            preparedownprogress.setVisibility(View.GONE);
                        }
                    }, 500);
                    downEveryOnePdf(url, currentpageNum);
                }
            }
        });
    }


    private synchronized void downEveryOnePdf(final String url, final int currentpageNum) {
        Log.e("downEveryOnePdf1", url);
        if (currentpageNum <= pageCount && currentpageNum >= 0) {
            final String fileurl2 = url.substring(0, url.lastIndexOf("<")) + currentpageNum + url.substring(url.lastIndexOf("."));
            String fileurl = url.substring(0, url.lastIndexOf("<")) + currentpageNum + url.substring(url.lastIndexOf("."));
            fileurl = fileurl.replace("ubao2", "ubao");
            final FileUtils fileUtils = new FileUtils(SyncRoomActivity.this);
            Log.e("downEveryOnePdf2", fileurl2 + "  " + fileurl);
            if (fileUtils.isFileExists(fileurl2)) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        preparedownprogress.setVisibility(View.GONE);
                    }
                }, 500);
                afterDownloadFile(url, currentpageNum);
            } else {
                if (fileUtils.isFileExists(fileurl)) {
                    new File(fileurl).delete();
                }
//                String downloadurl = prefixPdf + currentpageNum + "_2K" + url.substring(url.lastIndexOf("."));
                String downloadurl = prefixPdf + currentpageNum + url.substring(url.lastIndexOf("."));
                final String finalFileurl = fileurl;
                Log.e("downEveryOnePdf3", downloadurl);
                //开始新的下载
                DownloadUtil.get().download(downloadurl, fileurl, new DownloadUtil.OnDownloadListener() {
                    @Override
                    public void onDownloadSuccess(int arg0) {
                        final String fileurl2 = finalFileurl.replace("ubao", "ubao2");
                        Log.e("downEveryOnePdf4", "success   " + fileurl2);
                        new ApiTask(new Runnable() {
                            @Override
                            public void run() {
                                int retcode = Tools.copyFileSdCard(finalFileurl, fileurl2);
                                if (retcode == 0) {
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            preparedownprogress.setVisibility(View.GONE);
                                        }
                                    }, 500);
                                    Log.e("downEveryOnePdf4", "copy  success  ");
                                    afterDownloadFile(url, currentpageNum);
                                }
                            }
                        }).start(((App) getApplication()).getThreadMgr());
                    }

                    @Override
                    public void onDownloading(final int progress) {

                    }

                    @Override
                    public void onDownloadFailed() {
                        Log.e("downEveryOnePdf4", "failure   " + fileurl2);
//                        downEveryOnePdf(url, currentpageNum);
                    }
                });
            }
        }
    }

    @JavascriptInterface
    public void showErrorFunction(final String error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject(error);
                    int code = jsonObject.getInt("Code");
                    if (code == 1) {
                        Toast.makeText(SyncRoomActivity.this, "Not Current Page", Toast.LENGTH_LONG).show();
                    } else if (code == 2) {
                        Toast.makeText(SyncRoomActivity.this, "Not Support", Toast.LENGTH_LONG).show();
                    } else if (code == 3) {
                        Toast.makeText(SyncRoomActivity.this, "Not Current Document ", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void afterDownloadFile(final String url, final int currentpageNum) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(url) && wv_show != null) {
                    wv_show.loadUrl("javascript:AfterDownloadFile('" + url + "', " + currentpageNum + ")", null);
                }
            }
        });
    }


    /**
     * 获取每一页上的 Action
     */
    @JavascriptInterface
    public void afterChangePageFunction(final String pageNum, int type) {
        Log.e("webview-afterChangePage", pageNum + "  " + type);
        refreshTouchHelper();
        currentAttachmentPage = pageNum + "";
        AppConfig.currentPageNumber = currentAttachmentPage;
        String url;
        url = AppConfig.URL_PUBLIC + "PageObject/GetPageObjects?lessonID=" + lessonId + "&itemID=" + currentItemId +
                "&pageNumber=" + pageNum;
        MeetingServiceTools.getInstance().getGetPageObjects(url, MeetingServiceTools.GETGETPAGEOBJECTS, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                final String ddd = (String) object;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (wv_show != null) {
                            Log.e("dsdsasdada", ddd);
                            if (!TextUtil.isEmpty(ddd)) {
                                wv_show.loadUrl("javascript:PlayActionByArray(" + ddd + "," + 0 + ")", null);
                            }
                            if (isPlaying2) {
                                wv_show.loadUrl("javascript:ClearPageAndAction()", null);
                            }
                        }
                    }
                });
            }
        });

        if (closenote.getVisibility() == View.GONE) {
            ServiceInterfaceTools.getinstance().getNoteListV2(AppConfig.URL_PUBLIC + "DocumentNote/List?syncRoomID=" + 0 + "&documentItemID=" + currentAttachmentId + "&pageNumber=" + currentAttachmentPage + "&userID=" + AppConfig.UserID, ServiceInterfaceTools.GETNOTELISTV2, new ServiceInterfaceListener() {
                @Override
                public void getServiceReturnData(Object object) {
                    List<NoteDetail> noteDetails = (List<NoteDetail>) object;
                    if (noteDetails != null && noteDetails.size() > 0) {
                        notifyDrawNotes(noteDetails, 0);
                    }
                    if (isTwinkleBookNote) {
                        twinkleBookNote(linkID);
                        isTwinkleBookNote = false;
                    }
                    if (!TextUtils.isEmpty(selectCusterId) && !selectCusterId.equals(AppConfig.UserID)) {
                        ServiceInterfaceTools.getinstance().getNoteListV3(AppConfig.URL_PUBLIC + "DocumentNote/List?syncRoomID=" + 0 + "&documentItemID=" + currentAttachmentId + "&pageNumber=" + currentAttachmentPage + "&userID=" + selectCusterId, ServiceInterfaceTools.GETNOTELISTV3, new ServiceInterfaceListener() {
                            @Override
                            public void getServiceReturnData(Object object) {
                                List<NoteDetail> noteDetails = (List<NoteDetail>) object;
                                if (noteDetails != null && noteDetails.size() > 0) {
                                    notifyDrawNotes(noteDetails, 1);
                                }
                                if (isTwinkleBookNote) {
                                    twinkleBookNote(linkID);
                                    isTwinkleBookNote = false;
                                }

                            }
                        });
                    }
                }
            });
        }


//api.peertime.cn/peertime/V1/PageObject/GetPageObjects?lessonID=1893488&itemID=1886999&pageNumber=1

        if (isChangePageNumber) {
            isChangePageNumber = false;
            // 3 displayDrawingLine =0 展示所有的线
//            getLineAction(currentAttachmentPage, !isPause);
            Message msg = Message.obtain();
            msg.what = 0x4010;
            msg.obj = actions;
            handler.sendMessage(msg);
        }
        if (type == 3 || type == 4) { //手动翻页
            reflectPage(pageNum);
        }


    }

    private void reflectPage(String page) {
        currentAttachmentPage = page + "";
        if (isPlaying2) { //在播放音想的情况下，手动点击了翻页动作
            String url = AppConfig.URL_PUBLIC + "Soundtrack/PageActionStartTime?soundtrackID=" + soundtrackID + "&pageNumber=" + currentAttachmentPage;
            ServiceInterfaceTools.getinstance().getPageActionStartTime(url, ServiceInterfaceTools.GETPAGEACTIONSTARTTIME, new ServiceInterfaceListener() {
                @Override
                public void getServiceReturnData(Object object) {
                    int ttt = (int) object;  //翻到当前页的时间
                    if (ttt > tttime) {
                        if (mSeekBar != null) {
                            mSeekBar.setProgress(ttt);
                        }
                    }
//                    getLineAction(currentAttachmentPage, !isPause);
                    if (mediaPlayer2 != null) {
                        mediaPlayer2.seekTo(tttime);
                    }
                    if (mediaPlayer != null) {
                        mediaPlayer.seekTo(tttime);
                    }
                }
            });
        }
    }


    /**
     * 播放音响拿actions信息
     *
     * @param pageNum
     * @param isPlaying
     */
    private void getLineAction(final String pageNum, final boolean isPlaying) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {  //  清空当前页的线
                wv_show.loadUrl("javascript:ClearPageAndAction()", null);
            }
        });
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject;
                    String url = AppConfig.URL_PUBLIC + "PageObject/GetPageObjects?lessonID=0"
                            + "&itemID=0"
                            + "&pageNumber=" + pageNum
                            + "&attachmentID=0"
                            + "&soundtrackID=" + (soundtrackID == -1 ? 0 : soundtrackID)
                            + "&displayDrawingLine=" + (isPlaying ? 0 : 1);


                    jsonObject = ConnectService.getIncidentbyHttpGet(url);

                    Log.e("getLineAction", url + "     " + jsonObject.toString());
                    int retCode = jsonObject.getInt("RetCode");
                    switch (retCode) {
                        case 0:
                            JSONArray data = jsonObject.getJSONArray("RetData");
                            String mmm = "";
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject jsonObject1 = data.getJSONObject(i);
                                String ddd = jsonObject1.getString("Data");
                                if (!TextUtil.isEmpty(ddd)) {
                                    String dd = "'" + Tools.getFromBase64(ddd) + "'";
                                    if (i == 0) {
                                        mmm += "[" + dd;
                                    } else {
                                        mmm += "," + dd;
                                    }
                                    if (i == data.length() - 1) {
                                        mmm += "]";
                                    }
                                }
                            }
                            Message msg = Message.obtain();
                            msg.what = 0x4010;
                            msg.obj = mmm;
                            handler.sendMessage(msg);


                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start(((App) getApplication()).getThreadMgr());
    }


    public String EncoderByMd5(String str) {
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

    private static HttpUtils client;

    /**
     * 单例模式获得HttpUtils对象
     *
     * @param context
     * @return
     */
    public synchronized static HttpUtils getInstance(Context context) {
        if (client == null) {
            // 设置请求超时时间为10秒
            client = new HttpUtils(1000 * 10);
            client.configSoTimeout(1000 * 10);
            client.configResponseTextCharset("UTF-8");
            // 保存服务器端(Session)的Cookie
            PreferencesCookieStore cookieStore = new PreferencesCookieStore(
                    context);
            cookieStore.clear(); // 清除原来的cookie
            client.configCookieStore(cookieStore);
        }
        return client;
    }


    @JavascriptInterface
    public void reflect(String result) {
        if (isSync) {
            if (!TextUtils.isEmpty(result)) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    jsonObject.put("time", tttime);
                    result = jsonObject.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        String newresult = Tools.getBase64(result).replaceAll("[\\s*\t\n\r]", "");
        try {
            JSONObject loginjson = new JSONObject();
            loginjson.put("action", "ACT_FRAME");
            loginjson.put("sessionId", AppConfig.UserToken);
            loginjson.put("retCode", 1);
            loginjson.put("data", newresult);
            loginjson.put("itemId", currentItemId);
            loginjson.put("sequenceNumber", "3837");
            loginjson.put("ideaType", "document");
            String ss = loginjson.toString();
            Log.e("dsasa 划线", " " + currentItemId + " " + ss);
            SpliteSocket.sendMesageBySocket(ss);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isWebViewLoadFinish = true;

    /**
     * pdf 加载完成
     */
    @JavascriptInterface
    public void afterLoadFileFunction() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e("webview-afterLoadFile", "afterLoadFileFunction");
                wv_show.loadUrl("javascript:CheckZoom()", null);
                if (!TextUtils.isEmpty(zoomInfo)) {
                    Message msg3 = Message.obtain();
                    msg3.obj = zoomInfo;
                    msg3.what = 0x1109;
                    handler.sendMessage(msg3);
                }
                if (currentPresenterId.equals(AppConfig.UserID)) {
                    Log.e("---------", currentPresenterId.equals(AppConfig.UserID) + "  dd  " + (wv_show == null));
                    wv_show.loadUrl("javascript:ShowToolbar(" + true + ")", null);
                    wv_show.loadUrl("javascript:Record()", null);
                } else {
                    Log.e("---------", currentPresenterId.equals(AppConfig.UserID) + "  dd  " + (wv_show == null));
                    wv_show.loadUrl("javascript:ShowToolbar(" + false + ")", null);
                    wv_show.loadUrl("javascript:StopRecord()", null);
                }

                isWebViewLoadFinish = true;
            }
        });
    }


    private List<NoteDetail> pageNoteDetails = new ArrayList<>();

    /**
     * 切换文档
     *
     * @param diff
     */
    @JavascriptInterface
    public void autoChangeFileFunction(final int diff) {
        refreshTouchHelper();
        if (closenote.getVisibility() == View.VISIBLE) {
            String url = AppConfig.URL_PUBLIC + "DocumentNote/List?syncRoomID=" + lessonId + "&documentItemID=0&pageNumber=0&userID=" + selectCusterId;
            ServiceInterfaceTools.getinstance().getNoteListV2(url, ServiceInterfaceTools.GETNOTELISTV2, new ServiceInterfaceListener() {
                @Override
                public void getServiceReturnData(Object object) {
                    pageNoteDetails.clear();
                    pageNoteDetails.addAll((List<NoteDetail>) object);

                    Log.e("webview-autoChangeFile", diff + "  " + pageNoteDetails.size());
                    if (pageNoteDetails.size() == 0 || pageNoteDetails.size() == 1) {
                        return;
                    }
                    for (int i1 = 0; i1 < pageNoteDetails.size(); i1++) {
                        Log.e("webview-autoChangeFile", currentShowPdf.getItemId() + "  " + pageNoteDetails.get(i1).getNoteID());
                        if (currentShowPdf.getItemId().equals(pageNoteDetails.get(i1).getNoteID() + "")) {
                            NoteDetail noteDetail = new NoteDetail();
                            if (diff == 1) {  // 往后一页
                                if (i1 == pageNoteDetails.size() - 1) {
                                    noteDetail = pageNoteDetails.get(0);
                                } else {
                                    noteDetail = pageNoteDetails.get(i1 + 1);
                                }
                            } else if (diff == -1) {  //往前一页
                                if (i1 == 0) {
                                    noteDetail = pageNoteDetails.get(pageNoteDetails.size() - 1);
                                } else {
                                    noteDetail = pageNoteDetails.get(i1 - 1);
                                }
                            }
                            Note note = new Note();
                            note.setNoteID(noteDetail.getNoteID());
                            note.setAttachmentID(noteDetail.getAttachmentID());
                            note.setAttachmentUrl(noteDetail.getAttachmentUrl());
                            Log.e("webview-autoChangeFile", note.getNoteID() + "  ");
                            displayNoteChangePage(note, true);
                            break;
                        }
                    }
                    return;

                }
            });
        }

        Log.e("webview-autoChangeFile", diff + "");
        if (documentList.size() == 0 || documentList.size() == 1) {
            return;
        }
        if (isHavePresenter()) {
            for (int i = 0; i < documentList.size(); i++) {
                LineItem line = documentList.get(i);
                if (line.getAttachmentID().equals(currentShowPdf.getAttachmentID())) { //当前文档
                    if (diff == 1) {  // 往后一页
                        if (i == documentList.size() - 1) {  // 切换到首页
                            line.setSelect(false);
                            currentShowPdf = documentList.get(0);
                        } else {
                            line.setSelect(false);
                            currentShowPdf = documentList.get(i + 1);
                        }
                    } else if (diff == -1) {  //往前一页
                        if (i == 0) {
                            line.setSelect(false);
                            currentShowPdf = documentList.get(documentList.size() - 1);  //切换到最后一页
                        } else {
                            line.setSelect(false);
                            currentShowPdf = documentList.get(i - 1);
                        }
                    }
                    break;
                }
            }
            currentShowPdf.setSelect(true);

            currentAttachmentId = currentShowPdf.getAttachmentID();
            currentItemId = currentShowPdf.getItemId();
            targetUrl = currentShowPdf.getUrl();
            newPath = currentShowPdf.getNewPath();

            if (diff == 1) {
                currentAttachmentPage = "1";
                notifySwitchDocumentSocket(currentShowPdf, "1", 0);
            } else if (diff == -1) {
                if (!TextUtils.isEmpty(currentShowPdf.getUrl())) {
                    currentAttachmentPage = getPdfCount(currentShowPdf.getUrl()) + "";
                    notifySwitchDocumentSocket(currentShowPdf, currentAttachmentPage, 0);
                }
            }
            AppConfig.currentPageNumber = currentAttachmentPage;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadWebIndex();
                }
            });
        }

    }


    // 播放视频
    @JavascriptInterface
    public void videoPlayFunction(final int vid) {
        Log.e("videoPlayFunction", vid + " videoPlayFunction");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webVideoPlay(vid, false, 0);
            }
        });
    }

    //打开
    @JavascriptInterface
    public void videoSelectFunction(String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (favoritePopup != null) {
                    favoritePopup.StartPop(findViewById(R.id.layout));
                    favoritePopup.setData(2, false);
                }
            }
        });
    }

    // 录制
    @JavascriptInterface
    public void audioSyncFunction(final int id, final int isRecording) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webVideoPlay(id, true, isRecording);  //录音和录action
            }
        });
    }


    public static String mFilePath;

    private AddFileFromFavoriteDialog addFileFromFavoriteDialog;

    AddFileFromDocumentDialog AddFileFromDocumentDialog;

    private void openTeamDocument() {
        if (AddFileFromDocumentDialog != null) {
            AddFileFromDocumentDialog.dismiss();
        }
        AddFileFromDocumentDialog = new AddFileFromDocumentDialog(this);
        AddFileFromDocumentDialog.setOnSpaceSelectedListener(this);
        AddFileFromDocumentDialog.show();
    }


    private InviteUserPopup inviteUserPopup;

    private void flipInviteUserPopupWindow() {
        inviteUserPopup = new InviteUserPopup();
        inviteUserPopup.getPopwindow(SyncRoomActivity.this, meetingId);
        inviteUserPopup.setInvitePopupListener(new InviteUserPopup.InvitePopupListener() {
            @Override
            public void copyLink() {
                activte_linearlayout.setVisibility(View.GONE);
                command_active.setImageResource(R.drawable.icon_command);
            }

            @Override
            public void email(String url) {
                activte_linearlayout.setVisibility(View.GONE);
                command_active.setImageResource(R.drawable.icon_command);
                String[] targetemail = {"214176156@qq.com"};
                String[] email = {"1599528112@qq.com"};
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822"); // 设置邮件格式
                intent.putExtra(Intent.EXTRA_EMAIL, targetemail); // 接收人
                intent.putExtra(Intent.EXTRA_CC, email); // 抄送人
                intent.putExtra(Intent.EXTRA_SUBJECT, "这是邮件的主题部分"); // 主题
                intent.putExtra(Intent.EXTRA_TEXT, url); // 正文
                startActivity(Intent.createChooser(intent, "请选择邮件类应用"));
            }

            @Override
            public void dismiss() {
            }

            @Override
            public void open() {
            }
        });
        inviteUserPopup.StartPop();

    }

    /**
     * 会话 popupwindow
     *
     * @param
     */
    private String mGroupId;
    private ListView chatListview;
    private ChatAdapter chatAdapter;
    private List<TextMessage> chatMessages = new ArrayList<>();

    private void initChatAudienceList() {
        mGroupId = getResources().getString(R.string.Classroom) + lessonId;
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater
                .inflate(R.layout.chat_pop, null);
        RelativeLayout auditor = (RelativeLayout) view.findViewById(R.id.auditor);
        auditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatPopupWindow.dismiss();
            }
        });
        chatListview = (ListView) view.findViewById(R.id.listview);
        chatAdapter = new ChatAdapter(SyncRoomActivity.this, chatMessages);
        chatListview.setAdapter(chatAdapter);

        final EditText editText = (EditText) view.findViewById(R.id.edit);
        ImageView send = (ImageView) view.findViewById(R.id.send);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String context = editText.getText().toString();
                if (!TextUtils.isEmpty(context)) {
                    TextMessage myTextMessage = TextMessage.obtain(context);
                    myTextMessage.setExtra(AppConfig.UserID);
                    Tools.sendMessage(SyncRoomActivity.this, myTextMessage, mGroupId, AppConfig.UserID);
                    editText.setText("");
                }
            }
        });
        ImageView icon_socket_chat = (ImageView) view.findViewById(R.id.icon_socket_chat);
        icon_socket_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tools.openGroup(SyncRoomActivity.this, mGroupId);
                findViewById(R.id.bottomrl).setVisibility(View.GONE);
            }
        });
        chatPopupWindow = new PopupWindow(view, screenWidth,
                ViewGroup.LayoutParams.MATCH_PARENT);
        chatPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                findViewById(R.id.bottomrl).setVisibility(View.VISIBLE);
            }
        });
        chatPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        chatPopupWindow.setAnimationStyle(R.style.anination2);
        chatPopupWindow.setFocusable(true);

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventGroupInfo(io.rong.imlib.model.Message message) {
        getGroupinfo(1);
    }

    private BroadcastReceiver getGroupbroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getGroupinfo(1);
        }
    };


    public synchronized void getGroupinfo(int messageNumber) {
        RongIM.getInstance().getLatestMessages(Conversation.ConversationType.GROUP, mGroupId, messageNumber, new RongIMClient.ResultCallback<List<io.rong.imlib.model.Message>>() {
            @Override
            public void onSuccess(List<io.rong.imlib.model.Message> messages) {
                for (io.rong.imlib.model.Message message : messages) {
                    MessageContent m = message.getContent();
                    if (m instanceof TextMessage) {
                        chatMessages.add((TextMessage) m);
                    }
                }
                for (TextMessage message : chatMessages) {
                    for (int i1 = 0; i1 < teacorstudentList.size(); i1++) {
                        Customer customer = teacorstudentList.get(i1);
                        if (!TextUtils.isEmpty(message.getExtra())) {
                            if (message.getExtra().equals(customer.getUserID())) {
                                message.setExtra(customer.getName());
                                break;
                            }
                        }
                    }
                }
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }

        });
    }


    private int currentPosition = -1;
    private FavoriteVideoPopup favoritePopup;

    private void openSaveVideoPopup() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        Log.e("display_metric", "width:" + dm.widthPixels + ",height:" + dm.heightPixels);
        favoritePopup = new FavoriteVideoPopup(this);
        favoritePopup.setFavoritePoPListener(new FavoriteVideoPopup.FavoriteVideoPoPListener() {
            @Override
            public void dismiss() {

            }

            @Override
            public void open() {

            }

            @Override
            public void selectFavorite(int position) {
                currentPosition = position;
            }

            @Override
            public void cancel() {
                wv_show.loadUrl("javascript:VideoTagAfterSelect(" + null + ")", null);
            }

            @Override
            public void save(int type, boolean isYinxiang) {
                if (type == 2) {  //video
                    if (currentPosition >= 0) {
                        JSONObject jsonObject = favoritePopup.getData().get(currentPosition).getJsonObject();
                        wv_show.loadUrl("javascript:VideoTagAfterSelect(" + jsonObject + ")", null);
                    }
                } else if (type == 3) {  // audio
                    if (currentPosition >= 0) {
                        JSONObject jsonObject = favoritePopup.getData().get(currentPosition).getJsonObject();
                        wv_show.loadUrl("javascript:VideoTagAfterSelect(" + jsonObject + ")", null);
                    }
                }

                if (isYinxiang) {
                    if (yinxiangCreatePopup != null && currentPosition >= 0) {
                        if (isrecord == 0) {
                            yinxiangCreatePopup.setAudioBean(favoritePopup.getData().get(currentPosition));
                        } else if (isrecord == 1) {
                            yinxiangCreatePopup.setRecordBean(favoritePopup.getData().get(currentPosition));
                        }
                    }
                    if (yinxiangEditPopup != null && currentPosition >= 0) {
                        if (isrecord == 0) {
                            yinxiangEditPopup.setAudioBean(favoritePopup.getData().get(currentPosition));
                        } else if (isrecord == 1) {
                            yinxiangEditPopup.setRecordBean(favoritePopup.getData().get(currentPosition));
                        }
                    }
                }
            }

            @Override
            public void uploadFile() {
                favoritePopup.dismiss();
                AddAlbum();
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.addauditorimageview:
                if (identity == 1 || identity == 2) {
                    Intent startAuditor = new Intent(SyncRoomActivity.this, AddAuditorActivity.class);
                    startAuditor.putExtra("mAttendesList", (Serializable) teacorstudentList);
                    startAuditor.putExtra("teacher", teacherCustomer);
                    startAuditor.putExtra("student", studentCustomer);
                    startActivity(startAuditor);
                }
                break;
            case R.id.menu:
                if (syncroomll.getVisibility() == View.VISIBLE) {
                    syncroomll.setVisibility(View.GONE);
                    menu.setImageResource(R.drawable.icon_menu);
                } else if (syncroomll.getVisibility() == View.GONE) {
                    syncroomll.setVisibility(View.VISIBLE);
                    menu.setImageResource(R.drawable.icon_menu_active);
                }
                break;

            case R.id.prepareStart:  //进入meeting
                startMeeting();
                break;
            case R.id.prepareScanTV:
                openTvDevicesList();
                activte_linearlayout.setVisibility(View.GONE);
                command_active.setImageResource(R.drawable.icon_command);
                break;

            case R.id.command_active:  // 弹出语音
                if (activte_linearlayout.getVisibility() == View.VISIBLE) {
                    activte_linearlayout.setVisibility(View.GONE);
                    command_active.setImageResource(R.drawable.icon_command);
                } else if (activte_linearlayout.getVisibility() == View.GONE) {
                    activte_linearlayout.setVisibility(View.VISIBLE);
                    findViewById(R.id.hiddenwalkview).setVisibility(View.VISIBLE);
                    command_active.setImageResource(R.drawable.icon_command_active);
                    syncroomll.setVisibility(View.GONE);
                    menu.setImageResource(R.drawable.icon_menu);
                }
                break;
            case R.id.refresh_notify_2:
                if (teacorstudentList.size() + auditorList.size() > 1) {
                    if (mWebSocketClient != null) {
                        try {
                            JSONObject loginjson = new JSONObject();
                            loginjson.put("action", "LEAVE_MEETING");
                            loginjson.put("sessionId", AppConfig.UserToken);
                            String ss = loginjson.toString();
                            SpliteSocket.sendMesageBySocket(ss);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                mProgressBar.setVisibility(View.VISIBLE);
                Intent intent = new Intent();
                intent.setAction("com.cn.refreshsocket");
                sendBroadcast(intent);
                break;

            case R.id.audiencerl:
                showAudienceList();
                break;

            case R.id.syncdisplaydocument:
                openDocumentPopup();
                syncroomll.setVisibility(View.GONE);
                menu.setImageResource(R.drawable.icon_menu);
                break;
            case R.id.syncdisplaynote:
                openNotePopup();
                notifyTvNoteOpenOrClose(1, selectCusterId);
                syncroomll.setVisibility(View.GONE);
                menu.setImageResource(R.drawable.icon_menu);
                break;
            case R.id.syncdisplaymembers:
                openMemberPopup();
                syncroomll.setVisibility(View.GONE);
                menu.setImageResource(R.drawable.icon_menu);
                break;
            case R.id.syncdisplaymeeting:
                openMeetingPopup();
                syncroomll.setVisibility(View.GONE);
                menu.setImageResource(R.drawable.icon_menu);
                break;
            case R.id.syncdisplaychat:
                chatPopupWindow.showAtLocation(wv_show, Gravity.BOTTOM, 0, 0);
                findViewById(R.id.bottomrl).setVisibility(View.GONE);
                syncroomll.setVisibility(View.GONE);
                menu.setImageResource(R.drawable.icon_menu);
                break;
            case R.id.syncdisplayproperty:
                openPropertyPopup();
                syncroomll.setVisibility(View.GONE);
                menu.setImageResource(R.drawable.icon_menu);
                break;
            case R.id.syncdisplayshare:
                shareDocumentDialog(currentShowPdf, 0);
                syncroomll.setVisibility(View.GONE);
                menu.setImageResource(R.drawable.icon_menu);
                break;
            case R.id.syncyinxiang:
                openYinxiangList(yinxiangmode);
                syncroomll.setVisibility(View.GONE);
                menu.setImageResource(R.drawable.icon_menu);
                break;
            case R.id.syncdisplayquit:
                closeCourse(1);
                finish();
                break;
            case R.id.leavell:
                closeCourse(0);
                activte_linearlayout.setVisibility(View.GONE);
                command_active.setImageResource(R.drawable.icon_command);
                finish();
                break;
            case R.id.endll:
                activte_linearlayout.setVisibility(View.GONE);
                command_active.setImageResource(R.drawable.icon_command);
                endDialog();
                break;
            case R.id.startll:
                startCourse();
                activte_linearlayout.setVisibility(View.GONE);
                command_active.setImageResource(R.drawable.icon_command);
                break;
            case R.id.scanll:
                openTvDevicesList();
                break;
            case R.id.inviteuser:
                flipInviteUserPopupWindow();
                break;
            case R.id.joinvideo:
                activte_linearlayout.setVisibility(View.GONE);
                command_active.setImageResource(R.drawable.icon_command);
                break;

            case R.id.leftview:
                findViewById(R.id.settingll).setVisibility(View.GONE);
                break;
            case R.id.llpre:
                break;
            case R.id.leavepre:
                closeCourse(0);
                finish();
                break;
            case R.id.closeVideo:
                if (isFileVideo) {
                    sendVideoSocket(VIDEOSTATUSCLOSE, (float) (videoView.getCurrentPosition() / 1000), currentPlayVideoId, playUrl, 0);  //  Close
                } else {
                }
                videoPopuP.notifyDataChange(-1);
                findViewById(R.id.videoll).setVisibility(View.GONE);
                videoView.suspend();
                videoView.setVisibility(View.GONE);
                resumeYinxiang();
                break;
            case R.id.timehidden:
                timeShow.setVisibility(View.VISIBLE);
                audiosyncll.setVisibility(View.GONE);
                break;
            case R.id.timeshow:
                timeShow.setVisibility(View.GONE);
                audiosyncll.setVisibility(View.VISIBLE);
                break;
            case R.id.createblabkpage:
                new ApiTask(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final JSONObject jsonObject = ConnectService.submitDataByJson(AppConfig.URL_PUBLIC + "EventAttachment/AddBlankPage?lessonID=" + lessonId, null);
                            Log.e("jjjjj", AppConfig.URL_PUBLIC + "EventAttachment/AddBlankPage?lessonID=" + lessonId + jsonObject.toString());
                            if (jsonObject.getInt("RetCode") == 0) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        findViewById(R.id.defaultpagehaha).setVisibility(View.GONE);
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start(((App) getApplication()).getThreadMgr());
                break;
            case R.id.inviteattendee:
                if (identity == 1 || identity == 2) {
                    Intent startAuditor = new Intent(SyncRoomActivity.this, AddAuditorActivity.class);
                    startAuditor.putExtra("mAttendesList", (Serializable) teacorstudentList);
                    startAuditor.putExtra("teacher", teacherCustomer);
                    startAuditor.putExtra("student", studentCustomer);
                    startActivity(startAuditor);
                }
                break;
            case R.id.sharedocument:
                LoginGet loginGet = new LoginGet();
                loginGet.setMyFavoritesGetListener(new LoginGet.MyFavoritesGetListener() {
                    @Override
                    public void getFavorite(ArrayList<Document> list) {
                        myFavoriteList.clear();
                        myFavoriteList.addAll(list);
                        openMySavePopup(myFavoriteList, 1);
                    }
                });
                loginGet.MyFavoriteRequest(SyncRoomActivity.this, 1);
                break;
            default:
                break;
        }
        refreshTouchHelper();
    }


    private void loadWebIndex() {
        int deviceType = DeviceManager.getDeviceType(this);
        String indexUrl = "file:///android_asset/index.html";
        if (deviceType == SupportDevice.BOOK) {
            indexUrl += "?devicetype=4";
        }
        final String url = indexUrl;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (wv_show == null) {
                    return;
                }
                findViewById(R.id.defaultpagehaha).setVisibility(View.GONE);
                wv_show.loadUrl(url, null);
            }
        });
    }


    private SelectNoteDialog selectNoteDialog;

    /**
     * 笔记列表弹窗
     */
    private void selectNoteBook(final int linkID, final JSONObject linkProperty) {
        if (linkID == 0) {
            String url = AppConfig.URL_PUBLIC + "DocumentNote/UserNoteList?userID=" + AppConfig.UserID;
            selectNoteDialog = new SelectNoteDialog(SyncRoomActivity.this, lessonId);
            selectNoteDialog.setOnFavoriteDocSelectedListener(new SelectNoteDialog.OnFavoriteDocSelectedListener() {
                @Override
                public void onFavoriteDocSelected(Note note) {
                    String url = AppConfig.URL_PUBLIC + "DocumentNote/ImportNote";
                    ServiceInterfaceTools.getinstance().importNote(url, ServiceInterfaceTools.IMPORTNOTE, lessonId,
                            currentAttachmentId + "", currentAttachmentPage, note.getNoteID(), linkProperty.toString(),
                            new ServiceInterfaceListener() {
                                @Override
                                public void getServiceReturnData(Object object) {
                                    int linkid = (int) object;
                                    drawNote(linkid, linkProperty, 0);
                                }
                            });
                }
            });
            selectNoteDialog.show(url);
        } else {
            String linkidurl = AppConfig.URL_PUBLIC + "DocumentNote/NoteByLinkID?linkID=" + linkID;
            ServiceInterfaceTools.getinstance().getNoteByLinkID(linkidurl, ServiceInterfaceTools.GETNOTEBYLINKID, new ServiceInterfaceListener() {
                @Override
                public void getServiceReturnData(Object object) {
                    final Note note2 = (Note) object;
                    String url = AppConfig.URL_PUBLIC + "DocumentNote/UserNoteList?userID=" + AppConfig.UserID;
                    selectNoteDialog = new SelectNoteDialog(SyncRoomActivity.this, lessonId);
                    selectNoteDialog.setOnFavoriteDocSelectedListener(new SelectNoteDialog.OnFavoriteDocSelectedListener() {
                        @Override
                        public void onFavoriteDocSelected(Note note) {
                            Log.e("noterru", note.getNoteID() + "  " + note2.getNoteID());
                            if (note.getNoteID() != note2.getNoteID()) {
                                String url = AppConfig.URL_PUBLIC + "DocumentNote/ImportNote";
                                ServiceInterfaceTools.getinstance().importNote(url, ServiceInterfaceTools.IMPORTNOTE, lessonId,
                                        currentAttachmentId + "", currentAttachmentPage, note.getNoteID(), linkProperty.toString(),
                                        new ServiceInterfaceListener() {
                                            @Override
                                            public void getServiceReturnData(Object object) {
                                                int linkid = (int) object;
                                                Log.e("noterru", "删除了 " + linkID + "  添加了 " + linkid);
                                                deleteNote(linkID);
                                                drawNote(linkid, linkProperty, 0);
                                            }
                                        });
                            }
                        }
                    });
                    selectNoteDialog.show(url);
                }
            });
        }
    }


    private void startMeeting() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("CompanyID", AppConfig.SchoolID);
            jsonObject.put("SyncRoomID", lessonId);
            JSONArray jsonArray = new JSONArray();
            JSONObject member = new JSONObject();
            member.put("MemberID", AppConfig.UserID);
            member.put("Role", 2);
            jsonArray.put(member);
            jsonObject.put("MemberList", jsonArray);
            String url = AppConfig.URL_PUBLIC + "SyncRoom/CreateMeetingFromSyncRoom";
            ServiceInterfaceTools.getinstance().createMeetingFromSyncRoom(url, ServiceInterfaceTools.CREATEMEETINGFROMSYNCROOM, jsonObject, new ServiceInterfaceListener() {
                @Override
                public void getServiceReturnData(Object object) {
                    String lessonId = (String) object;
                    gotoMeeting(lessonId);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void gotoMeeting(String lessonId) {
        final Intent intent = new Intent(SyncRoomActivity.this, DocAndMeetingActivity.class);
        intent.putExtra("userid", AppConfig.UserID);
        intent.putExtra("meetingId", lessonId);
        intent.putExtra("isTeamspace", false);
        intent.putExtra("yinxiangmode", 0);
        intent.putExtra("identity", 2);
        intent.putExtra("lessionId", lessonId);
        intent.putExtra("isInstantMeeting", 0);
        intent.putExtra("teacherid", AppConfig.UserID.replace("-", ""));
        intent.putExtra("isStartCourse", true);
        startActivity(intent);
        finish();
    }


    private void shareDocumentDialog(final LineItem document, final int id) {
        final ShareKloudSyncDialog psk = new ShareKloudSyncDialog();
        psk.getPopwindow(this, document, id);
        psk.setPoPDismissListener(new ShareKloudSyncDialog.PopShareKloudSyncDismissListener() {
            @Override
            public void CopyLink() {

            }

            @Override
            public void Wechat() {

            }

            @Override
            public void Moment() {

            }

            @Override
            public void Scan() {

            }

            @Override
            public void PopBack() {

            }
        });
        psk.startPop();

    }

    private void openPropertyPopup() {
        SyncRoomPropertyPopup syncRoomPropertyPopup = new SyncRoomPropertyPopup();
        syncRoomPropertyPopup.getPopwindow(this);
        syncRoomPropertyPopup.setWebCamPopupListener(new SyncRoomPropertyPopup.WebCamPopupListener() {
            @Override
            public void changeOptions(SyncRoomBean syncRoomBean, TeamSpaceBean teamSpaceBean) {


            }
        });
        syncRoomPropertyPopup.StartPop(wv_show, lessonId, getIntent().getStringExtra("syncRoomname"));
        syncroomll.setVisibility(View.GONE);
        menu.setImageResource(R.drawable.icon_menu);
    }

    private void openMemberPopup() {

        SyncRoomMemberPopup syncRoomMemberPopup = new SyncRoomMemberPopup();
        syncRoomMemberPopup.getPopwindow(this);
        syncRoomMemberPopup.setWebCamPopupListener(new SyncRoomMemberPopup.WebCamPopupListener() {
            @Override
            public void inviteNew() {
                inviteNewTeamMember();
            }

            @Override
            public void dismiss() {
                getWindow().getDecorView().setAlpha(1.0f);
            }

            @Override
            public void open() {
                getWindow().getDecorView().setAlpha(0.5f);
            }
        });
        syncRoomMemberPopup.StartPop(wv_show, lessonId);
        syncroomll.setVisibility(View.GONE);
        menu.setImageResource(R.drawable.icon_menu);
    }


    private void openTvDevicesList() {

        ServiceInterfaceTools.getinstance().getBindTvs().enqueue(new Callback<DevicesResponse>() {
            @Override
            public void onResponse(Call<DevicesResponse> call, Response<DevicesResponse> response) {
                if (response != null && response.isSuccessful() && response.body() != null) {
                    if (response.body().getData() != null) {
                        List<TvDevice> devices = response.body().getData().getDeviceList();
                        boolean enable = response.body().getData().isEnableBind();
                        if (devices != null && devices.size() > 0) {
                            TvDevicesListPopup tvDevicesListPopup = new TvDevicesListPopup();
                            tvDevicesListPopup.getPopwindow(SyncRoomActivity.this);
                            tvDevicesListPopup.setWebCamPopupListener(new TvDevicesListPopup.WebCamPopupListener() {
                                @Override
                                public void scanTv() {
                                    gotoScanTv();
                                }

                                @Override
                                public void changeBindStatus(boolean isCheck) {
                                    String url = AppConfig.wssServer + "/tv/change_bind_tv_status?status=" + (isCheck ? 1 : 0);
                                    ServiceInterfaceTools.getinstance().changeBindTvStatus(url, ServiceInterfaceTools.CHANGEBINDTVSTATUS,
                                            isCheck, new ServiceInterfaceListener() {
                                                @Override
                                                public void getServiceReturnData(Object object) {

                                                }
                                            });
                                }

                                @Override
                                public void openTransfer(TvDevice tvDevice) {

                                }

                                @Override
                                public void closeTransfer(TvDevice tvDevice) {

                                }

                                @Override
                                public void logout(TvDevice tvDevice) {

                                }
                            });
                            tvDevicesListPopup.StartPop(wv_show, devices, enable, isTeamspace);
                            activte_linearlayout.setVisibility(View.GONE);
                            menu.setImageResource(R.drawable.icon_menu);
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

        String url = AppConfig.wssServer + "/tv/change_bind_tv_status?status=1";
        ServiceInterfaceTools.getinstance().changeBindTvStatus(url, ServiceInterfaceTools.CHANGEBINDTVSTATUS,
                true, new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {

                    }
                });

        if (worker().getRtcEngine() != null) {
            RtcEngineImpl engine = (RtcEngineImpl) worker().getRtcEngine();
//            engine.setVideoCamera(0);

        }

        Intent intent1 = new Intent(SyncRoomActivity.this, MipcaActivityCapture.class);
        intent1.putExtra("isHorization", true);
        intent1.putExtra("type", 0);
        startActivity(intent1);
    }


    private SyncRoomDocumentPopup syncRoomDocumentPopup;

    private void openDocumentPopup() {

        syncRoomDocumentPopup = new SyncRoomDocumentPopup();
        syncRoomDocumentPopup.getPopwindow(this);
        syncRoomDocumentPopup.setDocumentPopupEventListener(this);
        syncRoomDocumentPopup.setWebCamPopupListener(new SyncRoomDocumentPopup.WebCamPopupListener() {
            @Override
            public void changeOptions(LineItem syncRoomBean, int position) { //切换文档
//                if (isHavePresenter()) {
//
//                }

                notifySelectedFileIndex(position);
                currentAttachmentPage = "0";
                AppConfig.currentPageNumber = "0";
                for (int i = 0; i < documentList.size(); i++) {
                    documentList.get(i).setSelect(false);
                }
                currentShowPdf = syncRoomBean;
                currentShowPdf.setSelect(true);
                currentAttachmentId = currentShowPdf.getAttachmentID();  // itemId
                currentItemId = currentShowPdf.getItemId();
                targetUrl = currentShowPdf.getUrl();
                newPath = currentShowPdf.getNewPath();
                notifySwitchDocumentSocket(currentShowPdf, "1", 0);
                loadWebIndex();
            }

            @Override
            public void teamDocument() {
                openTeamDocument();
            }

            @Override
            public void takePhoto() {
                if (!Environment.MEDIA_MOUNTED.equals(Environment
                        .getExternalStorageState())) {
                    Toast.makeText(SyncRoomActivity.this, "please insert SDCard", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mFilePath = Environment.getExternalStorageDirectory().getPath();
                // 文件名
                mFilePath = DateFormat.format("yyyyMMdd_hhmmss",
                        Calendar.getInstance(Locale.CHINA))
                        + ".jpg";
                File file1 = new File(cache, mFilePath);
                //Android7.0文件保存方式改变了
                if (Build.VERSION.SDK_INT < 24) {
                    Uri uri = Uri.fromFile(new File(cache, mFilePath));
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                } else {
                    ContentValues contentValues = new ContentValues(1);
                    contentValues.put(MediaStore.Images.Media.DATA, file1.getAbsolutePath());
                    Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                }
                startActivityForResult(intent, REQUEST_CODE_CAPTURE_CAMEIA);
            }

            @Override
            public void importFromLibrary() {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_CAPTURE_ALBUM);
            }

            @Override
            public void savedFile() {
                addFileFromFavoriteDialog = new AddFileFromFavoriteDialog(SyncRoomActivity.this);
                addFileFromFavoriteDialog.setOnFavoriteDocSelectedListener(SyncRoomActivity.this);
                addFileFromFavoriteDialog.show();
            }

            @Override
            public void dismiss() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshTouchHelper();
                    }
                }, 1000);
            }

            @Override
            public void open() {
            }

            @Override
            public void delete(LineItem selectLineItem) {
                deleteDocument(selectLineItem);
            }

            @Override
            public void edit(LineItem selectLineItem) {
                editDocument(selectLineItem);
            }
        });
        syncRoomDocumentPopup.StartPop(wv_show, documentList);
        syncroomll.setVisibility(View.GONE);
        menu.setImageResource(R.drawable.icon_menu);
    }

    private String selectCusterId = AppConfig.UserID;

    private void openNotePopup() {
        if (TextUtils.isEmpty(selectCusterId)) {
            selectCusterId = AppConfig.UserID;
        }
        gotoOtherNoteList(selectCusterId);
    }

    /**
     * 进入笔记列表
     */
    private SyncRoomOtherNoteListPopup syncRoomOtherNoteListPopup;

    private void gotoOtherNoteList(String userid) {
        syncRoomOtherNoteListPopup = new SyncRoomOtherNoteListPopup();
        syncRoomOtherNoteListPopup.getPopwindow(SyncRoomActivity.this);
        syncRoomOtherNoteListPopup.setWebCamPopupListener(new SyncRoomOtherNoteListPopup.WebCamPopupListener() {
            @Override
            public void select(NoteDetail noteDetail) {
                switchPdf(noteDetail);
            }

            @Override
            public void viewNote(NoteDetail noteDetail) {
                Note note = new Note();
                note.setNoteID(noteDetail.getNoteID());
                note.setAttachmentID(noteDetail.getAttachmentID());
                note.setAttachmentUrl(noteDetail.getAttachmentUrl());
                Log.e("webview-autoChangeFile", note.getNoteID() + "  ");
                displayNote(note);
            }

            @Override
            public void notifychangeUserid(String userId) {
                loadNoteWhenChangeUser(userId);
                notifyChangeNoteUser(userId);
            }

            @Override
            public void close() {
                refreshTouchHelper();
                notifyTvNoteOpenOrClose(0, selectCusterId);
            }

        });
        syncRoomOtherNoteListPopup.StartPop(userid, lessonId);
    }

    private void notifyChangeNoteUser(String userId) {
        JSONObject json = new JSONObject();
        try {
            json.put("actionType", 27);
            json.put("userId", userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        send_message("SEND_MESSAGE", AppConfig.UserToken, 0, "", Tools.getBase64(json.toString()).replaceAll("[\\s*\t\n\r]", ""));
    }

    private void notifyTvNoteOpenOrClose(int type, String useid) {
        JSONObject actionJson = new JSONObject();
        try {
            actionJson.put("actionType", 1820);
            // 1表示打开 0表示关闭
            actionJson.put("stat", type);
            actionJson.put("useId", useid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        send_message("SEND_MESSAGE", AppConfig.UserToken, 0, null, Tools.getBase64(actionJson.toString()).replaceAll("[\\s*\t\n\r]", ""));
    }

    /**
     * 切换带笔记对应的文档
     */

    private int linkID;
    private boolean isTwinkleBookNote = false;

    private void switchPdf(final NoteDetail noteDetail) {
        closenote.setVisibility(View.GONE);
        int attachmentid = noteDetail.getDocumentItemID();
        int pagenumber = noteDetail.getPageNumber();
        if ((attachmentid + "").equals(currentAttachmentId) && (pagenumber + "").equals(currentAttachmentPage)) {
            twinkleBookNote(noteDetail.getLinkID());
            return;
        }
        linkID = noteDetail.getLinkID();
        if (documentList.size() > 0) {
            for (LineItem lineItem : documentList) {
                Log.e("switchPdf", attachmentid + "   " + lineItem.getAttachmentID());
                if (lineItem.getAttachmentID().equals(attachmentid + "")) {
                    Log.e("switchPdf22", attachmentid + "   " + lineItem.getAttachmentID());
                    currentAttachmentPage = pagenumber + "";  // 笔记在文档的那一页
                    AppConfig.currentPageNumber = pagenumber + "";
                    currentShowPdf = lineItem;
                    currentShowPdf.setSelect(true);
                    currentShowPdf.setPageNumber(pagenumber + "");
                    currentAttachmentId = currentShowPdf.getAttachmentID();
                    currentItemId = currentShowPdf.getItemId();
                    targetUrl = currentShowPdf.getUrl();
                    newPath = currentShowPdf.getNewPath();
                    isTwinkleBookNote = true;
                    notifySwitchDocumentSocket(currentShowPdf, currentAttachmentPage, 0);
                    loadWebIndex();
                    break;
                }
            }
        }
    }

    private void loadNoteWhenChangeUser(String userId) {
        if (!TextUtils.isEmpty(userId)) {
            selectCusterId = userId;
            if (userId.equals(AppConfig.UserID)) {
                //清除别人的日记
                clearBookNote(false, true);
            } else {//加载别人的日记
                ServiceInterfaceTools.getinstance().getNoteListV3(AppConfig.URL_PUBLIC + "DocumentNote/List?syncRoomID=" + 0 + "&documentItemID=" + currentAttachmentId + "&pageNumber=" + currentAttachmentPage + "&userID=" + userId, ServiceInterfaceTools.GETNOTELISTV3, new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        List<NoteDetail> noteDetails = (List<NoteDetail>) object;
                        if (noteDetails != null && noteDetails.size() > 0) {
                            notifyDrawNotes(noteDetails, 1);
                        }
                    }
                });
            }
            if (syncRoomOtherNoteListPopup != null && syncRoomOtherNoteListPopup.isShowing()) {
                syncRoomOtherNoteListPopup.reLoadUser(userId);
            }
        }
    }

    private void openMeetingPopup() {
        SyncRoomMeetingPopup syncRoomMeetingPopup = new SyncRoomMeetingPopup();
        syncRoomMeetingPopup.getPopwindow(this);
        syncRoomMeetingPopup.setWebCamPopupListener(new SyncRoomMeetingPopup.WebCamPopupListener() {
            @Override
            public void changeOptions(LineItem syncRoomBean) {

            }

            @Override
            public void dismiss() {
                refreshTouchHelper();
            }

            @Override
            public void open() {
            }
        });
        syncRoomMeetingPopup.StartPop(wv_show, lessonId);
        syncroomll.setVisibility(View.GONE);
        menu.setImageResource(R.drawable.icon_menu);
    }

    private AlertDialog dialog;

    private void deleteDocument(final LineItem lineItem) {
        final LayoutInflater inflater = LayoutInflater
                .from(SyncRoomActivity.this);
        View windov = inflater.inflate(R.layout.deletedocument_dialog, null);
        windov.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        windov.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                new ApiTask(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = ConnectService.getIncidentDataattachment(AppConfig.URL_PUBLIC + "TopicAttachment/RemoveDocument?itemID=" + lineItem.getItemId());
                        try {
                            Log.e("dddddd", AppConfig.URL_PUBLIC + "TopicAttachment/RemoveDocument?itemID=" + lineItem.getItemId() + jsonObject.toString());
                            int retcode = jsonObject.getInt("RetCode");
                            switch (retcode) {
                                case 0:
                                    getSyncRoomDetail();
                                    break;

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start(((App) getApplication()).getThreadMgr());
                dialog.dismiss();
                syncRoomDocumentPopup.dismiss();
            }
        });
        dialog = new AlertDialog.Builder(this).show();
        Window dialogWindow = dialog.getWindow();
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        p.width = (int) (d.getWidth() * 0.8);
        dialogWindow.setAttributes(p);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(windov);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    }

    private void editDocument(final LineItem lineItem) {
        final LayoutInflater inflater = LayoutInflater
                .from(SyncRoomActivity.this);
        View windov = inflater.inflate(R.layout.editdocument_dialog, null);
        windov.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        EditText tv1 = (EditText) windov.findViewById(R.id.tv1);
        tv1.setText(lineItem.getFileName());
        TextView tv2 = (TextView) windov.findViewById(R.id.tv2);
        tv2.setText(lineItem.getFileName());

        TextView tv3 = (TextView) windov.findViewById(R.id.tv3);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String ss = simpleDateFormat.format(new Date());
        tv3.setText(ss);
        windov.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                new ApiTask(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = ConnectService.getIncidentDataattachment(AppConfig.URL_PUBLIC + "TopicAttachment/RenameAttachment?itemID=" + lineItem.getItemId() + "&title=" + URLEncoder.encode(LoginGet.getBase64Password(lineItem.getFileName()), "UTF-8"));
                            Log.e("dddddd", AppConfig.URL_PUBLIC + "TopicAttachment/RenameAttachment?itemID=48431&title=" + lineItem.getItemId() + jsonObject.toString());
                            int retcode = jsonObject.getInt("RetCode");
                            switch (retcode) {
                                case 0:
                                    getSyncRoomDetail();
                                    break;

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start(((App) getApplication()).getThreadMgr());
                dialog.dismiss();
                syncRoomDocumentPopup.dismiss();
            }
        });
        dialog = new AlertDialog.Builder(this).show();
        Window dialogWindow = dialog.getWindow();
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        p.width = (int) (d.getWidth() * 0.8);
        dialogWindow.setAttributes(p);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(windov);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    }


    private void inviteNewTeamMember() {

        final LayoutInflater inflater = LayoutInflater
                .from(SyncRoomActivity.this);
        View windov = inflater.inflate(R.layout.invitenewteammember_dialog, null);
        windov.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });

        windov.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });

        EditText tv1 = (EditText) windov.findViewById(R.id.tv1);
        EditText tv2 = (EditText) windov.findViewById(R.id.tv2);
        CheckBox checkbox = (CheckBox) windov.findViewById(R.id.checkbox);


        windov.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
            }
        });
        dialog = new AlertDialog.Builder(this).show();
        Window dialogWindow = dialog.getWindow();
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        p.width = (int) (d.getWidth() * 0.8);
        dialogWindow.setAttributes(p);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(windov);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    }


    private void getOnstageMemberCount(final String meetingId) {

        ServiceInterfaceTools.getinstance().getOnstageMemberCount(AppConfig.URL_PUBLIC_AUDIENCE + "MeetingServer/member/join_role?meetingId=" + meetingId, ServiceInterfaceTools.GETONSTAGEMEMBERCOUNT, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                int role = (int) object;
                sendStringBySocket2("JOIN_MEETING", AppConfig.UserToken, "", meetingId, "", true, "v20140605.0", false, role, isInstantMeeting);
            }
        });
    }


    private void showAudienceList() {
        syncroomll.setVisibility(View.GONE);
        menu.setImageResource(R.drawable.icon_menu);

        AudienceMemberPopup audienceMemberPopup = new AudienceMemberPopup();
        audienceMemberPopup.getPopwindow(this);
        audienceMemberPopup.setWebCamPopupListener(new AudienceMemberPopup.WebCamPopupListener() {
            @Override
            public void inviteNew() {

            }

            @Override
            public void dismiss() {
                getWindow().getDecorView().setAlpha(1.0f);
            }

            @Override
            public void open() {
                getWindow().getDecorView().setAlpha(0.5f);
            }
        });
        audienceMemberPopup.StartPop(wv_show, meetingId);

    }

    /**
     * popupwindow
     */
    private void shareToSyncRoom() {
        SyncRoomPopup syncRoomPopup = new SyncRoomPopup();
        syncRoomPopup.getPopwindow(SyncRoomActivity.this);
        syncRoomPopup.setWebCamPopupListener(new SyncRoomPopup.WebCamPopupListener() {

            @Override
            public void changeOptions(SyncRoomBean syncRoomBean, TeamSpaceBean teamSpaceBean, int spaceid) {
                if (syncRoomBean.getItemID() == -1) {
                    CreateSyncRoomPopup createSyncRoomPopup = new CreateSyncRoomPopup();
                    createSyncRoomPopup.getPopwindow(SyncRoomActivity.this);
                    createSyncRoomPopup.setWebCamPopupListener(new CreateSyncRoomPopup.WebCamPopupListener() {
                        @Override
                        public void enter(SyncRoomBean syncRoomBean) {
                            enterSyncroom(syncRoomBean);
                        }
                    });
                    createSyncRoomPopup.StartPop(wv_show, teamSpaceBean, spaceid, currentAttachmentId);
                } else {
                    enterSyncroom(syncRoomBean);
                }
            }

            @Override
            public void dismiss() {
                getWindow().getDecorView().setAlpha(1.0f);

            }

            @Override
            public void open() {
                getWindow().getDecorView().setAlpha(0.5f);

            }
        });
        syncRoomPopup.StartPop(wv_show);

    }

    private void enterSyncroom(SyncRoomBean syncRoomBean) {
        Toast.makeText(SyncRoomActivity.this, syncRoomBean.getName(), Toast.LENGTH_LONG).show();
    }


    private YinxiangPopup yinxiangPopup;

    private void openYinxiangList(int yinxiangmode) {
        yinxiangPopup = new YinxiangPopup();
        yinxiangPopup.getPopwindow(SyncRoomActivity.this);
        yinxiangPopup.setShareDocumentToFriendListener(this);
        yinxiangPopup.setCurrentDocument(currentShowPdf);
        yinxiangPopup.setFavoritePoPListener(new YinxiangPopup.FavoritePoPListener() {
            @Override
            public void dismiss() {

            }

            @Override
            public void open() {

            }

            @Override
            public void createYinxiang() {
                createYinxiangPopup();
            }

            @Override
            public void editYinxiang(SoundtrackBean soundtrackBean) {
                editYinxiangPopup(soundtrackBean);
            }

            @Override
            public void playYinxiang(final SoundtrackBean soundtrackBean) {
                soundtrackID = soundtrackBean.getSoundtrackID();
                favoriteAudio = soundtrackBean.getBackgroudMusicInfo();
                getAudioAction(soundtrackBean.getSoundtrackID(), 0);
                String duration = soundtrackBean.getDuration();
                openAudioSync(false, true, TextUtils.isEmpty(duration) ? 0 : Long.parseLong(duration));
                if (favoriteAudio == null || favoriteAudio.getAttachmentID().equals("0")) {  //backgroundmusic
                    GetMediaPlay("", false);
                    sendAudioSocket(1, soundtrackID);
                } else {
                    GetMediaPlay(favoriteAudio.getFileDownloadURL(), false);
                    sendAudioSocket(1, soundtrackID);
                }
                if (soundtrackBean.getNewAudioAttachmentID() != 0) {
                    //1  拿 Bucket 信息
                    LoginGet lg = new LoginGet();
                    lg.setprepareUploadingGetListener(new LoginGet.prepareUploadingGetListener() {
                        @Override
                        public void getUD(final Uploadao ud) {
                            // 2 调queryDownloading接口
                            JsonDown2(ud, soundtrackBean);
                        }
                    });
                    lg.GetprepareUploading(SyncRoomActivity.this);
                } else if (soundtrackBean.getSelectedAudioAttachmentID() != 0) {
                    GetMediaPlay2(soundtrackBean.getSelectedAudioInfo().getFileDownloadURL());
                }

            }
        });
        if (yinxiangmode == 0) {
            yinxiangPopup.StartPop(wv_show, currentAttachmentId, "", false, isHavePresenter());
        } else if (yinxiangmode == 1) {
            yinxiangPopup.StartPop(wv_show, currentAttachmentId, lessonId, false, isHavePresenter());
        } else if (yinxiangmode == 2) {
            yinxiangPopup.StartPop(wv_show, currentAttachmentId, lessonId, true, isHavePresenter());
        } else if (yinxiangmode == 4) {
            yinxiangPopup.StartPop(wv_show, "0", lessonId, true, isHavePresenter());
        }
    }


    private void JsonDown2(final Uploadao ud, final SoundtrackBean soundtrackBean) {
        final String playurl = soundtrackBean.getNewAudioInfo().getFileDownloadURL();
        newPath = playurl.substring(playurl.indexOf(".com") + 5, playurl.lastIndexOf("/"));
        Log.e("当前录音信息", playurl + "       " + newPath);
        ServiceInterfaceTools.getinstance().queryDownloading(AppConfig.URL_LIVEDOC + "queryDownloading", ServiceInterfaceTools.QUERYDOWNLOADING, ud,
                newPath, new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        // https://peertime.oss-cn-shanghai.aliyuncs.com/P49/Attachment/D44811/20190527025334.wav
                        //拼接  url
                        String jsonstring = (String) object;
                        Log.e("当前录音信息json", jsonstring);
                        int xx = transfering(jsonstring);
                        if (2 == xx) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    JsonDown2(ud, soundtrackBean);
                                }
                            }, 1000);
                        } else if (1 == xx) {
                            String filename = playurl.substring(playurl.lastIndexOf("/") + 1);
                            String newpath = "";
                            if (1 == ud.getServiceProviderId()) {
                                newpath = "https://s3." + ud.getRegionName() + ".amazonaws.com/" + ud.getBucketName() + "/" + newPath + "/" + filename;
                            } else if (2 == ud.getServiceProviderId()) {
                                newpath = "https://" + ud.getBucketName() + "." + ud.getRegionName() + "." + "aliyuncs.com" + "/" + newPath + "/" + filename;
                            }
                            //https://peertime.oss-cn-shanghai.aliyuncs.com/P49/Attachment/D24893/3fffe932-5e52-4dbb-8376-9436a2de4dbe_1_2K.jpg
                            Log.e("当前录音信息", "url  " + newpath);
                            GetMediaPlay2(newpath);
                        } else {
                            Toast.makeText(SyncRoomActivity.this, "Not find the file.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    private YinxiangCreatePopup yinxiangCreatePopup;
    private int soundtrackID = -1;
    private int fieldId;
    private String fieldNewPath;
    private int isrecord = 0;

    private void createYinxiangPopup() {
        yinxiangCreatePopup = new YinxiangCreatePopup();
        yinxiangCreatePopup.getPopwindow(SyncRoomActivity.this);
        yinxiangCreatePopup.setFavoritePoPListener(new YinxiangCreatePopup.FavoritePoPListener() {

            @Override
            public void addrecord(int ii) {
                if (favoritePopup != null) {
                    isrecord = ii;
                    favoritePopup.StartPop(findViewById(R.id.layout));
                    favoritePopup.setData(3, true);
                }
            }

            @Override
            public void addaudio(int ii) {
                if (favoritePopup != null) {
                    isrecord = ii;
                    favoritePopup.StartPop(findViewById(R.id.layout));
                    favoritePopup.setData(3, true);
                }
            }

            @Override
            public void syncorrecord(boolean checked, SoundtrackBean soundtrackBean2) {  //录制音响
                favoriteAudio = soundtrackBean2.getBackgroudMusicInfo();
                soundtrackID = soundtrackBean2.getSoundtrackID();
                fieldId = soundtrackBean2.getFileId();
                fieldNewPath = soundtrackBean2.getPath();
                String duration = soundtrackBean2.getDuration();
                sendSync("AUDIO_SYNC", AppConfig.UserToken, 1, soundtrackID);
                openAudioSync(checked, false, TextUtils.isEmpty(duration) ? 0 : Long.parseLong(duration));
                if (favoriteAudio == null || favoriteAudio.getAttachmentID().equals("0")) {
                    GetMediaPlay("", checked);  // 录音
                } else {
                    GetMediaPlay(favoriteAudio.getFileDownloadURL(), checked);  // 录音
                }

                if (soundtrackBean2.getNewAudioAttachmentID() != 0) {
                    GetMediaPlay2(soundtrackBean2.getNewAudioInfo().getFileDownloadURL());
                } else if (soundtrackBean2.getSelectedAudioAttachmentID() != 0) {
                    GetMediaPlay2(soundtrackBean2.getSelectedAudioInfo().getFileDownloadURL());
                }
            }
        });
        yinxiangCreatePopup.StartPop(wv_show, currentAttachmentId);
    }


    private YinxiangEditPopup yinxiangEditPopup;

    private void editYinxiangPopup(SoundtrackBean soundtrackBean) {
        yinxiangEditPopup = new YinxiangEditPopup();
        yinxiangEditPopup.getPopwindow(SyncRoomActivity.this);
        yinxiangEditPopup.setFavoritePoPListener(new YinxiangEditPopup.FavoritePoPListener() {
            @Override
            public void dismiss() {
            }

            @Override
            public void open() {
            }

            @Override
            public void addrecord(int ii) {
                if (favoritePopup != null) {
                    isrecord = ii;
                    favoritePopup.StartPop(findViewById(R.id.layout));
                    favoritePopup.setData(3, true);
                }
            }

            @Override
            public void addaudio(int ii) {
                if (favoritePopup != null) {
                    isrecord = ii;
                    favoritePopup.StartPop(findViewById(R.id.layout));
                    favoritePopup.setData(3, true);
                }
            }

            @Override
            public void syncorrecord(boolean checked, SoundtrackBean soundtrackBean2) {
                favoriteAudio = soundtrackBean2.getBackgroudMusicInfo();
                soundtrackID = soundtrackBean2.getSoundtrackID();
                fieldId = soundtrackBean2.getFileId();
                fieldNewPath = soundtrackBean2.getPath();
                String duration = soundtrackBean2.getDuration();
                sendSync("AUDIO_SYNC", AppConfig.UserToken, 1, soundtrackID);
                openAudioSync(checked, false, TextUtils.isEmpty(duration) ? 0 : Long.parseLong(duration));
                if (favoriteAudio == null || favoriteAudio.getAttachmentID().equals("0")) {
                    GetMediaPlay("", checked);
                } else {
                    GetMediaPlay(favoriteAudio.getFileDownloadURL(), checked);
                }
                // 播放录音
                if (!TextUtils.isEmpty(soundtrackBean2.getSelectedAudioInfo().getFileDownloadURL())) {
                    GetMediaPlay2(soundtrackBean2.getSelectedAudioInfo().getFileDownloadURL());
                }
            }
        });
        yinxiangEditPopup.StartPop(wv_show, soundtrackBean);
    }


    private void getyinxiangdetail(final int soundtrackID2) {
        ServiceInterfaceTools.getinstance().getSoundItem(AppConfig.URL_PUBLIC + "Soundtrack/Item?soundtrackID=" + soundtrackID2,
                ServiceInterfaceTools.GETSOUNDITEM,
                new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        final SoundtrackBean soundtrackBean = (SoundtrackBean) object;
                        String duration = soundtrackBean.getDuration();
                        soundtrackID = soundtrackBean.getSoundtrackID();
                        openAudioSync(false, true, TextUtils.isEmpty(duration) ? 0 : Long.parseLong(duration));
                        if (soundtrackBean.getBackgroudMusicAttachmentID() != 0) {
                            GetMediaPlay(soundtrackBean.getBackgroudMusicInfo().getFileDownloadURL(), false);
                        } else {
                            GetMediaPlay("", false);
                        }
                        // 播放录音
                        if (soundtrackBean.getNewAudioAttachmentID() != 0) {
                            //1  拿 Bucket 信息
                            LoginGet lg = new LoginGet();
                            lg.setprepareUploadingGetListener(new LoginGet.prepareUploadingGetListener() {
                                @Override
                                public void getUD(final Uploadao ud) {
                                    // 2 调queryDownloading接口
                                    JsonDown2(ud, soundtrackBean);
                                }
                            });
                            lg.GetprepareUploading(SyncRoomActivity.this);
                        } else if (soundtrackBean.getSelectedAudioAttachmentID() != 0) {
                            GetMediaPlay2(soundtrackBean.getSelectedAudioInfo().getFileDownloadURL());
                        }
                    }
                });
    }


    /**
     * 老师开始上课
     */
    private void startCourse() {
        if (identity == 2) {
            JSONObject json = new JSONObject();
            try {
                json.put("meetingID", meetingId);
                json.put("sourceID", teacherid);
                json.put("targetID", studentid);
                json.put("incidentID", lessonId);
                json.put("roleType", "1");
                json.put("attachmentUrl", targetUrl);
                json.put("actionType", 11);
                json.put("isH5", false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            send_message("SEND_MESSAGE", AppConfig.UserToken, 0, "", Tools.getBase64(json.toString()).replaceAll("[\\s*\t\n\r]", ""));
            sendvoicenet("MEETING_STATUS", AppConfig.UserToken, meetingId, 1, lessonId);
        }
    }


    private DetchedPopup detchedPopup;

    private void detectPopwindow(int countDown) {
        if (detchedPopup == null) {
            detchedPopup = new DetchedPopup();
            detchedPopup.getPopwindow(SyncRoomActivity.this, (RelativeLayout) findViewById(R.id.layout), countDown);
            detchedPopup.setDetchedPopupListener(new DetchedPopup.DetchedPopupListener() {
                @Override
                public void closeNow() {
                    detchedPopup.dismiss();
                    endDialog();
                }
            });
            detchedPopup.StartPop(wv_show);
        }

    }

    private void endDialog() {
        final ConfirmDialog confirmDialog = new ConfirmDialog(this, getString(R.string.mtDoSaveMeeting), getString(R.string.mtSaveExit), getString(R.string.mtExit));
        confirmDialog.show();
        confirmDialog.setClicklistener(new ConfirmDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                confirmDialog.dismiss();
                new ApiTask(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = ConnectService.submitDataByJson(AppConfig.URL_PUBLIC + "Lesson/SaveInstantLesson?lessonID=" + lessonId, null);
                        Log.e("ennnnnnnnnnd", jsonObject.toString() + " ");
                        try {
                            if (jsonObject.getInt("RetCode") == 0) {
                                closeCourse(1);
                            } else {
                                finish();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start(((App) getApplication()).getThreadMgr());
            }

            @Override
            public void doCancel() {
                confirmDialog.dismiss();
                closeCourse(1);
            }
        });
    }


    //-----------------------------------------------video-------------------
    private VideoPopup videoPopuP;
    private final int VIDEOSTATUSPLAY = 1;
    private final int VIDEOSTATUSPAUSE = 0;
    private final int VIDEOSTATUSCLOSE = 2;
    private String currentPlayVideoId;
    private int videoDuration; //视频总长度

    private void initVideoPopup() {
        videoPopuP = new VideoPopup();
        videoPopuP.getPopwindow(SyncRoomActivity.this, screenWidth, (RelativeLayout) findViewById(R.id.bottomrl));
        // 两种情况
        videoView.setPlayPauseListener(new CustomVideoView.PlayPauseListener() {
            @Override
            public void onPlay() {
                if (isHavePresenter()) {
                    sendVideoSocket(VIDEOSTATUSPLAY, (float) (videoView.getCurrentPosition() / 1000), currentPlayVideoId, playUrl, 0); //播放
                }
            }

            @Override
            public void onPause() {
                if (isHavePresenter()) {
                    sendVideoSocket(VIDEOSTATUSPAUSE, (float) (videoView.getCurrentPosition() / 1000), currentPlayVideoId, playUrl, 0); //暂停
                }
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (isHavePresenter()) {
                    sendVideoSocket(VIDEOSTATUSCLOSE, (float) (videoView.getCurrentPosition() / 1000), currentPlayVideoId, playUrl, 0);  //  Close
                    videoPopuP.notifyDataChange(-1);
                    findViewById(R.id.videoll).setVisibility(View.GONE);
                    videoView.suspend();
                    videoView.setVisibility(View.GONE);
                    if (isFileVideo) {  // html
                        isFileVideo = false;
                    } else {
                    }
                }
                resumeYinxiang();
            }
        });

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                videoDuration = videoView.getDuration();
                mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                        int currentPosition = videoView.getCurrentPosition();
                        Log.e("onBufferingUpdate", "当前播放时间    " + currentPosition + "");
                    }
                });
            }
        });

        videoPopuP.setOnVideoListener(new VideoPopup.VideoListener() {
            @Override
            public void selectVideo() {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_CAPTURE_MEDIA);
            }

            @Override
            public void takePhoto() {
                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
            }

            @Override
            public void selectPlayVideo(final int position) {
                LineItem lineitem = videoList.get(position);
                if (!lineitem.isSelect()) {
                    videoPopuP.notifyDataChange(position);
                    findViewById(R.id.videoll).setVisibility(View.VISIBLE);
                    videoView.setVisibility(View.VISIBLE);
                    playVideo(lineitem.getUrl(), 0);
                    //播放通知
                    currentPlayVideoId = videoList.get(position).getAttachmentID();
                    playUrl = lineitem.getUrl();
                    sendVideoSocket(VIDEOSTATUSPLAY, (float) (videoView.getCurrentPosition() / 1000), currentPlayVideoId, playUrl, 0);
                }
            }

            @Override
            public void openSaveVideo() { // 打开我的收藏video
                LoginGet loginGet = new LoginGet();
                loginGet.setMyFavoritesGetListener(new LoginGet.MyFavoritesGetListener() {
                    @Override
                    public void getFavorite(ArrayList<Document> list) {
                        myFavoriteVideoList.clear();
                        myFavoriteVideoList.addAll(list);
                        openMySavePopup(myFavoriteVideoList, 2);
                    }
                });
                loginGet.MyFavoriteRequest(SyncRoomActivity.this, 2);
            }
        });
    }

    //-----------------------------------手势回调---------------------
    @Override
    public void onDown(MotionEvent e) {
        // 每次按下的时候更新当前亮度和音量，还有进度
        oldProgress = newProgress;
        oldVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        brightness = mLayoutParams.screenBrightness;
        if (brightness == -1) {
            //一开始是默认亮度的时候，获取系统亮度，计算比例值
            brightness = mBrightnessHelper.getBrightness() / 255f;
        }
    }

    @Override
    public void onEndFF_REW(MotionEvent e) {  // 0---100
        if (identity == 1) { // 学生
            if (TextUtils.isEmpty(studentCustomer.getUserID())) {
                return;
            }
            if (studentCustomer.getUserID().equals(AppConfig.UserID.replace("-", ""))) {
                videoView.seekTo(newProgress * videoDuration / 100);
                sendVideoSocket(VIDEOSTATUSPLAY, (float) (videoView.getCurrentPosition() / 1000), currentPlayVideoId, playUrl, 0); //播放
            }
        } else if (identity == 2) { //老师端 绘制
            if (currentPresenterId.equals(teacherCustomer.getUserID())) {
                videoView.seekTo(newProgress * videoDuration / 100);
                sendVideoSocket(VIDEOSTATUSPLAY, (float) (videoView.getCurrentPosition() / 1000), currentPlayVideoId, playUrl, 0); //播放
            }
        }
    }

    //音量回调
    @Override
    public void onVolumeGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        int value = videoGestureRelativeLayout.getHeight() / maxVolume;  //最大音量
        int newVolume = (int) ((e1.getY() - e2.getY()) / value + oldVolume);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, AudioManager.FLAG_PLAY_SOUND);
        //要强行转Float类型才能算出小数点，不然结果一直为0
        int volumeProgress = (int) (newVolume / Float.valueOf(maxVolume) * 100);
        if (volumeProgress >= 50) {
            showChangeLayout.setImageResource(R.drawable.volume_higher_w);
        } else if (volumeProgress > 0) {
            showChangeLayout.setImageResource(R.drawable.volume_lower_w);
        } else {
            showChangeLayout.setImageResource(R.drawable.volume_off_w);
        }
        showChangeLayout.setProgress(volumeProgress);  //设置进度条
        showChangeLayout.show();
    }


    /**
     * APP亮度的方法
     *
     * @param e1
     * @param e2
     * @param distanceX
     * @param distanceY
     */
    @Override
    public void onBrightnessGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        float newBrightness = (e1.getY() - e2.getY()) / videoGestureRelativeLayout.getHeight();
        newBrightness += brightness;
        if (newBrightness < 0) {
            newBrightness = 0;
        } else if (newBrightness > 1) {
            newBrightness = 1;
        }
        mLayoutParams.screenBrightness = newBrightness;
        mWindow.setAttributes(mLayoutParams);
        showChangeLayout.setProgress((int) (newBrightness * 100));
        showChangeLayout.setImageResource(R.drawable.brightness_w);
        showChangeLayout.show();
    }

    /**
     * 快进快退手势，手指在Layout左右滑动的时候调用
     */
    @Override
    public void onFF_REWGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        float offset = e2.getX() - e1.getX();
        //根据移动的正负决定快进还是快退
        if (offset > 0) {
            showChangeLayout.setImageResource(R.drawable.ff);
            newProgress = (int) (oldProgress + offset / videoGestureRelativeLayout.getWidth() * 100);
            if (newProgress > 100) {
                newProgress = 100;
            }
        } else {
            showChangeLayout.setImageResource(R.drawable.fr);
            newProgress = (int) (oldProgress + offset / videoGestureRelativeLayout.getWidth() * 100);
            if (newProgress < 0) {
                newProgress = 0;
            }
        }
        showChangeLayout.setProgress(newProgress);
        showChangeLayout.show();
    }

    @Override
    public void onSingleTapGesture(MotionEvent e) {

    }

    @Override
    public void onDoubleTapGesture(MotionEvent e) {

    }
    //-----------------------------------手势回调---------------------

    private FavoritePopup favoriteFilePopup;

    private void openMySavePopup(final List<Document> favoriteList, final int type) {
        favoriteFilePopup = new FavoritePopup();
        if (uploadFavorList.size() > 0) {
            favoriteList.addAll(uploadFavorList);
        }
        favoriteFilePopup.getPopwindow(SyncRoomActivity.this, favoriteList, type);
        favoriteFilePopup.setFavoritePoPListener(new FavoritePopup.FavoritePoPListener() {
            @Override
            public void dismiss() {
            }

            @Override
            public void open() {
            }

            @Override
            public void selectFavorite(int position) {
                favoriteFilePopup.dismiss();
                final Document favorite = favoriteList.get(position);
                new ApiTask(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject js = new JSONObject();
                            js.put("lessonID", lessonId);
                            js.put("itemIDs", favorite.getItemID());
                            JSONObject jsonObject = ConnectService.submitDataByJson
                                    (AppConfig.URL_PUBLIC + "EventAttachment/UploadFromFavorite?lessonID=" + lessonId + "&itemIDs=" + favorite.getItemID(), js);
                            Log.e("save_file", js.toString() + "   " + jsonObject.toString());
                            Message msg = Message.obtain();
                            msg.what = 0x6002;
                            msg.obj = type;
                            handler.sendMessage(msg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start(ThreadManager.getManager());

            }

            @Override
            public void uploadFile() {
                favoriteFilePopup.dismiss();
                AddAlbum();
            }
        });
        favoriteFilePopup.StartPop(wv_show);
    }

    private void AddAlbum() {
        PopAlbums pa = new PopAlbums();
        pa.getPopwindow(getApplicationContext());
        pa.setPoPDismissListener(new PopAlbums.PopAlbumsDismissListener() {
            @Override
            public void PopDismiss(boolean isAdd) {
                if (isAdd) {
                    GetVideo();
                }
            }

            @Override
            public void PopDismissPhoto(boolean isAdd) {

            }

            @Override
            public void PopBack() {

            }
        });
        pa.StartPop(wv_show);
    }

    private void GetVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_CAPTURE_SAVE_MEDIA);
    }


    /**
     * @param state        当前播放状态 status == 0,暂停，== 1 播放，== 2:Close
     * @param time
     * @param attachmentID 视频vid
     * @param url          video url
     * @param videotype    video type 0: video in list 1: video in file
     */
    private void sendVideoSocket(int state, float time, String attachmentID, String url, int videotype) {
        JSONObject json = new JSONObject();
        try {
            json.put("stat", state);
            json.put("actionType", 19);
            json.put("time", time);
            json.put("vid", attachmentID);
            json.put("url", url);
            json.put("type", isFileVideo ? 1 : 0);
            //录音想  过程中播放视频  结束不存入actions
            if (isSync && !isPause) {
                if (state == 1) {
                    pauseOrStartAudioRecord();
                    json.put("savetime", tttime);
                    json.put("save", state);
                    pauseMedia();
                    pauseMedia2();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        send_message("SEND_MESSAGE", AppConfig.UserToken, 0, "", Tools.getBase64(json.toString()).replaceAll("[\\s*\t\n\r]", ""));
    }


    /**
     * 播放音频socket
     *
     * @param state
     * @param
     */
    private void sendAudioSocket(int state, int soundtrackID) {
        JSONObject json = new JSONObject();
        try {
            json.put("actionType", 23);
            json.put("soundtrackId", soundtrackID);
            json.put("stat", state);
            if (isHavePresenter() && state == 4) {
                json.put("time", tttime);
            }
            if (isHavePresenter() && state == 5) {
                json.put("time", tttime);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        send_message("SEND_MESSAGE", AppConfig.UserToken, 0, "", Tools.getBase64(json.toString()).replaceAll("[\\s*\t\n\r]", ""));
    }


    /**
     * 录制socket
     */
    private boolean isSync = false;
    private int endRecordYinxiangTime;
    private long startTime;

    private void sendSync(String action, String sessionId, int status, int audioitemid) {
        if (status == 1) {
            isSync = true;
            startTime = System.currentTimeMillis();
            Log.e("refreshTime 开始", startTime + "");
            getjspagenumber();
        } else {
            isSync = false;
        }
        try {
            JSONObject loginjson = new JSONObject();
            loginjson.put("action", action);
            loginjson.put("sessionId", sessionId);
            loginjson.put("status", status);
            loginjson.put("soundtrackId", audioitemid);
            if (status == 0) {
                loginjson.put("duration", tttime);
                endRecordYinxiangTime = tttime;
                Log.e("refreshTime 结束", "   time" + endRecordYinxiangTime);

                //录音结束
                String url2 = AppConfig.URL_PUBLIC + "Soundtrack/EndSync?soundtrackID=" + soundtrackID + "&syncDuration=" + endRecordYinxiangTime;
                ServiceInterfaceTools.getinstance().endSync(url2, ServiceInterfaceTools.ENDSYNC, new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {

                    }
                });
                findViewById(R.id.recordstatus).setVisibility(View.GONE);
            } else if (status == 1) {
                findViewById(R.id.recordstatus).setVisibility(View.VISIBLE);
            }
            String ss = loginjson.toString();
            if (status == 1) {
                SpliteSocket.sendMesageBySocket(ss);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getjspagenumber() {
        wv_show.evaluateJavascript("javascript:GetCurrentPageNumber()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
                Log.e("GetCurrentPageNumber", s);
                int id = 1;
                if (TextUtils.isEmpty(s)) {

                } else {
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
                String newresult = Tools.getBase64(js.toString()).replaceAll("[\\s*\t\n\r]", "");
                try {
                    JSONObject loginjson = new JSONObject();
                    loginjson.put("action", "ACT_FRAME");
                    loginjson.put("sessionId", teacherCustomer.getUsertoken());
                    loginjson.put("retCode", 1);
                    loginjson.put("data", newresult);
                    loginjson.put("itemId", currentItemId);
                    loginjson.put("sequenceNumber", "3837");
                    loginjson.put("ideaType", "document");
                    String ss = loginjson.toString();
                    SpliteSocket.sendMesageBySocket(ss);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    /**
     * 学生收到消息是否播放视频
     *
     * @param stat
     * @param time
     * @param attachmentId
     */
    private void startOrPauseVideo(int stat, float time, String attachmentId, String url, int videotype) {
        Log.e("学生收到消息是否播放视频", stat + "    " + time + "         " + attachmentId + "    " + url + "    " + videotype);
        if (stat == VIDEOSTATUSPLAY) {  //play
            if (videoView.isPlaying()) {
                videoView.seekTo((int) time * 1000);
            } else {
                // 找到播放地址---------------------------
                String playurl = "";
                if (videotype == 0) {  //
                    for (int i = 0; i < videoList.size(); i++) {
                        LineItem item = videoList.get(i);
                        if (item.getAttachmentID().equals(attachmentId)) {
                            videoPopuP.notifyDataChange(i);
                            playurl = item.getUrl();
                            break;
                        }
                    }
                } else if (videotype == 1) {
                    playurl = url;
                }
                //找到播放地址---------------------------
                if (!TextUtils.isEmpty(playurl)) {
                    findViewById(R.id.videoll).setVisibility(View.VISIBLE);
                    videoView.setVisibility(View.VISIBLE);
                    playVideo(playurl, time);
                }
            }
        } else if (stat == VIDEOSTATUSPAUSE) { //pause
            if (videoView.isPlaying()) {
                videoView.pause();
            }
        } else if (stat == VIDEOSTATUSCLOSE) {  // close
            videoPopuP.notifyDataChange(-1);
            findViewById(R.id.videoll).setVisibility(View.GONE);
            videoView.suspend();
            videoView.setVisibility(View.GONE);
            resumeYinxiang();

        }

    }

    private boolean isFileVideo = false;
    /**
     * html 播放视频
     *
     * @param attachmentId
     */
    String playUrl;

    private void webVideoPlay(final int vid, final boolean playorrecord, final int isRecording) {
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject returnjson = ConnectService.getIncidentbyHttpGet(AppConfig.URL_PUBLIC +
                            "FavoriteAttachment/GetFavoriteAttachmentByID?itemID=" + vid);
                    Log.e("MyFavoriteAttachments", returnjson.toString());
                    if (returnjson.getInt("RetCode") == 0) {
                        final JSONObject jsonObject = returnjson.getJSONObject("RetData");
                        final String url = jsonObject.getString("AttachmentUrl");
                        final int filetype = jsonObject.getInt("FileType");
                        favoriteAudio = new Document();
                        favoriteAudio.setFileDownloadURL(jsonObject.getString("AttachmentUrl"));
                        favoriteAudio.setItemID(jsonObject.getInt("ItemID") + "");
                        favoriteAudio.setTitle(jsonObject.getString("Title"));
                        favoriteAudio.setAttachmentID(jsonObject.getInt("AttachmentID") + "");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (filetype == 4) {  //处理视频
                                    findViewById(R.id.videoll).setVisibility(View.VISIBLE);
                                    videoView.setVisibility(View.VISIBLE);
                                    playUrl = url;
                                    isFileVideo = true;
                                    playVideo(playUrl, 0.0f);
                                    // changevideo(4, "");  // 当前仍处于声网模式
                                    //播放通知
                                    currentPlayVideoId = vid + "";

                                    sendVideoSocket(VIDEOSTATUSPLAY, (float) (videoView.getCurrentPosition() / 1000), currentPlayVideoId, playUrl, 1); //1 html 播放
                                } else if (filetype == 5) {  // 处理音频
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());

    }


    private AudioRecorder audioRecorder;

    private void startAudioRecord() {
        if (ContextCompat.checkSelfPermission(SyncRoomActivity.this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(SyncRoomActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        }
        if (audioRecorder != null) {
            audioRecorder.canel();  //取消录音
        }
        audioRecorder = AudioRecorder.getInstance();
        try {
            if (audioRecorder.getStatus() == AudioRecorder.Status.STATUS_NO_READY) {
                Log.e("audioooooooooo", "startAudioRecord");
                String fileName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
                audioRecorder.createDefaultAudio(fileName);
                audioRecorder.startRecord(null);
            }
        } catch (IllegalStateException e) {
            Toast.makeText(SyncRoomActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void stopAudioRecord(final int soundtrackID) {
        if (audioRecorder != null) {
            audioRecorder.stopRecord(new RecordEndListener() {
                @Override
                public void endRecord(String fileName) {
                    Log.e("audioooooooooo", "录音结束，开始上传 " + fileName);
                    File file = com.ub.service.audiorecord.FileUtils.getWavFile(fileName);
                    if (file != null) {
                        Log.e("audioooooooooo", file.getAbsolutePath() + "   " + file.getName());
                        uploadAudioFile(file, soundtrackID, false, false);
                    }
                }
            });
            audioRecorder = null;
        }
    }

    private void pauseOrStartAudioRecord() {
        if (audioRecorder != null) {
            if (audioRecorder.getStatus() == AudioRecorder.Status.STATUS_START) {
                audioRecorder.pauseRecord();
                Log.e("是否录音", "false");
            } else {
                audioRecorder.startRecord(null);
                Log.e("是否录音", "true");
            }
        }
    }


    /**
     * 拿随 某时刻后面 20秒  音向 的Actions
     *
     * @param
     */
    private List<AudioActionBean> audioActionBeanList = new ArrayList<>();
    private int startTimee;

    private void getAudioAction(final int soundtrackID, int startTime) {
        startTimee = startTime;
        String url = AppConfig.URL_PUBLIC + "Soundtrack/SoundtrackActions?soundtrackID=" + soundtrackID + "&startTime=" + startTime + "&endTime=" + (startTime + 20000);
        ServiceInterfaceTools.getinstance().getSoundtrackActions(url, ServiceInterfaceTools.GETSOUNDTRACKACTIONS, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                List<AudioActionBean> audioActionBeanList2 = (List<AudioActionBean>) object;
                audioActionBeanList.clear();
                if (audioActionBeanList2.size() > 0) {
                    audioActionBeanList.addAll(audioActionBeanList2);
                }
            }
        });
    }

    private void newAudioActionTime(int locateTime) {
        Log.e("newAudioActionTime", audioActionBeanList.size() + "    当前播放器的位置 " + locateTime);
        for (int i1 = 0; i1 < audioActionBeanList.size(); i1++) {
            AudioActionBean audioActionBean = audioActionBeanList.get(i1);
            Log.e("newAudioActionTime", locateTime + "   " + audioActionBean.getTime() + "      " + audioActionBean.getData());
            if (locateTime >= audioActionBean.getTime()) {
                String data = audioActionBean.getData();
                if (doVideoAction(data)) { //存在  视频文件播放
                    audioActionBeanList.remove(i1);
                    i1--;
                } else {  // 不存在 视频文件播放
                    Message msg3 = Message.obtain();
                    msg3.obj = data;
                    msg3.what = 0x1109;
                    handler.sendMessage(msg3);

                    audioActionBeanList.remove(i1);
                    i1--;
                }
            } else {
                break;
            }
        }
        if (locateTime > (startTimee + 10000)) {
            getAudioAction(soundtrackID, locateTime);
        }
    }

    private boolean doVideoAction(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            int actionType = jsonObject.getInt("actionType");
            if (actionType == 19) {
                final int stat = jsonObject.getInt("stat");
                if (stat == 1) { // 播放视频  暂停音响的播放
                    pauseMedia();
                    pauseMedia2();
                } else if (stat == 2) {
                    resumeMedia();
                    resumeMedia2();
                }
                final float time = jsonObject.getInt("time");
//                final String attachmentId = jsonObject.getString("vid");
                final String url = jsonObject.getString("url");
                final int videotype = jsonObject.getInt("type");
                startOrPauseVideo(stat, time, "", url, videotype);
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }


    private void resumeYinxiang() {
        if (audiosyncll != null) {  //处于播音想的状态
            if (isSync && isPause) {  // 重新录音
                pauseOrStartAudioRecord();
                resumeMedia();
                resumeMedia2();
            }
            if (audiosyncll.getVisibility() == View.VISIBLE && isPlaying2) { //重新播放音想
                if (isPause) {
                    resumeMedia();
                    resumeMedia2();
                }
            }

        }
    }


    private void uploadAudioFile(File file, int vid, final boolean isagorareco, final boolean isClose) {
        Log.e("setProgressCallback1", file.getAbsolutePath() + "   " + file.getName());
        LineItem attachmentBean = new LineItem();
        attachmentBean.setUrl(file.getAbsolutePath()); // 文件的路径
        attachmentBean.setFileName(file.getName()); // 文件名
        uploadFile2(attachmentBean, isagorareco, true);
    }


    /**
     * start video
     *
     * @param path
     * @param time
     */
    private void playVideo(final String path, final float time) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Uri uri = Uri.parse(path);
                videoView.setVideoURI(uri);
                if (!videoView.isPlaying()) {
                    videoView.start();
                }
                videoView.seekTo((int) time * 1000);
                if (isHavePresenter()) {
                    MediaController mc = new MediaController(SyncRoomActivity.this);
                    mc.setVisibility(View.VISIBLE);
                    closeVideo.setVisibility(View.VISIBLE);
                    videoView.setMediaController(mc);
                } else {
                    MediaController mc = new MediaController(SyncRoomActivity.this);
                    mc.setVisibility(View.INVISIBLE);
                    closeVideo.setVisibility(View.GONE);
                    videoView.setMediaController(mc);
                }
            }
        });
    }


    /**
     * 获得PDF文档
     */
    private void getSyncRoomDetail() {
        if (lessonId.equals("-1")) {
            return;
        }
        String url = AppConfig.URL_PUBLIC + "SyncRoom/Item?itemId=" + lessonId;
        MeetingServiceTools.getInstance().getSyncroomDetail(url, MeetingServiceTools.GETTOPICATTACHMENT, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                Log.e("getSyncroomDetail", "object:" + object);
                List<LineItem> list = (List<LineItem>) object;
                Log.e("getSyncroomDetail", "list:" + list);
                documentList.clear();
                documentList.addAll(list);
                initpdfdata(documentList);
            }
        });
    }

    private void initpdfdata(final List<LineItem> documentList) {
        for (int i = 0; i < documentList.size(); i++) {
            String id = documentList.get(i).getItemId();
            if (!TextUtils.isEmpty(currentItemId)) {
                if (currentItemId.equals(id)) {
                    documentList.get(i).setSelect(true);
                }
            }
            for (int j = 0; j < uploadList.size(); j++) {
                String id2 = uploadList.get(j).getItemId();
                if (id.equals(id2)) {  // uploadList来不及删除
                    uploadList.remove(j);
                    j = j - 1;
                }
            }
        }
        documentList.addAll(uploadList);

        if (documentList.size() > 0) {
            findViewById(R.id.defaultpagehaha).setVisibility(View.GONE);
        } else {
            findViewById(R.id.defaultpagehaha).setVisibility(View.VISIBLE);
            TextView tv = findViewById(R.id.ashoast);
            if (ishavedefaultpage && identity == 2) {
                findViewById(R.id.ppromat).setVisibility(View.GONE);
                tv.setText(getString(R.string.shYcan));
                createblabkpage.setOnClickListener(this);
                inviteattendee.setOnClickListener(this);
                sharedocument.setOnClickListener(this);
            } else if (ishavedefaultpage) {
                findViewById(R.id.ppromat).setVisibility(View.VISIBLE);
                tv.setText(getString(R.string.shAcan));
                createblabkpage.setVisibility(View.GONE);
            } else {
                findViewById(R.id.defaultpagehaha).setVisibility(View.GONE);
            }
        }
        for (int i = 0; i < documentList.size(); i++) {
            LineItem lineItem2 = documentList.get(i);
            if (TextUtils.isEmpty(currentItemId) || currentItemId.equals("0")) {
                currentShowPdf = documentList.get(0);
            } else {
                if ((currentItemId).equals(lineItem2.getItemId())) {
                    currentShowPdf = lineItem2;
                    break;
                }
            }
        }
        if (isLoadPdfAgain && documentList.size() > 0) {
            isLoadPdfAgain = false;
            LineItem lineitem = new LineItem();
            lineitem.setItemId(currentItemId);
            lineitem.setAttachmentID(currentShowPdf.getAttachmentID());
            if (isJoinNote) {
                lineitem.setDocType(1);
            } else {
                lineitem.setDocType(0);
            }
            AppConfig.currentPageNumber = currentAttachmentPage;
            followChangeFile(lineitem, currentAttachmentPage);
            if (!TextUtils.isEmpty(currentItemId) && !currentItemId.equals("0")) {
                notifySwitchDocumentSocket(lineitem, currentAttachmentPage, lineitem.getDocType());
            }
        }
    }


    /**
     * 结束课程   关闭此meeting
     */
    private void closeCourse(int id) {
        if (mWebSocketClient != null) {
            try {
                JSONObject loginjson = new JSONObject();
                if (id == 1) {
                    loginjson.put("action", "LEAVE_MEETING");
                } else if (id == 0) {
                    loginjson.put("action", "LEAVE_MEETING");
                }
                loginjson.put("sessionId", AppConfig.UserToken);
                loginjson.put("meetingId", meetingId);
                String ss = loginjson.toString();
                SpliteSocket.sendMesageBySocket(ss);
            } catch (JSONException e) {
                finish();
                e.printStackTrace();
            }
        }
        finish();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            final ConfirmDialog confirmDialog = new ConfirmDialog(this, getString(R.string.mtDoLeaveMeeting), getString(R.string.Yes), getString(R.string.No));
            confirmDialog.show();
            confirmDialog.setClicklistener(new ConfirmDialog.ClickListenerInterface() {
                @Override
                public void doConfirm() {
                    closeCourse(0);
                    finish();
                }

                @Override
                public void doCancel() {
                    confirmDialog.dismiss();
                }
            });
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }


    //-------------------------------------------------------------------------------------------------
    private static final int REQUEST_CODE_CAPTURE_ALBUM = 0;
    private static final int REQUEST_CODE_CAPTURE_CAMEIA = 1;
    private static final int REQUEST_CODE_CAPTURE_MEDIA = 2;
    private static final int REQUEST_CODE_TAKE_PHOTO = 3;
    private static final int REQUEST_CODE_CAPTURE_SAVE_MEDIA = 4;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 系统相册返回
        if (requestCode == REQUEST_CODE_CAPTURE_ALBUM && resultCode == Activity.RESULT_OK
                && data != null) {                                   // 选择照片
            String path = FileUtils.getPath(this, data.getData());
            String pathname = path.substring(path.lastIndexOf("/") + 1);
            Log.e("path", path + "    " + pathname + "   " + data.getData());
            LineItem attachmentBean = new LineItem();
            attachmentBean.setUrl(path); // 文件的路径
            attachmentBean.setFileName(pathname);
            uploadFile(attachmentBean);
        }
        // 系统相机返回
        if (requestCode == REQUEST_CODE_CAPTURE_CAMEIA && resultCode == Activity.RESULT_OK) {      // 拍照
            String name;
            name = mFilePath;
            Log.e("duang", name + "   " + mFilePath);
            File localFile = new File(cache, name);

            String path = localFile.getPath();
            String pathname = path.substring(path.lastIndexOf("/") + 1);

            Log.e("pathname", path + "   " + pathname);
            LineItem attachmentBean = new LineItem();
            attachmentBean.setUrl(path); // 文件的路径
            attachmentBean.setFileName(pathname); // 文件名
            uploadFile(attachmentBean);

        }
        if (requestCode == REQUEST_CODE_CAPTURE_MEDIA && resultCode == Activity.RESULT_OK) { // // 选择视频
            Uri uri = data.getData();
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
            cursor.close();
            LineItem attachmentBean = new LineItem();
            attachmentBean.setUrl(path); // 文件的路径
            String title = path.substring(path.lastIndexOf("/") + 1);
            attachmentBean.setFileName(title); // 文件名
            uploadFile(attachmentBean);
        }
        if (requestCode == REQUEST_CODE_TAKE_PHOTO && resultCode == RESULT_OK) {     //  拍摄视频
            Uri uri = data.getData();
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
            Log.e("-----", "path=" + path);
            LineItem attachmentBean = new LineItem();
            attachmentBean.setUrl(path);
            String title = path.substring(path.lastIndexOf("/") + 1);
            attachmentBean.setFileName(title);
            uploadFile(attachmentBean);

        }

        if (requestCode == REQUEST_CODE_TAKE_PHOTO && resultCode == RESULT_CANCELED) {

        }

        if (requestCode == REQUEST_CODE_CAPTURE_SAVE_MEDIA && resultCode == RESULT_OK) {   // 选择视频
            Uri uri = data.getData();
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
            cursor.close();
            String title = path.substring(path.lastIndexOf("/") + 1);
            UpdateVideo(path, title);
        }

        if (requestCode == 100 && resultCode == RESULT_OK) {
//            Log.e("NoteBook","NoteBook return,data:" + data);
            if (wv_show != null) {
                String json = data.getStringExtra("OPEN_NOTE_BEAN_JSON");
                BookNote note = new Gson().fromJson(json, BookNote.class);
                uploadNote(note);
            }
        }

        if (wv_show != null) {
//            wv_show.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void UpdateVideo(final String path1, final String title) {
        final LineItem attachmentBean = new LineItem();
        attachmentBean.setUrl(path1);
        attachmentBean.setFileName(title);
        File file = FileGetTool.GetFile(attachmentBean);
        if (file.exists()) {
            try {
                String url = AppConfig.URL_PUBLIC + "FavoriteAttachment/UploadFileWithHash?Title="
                        + URLEncoder.encode(LoginGet.getBase64Password(title), "UTF-8") + "&Hash=" +
                        Md5Tool.getMd5ByFile(file);
                MeetingServiceTools.getInstance().uploadFileWithHash(url, MeetingServiceTools.UPLOADFILEWITHHASH, new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        try {
                            JSONObject responsedata = (JSONObject) object;
                            String retcode = responsedata.getString("RetCode");
                            if (retcode.equals(AppConfig.RIGHT_RETCODE)) {  // 刷新

                            } else if (retcode.equals(AppConfig.Upload_NoExist + "")) { // 添加
                                JSONObject jsonObject = responsedata.getJSONObject("RetData");
                                targetFolderKey = jsonObject.getString("Path");
                                field = jsonObject.getInt("FileID");
                                DocumentUploadUtil duu = new DocumentUploadUtil();
                                duu.setUpdateGetListener(new DocumentUploadUtil.UpdateGetListener() {
                                    @Override
                                    public void Update() {
                                        if (favoritePopup != null) {
                                            favoritePopup.setData(2, false);
                                        }
                                    }
                                });
                                duu.uploadVideoFavorite(SyncRoomActivity.this, targetFolderKey, field,
                                        attachmentBean, wv_show);
                            } else if (retcode.equals(AppConfig.Upload_Exist + "")) { //不要重复上传
                                String errorMessage = responsedata
                                        .getString("ErrorMessage");
                                Toast.makeText(getApplicationContext(), errorMessage,
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.nofile),
                    Toast.LENGTH_LONG).show();
        }
    }

    private Popupdate puo;

    public void uploadFile(LineItem attachmentBean) {
        isAddMyFavor(attachmentBean);
    }


    /**
     * 是否添加到我的收藏
     *
     * @param attachmentBean
     * @param
     */
    private void isAddMyFavor(final LineItem attachmentBean) {
        final Dialog passdDialog = new Dialog(this);
        View view2 = LayoutInflater.from(this).inflate(
                R.layout.isaddmyfavordialog, null);
        final CheckBox checkBox = (CheckBox) view2.findViewById(R.id.isaddfavorcb);
        checkBox.setChecked(false);
        TextView cancel = (TextView) view2.findViewById(R.id.cancel);
        TextView save = (TextView) view2.findViewById(R.id.save);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                passdDialog.dismiss();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                passdDialog.dismiss();
                UploadFileWithHash(attachmentBean, checkBox.isChecked());
            }
        });
        passdDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        passdDialog.setContentView(view2);
        Window dlgWindow = passdDialog.getWindow();
        dlgWindow.setGravity(Gravity.CENTER);
        WindowManager m = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = dlgWindow.getAttributes();
        p.width = (int) (d.getWidth() * 0.8);
        dlgWindow.setAttributes(p);
        passdDialog.setCanceledOnTouchOutside(false);
        passdDialog.show();

    }

    private boolean isAddToFavorite; //是否添加到收藏
    private String targetFolderKey;
    private int field;

    private void UploadFileWithHash(final LineItem attachmentBean, final boolean isAddToFavorite) {
        this.isAddToFavorite = isAddToFavorite;
        String title = attachmentBean.getFileName();
        File file = FileGetTool.GetFile(attachmentBean);
        if (file.exists()) {
            try {
                String url = AppConfig.URL_PUBLIC + "TopicAttachment/UploadFileWithHash?topicID=" + lessonId + "&Title="
                        + URLEncoder.encode(LoginGet.getBase64Password(title), "UTF-8") + "&Hash=" +
                        Md5Tool.getMd5ByFile(file) + "&IsAddToFavorite=" + (isAddToFavorite ? 1 : 0);
                MeetingServiceTools.getInstance().uploadFileWithHash(url, MeetingServiceTools.UPLOADFILEWITHHASH, new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        try {
                            JSONObject responsedata = (JSONObject) object;
                            String retcode = responsedata.getString("RetCode");
                            if (retcode.equals(AppConfig.RIGHT_RETCODE)) {  //刷新
                                getSyncRoomDetail();
                            } else if (retcode.equals(AppConfig.Upload_NoExist + "")) { // 添加
                                JSONObject jsonObject = responsedata.getJSONObject("RetData");
                                targetFolderKey = jsonObject.getString("Path");
                                field = jsonObject.getInt("FileID");
                                uploadFile2(attachmentBean, false, false);
                            } else if (retcode.equals(AppConfig.Upload_Exist + "")) { //不要重复上传
                                String errorMessage = responsedata
                                        .getString("ErrorMessage");
                                Toast.makeText(getApplicationContext(), errorMessage,
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.nofile),
                    Toast.LENGTH_LONG).show();
        }
    }


    private List<LineItem> uploadList = new ArrayList<>();

    /**
     * @param attachmentBean
     * @param isAgoraRecord  是否录制课程 合并录音
     * @param isrecording    是否上传录音文件 区别普通文件
     */
    public void uploadFile2(final LineItem attachmentBean, final boolean isAgoraRecord, final boolean isrecording) {

        LoginGet lg = new LoginGet();
        lg.setprepareUploadingGetListener(new LoginGet.prepareUploadingGetListener() {
            @Override
            public void getUD(Uploadao ud) {
                if (!isrecording) {
                    attachmentBean.setAttachmentID(-1 + "");
                    attachmentBean.setFlag(1);
                    uploadList.add(attachmentBean);
                    documentList.add(attachmentBean);
                } else {  //音响
                    puo = new Popupdate();
                    puo.getPopwindow(SyncRoomActivity.this, fileName);
                    puo.StartPop(wv_show);
                }
                if (1 == ud.getServiceProviderId()) {
                    uploadWithTransferUtility(attachmentBean, ud, isrecording);
                } else if (2 == ud.getServiceProviderId()) {
                    initOSS(attachmentBean, ud, isrecording);
                }
            }
        });
        lg.GetprepareUploading(this);
    }


    public void uploadWithTransferUtility(final LineItem attachmentBean, final Uploadao ud, final boolean isrecording) {
        mfile = new File(attachmentBean.getUrl());
        fileName = mfile.getName();
        String name2 = AppConfig.UserID + mfile.getName();
        MD5Hash = Md5Tool.transformMD5(name2);
        if (isrecording) {
            MD5Hash = fieldNewPath + "/" + fileName;
        }
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(
                        ud.getAccessKeyId(),
                        ud.getAccessKeySecret(),
                        ud.getSecurityToken());
                AmazonS3Client s3 = new AmazonS3Client(sessionCredentials);
                s3.setRegion(com.amazonaws.regions.Region.getRegion(ud.getRegionName()));
                com.amazonaws.services.s3.model.PutObjectRequest request = new com.amazonaws.services.s3.model.PutObjectRequest(ud.getBucketName(), MD5Hash, mfile);
                TransferManager tm = new TransferManager(s3);
                request.setCannedAcl(CannedAccessControlList.PublicRead);
                request.setGeneralProgressListener(new ProgressListener() {
                    @Override
                    public void progressChanged(final ProgressEvent progressEvent) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!isrecording) {

                                } else {
                                    puo.setProgress(mfile.length(), progressEvent.getBytesTransferred());
                                }
                            }
                        });
                    }
                });
                Upload upload = tm.upload(request);
                try {
                    upload.waitForCompletion();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!isrecording) {
                                attachmentBean.setFlag(2);

                                startConverting(ud, attachmentBean);
                            } else {
                                puo.DissmissPop();
                                yinxiangUploadNewFile(ud);
                            }
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start(ThreadManager.getManager());
    }

    private OSS oss;

    private void initOSS(final LineItem attachmentBean, final Uploadao ud, final boolean isrecording) {
        new ApiTask(new Runnable() {
            @Override
            public void run() {
                OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(ud.getAccessKeyId(),
                        ud.getAccessKeySecret(), ud.getSecurityToken());
                ClientConfiguration conf = new ClientConfiguration();
                conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
                conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
                conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个
                conf.setMaxErrorRetry(2);  // 失败后最大重试次数，默认2次
                OSSLog.enableLog();
                oss = new OSSClient(getApplicationContext(), ud.getRegionName() + ".aliyuncs.com", credentialProvider, conf);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        uploadFile3(attachmentBean, ud, isrecording);
                    }
                });
            }
        }).start(ThreadManager.getManager());
    }

    private File mfile;
    private String MD5Hash;
    private String fileName;

    private void uploadFile3(final LineItem attachmentBean, final Uploadao ud, final boolean isrecording) {
        Log.e("setProgressCallback", attachmentBean.getUrl() + "   " + attachmentBean.getFileName());
        String path = attachmentBean.getUrl();
        mfile = new File(path);
        fileName = mfile.getName();
        String name2 = AppConfig.UserID + mfile.getName();
        MD5Hash = Md5Tool.transformMD5(name2);
        if (isrecording) {
            MD5Hash = fieldNewPath + "/" + fileName;
        }
        Log.e("setProgressCallback", fileName + "   " + MD5Hash);
        String recordDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/oss_record/";
        File recordDir = new File(recordDirectory);
        // 要保证目录存在，如果不存在则主动创建
        if (!recordDir.exists()) {
            recordDir.mkdirs();
        }
        // 创建断点上传请求，参数中给出断点记录文件的保存位置，需是一个文件夹的绝对路径
        ResumableUploadRequest request = new ResumableUploadRequest(ud.getBucketName(),
                MD5Hash, path, recordDirectory);
        //设置false,取消时，不删除断点记录文件，如果不进行设置，默认true，是会删除断点记录文件，下次再进行上传时会重新上传。
        request.setDeleteUploadOnCancelling(false);
        request.setPartSize(1 * 1024 * 1024);
        // 设置上传过程回调
        request.setProgressCallback(new OSSProgressCallback<ResumableUploadRequest>() {
            @Override
            public void onProgress(ResumableUploadRequest request, final long currentSize, final long totalSize) {
                Log.e("setProgressCallback", currentSize + "   " + totalSize);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isrecording) {
                        } else {
                            puo.setProgress(totalSize, currentSize);
                        }
                    }
                });
            }
        });
        oss.asyncResumableUpload(request, new OSSCompletedCallback<ResumableUploadRequest, ResumableUploadResult>() {
            @Override
            public void onSuccess(ResumableUploadRequest request, ResumableUploadResult result) {
                if (!isrecording) {
                    attachmentBean.setFlag(2);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            startConverting(ud, attachmentBean);
                        }
                    });
                } else {
                    Log.e("setProgressCallback", "  successsss");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            puo.DissmissPop();
                            yinxiangUploadNewFile(ud);
                        }
                    });
                }
            }

            @Override
            public void onFailure(ResumableUploadRequest request, ClientException clientExcepion, ServiceException serviceException) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isrecording) {
                            puo.DissmissPop();
                        }
                    }
                });
            }
        });

    }


    private void yinxiangUploadNewFile(final Uploadao ud) {
        ServiceInterfaceTools.getinstance().yinxiangUploadNewFile(AppConfig.URL_PUBLIC + "Soundtrack/UploadNewFile", ServiceInterfaceTools.YINXIANGUPLOADNEWFILE,
                lessonId, currentAttachmentId, fileName, fieldId, soundtrackID, ud, new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        endRecordYinxiangTime = 0;
                        Toast.makeText(SyncRoomActivity.this, "upload file success", Toast.LENGTH_LONG).show();
                        // 音想音频文件上传完了,如果没有调用这个方法,调用一下
                        ServiceInterfaceTools.getinstance().notifyUploaded(AppConfig.URL_LIVEDOC + "notifyUploaded", ServiceInterfaceTools.NOTIFYUPLOADED, ud, MD5Hash, new ServiceInterfaceListener() {
                            @Override
                            public void getServiceReturnData(Object object) {
                                EventBus.getDefault().post(new EventSyncSucc());
                            }
                        });
                    }
                }
        );
    }


    private Uploadao uploadao = new Uploadao();

    private void startConverting(final Uploadao ud, final LineItem attachmentBean) {
        uploadao = ud;
        ServiceInterfaceTools.getinstance().startConverting(AppConfig.URL_LIVEDOC + "startConverting", ServiceInterfaceTools.STARTCONVERTING,
                uploadao, MD5Hash, fileName, targetFolderKey,
                new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                        Log.e("hhh", "startConvertingstartConverting");
                        convertingPercentage(attachmentBean);
                    }
                });
    }


    private Timer timer1;
    private TimerTask timerTask1;

    private void convertingPercentage(final LineItem attachmentBean) {
        timer1 = new Timer();
        timerTask1 = new TimerTask() {
            @Override
            public void run() {
                ServiceInterfaceTools.getinstance().queryConverting(AppConfig.URL_LIVEDOC + "queryConverting", ServiceInterfaceTools.QUERYCONVERTING,
                        uploadao, MD5Hash, new ServiceInterfaceListener() {
                            @Override
                            public void getServiceReturnData(Object object) {
                                Log.e("hhh", "queryConvertingqueryConverting");
                                uploadNewFile((ConvertingResult) object, attachmentBean);
                            }
                        });
            }
        };
        timer1.schedule(timerTask1, 1000, 1000);
    }


    private void uploadNewFile(final ConvertingResult convertingResult, final LineItem attachmentBean) {
        // {"Key":"d2e157d04da2cd330c259c64b806df10","Bucket":{"ServiceProviderId":2,"RegionName":"oss-cn-shanghai","BucketName":"peertime"}}
        // {"Success":true,"Data":{"CurrentStatus":0,"FinishPercent":0}}
        if (convertingResult.getCurrentStatus() == 0) {  // prepare
            attachmentBean.setProgress(0);
        } else if (convertingResult.getCurrentStatus() == 1) { //Converting
            attachmentBean.setProgress(convertingResult.getFinishPercent());
        } else if (convertingResult.getCurrentStatus() == 5) { //Done
            attachmentBean.setProgress(convertingResult.getFinishPercent());
            if (timer1 != null) {
                timer1.cancel();
                timer1 = null;
            }
            if (timerTask1 != null) {
                timerTask1.cancel();
                timerTask1 = null;
            }
            uploadList.remove(attachmentBean);
            ServiceInterfaceTools.getinstance().uploadNewFile(AppConfig.URL_PUBLIC + "EventAttachment/UploadNewFile", ServiceInterfaceTools.UPLOADNEWFILE,
                    fileName, uploadao, lessonId, MD5Hash, convertingResult, isAddToFavorite, field, new ServiceInterfaceListener() {
                        @Override
                        public void getServiceReturnData(Object object) {
                            Log.e("hhh", "uploadNewFileuploadNewFileuploadNewFile");
                            getSyncRoomDetail();
                            Toast.makeText(SyncRoomActivity.this, "upload success", Toast.LENGTH_LONG).show();
                        }
                    }
            );
        } else if (convertingResult.getCurrentStatus() == 3) { // Failed
            if (timer1 != null) {
                timer1.cancel();
                timer1 = null;
            }
            if (timerTask1 != null) {
                timerTask1.cancel();
                timerTask1 = null;
            }
        }
    }


    //  ----------------------------------------------------------  video  ----------------------------------------------------


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }


    boolean isRoute = true;
    int cRole = 0;
    float mPosX = 0, mCurPosX = 0;


    int clickNumber = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initUIandEvent() {

    }


    private boolean isHavePresenter() {
        if (meetingType != 0) {
            return true;
        }
        if (identity == 1) { // 学生
            if (TextUtils.isEmpty(studentCustomer.getUserID())) {
                return false;
            }
            if (studentCustomer.getUserID().equals(AppConfig.UserID.replace("-", ""))) {
                return true;
            }
        } else if (identity == 2) {
            if (currentPresenterId.equals(teacherCustomer.getUserID())) {
                return true;
            }
        }
        return false;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventTelephone(TelePhoneCall telePhoneCall) {
        boolean isTelephoneComing = telePhoneCall.isCall();
        if (isSync) { //录制音想模式
            if (isTelephoneComing) { //电话进来了  停止录音
                if (!isPause) {
                    pauseMedia();
                    pauseMedia2();
                }
            } else {  //挂断电话  开始录音
                if (isPause) {
                    resumeMedia();
                    resumeMedia2();
                }
            }
            pauseOrStartAudioRecord();
        }
    }

    /**
     * onDestory
     */
    @Override
    protected void deInitUIandEvent() {
        AppConfig.isPresenter = false;
        AppConfig.status = "0";
        AppConfig.currentLine = 0;
        AppConfig.currentMode = "0";
        syncroomInstance = false;
//        MainActivity.IsInSyncRoom  = false;
        wl.release();

        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
        if (getGroupbroadcastReceiver != null) {
            unregisterReceiver(getGroupbroadcastReceiver);
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        Tools.removeGroupMeaage(mGroupId);
        sendAudioSocket(0, soundtrackID);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (audiotimer != null) {
            audiotimer.cancel();
            audiotimer = null;
        }
        if (audioplaytimer != null) {
            audioplaytimer.cancel();
            audioplaytimer = null;
        }
        if (timer1 != null) {
            timer1.cancel();
            timer1 = null;
        }
        StopMedia2();
        if (audioRecorder != null) {
            audioRecorder.canel();  //取消录音
        }
        if (wv_show != null) {
            wv_show.clearCache(true);
            wv_show.removeAllViews();
            wv_show.destroy();
            wv_show = null;
        }
        if(DeviceManager.getDeviceType(this)==SupportDevice.BOOK){
            closeTouchHelper();
        }
        if (isRegistered) {
            unregisterReceiver(netWorkChangReceiver);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        StopMedia2();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ConnectionClassManager.getInstance().remove(connectionChangedListener);
        if (wv_show != null) {
            wv_show.pauseTimers();
//            wv_show.onHide();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (wv_show != null) {
//            wv_show.onNewIntent(intent);
        }
    }

    private void notifyDocumentPopupOpenOrClose(int type) {
        JSONObject actionJson = new JSONObject();
        try {
            actionJson.put("actionType", 1803);
            // 1表示打开 0表示关闭
            actionJson.put("stat", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        send_message("SEND_MESSAGE", AppConfig.UserToken, 0, null, Tools.getBase64(actionJson.toString()).replaceAll("[\\s*\t\n\r]", ""));
    }

    private void notifySelectedFileIndex(int position) {
        JSONObject actionJson = new JSONObject();
        try {
            actionJson.put("actionType", 1804);
            // 1表示打开 0表示关闭
            actionJson.put("stat", 1);
            actionJson.put("position", position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        send_message("SEND_MESSAGE", AppConfig.UserToken, 0, null, Tools.getBase64(actionJson.toString()).replaceAll("[\\s*\t\n\r]", ""));
    }


    //---- for note


    private void openNote(String noteId) {
        BookNote bookNote = null;
        if (TextUtils.isEmpty(noteId)) {
            bookNote = new BookNote().setTitle("new note").setJumpBackToNote(false);
        } else {
            bookNote = new BookNote().setDocumentId(noteId).setJumpBackToNote(false);
        }

        Intent intent = new Intent();
        intent.putExtra("OPEN_NOTE_BEAN", new Gson().toJson(bookNote));
        ComponentName comp = new ComponentName("com.onyx.android.note", "com.onyx.android.note.note.ui.ScribbleActivity");
        intent.setComponent(comp);
        startActivityForResult(intent, 100);
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
        wv_show.loadUrl("javascript:AfterEditBookNote(" + jsonObject + ")", null);
        noteManager = LocalNoteManager.getMgr(SyncRoomActivity.this);
        String exportPath = Environment.getExternalStorageDirectory().getPath() + File.separator + "Kloudsyn" + File.separator + "Kloud_" + note.documentId + ".pdf";

        noteManager.exportPdfAndUpload(SyncRoomActivity.this, note, exportPath, currentAttachmentId, currentAttachmentPage, spaceId, lessonId, currentLinkProperty.toString());
    }


    /**
     * 文档每一页上  展示 新的PDF文件（笔记）
     *
     * @param lineItem
     */

    private String currentAttachmentPage2;
    private LineItem currentShowPdf2 = new LineItem();
    private TextView closenote;
    private String localFileID = "";

    private void displayNote(Note note) {
        closenote.setVisibility(View.VISIBLE);
        closenote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("displayNotelog", isHavePresenter() + "");
                if (isHavePresenter()) if (!TextUtils.isEmpty(currentShowPdf2.getItemId())) {
                    closenote.setVisibility(View.GONE);
                    for (int i = 0; i < documentList.size(); i++) {
                        LineItem lineItem = documentList.get(i);
                        if (currentShowPdf2.getItemId().equals(lineItem.getItemId())) {
                            lineItem.setSelect(true);
                        } else {
                            lineItem.setSelect(false);
                        }
                    }
                    currentAttachmentPage = currentAttachmentPage2;
                    AppConfig.currentPageNumber = currentAttachmentPage2;
                    currentShowPdf.setNewPath(currentShowPdf2.getNewPath());
                    currentShowPdf.setUrl(currentShowPdf2.getUrl());
                    currentShowPdf.setItemId(currentShowPdf2.getItemId());
                    currentShowPdf.setAttachmentID(currentShowPdf2.getAttachmentID());
                    localFileID = "";
                    currentAttachmentId = currentShowPdf.getAttachmentID();
                    currentItemId = currentShowPdf.getItemId();
                    targetUrl = currentShowPdf.getUrl();
                    newPath = currentShowPdf.getNewPath();
                    notifySwitchDocumentSocket(currentShowPdf, currentAttachmentPage, 0);
                    loadWebIndex();
                }
            }
        });
        //保存
        currentAttachmentPage2 = currentAttachmentPage;
        currentShowPdf2.setNewPath(newPath);
        currentShowPdf2.setUrl(targetUrl);
        currentShowPdf2.setItemId(currentItemId);
        currentShowPdf2.setAttachmentID(currentAttachmentId);
        Log.e("displayNotelog", currentItemId + "  " + currentAttachmentPage2 + " " + currentAttachmentPage);
        for (int i = 0; i < documentList.size(); i++) {
            documentList.get(i).setSelect(false);
        }
        //重新赋值
        currentAttachmentPage = "0";
        AppConfig.currentPageNumber = "0";
        currentShowPdf = new LineItem();
        currentShowPdf.setUrl(note.getAttachmentUrl());
        currentShowPdf.setItemId(note.getNoteID() + ""); //同步笔记noteid
        currentShowPdf.setAttachmentID(note.getAttachmentID() + "");
        currentShowPdf.setDocumentItemID(note.getDocumentItemID());
        localFileID = note.getLocalFileID();
        currentAttachmentId = currentShowPdf.getAttachmentID();
        currentItemId = "0";
        targetUrl = currentShowPdf.getUrl();
        newPath = currentShowPdf.getNewPath();
        Log.e("dddddd", currentAttachmentId + "  " + currentItemId + "  " + targetUrl + "  " + newPath);
        notifySwitchDocumentSocket(currentShowPdf, "1", 1);
        loadWebIndex();
    }


    private void displayNoteTv(Note note) {
        if (closenote.getVisibility() == View.GONE) {
            closenote.setVisibility(View.VISIBLE);
            closenote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isHavePresenter()) {
                        if (!TextUtils.isEmpty(prevItemId)) {   //进入之前文档
                            closenote.setVisibility(View.GONE);
                            LineItem prevItem = new LineItem();
                            for (int i = 0; i < documentList.size(); i++) {
                                LineItem lineItem = documentList.get(i);
                                if (prevItemId.equals(lineItem.getItemId())) {
                                    prevItem = lineItem;
                                    lineItem.setSelect(true);
                                    break;
                                } else {
                                    lineItem.setSelect(false);
                                }
                            }
                            if (!TextUtils.isEmpty(prevItem.getItemId())) {
                                currentAttachmentPage = prevAttachmentPage;
                                AppConfig.currentPageNumber = prevAttachmentPage;
                                currentShowPdf.setPageNumber(currentAttachmentPage);
                                currentShowPdf.setNewPath(prevItem.getNewPath());
                                currentShowPdf.setUrl(prevItem.getUrl());
                                currentShowPdf.setItemId(prevItem.getItemId());
                                currentShowPdf.setAttachmentID(prevItem.getAttachmentID());
                                localFileID = "";
                                currentAttachmentId = currentShowPdf.getAttachmentID();
                                currentItemId = currentShowPdf.getItemId();
                                targetUrl = currentShowPdf.getUrl();
                                newPath = currentShowPdf.getNewPath();
                                notifySwitchDocumentSocket(currentShowPdf, currentAttachmentPage, 0);
                                loadWebIndex();
                                prevItemId = null;
                            } else {  //  没找到 回到欢迎页面 显示空白文档

                                findViewById(R.id.defaultpagehaha).setVisibility(View.VISIBLE);
                                TextView tv = findViewById(R.id.ashoast);
                                if (ishavedefaultpage && identity == 2) {
                                    findViewById(R.id.ppromat).setVisibility(View.GONE);
                                    tv.setText(getString(R.string.shYcan));
                                    createblabkpage.setOnClickListener(this);
                                    inviteattendee.setOnClickListener(this);
                                    sharedocument.setOnClickListener(this);
                                } else if (ishavedefaultpage) {
                                    findViewById(R.id.ppromat).setVisibility(View.VISIBLE);
                                    tv.setText(getString(R.string.shAcan));
                                    createblabkpage.setVisibility(View.GONE);
                                } else {
                                    findViewById(R.id.defaultpagehaha).setVisibility(View.GONE);
                                }

                            }
                        } else if (!TextUtils.isEmpty(currentShowPdf2.getItemId())) {
                            Log.e("displayNotelog", "3");
                            closenote.setVisibility(View.GONE);
                            for (int i = 0; i < documentList.size(); i++) {
                                LineItem lineItem = documentList.get(i);
                                if (currentShowPdf2.getItemId().equals(lineItem.getItemId())) {
                                    lineItem.setSelect(true);
                                } else {
                                    lineItem.setSelect(false);
                                }
                            }
                            currentAttachmentPage = currentAttachmentPage2;
                            AppConfig.currentPageNumber = currentAttachmentPage2;
                            currentShowPdf.setPageNumber(currentAttachmentPage);
                            currentShowPdf.setNewPath(currentShowPdf2.getNewPath());
                            currentShowPdf.setUrl(currentShowPdf2.getUrl());
                            currentShowPdf.setItemId(currentShowPdf2.getItemId());
                            currentShowPdf.setAttachmentID(currentShowPdf2.getAttachmentID());
                            currentAttachmentId = currentShowPdf.getAttachmentID();
                            currentItemId = currentShowPdf.getItemId();
                            targetUrl = currentShowPdf.getUrl();
                            newPath = currentShowPdf.getNewPath();
                            notifySwitchDocumentSocket(currentShowPdf, currentAttachmentPage, 0);
                            loadWebIndex();
                        }
                    }
                }
            });
            //保存
            currentAttachmentPage2 = currentShowPdf.getPageNumber();
            currentShowPdf2.setNewPath(newPath);
            currentShowPdf2.setUrl(targetUrl);
            currentShowPdf2.setItemId(currentItemId);
            Log.e("displayNotelog", currentItemId + "  " + currentAttachmentPage2 + " " + currentAttachmentPage);
            currentShowPdf2.setAttachmentID(currentAttachmentId);
            for (int i = 0; i < documentList.size(); i++) {
                documentList.get(i).setSelect(false);
            }
            //重新赋值
            currentAttachmentPage = "0";
            AppConfig.currentPageNumber = "0";
            currentShowPdf = new LineItem();
            currentShowPdf.setUrl(note.getAttachmentUrl());
            currentShowPdf.setItemId(note.getNoteID() + ""); // 同步笔记noteid
            currentShowPdf.setAttachmentID(note.getAttachmentID() + "");
            currentShowPdf.setDocumentItemID(note.getDocumentItemID());
            localFileID = note.getLocalFileID();
            currentAttachmentId = currentShowPdf.getAttachmentID();
            currentItemId = "0";
            targetUrl = currentShowPdf.getUrl();
            newPath = currentShowPdf.getNewPath();
            Log.e("dddddd", currentAttachmentId + "  " + currentItemId + "  " + targetUrl + "  " + newPath);
            loadWebIndex();
        } else {
            displayNoteChangePage(note, false);
        }
    }

    private void displayNoteChangePage(Note note, boolean isSend) {
        closenote.setVisibility(View.VISIBLE);
        currentAttachmentPage = "0";
        AppConfig.currentPageNumber = "0";
        currentShowPdf = new LineItem();
        currentShowPdf.setUrl(note.getAttachmentUrl());
        currentShowPdf.setItemId(note.getNoteID() + ""); //同步笔记noteid
        currentShowPdf.setAttachmentID(note.getAttachmentID() + "");
        currentShowPdf.setDocumentItemID(note.getDocumentItemID());
        localFileID = note.getLocalFileID();
        currentAttachmentId = currentShowPdf.getAttachmentID();
        currentItemId = "0";
        targetUrl = currentShowPdf.getUrl();
        newPath = currentShowPdf.getNewPath();
        Log.e("dddddd", currentAttachmentId + "  " + currentItemId + "  " + targetUrl + "  " + newPath);
        loadWebIndex();
        if (isSend) {
            notifySwitchDocumentSocket(currentShowPdf, "1", 1);
        }
    }


    private int spaceId;

    NoteDetail currentNote;
    JSONObject currentLinkProperty = new JSONObject();
    private String toolName;

    @JavascriptInterface
    public void callAppFunction(String action, final String data) {
//        Log.e("noteeeview", "callAppFunction,action:  " + action + ",data:" + data);
        JSONObject datas = null;
        try {
            datas = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        switch (action) {
            case "BookNoteSelect":
//            {"LinkID":0,"LinkProperty":{"X":0.29383634431455896,"Y":0.35509554140127386}}
                try {
                    final int linkId = datas.getInt("LinkID");
                    final JSONObject linkProperty = datas.getJSONObject("LinkProperty");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            selectNoteBook(linkId, linkProperty);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "BookNoteView":

                if (datas.has("LinkID")) {
                    try {
                        Log.e("noterru", datas.getInt("LinkID") + "");
                        displayNoteByLinkId(datas.getInt("LinkID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case "BookNoteMove":
                break;
            case "BookNoteDelete":
                String ss = "";
                try {
                    final JSONArray linkIds = datas.getJSONArray("LinkIDs");
                    for (int i = 0; i < linkIds.length(); i++) {
                        int link = linkIds.getInt(i);
                        if (i == linkIds.length() - 1) {
                            ss = ss + link;
                        } else {
                            ss = ss + link + ",";
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("BookNoteDelete", ss);
                String url = AppConfig.URL_PUBLIC + "DocumentNote/RemoveNote?linkIDs=" + ss;
                ServiceInterfaceTools.getinstance().removeNote(url, ServiceInterfaceTools.REMOVENOTE, new ServiceInterfaceListener() {
                    @Override
                    public void getServiceReturnData(Object object) {
                    }
                });
                break;
            case "BookNoteEdit":
                if (datas != null) {
                    if (datas.has("LinkID")) {
                        try {
                            final int linkId = datas.getInt("LinkID");
                            currentLinkProperty = datas.getJSONObject("LinkProperty");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(linkId==0){
                                        drawNote(-1, currentLinkProperty, 0);
                                    }
                                }
                            });
                            if (linkId == 0) {
                                openNote(null);
                            } else {
                                ServiceInterfaceTools.getinstance().getNoteDetailByLinkId(AppConfig.URL_PUBLIC + "DocumentNote/NoteByLinkID", linkId + "", new ServiceInterfaceTools.OnJsonResponseReceiver() {
                                    @Override
                                    public void jsonResponse(JSONObject jsonResponse) {
                                        if (jsonResponse != null) {
                                            try {
                                                String data = jsonResponse.getString("RetData");
                                                if (!TextUtils.isEmpty(data)) {
                                                    currentNote = new Gson().fromJson(data, NoteDetail.class);
                                                    if (currentLinkProperty != null && currentNote != null) {
                                                        currentNote.setLinkProperty(currentLinkProperty.toString());
                                                    }
                                                    openNote(currentNote.getLocalFileID());
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case "OnToolChange":
                if (DeviceManager.getDeviceType(this) == SupportDevice.BOOK) {
                    try {
                        toolName = datas.getString("ToolName");
                        if (toolName.equals("ToolPen") || toolName.equals("ToolLine")) {
                            touchHelper.setRawDrawingEnabled(true);
                            touchHelper.setStrokeStyle(TouchHelper.STROKE_STYLE_PENCIL);
                        } else if (toolName.equals("ToolBrush")) {
                            touchHelper.setRawDrawingEnabled(true);
                            touchHelper.setStrokeStyle(TouchHelper.STROKE_STYLE_BRUSH);
                        } else {
                            touchHelper.setRawDrawingEnabled(false);
                        }
                        Log.e("OnToolChange", toolName + " ");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case "OnToolBarVisibleChange":
                int isShow = 0;
                try {
                    isShow = datas.getInt("IsShow");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("OnToolBarVisibleChange", isShow + "");
                refreshTouchHelper();
                break;


        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void uploadNodeSuccess(NoteId noteId) {
        Log.e("addLocalNote", "draw note by id:" + noteId);
        if (noteId.getLinkID() == 0) {
            return;
        }
        deleteNote(-1);
        drawNote(noteId.getLinkID(), currentLinkProperty, 0);
    }

    private void notifyDrawNotes(List<NoteDetail> notes, int isother) {
        for (NoteDetail note : notes) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(note.getLinkProperty());
                drawNote(note.getLinkID(), jsonObject, isother);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void deleteNote(int linkId) {
        String url = AppConfig.URL_PUBLIC + "DocumentNote/RemoveNote?linkIDs=" + linkId;
        ServiceInterfaceTools.getinstance().removeNote(url, ServiceInterfaceTools.REMOVENOTE, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {

            }
        });
        JSONObject noteData = new JSONObject();
        try {
            noteData.put("type", 102);
            noteData.put("id", "BooXNote_" + linkId);
            Log.e("noteeeedelete", "note:" + noteData.toString());
            if (wv_show != null) {
                wv_show.loadUrl("javascript:PlayActionByTxt('" + noteData + "')", null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void drawNote(int linkId, JSONObject linkProperty, int isOther) {
        JSONObject noteData = new JSONObject();
        try {
            noteData.put("type", 38);
            noteData.put("LinkID", linkId);
            noteData.put("IsOther", isOther);
            noteData.put("LinkProperty", linkProperty);
            Log.e("noteeeeadd", "note:" + noteData.toString());
            if (wv_show != null) {
                wv_show.loadUrl("javascript:PlayActionByTxt('" + noteData + "')", null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void clearBookNote(boolean clearme, boolean clearother) {
        try {
            JSONObject clearnote = new JSONObject();
            clearnote.put("ClearMe", clearme);
            clearnote.put("ClearOther", clearother);
            String key = "ClearBookNote";
            Log.e("ClearBookNote", clearnote.toString() + "");
            wv_show.loadUrl("javascript:FromApp('" + key + "'," + clearnote + ")", null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showDotPanData(String pageIdentifier, final JSONObject jsonObject) {
        if (!TextUtils.isEmpty(localFileID) && pageIdentifier.equals(localFileID)) {
            try {
                final String key = "ShowDotPanData";
                Log.e("ShowDotPanData", jsonObject.toString() + "");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        wv_show.loadUrl("javascript:FromApp('" + key + "'," + jsonObject + ")", null);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void twinkleBookNote(int linkId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("LinkID", linkId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String key = "TwinkleBookNote";
        Log.e("TwinkleBookNote", linkId + "");
        wv_show.loadUrl("javascript:FromApp('" + key + "'," + jsonObject + ")", null);
    }


    private void notifH5draw(final String key, int x, int y) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("x", x / 2);
            jsonObject.put("y", y / 2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("TwinkleBookNote", key + " " + x / 2 + "  " + y / 2);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                wv_show.loadUrl("javascript:FromApp('" + key + "'," + jsonObject + ")", null);
            }
        });

    }

    private void displayNoteByLinkId(int linkId) {

        String url = AppConfig.URL_PUBLIC + "DocumentNote/NoteByLinkID?linkID=" + linkId;
        ServiceInterfaceTools.getinstance().getNoteByLinkID(url, ServiceInterfaceTools.GETNOTEBYLINKID, new ServiceInterfaceListener() {
            @Override
            public void getServiceReturnData(Object object) {
                Note note = (Note) object;
                if (note.getLinkID() == 0) {
                    Toast.makeText(SyncRoomActivity.this, "笔记正在上传，请稍后打开", Toast.LENGTH_LONG).show();
                } else {
                    displayNote(note);
                }

            }
        });
    }

    private void followLeaveMeeting() {
        if (mWebSocketClient != null) {
            try {
                JSONObject loginjson = new JSONObject();
                loginjson.put("action", "LEAVE_MEETING");
                loginjson.put("sessionId", AppConfig.UserToken);
                loginjson.put("meetingId", meetingId);
                loginjson.put("followToLeave", 1);
                String ss = loginjson.toString();
                SpliteSocket.sendMesageBySocket(ss);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        finish();
    }


}



