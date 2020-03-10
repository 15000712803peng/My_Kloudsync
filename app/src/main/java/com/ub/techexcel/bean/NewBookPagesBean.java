package com.ub.techexcel.bean;

import java.io.Serializable;
import java.util.List;

public class NewBookPagesBean implements Serializable {


	/**
	 * PeertimeToken : sample string 1
	 * BookPages : [{"PageAddress":"sample string 1","PenId":"sample string 2"},{"PageAddress":"sample string 1","PenId":"sample string 2"}]
	 */

	private String PeertimeToken;
	private List<BookPagesBean> BookPages;

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

	public static class BookPagesBean {
		/**
		 * PageAddress : sample string 1
		 * PenId : sample string 2
		 */

		private String PageAddress;
		private String PenId;

		public String getPageAddress() {
			return PageAddress;
		}

		public void setPageAddress(String PageAddress) {
			this.PageAddress = PageAddress;
		}

		public String getPenId() {
			return PenId;
		}

		public void setPenId(String PenId) {
			this.PenId = PenId;
		}
	}
}
