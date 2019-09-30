package com.kloudsync.techexcel.info;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Knowledge implements Serializable{
	
	private String IssueTitle;
	private String Description;
	private String VideoInfo;
	private int ProjectID;
	private int KnowledgeID;
	private int ItemTypeID;
	private int TypeID1;
	private int TypeID2;
	private int TypeID3;
	private int LinkOption;
	private int ImageID;
	
	public Knowledge() {
		super();
	}
	
	public Knowledge(String issueTitle,  int projectID,
			int knowledgeID, int itemTypeID, int typeID1,
			int typeID2, int typeID3) {
		super();
		IssueTitle = issueTitle;
		ProjectID = projectID;
		KnowledgeID = knowledgeID;
		ItemTypeID = itemTypeID;
		TypeID1 = typeID1;
		TypeID2 = typeID2;
		TypeID3 = typeID3;
	}


	public String getVideoInfo() {
		return VideoInfo;
	}

	public void setVideoInfo(String videoInfo) {
		VideoInfo = videoInfo;
	}

	public int getLinkOption() {
		return LinkOption;
	}

	public void setLinkOption(int linkOption) {
		LinkOption = linkOption;
	}

	public String getIssueTitle() {
		return IssueTitle;
	}
	public void setIssueTitle(String issueTitle) {
		IssueTitle = issueTitle;
	}
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		Description = description;
	}
	public int getProjectID() {
		return ProjectID;
	}
	public void setProjectID(int projectID) {
		ProjectID = projectID;
	}
	public int getKnowledgeID() {
		return KnowledgeID;
	}
	public void setKnowledgeID(int knowledgeID) {
		KnowledgeID = knowledgeID;
	}
	public int getItemTypeID() {
		return ItemTypeID;
	}
	public void setItemTypeID(int itemTypeID) {
		ItemTypeID = itemTypeID;
	}
	public int getTypeID1() {
		return TypeID1;
	}
	public void setTypeID1(int typeID1) {
		TypeID1 = typeID1;
	}
	public int getTypeID2() {
		return TypeID2;
	}
	public void setTypeID2(int typeID2) {
		TypeID2 = typeID2;
	}
	public int getTypeID3() {
		return TypeID3;
	}
	public void setTypeID3(int typeID3) {
		TypeID3 = typeID3;
	}

	public int getImageID() {
		return ImageID;
	}

	public void setImageID(int imageID) {
		ImageID = imageID;
	}
	
	
	

}
