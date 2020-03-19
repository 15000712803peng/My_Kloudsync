package com.ub.techexcel.bean;

import java.util.List;

public class SendWebScoketNoteBean {

	/**
	 * address : 1536.671.59.6
	 * lines : [{"id":"5b83d4fe-0502-f2b3-7f05dc5bddeb7eae","points":[[499,518,212,0],[499,511,244,0],[499,509,386,0],[501,511,422,0],[499,522,434,0],[479,572,446,0],[461,634,462,0],[456,672,474,0],[454,698,477,0],[456,707,489,0],[455,707,440,0],[455,702,345,0]]}]
	 * width : 5600
	 * height : 7920
	 * paper : A4
	 */
	private String address;
	private int width;
	private int height;
	private String paper;
	private List<LinesBean> lines;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getPaper() {
		return paper;
	}

	public void setPaper(String paper) {
		this.paper = paper;
	}

	public List<LinesBean> getLines() {
		return lines;
	}

	public void setLines(List<LinesBean> lines) {
		this.lines = lines;
	}

	public static class LinesBean {
		/**
		 * id : 5b83d4fe-0502-f2b3-7f05dc5bddeb7eae
		 * points : [[499,518,212,0],[499,511,244,0],[499,509,386,0],[501,511,422,0],[499,522,434,0],[479,572,446,0],[461,634,462,0],[456,672,474,0],[454,698,477,0],[456,707,489,0],[455,707,440,0],[455,702,345,0]]
		 */
		private String id;
		private List<List<String>> points;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public List<List<String>> getPoints() {
			return points;
		}

		public void setPoints(List<List<String>> points) {
			this.points = points;
		}
	}
}
