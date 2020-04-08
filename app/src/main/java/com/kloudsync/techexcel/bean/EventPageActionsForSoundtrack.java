package com.kloudsync.techexcel.bean;

/**
 * Created by tonyan on 2019/11/28.
 */

public class EventPageActionsForSoundtrack {
	private int pageNumber;
	private String data;

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "EventPageActionsForSoundtrack{" +
				"pageNumber=" + pageNumber +
				", data='" + data + '\'' +
				'}';
	}
}
