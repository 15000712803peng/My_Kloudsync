package com.kloudsync.techexcel.bean;

public class PersonalInfo {
	private int fieldID;
	private int type;
	private String label;
	private String value;

	public int getFieldID() {
		return fieldID;
	}

	public void setFieldID(int fieldID) {
		this.fieldID = fieldID;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public PersonalInfo(int fieldID, int type, String label, String value) {
		super();
		this.fieldID = fieldID;
		this.type = type;
		this.label = label;
		this.value = value;
	}

	public PersonalInfo() {
		super();
	}

}
