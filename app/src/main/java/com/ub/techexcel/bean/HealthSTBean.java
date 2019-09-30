package com.ub.techexcel.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HealthSTBean implements Serializable {

	/**
	 *  治疗方案 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private String description;

	/**
	 * 方案具体信息
	 */
	private  List<LineItem> mLineItems=new ArrayList<LineItem>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<LineItem> getmLineItems() {
		return mLineItems;
	}

	public void setmLineItems(List<LineItem> mLineItems) {
		this.mLineItems = mLineItems;
	}

	

}
