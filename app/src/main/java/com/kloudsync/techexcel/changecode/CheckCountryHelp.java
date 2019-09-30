package com.kloudsync.techexcel.changecode;

public class CheckCountryHelp {
	
	
	public static int CheckCountryCode(int code, String name){
		if(1 == code){
			if(name.equals("Canada")){
				code = 12;
			}else if(name.equals("United States")){
				code = 11;
			}
		}else if(7 == code){
			if(name.equals("Russia")){
				code = 71;
			}else if(name.equals("Kazakhstan")){
				code = 72;
			}
		}
		
		return code;		
	}

}
