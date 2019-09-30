package com.kloudsync.techexcel.bean;

import java.io.Serializable;

public class ConditionBean  implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	private  int id;
	private  String filterName;
	private String filterValue;
	private  String url;
	private String title;
	private String filterValueID;
	private boolean  isselect=false;
	private boolean  istemplate=false;
	
	//数据库
	
	private int  userId;
	private int projectId;
	
	
	
	
	
	
	
	public ConditionBean() {}
	public ConditionBean(int id, String filterName, String url,String title) {
		this.id = id;
		this.filterName = filterName;
		this.url = url;
		this.title=title;
	}
	
	
	
	
	




	public boolean isIstemplate() {
		return istemplate;
	}
	public void setIstemplate(boolean istemplate) {
		this.istemplate = istemplate;
	}
	public boolean isIsselect() {
		return isselect;
	}
	public void setIsselect(boolean isselect) {
		this.isselect = isselect;
	}
	public String getFilterValueID() {
		return filterValueID;
	}
	public void setFilterValueID(String filterValueID) {
		this.filterValueID = filterValueID;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFilterName() {
		return filterName;
	}
	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getFilterValue() {
		return filterValue;
	}
	public void setFilterValue(String filterValue) {
		this.filterValue = filterValue;
	}
	
	

}
