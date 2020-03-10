package com.ub.techexcel.bean;

import java.io.Serializable;
import java.util.List;

public class SyncNoteBean implements Serializable {

	/**
	 * PeertimeToken : sample string 1
	 * BookPages : [{"PageAddress":"sample string 1","TargetFolderKey":"sample string 2","FileId":3,"NoteId":4},{"PageAddress":"sample string 1","TargetFolderKey":"sample string 2","FileId":3,"NoteId":4}]
	 * DrawingData : [{"address":"sample string 1","userID":2,"event_type":3,"force":4,"point_x":5,"point_y":6,"timestamp":7.1,"penID":"sample string 8","strokeID":"sample string 9"},{"address":"sample string 1","userID":2,"event_type":3,"force":4,"point_x":5,"point_y":6,"timestamp":7.1,"penID":"sample string 8","strokeID":"sample string 9"}]
	 */


	private String PeertimeToken;
	private List<BookPagesBean> BookPages;
	private List<DrawingDataBean> DrawingData;

	public String getPeertimeToken() {
		return PeertimeToken;
	}

	public void setPeertimeToken(String PeertimeToken) {
		this.PeertimeToken = PeertimeToken;
	}

	public List<BookPagesBean> getBookPages() {
		return BookPages;
	}

	public void setBookPages(List<BookPagesBean> BookPages) {
		this.BookPages = BookPages;
	}

	public List<DrawingDataBean> getDrawingData() {
		return DrawingData;
	}

	public void setDrawingData(List<DrawingDataBean> DrawingData) {
		this.DrawingData = DrawingData;
	}

	public static class BookPagesBean {
		/**
		 * PageAddress : sample string 1
		 * TargetFolderKey : sample string 2
		 * FileId : 3
		 * NoteId : 4
		 */

		private String PageAddress;
		private String TargetFolderKey;
		private int FileId;
		private int NoteId;

		public String getPageAddress() {
			return PageAddress;
		}

		public void setPageAddress(String PageAddress) {
			this.PageAddress = PageAddress;
		}

		public String getTargetFolderKey() {
			return TargetFolderKey;
		}

		public void setTargetFolderKey(String TargetFolderKey) {
			this.TargetFolderKey = TargetFolderKey;
		}

		public int getFileId() {
			return FileId;
		}

		public void setFileId(int FileId) {
			this.FileId = FileId;
		}

		public int getNoteId() {
			return NoteId;
		}

		public void setNoteId(int NoteId) {
			this.NoteId = NoteId;
		}
	}

	public static class DrawingDataBean {
		/**
		 * address : sample string 1
		 * userID : 2
		 * event_type : 3
		 * force : 4
		 * point_x : 5
		 * point_y : 6
		 * timestamp : 7.1
		 * penID : sample string 8
		 * strokeID : sample string 9
		 */

		private String address;
		private String userID;
		private int event_type;
		private int force;
		private String point_x;
		private String point_y;
		private String timestamp;
		private String penID;
		private String strokeID;

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getUserID() {
			return userID;
		}

		public void setUserID(String userID) {
			this.userID = userID;
		}

		public int getEvent_type() {
			return event_type;
		}

		public void setEvent_type(int event_type) {
			this.event_type = event_type;
		}

		public int getForce() {
			return force;
		}

		public void setForce(int force) {
			this.force = force;
		}

		public String getPoint_x() {
			return point_x;
		}

		public void setPoint_x(String point_x) {
			this.point_x = point_x;
		}

		public String getPoint_y() {
			return point_y;
		}

		public void setPoint_y(String point_y) {
			this.point_y = point_y;
		}

		public String getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(String timestamp) {
			this.timestamp = timestamp;
		}

		public String getPenID() {
			return penID;
		}

		public void setPenID(String penID) {
			this.penID = penID;
		}

		public String getStrokeID() {
			return strokeID;
		}

		public void setStrokeID(String strokeID) {
			this.strokeID = strokeID;
		}
	}
}
