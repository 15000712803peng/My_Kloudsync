package com.kloudsync.techexcel.bean;

import java.io.Serializable;

public class LineItem implements Serializable {
	/**
	 * 解决方案具体信息(片段)
	 */
	private static final long serialVersionUID = 0x110;
	private int id;
	private String name;
	private String description;
	private int itemTypeID;
	private int checkOption; // Optional No = 0, Optional Yes=1, Mandatory=2

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

	public int getItemTypeID() {
		return itemTypeID;
	}

	public void setItemTypeID(int itemTypeID) {
		this.itemTypeID = itemTypeID;
	}

	public int getCheckOption() {
		return checkOption;
	}

	public void setCheckOption(int checkOption) {
		this.checkOption = checkOption;
	}

}
