package com.ub.techexcel.tools;

public class SiderIndex {
	public static String[] b = {"*", "A", "B", "C", "D", "E", "F", "G", "H", "I",
		"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
		"W", "X", "Y", "Z", "#" };
	
	
	
	public static int stringtoint(String s){
		int number=-1;
		for(int i=0;i<b.length;i++){
			if(b[i].toString().equals(s.toString())){		
				number= i;	
				break;  
			}	
		}
		return number;	
	}
}
