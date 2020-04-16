package com.kloudsync.techexcel.bean;

import java.util.List;

/**
 * Created by tonyan on 2020/4/14.
 */

public class CourseItemData {

    private int RetCode;
    private String ErrorMessage;
    private String DetailMessage;
    private List<LessionInCourse> RetData;

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

    public List<LessionInCourse> getRetData() {
        return RetData;
    }

    public void setRetData(List<LessionInCourse> retData) {
        RetData = retData;
    }
}
