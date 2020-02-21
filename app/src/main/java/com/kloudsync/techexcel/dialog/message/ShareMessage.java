package com.kloudsync.techexcel.dialog.message;

import android.os.Parcel;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.rong.common.ParcelUtils;
import io.rong.common.RLog;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

//RC:SimpleMsg
/*UB:FriendMsg
UB:SystemMsg
UB:KnowledgeMsg*/

@MessageTag(value = "UB:UBShareDocMessageType", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class ShareMessage extends MessageContent {

    private String shareDocTitle;//contect
    private String shareDocUrl;//文档URl
    private String shareDocThumbnailUrl;//缩略图Url
    private String shareDocAvatarUrl;       //头像Url
    private String shareDocUsername;     //发送人name
    private String shareDocTime;            //发送时间
    private String attachmentID;              //文档的attachment

    public String getShareDocTitle() {
        return shareDocTitle == null ? "" : shareDocTitle;
    }

    public void setShareDocTitle(String shareDocTitle) {
        this.shareDocTitle = shareDocTitle;
    }

    public String getShareDocUrl() {
        return shareDocUrl == null ? "" : shareDocUrl;
    }

    public void setShareDocUrl(String shareDocUrl) {
        this.shareDocUrl = shareDocUrl;
    }

    public String getShareDocThumbnailUrl() {
        return shareDocThumbnailUrl == null ? "" : shareDocThumbnailUrl;
    }

    public void setShareDocThumbnailUrl(String shareDocThumbnailUrl) {
        this.shareDocThumbnailUrl = shareDocThumbnailUrl;
    }

    public String getShareDocAvatarUrl() {
        return shareDocAvatarUrl == null ? "" : shareDocAvatarUrl;
    }

    public void setShareDocAvatarUrl(String shareDocAvatarUrl) {
        this.shareDocAvatarUrl = shareDocAvatarUrl;
    }

    public String getShareDocUsername() {
        return shareDocUsername == null ? "" : shareDocUsername;
    }

    public void setShareDocUsername(String shareDocUsername) {
        this.shareDocUsername = shareDocUsername;
    }

    public String getShareDocTime() {
        return shareDocTime == null ? "" : shareDocTime;
    }

    public void setShareDocTime(String shareDocTime) {
        this.shareDocTime = shareDocTime;
    }

    public String getAttachmentID() {
        return attachmentID == null ? "" : attachmentID;
    }

    public void setAttachmentID(String attachmentID) {
        this.attachmentID = attachmentID;
    }

    public ShareMessage(String shareDocTitle, String shareDocUrl, String shareDocThumbnailUrl,
                        String shareDocAvatarUrl, String shareDocUsername, String shareDocTime, String attachmentID) {
        this.shareDocTitle = shareDocTitle;
        this.shareDocUrl = shareDocUrl;
        this.shareDocThumbnailUrl = shareDocThumbnailUrl;
        this.shareDocAvatarUrl = shareDocAvatarUrl;
        this.shareDocUsername = shareDocUsername;
        this.shareDocTime = shareDocTime;
        this.attachmentID = attachmentID;
    }

    public ShareMessage() {
    }

    public ShareMessage(byte[] data) {
        String jsonStr = null;

        try {
            jsonStr = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e1) {

        }

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            Log.e("jsonObj", jsonObj.toString() + "");

            if (jsonObj.has("shareDocTitle")) {
                shareDocTitle = jsonObj.optString("shareDocTitle");
            }

            if (jsonObj.has("shareDocUrl")) {
                shareDocUrl = jsonObj.optString("shareDocUrl");
            }

            if (jsonObj.has("shareDocThumbnailUrl")) {
                shareDocThumbnailUrl = jsonObj.optString("shareDocThumbnailUrl");
            }

            if (jsonObj.has("shareDocAvatarUrl")) {
                shareDocAvatarUrl = jsonObj.optString("shareDocAvatarUrl");
            }
            if (jsonObj.has("shareDocUsername")) {
                shareDocUsername = jsonObj.optString("shareDocUsername");
            }
            if (jsonObj.has("shareDocTime")) {
                shareDocTime = jsonObj.optString("shareDocTime");
            }
            if (jsonObj.has("attachmentID")) {
                attachmentID = jsonObj.optString("attachmentID");
            }

        } catch (JSONException e) {
            RLog.e("JSONException", e.getMessage());
        }

    }

    //给消息赋值。
    public ShareMessage(Parcel in) {
        shareDocTitle = ParcelUtils.readFromParcel(in);//该类为工具类，消息属性...
        //这里可继续增加你消息的属性
        shareDocUrl = ParcelUtils.readFromParcel(in);
        shareDocThumbnailUrl = ParcelUtils.readFromParcel(in);
        shareDocAvatarUrl = ParcelUtils.readFromParcel(in);
        shareDocUsername = ParcelUtils.readFromParcel(in);
        shareDocTime = ParcelUtils.readFromParcel(in);
        attachmentID = ParcelUtils.readFromParcel(in);
    }

    /**
     * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
     */
    public static final Creator<ShareMessage> CREATOR = new Creator<ShareMessage>() {

        @Override
        public ShareMessage createFromParcel(Parcel source) {
            return new ShareMessage(source);
        }

        @Override
        public ShareMessage[] newArray(int size) {
            return new ShareMessage[size];
        }
    };

    /**
     * 描述了包含在 Parcelable 对象排列信息中的特殊对象的类型。
     *
     * @return 一个标志位，表明Parcelable对象特殊对象类型集合的排列。
     */
    public int describeContents() {
        return 0;
    }

    /**
     * 将类的数据写入外部提供的 Parcel 中。
     *
     * @param dest  对象被写入的 Parcel。
     * @param flags 对象如何被写入的附加标志。
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtils.writeToParcel(dest, shareDocTitle);//该类为工具类，对消息中属性进行序列化

        //这里可继续增加你消息的属性
        ParcelUtils.writeToParcel(dest, shareDocUrl);
        ParcelUtils.writeToParcel(dest, shareDocThumbnailUrl);
        ParcelUtils.writeToParcel(dest, shareDocAvatarUrl);
        ParcelUtils.writeToParcel(dest, shareDocUsername);
        ParcelUtils.writeToParcel(dest, shareDocTime);
        ParcelUtils.writeToParcel(dest, attachmentID);
    }


    @Override
    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();

        try {
            jsonObj.put("shareDocTitle", shareDocTitle);
            jsonObj.put("shareDocUrl", shareDocUrl);
            jsonObj.put("shareDocThumbnailUrl", shareDocThumbnailUrl);
            jsonObj.put("shareDocAvatarUrl", shareDocAvatarUrl);
            jsonObj.put("shareDocUsername", shareDocUsername);
            jsonObj.put("shareDocTime", shareDocTime);
            jsonObj.put("attachmentID", attachmentID);
        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage());
        }

        try {
            return jsonObj.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

}