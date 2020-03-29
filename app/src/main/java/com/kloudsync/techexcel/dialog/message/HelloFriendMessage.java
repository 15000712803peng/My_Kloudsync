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

@MessageTag(value = "Kloud:FriendMsg", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class HelloFriendMessage extends MessageContent {

    private String ifPromotor;
    private String userName;
    private String status;
    private String rongCloudId;
    private String messageContent;

    public String getIfPromotor() {
        return ifPromotor;
    }

    public void setIfPromotor(String ifPromotor) {
        this.ifPromotor = ifPromotor;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRongCloudId() {
        return rongCloudId;
    }

    public void setRongCloudId(String rongCloudId) {
        this.rongCloudId = rongCloudId;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public HelloFriendMessage(String ifPromotor,
                              String userName,
                              String status,
                              String rongCloudId,
                              String messageContent) {
        this.ifPromotor = ifPromotor;
        this.userName = userName;
        this.status = status;
        this.rongCloudId = rongCloudId;
        this.messageContent = messageContent;
    }

    public HelloFriendMessage() {
    }

    public HelloFriendMessage(byte[] data) {
        String jsonStr = null;

        try {
            jsonStr = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e1) {

        }

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            Log.e("jsonObj", jsonObj.toString() + "");

            if (jsonObj.has("ifPromotor")) {
                ifPromotor = jsonObj.optString("ifPromotor");
            }

            if (jsonObj.has("userName")) {
                userName = jsonObj.optString("userName");
            }

            if (jsonObj.has("status")) {
                status = jsonObj.optString("status");
            }

            if (jsonObj.has("rongCloudId")) {
                rongCloudId = jsonObj.optString("rongCloudId");
            }
            if (jsonObj.has("messageContent")) {
                messageContent = jsonObj.optString("messageContent");
            }

        } catch (JSONException e) {
            RLog.e("JSONException", e.getMessage());
        }

    }

    //给消息赋值。
    public HelloFriendMessage(Parcel in) {
        ifPromotor = ParcelUtils.readFromParcel(in);//该类为工具类，消息属性...
        //这里可继续增加你消息的属性
        userName = ParcelUtils.readFromParcel(in);
        status = ParcelUtils.readFromParcel(in);
        rongCloudId = ParcelUtils.readFromParcel(in);
        messageContent = ParcelUtils.readFromParcel(in);
    }

    /**
     * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
     */
    public static final Creator<HelloFriendMessage> CREATOR = new Creator<HelloFriendMessage>() {

        @Override
        public HelloFriendMessage createFromParcel(Parcel source) {
            return new HelloFriendMessage(source);
        }

        @Override
        public HelloFriendMessage[] newArray(int size) {
            return new HelloFriendMessage[size];
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
        ParcelUtils.writeToParcel(dest, ifPromotor);//该类为工具类，对消息中属性进行序列化

        //这里可继续增加你消息的属性
        ParcelUtils.writeToParcel(dest, userName);
        ParcelUtils.writeToParcel(dest, status);
        ParcelUtils.writeToParcel(dest, rongCloudId);
        ParcelUtils.writeToParcel(dest, messageContent);

    }


    @Override
    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("ifPromotor", ifPromotor);
            jsonObj.put("userName", userName);
            jsonObj.put("rongCloudId", rongCloudId);
            jsonObj.put("messageContent", messageContent);
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