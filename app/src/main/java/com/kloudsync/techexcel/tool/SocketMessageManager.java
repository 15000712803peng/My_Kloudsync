package com.kloudsync.techexcel.tool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;

import com.kloudsync.techexcel.bean.EventSocketMessage;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.bean.MeetingDocument;
import com.kloudsync.techexcel.bean.MeetingMember;
import com.kloudsync.techexcel.bean.MeetingType;
import com.kloudsync.techexcel.bean.NoteDetail;
import com.kloudsync.techexcel.config.AppConfig;
import com.ub.techexcel.bean.AgoraMember;
import com.ub.techexcel.bean.Note;
import com.ub.techexcel.tools.Tools;

import org.greenrobot.eventbus.EventBus;
import org.java_websocket.client.WebSocketClient;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tonyan on 2019/11/19.
 */

public class SocketMessageManager {

    private Context context;
    private volatile WebSocketClient socketClient;
    public static final String MESSAGE_LEAVE_MEETING = "LEAVE_MEETING";
    public static final String MESSAGE_JOIN_MEETING = "JOIN_MEETING";
    public static final String MESSAGE_BROADCAST_FRAME = "BROADCAST_FRAME";
    public static final String MESSAGE_SEND_MESSAGE = "SEND_MESSAGE";
    public static final String MESSAGE_END_MEETING = "END_MEETING";
    public static final String MESSAGE_MAKE_PRESENTER = "MAKE_PRESENTER";
    public static final String MESSAGE_ATTACHMENT_UPLOADED = "ATTACHMENT_UPLOADED";
    public static final String MESSAGE_AGORA_STATUS_CHANGE = "AGORA_STATUS_CHANGE";
    public static final String MESSAGE_MEMBER_LIST_CHANGE = "MEMBER_LIST_CHANGE";
    public static final String MESSAGE_NOTE_DATA = "NOTE_DATA";
    public static final String MESSAGE_NOTE_CHANGE = "NOTE_CHANGE";
    public static final String MESSAGE_NOTE_P1_CREATEAD = "NOTE_ITEM_4_P1_CREATED";
    public static final int MESSAGE_VIDEO_PAUSE = 0;
    public static final int MESSAGE_VIDEO_PLAY = 1;
    public static final int MESSAGE_VIDEO_CLOSE = 2;

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            if (TextUtils.isEmpty(message)) {
                return;
            }
            EventBus.getDefault().post(parseMessage(message));
        }
    };

    private EventSocketMessage parseMessage(String message) {
        EventSocketMessage socketMessage = new EventSocketMessage();
        if (message.equals(MESSAGE_LEAVE_MEETING)) {
            socketMessage.setAction(MESSAGE_LEAVE_MEETING);
            return socketMessage;
        }

        String _message = Tools.getFromBase64(message);
        try {
            JSONObject jsonMessage = new JSONObject(_message);
            if (jsonMessage.has("action")) {
                socketMessage.setAction(jsonMessage.getString("action"));
            }

            socketMessage.setData(jsonMessage);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return socketMessage;
    }

    private SocketMessageManager(Context context) {
        this.context = context;
    }

    static volatile SocketMessageManager instance;

    public static SocketMessageManager getManager(Context context) {
        if (instance == null) {
            synchronized (SocketMessageManager.class) {
                if (instance == null) {
                    instance = new SocketMessageManager(context);
                }
            }
        }
        return instance;
    }

    public void sendMessage_JoinMeeting(MeetingConfig config) {

        try {
            JSONObject message = new JSONObject();
            message.put("action", "JOIN_MEETING");
            message.put("sessionId", config.getUserToken());
            message.put("meetingId", config.getMeetingId());
            message.put("meetingPassword", "");
            message.put("clientVersion", "v20140605.0");
            message.put("role", config.getRole());
            message.put("mode", 0);
            message.put("type", config.getType());
            message.put("lessonId", config.getLessionId());
            message.put("isInstantMeeting", 1);
            if(config.getDocument() != null){
                message.put("itemId", config.getDocument().getItemID());
            }
            doSendMessage(message.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage_startMeeting(MeetingConfig config, String newMeetingId) {

        try {
            JSONObject message = new JSONObject();
            message.put("action", "JOIN_MEETING");
            message.put("sessionId", config.getUserToken());
            message.put("meetingId", newMeetingId);
            message.put("meetingPassword", "");
            message.put("clientVersion", "v20140605.0");
            config.setRole(MeetingConfig.MeetingRole.HOST);
            message.put("role", config.getRole());
            message.put("mode", 0);
            message.put("type", 0);
            message.put("lessonId", config.getLessionId());
            message.put("isInstantMeeting", 1);
            if(config.getDocument() != null){
                message.put("itemId", config.getDocument().getItemID());
            }
            doSendMessage(message.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 老师第一次join时，调一下这个方法，将当前文档ID发给鹏飞
     */
    public void sendMessage_UpdateAttchment(MeetingConfig meetingConfig) {
        JSONObject message = new JSONObject();
        try {
            message.put("sessionId", AppConfig.UserToken);
            if (meetingConfig.getType() == MeetingType.MEETING) {
                message.put("action", "UPDATE_CURRENT_DOCUMENT_ID");
                message.put("documentId", meetingConfig.getDocument().getItemID());
            } else {
                message.put("action", "UPDATE_CURRENT_ATTACHMENT_ID");
                message.put("documentId", meetingConfig.getDocument().getAttachmentID());
            }
            doSendMessage(message.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void sendMessage_DocumentShowed(MeetingConfig config) {

        try {
            JSONObject message = new JSONObject();
            MeetingDocument document = config.getDocument();
            message.put("actionType", 8);
//            message.put("eventID", config.getDocument().getEventID());
            message.put("attachmentUrl", document.getAttachmentUrl());
            message.put("meetingID", config.getMeetingId());
            message.put("itemId", document.getItemID());
            message.put("incidentID", config.getPageNumber());
            message.put("pageNumber", config.getPageNumber());
            message.put("docType", config.getType());
            message.put("isH5", true);
            //---------
            doSendMessage(wrapperSendMessage(AppConfig.UserToken, 0, Tools.getBase64(message.toString()).replaceAll("[\\s*\t\n\r]", "")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendMessage_UpdateAttchment(config);
    }

    public void sendMessage_ViewNote(MeetingConfig config,Note note) {

        try {
            JSONObject message = new JSONObject();
            MeetingDocument document = config.getDocument();
            message.put("actionType", 8);
            message.put("roleType", 1);
//            message.put("eventID", config.getDocument().getEventID());
            message.put("attachmentUrl", note.getAttachmentUrl());
            message.put("meetingID", config.getMeetingId());
            message.put("itemId", note.getNoteID());
            message.put("incidentID", config.getPageNumber());
            message.put("pageNumber", 1);
            message.put("docType", 1);
            message.put("isH5", true);
            //---------
            doSendMessage(wrapperSendMessage(AppConfig.UserToken, 0, Tools.getBase64(message.toString()).replaceAll("[\\s*\t\n\r]", "")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendMessage_UpdateAttchment(config);
    }

    public void sendMessage_LeaveMeeting(MeetingConfig config) {
        try {
            JSONObject message = new JSONObject();
            message.put("action", "LEAVE_MEETING");
            message.put("sessionId", config.getUserToken());
            message.put("meetingId", config.getMeetingId());
//                    message.put("followToLeave", 1);
            doSendMessage(message.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage_EndMeeting(MeetingConfig config) {
        try {
            JSONObject message = new JSONObject();
            message.put("action", "END_MEETING");
            message.put("sessionId", config.getUserToken());
            message.put("meetingId", config.getMeetingId());
//                    message.put("followToLeave", 1);
            doSendMessage(message.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage_MyActionFrame(String actions, MeetingConfig meetingConfig) {
        String _actions = Tools.getBase64(actions).replaceAll("[\\s*\t\n\r]", "");
        try {
            JSONObject message = new JSONObject();
            message.put("action", "ACT_FRAME");
            message.put("sessionId", AppConfig.UserToken);
            message.put("retCode", 1);
            message.put("data", _actions);
            message.put("itemId", meetingConfig.getDocument().getItemID());
            message.put("sequenceNumber", "3837");
            message.put("ideaType", "document");
            doSendMessage(message.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage_MyNoteActionFrame(String actions, MeetingConfig meetingConfig,Note note) {
        String _actions = Tools.getBase64(actions).replaceAll("[\\s*\t\n\r]", "");
        try {
            JSONObject message = new JSONObject();
            message.put("action", "ACT_FRAME");
            message.put("sessionId", AppConfig.UserToken);
            message.put("retCode", 1);
            message.put("data", _actions);
            message.put("itemId", note.getNoteID());
            message.put("sequenceNumber", "3837");
            message.put("ideaType", "document");
            doSendMessage(message.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage_MeetingStatus(MeetingConfig config) {
        try {
            JSONObject message = new JSONObject();
            message.put("action", "MEETING_STATUS");
            message.put("sessionId", config.getUserToken());
            message.put("meetingId", config.getMeetingId());
            message.put("lessonId", config.getLessionId());
            message.put("status", 1);
//                    message.put("followToLeave", 1);
            doSendMessage(message.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage_MakePresenter(MeetingConfig config, MeetingMember member){

        try {
            JSONObject message = new JSONObject();
            message.put("action", "MAKE_PRESENTER");
            message.put("sessionId", AppConfig.UserToken);
            message.put("meetingId", config.getMeetingId());
            message.put("lessonId", config.getLessionId());
            message.put("newPresenterSessionId", member.getSessionId());
            doSendMessage(message.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void sendMessage_VedioPlayedStatus(int state, float time, String vid, String url){
        JSONObject message = new JSONObject();
        try {
            message.put("stat", state);
            message.put("actionType", 19);
            message.put("time", time);
            message.put("vid", vid);
            message.put("url", url);
            message.put("type", 1 );
        } catch (Exception e) {
            e.printStackTrace();
        }

        doSendMessage(wrapperSendMessage(AppConfig.UserToken,0,Tools.getBase64(message.toString()).replaceAll("[\\s*\t\n\r]", "")));
    }


    public void sendMessage_AgoraStatusChange(MeetingConfig config, AgoraMember member){
        try {
            JSONObject message = new JSONObject();
            message.put("action", "AGORA_STATUS_CHANGE");
            message.put("sessionId", config.getUserToken());
            message.put("agoraStatus", 1);
            message.put("microphoneStatus", member.isMuteAudio()?3:2);
            message.put("cameraStatus", member.isMuteVideo() ? 3: 2);
            message.put("screenStatus", 0);
            doSendMessage(message.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void sendMessage_InviteToMeeting(MeetingConfig config,String users){
        try {
            JSONObject message = new JSONObject();
            message.put("action", "INVITE_TO_MEETING");
            message.put("sessionId", config.getUserToken());
            message.put("meetingId", config.getMeetingId());
            message.put("userIds", users);
            doSendMessage(message.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private WebSocketClient getClient() {
        if (socketClient == null) {
            socketClient = AppConfig.webSocketClient;
        }
        return socketClient;
    }

    private void doSendMessage(String message) {
        if (getClient() != null) {
            try {
                getClient().send(message);
                Log.e("SocketMessageManager", "send:" + message);
            } catch (Exception exception) {

            }
        }
    }

    private String wrapperSendMessage(String sessionId, int type, String data) {
        JSONObject message = new JSONObject();
        try {
            message.put("action", "SEND_MESSAGE");
            message.put("sessionId", sessionId);
            message.put("type", type);
            message.put("userList", "");
            message.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return message.toString();

    }

    public void registerMessageReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.cn.socket");
        context.registerReceiver(messageReceiver, intentFilter);
    }

    public void release() {
        context.unregisterReceiver(messageReceiver);
        instance = null;
    }

}
