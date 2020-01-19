package com.kloudsync.techexcel.bean;

public class UserPreferenceData {
        private int RetCode;
        private String ErrorMessage;
        private String DetailMessage;
        private Object RetData;

        public int getRetCode() {
            return RetCode;
        }

        public void setRetCode(int retCode) {
            RetCode = retCode;
        }

        public String getErrorMessage() {
            return ErrorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            ErrorMessage = errorMessage;
        }

        public String getDetailMessage() {
            return DetailMessage;
        }

        public void setDetailMessage(String detailMessage) {
            DetailMessage = detailMessage;
        }

        public Object getRetData() {
            return RetData;
        }

        public void setRetData(Object retData) {
            RetData = retData;
        }
    }
