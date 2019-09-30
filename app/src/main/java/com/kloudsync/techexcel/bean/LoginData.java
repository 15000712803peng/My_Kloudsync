package com.kloudsync.techexcel.bean;

public class LoginData {
    private String ExpirationDate;
    private String ClassRoomID;
    private String UserToken;
    private long UserID;
    private String Name;
    private String Mobile;
    private int Role;
    private long CustomerID;
    private long SchoolID;
    private int SchoolRole;
    private int SchoolCategory1;
    private int SchoolCategory2;
    private int ContactPrivilege;
    private int CoursePrivilege;
    private boolean WithSchool;
    private long KloudCallUserID;
    private boolean IsAdmin;
    private String EncryptUserID;

    public String getExpirationDate() {
        return ExpirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        ExpirationDate = expirationDate;
    }

    public String getClassRoomID() {
        return ClassRoomID;
    }

    public void setClassRoomID(String classRoomID) {
        ClassRoomID = classRoomID;
    }

    public String getUserToken() {
        return UserToken;
    }

    public void setUserToken(String userToken) {
        UserToken = userToken;
    }

    public long getUserID() {
        return UserID;
    }

    public void setUserID(long userID) {
        UserID = userID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public int getRole() {
        return Role;
    }

    public void setRole(int role) {
        Role = role;
    }

    public long getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(long customerID) {
        CustomerID = customerID;
    }

    public long getSchoolID() {
        return SchoolID;
    }

    public void setSchoolID(long schoolID) {
        SchoolID = schoolID;
    }

    public int getSchoolRole() {
        return SchoolRole;
    }

    public void setSchoolRole(int schoolRole) {
        SchoolRole = schoolRole;
    }

    public int getSchoolCategory1() {
        return SchoolCategory1;
    }

    public void setSchoolCategory1(int schoolCategory1) {
        SchoolCategory1 = schoolCategory1;
    }

    public int getSchoolCategory2() {
        return SchoolCategory2;
    }

    public void setSchoolCategory2(int schoolCategory2) {
        SchoolCategory2 = schoolCategory2;
    }

    public int getContactPrivilege() {
        return ContactPrivilege;
    }

    public void setContactPrivilege(int contactPrivilege) {
        ContactPrivilege = contactPrivilege;
    }

    public int getCoursePrivilege() {
        return CoursePrivilege;
    }

    public void setCoursePrivilege(int coursePrivilege) {
        CoursePrivilege = coursePrivilege;
    }

    public boolean isWithSchool() {
        return WithSchool;
    }

    public void setWithSchool(boolean withSchool) {
        WithSchool = withSchool;
    }

    public long getKloudCallUserID() {
        return KloudCallUserID;
    }

    public void setKloudCallUserID(long kloudCallUserID) {
        KloudCallUserID = kloudCallUserID;
    }

    public boolean isAdmin() {
        return IsAdmin;
    }

    public void setAdmin(boolean admin) {
        IsAdmin = admin;
    }

    public String getEncryptUserID() {
        return EncryptUserID;
    }

    public void setEncryptUserID(String encryptUserID) {
        EncryptUserID = encryptUserID;
    }
}
