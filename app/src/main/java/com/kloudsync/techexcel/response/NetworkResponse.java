package com.kloudsync.techexcel.response;

public class NetworkResponse<T> {
    protected int RetCode = -1;
    protected String ErrorMessage;
    protected String DetailMessage;
    protected T RetData;
    public int getRetCode() {
        return RetCode;
    }

    public static final int NETWORK_ERROR_CODE = 1001;

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

    public T getRetData() {
        return RetData;
    }

    public void setRetData(T retData) {
        RetData = retData;
    }

    @Override
    public String toString() {
        return "NetworkResponse{" +
                "RetCode=" + RetCode +
                ", ErrorMessage='" + ErrorMessage + '\'' +
                ", DetailMessage='" + DetailMessage + '\'' +
                ", RetData=" + RetData +
                '}';
    }
}
