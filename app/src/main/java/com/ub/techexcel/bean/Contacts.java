package com.ub.techexcel.bean;

import android.graphics.Bitmap;

public class Contacts{
   
	private long id;       //联系人id
	private String name;   //联系人姓名
	private String sortLetters; //姓名字母
	private Bitmap bitmap;//
	private int flag;
	private String phonenumber; //联系人手机号
	private String phonetype;//手机类型
	
	

	public Contacts(String name) {
		this.name = name;
	}
	public String getPhonetype() {
		return phonetype;
	}
	public void setPhonetype(String phonetype) {
		this.phonetype = phonetype;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getPhonenumber() {
		return phonenumber;
	}
	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}
	
	
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	
	
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
}
