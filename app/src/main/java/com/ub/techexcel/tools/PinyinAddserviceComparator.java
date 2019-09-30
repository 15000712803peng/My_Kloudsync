package com.ub.techexcel.tools;

import java.util.Comparator;

import com.kloudsync.techexcel.info.Customer;

/**
 * 
 * @author xiaanming
 *
 */

public class PinyinAddserviceComparator implements Comparator<Customer> {

	public int compare(Customer o1, Customer o2) {
		if (o1.getSortLetters().equals("@")
				|| o2.getSortLetters().equals("#")) {
			return -1;
		} else if (o1.getSortLetters().equals("#")
				|| o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}

}
