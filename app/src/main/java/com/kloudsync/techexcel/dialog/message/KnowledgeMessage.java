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

@MessageTag(value = "UB:KnowledgeMsg", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class KnowledgeMessage extends MessageContent {

	private String content;// 消息属性，可随意定义
	private String title;
	private String knowledgeID;
	private String imageID;
	private String videoInfo;
	
	
	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getKnowledgeID() {
		return knowledgeID;
	}
	public void setKnowledgeID(String knowledgeID) {
		this.knowledgeID = knowledgeID;
	}
	public String getImageID() {
		return imageID;
	}
	public void setImageID(String imageID) {
		this.imageID = imageID;
	}
	public String getVideoInfo() {
		return videoInfo;
	}
	public void setVideoInfo(String videoInfo) {
		this.videoInfo = videoInfo;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getContent() {
		return content;
	}
	
	
	public KnowledgeMessage(String content, String title, String knowledgeID, String imageID, String videoInfo) {
        this.content = content;
        this.title = title;
        this.knowledgeID = knowledgeID;
        this.imageID = imageID;
        this.videoInfo = videoInfo;
    }
	
	public KnowledgeMessage(byte[] data) {
	    String jsonStr = null;

	    try {
	        jsonStr = new String(data, "UTF-8");
	    } catch (UnsupportedEncodingException e1) {

	    }

	    try {
			JSONObject jsonObj = new JSONObject(jsonStr);
			Log.e("jsonObj", jsonObj.toString() + "");

	        if (jsonObj.has("content")){
	            content = jsonObj.optString("content");
	        }
	        
	        if (jsonObj.has("title")){
	        	title = jsonObj.optString("title");
	        }
	        if (jsonObj.has("knowledgeID")){
	        	knowledgeID = jsonObj.optString("knowledgeID");
	        }
	        if (jsonObj.has("imageID")){
	        	imageID = jsonObj.optString("imageID");
	        }
	        if (jsonObj.has("videoInfo")){
	        	videoInfo = jsonObj.optString("videoInfo");
	        }

	    } catch (JSONException e) {
	        RLog.e("JSONException", e.getMessage());
	    }

	}

	//给消息赋值。
	public KnowledgeMessage(Parcel in) {
	    content=ParcelUtils.readFromParcel(in);//该类为工具类，消息属性...
	    //这里可继续增加你消息的属性
	    title=ParcelUtils.readFromParcel(in);
	    knowledgeID=ParcelUtils.readFromParcel(in);
	    imageID=ParcelUtils.readFromParcel(in);
	    videoInfo=ParcelUtils.readFromParcel(in);
	  }

	  /**
	   * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
	   */
	  public static final Creator<KnowledgeMessage> CREATOR = new Creator<KnowledgeMessage>() {

	      @Override
	      public KnowledgeMessage createFromParcel(Parcel source) {
	          return new KnowledgeMessage(source);
	      }

	      @Override
	      public KnowledgeMessage[] newArray(int size) {
	          return new KnowledgeMessage[size];
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
	      ParcelUtils.writeToParcel(dest, content);//该类为工具类，对消息中属性进行序列化
	      
	      //这里可继续增加你消息的属性
	      ParcelUtils.writeToParcel(dest, title);
	      ParcelUtils.writeToParcel(dest, knowledgeID);
	      ParcelUtils.writeToParcel(dest, imageID);
	      ParcelUtils.writeToParcel(dest, videoInfo);
	  }
	

	@Override
	public byte[] encode() {
		JSONObject jsonObj = new JSONObject();

		try {
			jsonObj.put("content", content);
			jsonObj.put("title", title);
			jsonObj.put("knowledgeID", knowledgeID);
			jsonObj.put("imageID", imageID);
			jsonObj.put("videoInfo", videoInfo);
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
