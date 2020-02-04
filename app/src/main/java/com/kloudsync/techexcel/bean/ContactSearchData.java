package com.kloudsync.techexcel.bean;

import java.util.List;

/**
 * Created by tonyan on 2020/2/1.
 */

public class ContactSearchData {

    private List<FriendContact> myContactList;
    private List<FriendContact> companyContactVOList;

    public List<FriendContact> getMyContactList() {
        return myContactList;
    }

    public void setMyContactList(List<FriendContact> myContactList) {
        this.myContactList = myContactList;
    }

    public List<FriendContact> getCompanyContactVOList() {
        return companyContactVOList;
    }

    public void setCompanyContactVOList(List<FriendContact> companyContactVOList) {
        this.companyContactVOList = companyContactVOList;
    }
}
