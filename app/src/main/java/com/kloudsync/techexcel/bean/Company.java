package com.kloudsync.techexcel.bean;

public class Company {
    private int CompanyID;
    private String CompanyName;
    private String CompanyLogoUrl;
    private boolean isSelected;

    public int getCompanyID() {
        return CompanyID;
    }

    public void setCompanyID(int companyID) {
        CompanyID = companyID;
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(String companyName) {
        CompanyName = companyName;
    }

    public String getCompanyLogoUrl() {
        return CompanyLogoUrl;
    }

    public void setCompanyLogoUrl(String companyLogoUrl) {
        CompanyLogoUrl = companyLogoUrl;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
