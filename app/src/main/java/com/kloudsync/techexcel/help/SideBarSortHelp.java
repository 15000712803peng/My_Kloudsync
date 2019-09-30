package com.kloudsync.techexcel.help;

import com.kloudsync.techexcel.info.CountryCodeInfo;
import com.kloudsync.techexcel.info.Customer;

import java.util.List;


public class SideBarSortHelp {

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public static int getPositionForSection(List<Customer> list,char section) {
		for (int i = 0; i < list.size(); i++) {
			String sortStr = list.get(i).getSortLetters();
			if(null == sortStr){
				continue;
			}
			char firstChar = sortStr.charAt(0);
			if (firstChar == section) {
				return i;
			}
		}

		return -1;
	}
	

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public static int getPositionForSectionCC(List<CountryCodeInfo> list,char section) {
		for (int i = 0; i < list.size(); i++) {
			String sortStr = list.get(i).getSortLetters();
			char firstChar = sortStr.charAt(0);
			if (firstChar == section) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * 提取英文的首字母，非英文字母用#代替。
	 * 
	 * @param str
	 * @return
	 */
	public static String getAlpha(String str) {
		String sortStr = str;
		// 正则表达式，判断首字母是否是英文字母
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}
}
