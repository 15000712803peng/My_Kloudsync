package com.kloudsync.user.techexcel.pi.tools;

public class TaskBean {
	private String TaskCount;
	private String ID;
	private String Name;
	private String IconURL;
	private String TaskType;
	private String TaskID;
	private String Triggers;
	private String TaskIfDoneToday;
	private String Description;

	public String getTaskCount() {
		return TaskCount;
	}

	public void setTaskCount(String taskCount) {
		TaskCount = taskCount;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getIconURL() {
		return IconURL;
	}

	public void setIconURL(String iconURL) {
		IconURL = iconURL;
	}

	public String getTaskType() {
		return TaskType;
	}

	public void setTaskType(String taskType) {
		TaskType = taskType;
	}

	public String getTaskID() {
		return TaskID;
	}

	public void setTaskID(String taskID) {
		TaskID = taskID;
	}

	public String getTriggers() {
		return Triggers;
	}

	public void setTriggers(String triggers) {
		Triggers = triggers;
	}

	public String getTaskIfDoneToday() {
		return TaskIfDoneToday;
	}

	public void setTaskIfDoneToday(String taskIfDoneToday) {
		TaskIfDoneToday = taskIfDoneToday;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public TaskBean(String taskCount, String iD, String name, String iconURL,
			String taskType, String taskID, String triggers,
			String taskIfDoneToday, String description) {
		super();
		TaskCount = taskCount;
		ID = iD;
		Name = name;
		IconURL = iconURL;
		TaskType = taskType;
		TaskID = taskID;
		Triggers = triggers;
		TaskIfDoneToday = taskIfDoneToday;
		Description = description;
	}

	public TaskBean() {
		super();
	}
}
