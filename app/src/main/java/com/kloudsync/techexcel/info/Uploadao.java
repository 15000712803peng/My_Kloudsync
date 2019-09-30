package com.kloudsync.techexcel.info;

public class Uploadao {
    private int ServiceProviderId; //1: AWS S3 2: Ali OSS
    private String RegionName;
    private String BucketName;
    private String AccessKeyId;
    private String AccessKeySecret;
    private String SecurityToken;
    private String Data;

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }

    public int getServiceProviderId() {
        return ServiceProviderId;
    }

    public void setServiceProviderId(int serviceProviderId) {
        ServiceProviderId = serviceProviderId;
    }

    public String getRegionName() {
        return RegionName;
    }

    public void setRegionName(String regionName) {
        RegionName = regionName;
    }

    public String getBucketName() {
        return BucketName;
    }

    public void setBucketName(String bucketName) {
        BucketName = bucketName;
    }

    public String getAccessKeyId() {
        return AccessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        AccessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return AccessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        AccessKeySecret = accessKeySecret;
    }

    public String getSecurityToken() {
        return SecurityToken;
    }

    public void setSecurityToken(String securityToken) {
        SecurityToken = securityToken;
    }
}
