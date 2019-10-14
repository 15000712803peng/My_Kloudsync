package com.kloudsync.techexcel.bean;

public class CompanySubsystem {
    private String companyId;
    private String subSystemId;
    private String subSystemName;
    private int type;
    private String createDate;
    private String integrationUrl;
    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getSubSystemId() {
        return subSystemId;
    }

    public void setSubSystemId(String subSystemId) {
        this.subSystemId = subSystemId;
    }

    public String getSubSystemName() {
        return subSystemName;
    }

    public void setSubSystemName(String subSystemName) {
        this.subSystemName = subSystemName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getIntegrationUrl() {
        return integrationUrl;
    }

    public void setIntegrationUrl(String integrationUrl) {
        this.integrationUrl = integrationUrl;
    }

    @Override
    public String toString() {
        return "CompanySubsystem{" +
                "companyId='" + companyId + '\'' +
                ", subSystemId='" + subSystemId + '\'' +
                ", subSystemName='" + subSystemName + '\'' +
                ", type=" + type +
                ", createDate='" + createDate + '\'' +
                ", integrationUrl='" + integrationUrl + '\'' +
                ", isSelected=" + isSelected +
                '}';
    }
}
