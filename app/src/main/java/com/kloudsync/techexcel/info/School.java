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
}
