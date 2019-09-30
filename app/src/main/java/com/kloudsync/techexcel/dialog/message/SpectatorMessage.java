package com.kloudsync.techexcel.dialog.message;

import android.os.Parcel;
import android.util.Log;

import com.kloudsync.techexcel.info.Spectator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import io.rong.common.ParcelUtils;
import io.rong.common.RLog;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

//RC:SimpleMsg
/*UB:FriendMsg
UB:SystemMsg
UB:KnowledgeMsg*/

@MessageTag(value = "UB:AuditorMsg", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class SpectatorMessage extends MessageContent {

    private String auditor_meetingID;// 消息属性，可随意定义
    private String auditor_attachmentUrl;
    private String current_presenter;
    private List<Spectator> collection_dataArray;

    public String getAuditor_meetingID() {
        return auditor_meetingID;
    }

    public void setAuditor_meetingID(String auditor_meetingID) {
        this.auditor_meetingID = auditor_meetingID;
    }

    public String getAuditor_attachmentUrl() {
        return auditor_attachmentUrl;
    }

    public void setAuditor_attachmentUrl(String auditor_attachmentUrl) {
        this.auditor_attachmentUrl = auditor_attachmentUrl;
    }

    public String getCurrent_presenter() {
        return current_presenter;
    }

    public void setCurrent_presenter(String current_presenter) {
        this.current_presenter = current_presenter;
    }

    public List<Spectator> getCollection_dataArray() {
        return collection_dataArray;
    }

    public void setCollection_dataArray(List<Spectator> collection_dataArray) {
        this.collection_dataArray = collection_dataArray;
    }

    public SpectatorMessage() {
    }

    public SpectatorMessage(String auditor_meetingID, String auditor_attachmentUrl, String current_presenter) {
        this.auditor_meetingID = auditor_meetingID;
        this.auditor_attachmentUrl = auditor_attachmentUrl;
        this.current_presenter = current_presenter;
    }

    public SpectatorMessage(String auditor_meetingID, String auditor_attachmentUrl, String current_presenter, List<Spectator> collection_dataArray) {
        this.auditor_meetingID = auditor_meetingID;
        this.auditor_attachmentUrl = auditor_attachmentUrl;
        this.current_presenter = current_presenter;
        this.collection_dataArray = collection_dataArray;
    }

    public SpectatorMessage(byte[] data) {
        String jsonStr = null;

        try {
            jsonStr = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e1) {

        }

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            Log.e("Spectator jsonObj", jsonObj.toString() + "");

            if (jsonObj.has("auditor_meetingID")) {
                auditor_meetingID = jsonObj.optString("auditor_meetingID");
            }

            if (jsonObj.has("auditor_attachmentUrl")) {
                auditor_attachmentUrl = jsonObj.optString("auditor_attachmentUrl");
            }

            if (jsonObj.has("current_presenter")) {
                current_presenter = jsonObj.optString("current_presenter");
            }

            if (jsonObj.has("collection_dataArray")) {
                JSONArray RetDatas = jsonObj.getJSONArray("collection_dataArray");

                List<Spectator> xxxx = new ArrayList<>();
                for (int i = 0; i < RetDatas.length(); i++) {
                    JSONObject RetData = RetDatas.getJSONObject(i);
                    Spectator st = new Spectator();
                    if (RetData.has("IdentityType")) {
                        int IdentityType = RetData.getInt("IdentityType");
                        st.setIdentityType(IdentityType);
                    }
                    if (RetData.has("AvatarUrl")) {
                        String AvatarUrl = RetData.getString("AvatarUrl");
                        st.setAvatarUrl(AvatarUrl);
                    }
                    if (RetData.has("Name")) {
                        String Name = RetData.getString("Name");
                        st.setName(Name);
                    }
                    if (RetData.has("Identity")) {
                        String Identity = RetData.getString("Identity");
                        st.setIdentity(Identity);
                    }
                    xxxx.add(st);
                }
                collection_dataArray = xxxx;
            }


        } catch (JSONException e) {
            RLog.e("JSONException", e.getMessage());
        }

    }

    //给消息赋值。
    public SpectatorMessage(Parcel in) {
        auditor_meetingID = ParcelUtils.readFromParcel(in);//该类为工具类，消息属性...
        //这里可继续增加你消息的属性
        auditor_attachmentUrl = ParcelUtils.readFromParcel(in);
        current_presenter = ParcelUtils.readFromParcel(in);
        collection_dataArray = ParcelUtils.readListFromParcel(in, Spectator.class);
    }

    /**
     * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
     */
    public static final Creator<SpectatorMessage> CREATOR = new Creator<SpectatorMessage>() {

        @Override
        public SpectatorMessage createFromParcel(Parcel source) {
            return new SpectatorMessage(source);
        }

        @Override
        public SpectatorMessage[] newArray(int size) {
            return new SpectatorMessage[size];
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
        ParcelUtils.writeToParcel(dest, auditor_meetingID);//该类为工具类，对消息中属性进行序列化

        //这里可继续增加你消息的属性
        ParcelUtils.writeToParcel(dest, auditor_attachmentUrl);
        ParcelUtils.writeToParcel(dest, current_presenter);
        ParcelUtils.writeListToParcel(dest, collection_dataArray);
    }


    @Override
    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();

        try {
            jsonObj.put("auditor_meetingID", auditor_meetingID);
            jsonObj.put("auditor_attachmentUrl", auditor_attachmentUrl);
            jsonObj.put("current_presenter", current_presenter);
            JSONArray array = new JSONArray();
            for (int i = 0; i < collection_dataArray.size(); i++) {
                Spectator sp = collection_dataArray.get(i);
                JSONObject js = new JSONObject();
                js.put("IdentityType", sp.getIdentityType());
                js.put("AvatarUrl", sp.getAvatarUrl());
                js.put("Name", sp.getName());
                js.put("Identity", sp.getIdentity());
                array.put(i, js);
            }
            jsonObj.put("collection_dataArray", array);
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