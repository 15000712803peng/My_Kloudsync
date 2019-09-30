package com.kloudsync.techexcel.pc.bean;

public class IntegralDetails {
	private String changeType;
	private String changeTypeName;
	private String pointValue;
	private String changeDate;
	private String changeLog;
	public String getChangeType() {
		return changeType;
	}
	public void setChangeType(String changeType) {
		this.changeType = changeType;
	}
	public String getChangeTypeName() {
		return changeTypeName;
	}
	public void setChangeTypeName(String changeTypeName) {
		this.changeTypeName = changeTypeName;
	}
	public String getPointValue() {
		return pointValue;
	}
	public void setPointValue(String pointValue) {
		this.pointValue = pointValue;
	}
	public String getChangeDate() {
		return changeDate;
	}
	public void setChangeDate(String changeDate) {
		this.changeDate = changeDate;
	}
	public String getChangeLog() {
		return changeLog;
	}
	public void setChangeLog(String changeLog) {
		this.changeLog = changeLog;
	}
	public IntegralDetails(String changeType, String changeTypeName,
			String pointValue, String changeDate, String changeLog) {
		super();
		this.changeType = changeType;
		this.changeTypeName = changeTypeName;
		this.pointValue = pointValue;
		this.changeDate = changeDate;
		this.changeLog = changeLog;
	}
	public IntegralDetails() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
