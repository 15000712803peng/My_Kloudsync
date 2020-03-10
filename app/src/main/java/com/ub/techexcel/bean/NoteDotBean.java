package com.ub.techexcel.bean;

import com.tqltech.tqlpencomm.Dot;

import java.io.Serializable;

public class NoteDotBean implements Serializable {

	private String dotId;
	private Dot dot;

	public String getDotId() {
		return dotId;
	}

	public void setDotId(String dotId) {
		this.dotId = dotId;
	}

	public Dot getDot() {
		return dot;
	}

	public void setDot(Dot dot) {
		this.dot = dot;
	}

}
