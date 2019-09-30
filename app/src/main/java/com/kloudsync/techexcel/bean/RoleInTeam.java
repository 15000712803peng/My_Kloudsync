package com.kloudsync.techexcel.bean;

public class RoleInTeam {
    public static final int ROLE_MEMBER = 0;
    public static final int ROLE_OWENER = 1;
    public static final int ROLE_ADMIN = 2;
    private int teamRole;

    public int getTeamRole() {
        return teamRole;
    }

    public void setTeamRole(int teamRole) {
        this.teamRole = teamRole;
    }
}
