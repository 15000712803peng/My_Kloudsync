package com.ub.techexcel.bean;

import java.io.Serializable;

public class Knowledge implements Serializable{

	private int projectID;
	private int knowledgeID;
	private String description;
	private int createdByID;
	private String dateCreated;
	private int crntOwnerID;
	private String crntOwnerName;
	private int itemTypeID;
	private int typeID1;
	private int typeID2;
	private int typeID3;
	private String imgUrl;
	
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public int getProjectID() {
		return projectID;
	}
	public void setProjectID(int projectID) {
		this.projectID = projectID;
	}
	public int getKnowledgeID() {
		return knowledgeID;
	}
	public void setKnowledgeID(int knowledgeID) {
		this.knowledgeID = knowledgeID;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getCreatedByID() {
		return createdByID;
	}
	public void setCreatedByID(int createdByID) {
		this.createdByID = createdByID;
	}
	public String getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}
	public int getCrntOwnerID() {
		return crntOwnerID;
	}
	public void setCrntOwnerID(int crntOwnerID) {
		this.crntOwnerID = crntOwnerID;
	}
	public String getCrntOwnerName() {
		return crntOwnerName;
	}
	public void setCrntOwnerName(String crntOwnerName) {
		this.crntOwnerName = crntOwnerName;
	}
	public int getItemTypeID() {
		return itemTypeID;
	}
	public void setItemTypeID(int itemTypeID) {
		this.itemTypeID = itemTypeID;
	}
	public int getTypeID1() {
		return typeID1;
	}
	public void setTypeID1(int typeID1) {
		this.typeID1 = typeID1;
	}
	public int getTypeID2() {
		return typeID2;
	}
	public void setTypeID2(int typeID2) {
		this.typeID2 = typeID2;
	}
	public int getTypeID3() {
		return typeID3;
	}
	public void setTypeID3(int typeID3) {
		this.typeID3 = typeID3;
	}
	
	
	

}
