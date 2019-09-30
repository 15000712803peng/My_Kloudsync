package com.ub.techexcel.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.kloudsync.techexcel.info.Customer;

public class ServiceBean implements Serializable {

	// private int serviceId;
	private static final long serialVersionUID = 0x111;
	private String name; // 服务名
	private String serviceStartTime;
	private int categoryID; // 大类
	private int subCategoryID; // 小类

	private int concernID; // 关注点
	private String concernName;  // 关注点

	private int linkedSolutionID; // 选择的解決方案
	private String linkedSolutionName;

	private int statusID;
	private String statusValue;

	private int ifClose = 0;
	private int id;

	private boolean isFinished;

	private Customer customer = new Customer();

	private String comment; // 备注
	private String description;

	private String customerRongCloudId;

	private String userId,teacherId;
	private String userName,teacherName;
	private String userUrl,teacherUrl;

	private String planedStartDate,planedEndDate;
	private int dateType;
	private int mins;

	private boolean isShow;


	private int teacherCount,studentCount;

	public int getTeacherCount() {
		return teacherCount;
	}

	public void setTeacherCount(int teacherCount) {
		this.teacherCount = teacherCount;
	}

	public int getStudentCount() {
		return studentCount;
	}

	public void setStudentCount(int studentCount) {
		this.studentCount = studentCount;
	}

	public boolean isFinished() {
		return isFinished;
	}

	public void setFinished(boolean finished) {
		isFinished = finished;
	}

	private String courseName;

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public boolean isShow() {
		return isShow;
	}

	public void setShow(boolean show) {
		isShow = show;
	}

	public int getMins() {
		return mins;
	}

	public void setMins(int mins) {
		this.mins = mins;
	}

	public int getDateType() {
		return dateType;
	}

	public void setDateType(int dateType) {
		this.dateType = dateType;
	}

	public String getPlanedStartDate() {
		return planedStartDate;
	}

	public void setPlanedStartDate(String planedStartDate) {
		this.planedStartDate = planedStartDate;
	}

	public String getPlanedEndDate() {
		return planedEndDate;
	}

	public void setPlanedEndDate(String planedEndDate) {
		this.planedEndDate = planedEndDate;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getTeacherName() {
		return teacherName;
	}

	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}

	public String getUserUrl() {
		return userUrl;
	}

	public void setUserUrl(String userUrl) {
		this.userUrl = userUrl;
	}

	public String getTeacherUrl() {
		return teacherUrl;
	}

	public void setTeacherUrl(String teacherUrl) {
		this.teacherUrl = teacherUrl;
	}

	private int roleinlesson;

	public int getRoleinlesson() {
		return roleinlesson;
	}

	public void setRoleinlesson(int roleinlesson) {
		this.roleinlesson = roleinlesson;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(String teacherId) {
		this.teacherId = teacherId;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getCustomerRongCloudId() {
		return customerRongCloudId;
	}

	public void setCustomerRongCloudId(String customerRongCloudId) {
		this.customerRongCloudId = customerRongCloudId;
	}

	/**
	 * 方案具体信息
	 */
	private List<LineItem> lineItems = new ArrayList<LineItem>(); // 方案

	private List<String> picAddress = new ArrayList<String>();

	private boolean isSelect = false;

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(int categoryID) {
		this.categoryID = categoryID;
	}

	public int getLinkedSolutionID() {
		return linkedSolutionID;
	}

	public void setLinkedSolutionID(int linkedSolutionID) {
		this.linkedSolutionID = linkedSolutionID;
	}

	public String getLinkedSolutionName() {
		return linkedSolutionName;
	}

	public void setLinkedSolutionName(String linkedSolutionName) {
		this.linkedSolutionName = linkedSolutionName;
	}

	public int getSubCategoryID() {
		return subCategoryID;
	}

	public void setSubCategoryID(int subCategoryID) {
		this.subCategoryID = subCategoryID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getConcernID() {
		return concernID;
	}

	public void setConcernID(int concernID) {
		this.concernID = concernID;
	}

	public String getConcernName() {
		return concernName;
	}

	public void setConcernName(String concernName) {
		this.concernName = concernName;
	}

	public void setServiceStartTime(String serviceStartTime) {
		this.serviceStartTime = serviceStartTime;
	}

	public String getServiceStartTime() {
		return serviceStartTime;
	}

	public int getStatusID() {
		return statusID;
	}

	public void setStatusID(int statusID) {
		this.statusID = statusID;
	}

	public String getStatusValue() {
		return statusValue;
	}

	public void setStatusValue(String statusValue) {
		this.statusValue = statusValue;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public int getIfClose() {
		return ifClose;
	}

	public void setIfClose(int ifClose) {
		this.ifClose = ifClose;
	}

	public List<LineItem> getLineItems() {
		return lineItems;
	}

	public void setLineItems(List<LineItem> lineItems) {
		this.lineItems = lineItems;
	}

	public List<String> getPicAddress() {
		return picAddress;
	}

	public void setPicAddress(List<String> picAddress) {
		this.picAddress = picAddress;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
