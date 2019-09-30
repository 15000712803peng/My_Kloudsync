package com.kloudsync.techexcel.info;

import java.io.Serializable;

public class SystemShow implements Serializable{

	private String time;
	private String title;
	private String type;
	private String photoUrl;
	private String url;
	
	
	public SystemShow() {
		super();
	}
	public SystemShow(String time, String title, String type, String photoUrl,
			String url) {
		super();
		this.time = time;
		this.title = title;
		this.type = type;
		this.photoUrl = photoUrl;
		this.url = url;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPhotoUrl() {
		return photoUrl;
	}
	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	
}
