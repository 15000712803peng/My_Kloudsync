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

@MessageTag(value = "UB:MeetingMsg", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class CourseMessage extends MessageContent {

    private String rongCloudUserID;// 消息属性，可随意定义
    private String attachmentUrl;
    private String meetingId;
    private String fromName;
    private String itemId;
    private String roleId;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getRongCloudUserID() {
        return rongCloudUserID;
    }

    public void setRongCloudUserID(String rongCloudUserID) {
        this.rongCloudUserID = rongCloudUserID;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }


    public CourseMessage(String rongCloudUserID, String attachmentUrl, String meetingId, String fromName
            , String itemId) {
        this.rongCloudUserID = rongCloudUserID;
        this.attachmentUrl = attachmentUrl;
        this.meetingId = meetingId;
        this.fromName = fromName;
        this.itemId = itemId;
    }

    public CourseMessage(String rongCloudUserID, String attachmentUrl, String meetingId, String fromName) {
        this.rongCloudUserID = rongCloudUserID;
        this.attachmentUrl = attachmentUrl;
        this.meetingId = meetingId;
        this.fromName = fromName;
    }

    public CourseMessage(String rongCloudUserID, String attachmentUrl, String meetingId) {
        this.rongCloudUserID = rongCloudUserID;
        this.attachmentUrl = attachmentUrl;
        this.meetingId = meetingId;
    }


    public CourseMessage(byte[] data) {
        String jsonStr = null;

        try {
            jsonStr = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e1) {

        }

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            Log.e("jsonObj", jsonObj.toString() + "");

            if (jsonObj.has("rongCloudUserID")) {
                rongCloudUserID = jsonObj.optString("rongCloudUserID");
            }

            if (jsonObj.has("attachmentUrl")) {
                attachmentUrl = jsonObj.optString("attachmentUrl");
            }

            if (jsonObj.has("meetingId")) {
                meetingId = jsonObj.optString("meetingId");
            }

            if (jsonObj.has("fromName")) {
                fromName = jsonObj.optString("fromName");
            }
            if (jsonObj.has("itemId")) {
                itemId = jsonObj.optString("itemId");
            }
            if (jsonObj.has("roleId")) {
                roleId = jsonObj.optString("roleId");
            }

        } catch (JSONException e) {
            RLog.e("JSONException", e.getMessage());
        }

    }

    //给消息赋值。
    public CourseMessage(Parcel in) {
        rongCloudUserID = ParcelUtils.readFromParcel(in);//该类为工具类，消息属性...
        //这里可继续增加你消息的属性
        attachmentUrl = ParcelUtils.readFromParcel(in);
        meetingId = ParcelUtils.readFromParcel(in);
        fromName = ParcelUtils.readFromParcel(in);
        itemId = ParcelUtils.readFromParcel(in);
        roleId = ParcelUtils.readFromParcel(in);
    }

    /**
     * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
     */
    public static final Creator<CourseMessage> CREATOR = new Creator<CourseMessage>() {

        @Override
        public CourseMessage createFromParcel(Parcel source) {
            return new CourseMessage(source);
        }

        @Override
        public CourseMessage[] newArray(int size) {
            return new CourseMessage[size];
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
        ParcelUtils.writeToParcel(dest, rongCloudUserID);//该类为工具类，对消息中属性进行序列化

        //这里可继续增加你消息的属性
        ParcelUtils.writeToParcel(dest, attachmentUrl);
        ParcelUtils.writeToParcel(dest, meetingId);
        ParcelUtils.writeToParcel(dest, fromName);
        ParcelUtils.writeToParcel(dest, itemId);
        ParcelUtils.writeToParcel(dest, roleId);
    }


    @Override
    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();

        try {
            jsonObj.put("rongCloudUserID", rongCloudUserID);
            jsonObj.put("attachmentUrl", attachmentUrl);
            jsonObj.put("meetingId", meetingId);
            jsonObj.put("fromName", fromName);
            jsonObj.put("itemId", itemId);
            jsonObj.put("roleId", roleId);
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