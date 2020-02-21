package com.kloudsync.techexcel.bean;

/**
 * Created by tonyan on 2020/2/21.
 */

public class SearchContactInfo {
    private long UserID;
    private String UserName;
    private String Phone;
    private long RongCloudID;
    private String AvatarUrl;
    private int RoleInLesson;
    private int DeviceType;
    private String DeviceName;
    private String ClassroomID;
    private String AppID;

    public long getUserID() {
        return UserID;
    }

    public void setUserID(long userID) {
        UserID = userID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public long getRongCloudID() {
        return RongCloudID;
    }

    public void setRongCloudID(long rongCloudID) {
        RongCloudID = rongCloudID;
    }

    public String getAvatarUrl() {
        return AvatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        AvatarUrl = avatarUrl;
    }

    public int getRoleInLesson() {
        return RoleInLesson;
    }

    public void setRoleInLesson(int roleInLesson) {
        RoleInLesson = roleInLesson;
    }

    public int getDeviceType() {
        return DeviceType;
    }

    public void setDeviceType(int deviceType) {
        DeviceType = deviceType;
    }

    public String getDeviceName() {
        return DeviceName;
    }

    public void setDeviceName(String deviceName) {
        DeviceName = deviceName;
    }

    public String getClassroomID() {
        return ClassroomID;
    }

    public void setClassroomID(String classroomID) {
        ClassroomID = classroomID;
    }

    public String getAppID() {
        return AppID;
    }

    public void setAppID(String appID) {
        AppID = appID;
    }
}
