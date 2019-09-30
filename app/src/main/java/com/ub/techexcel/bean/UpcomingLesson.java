package com.ub.techexcel.bean;

/**
 * Created by wang on 2018/1/3.
 */

public class UpcomingLesson {

    private String lessonID;
    private String title;
    private String description;
    private String startDate;
    private String endDate;
    private String teacherID;
    private String studentID;
    private String courseID;
    private String courseName;
    private String lectureIDs;
    private int  isInClassroom;
    private int  isOnGoing;


    public int getIsInClassroom() {
        return isInClassroom;
    }

    public void setIsInClassroom(int isInClassroom) {
        this.isInClassroom = isInClassroom;
    }

    public int getIsOnGoing() {
        return isOnGoing;
    }

    public void setIsOnGoing(int isOnGoing) {
        this.isOnGoing = isOnGoing;
    }

    public String getLessonID() {
        return lessonID;
    }

    public void setLessonID(String lessonID) {
        this.lessonID = lessonID;
    }

    public String getLectureIDs() {
        return lectureIDs;
    }

    public void setLectureIDs(String lectureIDs) {
        this.lectureIDs = lectureIDs;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}
