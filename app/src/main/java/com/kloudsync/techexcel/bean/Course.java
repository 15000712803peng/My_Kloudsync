package com.kloudsync.techexcel.bean;

/**
 * Created by tonyan on 2020/4/14.
 */

public class Course {


    private long CompanyID;
    private long MeetingID;
    private int MeetingType;
    private String Title;
    private String StartDate;
    private String EndDate;
    private int RuleCount;
    private int Duration;// 分钟
    private int LinkedContentID;
    private int TeacherCount;
    private int StudentCount;
    private String TeacherNames;
    private String StudentNames;
    private int ItemsTotal;
    private int ItemsExpired;
    private int ItemsFinished;
    private long NextMeetingID;
    private int NextMeetingIndex;
    private String NextMeetingStartDate;
    private String NextMeetingEndDate;
    private String NextMeetingRealID;
    private int dateType;

    public int getDateType() {
        return dateType;
    }

    public void setDateType(int dateType) {
        this.dateType = dateType;
    }

    public long getCompanyID() {
        return CompanyID;
    }

    public void setCompanyID(long companyID) {
        CompanyID = companyID;
    }

    public long getMeetingID() {
        return MeetingID;
    }

    public void setMeetingID(long meetingID) {
        MeetingID = meetingID;
    }

    public int getMeetingType() {
        return MeetingType;
    }

    public void setMeetingType(int meetingType) {
        MeetingType = meetingType;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getStartDate() {
        return StartDate;
    }

    public void setStartDate(String startDate) {
        StartDate = startDate;
    }

    public String getEndDate() {
        return EndDate;
    }

    public void setEndDate(String endDate) {
        EndDate = endDate;
    }

    public int getRuleCount() {
        return RuleCount;
    }

    public void setRuleCount(int ruleCount) {
        RuleCount = ruleCount;
    }

    public int getDuration() {
        return Duration;
    }

    public void setDuration(int duration) {
        Duration = duration;
    }

    public int getLinkedContentID() {
        return LinkedContentID;
    }

    public void setLinkedContentID(int linkedContentID) {
        LinkedContentID = linkedContentID;
    }

    public int getTeacherCount() {
        return TeacherCount;
    }

    public void setTeacherCount(int teacherCount) {
        TeacherCount = teacherCount;
    }

    public int getStudentCount() {
        return StudentCount;
    }

    public void setStudentCount(int studentCount) {
        StudentCount = studentCount;
    }

    public String getTeacherNames() {
        return TeacherNames;
    }

    public void setTeacherNames(String teacherNames) {
        TeacherNames = teacherNames;
    }

    public String getStudentNames() {
        return StudentNames;
    }

    public void setStudentNames(String studentNames) {
        StudentNames = studentNames;
    }

    public int getItemsTotal() {
        return ItemsTotal;
    }

    public void setItemsTotal(int itemsTotal) {
        ItemsTotal = itemsTotal;
    }

    public int getItemsExpired() {
        return ItemsExpired;
    }

    public void setItemsExpired(int itemsExpired) {
        ItemsExpired = itemsExpired;
    }

    public int getItemsFinished() {
        return ItemsFinished;
    }

    public void setItemsFinished(int itemsFinished) {
        ItemsFinished = itemsFinished;
    }

    public long getNextMeetingID() {
        return NextMeetingID;
    }

    public void setNextMeetingID(long nextMeetingID) {
        NextMeetingID = nextMeetingID;
    }

    public int getNextMeetingIndex() {
        return NextMeetingIndex;
    }

    public void setNextMeetingIndex(int nextMeetingIndex) {
        NextMeetingIndex = nextMeetingIndex;
    }

    public String getNextMeetingStartDate() {
        return NextMeetingStartDate;
    }

    public void setNextMeetingStartDate(String nextMeetingStartDate) {
        NextMeetingStartDate = nextMeetingStartDate;
    }

    public String getNextMeetingEndDate() {
        return NextMeetingEndDate;
    }

    public void setNextMeetingEndDate(String nextMeetingEndDate) {
        NextMeetingEndDate = nextMeetingEndDate;
    }

    public String getNextMeetingRealID() {
        return NextMeetingRealID;
    }

    public void setNextMeetingRealID(String nextMeetingRealID) {
        NextMeetingRealID = nextMeetingRealID;
    }
}
