package com.kloudsync.techexcel.info;

import android.os.Parcel;

import java.io.Serializable;

/**
 * Created by pingfan on 2017/8/24.
 */

public class Spectator implements Serializable {

    private int IdentityType;
    private String AvatarUrl;
    private String Name;
    private String Identity;

    public Spectator() {
        super();
    }

    public Spectator(int identityType, String avatarUrl, String name, String identity) {
        super();
        IdentityType = identityType;
        AvatarUrl = avatarUrl;
        Name = name;
        Identity = identity;
    }

    public int getIdentityType() {
        return IdentityType;
    }

    public void setIdentityType(int identityType) {
        IdentityType = identityType;
    }

    public String getAvatarUrl() {
        return AvatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        AvatarUrl = avatarUrl;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getIdentity() {
        return Identity;
    }

    public void setIdentity(String identity) {
        Identity = identity;
    }

    protected Spectator(Parcel in) {
    }

}
