package com.kloudsync.user.techexcel.pi.tools;

public class MemberBean {
	private String UserID;
	private String RongCloudUserID;
	private String name;
	private String sex;// 性别
	private String Birthday;
	private String Phone;
	private String State;
	private String City;
	private String Address;
	private String AvatarUrl;
	private String MemberPoints;
	private String ExpirationDate;
	private String SkilledFields;
	private String Summary;
	private String ArticleCount;

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

	public String getMemberPoints() {
		return MemberPoints;
	}

	public void setMemberPoints(String memberPoints) {
		MemberPoints = memberPoints;
	}

	public String getExpirationDate() {
		return ExpirationDate;
	}

	public void setExpirationDate(String expirationDate) {
		ExpirationDate = expirationDate;
	}

	public String getSkilledFields() {
		return SkilledFields;
	}

	public void setSkilledFields(String skilledFields) {
		SkilledFields = skilledFields;
	}

	public String getSummary() {
		return Summary;
	}

	public void setSummary(String summary) {
		Summary = summary;
	}

	public String getArticleCount() {
		return ArticleCount;
	}

	public void setArticleCount(String articleCount) {
		ArticleCount = articleCount;
	}

	public MemberBean(String userID, String rongCloudUserID, String name,
			String sex, String birthday, String phone, String state,
			String city, String address, String avatarUrl, String memberPoints,
			String expirationDate, String skilledFields, String summary,
			String articleCount) {
		super();
		UserID = userID;
		RongCloudUserID = rongCloudUserID;
		this.name = name;
		this.sex = sex;
		Birthday = birthday;
		Phone = phone;
		State = state;
		City = city;
		Address = address;
		AvatarUrl = avatarUrl;
		MemberPoints = memberPoints;
		ExpirationDate = expirationDate;
		SkilledFields = skilledFields;
		Summary = summary;
		ArticleCount = articleCount;
	}

	public MemberBean() {
		super();
	}
}
