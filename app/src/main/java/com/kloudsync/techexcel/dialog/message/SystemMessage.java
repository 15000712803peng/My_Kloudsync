package com.kloudsync.techexcel.dialog.message;

import android.os.Parcel;
import android.util.Log;

import com.kloudsync.techexcel.info.SystemShow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import io.rong.common.ParcelUtils;
import io.rong.common.RLog;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

//RC:SimpleMsg
/*UB:FriendMsg
UB:SystemMsg
UB:KnowledgeMsg*/

@MessageTag(value = "UB:SystemMsg", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class SystemMessage extends MessageContent {

	private String extra;// 消息属性，可随意定义
	private String firstTitle;// 消息属性，可随意定义
	private ArrayList<SystemShow> list = new ArrayList<SystemShow>();
	
	
	public String getFirstTitle() {
		return firstTitle;
	}

	public void setFirstTitle(String firstTitle) {
		this.firstTitle = firstTitle;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public ArrayList<SystemShow> getList() {
		return list;
	}

	public void setList(ArrayList<SystemShow> list) {
		this.list = list;
	}

	public SystemMessage(String extra, String firstTitle, ArrayList<SystemShow> list) {
		this.extra = extra;
		this.firstTitle = firstTitle;
		this.list = list;
	}
	
	public SystemMessage(byte[] data) {
	    String jsonStr = null;

	    try {
	        jsonStr = new String(data, "UTF-8");
	    } catch (UnsupportedEncodingException e1) {

	    }

	    try {
			JSONObject jsonObj = new JSONObject(jsonStr);
			Log.e("system jsonObj", jsonObj.toString() + "");

	        if (jsonObj.has("extra")){
	        	extra = jsonObj.optString("extra");
	        }
	        if (jsonObj.has("firstTitle")){
	        	firstTitle = jsonObj.optString("firstTitle");
	        }
	        if (jsonObj.has("dataArray")){
		        JSONArray dataArray = jsonObj.getJSONArray("dataArray");
		        list = new ArrayList<SystemShow>();
		        for (int i = 0; i < dataArray.length(); i++) {
					JSONObject RetData = dataArray.getJSONObject(i);
					String time = RetData.getString("time");
					String title = RetData.getString("title");
					String type = RetData.getString("type");
					String photoUrl = RetData.getString("photoUrl");
					String url = RetData.getString("url");
					
					SystemShow ss = new SystemShow(time, title, type, photoUrl, url);
					
					list.add(ss);
				}
	        }
	        

	    } catch (JSONException e) {
	        RLog.e("JSONException", e.getMessage());
	    }

	}

	//给消息赋值。
	public SystemMessage(Parcel in) {
		extra=ParcelUtils.readFromParcel(in);//该类为工具类，消息属性...
		firstTitle=ParcelUtils.readFromParcel(in);
	    //这里可继续增加你消息的属性
		list = ParcelUtils.readListFromParcel(in, SystemShow.class);
	  }

	  /**
	   * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
	   */
	  public static final Creator<SystemMessage> CREATOR = new Creator<SystemMessage>() {

	      @Override
	      public SystemMessage createFromParcel(Parcel source) {
	          return new SystemMessage(source);
	      }

	      @Override
	      public SystemMessage[] newArray(int size) {
	          return new SystemMessage[size];
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
	      ParcelUtils.writeToParcel(dest, extra);//该类为工具类，对消息中属性进行序列化
	      ParcelUtils.writeToParcel(dest, firstTitle);

	      ParcelUtils.writeListToParcel(dest, list);
	      //这里可继续增加你消息的属性
	  }
	

	@Override
	public byte[] encode() {
		JSONObject jsonObj = new JSONObject();

		try {
			jsonObj.put("extra", extra);
			jsonObj.put("firstTitle", firstTitle);
			JSONArray dataArray = new JSONArray();
			for (int i = 0; i < list.size(); i++) {
				SystemShow ss = list.get(i);
				JSONObject js = new JSONObject();
				js.put("time", ss.getTime());
				js.put("title", ss.getTitle());
				js.put("type", ss.getType());
				js.put("photoUrl", ss.getPhotoUrl());
				js.put("url", ss.getUrl());
				dataArray.put(i, js);
			}
			jsonObj.put("dataArray", dataArray);
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
