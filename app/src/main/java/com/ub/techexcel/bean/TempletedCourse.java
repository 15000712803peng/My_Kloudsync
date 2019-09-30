package com.ub.techexcel.bean;

/**
 * Created by wang on 2018/5/23.
 */

public class TempletedCourse {

    private String title;
    private String teacherName;
    private int authorLessonCount;
    private int authorLessonCost;
    private int authorCost;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public int getAuthorLessonCount() {
        return authorLessonCount;
    }

    public void setAuthorLessonCount(int authorLessonCount) {
        this.authorLessonCount = authorLessonCount;
    }

    public int getAuthorLessonCost() {
        return authorLessonCost;
    }

    public void setAuthorLessonCost(int authorLessonCost) {
        this.authorLessonCost = authorLessonCost;
    }

    public int getAuthorCost() {
        return authorCost;
    }

    public void setAuthorCost(int authorCost) {
        this.authorCost = authorCost;
    }
}
