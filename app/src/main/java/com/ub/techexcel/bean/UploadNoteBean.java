package com.ub.techexcel.bean;

public class UploadNoteBean {


	/**
	 * Success : true
	 * Error : {"ErrorCode":1,"ErrorMessage":"sample string 1"}
	 * Data : {"Token":"1:sample string 1,2:sample string 1"}
	 */

	private boolean Success;
	private ErrorBean Error;
	private DataBean Data;

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

	public DataBean getData() {
		return Data;
	}

	public void setData(DataBean Data) {
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
		 * Token : 1:sample string 1,2:sample string 1
		 */

		private String Token;

		public String getToken() {
			return Token;
		}

		public void setToken(String Token) {
			this.Token = Token;
		}
	}
}
