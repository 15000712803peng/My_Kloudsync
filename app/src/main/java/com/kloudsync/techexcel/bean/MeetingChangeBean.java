package com.kloudsync.techexcel.bean;

public class MeetingChangeBean {


	/**
	 * changeNumber : 8
	 * retCode : 0
	 * action : MEETING_CHANGE
	 * retData : {"type":1,"value":"","promoter":"225017019"}
	 */

	private int changeNumber;
	private String retCode;
	private String action;
	private RetDataBean retData;

	public int getChangeNumber() {
		return changeNumber;
	}

	public void setChangeNumber(int changeNumber) {
		this.changeNumber = changeNumber;
	}

	public String getRetCode() {
		return retCode;
	}

	public void setRetCode(String retCode) {
		this.retCode = retCode;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public RetDataBean getRetData() {
		return retData;
	}

	public void setRetData(RetDataBean retData) {
		this.retData = retData;
	}

	public static class RetDataBean {
		/**
		 * type : 1
		 * value :
		 * promoter : 225017019
		 */

		private int type;
		private String value;
		private String promoter;

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getPromoter() {
			return promoter;
		}

		public void setPromoter(String promoter) {
			this.promoter = promoter;
		}

		@Override
		public String toString() {
			return "RetDataBean{" +
					"type=" + type +
					", value='" + value + '\'' +
					", promoter='" + promoter + '\'' +
					'}';
		}
	}

	@Override
	public String toString() {
		return "MeetingChangeBean{" +
				"changeNumber=" + changeNumber +
				", retCode=" + retCode +
				", action='" + action + '\'' +
				", retData=" + retData +
				'}';
	}
}
