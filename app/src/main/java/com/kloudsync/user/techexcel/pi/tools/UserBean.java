package com.kloudsync.user.techexcel.pi.tools;

public class UserBean {
	private String UserID;
	private String RongCloudUserID;
	private String name;
	private String sex;// 性别
	private String Birthday;
	private String Age;
	private String Height;
	private String Weight;
	private String Phone;
	private String State;
	private String City;
	private String Address;
	private String AvatarUrl;

	public UserBean() {
		super();
	}

	public UserBean(String userID, String rongCloudUserID, String name,
			String sex, String birthday, String age, String height,
			String weight, String phone, String state, String city,
			String address, String avatarUrl) {
		super();
		UserID = userID;
		RongCloudUserID = rongCloudUserID;
		this.name = name;
		this.sex = sex;
		Birthday = birthday;
		Age = age;
		Height = height;
		Weight = weight;
		Phone = phone;
		State = state;
		City = city;
		Address = address;
		AvatarUrl = avatarUrl;
	}

	public String getUserID() {
		return UserID;
	}

	public void setUserID(String userID) {
		UserID = userID;
	}

	public String getRongCloudUserID() {
		return RongCloudUserID;
	}

	public void setRongCloudUserID(String rongCloudUserID) {
		RongCloudUserID = rongCloudUserID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getBirthday() {
		return Birthday;
	}

	public void setBirthday(String birthday) {
		Birthday = birthday;
	}

	public String getAge() {
		return Age;
	}

	public void setAge(String age) {
		Age = age;
	}

	public String getHeight() {
		return Height;
	}

	public void setHeight(String height) {
		Height = height;
	}

	public String getWeight() {
		return Weight;
	}

	public void setWeight(String weight) {
		Weight = weight;
	}

	public String getPhone() {
		return Phone;
	}

	public void setPhone(String phone) {
		Phone = phone;
	}

	public String getState() {
		return State;
	}

	public void setState(String state) {
		State = state;
	}

	public String getCity() {
		return City;
	}

	public void setCity(String city) {
		City = city;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}

	public String getAvatarUrl() {
		return AvatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		AvatarUrl = avatarUrl;
	}
}
