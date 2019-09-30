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

@MessageTag(value = "UB:FriendMsg", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class FriendMessage extends MessageContent {

	private String sourceID;// 消息属性，可随意定义
	private String targetID;
	private String time;
	private String type;
	private String messageContent;
	
	
	
	
	public String getSourceID() {
		return sourceID;
	}

	public void setSourceID(String sourceID) {
		this.sourceID = sourceID;
	}

	public String getTargetID() {
		return targetID;
	}

	public void setTargetID(String targetID) {
		this.targetID = targetID;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}

	public FriendMessage(String sourceID, String targetID, String time,
			String type, String messageContent) {
		this.sourceID = sourceID;
		this.targetID = targetID;
		this.time = time;
		this.type = type;
		this.messageContent = messageContent;
	}
	
	public FriendMessage(byte[] data) {
	    String jsonStr = null;

	    try {
	        jsonStr = new String(data, "UTF-8");
	    } catch (UnsupportedEncodingException e1) {

	    }

	    try {
			JSONObject jsonObj = new JSONObject(jsonStr);
			Log.e("jsonObj", jsonObj.toString() + "");

	        if (jsonObj.has("sourceID")){
	        	sourceID = jsonObj.optString("sourceID");
	        }
	        
	        if (jsonObj.has("targetID")){
	        	targetID = jsonObj.optString("targetID");
	        }
	        if (jsonObj.has("time")){
	        	time = jsonObj.optString("time");
	        }
	        if (jsonObj.has("type")){
	        	type = jsonObj.optString("type");
	        }
	        if (jsonObj.has("messageContent")){
	        	messageContent = jsonObj.optString("messageContent");
	        }

	    } catch (JSONException e) {
	        RLog.e("JSONException", e.getMessage());
	    }

	}

	//给消息赋值。
	public FriendMessage(Parcel in) {
		sourceID=ParcelUtils.readFromParcel(in);//该类为工具类，消息属性...
	    //这里可继续增加你消息的属性
		targetID=ParcelUtils.readFromParcel(in);
		time=ParcelUtils.readFromParcel(in);
		type=ParcelUtils.readFromParcel(in);
		messageContent=ParcelUtils.readFromParcel(in);
	  }

	  /**
	   * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
	   */
	  public static final Creator<FriendMessage> CREATOR = new Creator<FriendMessage>() {

	      @Override
	      public FriendMessage createFromParcel(Parcel source) {
	          return new FriendMessage(source);
	      }

	      @Override
	      public FriendMessage[] newArray(int size) {
	          return new FriendMessage[size];
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
	      ParcelUtils.writeToParcel(dest, sourceID);//该类为工具类，对消息中属性进行序列化
	      
	      //这里可继续增加你消息的属性
	      ParcelUtils.writeToParcel(dest, targetID);
	      ParcelUtils.writeToParcel(dest, time);
	      ParcelUtils.writeToParcel(dest, type);
	      ParcelUtils.writeToParcel(dest, messageContent);
	  }
	

	@Override
	public byte[] encode() {
		JSONObject jsonObj = new JSONObject();

		try {
			jsonObj.put("sourceID", sourceID);
			jsonObj.put("targetID", targetID);
			jsonObj.put("time", time);
			jsonObj.put("type", type);
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
