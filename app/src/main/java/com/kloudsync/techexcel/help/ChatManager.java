package com.kloudsync.techexcel.help;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.kloudsync.techexcel.bean.ChatMessage;
import com.kloudsync.techexcel.tool.ChatMessagesCache;
import com.ub.techexcel.tools.ServiceInterfaceTools;
import com.ub.techexcel.tools.Tools;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;

/**
 * Created by tonyan on 2020/1/3.
 */

public class ChatManager extends RongIMClient.OperationCallback {

    private static ChatManager manager;
    private String roomId;
//    private List<ChatMessage> messages;
    ChatMessagesCache messagesCache;
    private Map<String,UserInfo> caches;
    private static String meetingId;

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    private ChatManager(Context context){
        caches = new HashMap<>();
        messagesCache = ChatMessagesCache.getInstance(context);
    }

    PopBottomChat popBottomChat;

    public void setPopBottomChat(PopBottomChat popBottomChat,String roomId) {
        this.popBottomChat = popBottomChat;
        this.roomId = roomId;
        joinChatRoom(roomId);
    }

    @Override
    public void onSuccess() {
        getHistoryMessage();
        RongIMClient.setOnReceiveMessageListener(messageReceiver);
        if(popBottomChat != null){
            LinkedList<ChatMessage> messages = messagesCache.getChatMessage(meetingId);
            Log.e("ChatManager","meeting_id:" + meetingId + ",cache_messages:" + messages);
            popBottomChat.initMessages(messages);
        }
    }

    @Override
    public void onError(RongIMClient.ErrorCode errorCode) {
        Log.e("ChatManager","Join_Error:" + errorCode.getMessage());
    }

    public static ChatManager getManager(Context context,String _meetingId) {
        meetingId = _meetingId;
        if (manager == null) {
            synchronized (ChatManager.class) {
                if (manager == null) {
                    manager = new ChatManager(context);
                }
            }
        }
        return manager;
    }

    public void joinChatRoom(String roomId){
        this.roomId = roomId;
        Tools.joinChatRoom(roomId,this);
    }

    RongIMClient.OnReceiveMessageWrapperListener messageReceiver = new RongIMClient.OnReceiveMessageWrapperListener() {
        @Override
        public boolean onReceived(Message message, int i, boolean b, boolean b1) {
            Log.e("ChatManager","Receive_wrapper_Message:" + message);
            wrapMessage(message);
            return false;
        }
    };

    RongIMClient.ResultCallback<List<Message>> historyMessageCallback = new RongIMClient.ResultCallback<List<Message>>() {
        @Override
        public void onSuccess(List<Message> messages) {
            Log.e("ChatManager","GetHistoryMessageSuccess:" + messages.size());
        }

        @Override
        public void onError(RongIMClient.ErrorCode errorCode) {
            Log.e("ChatManager","GetHistoryMessageError:" + errorCode.getMessage());
        }
    };

    private void getHistoryMessage(){
        Tools.getChatroomHistoryMessage(roomId,historyMessageCallback);
    }

    class UserInfo{
        public String name;
        public String avatorUrl;
    }

    public void wrapMessage(final Message message){

        if(!(message.getContent() instanceof TextMessage)){
            return;
        }
        Observable.just(message).observeOn(Schedulers.io()).map(new Function<Message, ChatMessage>() {
            @Override
            public ChatMessage apply(Message message) throws Exception {
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setTime(message.getReceivedTime());
                if(chatMessage.getTime() <= 0){
                    chatMessage.setTime(message.getSentTime());
                }
                if(caches.containsKey(message.getSenderUserId())){
                    chatMessage.setUserName(caches.get(message.getSenderUserId()).name);
                    chatMessage.setAvatorUrl(caches.get(message.getSenderUserId()).avatorUrl);
                }
                chatMessage.setMessage(message);
                return chatMessage;
            }
        }).doOnNext(new Consumer<ChatMessage>() {
            @Override
            public void accept(ChatMessage chatMessage) throws Exception {
                if(TextUtils.isEmpty(chatMessage.getUserName())){
                    JSONObject result = ServiceInterfaceTools.getinstance().syncGetUserListBasicInfoByRongCloud(chatMessage.getMessage().getSenderUserId());
                    if(result.has("RetCode")){
                        if(result.getInt("RetCode") == 0){
                            JSONObject _data = result.getJSONArray("RetData").getJSONObject(0);
                            if(_data != null){
                                chatMessage.setUserName(_data.getString("UserName"));
                                chatMessage.setAvatorUrl(_data.getString("AvatarUrl"));
                            }
                            UserInfo userInfo = new UserInfo();
                            userInfo.name = chatMessage.getUserName();
                            userInfo.avatorUrl = chatMessage.getAvatorUrl();
                            caches.put(chatMessage.getMessage().getSenderUserId(),userInfo);
                        }
                    }
                }
//                messages.add(chatMessage);
                messagesCache.cacheChatMessage(chatMessage,meetingId);
                Log.e("ChatManager","Cache_Message:" + messagesCache.getChatMessage(meetingId).size());
            }

        }).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<ChatMessage>() {
            @Override
            public void accept(ChatMessage message) throws Exception {
                if(popBottomChat != null){
                    popBottomChat.addMessage(message);
                }
            }
        }).subscribe();
    }

}
