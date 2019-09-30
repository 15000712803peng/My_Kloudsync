package com.kloudsync.techexcel.pc.bean;

public class PublishedArticleDetails {
	private String AuthorUserID;
	private String AuthorName;
	private String AuthorMobile;
	private String PublishDate;
	private String ProjectID;
	private String KnowledgeID;
	private String IssueTitle;	//标题
	private String Description;  //详情
	private String TypeID1;
	private String TypeID2;
	private String TypeID3;
	private String ImageID;
	private String VideoInfo;
	public String getAuthorUserID() {
		return AuthorUserID;
	}
	public void setAuthorUserID(String authorUserID) {
		AuthorUserID = authorUserID;
	}
	public String getAuthorName() {
		return AuthorName;
	}
	public void setAuthorName(String authorName) {
		AuthorName = authorName;
	}
	public String getAuthorMobile() {
		return AuthorMobile;
	}
	public void setAuthorMobile(String authorMobile) {
		AuthorMobile = authorMobile;
	}
	public String getPublishDate() {
		return PublishDate;
	}
	public void setPublishDate(String publishDate) {
		PublishDate = publishDate;
	}
	public String getProjectID() {
		return ProjectID;
	}
	public void setProjectID(String projectID) {
		ProjectID = projectID;
	}
	public String getKnowledgeID() {
		return KnowledgeID;
	}
	public void setKnowledgeID(String knowledgeID) {
		KnowledgeID = knowledgeID;
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
	public String getTypeID1() {
		return TypeID1;
	}
	public void setTypeID1(String typeID1) {
		TypeID1 = typeID1;
	}
	public String getTypeID2() {
		return TypeID2;
	}
	public void setTypeID2(String typeID2) {
		TypeID2 = typeID2;
	}
	public String getTypeID3() {
		return TypeID3;
	}
	public void setTypeID3(String typeID3) {
		TypeID3 = typeID3;
	}
	public String getImageID() {
		return ImageID;
	}
	public void setImageID(String imageID) {
		ImageID = imageID;
	}
	public String getVideoInfo() {
		return VideoInfo;
	}
	public void setVideoInfo(String videoInfo) {
		VideoInfo = videoInfo;
	}
	public PublishedArticleDetails(String authorUserID, String authorName,
			String authorMobile, String publishDate, String projectID,
			String knowledgeID, String issueTitle, String description,
			String typeID1, String typeID2, String typeID3, String imageID,
			String videoInfo) {
		super();
		AuthorUserID = authorUserID;
		AuthorName = authorName;
		AuthorMobile = authorMobile;
		PublishDate = publishDate;
		ProjectID = projectID;
		KnowledgeID = knowledgeID;
		IssueTitle = issueTitle;
		Description = description;
		TypeID1 = typeID1;
		TypeID2 = typeID2;
		TypeID3 = typeID3;
		ImageID = imageID;
		VideoInfo = videoInfo;
	}
	public PublishedArticleDetails() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
