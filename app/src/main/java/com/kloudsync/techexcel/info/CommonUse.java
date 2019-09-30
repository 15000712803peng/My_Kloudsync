package com.kloudsync.techexcel.info;

import java.io.Serializable;

public class CommonUse implements Serializable{
	
	private int NodeType;
	private int[] ChildSelections;
	private int ID;
	private String Name;
	
	public CommonUse() {
		super();
	}
	public CommonUse(int nodeType, int iD, String name) {
		super();
		NodeType = nodeType;
		ID = iD;
		Name = name;
	}
	public int getNodeType() {
		return NodeType;
	}
	public void setNodeType(int nodeType) {
		NodeType = nodeType;
	}
	public int[] getChildSelections() {
		return ChildSelections;
	}
	public void setChildSelections(int[] childSelections) {
		ChildSelections = childSelections;
	}
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}

	
}
