package com.kloudsync.techexcel.info;

import android.support.annotation.NonNull;

import com.kloudsync.techexcel.bean.CompanySubsystem;
import com.ub.kloudsync.activity.TeamSpaceBean;

import java.util.List;

public class School implements Comparable<School> {
    private int SchoolID;
    private String SchoolName;
    private TeamSpaceBean teamSpaceBean;
    public static int selectedId;
    private List<CompanySubsystem> subsystems;
    private boolean isSelected;
    private boolean isSubSystemSelected;

    public boolean isSubSystemSelected() {
        return isSubSystemSelected;
    }

    public void setSubSystemSelected(boolean subSystemSelected) {
        isSubSystemSelected = subSystemSelected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public List<CompanySubsystem> getSubsystems() {
        return subsystems;
    }

    public void setSubsystems(List<CompanySubsystem> subsystems) {
        this.subsystems = subsystems;
    }

    public School() {
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

    public TeamSpaceBean getTeamSpaceBean() {
        return teamSpaceBean;
    }

    public void setTeamSpaceBean(TeamSpaceBean teamSpaceBean) {
        this.teamSpaceBean = teamSpaceBean;
    }

    @Override
    public int compareTo(@NonNull School school) {
        if (getSchoolID() == selectedId) {
            return 1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "School{" +
                "SchoolID=" + SchoolID +
                ", SchoolName='" + SchoolName + '\'' +
                ", teamSpaceBean=" + teamSpaceBean +
                ", subsystems=" + subsystems +
                ", isSelected=" + isSelected +
                ", isSubSystemSelected=" + isSubSystemSelected +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        School school = (School) o;

        return SchoolID == school.SchoolID;
    }

    @Override
    public int hashCode() {
        return SchoolID;
    }
}
