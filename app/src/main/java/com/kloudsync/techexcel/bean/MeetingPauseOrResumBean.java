package com.kloudsync.techexcel.bean;

public class MeetingPauseOrResumBean {


	/**
	 * code : 0
	 * msg : success
	 * data : {}
	 */

	private int code;
	private String msg;
	private DataBean data;
	private boolean isPause;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public boolean isPause() {
		return isPause;
	}

	public void setPause(boolean pause) {
		isPause = pause;
	}

	public DataBean getData() {
		return data;
	}

	public void setData(DataBean data) {
		this.data = data;
	}

	public static class DataBean {
	}
}
