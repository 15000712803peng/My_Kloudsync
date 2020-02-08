package com.kloudsync.techexcel.tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kloudsync.techexcel.bean.ChatMessage;
import com.kloudsync.techexcel.bean.DocumentPage;

import org.feezu.liuli.timeselector.Utils.TextUtil;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class ChatMessagesCache {
    //    private final SharedPreferences cachePreference;
    private static ChatMessagesCache instance;
    Gson gson;

    private ChatMessagesCache(Context context) {
//        cachePreference = context.getSharedPreferences("kloud_chat_messages_cache", Context.MODE_PRIVATE);
//        gson = new Gson();
        cacheMaps = new LinkedHashMap<>();

    }

    public static synchronized ChatMessagesCache getInstance(Context context) {
        if (instance == null) {
            instance = new ChatMessagesCache(context);
        }
        return instance;
    }

    public void cacheChatMessage(ChatMessage message, String meetingId) {

        if (TextUtils.isEmpty(meetingId)) {
            return;
        }
        Map<String, LinkedList<ChatMessage>> map = getPageMap();
        if (map == null) {
            cacheMaps = new LinkedHashMap<>();
        }
        LinkedList<ChatMessage> chatMessages = map.get(meetingId);
        if (chatMessages == null) {
            chatMessages = new LinkedList<>();
        }
        chatMessages.addFirst(message);
        if (chatMessages.size() > 10) {
            chatMessages.removeLast();

        }
        map.put(meetingId, chatMessages);
//        cachePreference.edit().putString("chat_messaes_map", new Gson().toJson(map)).commit();
    }

    public LinkedList<ChatMessage> getChatMessage(String meetingId) {

        if (TextUtils.isEmpty(meetingId)) {
            return null;
        }
        Map<String, LinkedList<ChatMessage>> map = getPageMap();
        if (map == null) {
            return null;
        }
        LinkedList<ChatMessage> chatMessages = map.get(meetingId);
        if (chatMessages == null) {
            chatMessages = new LinkedList<>();
        }
        return chatMessages;
    }


    Map<String, LinkedList<ChatMessage>> cacheMaps;

    private Map<String, LinkedList<ChatMessage>> getPageMap() {
        return cacheMaps;
//        String json = cachePreference.getString("chat_messaes_map", "");
//        if (TextUtil.isEmpty(json)) {
//            return new HashMap<>();
//        }
//        return gson.fromJson(json, new TypeToken<Map<String,LinkedList<ChatMessage>>>() {
//        }.getType());
    }

    public LinkedList<ChatMessage> getMeetingChatMessages(String meetingId) {
        Map<String, LinkedList<ChatMessage>> map = getPageMap();
        Log.e("chat_message", "getPageCache, map:" + map);
        if (map != null) {
            return map.get(meetingId);
        }
        return null;
    }


    public void clear() {
        cacheMaps.clear();
//        cachePreference.edit().putString("chat_messaes_map", "").commit();
    }


}
