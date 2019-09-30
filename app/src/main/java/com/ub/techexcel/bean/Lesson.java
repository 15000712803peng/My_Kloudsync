package com.ub.techexcel.bean;

import java.util.List;

/**
 * Created by wang on 2018/2/8.
 */

public class Lesson {
    private int lessonId;
    private String title;
    private String description;
    private List<CourseLesson> lectures;
    private long startData;
    private  long endData;


    public int getLessonId() {
        return lessonId;
    }

    public void setLessonId(int lessonId) {
        this.lessonId = lessonId;
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

    public List<CourseLesson> getLectures() {
        return lectures;
    }

    public void setLectures(List<CourseLesson> lectures) {
        this.lectures = lectures;
    }

    public long getStartData() {
        return startData;
    }

    public void setStartData(long startData) {
        this.startData = startData;
    }

    public long getEndData() {
        return endData;
    }

    public void setEndData(long endData) {
        this.endData = endData;
    }
}
