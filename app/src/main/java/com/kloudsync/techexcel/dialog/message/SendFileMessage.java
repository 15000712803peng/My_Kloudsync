package com.kloudsync.techexcel.dialog.message;

import android.os.Parcel;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import io.rong.common.ParcelUtils;
import io.rong.common.RLog;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

//RC:SimpleMsg
/*UB:FriendMsg
UB:SystemMsg
UB:KnowledgeMsg*/

@MessageTag(value = "UB:FavoriteFileMessage", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class SendFileMessage extends MessageContent implements Serializable {

	private String LinkedKWProjectID;// 消息属性，可随意定义
	private String AttachmentID;
	private String IncidentID;
	private String Title;
	private String FileID;
	private String FileName;
	private String FileDownloadURL;
	private String AttachmentTypeID;
	private String CreatedDate;
	private String Status;

	public String getLinkedKWProjectID() {
		return LinkedKWProjectID;
	}

	public void setLinkedKWProjectID(String linkedKWProjectID) {
		LinkedKWProjectID = linkedKWProjectID;
	}

	public String getAttachmentID() {
		return AttachmentID;
	}

	public void setAttachmentID(String attachmentID) {
		AttachmentID = attachmentID;
	}

	public String getIncidentID() {
		return IncidentID;
	}

	public void setIncidentID(String incidentID) {
		IncidentID = incidentID;
	}

	public String getFileID() {
		return FileID;
	}

	public void setFileID(String fileID) {
		FileID = fileID;
	}

	public String getFileName() {
		return FileName;
	}

	public void setFileName(String fileName) {
		FileName = fileName;
	}

	public String getFileDownloadURL() {
		return FileDownloadURL;
	}

	public void setFileDownloadURL(String fileDownloadURL) {
		FileDownloadURL = fileDownloadURL;
	}

	public String getAttachmentTypeID() {
		return AttachmentTypeID;
	}

	public void setAttachmentTypeID(String attachmentTypeID) {
		AttachmentTypeID = attachmentTypeID;
	}

	public String getCreatedDate() {
		return CreatedDate;
	}

	public void setCreatedDate(String createdDate) {
		CreatedDate = createdDate;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public SendFileMessage() {
	}

	public SendFileMessage(String linkedKWProjectID,
						   String attachmentID, String incidentID,
						   String title, String fileID, String fileName,
						   String fileDownloadURL, String attachmentTypeID,
						   String createdDate, String status) {
		LinkedKWProjectID = linkedKWProjectID;
		AttachmentID = attachmentID;
		IncidentID = incidentID;
		Title = title;
		FileID = fileID;
		FileName = fileName;
		FileDownloadURL = fileDownloadURL;
		AttachmentTypeID = attachmentTypeID;
		CreatedDate = createdDate;
		Status = status;
	}


	public SendFileMessage(byte[] data) {
	    String jsonStr = null;

	    try {
	        jsonStr = new String(data, "UTF-8");
	    } catch (UnsupportedEncodingException e1) {

	    }

	    try {
			JSONObject jsonObj = new JSONObject(jsonStr);
			Log.e("jsonObj", jsonObj.toString() + "");

	        if (jsonObj.has("LinkedKWProjectID")){
				LinkedKWProjectID = jsonObj.optString("LinkedKWProjectID");
	        }

	        if (jsonObj.has("AttachmentID")){
				AttachmentID = jsonObj.optString("AttachmentID");
	        }
	        if (jsonObj.has("IncidentID")){
				IncidentID = jsonObj.optString("IncidentID");
	        }
	        if (jsonObj.has("Title")){
				Title = jsonObj.optString("Title");
	        }
	        if (jsonObj.has("FileID")){
				FileID = jsonObj.optString("FileID");
	        }
	        if (jsonObj.has("FileName")){
				FileName = jsonObj.optString("FileName");
	        }
	        if (jsonObj.has("FileDownloadURL")){
				FileDownloadURL = jsonObj.optString("FileDownloadURL");
	        }
	        if (jsonObj.has("AttachmentTypeID")){
				AttachmentTypeID = jsonObj.optString("AttachmentTypeID");
	        }
	        if (jsonObj.has("CreatedDate")){
				CreatedDate = jsonObj.optString("CreatedDate");
	        }
	        if (jsonObj.has("Status")){
				Status = jsonObj.optString("Status");
	        }

	    } catch (JSONException e) {
	        RLog.e("JSONException", e.getMessage());
	    }

	}

	//给消息赋值。
	public SendFileMessage(Parcel in) {
		LinkedKWProjectID=ParcelUtils.readFromParcel(in);//该类为工具类，消息属性...
	    //这里可继续增加你消息的属性
		AttachmentID=ParcelUtils.readFromParcel(in);
		IncidentID=ParcelUtils.readFromParcel(in);
		Title=ParcelUtils.readFromParcel(in);
		FileID=ParcelUtils.readFromParcel(in);
		FileName=ParcelUtils.readFromParcel(in);
		FileDownloadURL=ParcelUtils.readFromParcel(in);
		AttachmentTypeID=ParcelUtils.readFromParcel(in);
		CreatedDate=ParcelUtils.readFromParcel(in);
		Status=ParcelUtils.readFromParcel(in);
	  }

	  /**
	   * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
	   */
	  public static final Creator<SendFileMessage> CREATOR = new Creator<SendFileMessage>() {

	      @Override
	      public SendFileMessage createFromParcel(Parcel source) {
	          return new SendFileMessage(source);
	      }

	      @Override
	      public SendFileMessage[] newArray(int size) {
	          return new SendFileMessage[size];
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
	      ParcelUtils.writeToParcel(dest, LinkedKWProjectID);//该类为工具类，对消息中属性进行序列化
	      
	      //这里可继续增加你消息的属性
	      ParcelUtils.writeToParcel(dest, AttachmentID);
	      ParcelUtils.writeToParcel(dest, IncidentID);
	      ParcelUtils.writeToParcel(dest, Title);
	      ParcelUtils.writeToParcel(dest, FileID);
	      ParcelUtils.writeToParcel(dest, FileName);
	      ParcelUtils.writeToParcel(dest, FileDownloadURL);
	      ParcelUtils.writeToParcel(dest, AttachmentTypeID);
	      ParcelUtils.writeToParcel(dest, CreatedDate);
	      ParcelUtils.writeToParcel(dest, Status);
	  }
	

	@Override
	public byte[] encode() {
		JSONObject jsonObj = new JSONObject();

		try {
			jsonObj.put("LinkedKWProjectID", LinkedKWProjectID);
			jsonObj.put("AttachmentID", AttachmentID);
			jsonObj.put("IncidentID", IncidentID);
			jsonObj.put("Title", Title);
			jsonObj.put("FileID", FileID);
			jsonObj.put("FileName", FileName);
			jsonObj.put("FileDownloadURL", FileDownloadURL);
			jsonObj.put("AttachmentTypeID", AttachmentTypeID);
			jsonObj.put("CreatedDate", CreatedDate);
			jsonObj.put("FileID", Status);
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
