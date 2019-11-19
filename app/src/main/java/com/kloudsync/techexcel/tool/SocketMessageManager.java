package com.kloudsync.techexcel.tool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;

import com.kloudsync.techexcel.bean.EventSocketMessage;
import com.kloudsync.techexcel.bean.MeetingConfig;
import com.kloudsync.techexcel.config.AppConfig;
import com.ub.techexcel.tools.SpliteSocket;
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

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            if(TextUtils.isEmpty(message)){
                return;
            }
            EventBus.getDefault().post(parseMessage(message));
        }
    };

    private EventSocketMessage parseMessage(String message){
        EventSocketMessage socketMessage = new EventSocketMessage();
        if(message.equals(MESSAGE_LEAVE_MEETING)){
            socketMessage.setAction(MESSAGE_LEAVE_MEETING);
            return socketMessage;
        }
        String _message = Tools.getFromBase64(message);
        try {
            JSONObject jsonMessage = new JSONObject(_message);
            if(jsonMessage.has("action")){
                socketMessage.setAction(jsonMessage.getString("action"));
            }
            if(jsonMessage.has("data")){
                socketMessage.setData(jsonMessage.getString("data"));
            }
            if(jsonMessage.has("retData")){
                socketMessage.setRetData(jsonMessage.getString("retData"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return socketMessage;
    }

    private SocketMessageManager(Context context){
        this.context = context;
        registerMessageReceiver();
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

    public void sendMessage_JoinMeeting(MeetingConfig config){

        try {
            JSONObject message = new JSONObject();
            message.put("action", "JOIN_MEETING");
            message.put("sessionId", config.getUserToken());
            message.put("meetingId", config.getMeetingId());
            message.put("meetingPassword", "");
            message.put("clientVersion", "v20140605.0");
            message.put("role", config.getRole().getRole());
            message.put("mode", 0);
            message.put("type", config.getType());
            message.put("lessonId", config.getLessionId());
            if(!TextUtils.isEmpty(config.getDocumentId())){
                message.put("itemId",config.getDocumentId());
            }
            doSendMessage(message.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private WebSocketClient getClient(){
        if(socketClient == null){
            socketClient = AppConfig.webSocketClient;
        }
        return socketClient;
    }

    private void doSendMessage(String message){
        if(getClient() != null){
            try {
                getClient().send(message);
                Log.e("SocketMessageManager","send:" + message);
            }catch (Exception exception){

            }
        }
    }

    private void registerMessageReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.cn.socket");
        context.registerReceiver(messageReceiver, intentFilter);
    }

    public void release(){
        context.unregisterReceiver(messageReceiver);
    }

}
