package com.kloudsync.techexcel.info;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Customer implements Serializable {

    private String UserID;
    private String UBAOUserID;
    private String name;
    private String sex;//性别
    private String Summary;//Summary
    private String phone;
    private String currentPosition;//CurrentPosition
    private String sortLetters;
    private String distance;
    private String symptom;
    private String title;
    private String age;
    private String address;
    private String Birthday;
    private String url;
    private String City;
    private String State;
    private String UBAOPersonName;
    private String ExpirationDate;
    private String SelfDescription;
    private String PersonalComment;
    private String SkilledFields;
    private String usertoken;
    private String height;
    private String weight;
    private String schoolname;
    private List<String> FocusPoints;//FocusPoints
    private ArrayList<CommonUse> HealthConcerns;//关注点
    private boolean isCrown;
    private boolean isNew;
    private boolean isSelected;//是否选择
    private boolean hasSelected;//已被选择
    private int serviceCount;//服务次数ServiceCount
    private int VIPLevel;//等级
    private int Role;//区分用户和会员
    private int Type;//区分优葆人和普通用户
    private int UBAOPersonID;
    private int ArticleCount;//文章数量
    private double latitude;//纬度
    private double longitude;//经度
    private boolean isPresenter;

    private boolean isEnterMeeting;
    private boolean isOnline;
    private boolean isTeam;//是否teamspace
    private boolean EnableChat;//是否teamspace

    private Space space;
    private ArrayList<Space> SpaceList;


    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public boolean isEnterMeeting() {
        return isEnterMeeting;
    }

    public void setEnterMeeting(boolean enterMeeting) {
        isEnterMeeting = enterMeeting;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Customer) {
            Customer question = (Customer) o;
            return this.UserID.equals(question.UserID);
        }
        return super.equals(o);
    }

    public String getUsertoken() {
        return usertoken;
    }

    public void setUsertoken(String usertoken) {
        this.usertoken = usertoken;
    }

    public boolean isPresenter() {
        return isPresenter;
    }

    public void setPresenter(boolean presenter) {
        isPresenter = presenter;
    }

    public Customer() {
        super();
    }

    public Customer(String userID, String uBAOUserID, String name, String sex,
                    String currentPosition, String sortLetters, String age) {
        super();
        UserID = userID;
        UBAOUserID = uBAOUserID;
        this.name = name;
        this.sex = sex;
        this.currentPosition = currentPosition;
        this.sortLetters = sortLetters;
        this.age = age;
    }

    public boolean isEnableChat() {
        return EnableChat;
    }

    public void setEnableChat(boolean enableChat) {
        EnableChat = enableChat;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public int getRole() {
        return Role;
    }

    public void setRole(int role) {
        Role = role;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthday() {
        return Birthday;
    }

    public void setBirthday(String birthday) {
        Birthday = birthday;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUBAOUserID() {
        return UBAOUserID;
    }

    public void setUBAOUserID(String uBAOUserID) {
        UBAOUserID = uBAOUserID;
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

    public String getSummary() {
        return Summary;
    }

    public void setSummary(String summary) {
        Summary = summary;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(String currentPosition) {
        this.currentPosition = currentPosition;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getSymptom() {
        return symptom;
    }

    public void setSymptom(String symptom) {
        this.symptom = symptom;
    }

    public List<String> getFocusPoints() {
        return FocusPoints;
    }

    public void setFocusPoints(List<String> focusPoints) {
        FocusPoints = focusPoints;
    }

    public boolean isCrown() {
        return isCrown;
    }

    public void setCrown(boolean isCrown) {
        this.isCrown = isCrown;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public int getServiceCount() {
        return serviceCount;
    }

    public void setServiceCount(int serviceCount) {
        this.serviceCount = serviceCount;
    }

    public int getVIPLevel() {
        return VIPLevel;
    }

    public void setVIPLevel(int vIPLevel) {
        VIPLevel = vIPLevel;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public boolean isHasSelected() {
        return hasSelected;
    }

    public void setHasSelected(boolean hasSelected) {
        this.hasSelected = hasSelected;
    }

    public String getUBAOPersonName() {
        return UBAOPersonName;
    }

    public void setUBAOPersonName(String uBAOPersonName) {
        UBAOPersonName = uBAOPersonName;
    }

    public int getUBAOPersonID() {
        return UBAOPersonID;
    }

    public void setUBAOPersonID(int uBAOPersonID) {
        UBAOPersonID = uBAOPersonID;
    }

    public String getExpirationDate() {
        return ExpirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        ExpirationDate = expirationDate;
    }

    public ArrayList<CommonUse> getHealthConcerns() {
        return HealthConcerns;
    }

    public void setHealthConcerns(ArrayList<CommonUse> healthConcerns) {
        HealthConcerns = healthConcerns;
    }

    public String getSelfDescription() {
        return SelfDescription;
    }

    public void setSelfDescription(String selfDescription) {
        SelfDescription = selfDescription;
    }

    public String getPersonalComment() {
        return PersonalComment;
    }

    public void setPersonalComment(String personalComment) {
        PersonalComment = personalComment;
    }

    public String getSkilledFields() {
        return SkilledFields;
    }

    public void setSkilledFields(String skilledFields) {
        SkilledFields = skilledFields;
    }

    public int getArticleCount() {
        return ArticleCount;
    }

    public void setArticleCount(int articleCount) {
        ArticleCount = articleCount;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getSchoolname() {
        return schoolname;
    }

    public void setSchoolname(String schoolname) {
        this.schoolname = schoolname;
    }

    public boolean isTeam() {
        return isTeam;
    }

    public void setTeam(boolean team) {
        isTeam = team;
    }

    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    public ArrayList<Space> getSpaceList() {
        return SpaceList;
    }

    public void setSpaceList(ArrayList<Space> spaceList) {
        SpaceList = spaceList;
    }

    /**
     * 雄峰 汪
     */
    private boolean isMember;//是否会员


    public boolean isMember() {
        return isMember;
    }

    public void setMember(boolean isMember) {
        this.isMember = isMember;
    }


}
