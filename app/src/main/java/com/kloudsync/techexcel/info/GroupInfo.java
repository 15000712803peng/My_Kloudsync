package com.kloudsync.techexcel.info;

import java.io.Serializable;

public class GroupInfo implements Serializable{
	
	private String GroupID;
	private String GroupName;
	private String GroupAdminID;
	private String GroupTempName;
	
	public GroupInfo() {
		super();
	}
	public GroupInfo(String groupID, String groupName, String groupAdminID) {
		super();
		GroupID = groupID;
		GroupName = groupName;
		GroupAdminID = groupAdminID;
	}
	public String getGroupID() {
		return GroupID;
	}
	public void setGroupID(String groupID) {
		GroupID = groupID;
	}
	public String getGroupName() {
		return GroupName;
	}
	public void setGroupName(String groupName) {
		GroupName = groupName;
	}
	public String getGroupAdminID() {
		return GroupAdminID;
	}
	public void setGroupAdminID(String groupAdminID) {
		GroupAdminID = groupAdminID;
	}
	public String getGroupTempName() {
		return GroupTempName;
	}
	public void setGroupTempName(String groupTempName) {
		GroupTempName = groupTempName;
	}
	
	

}
