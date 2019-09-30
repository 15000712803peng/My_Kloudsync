package com.ub.techexcel.bean;

import java.io.Serializable;

public class KnowledgeType implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String  name;
	private int id;
	private String identifier;
	private int systemType;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public int getSystemType() {
		return systemType;
	}
	public void setSystemType(int systemType) {
		this.systemType = systemType;
	}
	
      
}
