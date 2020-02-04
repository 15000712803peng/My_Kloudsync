package com.kloudsync.techexcel.bean;

import java.util.List;

/**
 * Created by tonyan on 2020/1/21.
 */

public class SameLetterFriends {

    private String firstLetter = "";
    private List<FriendContact> contactVOList;

    public String getFirstLetter() {
        return firstLetter;
    }

    public void setFirstLetter(String firstLetter) {
        this.firstLetter = firstLetter;
    }

    public List<FriendContact> getContactVOList() {
        return contactVOList;
    }

    public void setContactVOList(List<FriendContact> contactVOList) {
        this.contactVOList = contactVOList;
    }

    public Object getItem(int position) {
        // Category排在第一位
        if (position == 0) {
            return firstLetter;
        } else {
            return contactVOList.get(position - 1);
        }
    }


    /**
     * 当前类别Item总数。Category也需要占用一个Item
     * @return
     */
    public int getItemCount() {
        if(contactVOList == null || contactVOList.size() <= 0){
            return 1;
        }
        return contactVOList.size() + 1;
    }

}
