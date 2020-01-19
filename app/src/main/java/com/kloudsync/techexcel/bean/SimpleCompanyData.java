package com.kloudsync.techexcel.bean;

/**
 * Created by tonyan on 2020/1/19.
 */

public class SimpleCompanyData {

    private int Role;
    private String Slogan;
    private String MainPictureUrl;
    private int OwnerID;
    private String OwnerName;
    private int IsMySchool;
    private int SchoolID;
    private String SchoolName;
    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getRole() {
        return Role;
    }

    public void setRole(int role) {
        Role = role;
    }

    public String getSlogan() {
        return Slogan;
    }

    public void setSlogan(String slogan) {
        Slogan = slogan;
    }

    public String getMainPictureUrl() {
        return MainPictureUrl;
    }

    public void setMainPictureUrl(String mainPictureUrl) {
        MainPictureUrl = mainPictureUrl;
    }

    public int getOwnerID() {
        return OwnerID;
    }

    public void setOwnerID(int ownerID) {
        OwnerID = ownerID;
    }

    public String getOwnerName() {
        return OwnerName;
    }

    public void setOwnerName(String ownerName) {
        OwnerName = ownerName;
    }

    public int getIsMySchool() {
        return IsMySchool;
    }

    public void setIsMySchool(int isMySchool) {
        IsMySchool = isMySchool;
    }

    public int getSchoolID() {
        return SchoolID;
    }

    public void setSchoolID(int schoolID) {
        SchoolID = schoolID;
    }

    public String getSchoolName() {
        return SchoolName;
    }

    public void setSchoolName(String schoolName) {
        SchoolName = schoolName;
    }
}
