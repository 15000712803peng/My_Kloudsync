package com.kloudsync.techexcel.info;

public class CountryCodeInfo {
	private int code;
	private String name;
    private String sortLetters;
    private String showname;
    private Boolean ncshow;
    
    
	public CountryCodeInfo() {
		super();
	}
	public CountryCodeInfo(int code, String name, String sortLetters) {
		super();
		this.code = code;
		this.name = name;
		this.sortLetters = sortLetters;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
	public String getShowname() {
		return showname;
	}
	public void setShowname(String showname) {
		this.showname = showname;
	}
	public Boolean getNcshow() {
		return ncshow;
	}
	public void setNcshow(Boolean ncshow) {
		this.ncshow = ncshow;
	}
    
    
	

}
