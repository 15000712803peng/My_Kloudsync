package com.kloudsync.techexcel.bean;

import java.util.List;

/**
 * Created by tonyan on 2019/10/14.
 */

public class OutlineChapterItem {

    String IdeaID;
    String ChapterTitle;
    int ChapterType;
    int TotalPageCount;
    int Order;
    long OutLineID;
    int Expanded;
    long ParentOutLineID;
    String KeyWords;
    String DownLoadURL;
    List<OutlineSectionItem> SectionItems;

    public String getIdeaID() {
        return IdeaID;
    }

    public void setIdeaID(String ideaID) {
        IdeaID = ideaID;
    }

    public String getChapterTitle() {
        return ChapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        ChapterTitle = chapterTitle;
    }

    public int getChapterType() {
        return ChapterType;
    }

    public void setChapterType(int chapterType) {
        ChapterType = chapterType;
    }

    public int getTotalPageCount() {
        return TotalPageCount;
    }

    public void setTotalPageCount(int totalPageCount) {
        TotalPageCount = totalPageCount;
    }

    public int getOrder() {
        return Order;
    }

    public void setOrder(int order) {
        Order = order;
    }

    public long getOutLineID() {
        return OutLineID;
    }

    public void setOutLineID(long outLineID) {
        OutLineID = outLineID;
    }

    public int getExpanded() {
        return Expanded;
    }

    public void setExpanded(int expanded) {
        Expanded = expanded;
    }

    public long getParentOutLineID() {
        return ParentOutLineID;
    }

    public void setParentOutLineID(long parentOutLineID) {
        ParentOutLineID = parentOutLineID;
    }

    public String getKeyWords() {
        return KeyWords;
    }

    public void setKeyWords(String keyWords) {
        KeyWords = keyWords;
    }

    public String getDownLoadURL() {
        return DownLoadURL;
    }

    public void setDownLoadURL(String downLoadURL) {
        DownLoadURL = downLoadURL;
    }

    public List<OutlineSectionItem> getSectionItems() {
        return SectionItems;
    }

    public void setSectionItems(List<OutlineSectionItem> sectionItems) {
        SectionItems = sectionItems;
    }
}
