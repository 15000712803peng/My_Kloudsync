package com.ub.techexcel.bean;

import java.util.List;

public class NoteInfoBean {


	/**
	 * Success : true
	 * Error : {"ErrorCode":1,"ErrorMessage":"sample string 1"}
	 * Data : [{"Address":"sample string 1","NoteId":2,"FileId":3,"TargetFolder":"sample string 4","Success":true,"ErrMsg":"sample string 6"},{"Address":"sample string 1","NoteId":2,"FileId":3,"TargetFolder":"sample string 4","Success":true,"ErrMsg":"sample string 6"}]
	 */

	private boolean Success;
	private ErrorBean Error;
	private List<DataBean> Data;

	public boolean isSuccess() {
		return Success;
	}

	public void setSuccess(boolean Success) {
		this.Success = Success;
	}

	public ErrorBean getError() {
		return Error;
	}

	public void setError(ErrorBean Error) {
		this.Error = Error;
	}

	public List<DataBean> getData() {
		return Data;
	}

	public void setData(List<DataBean> Data) {
		this.Data = Data;
	}

	public static class ErrorBean {
		/**
		 * ErrorCode : 1
		 * ErrorMessage : sample string 1
		 */

		private int ErrorCode;
		private String ErrorMessage;

		public int getErrorCode() {
			return ErrorCode;
		}

		public void setErrorCode(int ErrorCode) {
			this.ErrorCode = ErrorCode;
		}

		public String getErrorMessage() {
			return ErrorMessage;
		}

		public void setErrorMessage(String ErrorMessage) {
			this.ErrorMessage = ErrorMessage;
		}
	}

	public static class DataBean {
		/**
		 * Address : sample string 1
		 * NoteId : 2
		 * FileId : 3
		 * TargetFolder : sample string 4
		 * Success : true
		 * ErrMsg : sample string 6
		 */

		private String Address;
		private int NoteId;
		private int FileId;
		private String TargetFolder;
		private boolean Success;
		private String ErrMsg;

		public String getAddress() {
			return Address;
		}

		public void setAddress(String Address) {
			this.Address = Address;
		}

		public int getNoteId() {
			return NoteId;
		}

		public void setNoteId(int NoteId) {
			this.NoteId = NoteId;
		}

		public int getFileId() {
			return FileId;
		}

		public void setFileId(int FileId) {
			this.FileId = FileId;
		}

		public String getTargetFolder() {
			return TargetFolder;
		}

		public void setTargetFolder(String TargetFolder) {
			this.TargetFolder = TargetFolder;
		}

		public boolean isSuccess() {
			return Success;
		}

		public void setSuccess(boolean Success) {
			this.Success = Success;
		}

		public String getErrMsg() {
			return ErrMsg;
		}

		public void setErrMsg(String ErrMsg) {
			this.ErrMsg = ErrMsg;
		}
	}
}
