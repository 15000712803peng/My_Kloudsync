package com.ub.techexcel.bean;

public class Person {
	private String flag;
	private int id;
	private String name;
	
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Person(String flag, int id, String name) {
		this.flag = flag;
		this.id = id;
		this.name = name;
	}
	

}
