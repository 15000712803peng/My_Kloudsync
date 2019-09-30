package com.kloudsync.user.techexcel.pi.tools;

public class ProvinceBean {
	private int ID;
	private String Name;

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

	public ProvinceBean(int iD, String name) {
		super();
		ID = iD;
		Name = name;
	}

	public ProvinceBean() {
		super();
	}
}
