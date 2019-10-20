package com.kloudsync.techexcel.bean;

import java.util.List;

/**
 * Created by tonyan on 2019/10/14.
 */

public class SyncbookInfo {

    String BookTitle;
    int Type;
    String BookIdentifier;
    List<OutlineChapterItem> ChapterItems;

    public String getBookTitle() {
        return BookTitle;
    }

    public void setBookTitle(String bookTitle) {
        BookTitle = bookTitle;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public String getBookIdentifier() {
        return BookIdentifier;
    }

    public void setBookIdentifier(String bookIdentifier) {
        BookIdentifier = bookIdentifier;
    }

    public List<OutlineChapterItem> getChapterItems() {
        return ChapterItems;
    }

    public void setChapterItems(List<OutlineChapterItem> chapterItems) {
        ChapterItems = chapterItems;
    }

    @Override
    public String toString() {
        return "SyncbookInfo{" +
                "BookTitle='" + BookTitle + '\'' +
                ", Type=" + Type +
                ", BookIdentifier='" + BookIdentifier + '\'' +
                ", ChapterItems=" + ChapterItems +
                '}';
    }
}
