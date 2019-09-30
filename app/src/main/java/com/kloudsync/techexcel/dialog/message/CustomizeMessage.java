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

@MessageTag(value = "UB:SimpleMsg", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class CustomizeMessage extends MessageContent {

	private String serviceID;// 消息属性，可随意定义
	private String targetName;
	private String tags;
	private String price;
	
	
	
	public String getServiceID() {
		return serviceID;
	}

	public void setServiceID(String serviceID) {
		this.serviceID = serviceID;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public CustomizeMessage(String serviceID, String targetName,
			String tags, String price) {
		this.serviceID = serviceID;
		this.targetName = targetName;
		this.tags = tags;
		this.price = price;
	}
	
	public CustomizeMessage(byte[] data) {
	    String jsonStr = null;

	    try {
	        jsonStr = new String(data, "UTF-8");
	    } catch (UnsupportedEncodingException e1) {

	    }

	    try {
			JSONObject jsonObj = new JSONObject(jsonStr);
			Log.e("jsonObj", jsonObj.toString() + "");

	        if (jsonObj.has("serviceID")){
	        	serviceID = jsonObj.optString("serviceID");
	        }
	        
	        if (jsonObj.has("targetName")){
	        	targetName = jsonObj.optString("targetName");
	        }

	        if (jsonObj.has("tags")){
	        	tags = jsonObj.optString("tags");
	        }

	        if (jsonObj.has("price")){
	        	price = jsonObj.optString("price");
	        }

	    } catch (JSONException e) {
	        RLog.e("JSONException", e.getMessage());
	    }

	}

	//给消息赋值。
	public CustomizeMessage(Parcel in) {
		serviceID=ParcelUtils.readFromParcel(in);//该类为工具类，消息属性...
	    //这里可继续增加你消息的属性
		targetName=ParcelUtils.readFromParcel(in);
		tags=ParcelUtils.readFromParcel(in);
		price=ParcelUtils.readFromParcel(in);
	  }

	  /**
	   * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
	   */
	  public static final Creator<CustomizeMessage> CREATOR = new Creator<CustomizeMessage>() {

	      @Override
	      public CustomizeMessage createFromParcel(Parcel source) {
	          return new CustomizeMessage(source);
	      }

	      @Override
	      public CustomizeMessage[] newArray(int size) {
	          return new CustomizeMessage[size];
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
	      ParcelUtils.writeToParcel(dest, serviceID);//该类为工具类，对消息中属性进行序列化
	      
	      //这里可继续增加你消息的属性
	      ParcelUtils.writeToParcel(dest, targetName);
	      ParcelUtils.writeToParcel(dest, tags);
	      ParcelUtils.writeToParcel(dest, price);
	  }
	

	@Override
	public byte[] encode() {
		JSONObject jsonObj = new JSONObject();

		try {
			jsonObj.put("serviceID", serviceID);
			jsonObj.put("targetName", targetName);
			jsonObj.put("tags", tags);
			jsonObj.put("price", price);
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
