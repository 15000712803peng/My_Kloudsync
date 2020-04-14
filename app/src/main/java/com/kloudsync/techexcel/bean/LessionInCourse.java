package com.kloudsync.techexcel.bean;

/**
 * Created by tonyan on 2020/4/14.
 */

public class LessionInCourse {

    private int TeacherCount;
    private String TeacherNames;
    private int StudentCount;
    private String StudentNames;
    private int FileCount;
    private long MeetingID;
    private long MeetingItemID;
    private int DisplayIndex;
    private String Title;
    private String StartTime;
    private String EndTime;
    private String Notes;
    private int Status;
    private int ItemType;
    private long RealMeetingID;
    private int RealMeetingStatus;

    public int getTeacherCount() {
        return TeacherCount;
    }

    public void setTeacherCount(int teacherCount) {
        TeacherCount = teacherCount;
    }

    public String getTeacherNames() {
        return TeacherNames;
    }

    public void setTeacherNames(String teacherNames) {
        TeacherNames = teacherNames;
    }

    public int getStudentCount() {
        return StudentCount;
    }

    public void setStudentCount(int studentCount) {
        StudentCount = studentCount;
    }

    public String getStudentNames() {
        return StudentNames;
    }

    public void setStudentNames(String studentNames) {
        StudentNames = studentNames;
    }

    public int getFileCount() {
        return FileCount;
    }

    public void setFileCount(int fileCount) {
        FileCount = fileCount;
    }

    public long getMeetingID() {
        return MeetingID;
    }

    public void setMeetingID(long meetingID) {
        MeetingID = meetingID;
    }

    public long getMeetingItemID() {
        return MeetingItemID;
    }

    public void setMeetingItemID(long meetingItemID) {
        MeetingItemID = meetingItemID;
    }

    public int getDisplayIndex() {
        return DisplayIndex;
    }

    public void setDisplayIndex(int displayIndex) {
        DisplayIndex = displayIndex;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public int getItemType() {
        return ItemType;
    }

    public void setItemType(int itemType) {
        ItemType = itemType;
    }

    public long getRealMeetingID() {
        return RealMeetingID;
    }

    public void setRealMeetingID(long realMeetingID) {
        RealMeetingID = realMeetingID;
    }

    public int getRealMeetingStatus() {
        return RealMeetingStatus;
    }

    public void setRealMeetingStatus(int realMeetingStatus) {
        RealMeetingStatus = realMeetingStatus;
    }
}
