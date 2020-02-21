package com.kloudsync.techexcel.tool;

import android.util.Log;

import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;

public class MessageTool {

    public static void sendMessage(MessageContent msg, String mTargetId, Conversation.ConversationType type) {
        Message myMessage = Message.obtain(mTargetId, type, msg);
        RongIM.getInstance()
                .sendMessage(myMessage, null, null, new IRongCallback.ISendMessageCallback() {
                    @Override
                    public void onAttached(Message message) {

                    }

                    @Override
                    public void onSuccess(Message message) {
                        Log.e("lalala", "sendMessage onSuccess");

                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                        Log.e("lalala", "sendMessage onError");

                    }
                });
    }

    public static void sendMessage(MessageContent msg, String mTargetId, Conversation.ConversationType type, IRongCallback.ISendMessageCallback callback) {
        Message myMessage = Message.obtain(mTargetId, type, msg);
        RongIM.getInstance()
                .sendMessage(myMessage, null, null, callback);
    }
}
