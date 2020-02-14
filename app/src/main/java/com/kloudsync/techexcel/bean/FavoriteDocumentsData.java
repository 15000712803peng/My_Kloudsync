package com.kloudsync.techexcel.bean;

import java.util.List;

/**
 * Created by tonyan on 2020/2/14.
 */

public class FavoriteDocumentsData {
    private int Count;
    private List<MeetingDocument> List;

    public int getCount() {
        return Count;
    }

    public void setCount(int count) {
        Count = count;
    }

    public java.util.List<MeetingDocument> getList() {
        return List;
    }

    public void setList(java.util.List<MeetingDocument> list) {
        List = list;
    }
}
