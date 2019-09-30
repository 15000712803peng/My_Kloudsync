package com.kloudsync.techexcel.tool;

import com.kloudsync.techexcel.bean.AccountSettingContactBean;

import java.util.Comparator;

public class AccountSettingPinyinComparator implements Comparator<AccountSettingContactBean> {

    @Override
    public int compare(AccountSettingContactBean lhs, AccountSettingContactBean rhs) {

        String str1 = (String) lhs.getSortLetters();
        String str2 = (String) rhs.getSortLetters();

        if (!str1.matches("[A-Z]")) {
            return -1;
        } else if (!str2.matches("[A-Z]")) {
            return 1;
        }
        if (str1.equals(str2)) {
            return lhs.getUserName().compareTo(rhs.getUserName());
        } else {
            return str1.compareTo(str2);

        }
    }


}
