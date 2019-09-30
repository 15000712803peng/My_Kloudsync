package com.kloudsync.techexcel.help;

import android.text.format.DateFormat;

import com.kloudsync.techexcel.config.AppConfig;
import com.kloudsync.techexcel.dialog.message.ShareMessage;
import com.kloudsync.techexcel.info.MyFriend;
import com.kloudsync.techexcel.tool.MessageTool;
import com.ub.kloudsync.activity.Document;
import com.ub.techexcel.bean.LineItem;

import io.rong.imlib.IRongCallback;
import io.rong.imlib.model.Conversation;

public class ShareTool {
    public static void shareDocumentToFriend(String userUrl, LineItem document, MyFriend friend, IRongCallback.ISendMessageCallback callback) {
        ShareMessage sm = new ShareMessage();
        sm.setShareDocTitle(document.getFileName());
        sm.setAttachmentID(document.getAttachmentID() + "");
        String url = AppConfig.SHARE_ATTACHMENT + document.getAttachmentID();
        String thumurl = document.getSourceFileUrl();
        sm.setShareDocThumbnailUrl(thumurl);
        sm.setShareDocUrl(url);
        sm.setShareDocAvatarUrl("");
        String date = (String) DateFormat.format("yy.MM.dd", System.currentTimeMillis());
        sm.setShareDocTime("Sharee at " + date);
        sm.setShareDocUsername(AppConfig.UserName);
        MessageTool.sendMessage(sm, friend.getRongCloudUserID(), Conversation.ConversationType.PRIVATE, callback);
    }
}
