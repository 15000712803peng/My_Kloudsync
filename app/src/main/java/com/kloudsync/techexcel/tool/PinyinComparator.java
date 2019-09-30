package com.kloudsync.techexcel.tool;

import com.kloudsync.techexcel.info.Customer;

import java.util.Comparator;

public class PinyinComparator implements Comparator<Customer> {

    @Override
    public int compare(Customer lhs, Customer rhs) {

        String str1 = (String) lhs.getSortLetters();
        String str2 = (String) rhs.getSortLetters();

        if (!str1.matches("[A-Z]")) {
            return -1;
        } else if (!str2.matches("[A-Z]")) {
            return 1;
        }
        if (str1.equals(str2)) {
            return lhs.getName().compareTo(rhs.getName());
        } else {
            return str1.compareTo(str2);
        }
    }


}
