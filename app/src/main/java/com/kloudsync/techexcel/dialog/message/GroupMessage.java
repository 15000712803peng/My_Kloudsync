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

@MessageTag(value = "UB:GroupMsg", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class GroupMessage extends MessageContent {

	private String messageContent;// 消息属性，可随意定义
	
	
	

	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}

	public GroupMessage(String messageContent) {
		this.messageContent = messageContent;
	}
	
	public GroupMessage(byte[] data) {
	    String jsonStr = null;

	    try {
	        jsonStr = new String(data, "UTF-8");
	    } catch (UnsupportedEncodingException e1) {

	    }

	    try {
			JSONObject jsonObj = new JSONObject(jsonStr);
			Log.e("jsonObj", jsonObj.toString() + "");

	        if (jsonObj.has("messageContent")){
	        	messageContent = jsonObj.optString("messageContent");
	        }

	    } catch (JSONException e) {
	        RLog.e("JSONException", e.getMessage());
	    }

	}

	//给消息赋值。
	public GroupMessage(Parcel in) {
		messageContent=ParcelUtils.readFromParcel(in);
	  }

	  /**
	   * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
	   */
	  public static final Creator<GroupMessage> CREATOR = new Creator<GroupMessage>() {

	      @Override
	      public GroupMessage createFromParcel(Parcel source) {
	          return new GroupMessage(source);
	      }

	      @Override
	      public GroupMessage[] newArray(int size) {
	          return new GroupMessage[size];
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
	      ParcelUtils.writeToParcel(dest, messageContent);
	  }
	

	@Override
	public byte[] encode() {
		JSONObject jsonObj = new JSONObject();

		try {
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
