package com.kloudsync.techexcel.bean;

public class UserInCompany {
    String companyID;
    String UserID;
    String LoginName;
    String Nickname;
    String UserName;
    String FirstName;
    String MiddleName;
    String LastName;
    String AvatarUrl;
    int Gender;
    String Phone;
    String Email;
    String BirthDate;
    String Grade;
    String SchoolName;
    int Role;
    String ContactPrivilege;
    String CoursePrivilege;
    RoleInTeam roleInTeam;
    long rondom;

    public long getRondom() {
        return rondom;
    }

    public void setRondom(long rondom) {
        this.rondom = rondom;
    }

    public RoleInTeam getRoleInTeam() {
        return roleInTeam;
    }

    public void setRoleInTeam(int role) {
        RoleInTeam roleInTeam = new RoleInTeam();
        roleInTeam.setTeamRole(role);
        this.roleInTeam = roleInTeam;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getLoginName() {
        return LoginName;
    }

    public void setLoginName(String loginName) {
        LoginName = loginName;
    }

    public String getNickname() {
        return Nickname;
    }

    public void setNickname(String nickname) {
        Nickname = nickname;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getMiddleName() {
        return MiddleName;
    }

    public void setMiddleName(String middleName) {
        MiddleName = middleName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getAvatarUrl() {
        return AvatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        AvatarUrl = avatarUrl;
    }

    public int getGender() {
        return Gender;
    }

    public void setGender(int gender) {
        Gender = gender;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getBirthDate() {
        return BirthDate;
    }

    public void setBirthDate(String birthDate) {
        BirthDate = birthDate;
    }

    public String getGrade() {
        return Grade;
    }

    public void setGrade(String grade) {
        Grade = grade;
    }

    public String getSchoolName() {
        return SchoolName;
    }

    public void setSchoolName(String schoolName) {
        SchoolName = schoolName;
    }

    public int getRole() {
        return Role;
    }

    public void setRole(int role) {
        Role = role;
    }

    public String getContactPrivilege() {
        return ContactPrivilege;
    }

    public void setContactPrivilege(String contactPrivilege) {
        ContactPrivilege = contactPrivilege;
    }

    public String getCoursePrivilege() {
        return CoursePrivilege;
    }

    public void setCoursePrivilege(String coursePrivilege) {
        CoursePrivilege = coursePrivilege;
    }

    public String getCompanyID() {
        return companyID;
    }

    public void setCompanyID(String companyID) {
        this.companyID = companyID;
    }
}
